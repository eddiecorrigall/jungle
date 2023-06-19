package com.jungle;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.compiler.Compiler;
import com.jungle.symbol.SymbolEntry;
import com.jungle.symbol.SymbolTable;
import com.jungle.symbol.SymbolType;
import com.jungle.walker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

import static com.jungle.examples.Examples.*;

public class Jungle implements IVisitor {

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
    final BinaryOperatorVisitor binaryOperatorVisitor;

    @NotNull
    final PrintVisitor printVisitor;

    // endregion

    public Jungle() {
        super();

        // Chicken before the egg problem...

        expressionVisitor = new ExpressionVisitor();

        literalVisitor = new LiteralVisitor(operandStackTypeStack);
        identifierVisitor = new IdentifierVisitor(operandStackTypeStack, symbolTable);

        castIntegerVisitor = new CastIntegerVisitor(operandStackTypeStack, symbolTable);
        castIntegerVisitor.withExpressionVisitor(expressionVisitor);

        assignmentVisitor = new AssignmentVisitor(operandStackTypeStack, symbolTable);
        assignmentVisitor.withExpressionVisitor(expressionVisitor);

        binaryOperatorVisitor = new BinaryOperatorVisitor(operandStackTypeStack);
        binaryOperatorVisitor.withExpressionVisitor(expressionVisitor);

        expressionVisitor
                .withIdentifierVisitor(identifierVisitor)
                .withLiteralVisitor(literalVisitor)
                .withBinaryOperatorVisitor(binaryOperatorVisitor)
                .withCastIntegerVisitor(castIntegerVisitor);

        printVisitor = new PrintVisitor(operandStackTypeStack);
        printVisitor.withExpressionVisitor(expressionVisitor);
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {
        /*
        // int x = 3;
        mv.visitInsn(Opcodes.ICONST_3);
        SymbolEntry entry = symbolTable.set("x", SymbolType.INTEGER);
        mv.visitVarInsn(Opcodes.ISTORE, entry.getIndex());
        */

        if (ast == null) {
            return;
        }

        if (ast.getType() == NodeType.SEQUENCE) {
            visit(mv, ast.getLeft());
            visit(mv, ast.getRight());
            return;
        }

        if (ast.getType() == NodeType.BLOCK) {
            // TODO: handle scope?
            visit(mv, ast.getLeft());
            return;
        }

        if (ast.getType() == NodeType.ASSIGN) {
            assignmentVisitor.visit(mv, ast);
            return;
        }

        if (NodeType.BINARY_OPERATORS.contains(ast.getType())) {
            binaryOperatorVisitor.visit(mv, ast);
            return;
        }

        if (ast.getType() == NodeType.PRINT) {
            printVisitor.visit(mv, ast);
            return;
        }

        throw new Error("unexpected node " + ast);
    }

    public static void main(String[] args) {
        // INode ast = new Node(NodeType.LITERAL_STRING).withValue("Hello, world!\n");
        // INode ast = EXPRESSION_INT_FLOAT;
        // INode ast = EXPRESSION_IDENTIFIER;
        // INode ast = ASSIGNMENT;

        INode ast = new Node(NodeType.SEQUENCE)
                .withLeft(ASSIGNMENT)
                .withRight(new Node(NodeType.PRINT).withLeft(EXPRESSION_IDENTIFIER));
        Compiler compiler = new Compiler();
        compiler.compile(new Jungle(), ast);
    }
}
