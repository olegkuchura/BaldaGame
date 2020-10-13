package com.adlab.balda.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.SCROLL_AXIS_VERTICAL
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.adlab.balda.R
import com.adlab.balda.adapters.BaseFieldAdapter
import com.adlab.balda.adapters.BaseFieldAdapter.OnFieldItemClickListener
import com.adlab.balda.adapters.FieldRecyclerAdapter
import com.adlab.balda.adapters.HexagonFieldRecyclerAdapter
import com.adlab.balda.adapters.ScoreAdapter
import com.adlab.balda.contracts.MultiplayerGameContract
import com.adlab.balda.databinding.ActivityMultiplayerGameBinding
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.enums.GameMessageType
import com.adlab.balda.utils.PresenterManager
import com.adlab.balda.utils.dpToPxFloat
import com.adlab.balda.widgets.BlockTouchEventLayout.TouchListener
import com.adlab.balda.widgets.BorderDecoration
import com.adlab.balda.widgets.BorderDecorationHexagon
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MultiplayerGameActivity: AppCompatActivity(),
        MultiplayerGameContract.View, OnFieldItemClickListener {
    companion object {
        private const val ITEM_SIZE = 64
        private const val TEXT_SIZE = 46
        private const val NUMBER_SIZE = 14
        private const val DIVIDER_SIZE = 2

        @JvmStatic
        fun createIntent(context: Context) = Intent(context, MultiplayerGameActivity::class.java)
    }

    private var itemSizePx = 0f
    private var dividerSizePx = 0f

    private lateinit var mPresenter: MultiplayerGameContract.Presenter

    private lateinit var binding: ActivityMultiplayerGameBinding

    private lateinit var imm: InputMethodManager

    private var textViewUsedWords: TextView? = null

    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: GridLayoutManager? = null

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_multiplayer_game)
        setSupportActionBar(binding.toolbar)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        itemSizePx = dpToPxFloat(ITEM_SIZE.toFloat())
        dividerSizePx = dpToPxFloat(DIVIDER_SIZE.toFloat())
        textViewUsedWords = binding.navView.getHeaderView(0).findViewById(R.id.tv_used_words)
        (binding.rvScore.layoutManager as FlexboxLayoutManager).alignItems = AlignItems.CENTER
        (binding.rvScore.layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.CENTER
        binding.clContentGame.setOnClickListener { hideKeyboard() }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) { hideKeyboard() }
        })
        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {
                mPresenter.onKeyboardOpen()
            } else {
                mPresenter.onKeyboardHidden()
            }
        }
        binding.touchEventLayout.setTouchListener(object : TouchListener {
            private var mPosX = 0f
            override fun onTouch(ev: MotionEvent) {
                val copy = MotionEvent.obtain(ev)
                copy.offsetLocation(binding.scrollH.scrollX.toFloat(), 0f)
                binding.activityGameRecyclerView.dispatchTouchEvent(copy)
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> mPosX = ev.x
                    MotionEvent.ACTION_MOVE -> {
                        binding.scrollH.scrollBy((mPosX - ev.x).toInt(), 0)
                        mPosX = ev.x
                    }
                }
            }
        })
        binding.etInputFieldItem.addTextChangedListener(SingleLetterTextWatcher())

        PresenterManager.provideMultiplayerGamePresenter(this)
        mPresenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.resetView()
        if (isFinishing) {
            PresenterManager.resetMultiplayerGamePresenter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_game, menu)
        menu?.findItem(R.id.menu_move_hint)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_used_words -> {
                mPresenter.onShowUsedWordsClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(itemView: View, position: Int) {
        mPresenter.onCellClicked(position)
    }

    override fun onItemLongClick(itemView: View, position: Int) {
        mPresenter.onCellLongClicked(position)
    }

    override fun onClearLetterClick() {
        binding.etInputFieldItem.setText("")
        imm.restartInput(binding.etInputFieldItem)
        mPresenter.clearEnteredLetter()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
            binding.drawerLayout.closeDrawer(binding.navView)
        } else {
            mPresenter.finishGame()
        }
    }

    override fun showField(rowCount: Int, colCount: Int, fieldType: FieldType, fieldSize: FieldSizeType) {
        adapter = makeAdapterByType(fieldType, fieldSize, colCount)
        (adapter as BaseFieldAdapter).setOnItemClickListener(this)
        layoutManager = GridLayoutManager(this, colCount)
        if (fieldType === FieldType.HEXAGON) {
            layoutManager?.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    var size = 1
                    if ((position - colCount) % (colCount * 2 - 1) == 0) {
                        size = 2
                    }
                    return size
                }
            }
        }
        val decoration: ItemDecoration = makeItemDecorationByType(fieldType, colCount)
        binding.activityGameRecyclerView.layoutManager = layoutManager
        binding.activityGameRecyclerView.adapter = adapter
        binding.activityGameRecyclerView.addItemDecoration(decoration)
    }

    override fun showUsedWords() {
        binding.drawerLayout.openDrawer(binding.navView)
    }

    override fun showGameResult(winnerNickname: String, winnerScore: Int, otherPlayers: List<Pair<String, Int>>) {
        binding.rvScore.visibility = View.GONE

        binding.tvWinner.text = getString(R.string.congratulations_nickname, winnerNickname, winnerScore)
        binding.tvOtherPlayers.text = StringBuilder().apply {
            otherPlayers.map { getString(R.string.score, it.first, it.second) }
                    .forEachIndexed { index, value ->
                        append(value)
                        if (index != otherPlayers.size -1)
                            append('\n')
                    }
        }
        binding.llCongratulations.visibility = View.VISIBLE
    }

    override fun showGameExit() {
        AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_game_message)
                .setPositiveButton(R.string.finish) { _, _ -> finish() }
                .setNegativeButton(R.string.back_to_the_game, null)
                .create().show()
    }

    override fun setPresenter(presenter: MultiplayerGameContract.Presenter) {
        mPresenter = presenter
    }

    override fun updateCell(cellNumber: Int) {
        adapter!!.notifyItemChanged(cellNumber)
    }

    override fun showKeyboard() {
        binding.etInputFieldItem.requestFocus()
        imm.showSoftInput(binding.etInputFieldItem, InputMethodManager.SHOW_FORCED)
    }

    override fun scrollFieldToCell(cellNumber: Int) {
        binding.activityGameRecyclerView.postDelayed({
            if (!binding.activityGameRecyclerView.hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
                binding.activityGameRecyclerView.
                    startNestedScroll(SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH)
            }
            binding.scrollH.smoothScrollBy(calculateHorizontalOffset(cellNumber), 0)
            binding.activityGameRecyclerView.smoothScrollBy(0, calculateVerticalOffset(cellNumber))
        }, 100)
    }

    private fun calculateHorizontalOffset(cellNumber: Int): Int {
        val cellCol = cellNumber % layoutManager!!.spanCount
        val extraOffset = (itemSizePx * 0.45).toInt()
        val cellEnd = ((cellCol + 1) * itemSizePx + (cellCol + 2) * dividerSizePx).toInt()
        val cellStart = (cellCol * itemSizePx + cellCol * dividerSizePx).toInt()
        val screenEnd = binding.scrollH.scrollX + binding.scrollH.width
        val screenStart = binding.scrollH.scrollX
        return when {
            cellEnd + extraOffset > screenEnd -> {
                cellEnd - screenEnd + extraOffset
            }
            cellStart - extraOffset < screenStart -> {
                cellStart - screenStart - extraOffset
            }
            else -> {
                0
            }
        }
    }

    private fun calculateVerticalOffset(cellNumber: Int): Int {
        val cellRow = cellNumber / layoutManager!!.spanCount
        val extraOffset = (itemSizePx * 0.45).toInt()
        val cellEnd = ((cellRow + 1) * itemSizePx + (cellRow + 2) * dividerSizePx).toInt()
        val cellStart = (cellRow * itemSizePx + cellRow * dividerSizePx).toInt()
        val screenEnd = binding.activityGameRecyclerView.computeVerticalScrollOffset() + binding.activityGameRecyclerView.height
        val screenStart = binding.activityGameRecyclerView.computeVerticalScrollOffset()
        return if (cellEnd + extraOffset > screenEnd) {
            cellEnd - screenEnd + extraOffset
        } else if (cellStart - extraOffset < screenStart) {
            cellStart - screenStart - extraOffset
        } else {
            0
        }
    }

    override fun hideKeyboard() {
        binding.etInputFieldItem.requestFocus()
        imm.hideSoftInputFromWindow(binding.etInputFieldItem.windowToken, 0)
    }

    override fun showScoreAnimation(deltaScore: Int) {
        binding.tvScoreAnim.text = getString(R.string.plus_points, deltaScore)
        binding.cvScoreAnim.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.score)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                binding.cvScoreAnim.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.cvScoreAnim.startAnimation(animation)
    }

    override fun updateUsedWords(listOfWords: List<String>) {
        val r = StringBuilder()
        for (s in listOfWords) {
            r.append(s).append('\n')
        }
        textViewUsedWords!!.text = r.toString().toUpperCase()
    }

    override fun activateActionMode() {
        actionMode = startSupportActionMode(ActionModeCallback())
    }

    override fun deactivateActionMode() {
        actionMode!!.finish()
    }

    override fun showScore(playersCount: Int) {
        binding.rvScore.adapter = ScoreAdapter(mPresenter, playersCount)
    }

    override fun updateActivatedLetterSequence(letterSequence: String) {
        actionMode!!.title = letterSequence.toUpperCase()
    }

    override fun updateScore() {
        // todo make better (DiffUtil for example)
        binding.rvScore.adapter?.notifyDataSetChanged()
    }

    override fun showMessage(message: GameMessageType) {
        when (message) {
            GameMessageType.INCORRECT_SYMBOL -> showToast(R.string.invalid_symbol)
            GameMessageType.NEED_ENTER_LETTER -> showSnackbar(R.string.enter_new_letter)
            GameMessageType.MUST_CONTAIN_NEW_LETTER -> showSnackbar(R.string.must_contain_new_letter)
            GameMessageType.NO_SUCH_WORD -> showSnackbar(R.string.no_such_word)
            GameMessageType.WORD_ALREADY_USED -> showSnackbar(R.string.such_word_have_already_been_used)
        }
    }

    private fun showSnackbar(@StringRes resId: Int, vararg values: Any) {
        Snackbar.make(binding.clContentGame, getString(resId, *values), Snackbar.LENGTH_LONG)
                .show()
    }

    private fun showToast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    private fun makeAdapterByType(fieldType: FieldType, fieldSize: FieldSizeType, colCount: Int): RecyclerView.Adapter<*> =
            when (fieldType) {
                FieldType.SQUARE -> FieldRecyclerAdapter(mPresenter, fieldSize, itemSizePx.toInt(), TEXT_SIZE, NUMBER_SIZE)
                FieldType.HEXAGON -> HexagonFieldRecyclerAdapter(mPresenter, colCount, fieldSize, (itemSizePx + itemSizePx / 4).toInt(), TEXT_SIZE, NUMBER_SIZE)
            }

    private fun makeItemDecorationByType(fieldType: FieldType, colCount: Int): ItemDecoration =
            when (fieldType) {
                FieldType.SQUARE -> BorderDecoration(this, DIVIDER_SIZE.toFloat(), colCount)
                FieldType.HEXAGON -> BorderDecorationHexagon(this, DIVIDER_SIZE.toFloat(), colCount)
            }

    private inner class SingleLetterTextWatcher : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val iLen = editable.length
            if (iLen > 1) {
                editable.delete(0, 1)
            }
            imm.restartInput(binding.etInputFieldItem)
            if (iLen != 0) {
                mPresenter.enterLetter(editable[0])
            } else {
                mPresenter.clearEnteredLetter()
            }
        }
    }


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            if (item.itemId == R.id.action_confirm) {
                mPresenter.confirmWord()
                return true
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mPresenter.deactivateActionMode()
            actionMode = null
        }
    }

}