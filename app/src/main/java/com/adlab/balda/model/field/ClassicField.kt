package com.adlab.balda.model.field

import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType

class ClassicField(initWord: String, fieldSize: FieldSizeType) : AbstractField(initWord, fieldSize) {

    private val rowCount: Int = fieldSize.intValue()
    private val colCount: Int = fieldSize.intValue()

    override val items: CharArray = CharArray(fieldSize.intValue() * fieldSize.intValue()) { EMPTY_CELL_VALUE }
    override val fieldType: FieldType
        get() = FieldType.SQUARE

    init {
        initWord.forEachIndexed { index, c ->
            items[beginningOfMiddleRow + index] = c
        }
    }

    override val beginningOfMiddleRow: Int
        get() = (rowCount / 2) * colCount

}