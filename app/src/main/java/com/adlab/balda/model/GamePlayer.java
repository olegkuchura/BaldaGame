package com.adlab.balda.model;

import java.util.ArrayList;
import java.util.List;

public class GamePlayer {

    private int score;
    private List<String> enteredWords;

    public GamePlayer() {
        score = 0;
        enteredWords = new ArrayList<>();
    }

    public void increaseScore(int delta) {
        score += delta;
    }

    public int getScore() {
        return score;
    }

    public void addEnteredWord(String word) {
        enteredWords.add(word);
    }

    public List<String> getEnteredWords() {
        return enteredWords;
    }
}
