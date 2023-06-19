package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class BinaryOperatorVisitor implements IVisitor {
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;

    @Nullable
    private ExpressionVisitor expressionVisitor;

    public BinaryOperatorVisitor(@NotNull Stack<OperandStackType> operandStackTypeStack) {
        super();
        this.operandStackTypeStack = operandStackTypeStack;
    }

    @NotNull
    public BinaryOperatorVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit binary operator " + ast);

        if (!NodeType.BINARY_OPERATORS.contains(ast.getType())) {
            throw new Error("expected binary operator");
        }

        expressionVisitor.visit(mv, ast.getLeft());
        OperandStackType leftNodeType = operandStackTypeStack.pop();

        expressionVisitor.visit(mv, ast.getRight());
        OperandStackType rightNodeType = operandStackTypeStack.pop();

        if (leftNodeType != rightNodeType) {
            throw new Error("left or right expression requires type cast " + ast);
        }

        OperandStackType operandStackType = leftNodeType;
        SymbolType type = operandStackType.getSymbolType();

        switch (ast.getType()) {
            case OPERATOR_ADD: mv.visitInsn(type.getAddOpcode()); break;
            case OPERATOR_SUBTRACT: mv.visitInsn(type.getSubtractOpcode()); break;
            case OPERATOR_MULTIPLY: mv.visitInsn(type.getMultiplyOpcode()); break;
            case OPERATOR_DIVIDE: mv.visitInsn(type.getDivideOpcode()); break;
            case OPERATOR_MODULO: mv.visitInsn(type.getModuloOpcode()); break;
            default: throw new Error("unhandled binary operator " + ast);
        }

        operandStackTypeStack.push(operandStackType);
    }
}