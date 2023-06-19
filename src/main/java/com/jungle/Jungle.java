package com.jungle;

import com.jungle.ast.INode;
import com.jungle.compiler.Compiler;
import com.jungle.symbol.SymbolTable;
import com.jungle.walker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

import static com.jungle.examples.Examples.EXPRESSION_INT_FLOAT;

public class Jungle implements IVisitor {
    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {
        // Track the node type that goes onto the jvm stack to catch semantic errors before they are runtime errors
        // When the jvm instruction adds to the stack, add the node type to this compile-time stack
        // When the jvm instruction removes from the stack, remove the type from this compile-time stack
        final Stack<OperandStackType> operandStackTypeStack = new Stack<>();

        final SymbolTable symbolTable = new SymbolTable();

        // Chicken before the egg problem...
        final ExpressionVisitor expressionVisitor = new ExpressionVisitor();

        final LiteralVisitor literalVisitor = new LiteralVisitor(operandStackTypeStack);
        final IdentifierVisitor identifierVisitor = new IdentifierVisitor(operandStackTypeStack, symbolTable);

        final CastIntegerVisitor castIntegerVisitor = new CastIntegerVisitor(operandStackTypeStack, symbolTable);
        castIntegerVisitor.withExpressionVisitor(expressionVisitor);

        final AssignmentVisitor assignmentVisitor = new AssignmentVisitor(operandStackTypeStack, symbolTable);
        assignmentVisitor.withExpressionVisitor(expressionVisitor);

        final BinaryOperatorVisitor binaryOperatorVisitor = new BinaryOperatorVisitor(operandStackTypeStack);
        binaryOperatorVisitor.withExpressionVisitor(expressionVisitor);

        expressionVisitor
                .withIdentifierVisitor(identifierVisitor)
                .withLiteralVisitor(literalVisitor)
                .withBinaryOperatorVisitor(binaryOperatorVisitor)
                .withCastIntegerVisitor(castIntegerVisitor);

        final PrintVisitor printVisitor = new PrintVisitor(operandStackTypeStack);
        printVisitor.withExpressionVisitor(expressionVisitor);

        // ...

        printVisitor.visit(mv, ast);
    }

    public static void main(String[] args) {
        INode ast = EXPRESSION_INT_FLOAT;
        // INode ast = new Node(NodeType.LITERAL_STRING).withValue("Hello, world!\n");
        // INode ast = ASSIGNMENT;
        Compiler compiler = new Compiler();
        compiler.compile(new Jungle(), ast);
    }
}
