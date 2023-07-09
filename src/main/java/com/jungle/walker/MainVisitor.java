package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class MainVisitor implements IVisitor {

    @NotNull
    private final OperandStackContext operandStackContext = new OperandStackContext();

    // region Visitors

    @NotNull
    private final ExpressionVisitor expressionVisitor;

    @NotNull
    private final LiteralVisitor literalVisitor;

    @NotNull
    final IdentifierVisitor identifierVisitor;

    @NotNull
    final CastIntegerVisitor castIntegerVisitor;

    @NotNull
    final AssignmentVisitor assignmentVisitor;

    @NotNull
    final NumericOperatorVisitor numericOperatorVisitor;

    @NotNull
    final BooleanOperatorVisitor booleanOperatorVisitor;

    @NotNull
    final BlockVisitor blockVisitor;

    @NotNull
    final AssertVisitor assertVisitor;

    @NotNull
    final PrintVisitor printVisitor;

    @NotNull
    final IfVisitor ifVisitor;

    @NotNull
    final LoopVisitor loopVisitor;

    // endregion

    public MainVisitor() {
        super();

        // Chicken before the egg problem...

        blockVisitor = new BlockVisitor(this);

        expressionVisitor = new ExpressionVisitor();

        ifVisitor = new IfVisitor(operandStackContext, expressionVisitor, blockVisitor);
        literalVisitor = new LiteralVisitor(operandStackContext);
        identifierVisitor = new IdentifierVisitor(operandStackContext);
        castIntegerVisitor = new CastIntegerVisitor(operandStackContext, expressionVisitor);
        assignmentVisitor = new AssignmentVisitor(operandStackContext, expressionVisitor);
        numericOperatorVisitor = new NumericOperatorVisitor(operandStackContext, expressionVisitor);
        booleanOperatorVisitor = new BooleanOperatorVisitor(ifVisitor);

        expressionVisitor
                .withIdentifierVisitor(identifierVisitor)
                .withLiteralVisitor(literalVisitor)
                .withNumericOperatorVisitor(numericOperatorVisitor)
                .withCastIntegerVisitor(castIntegerVisitor)
                .withBooleanOperatorVisitor(booleanOperatorVisitor);

        assertVisitor = new AssertVisitor(operandStackContext, expressionVisitor);
        printVisitor = new PrintVisitor(operandStackContext, expressionVisitor);
        loopVisitor = new LoopVisitor(operandStackContext, expressionVisitor, blockVisitor);
    }

    public boolean canVisit(@NotNull INode ast) {
        return true; // TODO
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {

        if (ast == null) {
            // (No-op) No operation
            return;
        }

        if (ast.getType() == NodeType.SEQUENCE) {
            visit(mv, ast.getLeft());
            visit(mv, ast.getRight());
            return;
        }

        if (blockVisitor.canVisit(ast)) {
            blockVisitor.visit(mv, ast);
            return;
        }

        if (literalVisitor.canVisit(ast)) {
            literalVisitor.visit(mv, ast);
            return;
        }

        if (assignmentVisitor.canVisit(ast)) {
            assignmentVisitor.visit(mv, ast);
            return;
        }

        if (assertVisitor.canVisit(ast)) {
            assertVisitor.visit(mv, ast);
            return;
        }

        if (printVisitor.canVisit(ast)) {
            printVisitor.visit(mv, ast);
            return;
        }

        if (ifVisitor.canVisit(ast)) {
            ifVisitor.visit(mv, ast);
            return;
        }

        if (loopVisitor.canVisit(ast)) {
            loopVisitor.visit(mv, ast);
            return;
        }

        throw new Error("unexpected node " + ast);
    }
}
