package com.adlab.balda.model;

import java.util.List;

public interface Player {
    void makeMove();
    void finishGame(boolean isWinner);
    void increaseScore(int delta);
    int getScore();
    void addEnteredWord(String word);
    List<String> getEnteredWords();
    char [] getActualField();
}
