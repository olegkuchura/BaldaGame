package com.adlab.balda.contracts;

import com.adlab.balda.BasePresenter;
import com.adlab.balda.BaseView;
import com.adlab.balda.adapters.PlayersAdapter;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.enums.GameType;

import androidx.annotation.NonNull;

public interface GameSettingsContract {

    interface View extends BaseView<Presenter> {

        void setInitWord(String word);

        void showNonExistentWordError();

        void showEmptyWordError();

        void showNonAppropriateWordLength(int correctLength);

        void hideNonAppropriateWordLength();

        void hideInitWordError();

        void showPlayerNamesBlock(boolean show);

        void updatePlayers(int playersCount);

        void playerAdded();

        void playerDeleted(int position);

        void setPlayerEdit(String playerName);

        void showNicknameError();

        void showPlayersCountErrorMax(int playersCountLimit);

        void showPlayersCountErrorMin(int playersCountLimit);

        void showAlreadyUsedNicknameError();

        void updateFiledSize(FieldSizeType sizeType, boolean withAnim, boolean biggerValue);

        void setIncreaseFieldSizeEnabled(boolean enabled);

        void setReduceFieldSizeEnabled(boolean enabled);

        void setStartGameEnabled(boolean enabled);

        void navigateToGameScreen(GameType gameType);
    }

    interface Presenter extends BasePresenter {

        void start(GameType gameType);

        void setView(@NonNull GameSettingsContract.View view);

        void resetView();

        void cleanup();

        void initWordChanged(@NonNull String newWord);

        void generateRandomWord();

        void bindPlayer(@NonNull PlayersAdapter.PlayerView playerView, int position);

        void onDeletePlayerClicked(int position);

        void onAddPlayerClicked(@NonNull String playerName);

        void increaseFieldSize();

        void reduceFieldSize();

        void fieldTypeChanged(FieldType fieldType);

        void startGameClicked();
    }

}
