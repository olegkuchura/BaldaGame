package com.adlab.balda.activities;

import java.util.LinkedList;
import java.util.List;

public class Field {
    public static final char EMPTY_VALUE = ' ';

    private int rowCount;
    private int colCount;

    private char[] items;
    private int selectedViewNumber = -1;
    private int enteredViewNumber = -1;
    private char enteredLetter = EMPTY_VALUE;
    private LinkedList<Integer> activeViewNumbers;

    public Field(char[] items, int rowCount, int colCount) {
        this(items, rowCount, colCount,
                -1, -1, EMPTY_VALUE, new LinkedList<Integer>());
    }

    public Field(char[] items, int rowCount, int colCount,
                 int selectedViewNumber, int enteredViewNumber, char enteredLetter, LinkedList<Integer> activeViewNumbers) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.items = items;
        this.selectedViewNumber = selectedViewNumber;
        this.enteredViewNumber = enteredViewNumber;
        this.enteredLetter = enteredLetter;
        this.activeViewNumbers = activeViewNumbers;
    }

    public int getSelectedViewNumber() {
        return selectedViewNumber;
    }

    public int getEnteredViewNumber() {
        return enteredViewNumber;
    }

    public char getEnteredLetter() {
        return enteredLetter;
    }

    public boolean isEnteredLetter(){
        if (enteredLetter != EMPTY_VALUE) {
            return true;
        } else {
            return false;
        }
    }

    public char getLetter(int position) {
        return items[position];
    }

    public int getCellCount() {
        return items.length;
    }

    public int getActiveCellNumber(int position) {
        return activeViewNumbers.indexOf(position);
    }

    public int[] getActiveViewNumbers() {
        int[] result = new int[activeViewNumbers.size()];
        int i = 0;

        for (int el : activeViewNumbers){
            result[i] = el;
            i++;
        }
        return result;
    }

    public boolean isEnteredLetterInsideActiveWord(){
        return activeViewNumbers.contains(enteredViewNumber);
    }

    /**
     *
     * @param viewPosition
     * @return true if there still are elements in activeViewNumbers list, false otherwise
     */
    public boolean toggleActiveModeForView(int viewPosition) {
        int index;
        if((index = activeViewNumbers.indexOf(viewPosition)) != -1){
            while (activeViewNumbers.size() != index) {
                activeViewNumbers.removeLast();
            }
            return !activeViewNumbers.isEmpty();
        }

        if (items[viewPosition] == EMPTY_VALUE && viewPosition != enteredViewNumber){
            return !activeViewNumbers.isEmpty();
        }
        if(activeViewNumbers.isEmpty()){
            activeViewNumbers.addLast(viewPosition);
            return true;
        }

        int rowOfNew = viewPosition/colCount;
        int colOfNew = viewPosition%colCount;
        int lastActivatedViewPosition = activeViewNumbers.getLast();
        int rowOfLast = lastActivatedViewPosition/colCount;
        int colOfLast = lastActivatedViewPosition%colCount;

        if(
                ((rowOfNew == rowOfLast) && ((colOfNew == (colOfLast + 1))||(colOfNew == (colOfLast - 1)))) ||
                        ((colOfNew == colOfLast) && ((rowOfNew == (rowOfLast + 1))||(rowOfNew == (rowOfLast - 1))))
                ) {
            activeViewNumbers.addLast(viewPosition);
            return true;
        }

        return !activeViewNumbers.isEmpty();
    }

    public void finishActiveMode(){
        while (!activeViewNumbers.isEmpty()) {
            int i = activeViewNumbers.removeLast();
        }
    }

    public void resetState(char[] field){
        items = field;
        selectedViewNumber = -1;
        enteredViewNumber = -1;
        enteredLetter = EMPTY_VALUE;
        activeViewNumbers.clear();
    }

    public String getEnteredLetterSequence(){
        StringBuilder letterSequence = new StringBuilder();
        for (Integer i : activeViewNumbers){
            if (i == enteredViewNumber) {
                letterSequence.append(enteredLetter);
            } else {
                letterSequence.append(items[i]);
            }
        }
        return letterSequence.toString();
    }

    public boolean setSelectedViewNumber(int viewNumber) {
        if (isCellAvailableForEntering(viewNumber)) {
            selectedViewNumber = viewNumber;
            return true;
        } else {
            selectedViewNumber = -1;
            return false;
        }
    }

    public void removeSelection() {
        setSelectedViewNumber(-1);
    }

    public void setSelectedItem(char letter) {
        if (selectedViewNumber >= 0) {
            enteredLetter = letter;
            enteredViewNumber = selectedViewNumber;
        }
    }

    private boolean isCellAvailableForEntering(int cellNumber) {
        if(cellNumber == -1 || Character.isLetter(items[cellNumber])){
            return false;
        }
        int row = cellNumber/colCount;
        int col = cellNumber%colCount;
        if(row > 0 && Character.isLetter(items[(row-1)*colCount + col])){
            return true;
        }
        if(col < colCount - 1 && Character.isLetter(items[row*colCount + col + 1])){
            return true;
        }
        if(row < rowCount - 1 && Character.isLetter(items[(row+1)*colCount + col])){
            return true;
        }
        if(col > 0 && Character.isLetter(items[row*colCount + col - 1])){
            return true;
        }
        return false;
    }
}
