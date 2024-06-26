package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class ExpressionVisitor extends AbstractVisitor {
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
            numericOperatorVisitor = new NumericOperatorVisitor(getCompilerOptions());
        }
        return numericOperatorVisitor;
    }

    @Nullable
    private CastVisitor castVisitor;

    @NotNull
    private CastVisitor getCastVisitor() {
        if (castVisitor == null) {
            castVisitor = new CastVisitor(getCompilerOptions());
        }
        return castVisitor;
    }

    @Nullable BooleanOperatorVisitor booleanOperatorVisitor;

    @NotNull
    private BooleanOperatorVisitor getBooleanOperatorVisitor() {
        if (booleanOperatorVisitor == null) {
            booleanOperatorVisitor = new BooleanOperatorVisitor(getCompilerOptions());
        }
        return booleanOperatorVisitor;
    }

    public ExpressionVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return true; // TODO
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit expression " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected expression");
        }

        if (ast.getType() == NodeType.NOOP) {
            logger.debug("no-op");
            return;
        }

        if (getIdentifierVisitor().canVisit(ast)) {
            getIdentifierVisitor().visit(mv, ast, context);
            return;
        }

        if (getLiteralVisitor().canVisit(ast)) {
            getLiteralVisitor().visit(mv, ast, context);
            return;
        }

        if (getNumericOperatorVisitor().canVisit(ast)) {
            getNumericOperatorVisitor().visit(mv, ast, context);
            return;
        }

        if (getCastVisitor().canVisit(ast)) {
            getCastVisitor().visit(mv, ast, context);
            return;
        }

        if (getBooleanOperatorVisitor().canVisit(ast)) {
            getBooleanOperatorVisitor().visit(mv, ast, context);
            return;
        }

        throw new Error("expected expression " + ast);
    }
}
