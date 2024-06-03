package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandStackType;
import com.jungle.compiler.symbol.SymbolType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jungle.ast.NodeType.*;

public class NumericOperatorVisitor implements IVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(NumericOperatorVisitor.class.getName());

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
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NUMERIC_OPERATORS.contains(node.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        logger.debug("visit binary operator " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected numeric operator");
        }

        if (ast.getLeft() == null) {
            throw new Error("binary operator missing left expression");
        }

        if (ast.getRight() == null) {
            throw new Error("binary operator missing right expression");
        }

        // prepare for operation...

        getExpressionVisitor().visit(mv, ast.getLeft());
        OperandStackType leftExpressionType = getOperandStackContext().pop();

        getExpressionVisitor().visit(mv, ast.getRight());
        OperandStackType rightExpressionType = getOperandStackContext().pop();

        // prepare for operation - cast types...

        OperandStackType operandStackType;

        if (leftExpressionType == rightExpressionType) {
            operandStackType = leftExpressionType;
            getOperandStackContext().push(operandStackType);
        } else if (OperandStackType.INTEGER == leftExpressionType && OperandStackType.CHARACTER == rightExpressionType) {
            mv.visitInsn(Opcodes.SWAP); // move integer (left) to top
            mv.visitInsn(Opcodes.I2C); // convert integer to character
            mv.visitInsn(Opcodes.SWAP); // restore
            operandStackType = OperandStackType.INTEGER; // operation
            getOperandStackContext().push(OperandStackType.CHARACTER); // final type
        } else if (OperandStackType.CHARACTER == leftExpressionType && OperandStackType.INTEGER == rightExpressionType) {
            mv.visitInsn(Opcodes.I2C); // convert integer (right)
            operandStackType = OperandStackType.INTEGER; // operation
            getOperandStackContext().push(OperandStackType.CHARACTER); // final type
        } else {
            throw new Error("binary operator left or right expression requires type cast " + ast);
        }

        // perform operation...

        SymbolType symbolType = operandStackType.getSymbolType();

        switch (ast.getType()) {
            case OPERATOR_ADD: mv.visitInsn(symbolType.getAddOpcode()); break;
            case OPERATOR_SUBTRACT: mv.visitInsn(symbolType.getSubtractOpcode()); break; // order matters
            case OPERATOR_MULTIPLY: mv.visitInsn(symbolType.getMultiplyOpcode()); break;
            case OPERATOR_DIVIDE: mv.visitInsn(symbolType.getDivideOpcode()); break; // order matters
            case OPERATOR_MODULO: mv.visitInsn(symbolType.getModuloOpcode()); break; // order matters
            default: throw new Error("unhandled binary operator " + ast);
        }
    }
}
