package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandStackType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CastIntegerVisitor implements IVisitor {
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
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor();
        }
        return expressionVisitor;
    }

    private CastIntegerVisitor(
            @NotNull final OperandStackContext operandStackContext,
            @NotNull final ExpressionVisitor expressionVisitor
    ) {
        super();
        this.operandStackContext = operandStackContext;
        this.expressionVisitor = expressionVisitor;
    }

    public CastIntegerVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.CAST_INTEGER.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit cast integer " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected cast integer");
        }

        if (ast.getLeft() == null) {
            throw new Error("cast integer missing expression");
        }

        getExpressionVisitor().visit(mv, ast.getLeft());
        OperandStackType type = getOperandStackContext().pop();
        switch (type) {
            case INTEGER: {
                System.out.println("WARN: value is already an integer");
            } break;
            case FLOAT: {
                mv.visitInsn(Opcodes.F2I);
            } break;
            default: {
                throw new Error("integer cast not supported for " + ast);
            }
        }
        getOperandStackContext().push(OperandStackType.INTEGER);
    }
}
