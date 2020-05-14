package com.adlab.balda.presenters;


import android.util.Log;

import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.model.GameLab;
import com.adlab.balda.model.GamePlayer;
import com.adlab.balda.model.OneManGame;

import java.util.LinkedList;

import androidx.annotation.NonNull;

import static com.adlab.balda.contracts.GameContract.MessageType.INCORRECT_SYMBOL;
import static com.adlab.balda.contracts.GameContract.MessageType.MUST_CONTAIN_NEW_LETTER;
import static com.adlab.balda.contracts.GameContract.MessageType.NEED_ENTER_LETTER;
import static com.adlab.balda.contracts.GameContract.MessageType.NO_SUCH_WORD;
import static com.adlab.balda.contracts.GameContract.MessageType.WORD_ALREADY_USED;

public class GamePresenter implements GameContract.Presenter {
    private static final char EMPTY_CELL_VALUE = ' ';
    private static final int UNDEFINED_CELL_NUMBER = -1;

    private GameContract.View mGameView;

    private OneManGame game;
    private GamePlayer player;

    private int mRowCount;
    private int mColCount;

    private char[] mItems;
    private int mSelectedCellNumber;
    private int mEnteredCellNumber;
    private char mEnteredLetter;
    private LinkedList<Integer> mActiveCellNumbers;

    public GamePresenter(@NonNull GameContract.View view) {
        mGameView = view;

        mGameView.setPresenter(this);

        game = GameLab.getInstance().getGame();

        mRowCount = game.getRowCount();
        mColCount = game.getColCount();

        mItems = game.getField();
        player = game.getPlayer();

        mSelectedCellNumber = UNDEFINED_CELL_NUMBER;
        mEnteredCellNumber = UNDEFINED_CELL_NUMBER;
        mEnteredLetter = EMPTY_CELL_VALUE;
        mActiveCellNumbers = new LinkedList<>();
    }

    @Override
    public void setView(@NonNull GameContract.View view) {
        mGameView = view;

        mGameView.setPresenter(this);
    }

    @Override
    public void resetView() {
        mGameView = null;
    }

    @Override
    public void start() {
        mGameView.showField(mRowCount, mColCount);

        mGameView.updateScore(player.getScore());

        mGameView.updateUsedWords(game.getUsedWords());

        if (!mActiveCellNumbers.isEmpty()) {
            mGameView.activateActionMode();
            mGameView.updateActivatedLetterSequence(getEnteredLetterSequence());
        }

        if (mSelectedCellNumber != UNDEFINED_CELL_NUMBER) {
            int oldSelected = mSelectedCellNumber;
            mSelectedCellNumber = UNDEFINED_CELL_NUMBER;
            mGameView.updateCell(oldSelected);
        }
    }

    @Override
    public void bindCell(@NonNull GameContract.CellView cellView, int cellNumber) {
        if (cellNumber == mEnteredCellNumber) {
            cellView.showEnteredLetter(mEnteredLetter);
        } else {
            cellView.showLetter(mItems[cellNumber]);
        }

        if (cellNumber == mEnteredCellNumber && mActiveCellNumbers.isEmpty()) {
            cellView.showClearLetterButton();
        } else {
            cellView.hideClearLetterButton();
        }

        cellView.resetState();
        if (cellNumber == mSelectedCellNumber) {
            cellView.select();
        } else {
            int activeIndex = mActiveCellNumbers.indexOf(cellNumber);
            if (activeIndex != -1) {
                cellView.activate(activeIndex + 1);
            }
        }
    }

    @Override
    public void onCellClicked(int cellNumber) {
        if (mActiveCellNumbers.isEmpty()) {
            if (cellNumber == mSelectedCellNumber) return;
            int oldSelectedCellNumber = mSelectedCellNumber;
            boolean isSelected = setSelectedCellNumber(cellNumber);
            mGameView.updateCell(oldSelectedCellNumber);
            if (isSelected) {
                mGameView.updateCell(cellNumber);
                mGameView.showKeyboard();
            } else {
                mGameView.hideKeyboard();
            }
        } else {
            toggleActiveModeForCell(cellNumber);
            if (mActiveCellNumbers.isEmpty()) {
                mGameView.deactivateActionMode();
            } else {
                mGameView.updateActivatedLetterSequence(getEnteredLetterSequence());
            }
        }
    }

