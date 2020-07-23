package com.adlab.balda.model.view_field

import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.utils.HexagonUtils.hexagonCellCountBySize

class HexagonViewField(items: CharArray, fieldSize: FieldSizeType): AbstractViewField(items, fieldSize) {

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

        val nextByLast = getNearestCells(activeCellNumbers.last)
        if (nextByLast.contains(cellNumber)) {
            activeCellNumbers.addLast(cellNumber)
            result.add(cellNumber)
        }
        return result
    }

    private fun getNearestCells(cellNumber: Int): List<Int> = mutableListOf<Int>().apply {
        val colCount = fieldSize.intValue()
        val pos = cellNumber % (colCount * 2 - 1)
        if (cellNumber > (colCount - 1) && pos != 0) {
            add(cellNumber - colCount)
        }
        if (cellNumber > (colCount - 1) && pos != colCount - 1) {
            add(cellNumber - (colCount - 1))
        }
        if (pos != (colCount - 1) && pos != (colCount * 2 - 2)) {
            add(cellNumber + 1)
        }
        if (cellNumber < hexagonCellCountBySize(fieldSize) - colCount && pos != colCount - 1) {
            add(cellNumber + colCount)
        }
        if (cellNumber < hexagonCellCountBySize(fieldSize) - colCount && pos != 0) {
            add(cellNumber + (colCount - 1))
        }
        if (pos != 0 && pos != colCount) {
            add(cellNumber - 1)
        }
    }


    private fun isCellAvailableForEntering(cellNumber: Int): Boolean {
        if (cellNumber == UNDEFINED_CELL_NUMBER || items[cellNumber].isLetter()) {
            return false
        }

        getNearestCells(cellNumber).forEach {
            if (items[it].isLetter())
                return true
        }
        return false
    }

}