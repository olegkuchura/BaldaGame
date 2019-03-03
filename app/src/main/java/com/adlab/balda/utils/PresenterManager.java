package com.adlab.balda.utils;

import android.content.Context;

import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.database.BaldaDataBase;
import com.adlab.balda.presenters.GameSettingsPresenter;

import androidx.annotation.NonNull;

public class PresenterManager {

    private static GameSettingsContract.Presenter mGameSettingsPresenter;

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

}
