package com.adlab.balda.contracts;

import com.adlab.balda.BasePresenter;
import com.adlab.balda.BaseView;

import androidx.annotation.NonNull;

public interface GameSettingsContract {

    interface View extends BaseView<Presenter> {

        void setInitWord(String word);

        void showNonExistentWordError();

        void showEmptyWordError();

        void hideInitWordError();

        void setRowCount(int rowCount);

        void setColCount(int colCount);

        void setIncreaseRowCountEnabled(boolean enabled);

        void setReduceRowCountEnabled(boolean enabled);

        void setIncreaseColCountEnabled(boolean enabled);

        void setReduceColCountEnabled(boolean enabled);

        void setStartGameEnabled(boolean enabled);

        void showGameScreen(String word, int rowCount, int colCount);
    }

    interface Presenter extends BasePresenter {

        void setView(@NonNull GameSettingsContract.View view);

        void resetView();

        void initWordChanged(@NonNull String newWord);

        void generateRandomWord();

        void increaseRowCount();

        void reduceRowCount();

        void increaseColumnCount();

        void reduceColumnCount();

        void startGame();

        void destroy();

    }

}
