package com.adlab.balda.model.view_field

import com.adlab.balda.enums.FieldSizeType
import java.util.*

abstract class AbstractViewField(var items: CharArray, val fieldSize: FieldSizeType) {

    protected companion object {
        const val EMPTY_CELL_VALUE = ' '
        const val UNDEFINED_CELL_NUMBER = -1
    }

    var selectedCellNumber: Int = UNDEFINED_CELL_NUMBER
        set(value) {
            if (value >= 0 || value == UNDEFINED_CELL_NUMBER) field = value
            else throw IllegalArgumentException("property 'selectedCellNumber' cannot be less 0")
        }

    val hasSelectedCell: Boolean
        get() = selectedCellNumber != UNDEFINED_CELL_NUMBER

    var enteredCellNumber: Int = UNDEFINED_CELL_NUMBER
        set(value) {
            if (value >= 0 || value == UNDEFINED_CELL_NUMBER) field = value
            else throw IllegalArgumentException("property 'enteredCellNumber' cannot be less 0")
        }

    var enteredLetter: Char = EMPTY_CELL_VALUE

    val hasEnteredLetter: Boolean
        get() = enteredCellNumber != UNDEFINED_CELL_NUMBER

    val activeCellNumbers: LinkedList<Int> = LinkedList()

    val activeCellNumbersArray: IntArray
        get() = IntArray(activeCellNumbers.size).apply {
            activeCellNumbers.forEachIndexed { index, cellNumber ->
                this[index] = cellNumber
            }
        }

    val enteredLetterSequence: String
        get() = StringBuilder().apply {
            activeCellNumbers.forEach { append(if (it == enteredCellNumber) enteredLetter else items[it]) }
        }.toString()

    fun clearSelection(): Int {
        val oldSelectedCellNumber = selectedCellNumber
        selectedCellNumber = UNDEFINED_CELL_NUMBER
        return oldSelectedCellNumber
    }

    fun clearEnteredLetter(): Int {
        val oldEnteredCellNumber = enteredCellNumber
        enteredCellNumber = UNDEFINED_CELL_NUMBER
        enteredLetter = EMPTY_CELL_VALUE
        return oldEnteredCellNumber
    }

    fun activeCellsContainEnteredLetter(): Boolean = activeCellNumbers.contains(enteredCellNumber)

    abstract fun trySetSelectedCellNumber(cellNumber: Int): Boolean

    abstract fun toggleActiveModeForCell(cellNumber: Int): List<Int>

}