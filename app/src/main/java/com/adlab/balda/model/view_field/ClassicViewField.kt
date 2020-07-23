package com.adlab.balda.model.view_field

import com.adlab.balda.enums.FieldSizeType

class ClassicViewField(items: CharArray, fieldSize: FieldSizeType) : AbstractViewField(items, fieldSize) {

    private val mColCount: Int
        get() = fieldSize.intValue()
    private val mRowCount: Int
        get() = fieldSize.intValue()

    override fun trySetSelectedCellNumber(cellNumber: Int): Boolean =
            if (isCellAvailableForEntering(cellNumber)) {
                selectedCellNumber = cellNumber
                true
            } else {
                selectedCellNumber = UNDEFINED_CELL_NUMBER
                false
            }

    override fun toggleActiveModeForCell(cellNumber: Int): List<Int> {
        val result = mutableListOf<Int>()
        if (items[cellNumber] == EMPTY_CELL_VALUE && cellNumber != enteredCellNumber) {
            return result
        }

        val index = activeCellNumbers.indexOf(cellNumber)
        if (index != -1) {
            while (activeCellNumbers.size != index) {
                val removedCellNumber: Int = activeCellNumbers.removeLast()
                result.add(removedCellNumber)
            }
            return result
        }

        if (activeCellNumbers.isEmpty()) {
            activeCellNumbers.addLast(cellNumber)
            result.add(cellNumber)
            return result
        }

        val rowOfNew = cellNumber / mColCount
        val colOfNew = cellNumber % mColCount
        val lastActivatedViewPosition: Int = activeCellNumbers.last
        val rowOfLast = lastActivatedViewPosition / mColCount
        val colOfLast = lastActivatedViewPosition % mColCount

        if (rowOfNew == rowOfLast && (colOfNew == colOfLast + 1 || colOfNew == colOfLast - 1) ||
                colOfNew == colOfLast && (rowOfNew == rowOfLast + 1 || rowOfNew == rowOfLast - 1)) {
            activeCellNumbers.addLast(cellNumber)
            result.add(cellNumber)
        }
        return result
    }

    private fun isCellAvailableForEntering(cellNumber: Int): Boolean {
        if (cellNumber == UNDEFINED_CELL_NUMBER || items[cellNumber].isLetter()) {
            return false
        }
        val row: Int = cellNumber / mColCount
        val col: Int = cellNumber % mColCount
        if (row > 0 && items[(row - 1) * mColCount + col].isLetter()) {
            return true
        }
        if (col < mColCount - 1 && items[row * mColCount + col + 1].isLetter()) {
            return true
        }
        if (row < mRowCount - 1 && items[(row + 1) * mColCount + col].isLetter()) {
            return true
        }
        if (col > 0 && items[row * mColCount + col - 1].isLetter()) {
            return true
        }
        return false
    }

}