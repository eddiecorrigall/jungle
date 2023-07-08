package com.jungle.walker;

import com.jungle.symbol.SymbolType;

public enum OperandStackType {
    INDEX,
    BOOLEAN,
    INTEGER,
    CHARACTER,
    DOUBLE,
    FLOAT,
    REFERENCE_ARRAY,
    REFERENCE_OBJECT;

    public SymbolType getSymbolType() {
        switch (this) {
            case CHARACTER: return SymbolType.CHARACTER;
            case INTEGER: return SymbolType.INTEGER;
            case FLOAT: return SymbolType.FLOAT;
            case DOUBLE: return SymbolType.DOUBLE;
            case REFERENCE_OBJECT: return SymbolType.OBJECT;
            default: throw new Error("get symbol type - cannot resolve from operand stack type " + this);
        }
    }
}