    @Override
    public void onCellLongClicked(int cellNumber) {
        // remove selection if it is needed
        if (mActiveCellNumbers.isEmpty() && mSelectedCellNumber != UNDEFINED_CELL_NUMBER) {
            int oldSelectedCellNumber = mSelectedCellNumber;
            mSelectedCellNumber = UNDEFINED_CELL_NUMBER;
            mGameView.updateCell(oldSelectedCellNumber);
            mGameView.hideKeyboard();
        }

        if (mEnteredLetter == EMPTY_CELL_VALUE) {
            mGameView.showMessage(NEED_ENTER_LETTER);
            return;
        }

        mGameView.updateCell(mEnteredCellNumber);

        boolean wasActiveModeActivated = !mActiveCellNumbers.isEmpty();

        toggleActiveModeForCell(cellNumber);

        if (mActiveCellNumbers.isEmpty() && wasActiveModeActivated) {
            mGameView.deactivateActionMode();
        } else if (!mActiveCellNumbers.isEmpty() && !wasActiveModeActivated){
            mGameView.activateActionMode();
            mGameView.updateActivatedLetterSequence(getEnteredLetterSequence());
        }
    }

    @Override
    public void onKeyboardOpen() {
        if (mSelectedCellNumber == UNDEFINED_CELL_NUMBER) {
            mGameView.hideKeyboard();
        } else {
            mGameView.scrollFieldToCell(mSelectedCellNumber);
        }
    }

    @Override
    public void onKeyboardHidden() {
        int oldSelectedCellNumber = mSelectedCellNumber;
        mSelectedCellNumber = UNDEFINED_CELL_NUMBER;
        mGameView.updateCell(oldSelectedCellNumber);
    }

    @Override
    public void enterLetter(char letter) {
        if (isCorrectChar(letter)) {
            int oldEnteredCellNumber = mEnteredCellNumber;
            mEnteredLetter = letter;
            mEnteredCellNumber = mSelectedCellNumber;
            mGameView.updateCell(mEnteredCellNumber);
            if (oldEnteredCellNumber != mEnteredCellNumber) {
                mGameView.updateCell(oldEnteredCellNumber);
            }
        } else {
            mGameView.showMessage(INCORRECT_SYMBOL);
        }
    }

    @Override
    public void clearEnteredLetter() {
        int oldEnteredCellNumber = mEnteredCellNumber;
        mEnteredLetter = EMPTY_CELL_VALUE;
        mEnteredCellNumber = UNDEFINED_CELL_NUMBER;

        mGameView.updateCell(oldEnteredCellNumber);
    }

    @Override
    public void deactivateActionMode() {
        while (mActiveCellNumbers.size() != 0) {
            int removedCellNumber = mActiveCellNumbers.removeLast();
            mGameView.updateCell(removedCellNumber);
        }
        mGameView.updateCell(mEnteredCellNumber);
    }

    @Override
    public void confirmWord() {
        if (mActiveCellNumbers.contains(mEnteredCellNumber)) {
            final int oldScore = player.getScore();
            game.makeMove(mEnteredCellNumber, mEnteredLetter, getActiveCellNumbers(), new OneManGame.MakeMoveCallback() {
                @Override
                public void makeNextMove() {
                    mItems = game.getField();
                    mEnteredLetter = EMPTY_CELL_VALUE;
                    mEnteredCellNumber = UNDEFINED_CELL_NUMBER;
                    int[] activeCellNumbers = getActiveCellNumbers();
                    mActiveCellNumbers.clear();
                    for (int cellNumber : activeCellNumbers) {
                        mGameView.updateCell(cellNumber);
                    }
                    mGameView.deactivateActionMode();
                    mGameView.updateScore(player.getScore());
                    mGameView.showScoreAnimation(player.getScore() - oldScore);
                    mGameView.updateUsedWords(game.getUsedWords());
                }

                @Override
                public void onWordIsNotExist() {
                    mGameView.showMessage(NO_SUCH_WORD);
                }

                @Override
                public void onWordIsAlreadyUsed() {
                    mGameView.showMessage(WORD_ALREADY_USED);
                }

                @Override
                public void onGameFinished() {
                    mGameView.showGameResult(player.getScore());
                }
            });

        } else {
            mGameView.showMessage(MUST_CONTAIN_NEW_LETTER);
        }
    }

