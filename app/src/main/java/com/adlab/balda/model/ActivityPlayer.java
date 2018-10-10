package com.adlab.balda.model;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlayer implements Player {

    private ActivityForPlayer activity;
    private Game game;

    private int score;
    private List<String> enteredWords;

    public ActivityPlayer(ActivityForPlayer activity, Game game) {
        this.activity = activity;
        this.game = game;
        score = 0;
        enteredWords = new ArrayList<>();
    }

    @Override
    public void makeMove() {
        if (activity == null) {
            return;
        }
        activity.makeMove();
    }

    @Override
    public void finishGame(boolean isWinner) {
        if (activity == null) {
            return;
        }
        activity.finishGame(isWinner);
    }

    @Override
    public void increaseScore(int delta) {
        score = score + delta;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public char[] getActualField() {
        return game.getField();
    }

    @Override
    public void addEnteredWord(String word) {
        enteredWords.add(word);
    }

    @Override
    public List<String> getEnteredWords() {
        return enteredWords;
    }

    public void setActivity(ActivityForPlayer activity) {
        this.activity = activity;
    }

    public int finishMove(int cellNumber, char letter, int[] cellsWithWord){
        return game.finishMove(cellNumber, letter, cellsWithWord);
    }
}
