package com.adlab.balda.utils

import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType

fun findAdjacentCells(position: Int, fieldSize: FieldSizeType, fieldType: FieldType)
        = ArrayList<Int>().apply {
    when(fieldType) {
        FieldType.SQUARE -> findAdjacentCellsSquare(position, fieldSize, this)
        FieldType.HEXAGON -> findAdjacentCellsHexagon(position, fieldSize, this)
    }
}

private fun findAdjacentCellsSquare(position: Int, fieldSize: FieldSizeType, list: MutableList<Int>) {
    val columnCount = fieldSize.intValue()
    val rowCount = fieldSize.intValue()
    val row = position / fieldSize.intValue()
    val col = position % fieldSize.intValue()

    if (row > 0) { list.add((row - 1) * columnCount + col) }
    if (col < columnCount - 1) { list.add(row * columnCount + col + 1) }
    if (row < rowCount - 1) { list.add((row + 1) * columnCount + col) }
    if (col > 0) { list.add(row * columnCount + col - 1) }
}

private fun findAdjacentCellsHexagon(position: Int, fieldSize: FieldSizeType, list: MutableList<Int>) {
    val colCount = fieldSize.intValue()
    val pos = position % (colCount * 2 - 1)

    if (position > (colCount - 1) && pos != 0) { list.add(position - colCount) }
    if (position > (colCount - 1) && pos != colCount - 1) { list.add(position - (colCount - 1)) }
    if (pos != (colCount - 1) && pos != (colCount * 2 - 2)) { list.add(position + 1) }
    if (position < HexagonUtils.hexagonCellCountBySize(fieldSize) - colCount && pos != colCount - 1) { list.add(position + colCount) }
    if (position < HexagonUtils.hexagonCellCountBySize(fieldSize) - colCount && pos != 0) { list.add(position + (colCount - 1)) }
    if (pos != 0 && pos != colCount) { list.add(position - 1) }
}