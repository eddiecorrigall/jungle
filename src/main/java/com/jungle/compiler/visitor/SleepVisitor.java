package com.jungle.compiler.visitor;

import static org.objectweb.asm.Opcodes.I2L;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;

public class SleepVisitor extends AbstractVisitor {

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    public SleepVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return ast.getType().equals(NodeType.SLEEP);
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast, @NotNull OperandStackContext context) {
        // generate code for the integer representing the number of milliseconds to wait

        if (ast.getLeft() == null) {
            throw new Error("sleep missing expression");
        }

        // push expression onto operand stack
        getExpressionVisitor().visit(mv, ast.getLeft(), context);

        // verify that the expression was an integer type
        if (context.peek() != OperandType.INTEGER) {
            throw new Error("sleep expression expected to be type integer");
        }

        // For now, convert the integer to long
        mv.visitInsn(I2L);

        // invoke static method: Thread.sleep()
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Thread",
            "sleep",
            "(J)V",
            false
        );
    }
}
