package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import com.jungle.operand.OperandStackType;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AssertVisitor implements IVisitor {
    @NotNull
    private final OperandStackContext operandStackContext;
    @NotNull
    private final IVisitor expressionVisitor;

    public AssertVisitor(
            @NotNull final OperandStackContext operandStackContext,
            @NotNull final IVisitor expressionVisitor
    ) {
        super();
        this.operandStackContext = operandStackContext;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.ASSERT.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit assert " + ast);

        if (!canVisit(ast)) {
            return;
        }

        if (ast.getLeft() == null) {
            throw new Error("assert missing expression");
        }

        // push expression/condition onto operand stack
        expressionVisitor.visit(mv, ast.getLeft());

        // if (![int expression]) throw new AssertionError("Detailed Message");

        if (operandStackContext.peek() != OperandStackType.INTEGER) {
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
