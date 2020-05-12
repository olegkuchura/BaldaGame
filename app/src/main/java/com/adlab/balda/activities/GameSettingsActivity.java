package com.adlab.balda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.R;
import com.adlab.balda.model.GameLab;
import com.adlab.balda.model.GamePlayer;
import com.adlab.balda.utils.PresenterManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GameSettingsActivity extends AppCompatActivity implements GameSettingsContract.View, View.OnClickListener{

    private GameSettingsContract.Presenter mPresenter;

    private ImageButton mButtonIncreaseRowCount;
    private ImageButton mButtonReduceRowCount;
    private ImageButton mButtonIncreaseColCount;
    private ImageButton mButtonReduceColCount;
    private Button mButtonStartGame;
    private TextView mTextViewRowCount;
    private TextView mTextViewColCount;
    private TextInputEditText mEditTextInitWord;
    private TextInputLayout mTextInputLayoutInitWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        getSupportActionBar().setTitle(R.string.settings_for_one_man_game);

        mButtonIncreaseRowCount = findViewById(R.id.b_increase_row_count);
        mButtonReduceRowCount = findViewById(R.id.b_reduce_row_count);
        mButtonIncreaseColCount = findViewById(R.id.b_increase_col_count);
        mButtonReduceColCount = findViewById(R.id.b_reduce_col_count);
        mButtonStartGame = findViewById(R.id.b_start_game);
        ImageButton buttonGenerateWord = findViewById(R.id.ib_generate_word);
        mTextViewRowCount = findViewById(R.id.tv_row_count);
        mTextViewColCount = findViewById(R.id.tv_column_count);
        mEditTextInitWord = findViewById(R.id.et_initWord);
        mTextInputLayoutInitWord = findViewById(R.id.til);

        mEditTextInitWord.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEditTextInitWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString().toLowerCase();
                mPresenter.initWordChanged(word);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        mButtonIncreaseRowCount.setOnClickListener(this);
        mButtonReduceRowCount.setOnClickListener(this);
        mButtonIncreaseColCount.setOnClickListener(this);
        mButtonReduceColCount.setOnClickListener(this);
        mButtonStartGame.setOnClickListener(this);
        buttonGenerateWord.setOnClickListener(this);

        PresenterManager.provideGameSettingsPresenter(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull GameSettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setInitWord(String word) {
        mEditTextInitWord.setText(word);
    }

    @Override
    public void showNonExistentWordError() {
        mTextInputLayoutInitWord.setError(getString(R.string.non_existent_word));
    }

    @Override
    public void showEmptyWordError() {
        mTextInputLayoutInitWord.setError(getString(R.string.need_enter_word));
    }

    @Override
    public void hideInitWordError() {
        mTextInputLayoutInitWord.setError(null);
    }

    @Override
    public void setRowCount(int rowCount) {
        mTextViewRowCount.setText(String.valueOf(rowCount));
    }

    @Override
    public void setColCount(int colCount) {
        mTextViewColCount.setText(String.valueOf(colCount));
    }

    @Override
    public void setStartGameEnabled(boolean enabled) {
        mButtonStartGame.setEnabled(enabled);
        mButtonStartGame.setClickable(enabled);
    }

    @Override
    public void setIncreaseRowCountEnabled(boolean enabled) {
        mButtonIncreaseRowCount.setEnabled(enabled);
        mButtonIncreaseRowCount.setClickable(enabled);
    }

    @Override
    public void setReduceRowCountEnabled(boolean enabled) {
        mButtonReduceRowCount.setEnabled(enabled);
        mButtonReduceRowCount.setClickable(enabled);
    }

    @Override
    public void setIncreaseColCountEnabled(boolean enabled) {
        mButtonIncreaseColCount.setEnabled(enabled);
        mButtonIncreaseColCount.setClickable(enabled);
    }

    @Override
    public void setReduceColCountEnabled(boolean enabled) {
        mButtonReduceColCount.setEnabled(enabled);
        mButtonReduceColCount.setClickable(enabled);
    }

    @Override
    public void showGameScreen(String word, int rowCount, int colCount) {
        GameLab gameLab = GameLab.getInstance();
        GamePlayer player =  new GamePlayer();
        gameLab.createGame(rowCount, colCount, word, player, getApplicationContext());
        startActivity(GameActivity.createIntent(this));
        finish();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, GameSettingsActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.resetView();
        if (isFinishing()) {
            PresenterManager.resetGameSettingsPresenter();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_increase_row_count:
                mPresenter.increaseRowCount();
                break;
            case R.id.b_reduce_row_count:
                mPresenter.reduceRowCount();
                break;
            case R.id.b_increase_col_count:
                mPresenter.increaseColumnCount();
                break;
            case R.id.b_reduce_col_count:
                mPresenter.reduceColumnCount();
                break;
            case R.id.ib_generate_word:
                mPresenter.generateRandomWord();
                break;
            case R.id.b_start_game:
                mPresenter.startGame();
                break;
        }
    }

}
