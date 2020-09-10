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
    private MultiplePlayersGame multiGame;

    public OneManGame createGame(String initWord, FieldSizeType fieldSize, FieldType fieldType, GamePlayer player) {
        game = new OneManGame(initWord, fieldSize, fieldType, player, Injection.provideWordsRepository());
        return game;
    }

    public MultiplePlayersGame createMultiplayerGame(
            String initWord, FieldSizeType fieldSize, FieldType fieldType, List<GamePlayer> players) {
        AbstractField field;
        if (fieldType == FieldType.SQUARE) {
            field = new ClassicField(initWord, fieldSize);
        } else {
            field = new HexagonField(initWord, fieldSize);
        }
        multiGame = new MultiplePlayersGame(field, players, Injection.provideWordsRepository());
        return multiGame;
    }

    public OneManGame getGame() {
        return game;
    }

    public MultiplePlayersGame getMultiplayerGame() {
        return multiGame;
    }

}
