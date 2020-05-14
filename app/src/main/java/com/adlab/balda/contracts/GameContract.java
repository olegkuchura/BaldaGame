package com.adlab.balda.contracts;

import com.adlab.balda.BasePresenter;
import com.adlab.balda.BaseView;

import java.util.List;

import androidx.annotation.NonNull;

public interface GameContract {

    interface View extends BaseView<Presenter> {

        void showField(int rowCount, int colCount);

        void updateCell(int cellNumber);

        void showKeyboard();

        void scrollFieldToCell(int cellNumber);

        void hideKeyboard();

        void activateActionMode();

        void updateActivatedLetterSequence(String letterSequence);

        void deactivateActionMode();

        void updateScore(int score);

        void showMessage(MessageType message);

        void showScoreAnimation(int deltaScore);

        void updateUsedWords(List<String> listOfWords);

        void showGameResult(int score);

        void showGameExit();

    }

    interface CellView {

        void showLetter(char letter);

        void showEnteredLetter(char letter);

        void select();

        void activate(int number);

        void showClearLetterButton();

        void hideClearLetterButton();

        void resetState();

    }

    interface Presenter extends BasePresenter {

        void setView(@NonNull View view);

        void resetView();

        void bindCell(@NonNull CellView cellView, int cellNumber);

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

    enum MessageType {
        INCORRECT_SYMBOL,
        NEED_ENTER_LETTER,
        MUST_CONTAIN_NEW_LETTER,
        NO_SUCH_WORD,
        WORD_ALREADY_USED
    }
}
