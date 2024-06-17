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

public class AssertVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(AssertVisitor.class.getName());

    /* TODO
     * It might be possible to use reflection to provide more context on the assertion.
     * For example: which method, which line, which variables, etc.
     */

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    public AssertVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.ASSERT.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit assert " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected assert");
        }

        if (ast.getLeft() == null) {
            throw new Error("assert condition missing expression");
        }

        // push expression/condition onto operand stack
        getExpressionVisitor().visit(mv, ast.getLeft(), context);

        // if (![int expression]) throw new AssertionError("Detailed Message");

        if (context.peek() != OperandType.INTEGER) {
            throw new Error("assert condition/expression expected to be type integer");
        }

        // if int value on operand stack is not-equal to zero then throw error
        Label endLabel = new Label();
        mv.visitJumpInsn(Opcodes.IFNE, endLabel);

        // throw new AssertionError();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/AssertionError"); // create on operand stack
        mv.visitInsn(Opcodes.DUP); // duplicate reference on operand stack

        // push detailed message onto operand stack
        mv.visitLdcInsn("assertion condition evaluated to 0 (false)");

        mv.visitMethodInsn( // initialize reference on operand stack
                Opcodes.INVOKESPECIAL, "java/lang/AssertionError", "<init>", "(Ljava/lang/Object;)V", false);
        mv.visitInsn(Opcodes.ATHROW); // throw reference

        // continue with program...
        mv.visitLabel(endLabel);
    }
}
