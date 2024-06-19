package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LoopVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(LoopVisitor.class.getName());

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    @Nullable
    private BlockVisitor blockVisitor;

    @NotNull
    private BlockVisitor getBlockVisitor() {
        if (blockVisitor == null) {
            blockVisitor = new BlockVisitor(getCompilerOptions());
        }
        return blockVisitor;
    }

    public LoopVisitor(@NotNull final ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.LOOP.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        /*
         loop: IFEQ #end
               "block"
               GOTO #loop
         end:  ...
         */
        logger.debug("visit loop " + ast);

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
        getExpressionVisitor().visit(mv, ast.getLeft(), context);
        if (!OperandType.INTEGER_COMPUTATIONAL_TYPES.contains(context.peek())) {
            throw new Error("loop condition/expression expected to be type integer");
        }
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);

        // loop-block
        getBlockVisitor().visit(mv, ast.getRight(), context);
        mv.visitJumpInsn(Opcodes.GOTO, loopLabel);

        // end
        mv.visitLabel(endLabel);
    }
}
