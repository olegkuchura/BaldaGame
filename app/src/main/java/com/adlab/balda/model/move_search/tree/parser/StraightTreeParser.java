package com.adlab.balda.model.move_search.tree.parser;

import com.adlab.balda.model.move_search.tree.model.LexicalTree;

import java.util.List;

public class StraightTreeParser {

    public LexicalTree parse(List<String> words){
        LexicalTree straightTree = new LexicalTree();

        for (String word : words){
            char path[] = new char[word.length()];
            word.getChars(0, word.length(), path, 0);
            straightTree.addAllMissingChildrenByPath(true, path);
        }

        return straightTree;
    }
}
