package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class ExpressionVisitor implements IVisitor {
    public ExpressionVisitor() {
        super();
    }

    @Nullable
    private IdentifierVisitor identifierVisitor;

    @NotNull
    public ExpressionVisitor withIdentifierVisitor(@NotNull IdentifierVisitor identifierVisitor) {
        this.identifierVisitor = identifierVisitor;
        return this;
    }

    @Nullable
    private LiteralVisitor literalVisitor;

    @NotNull
    public ExpressionVisitor withLiteralVisitor(@NotNull LiteralVisitor literalVisitor) {
        this.literalVisitor = literalVisitor;
        return this;
    }

    @Nullable
    private BinaryOperatorVisitor binaryOperatorVisitor;

    @NotNull
    public ExpressionVisitor withBinaryOperatorVisitor(@NotNull BinaryOperatorVisitor binaryOperatorVisitor) {
        this.binaryOperatorVisitor = binaryOperatorVisitor;
        return this;
    }

    @Nullable
    private CastIntegerVisitor castIntegerVisitor;

    @NotNull
    public ExpressionVisitor withCastIntegerVisitor(@NotNull CastIntegerVisitor castIntegerVisitor) {
        this.castIntegerVisitor = castIntegerVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit expression " + ast);

        if (NodeType.IDENTIFIER == ast.getType()) {
            identifierVisitor.visit(mv, ast);
            return;
        }

        if (NodeType.LITERALS.contains(ast.getType())) {
            literalVisitor.visit(mv, ast);
            return;
        }

        if (NodeType.CAST_INTEGER == ast.getType()) {
            castIntegerVisitor.visit(mv, ast);
            return;
        }

        if (NodeType.BINARY_OPERATORS.contains(ast.getType())) {
            binaryOperatorVisitor.visit(mv, ast);
            return;
        }

        throw new Error("expected expression " + ast);
    }
}
