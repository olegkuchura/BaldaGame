package com.adlab.balda.model.move_search;

import java.util.ArrayList;

public class WordStartOption {
    private ArrayList<Character> wordStart;
    private ArrayList<Integer> wordStartPos;

    public WordStartOption() {
        this.wordStart = new ArrayList<>();
        this.wordStartPos = new ArrayList<>();
    }

    public WordStartOption copy(){
        WordStartOption copy = new WordStartOption();
        copy.wordStart.addAll(this.wordStart);
        copy.wordStartPos.addAll(this.wordStartPos);
        return copy;

    }

    public void addAllInReverseOrder(ArrayList<Character> wordStart, ArrayList<Integer> wordStartPos){
        for (int i = wordStart.size()-1; i>=0; i--){
            this.wordStart.add(wordStart.get(i));
            this.wordStartPos.add(wordStartPos.get(i));
        }
    }

    public void addLetter(Character letter, Integer position){
        wordStart.add(letter);
        wordStartPos.add(position);
    }

    public void addLetterToStart(Character letter, Integer position){
        wordStart.add(0, letter);
        wordStartPos.add(0, position);
    }

    public boolean contains(Integer position){
        return wordStartPos.contains(position);
    }

    public int length() { return wordStart.size(); }

    public ArrayList<Character> getWordStart() {
        return wordStart;
    }

    public String getWordStartAsString() {
        StringBuilder builder = new StringBuilder();
        for (Character c: wordStart) {
            builder.append(c);
        }
        return builder.toString();
    }

    public ArrayList<Integer> getWordStartPos() {
        return wordStartPos;
    }
}
