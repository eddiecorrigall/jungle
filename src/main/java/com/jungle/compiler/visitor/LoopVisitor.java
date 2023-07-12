package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandStackType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LoopVisitor implements IVisitor {
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

    @Nullable
    private BlockVisitor blockVisitor;

    @NotNull
    private BlockVisitor getBlockVisitor() {
        if (blockVisitor == null) {
            blockVisitor = new BlockVisitor();
        }
        return blockVisitor;
    }

    public LoopVisitor(
            @NotNull final OperandStackContext operandStackContext,
            @NotNull final ExpressionVisitor expressionVisitor,
            @NotNull final BlockVisitor blockVisitor
    ) {
        super();
        this.operandStackContext = operandStackContext;
        this.expressionVisitor = expressionVisitor;
        this.blockVisitor = blockVisitor;
    }

    public LoopVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.LOOP.equals(ast.getType());
    }
    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        /*
         loop: IFEQ #end
               "block"
               GOTO #loop
         end:  ...
         */
        System.out.println("visit loop " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected loop");
        }

        if (ast.getLeft() == null) {
            throw new Error("loop missing expression");
        }

        if (ast.getRight() == null) {
            throw new Error("loop missing body");
        }

        Label loopLabel = new Label();
        Label endLabel = new Label();

        // loop-condition
        mv.visitLabel(loopLabel);
        getExpressionVisitor().visit(mv, ast.getLeft());
        if (getOperandStackContext().peek() != OperandStackType.INTEGER) {
            // TODO: shouldn't this be testing for boolean?
            throw new Error("loop condition/expression expected to be type integer");
        }
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);

        // loop-block
        getBlockVisitor().visit(mv, ast.getRight());
        mv.visitJumpInsn(Opcodes.GOTO, loopLabel);

        // end
        mv.visitLabel(endLabel);
    }
}
