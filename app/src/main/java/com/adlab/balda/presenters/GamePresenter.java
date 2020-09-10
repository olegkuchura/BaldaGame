package com.adlab.balda.presenters;


import android.util.Log;

import com.adlab.balda.contracts.CellView;
import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.enums.GameMessageType;
import com.adlab.balda.model.GameLab;
import com.adlab.balda.model.GamePlayer;
import com.adlab.balda.model.OneManGame;
import com.adlab.balda.model.view_field.AbstractViewField;
import com.adlab.balda.model.view_field.ClassicViewField;
import com.adlab.balda.model.view_field.HexagonViewField;

import java.util.List;

import androidx.annotation.NonNull;

import static com.adlab.balda.utils.UtilsKt.isCorrectChar;

public class GamePresenter implements GameContract.Presenter {

    private GameContract.View mGameView;

    private OneManGame game;
    private GamePlayer player;

    private int mRowCount;
    private int mColCount;

    private AbstractViewField field;

    public GamePresenter(@NonNull GameContract.View view) {
        mGameView = view;

        mGameView.setPresenter(this);

        game = GameLab.getInstance().getGame();

        mRowCount = game.getRowCount();
        mColCount = game.getColCount();

        field = getViewFieldByType(game.getFieldType(), game.getCopyOfCells(), game.getFieldSize());
        player = game.getPlayer();
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
        mGameView.showField(mRowCount, mColCount, game.getFieldType(), game.getFieldSize());

        mGameView.updateScore(player.getScore());

        mGameView.updateUsedWords(game.getUsedWords());

        if (!field.getActiveCellNumbers().isEmpty()) {
            mGameView.activateActionMode();
            mGameView.updateActivatedLetterSequence(field.getEnteredLetterSequence());
        }

        if (field.getHasSelectedCell()) {
            mGameView.updateCell(field.clearSelection());
        }
    }

    @Override
    public void bindCell(@NonNull CellView cellView, int cellNumber) {
        if (cellNumber == field.getEnteredCellNumber()) {
            cellView.showEnteredLetter(field.getEnteredLetter());
        } else {
            cellView.showLetter(field.getItems()[cellNumber]);
        }

        if (cellNumber == field.getEnteredCellNumber() && field.getActiveCellNumbers().isEmpty()) {
            cellView.showClearLetterButton();
        } else {
            cellView.hideClearLetterButton();
        }

        cellView.resetState();
        if (cellNumber == field.getSelectedCellNumber()) {
            cellView.select();
        } else {
            int activeIndex = field.getActiveCellNumbers().indexOf(cellNumber);
            if (activeIndex != -1) {
                cellView.activate(activeIndex + 1);
            }
        }
    }

    @Override
    public void onCellClicked(int cellNumber) {
        if (field.getActiveCellNumbers().isEmpty()) {
            if (cellNumber == field.getSelectedCellNumber()) return;
            int oldSelectedCellNumber = field.getSelectedCellNumber();
            boolean isSelected = field.trySetSelectedCellNumber(cellNumber);
            mGameView.updateCell(oldSelectedCellNumber);
            if (isSelected) {
                mGameView.updateCell(cellNumber);
                mGameView.showKeyboard();
            } else {
                mGameView.hideKeyboard();
            }
        } else {
            List<Integer> changedItems = field.toggleActiveModeForCell(cellNumber);
            for (int item : changedItems) {
                mGameView.updateCell(item);
            }
            if (field.getActiveCellNumbers().isEmpty()) {
                mGameView.deactivateActionMode();
            } else {
                mGameView.updateActivatedLetterSequence(field.getEnteredLetterSequence());
            }
        }
    }

