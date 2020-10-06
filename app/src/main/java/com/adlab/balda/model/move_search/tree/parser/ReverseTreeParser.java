package com.adlab.balda.model.move_search.tree.parser;

import com.adlab.balda.model.move_search.tree.model.LexicalTree;

import java.util.Arrays;
import java.util.List;

public class ReverseTreeParser {

    public LexicalTree parse(List<String> words) {
        LexicalTree reverseTree = new LexicalTree();

        for (String word : words) {
            word = new StringBuilder(word).reverse().toString();
            char[] path = new char[word.length()];
            word.getChars(0, word.length(), path, 0);
            for (int i = word.length(); i > 0; i--) {
                reverseTree.addAllMissingChildrenByPath(true, path);
                path = Arrays.copyOfRange(path, 1, path.length);
            }

        }

        return reverseTree;
    }
}
