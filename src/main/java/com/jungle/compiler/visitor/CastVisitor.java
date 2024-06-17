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

public class CastVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(CastVisitor.class.getName());

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    public CastVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    protected OperandType getCastType(@NotNull INode ast) {
        switch (ast.getType()) {
            case CAST_INTEGER: return OperandType.INTEGER;
            case CAST_LONG: return OperandType.LONG;
            case CAST_FLOAT: return OperandType.FLOAT;
            case CAST_DOUBLE: return OperandType.DOUBLE;
            default: break;
        }
        throw new Error("expected cast");
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        switch (ast.getType()) {
            case CAST_INTEGER:
            case CAST_LONG:
            case CAST_FLOAT:
            case CAST_DOUBLE:
                return true;
            default: return false;
        }
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit cast integer " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected cast");
        }

        if (ast.getLeft() == null) {
            throw new Error("cast missing expression");
        }

        getExpressionVisitor().visit(mv, ast.getLeft(), context);
        OperandType fromType = context.pop();
        OperandType toType = getCastType(ast);

        mv.visitInsn(fromType.getConvertOpcode(toType));
        context.push(toType);
    }
}
