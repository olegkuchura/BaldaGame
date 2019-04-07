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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.utils.PresenterManager;
import com.adlab.balda.widgets.BorderDecoration;
import com.adlab.balda.adapters.FieldRecyclerAdapter;
import com.adlab.balda.R;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class GameActivity extends AppCompatActivity implements GameContract.View, FieldRecyclerAdapter.OnItemClickListener{
    private static final String SHARED_SETTINGS = "settings";
    private static final String GAME_LANGUAGE = "gameLanguage";
    private static final String DEFAULT_GAME_LANGUAGE= "ru";

    private static final String ENTERED_ITEM = "enteredItem";
    private static final String ENTERED_LETTER = "enteredLetter";
    private static final String ACTIVE_VIEW_NUMBERS = "activeViewNumbers";

    public static final String ROW_COUNT = "rowCount";
    public static final String COL_COUNT = "colCount";
    public static final String INIT_WORD = "initWord";
    public static final String PLAYER_ID = "playerId";

    private GameContract.Presenter mPresenter;

    private InputMethodManager imm;

    private RecyclerView recyclerView;
    private TextView textViewScore;
    private EditText editTextFieldItem;
    private View viewContent;
    private TextView textViewScoreAnim;
    private FieldRecyclerAdapter adapter;

    private ActionMode actionMode = null;

    public static Intent createIntent(Context context, int rowCount, int colCount) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(ROW_COUNT, rowCount);
        intent.putExtra(COL_COUNT, colCount);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        int rowCount = intent.getIntExtra(ROW_COUNT, 5);
        int colCount = intent.getIntExtra(COL_COUNT, 5);

        viewContent = findViewById(R.id.content);
        recyclerView = findViewById(R.id.activity_game_recycler_view);
        textViewScore = findViewById(R.id.tv_score);
        editTextFieldItem = findViewById(R.id.et_input_field_item);
        textViewScoreAnim = findViewById(R.id.tv_score_anim);

        PresenterManager.provideGamePresenter(this, this);

        adapter = new FieldRecyclerAdapter(mPresenter, colCount * rowCount);
        adapter.setOnItemClickListener(this);
        adapter.setHasStableIds(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, colCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        BorderDecoration decoration = new BorderDecoration(this, Color.GRAY, 2f, colCount);
        recyclerView.addItemDecoration(decoration);

        KeyboardVisibilityEvent.setEventListener(
                this, new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        Log.d("TESTING", "GameActivity -> onVisibilityChanged() -> isOpen = " + isOpen);
                        if (isOpen) {
                            mPresenter.onKeyboardOpen();
                        } else {
                            mPresenter.onKeyboardHidden();
                        }
                    }
                });

        editTextFieldItem.addTextChangedListener(new SingleLetterTextWatcher());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void onItemClick(View itemView, int position) {
        mPresenter.onCellClicked(position);
    }

    @Override
    public void onItemLongClick(View itemView, int position) {
        mPresenter.onCellLongClicked(position);
    }

    @Override
    public void onBackPressed() {
        mPresenter.finishGame();
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
                recyclerView.smoothScrollToPosition(cellNumber);
            }
        }, 100);
    }

    @Override
    public void hideKeyboard() {
        editTextFieldItem.requestFocus();
        imm.hideSoftInputFromWindow(editTextFieldItem.getWindowToken(), 0);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showScoreAnimation(int deltaScore) {
        textViewScoreAnim.setText(getString(R.string.plus_points, deltaScore));
        textViewScoreAnim.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.score);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }
            @Override
            public void onAnimationEnd(Animation animation) {
                textViewScoreAnim.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {  }
        });
        textViewScoreAnim.startAnimation(animation);
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
                // todo text in resources! and call showToast()
                Toast.makeText(GameActivity.this, "It is not correct symbol", Toast.LENGTH_SHORT).show();
                break;
            case NEED_ENTER_LETTER:
                showSnackbar(R.string.need_enter_word);
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

    class SingleLetterTextWatcher implements TextWatcher {

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
            Log.d("TESTING", "GameActivity -> onDestroyActionMode() ");
        }
    }
}
