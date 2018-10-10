package com.adlab.balda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adlab.balda.database.BaldaDataBase;
import com.adlab.balda.R;
import com.adlab.balda.model.ActivityPlayer;
import com.adlab.balda.model.Game;
import com.adlab.balda.model.GameLab;
import com.adlab.balda.model.Player;

public class GameSettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String ROW_COUNT = "rowCount";
    private static final String COL_COUNT = "colCount";

    private ImageButton buttonIncreaseRowCount;
    private ImageButton buttonReduceRowCount;
    private ImageButton buttonIncreaseColCount;
    private ImageButton buttonReduceColCount;
    private Button buttonStartGame;
    private TextView textViewRowCount;
    private TextView textViewColCount;
    private EditText editTextInitWord;
    private TextView textViewNonExistentWord;

    private BaldaDataBase dataBase;

    private int rowCount = 5;
    private int colCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        getSupportActionBar().setTitle("Settings for single game");

        if (savedInstanceState != null) {
            rowCount = savedInstanceState.getInt(ROW_COUNT);
            colCount = savedInstanceState.getInt(COL_COUNT);
        }

        buttonIncreaseRowCount = findViewById(R.id.b_increase_row_count);
        buttonReduceRowCount = findViewById(R.id.b_reduce_row_count);
        buttonIncreaseColCount = findViewById(R.id.b_increase_col_count);
        buttonReduceColCount = findViewById(R.id.b_reduce_col_count);
        buttonStartGame = findViewById(R.id.b_start_game);
        ImageButton buttonGenerateWord = findViewById(R.id.ib_generate_word);
        textViewRowCount = findViewById(R.id.tv_row_count);
        textViewColCount = findViewById(R.id.tv_column_count);
        editTextInitWord = findViewById(R.id.et_initWord);
        textViewNonExistentWord = findViewById(R.id.tv_non_existent_word);

        editTextInitWord.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editTextInitWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString();
                word = word.toLowerCase();
                boolean isWordExist = dataBase.isWordExist(word);
                if (isWordExist) {
                    textViewNonExistentWord.setVisibility(View.INVISIBLE);
                    if (word.length() != colCount) {
                        buttonStartGame.setEnabled(false);
                        buttonStartGame.setClickable(false);
                    } else {
                        buttonStartGame.setEnabled(true);
                        buttonStartGame.setClickable(true);
                    }
                } else {
                    textViewNonExistentWord.setVisibility(View.VISIBLE);
                    buttonStartGame.setEnabled(false);
                    buttonStartGame.setClickable(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        buttonIncreaseRowCount.setOnClickListener(this);
        buttonReduceRowCount.setOnClickListener(this);
        buttonIncreaseColCount.setOnClickListener(this);
        buttonReduceColCount.setOnClickListener(this);
        buttonStartGame.setOnClickListener(this);
        buttonGenerateWord.setOnClickListener(this);

        textViewRowCount.setText(rowCount + "");
        textViewColCount.setText(colCount + "");

        dataBase = new BaldaDataBase(this);

        generateRandomWord();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, GameSettingsActivity.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ROW_COUNT, rowCount);
        outState.putInt(COL_COUNT, colCount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBase.close();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_increase_row_count:
                increaseRowCount();
                break;
            case R.id.b_reduce_row_count:
                reduceRowCount();
                break;
            case R.id.b_increase_col_count:
                increaseColumnCount();
                break;
            case R.id.b_reduce_col_count:
                reduceColumnCount();
                break;
            case R.id.ib_generate_word:
                generateRandomWord();
                break;
            case R.id.b_start_game:
                startGameActivity();
                break;
        }
    }

    private void generateRandomWord() {
        String word = dataBase.getRandomWord(colCount);
        editTextInitWord.setText(word);
        textViewNonExistentWord.setVisibility(View.INVISIBLE);
        buttonStartGame.setEnabled(true);
        buttonStartGame.setClickable(true);
    }

    private void startGameActivity() {
        GameLab gameLab = GameLab.getInstance();
        Player[] players =  {null};
        Game game = gameLab.createGame(rowCount, colCount, editTextInitWord.getText().toString().toLowerCase(), players, getApplicationContext());
        game.start();
        startActivity(GameActivity.createIntent(this, 0, rowCount, colCount));
        finish();
    }

    private void increaseRowCount() {
        rowCount++;
        if (rowCount == 10) {
            buttonIncreaseRowCount.setEnabled(false);
            buttonIncreaseRowCount.setClickable(false);
        }
        else {
            buttonReduceRowCount.setEnabled(true);
            buttonReduceRowCount.setClickable(true);
        }
        textViewRowCount.setText("" + rowCount);
    }

    private void reduceRowCount() {
        rowCount--;
        if (rowCount == 3) {
            buttonReduceRowCount.setEnabled(false);
            buttonReduceRowCount.setClickable(false);
        }
        else {
            buttonIncreaseRowCount.setEnabled(true);
            buttonIncreaseRowCount.setClickable(true);
        }
        textViewRowCount.setText("" + rowCount);
    }

    private void increaseColumnCount() {
        colCount++;
        if (colCount == 10) {
            buttonIncreaseColCount.setEnabled(false);
            buttonIncreaseColCount.setClickable(false);
        }
        else {
            buttonReduceColCount.setEnabled(true);
            buttonReduceColCount.setClickable(true);
        }
        textViewColCount.setText("" + colCount);
        String word = editTextInitWord.getText().toString().toLowerCase();
        if (word.length() == colCount && dataBase.isWordExist(word)) {
            buttonStartGame.setEnabled(true);
            buttonStartGame.setClickable(true);
        } else {
            generateRandomWord();
        }
    }

    private void reduceColumnCount() {
        colCount--;
        if (colCount == 3) {
            buttonReduceColCount.setEnabled(false);
            buttonReduceColCount.setClickable(false);
        }
        else {
            buttonIncreaseColCount.setEnabled(true);
            buttonIncreaseColCount.setClickable(true);
        }
        textViewColCount.setText("" + colCount);
        String word = editTextInitWord.getText().toString().toLowerCase();
        if (word.length() == colCount && dataBase.isWordExist(word)) {
            buttonStartGame.setEnabled(true);
            buttonStartGame.setClickable(true);
        } else {
            generateRandomWord();
        }
    }

}
