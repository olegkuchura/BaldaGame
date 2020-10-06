package com.adlab.balda.model.move_search;

import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.model.move_search.tree.model.LexicalTree;
import com.adlab.balda.model.move_search.tree.model.Node;
import com.adlab.balda.utils.FieldUtilsKt;

import java.util.ArrayList;
import java.util.List;

public class MoveFinder {

    private LexicalTree straightTree;
    private LexicalTree reverseTree;
    private char[] alphabet = new char[]{'а','б','в','г','д','е','ж','з','и','й','к','л','м','н',
            'о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я'};

    public MoveFinder(LexicalTree straightTree, LexicalTree reverseTree){
        this.straightTree = straightTree;
        this.reverseTree = reverseTree;
    }

    public List<Move> findPossibleMovesForField(char[] field, FieldSizeType fieldSize, FieldType fieldType) {
        ArrayList<Move> resultMoves = new ArrayList<>();
        List<Integer> availableCells = getAvailableCellsForMove(field, fieldSize, fieldType);
        for (Integer cellNumber : availableCells){
            ArrayList<WordStartOption> startOptions = new ArrayList<>();
            findOptions(cellNumber, field, fieldSize, fieldType, new WordStartOption(), startOptions);
            for (int i = 0; i < alphabet.length; i++) {
                ArrayList<WordStartOption> availableWordStarts =
                        findAvailableWordStarts(cellNumber, alphabet[i], startOptions, reverseTree);
                ArrayList<Move> moves = findAvailableMoves(cellNumber, field, fieldSize, fieldType,
                        availableWordStarts, straightTree);
                resultMoves.addAll(moves);
            }
        }

        return resultMoves;
    }

//    public List<WordStartOption> findPossibleMovesForFieldByCell(char[] field, int rowCount, int columnCount,
//                                                                 int cellNumber) {
//        ArrayList<Move> resultMoves = new ArrayList<>();
//        ArrayList<WordStartOption> resultOptions = new ArrayList<>();
//        ArrayList<WordStartOption> startOptions = new ArrayList<>();
//        findOptions(cellNumber, field, rowCount, columnCount, new WordStartOption(), startOptions);
//        resultOptions.addAll(startOptions);
//        return resultOptions;
//    }
//
//    public List<WordStartOption> findPossibleMovesForFieldByCellAndChar(char[] field, int rowCount, int columnCount,
//                                                                 int cellNumber, char character) {
//        ArrayList<Move> resultMoves = new ArrayList<>();
//        ArrayList<WordStartOption> startOptions = new ArrayList<>();
//
//        findOptions(cellNumber, field, rowCount, columnCount, new WordStartOption(), startOptions);
//
//
//        ArrayList<WordStartOption> availableWordStarts =
//                findAvailableWordStarts(cellNumber, character, startOptions, reverseTree);
//
//        return availableWordStarts;
//    }

