package com.adlab.balda.model.move_search;

import java.util.ArrayList;

public class Move {
    private int addedCharPos;
    private char addedChar;
    private ArrayList<Character> wordChars;

    private ArrayList<Integer> wordCharsPos;

    public Move(int addedCharPos, char addedChar, ArrayList<Character> wordChars, ArrayList<Integer> wordCharsPos) {
        this.addedCharPos = addedCharPos;
        this.addedChar = addedChar;
        this.wordChars = new ArrayList<>(wordChars);
        this.wordCharsPos = new ArrayList<>(wordCharsPos);
    }

    public ArrayList<Integer> getWordCharsPos() {
        return wordCharsPos;
    }

    public boolean contains(int position){
        return wordCharsPos.contains(position);
    }

    public boolean addChar(char letter, int position){
        if(!wordCharsPos.contains(position)) {
            wordChars.add(letter);
            wordCharsPos.add(position);
            return true;
        }
        return false;
    }

    public int getAddedCharPos() {
        return addedCharPos;
    }

    public char getAddedChar() {
        return addedChar;
    }

    public ArrayList<Character> getWordChars() {
        return wordChars;
    }

    public String getWord() {
        StringBuilder builder = new StringBuilder();
        for (Character c: wordChars) {
            builder.append(c);
        }
        return builder.toString();
    }

    public char getLastChar() {
        return wordChars.get(wordChars.size() - 1);
    }

    public int getLastCharPos() {
        return wordCharsPos.get(wordCharsPos.size() - 1);
    }

    public Move copy() {
        return new Move(this.addedCharPos, this.addedChar, this.wordChars, this.wordCharsPos);
    }
}