    @Override
    public void onCellLongClicked(int cellNumber) {
        // remove selection if it is needed
        if (field.getActiveCellNumbers().isEmpty() && field.getHasSelectedCell()) {
            mGameView.updateCell(field.clearSelection());
            mGameView.hideKeyboard();
        }

        if (!field.getHasEnteredLetter()) {
            mGameView.showMessage(GameMessageType.NEED_ENTER_LETTER);
            return;
        }

        mGameView.updateCell(field.getEnteredCellNumber());

        boolean wasActiveModeActivated = !field.getActiveCellNumbers().isEmpty();

        List<Integer> changedItems = field.toggleActiveModeForCell(cellNumber);
        for (int item : changedItems) {
            mGameView.updateCell(item);
        }

        if (field.getActiveCellNumbers().isEmpty() && wasActiveModeActivated) {
            mGameView.deactivateActionMode();
        } else if (!field.getActiveCellNumbers().isEmpty() && !wasActiveModeActivated){
            mGameView.activateActionMode();
            mGameView.updateActivatedLetterSequence(field.getEnteredLetterSequence());
        }
    }

    @Override
    public void onKeyboardOpen() {
        if (!field.getHasSelectedCell()) {
            mGameView.hideKeyboard();
        } else {
            mGameView.scrollFieldToCell(field.getSelectedCellNumber());
        }
    }

    @Override
    public void onKeyboardHidden() {
        mGameView.updateCell(field.clearSelection());
    }

    @Override
    public void enterLetter(char letter) {
        if (isCorrectChar(letter)) {
            int oldEnteredCellNumber = field.getEnteredCellNumber();
            field.setEnteredLetter(letter);
            field.setEnteredCellNumber(field.getSelectedCellNumber());
            mGameView.updateCell(field.getEnteredCellNumber());
            if (oldEnteredCellNumber != field.getEnteredCellNumber()) {
                mGameView.updateCell(oldEnteredCellNumber);
            }
        } else {
            mGameView.showMessage(GameMessageType.INCORRECT_SYMBOL);
        }
    }

    @Override
    public void clearEnteredLetter() {
        mGameView.updateCell(field.clearEnteredLetter());
    }

    @Override
    public void deactivateActionMode() {
        while (field.getActiveCellNumbers().size() != 0) {
            int removedCellNumber = field.getActiveCellNumbers().removeLast();
            mGameView.updateCell(removedCellNumber);
        }
        mGameView.updateCell(field.getEnteredCellNumber());
    }

    @Override
    public void confirmWord() {
        if (field.activeCellsContainEnteredLetter()) {
            final int oldScore = player.getScore();
            game.makeMove(field.getEnteredCellNumber(), field.getEnteredLetter(),
                    field.getActiveCellNumbersArray(), new OneManGame.MakeMoveCallback() {
                @Override
                public void makeNextMove() {
                    updateViewAfterSuccessfulMove(oldScore);
                }

                @Override
                public void onWordIsNotExist() {
                    mGameView.showMessage(GameMessageType.NO_SUCH_WORD);
                }

                @Override
                public void onWordIsAlreadyUsed() {
                    mGameView.showMessage(GameMessageType.WORD_ALREADY_USED);
                }

                @Override
                public void onGameFinished() {
                    updateViewAfterSuccessfulMove(oldScore);

                    mGameView.showGameResult(player.getScore());
                }
            });

        } else {
            mGameView.showMessage(GameMessageType.MUST_CONTAIN_NEW_LETTER);
        }
    }

    @Override
    public void finishGame() {
        mGameView.showGameExit();
    }

    private void updateViewAfterSuccessfulMove(int oldScore) {
        field.setItems(game.getCopyOfCells());
        field.clearEnteredLetter();
        int[] activeCellNumbers = field.getActiveCellNumbersArray();
        field.getActiveCellNumbers().clear();
        for (int cellNumber : activeCellNumbers) {
            mGameView.updateCell(cellNumber);
        }
        mGameView.deactivateActionMode();
        mGameView.updateScore(player.getScore());
        //mGameView.showScoreAnimation(player.getScore() - oldScore);
        mGameView.updateUsedWords(game.getUsedWords());
    }

    private AbstractViewField getViewFieldByType(FieldType fieldType, char[] items, FieldSizeType fieldSize) {
        if (fieldType == FieldType.SQUARE) {
            return new ClassicViewField(items, fieldSize);
        } else {
            return new HexagonViewField(items, fieldSize);
        }
    }
}
