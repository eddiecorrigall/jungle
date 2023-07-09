package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class MainVisitor implements IVisitor {

    // region Visitors

    @NotNull
    private final LiteralVisitor literalVisitor;

    @NotNull
    private final AssignmentVisitor assignmentVisitor;

    @NotNull
    private final BlockVisitor blockVisitor;

    @NotNull
    private final AssertVisitor assertVisitor;

    @NotNull
    private final PrintVisitor printVisitor;

    @NotNull
    private final IfVisitor ifVisitor;

    @NotNull
    private final LoopVisitor loopVisitor;

    @NotNull
    private final SequenceVisitor sequenceVisitor;

    // endregion

    public MainVisitor() {
        super();

        // Chicken before the egg problem...

        OperandStackContext operandStackContext = new OperandStackContext();

        ExpressionVisitor expressionVisitor = new ExpressionVisitor();

        sequenceVisitor = new SequenceVisitor(this);
        blockVisitor = new BlockVisitor(this);
        ifVisitor = new IfVisitor(operandStackContext, expressionVisitor, blockVisitor);
        literalVisitor = new LiteralVisitor(operandStackContext);
        IdentifierVisitor identifierVisitor = new IdentifierVisitor(operandStackContext);
        CastIntegerVisitor castIntegerVisitor = new CastIntegerVisitor(operandStackContext, expressionVisitor);
        assignmentVisitor = new AssignmentVisitor(operandStackContext, expressionVisitor);
        NumericOperatorVisitor numericOperatorVisitor = new NumericOperatorVisitor(operandStackContext, expressionVisitor);
        BooleanOperatorVisitor booleanOperatorVisitor = new BooleanOperatorVisitor(ifVisitor);
        assertVisitor = new AssertVisitor(operandStackContext, expressionVisitor);
        printVisitor = new PrintVisitor(operandStackContext, expressionVisitor);
        loopVisitor = new LoopVisitor(operandStackContext, expressionVisitor, blockVisitor);

        expressionVisitor
                .withIdentifierVisitor(identifierVisitor)
                .withLiteralVisitor(literalVisitor)
                .withNumericOperatorVisitor(numericOperatorVisitor)
                .withCastIntegerVisitor(castIntegerVisitor)
                .withBooleanOperatorVisitor(booleanOperatorVisitor);
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

        if (sequenceVisitor.canVisit(ast)) {
            sequenceVisitor.visit(mv, ast);
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
