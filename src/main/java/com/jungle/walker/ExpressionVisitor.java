package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class ExpressionVisitor extends BaseVisitor {
    public ExpressionVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super(operandStackTypeStack, symbolTable);
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
    private NumericOperatorVisitor numericOperatorVisitor;

    @NotNull
    public ExpressionVisitor withNumericOperatorVisitor(@NotNull NumericOperatorVisitor numericOperatorVisitor) {
        this.numericOperatorVisitor = numericOperatorVisitor;
        return this;
    }

    @Nullable
    private CastIntegerVisitor castIntegerVisitor;

    @NotNull
    public ExpressionVisitor withCastIntegerVisitor(@NotNull CastIntegerVisitor castIntegerVisitor) {
        this.castIntegerVisitor = castIntegerVisitor;
        return this;
    }

    @Nullable BooleanOperatorVisitor booleanOperatorVisitor;

    @NotNull
    public ExpressionVisitor withBooleanOperatorVisitor(@NotNull BooleanOperatorVisitor booleanOperatorVisitor) {
        this.booleanOperatorVisitor = booleanOperatorVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit expression " + ast);

        if (identifierVisitor.canVisit(ast)) {
            identifierVisitor.visit(mv, ast);
            return;
        }

        if (literalVisitor.canVisit(ast)) {
            literalVisitor.visit(mv, ast);
            return;
        }

        if (numericOperatorVisitor.canVisit(ast)) {
            numericOperatorVisitor.visit(mv, ast);
            return;
        }

        if (castIntegerVisitor.canVisit(ast)) {
            castIntegerVisitor.visit(mv, ast);
            return;
        }

        if (booleanOperatorVisitor.canVisit(ast)) {
            booleanOperatorVisitor.visit(mv, ast);
            return;
        }

        throw new Error("expected expression " + ast);
    }
}
