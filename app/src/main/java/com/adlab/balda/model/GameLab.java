package com.adlab.balda.model;

import android.content.Context;

public class GameLab {

    private static GameLab gameLab = null;
    private GameLab() {}
    public static GameLab getInstance(){
        if (gameLab == null) {
            gameLab = new GameLab();
        }
        return gameLab;
    }

    private Game game;

    public Game createGame(int rowCount, int colCount, String initWord, Player[] players, Context context) {
        game = new Game(rowCount, colCount, initWord, players, context);
        return game;
    }

    public Game getGame() {
        return game;
    }

}
