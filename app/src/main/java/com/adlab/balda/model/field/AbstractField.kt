package com.adlab.balda.model.field

import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType

abstract class AbstractField (
        val initWord: String,
        val fieldSize: FieldSizeType
) {
    protected val EMPTY_CELL_VALUE = ' '

    abstract val items: CharArray

    abstract val fieldType: FieldType

    abstract val beginningOfMiddleRow: Int

    fun getCopyOfCells(): CharArray {
        val buffer = CharArray(items.size)
        items.forEachIndexed { index, c ->
            buffer[index] = c
        }
        return buffer
    }

    fun getEnteredWord(enteredCellNumber: Int, enteredLetter: Char, cellsWithWord: IntArray): String {
        val wordBuilder = StringBuilder()
        for (cell in cellsWithWord) {
            if (cell == enteredCellNumber) {
                wordBuilder.append(enteredLetter)
            } else {
                wordBuilder.append(items[cell])
            }
        }
        return wordBuilder.toString()
    }

    fun setLetter(cellNumber: Int, letter: Char) {
        items[cellNumber] = letter
    }

    val isFieldFull: Boolean
        get() = !items.contains(EMPTY_CELL_VALUE)

}