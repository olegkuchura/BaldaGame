package com.adlab.balda.presenters;

import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.database.BaldaDataBase;

import androidx.annotation.NonNull;

public class GameSettingsPresenter implements GameSettingsContract.Presenter {
    private static final int MAX_FIELD_SIZE = 10;
    private static final int MIN_FIELD_SIZE = 3;
    private static final int DEFAULT_FIELD_SIZE = 5;

    private BaldaDataBase mDatabase;

    private GameSettingsContract.View mGameSettingsView;

    private String mCurrentWord;
    private boolean mIsCurrentWordExist;
    private int mRowCount;
    private int mColCount;

    public GameSettingsPresenter(@NonNull BaldaDataBase database, @NonNull GameSettingsContract.View view) {
        mDatabase = database;
        mGameSettingsView = view;

        mGameSettingsView.setPresenter(this);

        mRowCount = DEFAULT_FIELD_SIZE;
        mColCount = DEFAULT_FIELD_SIZE;
    }

    @Override
    public void setView(@NonNull GameSettingsContract.View view) {
        mGameSettingsView = view;

        mGameSettingsView.setPresenter(this);
    }

    @Override
    public void resetView() {
        mGameSettingsView = null;
    }

    @Override
    public void start() {
        if (mCurrentWord == null) {  // first start
            generateRandomWord();
        }

        mGameSettingsView.setRowCount(mRowCount);
        mGameSettingsView.setColCount(mColCount);
    }

    @Override
    public void initWordChanged(@NonNull String newWord) {
        mCurrentWord = newWord;
        if (mCurrentWord.isEmpty()) {
            mIsCurrentWordExist = false;
            mGameSettingsView.showEmptyWordError();
            mGameSettingsView.setStartGameEnabled(false);
            return;
        }
        mIsCurrentWordExist = mDatabase.isWordExist(mCurrentWord);
        if (mIsCurrentWordExist){
            mGameSettingsView.hideInitWordError();
            mGameSettingsView.setStartGameEnabled(newWord.length() == mColCount);
        } else {
            mGameSettingsView.showNonExistentWordError();
            mGameSettingsView.setStartGameEnabled(false);
        }
    }

    @Override
    public void generateRandomWord() {
        String word = mDatabase.getRandomWord(mColCount);
        if (word != null) {
            mCurrentWord = word;
            mIsCurrentWordExist = true;
            mGameSettingsView.setInitWord(mCurrentWord);
            mGameSettingsView.hideInitWordError();
            mGameSettingsView.setStartGameEnabled(true);
        }
    }

    @Override
    public void increaseRowCount() {
        mRowCount++;
        if (mRowCount == MAX_FIELD_SIZE) {
            mGameSettingsView.setIncreaseRowCountEnabled(false);
        }
        else {
            mGameSettingsView.setReduceRowCountEnabled(true);
        }
        mGameSettingsView.setRowCount(mRowCount);
    }

    @Override
    public void reduceRowCount() {
        mRowCount--;
        if (mRowCount == MIN_FIELD_SIZE) {
            mGameSettingsView.setReduceRowCountEnabled(false);
        }
        else {
            mGameSettingsView.setIncreaseRowCountEnabled(true);
        }
        mGameSettingsView.setRowCount(mRowCount);
    }

    @Override
    public void increaseColumnCount() {
        mColCount++;
        if (mColCount == MAX_FIELD_SIZE) {
            mGameSettingsView.setIncreaseColCountEnabled(false);
        }
        else {
            mGameSettingsView.setReduceColCountEnabled(true);
        }
        mGameSettingsView.setColCount(mColCount);
        if (mCurrentWord.length() == mColCount && mIsCurrentWordExist) {
            mGameSettingsView.setStartGameEnabled(true);
        } else {
            generateRandomWord();
        }
    }

    @Override
    public void reduceColumnCount() {
        mColCount--;
        if (mColCount == MIN_FIELD_SIZE) {
            mGameSettingsView.setReduceColCountEnabled(false);
        }
        else {
            mGameSettingsView.setIncreaseColCountEnabled(true);
        }
        mGameSettingsView.setColCount(mColCount);
        if (mCurrentWord.length() == mColCount && mIsCurrentWordExist) {
            mGameSettingsView.setStartGameEnabled(true);
        } else {
            generateRandomWord();
        }
    }

    @Override
    public void startGame() {
        mGameSettingsView.showGameScreen(mCurrentWord, mRowCount, mColCount);
    }

    @Override
    public void destroy() {
        mDatabase.close();
    }
}
