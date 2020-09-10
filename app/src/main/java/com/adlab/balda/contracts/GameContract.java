package com.adlab.balda.contracts;

import com.adlab.balda.BasePresenter;
import com.adlab.balda.BaseView;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.enums.GameMessageType;

import java.util.List;

import androidx.annotation.NonNull;

public interface GameContract {

    interface View extends BaseView<Presenter> {

        void showField(int rowCount, int colCount, FieldType fieldType, FieldSizeType fieldSize);

        void updateCell(int cellNumber);

        void showKeyboard();

        void scrollFieldToCell(int cellNumber);

        void hideKeyboard();

        void activateActionMode();

        void updateActivatedLetterSequence(String letterSequence);

        void deactivateActionMode();

        void updateScore(int score);

        void showMessage(GameMessageType message);

        void showScoreAnimation(int deltaScore);

        void updateUsedWords(List<String> listOfWords);

        void showGameResult(int score);

        void showGameExit();

    }

    interface Presenter extends BasePresenter, BaseGamePresenter {

        void setView(@NonNull View view);

        void resetView();

        void onCellClicked(int cellNumber);

        void onCellLongClicked(int cellNumber);

        void onKeyboardOpen();

        void onKeyboardHidden();

        void enterLetter(char letter);

        void clearEnteredLetter();

        void deactivateActionMode();

        void confirmWord();

        void finishGame();

    }
}
