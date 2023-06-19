package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class LiteralVisitor implements IVisitor {
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;

    public LiteralVisitor(@NotNull Stack<OperandStackType> operandStackTypeStack) {
       super();
       this.operandStackTypeStack = operandStackTypeStack;
    }

    public Object getLiteralValue(@NotNull INode ast) {
        switch (ast.getType()) {
            case LITERAL_BOOLEAN: return Boolean.valueOf(ast.getValue());
            case LITERAL_CHARACTER: return ast.getValue().charAt(0);
            case LITERAL_INTEGER: return Integer.parseInt(ast.getValue());
            case LITERAL_FLOAT: return Float.parseFloat(ast.getValue());
            case LITERAL_STRING: return ast.getValue();
            default: throw new Error("cannot visit literal node " + ast);
        }
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {
        if (ast == null) {
            return;
        }
        if (!NodeType.LITERALS.contains(ast.getType())) {
            throw new Error("expected literal but got " + ast);
        }
        System.out.println("visit literal " + ast);
        switch (ast.getType()) {
            case LITERAL_BOOLEAN: operandStackTypeStack.push(OperandStackType.BOOLEAN); break;
            case LITERAL_CHARACTER: operandStackTypeStack.push(OperandStackType.CHARACTER); break;
            case LITERAL_INTEGER: operandStackTypeStack.push(OperandStackType.INTEGER); break;
            case LITERAL_FLOAT: operandStackTypeStack.push(OperandStackType.FLOAT); break;
            case LITERAL_STRING: operandStackTypeStack.push(OperandStackType.REFERENCE_OBJECT); break;
            default: throw new Error("cannot push operand stack type - unhandled literal");
        }
        Object value = getLiteralValue(ast);
        mv.visitLdcInsn(value);
    }
}
