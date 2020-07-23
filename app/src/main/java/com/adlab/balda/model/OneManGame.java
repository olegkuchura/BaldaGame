package com.adlab.balda.model;

import com.adlab.balda.database.WordsDataSource;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.model.field.AbstractField;
import com.adlab.balda.model.field.ClassicField;
import com.adlab.balda.model.field.HexagonField;

import java.util.ArrayList;
import java.util.List;

public class OneManGame {

    private static final char EMPTY_CELL_VALUE = ' ';

    //private char[] field;
    //private FieldType fieldType;
    private AbstractField field;
    private int rowCount;
    private int colCount;
    //private String initWord;
    private GamePlayer player;

    private WordsDataSource wordsRepository;

    public OneManGame(String initWord, FieldSizeType fieldSize, FieldType fieldType, GamePlayer player, WordsDataSource wordsRepository) {
        this.rowCount = fieldSize.intValue();
        this.colCount = fieldSize.intValue();
        //this.initWord = initWord;
        //this.fieldType = fieldType;
        this.player = player;
        this.wordsRepository = wordsRepository;
        if (fieldType == FieldType.SQUARE) {
            field = new ClassicField(initWord, fieldSize);
        } else {
            field = new HexagonField(initWord, fieldSize);
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public char[] getCopyOfCells() {
        return field.getCopyOfCells();
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public List<String> getUsedWords() {
        ArrayList<String> list = new ArrayList<>();
        list.add(field.getInitWord());
        list.addAll(player.getEnteredWords());
        return list;
    }

    public void makeMove(final int enteredCellNumber, final char enteredLetter, int[] cellsWithWord, final MakeMoveCallback callback) {
        final String enteredWord = field.getEnteredWord(enteredCellNumber, enteredLetter, cellsWithWord);

        if (field.getInitWord().equals(enteredWord)) {
            callback.onWordIsAlreadyUsed();
            return;
        }

        for (String word : player.getEnteredWords()) {
            if (word.equals(enteredWord)) {
                callback.onWordIsAlreadyUsed();
                return;
            }
        }

        wordsRepository.isWordExist(enteredWord, new WordsDataSource.CheckWordCallback() {
            @Override
            public void onWordChecked(boolean isWordExist) {
                if (isWordExist) {
                    boolean isGameFinished = applyMove(enteredCellNumber, enteredLetter, enteredWord);
                    if (isGameFinished) {
                        callback.onGameFinished();
                    } else {
                        callback.makeNextMove();
                    }
                } else {
                    callback.onWordIsNotExist();
                }
            }
        });
    }

    // return true if game is finished and false otherwise
    private boolean applyMove(int enteredCellNumber, char enteredLetter, String enteredWord) {
        player.increaseScore(enteredWord.length());
        player.addEnteredWord(enteredWord);
        field.setLetter(enteredCellNumber, enteredLetter);
        return isGameFinish();
    }

    private boolean isGameFinish() {
        return field.isFieldFull();
    }

    public FieldType getFieldType() {
        return field.getFieldType();
    }

    public FieldSizeType getFieldSize() {
        return field.getFieldSize();
    }

    public interface MakeMoveCallback {
        void makeNextMove();
        void onWordIsNotExist();
        void onWordIsAlreadyUsed();
        void onGameFinished();
    }
}
