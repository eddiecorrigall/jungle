package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class ExpressionVisitor extends AbstractClassPathVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(ExpressionVisitor.class.getName());

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
            numericOperatorVisitor = new NumericOperatorVisitor(getClassPath());
        }
        return numericOperatorVisitor;
    }

    @Nullable
    private CastIntegerVisitor castIntegerVisitor;

    @NotNull
    private CastIntegerVisitor getCastIntegerVisitor() {
        if (castIntegerVisitor == null) {
            castIntegerVisitor = new CastIntegerVisitor(getClassPath());
        }
        return castIntegerVisitor;
    }

    @Nullable BooleanOperatorVisitor booleanOperatorVisitor;

    @NotNull
    private BooleanOperatorVisitor getBooleanOperatorVisitor() {
        if (booleanOperatorVisitor == null) {
            booleanOperatorVisitor = new BooleanOperatorVisitor(getClassPath());
        }
        return booleanOperatorVisitor;
    }

    public ExpressionVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return true; // TODO
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        logger.debug("visit expression " + ast);

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
