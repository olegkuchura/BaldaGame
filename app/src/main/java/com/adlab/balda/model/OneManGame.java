package com.adlab.balda.model;

import com.adlab.balda.database.WordsDataSource;

public class OneManGame {

    private static final char EMPTY_CELL_VALUE = ' ';

    private char[] field;
    private int rowCount;
    private int colCount;
    private String initWord;
    private GamePlayer player;

    private WordsDataSource wordsRepository;

    public OneManGame(int rowCount, int colCount, String initWord, GamePlayer player, WordsDataSource wordsRepository) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.initWord = initWord;
        this.player = player;
        this.wordsRepository = wordsRepository;
        this.field = generateInitialFiledData(rowCount, colCount, initWord);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public char[] getField() {
        char[] buffer = new char[field.length];
        for (int i = 0; i < field.length; i++) {
            buffer[i] = field[i];
        }
        return buffer;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public void makeMove(final int enteredCellNumber, final char enteredLetter, int[] cellsWithWord, final MakeMoveCallback callback) {
        final String enteredWord = getEnteredWord(enteredCellNumber, enteredLetter, cellsWithWord);

        if (initWord.equals(enteredWord)) {
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
        field[enteredCellNumber] = enteredLetter;
        return isGameFinish();
    }

    private boolean isGameFinish() {
        for (char c: field) {
            if (c == EMPTY_CELL_VALUE) {
                return false;
            }
        }
        return true;
    }

    private String getEnteredWord(int enteredCellNumber, char enteredLetter, int[] cellsWithWord) {
        StringBuilder wordBuilder = new StringBuilder();

        for (int cell : cellsWithWord) {
            if (cell == enteredCellNumber) {
                wordBuilder.append(enteredLetter);
            } else {
                wordBuilder.append(field[cell]);
            }
        }

        return wordBuilder.toString();
    }


    private char[] generateInitialFiledData(int rowCount, int colCount, String initWord) {
        char[] items = new char[rowCount * colCount];
        for (int i = 0; i < items.length; i++) {
            items[i] = EMPTY_CELL_VALUE;
        }
        int beginningOfMiddleRow = (rowCount / 2) * colCount;
        int j = beginningOfMiddleRow;
        for (int i = 0; i < initWord.length(); i++, j++) {
            items[j] = initWord.charAt(i);
        }
        return items;
    }

    public interface MakeMoveCallback {
        void makeNextMove();
        void onWordIsNotExist();
        void onWordIsAlreadyUsed();
        void onGameFinished();
    }
}
