package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandStackType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CastIntegerVisitor extends AbstractClassPathVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(CastIntegerVisitor.class.getName());

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getClassPath());
        }
        return expressionVisitor;
    }

    public CastIntegerVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.CAST_INTEGER.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit cast integer " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected cast integer");
        }

        if (ast.getLeft() == null) {
            throw new Error("cast integer missing expression");
        }

        getExpressionVisitor().visit(mv, ast.getLeft(), context);
        OperandStackType type = context.pop();
        switch (type) {
            case INTEGER: {
                logger.warn("value is already an integer");
            } break;
            case FLOAT: {
                mv.visitInsn(Opcodes.F2I);
            } break;
            default: {
                throw new Error("integer cast not supported for " + ast);
            }
        }
        context.push(OperandStackType.INTEGER);
    }
}
