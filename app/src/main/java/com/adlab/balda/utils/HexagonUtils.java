package com.adlab.balda.utils;

import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;

public class HexagonUtils {

    public static LineType lineTypeByCellNumber(int cellNumber, int columnCount) {
        if(cellNumber % (columnCount * 2 - 1) >= columnCount) return LineType.SHORT;
        else return LineType.LONG;
    }

    public static float coeffByColumnCount(int columnCount) {
        switch (columnCount) {
            case 3: return 2.3f;
            case 5: return 1.9f;
            case 7: return 1.7f;
            case 9: return 1.7f;
        }
        return 0;
    }

    public static int hexagonCellCountBySize(FieldSizeType fieldSize) {
        //TODO calculate hexagonCellCountBySize
        switch (fieldSize.stringValue) {
            case "Small": return 8;
            case "Medium": return 23;
            case "Large": return 46;
            case "Extra Large": return 77;
            default:
                throw new IllegalStateException("Unexpected value: " + fieldSize);
        }
    }

    public static int hexagonBeginningCell(FieldSizeType fieldSize) {
        //TODO calculate hexagonBeginningCell
        switch (fieldSize.stringValue) {
            case "Small": return 3;
            case "Medium": return 9;
            case "Large": return 20;
            case "Extra Large": return 34;
            default:
                throw new IllegalStateException("Unexpected value: " + fieldSize);
        }
    }

    public enum LineType {
        LONG,
        SHORT
    }
}
