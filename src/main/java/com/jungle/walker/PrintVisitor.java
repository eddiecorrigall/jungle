package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

import static org.objectweb.asm.Opcodes.*;

public class PrintVisitor implements IVisitor {
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;

    @Nullable
    private ExpressionVisitor expressionVisitor;

    public PrintVisitor(@NotNull Stack<OperandStackType> operandStackTypeStack) {
       super();
       this.operandStackTypeStack = operandStackTypeStack;
    }

    @NotNull
    public PrintVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.PRINT.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {
        System.out.println("visit print " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected print");
        }

        if (ast.getLeft() == null) {
            throw new Error("print missing expression");
        }

        expressionVisitor.visit(mv, ast.getLeft());
        visitPrint(mv);
    }

    protected void visitPrint(@NotNull MethodVisitor mv) {
        // System.out.print(stack[0]);

        // Prints what ever value is on the stack

        OperandStackType operandStackType = operandStackTypeStack.pop();
        String descriptor;
        switch (operandStackType) {
            case BOOLEAN: descriptor = "(B)V"; break;
            case CHARACTER: descriptor = "(C)V"; break;
            case INTEGER: descriptor = "(I)V"; break;
            case FLOAT: descriptor = "(F)V"; break;
            case REFERENCE_OBJECT: descriptor = "(Ljava/lang/Object;)V"; break;
            default: throw new Error("cannot determine print descriptor from operand stack type " + operandStackType);
        }

        // System.out
        mv.visitFieldInsn(
                GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");
        // "swap" last 2 words on stack to make the desired value to print first and the "out" Object second
        mv.visitInsn(SWAP);
        // out.print()
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "print",
                descriptor,
                false);
    }
}
