package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class PrintVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(NumericOperatorVisitor.class.getName());

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    public PrintVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.PRINT.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit print " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected print");
        }

        if (ast.getLeft() == null) {
            throw new Error("print missing expression");
        }

        getExpressionVisitor().visit(mv, ast.getLeft(), context);
        visitPrint(mv, context);
    }

    protected void visitPrint(@NotNull MethodVisitor mv, @NotNull OperandStackContext context) {
        // System.out.print(stack[0]);

        // Prints what ever value is on the stack

        OperandType operandType = context.pop();
        String descriptor;
        switch (operandType) {
            case BOOLEAN: descriptor = "(B)V"; break;
            case CHAR: descriptor = "(C)V"; break;
            case INTEGER: descriptor = "(I)V"; break;
            case FLOAT: descriptor = "(F)V"; break;
            case OBJECT: descriptor = "(Ljava/lang/Object;)V"; break;
            default: throw new Error("cannot determine print descriptor from operand stack type " + operandType);
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
