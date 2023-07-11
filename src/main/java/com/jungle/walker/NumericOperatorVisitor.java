package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import com.jungle.operand.OperandStackType;
import com.jungle.symbol.SymbolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jungle.ast.NodeType.*;

public class NumericOperatorVisitor implements IVisitor {
    private static final Set<NodeType> NUMERIC_OPERATORS = new HashSet<>(Arrays.asList(
            OPERATOR_ADD,
            OPERATOR_SUBTRACT,
            OPERATOR_MULTIPLY,
            OPERATOR_DIVIDE,
            OPERATOR_MODULO
    ));

    @Nullable
    private OperandStackContext operandStackContext;

    private OperandStackContext getOperandStackContext() {
        if (operandStackContext == null) {
            operandStackContext = OperandStackContext.getInstance();
        }
        return operandStackContext;
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor();
        }
        return expressionVisitor;
    }

    public NumericOperatorVisitor(
            @NotNull final OperandStackContext operandStackContext,
            @NotNull final ExpressionVisitor expressionVisitor
    ) {
        super();
        this.operandStackContext = operandStackContext;
        this.expressionVisitor = expressionVisitor;
    }

    public NumericOperatorVisitor() {
        this(null, null);
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NUMERIC_OPERATORS.contains(node.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit binary operator " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected numeric operator");
        }

        if (ast.getLeft() == null) {
            throw new Error("binary operator missing left expression");
        }

        if (ast.getRight() == null) {
            throw new Error("binary operator missing right expression");
        }

        getExpressionVisitor().visit(mv, ast.getLeft());
        OperandStackType leftExpressionType = getOperandStackContext().pop();

        getExpressionVisitor().visit(mv, ast.getRight());
        OperandStackType rightExpressionType = getOperandStackContext().pop();

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

        getOperandStackContext().push(operandStackType);
    }
}
