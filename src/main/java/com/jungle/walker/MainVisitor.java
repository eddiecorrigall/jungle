package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class MainVisitor implements IVisitor {
    // Track the node type that goes onto the jvm stack to catch semantic errors before they are runtime errors
    // When the jvm instruction adds to the stack, add the node type to this compile-time stack
    // When the jvm instruction removes from the stack, remove the type from this compile-time stack
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack = new Stack<>();

    @NotNull
    private final SymbolTable symbolTable = new SymbolTable();

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

        expressionVisitor = new ExpressionVisitor(operandStackTypeStack, symbolTable);

        ifVisitor = new IfVisitor(operandStackTypeStack, symbolTable, expressionVisitor, blockVisitor);
        literalVisitor = new LiteralVisitor(operandStackTypeStack);
        identifierVisitor = new IdentifierVisitor(operandStackTypeStack, symbolTable);
        castIntegerVisitor = new CastIntegerVisitor(operandStackTypeStack, symbolTable, expressionVisitor);
        assignmentVisitor = new AssignmentVisitor(operandStackTypeStack, symbolTable, expressionVisitor);
        numericOperatorVisitor = new NumericOperatorVisitor(operandStackTypeStack, symbolTable, expressionVisitor);
        booleanOperatorVisitor = new BooleanOperatorVisitor(operandStackTypeStack, symbolTable, ifVisitor);

        expressionVisitor
                .withIdentifierVisitor(identifierVisitor)
                .withLiteralVisitor(literalVisitor)
                .withNumericOperatorVisitor(numericOperatorVisitor)
                .withCastIntegerVisitor(castIntegerVisitor)
                .withBooleanOperatorVisitor(booleanOperatorVisitor);

        assertVisitor = new AssertVisitor(operandStackTypeStack, symbolTable, expressionVisitor);
        printVisitor = new PrintVisitor(operandStackTypeStack, expressionVisitor);
        loopVisitor = new LoopVisitor(operandStackTypeStack, symbolTable, expressionVisitor, blockVisitor);
    }

    public boolean canVisit(@NotNull INode ast) {
        return false;
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