    @Override
    public void finishGame() {
        mGameView.showGameExit();
    }

    private boolean isCorrectChar(char c) {
        String gameLanguage = "ru";
        switch (gameLanguage) {
            case "ru": return ((c >= 0x0410 && c<= 0x044F) || c == 0x0451 || c == 0x0401);
            default:   return false;
        }
    }

    private boolean setSelectedCellNumber(int cellNumber) {
        if (isCellAvailableForEntering(cellNumber)) {
            mSelectedCellNumber = cellNumber;
            return true;
        } else {
            mSelectedCellNumber = UNDEFINED_CELL_NUMBER;
            return false;
        }
    }

    private boolean isCellAvailableForEntering(int cellNumber) {
        if(cellNumber == UNDEFINED_CELL_NUMBER || Character.isLetter(mItems[cellNumber])){
            return false;
        }
        int row = cellNumber / mColCount;
        int col = cellNumber % mColCount;
        if(row > 0 && Character.isLetter(mItems[(row-1) * mColCount + col])){
            return true;
        }
        if(col < mColCount - 1 && Character.isLetter(mItems[row * mColCount + col + 1])){
            return true;
        }
        if(row < mRowCount - 1 && Character.isLetter(mItems[(row+1) * mColCount + col])){
            return true;
        }
        if(col > 0 && Character.isLetter(mItems[row * mColCount + col - 1])){
            return true;
        }
        return false;
    }

    private void toggleActiveModeForCell(int cellNumber) {
        if (mItems[cellNumber] == EMPTY_CELL_VALUE && cellNumber != mEnteredCellNumber) {
            return;
        }

        int index;
        if((index = mActiveCellNumbers.indexOf(cellNumber)) != -1){
            while (mActiveCellNumbers.size() != index) {
                int removedCellNumber = mActiveCellNumbers.removeLast();
                mGameView.updateCell(removedCellNumber);
            }
            return;
        }

        if(mActiveCellNumbers.isEmpty()){
            mActiveCellNumbers.addLast(cellNumber);
            mGameView.updateCell(cellNumber);
            return;
        }

        int rowOfNew = cellNumber / mColCount;
        int colOfNew = cellNumber % mColCount;
        int lastActivatedViewPosition = mActiveCellNumbers.getLast();
        int rowOfLast = lastActivatedViewPosition / mColCount;
        int colOfLast = lastActivatedViewPosition % mColCount;

        if(     ((rowOfNew == rowOfLast) && ((colOfNew == (colOfLast + 1))||(colOfNew == (colOfLast - 1)))) ||
                ((colOfNew == colOfLast) && ((rowOfNew == (rowOfLast + 1))||(rowOfNew == (rowOfLast - 1)))) ) {
            mActiveCellNumbers.addLast(cellNumber);
            mGameView.updateCell(cellNumber);
        }
    }

    private String getEnteredLetterSequence(){
        StringBuilder letterSequence = new StringBuilder();
        for (Integer i : mActiveCellNumbers){
            if (i == mEnteredCellNumber) {
                letterSequence.append(mEnteredLetter);
            } else {
                letterSequence.append(mItems[i]);
            }
        }
        return letterSequence.toString();
    }

    private int[] getActiveCellNumbers() {
        int[] result = new int[mActiveCellNumbers.size()];
        int i = 0;

        for (int el : mActiveCellNumbers){
            result[i] = el;
            i++;
        }
        return result;
    }
}
