package com.adlab.balda.model;

import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.model.field.AbstractField;
import com.adlab.balda.model.field.ClassicField;
import com.adlab.balda.model.field.HexagonField;
import com.adlab.balda.utils.Injection;

import java.util.List;

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
    private MultiplayerGame multiGame;

    public OneManGame createGame(String initWord, FieldSizeType fieldSize, FieldType fieldType, GamePlayer player) {
        AbstractField field;
        if (fieldType == FieldType.SQUARE) {
            field = new ClassicField(initWord, fieldSize);
        } else {
            field = new HexagonField(initWord, fieldSize);
        }
        game = new OneManGame(field, player, Injection.provideAppRepository());
        return game;
    }

    public MultiplayerGame createMultiplayerGame(
            String initWord, FieldSizeType fieldSize, FieldType fieldType, List<GamePlayer> players) {
        AbstractField field;
        if (fieldType == FieldType.SQUARE) {
            field = new ClassicField(initWord, fieldSize);
        } else {
            field = new HexagonField(initWord, fieldSize);
        }
        multiGame = new MultiplayerGame(field, players, Injection.provideAppRepository());
        return multiGame;
    }

    public OneManGame getGame() {
        return game;
    }

    public MultiplayerGame getMultiplayerGame() {
        return multiGame;
    }

}
