package com.adlab.balda.model;

import android.content.Context;

import com.adlab.balda.utils.Injection;

public class GameLab {

    private static GameLab gameLab = null;
    private GameLab() {}
    public static GameLab getInstance(){
        if (gameLab == null) {
            gameLab = new GameLab();
        }
        return gameLab;
    }

    private OneManGame game;

    public OneManGame createGame(int rowCount, int colCount, String initWord, GamePlayer player, Context context) {
        game = new OneManGame(rowCount, colCount, initWord, player, Injection.provideWordsRepository(context));
        return game;
    }

    public OneManGame getGame() {
        return game;
    }

}
