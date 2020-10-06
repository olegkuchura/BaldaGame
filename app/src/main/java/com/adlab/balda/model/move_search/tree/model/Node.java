package com.adlab.balda.model.move_search.tree.model;

import java.util.*;

public class Node {
    private char character;
    private boolean isWord;
    private Map<Character, Node> children;

    public Node(char character, boolean isWord){
        this.character = character;
        this.isWord = isWord;
        children = new HashMap<>();
    }

    public void setWord(boolean isWord) {
        this.isWord = isWord;
    }

    public boolean isWord(){
        return this.isWord;
    }

    public boolean addChild(char character, boolean isWord){
        return addChild(new Node(character, isWord));
    }

    public boolean addChild(Node newNode) {
        if (!this.children.containsKey(newNode.character)){
            this.children.put(newNode.character, newNode);
            return true;
        }
        return false;
    }

    public Node getChildByKey(char character){
        return children.get(character);
    }


    public char getCharacter() {
        return character;
    }

    public Collection<Node> getChildren(){
        return children.values();
    }
}
