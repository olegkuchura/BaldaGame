package com.adlab.balda.test

import com.adlab.balda.utils.HexagonUtils
import com.adlab.balda.utils.HexagonUtils.LineType
import org.junit.Test

import org.junit.Assert.*

class HexagonUtilsTest {

    @Test
    fun lineTypeByCellNumber() {
        assertEquals(LineType.LONG, HexagonUtils.lineTypeByCellNumber(0, 5))
        assertEquals(LineType.LONG, HexagonUtils.lineTypeByCellNumber(4, 5))

        assertEquals(LineType.SHORT, HexagonUtils.lineTypeByCellNumber(4, 4))
        assertEquals(LineType.SHORT, HexagonUtils.lineTypeByCellNumber(5, 4))
        assertEquals(LineType.SHORT, HexagonUtils.lineTypeByCellNumber(6, 4))
        assertEquals(LineType.LONG, HexagonUtils.lineTypeByCellNumber(7, 4))
        assertEquals(LineType.LONG, HexagonUtils.lineTypeByCellNumber(10, 4))
        assertEquals(LineType.SHORT, HexagonUtils.lineTypeByCellNumber(13, 4))
    }
}