package com.adlab.balda.model.field

import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.utils.HexagonUtils.hexagonBeginningCell
import com.adlab.balda.utils.HexagonUtils.hexagonCellCountBySize

class HexagonField(initWord: String, fieldSize: FieldSizeType): AbstractField(initWord, fieldSize) {

    override val items: CharArray = CharArray(hexagonCellCountBySize(fieldSize)) {EMPTY_CELL_VALUE}
    override val fieldType: FieldType
        get() = FieldType.HEXAGON

    init {

        initWord.forEachIndexed { index, c ->
            items[beginningOfMiddleRow + index] = c
        }
    }

    override val beginningOfMiddleRow: Int
        get() = hexagonBeginningCell(fieldSize)

}