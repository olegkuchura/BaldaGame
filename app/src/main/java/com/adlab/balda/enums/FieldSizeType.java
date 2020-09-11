package com.adlab.balda.enums;

public enum FieldSizeType {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE;

    public FieldSizeType increase() {
        switch (this) {
            case SMALL: return MEDIUM;
            case MEDIUM: return LARGE;
            case LARGE:
            case EXTRA_LARGE: return EXTRA_LARGE;
        }
        throw new IllegalStateException("Unknown field size");
    }

    public FieldSizeType reduce() {
        switch (this) {
            case SMALL:
            case MEDIUM: return SMALL;
            case LARGE: return MEDIUM;
            case EXTRA_LARGE: return LARGE;
        }
        throw new IllegalStateException("Unknown field size");
    }

    public static FieldSizeType maxValue() {
        return EXTRA_LARGE;
    }

    public static FieldSizeType minValue() {
        return SMALL;
    }

    public int intValue() {
        switch (this) {
            case SMALL: return 3;
            case MEDIUM: return 5;
            case LARGE: return 7;
            case EXTRA_LARGE: return 9;
        }
        throw new IllegalStateException("Unknown field size");
    }
}
