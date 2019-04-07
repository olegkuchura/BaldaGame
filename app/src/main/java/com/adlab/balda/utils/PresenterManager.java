package com.adlab.balda.utils;

import android.content.Context;

import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.database.BaldaDataBase;
import com.adlab.balda.presenters.GamePresenter;
import com.adlab.balda.presenters.GameSettingsPresenter;

import androidx.annotation.NonNull;

public class PresenterManager {

    private static GameSettingsContract.Presenter mGameSettingsPresenter;
    private static GameContract.Presenter mGamePresenter;

    public static void provideGameSettingsPresenter(@NonNull Context context, @NonNull GameSettingsContract.View view) {
        if (mGameSettingsPresenter == null) {
            mGameSettingsPresenter = new GameSettingsPresenter(
                    Injection.provideWordsRepository(context.getApplicationContext()),
                    view);
        } else {
            mGameSettingsPresenter.setView(view);
        }
    }

    public static void resetGameSettingsPresenter() {
        mGameSettingsPresenter = null;
    }

    public static void provideGamePresenter(@NonNull Context context, @NonNull GameContract.View view) {
        if (mGamePresenter == null) {
            mGamePresenter = new GamePresenter(
                    Injection.provideWordsRepository(context.getApplicationContext()),
                    view);
        } else {
            mGamePresenter.setView(view);
        }
    }

    public static void resetGamePresenter() {
        mGamePresenter = null;
    }

}
