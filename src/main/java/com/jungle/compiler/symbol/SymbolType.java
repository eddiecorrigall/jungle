package com.jungle.compiler.symbol;

import org.objectweb.asm.Opcodes;

public enum SymbolType {
    OBJECT,
    // Primitives...
    BOOLEAN,
    BYTE,
    CHARACTER,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE;

    public int getAddOpcode() {
        switch (this) {
            case INTEGER: return Opcodes.IADD;
            case FLOAT: return Opcodes.FADD;
            case DOUBLE: return Opcodes.DADD;
            default: throw new Error("no add opcode for symbol type " + this);
        }
    }

    public int getSubtractOpcode() {
        switch (this) {
            case INTEGER: return Opcodes.ISUB;
            case FLOAT: return Opcodes.FSUB;
            case DOUBLE: return Opcodes.DSUB;
            default: throw new Error("no sub opcode for symbol type " + this);
        }
    }

    public int getMultiplyOpcode() {
        switch (this) {
            case INTEGER: return Opcodes.IMUL;
            case FLOAT: return Opcodes.FMUL;
            case DOUBLE: return Opcodes.DMUL;
            default: throw new Error("no mul opcode for symbol type " + this);
        }
    }

    public int getDivideOpcode() {
        switch (this) {
            case INTEGER: return Opcodes.IDIV;
            case FLOAT: return Opcodes.FDIV;
            case DOUBLE: return Opcodes.DDIV;
            default: throw new Error("no div opcode for symbol type " + this);
        }
    }

    public int getModuloOpcode() {
        switch (this) {
            case INTEGER: return Opcodes.IREM;
            case FLOAT: return Opcodes.FREM;
            case DOUBLE: return Opcodes.DREM;
            default: throw new Error("no mod opcode for symbol type " + this);
        }
    }

    public int getStoreOpcode() {
        switch (this) {
            case CHARACTER: return Opcodes.ISTORE;
            case INTEGER: return Opcodes.ISTORE;
            case FLOAT: return Opcodes.FSTORE;
            case DOUBLE: return Opcodes.DSTORE;
            case OBJECT: return Opcodes.ASTORE;
            default: throw new Error("no store opcode for symbol type " + this);
        }
    }

    public int getLoadOpcode() {
        switch (this) {
            case CHARACTER: return Opcodes.ILOAD;
            case INTEGER: return Opcodes.ILOAD;
            case FLOAT: return Opcodes.FLOAD;
            case DOUBLE: return Opcodes.DLOAD;
            case OBJECT: return Opcodes.ALOAD;
            default: throw new Error("no load opcode for symbol type " + this);
        }
    }
}
