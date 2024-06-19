package com.jungle.compiler.operand;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;

import com.jungle.common.SetUtils;

/**
 * Types that exist on the operand stack.
 * 
 * Used to determine the data type currently on the stack to validate and
 * make decisions when program is compiled.
 * 
 * https://docs.oracle.com/javase/specs/jvms/se6/html/Overview.doc.html
 */

public enum OperandType {
    ADDRESS, // return address
    INDEX,

    // Reference types...
    ARRAY,
    OBJECT,

    BOOLEAN,
    CHAR,

    // Integral types...
    BYTE,
    SHORT,
    INTEGER,
    LONG,

    // Floating-point types...
    DOUBLE,
    FLOAT;

    @NotNull
    public static final Set<OperandType> INTEGER_COMPUTATIONAL_TYPES = SetUtils.newSet(
        OperandType.BOOLEAN,
        OperandType.CHAR,
        OperandType.BYTE,
        OperandType.SHORT,
        OperandType.INTEGER
    );

    public int getConvertOpcode(@NotNull OperandType that) {
        switch (this) {
            case INTEGER: {
                switch (that) {
                    case CHAR: return Opcodes.I2C;
                    case BYTE: return Opcodes.I2B;
                    case SHORT: return Opcodes.I2S;
                    case LONG: return Opcodes.I2L;
                    case FLOAT: return Opcodes.I2F;
                    case DOUBLE: return Opcodes.I2D;
                    default: break;
                }
            }
            case LONG: {
                switch (that) {
                    case INTEGER: return Opcodes.L2I;
                    case FLOAT: return Opcodes.L2F;
                    case DOUBLE: return Opcodes.L2D;
                    default: break;
                }
            }
            case FLOAT: {
                switch (that) {
                    case INTEGER: return Opcodes.F2I;
                    case LONG: return Opcodes.F2L;
                    case DOUBLE: return Opcodes.F2D;
                    default: break;
                }
            }
            case DOUBLE: {
                switch (that) {
                    case INTEGER: return Opcodes.D2I;
                    case LONG: return Opcodes.D2L;
                    case FLOAT: return Opcodes.D2F;
                    default: break;
                }
            }
            default: break;
        }
        throw new Error("no convert opcode from " + this + " to " + that);
    }

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
            case CHAR: return Opcodes.ISTORE;
            case INTEGER: return Opcodes.ISTORE;
            case FLOAT: return Opcodes.FSTORE;
            case DOUBLE: return Opcodes.DSTORE;
            case OBJECT: return Opcodes.ASTORE;
            default: throw new Error("no store opcode for symbol type " + this);
        }
    }

    public int getLoadOpcode() {
        switch (this) {
            case CHAR: return Opcodes.ILOAD;
            case INTEGER: return Opcodes.ILOAD;
            case FLOAT: return Opcodes.FLOAD;
            case DOUBLE: return Opcodes.DLOAD;
            case OBJECT: return Opcodes.ALOAD;
            default: throw new Error("no load opcode for symbol type " + this);
        }
    }
}
