package com.adlab.balda.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.utils.PresenterManager;
import com.adlab.balda.widgets.BorderDecoration;
import com.adlab.balda.adapters.FieldRecyclerAdapter;
import com.adlab.balda.R;
import com.adlab.balda.widgets.BlockTouchEventLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.List;

public class GameActivity extends AppCompatActivity implements GameContract.View, FieldRecyclerAdapter.OnItemClickListener{

    private final static int ITEM_SIZE = 64;
    private final static int TEXT_SIZE = 48;
    private final static int NUMBER_SIZE = 14;
    private final static int DIVIDER_SIZE = 2;

    private float itemSizePx;
    private float dividerSizePx;

    private GameContract.Presenter mPresenter;

    private InputMethodManager imm;

    private RecyclerView recyclerView;
    private TextView textViewScore;
    private EditText editTextFieldItem;
    private View viewContent;
    private TextView textViewScoreAnim;
    private CardView cardViewScoreAnim;
    private HorizontalScrollView horizontalScrollView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView textViewUsedWords;

    private FieldRecyclerAdapter adapter;
    private GridLayoutManager layoutManager;

    private ActionMode actionMode = null;

    public static Intent createIntent(Context context) {
        return new Intent(context, GameActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        itemSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ITEM_SIZE, getResources().getDisplayMetrics());
        dividerSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIVIDER_SIZE, getResources().getDisplayMetrics());

        viewContent = findViewById(R.id.rl_content_game);
        recyclerView = findViewById(R.id.activity_game_recycler_view);
        textViewScore = findViewById(R.id.tv_score);
        editTextFieldItem = findViewById(R.id.et_input_field_item);
        textViewScoreAnim = findViewById(R.id.tv_score_anim);
        cardViewScoreAnim = findViewById(R.id.cv_score_anim);
        BlockTouchEventLayout touchEventLayout = findViewById(R.id.touchEventLayout);
        horizontalScrollView = findViewById(R.id.scrollH);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        textViewUsedWords = navigationView.getHeaderView(0).findViewById(R.id.tv_used_words);

        viewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideKeyboard();
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        KeyboardVisibilityEvent.setEventListener(
                this, new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            mPresenter.onKeyboardOpen();
                        } else {
                            mPresenter.onKeyboardHidden();
                        }
                    }
                });

        touchEventLayout.setTouchListener(new BlockTouchEventLayout.TouchListener() {
            private float mPosX = 0;

            @Override
            public void onTouch(MotionEvent ev) {
                MotionEvent copy = MotionEvent.obtain(ev);
                copy.offsetLocation(horizontalScrollView.getScrollX(), 0);
                recyclerView.dispatchTouchEvent(copy);
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX = ev.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        horizontalScrollView.scrollBy((int)(mPosX - ev.getX()), 0);
                        mPosX = ev.getX();
                        break;
                }
            }
        });

        editTextFieldItem.addTextChangedListener(new SingleLetterTextWatcher());

        PresenterManager.provideGamePresenter(this);

        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.resetView();
        if (isFinishing()) {
            PresenterManager.resetGamePresenter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.used_words:
                drawerLayout.openDrawer(navigationView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {
        mPresenter.onCellClicked(position);
    }

    @Override
    public void onItemLongClick(View itemView, int position) {
        mPresenter.onCellLongClicked(position);
    }

    @Override
    public void onClearLetterClick() {
        editTextFieldItem.setText("");
        imm.restartInput(editTextFieldItem);
        mPresenter.clearEnteredLetter();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            mPresenter.finishGame();
        }
    }

    @Override
    public void showField(int rowCount, int colCount) {
        adapter = new FieldRecyclerAdapter(mPresenter, rowCount * colCount, (int) itemSizePx, TEXT_SIZE, NUMBER_SIZE);
        adapter.setOnItemClickListener(this);
        adapter.setHasStableIds(true);
        layoutManager = new GridLayoutManager(this, colCount);
        BorderDecoration decoration = new BorderDecoration(this, Color.GRAY, DIVIDER_SIZE, colCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public void showGameResult(int score) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.congratulations)
                .setMessage(getString(R.string.filled_whole_field, score))
                .setPositiveButton(R.string.back_to_menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.start_new_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(GameSettingsActivity.createIntent(GameActivity.this));
                        finish();
                    }
                })
                .create().show();
    }

    @Override
    public void showGameExit() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_game_message)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.back_to_the_game, null)
                .create().show();
    }

    @Override
    public void setPresenter(@NonNull GameContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateCell(int cellNumber) {
        adapter.notifyItemChanged(cellNumber);
    }

    @Override
    public void showKeyboard() {
        editTextFieldItem.requestFocus();
        imm.showSoftInput(editTextFieldItem, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void scrollFieldToCell(final int cellNumber) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.smoothScrollBy(calculateHorizontalOffset(cellNumber), 0);
                recyclerView.smoothScrollBy(0, calculateVerticalOffset(cellNumber));
            }
        }, 100);
    }

    private int calculateHorizontalOffset(int cellNumber) {
        int cellCol = cellNumber % layoutManager.getSpanCount();

        int extraOffset = (int) (itemSizePx * 0.45);
        int cellEnd = (int) (((cellCol + 1) * itemSizePx) + ((cellCol + 2) * dividerSizePx));
        int cellStart = (int) ((cellCol * itemSizePx) + (cellCol * dividerSizePx));
        int screenEnd = horizontalScrollView.getScrollX() + horizontalScrollView.getWidth();
        int screenStart = horizontalScrollView.getScrollX();

        if (cellEnd + extraOffset > screenEnd) {
            return (cellEnd - screenEnd) + extraOffset;
        } else if (cellStart - extraOffset < screenStart) {
            return (cellStart - screenStart) - extraOffset;
        } else {
            return 0;
        }
    }

    private int calculateVerticalOffset(int cellNumber) {
        int cellRow = cellNumber / layoutManager.getSpanCount();

        int extraOffset = (int) (itemSizePx * 0.45);
        int cellEnd = (int) (((cellRow + 1) * itemSizePx) + ((cellRow + 2) * dividerSizePx));
        int cellStart = (int) ((cellRow * itemSizePx) + (cellRow * dividerSizePx));
        int screenEnd = recyclerView.computeVerticalScrollOffset() + recyclerView.getHeight();
        int screenStart = recyclerView.computeVerticalScrollOffset();

        if (cellEnd + extraOffset > screenEnd) {
            return (cellEnd - screenEnd) + extraOffset;
        } else if (cellStart - extraOffset < screenStart) {
            return (cellStart - screenStart) - extraOffset;
        } else {
            return 0;
        }
    }

    @Override
    public void hideKeyboard() {
        editTextFieldItem.requestFocus();
        imm.hideSoftInputFromWindow(editTextFieldItem.getWindowToken(), 0);
    }

    @Override
    public void showScoreAnimation(int deltaScore) {
        textViewScoreAnim.setText(getString(R.string.plus_points, deltaScore));
        cardViewScoreAnim.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.score);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }
            @Override
            public void onAnimationEnd(Animation animation) {
                cardViewScoreAnim.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {  }
        });
        cardViewScoreAnim.startAnimation(animation);
    }

    @Override
    public void updateUsedWords(List<String> listOfWords) {
        StringBuilder r = new StringBuilder();
        for (String s : listOfWords) {
            r.append(s).append('\n');
        }
        textViewUsedWords.setText(r.toString().toUpperCase());
    }

    @Override
    public void activateActionMode() {
        actionMode = startSupportActionMode(new ActionModeCallback());
    }

    @Override
    public void deactivateActionMode() {
        actionMode.finish();
    }

    @Override
    public void updateActivatedLetterSequence(String letterSequence) {
        actionMode.setTitle(letterSequence.toUpperCase());
    }

    @Override
    public void updateScore(int score) {
        textViewScore.setText(getString(R.string.score, score));
    }

    @Override
    public void showMessage(GameContract.MessageType message) {
        switch (message) {
            case INCORRECT_SYMBOL:
                showToast(R.string.invalid_symbol);
                break;
            case NEED_ENTER_LETTER:
                showSnackbar(R.string.enter_new_letter);
                break;
            case MUST_CONTAIN_NEW_LETTER:
                showSnackbar(R.string.must_contain_new_letter);
                break;
            case NO_SUCH_WORD:
                showSnackbar(R.string.no_such_word);
                break;
            case WORD_ALREADY_USED:
                showSnackbar(R.string.such_word_have_already_been_used);
                break;
        }
    }

    private void showSnackbar(@StringRes int resId, Object... values) {
        Snackbar.make(viewContent, getString(resId, values), Snackbar.LENGTH_LONG)
                .show();
    }

    private void showToast(@StringRes int resId) {
        Toast.makeText(GameActivity.this, resId, Toast.LENGTH_SHORT).show();
    }

    private class SingleLetterTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        @Override
        public void afterTextChanged(Editable editable) {
            int iLen = editable.length();
            if (iLen > 1){
                editable.delete(0, 1);
            }
            imm.restartInput(editTextFieldItem);

            if (iLen != 0){
                mPresenter.enterLetter(editable.charAt(0));
            } else {
                mPresenter.clearEnteredLetter();
            }
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_confirm) {
                mPresenter.confirmWord();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mPresenter.deactivateActionMode();
            actionMode = null;
        }
    }
}
