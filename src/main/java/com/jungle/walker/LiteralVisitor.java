package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class LiteralVisitor implements IVisitor {
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;

    public LiteralVisitor(@NotNull Stack<OperandStackType> operandStackTypeStack) {
       super();
       this.operandStackTypeStack = operandStackTypeStack;
    }

    protected static Boolean stringToBoolean(@NotNull String value) {
        return Boolean.valueOf(value);
    }

    protected static Character stringToCharacter(@NotNull String value) {
        if (value.length() != 1) {
            throw new Error("failed to parse character as integer");
        }
        return new Character(value.charAt(0));
    }

    protected static Integer stringToInteger(@NotNull String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new Error("failed to parse string as integer", e);
        }
    }

    protected static Float stringToFloat(@NotNull String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new Error("failed to parse string as float", e);
        }
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit literal " + ast);

        if (!NodeType.LITERALS.contains(ast.getType())) {
            throw new Error("expected literal");
        }

        if (ast.getValue() == null) {
            throw new Error("literal missing value");
        }

        Object objectValue;
        OperandStackType type;

        switch (ast.getType()) {
            case LITERAL_BOOLEAN: {
                objectValue = stringToBoolean(ast.getValue());
                type = OperandStackType.BOOLEAN;
            } break;
            case LITERAL_CHARACTER: {
                objectValue = stringToCharacter(StringEscapeUtils.unescapeJava(ast.getValue()));
                type = OperandStackType.CHARACTER;
            } break;
            case LITERAL_INTEGER: {
                objectValue = stringToInteger(ast.getValue());
                type = OperandStackType.INTEGER;
            } break;
            case LITERAL_FLOAT: {
                objectValue = stringToFloat(ast.getValue());
                type = OperandStackType.FLOAT;
            } break;
            case LITERAL_STRING: {
                objectValue = StringEscapeUtils.unescapeJava(ast.getValue());
                type = OperandStackType.REFERENCE_OBJECT;
            } break;
            default: throw new Error("unhandled literal");
        }

        mv.visitLdcInsn(objectValue);
        operandStackTypeStack.push(type);
    }
}
