package com.adlab.balda.model;

import android.content.Context;

import com.adlab.balda.database.BaldaDataBase;

public class Game {
    public static final char EMPTY_VALUE = ' ';

    private char[] field;
    private String initWord;
    private int currentPlayer;
    private Player[] players;
    private boolean isRun;

    private Context context;

    private BaldaDataBase db;

    public Game(int rowCount, int colCount, String initWord, Player[] players, Context context) {
        this.field = getInitialFiledData(rowCount, colCount, initWord);
        this.initWord = initWord;
        this.players = players;
        currentPlayer = 0;
        isRun = false;
        this.context = context;
        db = new BaldaDataBase(context);
    }

    public void setPlayer(int playerPos, Player player) {
        if (playerPos < this.players.length && playerPos >= 0) {
            players[playerPos] = player;
        }
        if (isRun && checkPlayers()) {
            makeMove();
        }
    }

    public Player getPlayer(int playerPos) {
        return players[playerPos];
    }

    public char[] getField() {
        char[] buffer = new char[field.length];
        for (int i = 0; i < field.length; i++) {
            buffer[i] = field[i];
        }
        return buffer;
    }

    public void start() {
        isRun = true;
        if (checkPlayers()) {
            makeMove();
        }
    }

    /**
     *
     * @param cellNumber
     * @param letter
     * @param cellsWithWord
     * @return 0 - correct word; -1 - word isn't found in DB; -2 - word have already been used
     */
    public int finishMove(int cellNumber, char letter, int[] cellsWithWord) {
        StringBuilder wordBuilder = new StringBuilder();

        for (int cell : cellsWithWord) {
            if (cell == cellNumber) {
                wordBuilder.append(letter);
            } else {
                wordBuilder.append(field[cell]);
            }
        }

        String word = wordBuilder.toString();

        switch (checkWord(word)) {
            case 0:
                players[currentPlayer].increaseScore(cellsWithWord.length);
                players[currentPlayer].addEnteredWord(word);
                field[cellNumber] = letter;
                if (isGameFinish()) {
                    finishGame();
                } else {
                    nextPlayer();
                    makeMove();
                }
                nextPlayer();
                makeMove();
                return 0;
            case -1:
                return -1;
            case -2:
                return -2;
            default: return 0;
        }

    }

    private void makeMove() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                players[currentPlayer].makeMove();
            }
        }).start();
    }


    private void finishGame() {
        int maxScore = -1;
        for (Player p: players) {
            if (p.getScore() > maxScore) {
                maxScore = p.getScore();
            }
        }

        for (Player p: players) {
            if (p.getScore() == maxScore) {
                p.finishGame(true);
            } else {
                p.finishGame(false);
            }
        }
    }



    private boolean isGameFinish() {
        for (char c: field) {
            if (c == EMPTY_VALUE) {
                return false;
            }
        }
        return true;
    }

    private char[] getInitialFiledData(int rowCount, int spanCount, String initWord) {
        char[] items = new char[rowCount * spanCount];
        for (int i = 0; i < items.length; i++) {
            items[i] = ' ';
        }
        int beginningOfMiddleRow = (rowCount / 2) * spanCount;
        int j = beginningOfMiddleRow;
        for (int i = 0; i < initWord.length(); i++, j++) {
            items[j] = initWord.charAt(i);
        }
        return items;
    }

    private void nextPlayer() {
        if (currentPlayer == players.length - 1) {
            currentPlayer = 0;
        } else {
            currentPlayer++;
        }
    }

    private int checkWord(String word) {
        if (!db.isWordExist(word)) {
            return -1;
        }
        if (initWord.equals(word)) {
            return -2;
        }
        for (Player p: players) {
            for (String w: p.getEnteredWords()) {
                if (w.equals(word)) {
                    return -2;
                }
            }
        }
        return 0;
    }

    private boolean checkPlayers() {
        for (Player p : players) {
            if (p == null) {
                return false;
            }
        }
        return true;
    }
}
