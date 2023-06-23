package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import com.jungle.symbol.SymbolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static com.jungle.ast.NodeType.*;

public class NumericOperatorVisitor extends BaseVisitor {
    public static final Set<NodeType> NUMERIC_OPERATORS = new HashSet<>(Arrays.asList(
            OPERATOR_ADD,
            OPERATOR_SUBTRACT,
            OPERATOR_MULTIPLY,
            OPERATOR_DIVIDE,
            OPERATOR_MODULO
    ));

    public NumericOperatorVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable) {
        super(operandStackTypeStack, symbolTable);
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public NumericOperatorVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NUMERIC_OPERATORS.contains(node.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit binary operator " + ast);

        if (!canVisit(ast)) {
            return;
        }

        if (ast.getLeft() == null) {
            throw new Error("binary operator missing left expression");
        }

        if (ast.getRight() == null) {
            throw new Error("binary operator missing right expression");
        }

        expressionVisitor.visit(mv, ast.getLeft());
        OperandStackType leftExpressionType = operandStackTypeStack.pop();

        expressionVisitor.visit(mv, ast.getRight());
        OperandStackType rightExpressionType = operandStackTypeStack.pop();

        if (leftExpressionType != rightExpressionType) {
            throw new Error("binary operator left or right expression requires type cast " + ast);
        }

        OperandStackType operandStackType = leftExpressionType;
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
