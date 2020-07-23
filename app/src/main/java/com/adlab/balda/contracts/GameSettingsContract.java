package com.adlab.balda.contracts;

import com.adlab.balda.BasePresenter;
import com.adlab.balda.BaseView;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;

import androidx.annotation.NonNull;

public interface GameSettingsContract {

    interface View extends BaseView<Presenter> {

        void setInitWord(String word);

        void showNonExistentWordError();

        void showEmptyWordError();

        void showNonAppropriateWordLength(int correctLength);

        void hideNonAppropriateWordLength();

        void hideInitWordError();

        void updateFiledSize(FieldSizeType sizeType, boolean withAnim, boolean biggerValue);

        void setIncreaseFieldSizeEnabled(boolean enabled);

        void setReduceFieldSizeEnabled(boolean enabled);

        void setStartGameEnabled(boolean enabled);

        void navigateToGameScreen(String word, FieldSizeType fieldSize, FieldType fieldType);
    }

    interface Presenter extends BasePresenter {

        void setView(@NonNull GameSettingsContract.View view);

        void resetView();

        void initWordChanged(@NonNull String newWord);

        void generateRandomWord();

        void increaseFieldSize();

        void reduceFieldSize();

        void fieldTypeChanged(FieldType fieldType);

        void startGame();

    }

}
