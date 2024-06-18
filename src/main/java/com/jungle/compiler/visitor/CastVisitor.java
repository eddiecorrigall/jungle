package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.common.MapBuilder;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;
import com.jungle.logger.FileLogger;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class CastVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(CastVisitor.class.getName());

    private static final Map<NodeType, OperandType> NODE_TYPE_TO_OPERAND_TYPE_MAP = new MapBuilder<NodeType, OperandType>()
        .withEntry(NodeType.CAST_CHAR, OperandType.CHAR)
        .withEntry(NodeType.CAST_BYTE, OperandType.BYTE)
        .withEntry(NodeType.CAST_SHORT, OperandType.SHORT)
        .withEntry(NodeType.CAST_INTEGER, OperandType.INTEGER)
        .withEntry(NodeType.CAST_LONG, OperandType.LONG)
        .withEntry(NodeType.CAST_FLOAT, OperandType.FLOAT)
        .withEntry(NodeType.CAST_DOUBLE, OperandType.DOUBLE)
        .build();

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

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NODE_TYPE_TO_OPERAND_TYPE_MAP.containsKey(ast.getType());
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
        OperandType toType = NODE_TYPE_TO_OPERAND_TYPE_MAP.get(ast.getType());

        mv.visitInsn(fromType.getConvertOpcode(toType));
        context.push(toType);
    }
}