    private List<Integer> getAvailableCellsForMove(char[] field, FieldSizeType fieldSize, FieldType fieldType) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int cellNumber = 0; cellNumber < field.length; cellNumber++) {
            if (!Character.isLetter(field[cellNumber])) {
                List<Integer> adjacentCells = FieldUtilsKt.findAdjacentCells(cellNumber, fieldSize, fieldType);
                for (Integer nextAdjacentCell: adjacentCells) {
                    if (Character.isLetter(field[nextAdjacentCell])) {
                        result.add(cellNumber);
                        break;
                    }
                }
            }
        }
        return result;
    }

    private ArrayList<WordStartOption> findOptions(int cellNumber, char[] field, FieldSizeType fieldSize, FieldType fieldType,
                                                   WordStartOption currentWordStart,
                                                   ArrayList<WordStartOption> resultOptions){

        resultOptions.add(currentWordStart);
        List<Integer> adjacentCells = FieldUtilsKt.findAdjacentCells(cellNumber, fieldSize, fieldType);
        for (Integer nextCellNumber: adjacentCells) {
            if (Character.isLetter(field[nextCellNumber])) {
                callFindOption(nextCellNumber, field, fieldSize, fieldType, currentWordStart, resultOptions);
            }
        }

        return resultOptions;
    }

    private void callFindOption(int nextCellNumber, char[] field, FieldSizeType fieldSize, FieldType fieldType, WordStartOption currentWordStart, ArrayList<WordStartOption> resultOptions){
        if (!currentWordStart.contains(nextCellNumber)) {
            WordStartOption copy = currentWordStart.copy();
            copy.addLetter(field[nextCellNumber], nextCellNumber);
            findOptions(nextCellNumber, field, fieldSize, fieldType, copy, resultOptions);
        }
    }

    private ArrayList<WordStartOption> findAvailableWordStarts(int currentPos, char newLetter,
                                                               ArrayList<WordStartOption> startOptions,
                                                               LexicalTree reverseTree){
        ArrayList<WordStartOption> result = new ArrayList<>();
        for (WordStartOption option : startOptions){
            ArrayList<Character> wordStart = new ArrayList<>();
            wordStart.addAll(option.getWordStart());
            wordStart.add(0, newLetter);
            Node res = reverseTree.getChildByPath(wordStart.toArray(new Character[wordStart.size()]));
            if(res != null && res.isWord()){
                WordStartOption availableOption = new WordStartOption();
                availableOption.addAllInReverseOrder(option.getWordStart(), option.getWordStartPos());
                availableOption.addLetter(newLetter, currentPos);
                result.add(availableOption);
            }
        }

        //todo remove if it is unnecessary
//        Node res = reverseTree.getChildByPath(new char[]{newLetter});
//        if(res != null && res.isWord()){
//            WordStartOption availableOption = new WordStartOption();
//            availableOption.addLetter(newLetter, currentPos);
//            result.add(availableOption);
//        }

        return result;
    }

    private ArrayList<Move> findAvailableMoves(int cellNumber, char[] field, FieldSizeType fieldSize, FieldType fieldType,
                                               ArrayList<WordStartOption> availableWordStarts,
                                               LexicalTree straightTree){
        ArrayList<Move> resultList = new ArrayList<>();
        for (WordStartOption option : availableWordStarts){
            Move possibleMove = new Move(cellNumber, option.getWordStart().get(option.length() - 1),
                    option.getWordStart(), option.getWordStartPos());
            findMove( field, fieldSize, fieldType, possibleMove, straightTree, resultList);

        }
        return resultList;
    }

    private void findMove(char[] field, FieldSizeType fieldSize, FieldType fieldType,
                          Move possibleMove, LexicalTree straightTree, ArrayList<Move> resultList){
        ArrayList<Character> word = possibleMove.getWordChars();
        Node node = straightTree.getChildByPath(word.toArray(new Character[word.size()]));
        if (node != null && node.isWord()) {
            resultList.add(possibleMove);
        }
        if (node == null || node.getChildren().isEmpty()) {
            return;
        }

        int cellNumber = possibleMove.getLastCharPos();

        List<Integer> adjacentCells = FieldUtilsKt.findAdjacentCells(cellNumber, fieldSize, fieldType);
        for (Integer nextCellNumber: adjacentCells) {
            if (Character.isLetter(field[nextCellNumber])) {
                callFindMove(nextCellNumber, field, fieldSize, fieldType, possibleMove, straightTree, resultList);
            }
        }
    }

    private void callFindMove(int nextCellNumber, char[] field, FieldSizeType fieldSize, FieldType fieldType,
                              Move possibleMove, LexicalTree straightTree, ArrayList<Move> resultList) {
        if(!possibleMove.contains(nextCellNumber)) {
            Move nextMove = possibleMove.copy();
            nextMove.addChar(field[nextCellNumber], nextCellNumber);
            findMove(field, fieldSize, fieldType, nextMove, straightTree, resultList);
        }
    }


    private int getOneMensionIndex(int row, int column, int columnCount){
        return row*columnCount+column;
    }
}