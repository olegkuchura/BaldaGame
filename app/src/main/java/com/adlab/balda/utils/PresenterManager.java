package com.adlab.balda.utils;

import android.content.Context;

import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.contracts.MultiplayerGameContract;
import com.adlab.balda.presenters.GamePresenter;
import com.adlab.balda.presenters.GameSettingsPresenter;
import com.adlab.balda.presenters.MultiplayerGamePresenter;

import androidx.annotation.NonNull;

public class PresenterManager {

    private static GameSettingsContract.Presenter mGameSettingsPresenter;
    private static GameContract.Presenter mGamePresenter;
    private static MultiplayerGameContract.Presenter mMultiplayerGamePresenter;

    public static void provideGameSettingsPresenter(@NonNull Context context, @NonNull GameSettingsContract.View view) {
        if (mGameSettingsPresenter == null) {
            mGameSettingsPresenter = new GameSettingsPresenter(
                    Injection.provideWordsRepository(),
                    view);
        } else {
            mGameSettingsPresenter.setView(view);
        }
    }

    public static void resetGameSettingsPresenter() {
        mGameSettingsPresenter = null;
    }

    public static void provideGamePresenter(@NonNull GameContract.View view) {
        if (mGamePresenter == null) {
            mGamePresenter = new GamePresenter(view);
        } else {
            mGamePresenter.setView(view);
        }
    }

    public static void resetGamePresenter() {
        mGamePresenter = null;
    }

    public static void provideMultiplayerGamePresenter(@NonNull MultiplayerGameContract.View view) {
        if (mMultiplayerGamePresenter == null) {
            mMultiplayerGamePresenter = new MultiplayerGamePresenter(view);
        } else {
            mMultiplayerGamePresenter.setView(view);
        }
    }

    public static void resetMultiplayerGamePresenter() {
        mMultiplayerGamePresenter = null;
    }

}
