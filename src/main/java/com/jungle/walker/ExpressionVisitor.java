package com.jungle.walker;

import com.jungle.ast.INode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class ExpressionVisitor implements IVisitor {
    @Nullable
    private IdentifierVisitor identifierVisitor;

    @NotNull
    private IdentifierVisitor getIdentifierVisitor() {
        if (identifierVisitor == null) {
            identifierVisitor = new IdentifierVisitor();
        }
        return identifierVisitor;
    }

    @Nullable
    private LiteralVisitor literalVisitor;

    @NotNull
    private LiteralVisitor getLiteralVisitor() {
        if (literalVisitor == null) {
            literalVisitor = new LiteralVisitor();
        }
        return literalVisitor;
    }

    @Nullable
    private NumericOperatorVisitor numericOperatorVisitor;

    @NotNull
    private NumericOperatorVisitor getNumericOperatorVisitor() {
        if (numericOperatorVisitor == null) {
            numericOperatorVisitor = new NumericOperatorVisitor();
        }
        return numericOperatorVisitor;
    }

    @Nullable
    private CastIntegerVisitor castIntegerVisitor;

    @NotNull
    private CastIntegerVisitor getCastIntegerVisitor() {
        if (castIntegerVisitor == null) {
            castIntegerVisitor = new CastIntegerVisitor();
        }
        return castIntegerVisitor;
    }

    @Nullable BooleanOperatorVisitor booleanOperatorVisitor;

    @NotNull
    private BooleanOperatorVisitor getBooleanOperatorVisitor() {
        if (booleanOperatorVisitor == null) {
            booleanOperatorVisitor = new BooleanOperatorVisitor();
        }
        return booleanOperatorVisitor;
    }

    private ExpressionVisitor(
            @Nullable IdentifierVisitor identifierVisitor,
            @Nullable LiteralVisitor literalVisitor,
            @Nullable NumericOperatorVisitor numericOperatorVisitor,
            @Nullable CastIntegerVisitor castIntegerVisitor,
            @Nullable BooleanOperatorVisitor booleanOperatorVisitor
    ) {
        super();
        this.identifierVisitor = identifierVisitor;
        this.literalVisitor = literalVisitor;
        this.numericOperatorVisitor = numericOperatorVisitor;
        this.castIntegerVisitor = castIntegerVisitor;
        this.booleanOperatorVisitor = booleanOperatorVisitor;
    }

    public ExpressionVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return true; // TODO
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit expression " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected expression");
        }

        if (getIdentifierVisitor().canVisit(ast)) {
            getIdentifierVisitor().visit(mv, ast);
            return;
        }

        if (getLiteralVisitor().canVisit(ast)) {
            getLiteralVisitor().visit(mv, ast);
            return;
        }

        if (getNumericOperatorVisitor().canVisit(ast)) {
            getNumericOperatorVisitor().visit(mv, ast);
            return;
        }

        if (getCastIntegerVisitor().canVisit(ast)) {
            getCastIntegerVisitor().visit(mv, ast);
            return;
        }

        if (getBooleanOperatorVisitor().canVisit(ast)) {
            getBooleanOperatorVisitor().visit(mv, ast);
            return;
        }

        throw new Error("expected expression " + ast);
    }
}
