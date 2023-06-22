package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class AssertVisitor implements IVisitor {

    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;

    public AssertVisitor(@NotNull Stack<OperandStackType> operandStackTypeStack) {
        super();
        this.operandStackTypeStack = operandStackTypeStack;
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public AssertVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit assert " + ast);
        if (ast.getType() != NodeType.ASSERT) {
            throw new Error("expected assert");
        }
        if (ast.getLeft() == null) {
            throw new Error("assert missing expression");
        }
        expressionVisitor.visit(mv, ast.getLeft());
        visitAssert(mv);
    }

    protected void visitAssert(@NotNull MethodVisitor mv) {
        // if (![int expression]) throw new AssertionError();

        if (operandStackTypeStack.peek() != OperandStackType.INTEGER) {
            throw new Error("expected assert expression to be integer");
        }

        // if int value on operand stack is not-equal to zero then throw error
        Label continueLabel = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, continueLabel);

        // throw new AssertionError();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/AssertionError"); // create on operand stack
        mv.visitInsn(Opcodes.DUP); // duplicate reference on operand stack
        mv.visitMethodInsn( // initialize reference on operand stack
                Opcodes.INVOKESPECIAL, "java/lang/AssertionError", "<init>", "()V", false);
        mv.visitInsn(Opcodes.ATHROW); // throw reference

        // continue with program...
        mv.visitLabel(continueLabel);
    }
}
