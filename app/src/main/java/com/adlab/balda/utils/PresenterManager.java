package com.adlab.balda.utils;

import android.content.Context;

import com.adlab.balda.App;
import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.contracts.MainScreenContract;
import com.adlab.balda.contracts.MultiplayerGameContract;
import com.adlab.balda.database.AppDatabase;
import com.adlab.balda.database.AppRepository;
import com.adlab.balda.database.AppRepositoryImpl;
import com.adlab.balda.presenters.GamePresenter;
import com.adlab.balda.presenters.GameSettingsPresenter;
import com.adlab.balda.presenters.MainScreenPresenter;
import com.adlab.balda.presenters.MultiplayerGamePresenter;

import androidx.annotation.NonNull;

public class PresenterManager {

    private static MainScreenContract.Presenter mMainScreenPresenter;
    private static GameSettingsContract.Presenter mGameSettingsPresenter;
    private static GameContract.Presenter mGamePresenter;
    private static MultiplayerGameContract.Presenter mMultiplayerGamePresenter;

    public static void provideMainScreenPresenter(@NonNull MainScreenContract.View view) {
        if (mMainScreenPresenter == null) {
            AppDatabase database = AppDatabase.getInstance(App.appContext);
            AppRepository repo = new AppRepositoryImpl(database);
            WordsLoader wordsLoader = new WordsLoader(App.appContext);
            mMainScreenPresenter = new MainScreenPresenter(view, repo, wordsLoader);
        } else {
            mMainScreenPresenter.setView(view);
        }
    }

    public static void resetMainScreenPresenter() {
        mMainScreenPresenter = null;
    }

    public static void provideGameSettingsPresenter(@NonNull GameSettingsContract.View view) {
        if (mGameSettingsPresenter == null) {
            mGameSettingsPresenter = new GameSettingsPresenter(
                    view,
                    new AppRepositoryImpl(AppDatabase.getInstance(App.appContext)));
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
