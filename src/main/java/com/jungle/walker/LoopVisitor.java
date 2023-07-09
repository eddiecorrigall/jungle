package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import com.jungle.operand.OperandStackType;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LoopVisitor implements IVisitor {

    @NotNull
    private final OperandStackContext operandStackContext;

    @NotNull
    private final ExpressionVisitor expressionVisitor;

    @NotNull
    private final BlockVisitor blockVisitor;

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
            return;
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
        expressionVisitor.visit(mv, ast.getLeft());
        if (operandStackContext.peek() != OperandStackType.INTEGER) {
            // TODO: shouldn't this be testing for boolean?
            throw new Error("loop condition/expression expected to be type integer");
        }
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);

        // loop-block
        blockVisitor.visit(mv, ast.getRight());
        mv.visitJumpInsn(Opcodes.GOTO, loopLabel);

        // end
        mv.visitLabel(endLabel);
    }
}
