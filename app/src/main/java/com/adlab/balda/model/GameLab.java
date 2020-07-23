package com.adlab.balda.model;

import android.content.Context;

import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
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

    public OneManGame createGame(String initWord, FieldSizeType fieldSize, FieldType fieldType, GamePlayer player, Context context) {
        game = new OneManGame(initWord, fieldSize, fieldType, player, Injection.provideWordsRepository(context));
        return game;
    }

    public OneManGame getGame() {
        return game;
    }

}
