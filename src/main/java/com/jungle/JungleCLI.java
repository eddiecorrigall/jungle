package com.jungle;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.compiler.Compiler;
import com.jungle.scanner.Scanner;
import com.jungle.symbol.SymbolTable;
import com.jungle.walker.*;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.io.*;
import java.util.Stack;

public class JungleCLI implements IVisitor {

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

    public JungleCLI() {
        super();

        // Chicken before the egg problem...

        blockVisitor = new BlockVisitor(this);

        expressionVisitor = new ExpressionVisitor(operandStackTypeStack, symbolTable);

        ifVisitor = new IfVisitor(operandStackTypeStack, symbolTable);
        ifVisitor.withBlockVisitor(blockVisitor);
        ifVisitor.withExpressionVisitor(expressionVisitor);

        literalVisitor = new LiteralVisitor(operandStackTypeStack);
        identifierVisitor = new IdentifierVisitor(operandStackTypeStack, symbolTable);

        castIntegerVisitor = new CastIntegerVisitor(operandStackTypeStack, symbolTable);
        castIntegerVisitor.withExpressionVisitor(expressionVisitor);

        assignmentVisitor = new AssignmentVisitor(operandStackTypeStack, symbolTable);
        assignmentVisitor.withExpressionVisitor(expressionVisitor);

        numericOperatorVisitor = new NumericOperatorVisitor(operandStackTypeStack, symbolTable);
        numericOperatorVisitor.withExpressionVisitor(expressionVisitor);

        booleanOperatorVisitor = new BooleanOperatorVisitor(operandStackTypeStack, symbolTable);
        booleanOperatorVisitor.withExpressionVisitor(expressionVisitor);
        booleanOperatorVisitor.withIfElseVisitor(ifVisitor);

        expressionVisitor
                .withIdentifierVisitor(identifierVisitor)
                .withLiteralVisitor(literalVisitor)
                .withNumericOperatorVisitor(numericOperatorVisitor)
                .withCastIntegerVisitor(castIntegerVisitor)
                .withBooleanOperatorVisitor(booleanOperatorVisitor);

        assertVisitor = new AssertVisitor(operandStackTypeStack, symbolTable);
        assertVisitor.withExpressionVisitor(expressionVisitor);

        printVisitor = new PrintVisitor(operandStackTypeStack);
        printVisitor.withExpressionVisitor(expressionVisitor);

        loopVisitor = new LoopVisitor(operandStackTypeStack, symbolTable);
        loopVisitor.withExpressionVisitor(expressionVisitor);
        loopVisitor.withBlockVisitor(blockVisitor);
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

    public static void helpCommand(@NotNull Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jungle", options);
    }

    public static void scanCommand(@NotNull CommandLine cli) {
        BufferedReader standardInputReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        try {
            Scanner.tokenize(standardInputReader, writer, new Scanner());
        } catch (IOException e) {
            System.err.println("scanner - failed to tokenize");
        }
        try {
            writer.flush();
        } catch (IOException e) {
            System.err.println("scanner - failed to flush write buffer.");
        }
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("scanner - failed to close write buffer");
        }
        try {
            standardInputReader.close();
        } catch (IOException e) {
            System.err.println("scanner - failed to close read buffer");
        }
    }

    public static void compileCommand(@NotNull CommandLine cli) {
        String mainClassName = cli.getOptionValue("class-name");
        if (mainClassName == null) {
            System.err.println("compiler - main class name required");
            System.exit(1);
        }
        System.out.println("compiling to main class name " + mainClassName);
        BufferedReader standardInputReader = new BufferedReader(new InputStreamReader(System.in));
        INode ast;
        try {
            ast = Node.load(standardInputReader);
        } catch (IOException e) {
            System.err.println("failed to load AST - " + e.getMessage());
            System.exit(1);
            return;
        }
        Compiler compiler = new Compiler();
        compiler.compile(mainClassName, new JungleCLI(), ast);
    }

    public static void main(String[] args) throws FileNotFoundException {
        // INode ast = new Node(NodeType.LITERAL_STRING).withValue("Hello, world!\n");
        // INode ast = EXPRESSION_INT_FLOAT;
        // INode ast = EXPRESSION_IDENTIFIER;
        // INode ast = ASSIGNMENT;
        /*
        INode ast = new Node(NodeType.SEQUENCE)
                .withLeft(ASSIGNMENT)
                .withRight(new Node(NodeType.PRINT).withLeft(EXPRESSION_IDENTIFIER));
         */

        Options options = new Options();
        options.addOption("h", "help", false, "Show help options.");
        options.addOption("s", "scan", false, "Scan program from stdin. Output tokens.");
        options.addOption("c", "compile", false, "Compile program from stdin. Input format is AST. Output is bytecode.");
        options.addOption("o", "class-name", true, "Output class name.");

        CommandLineParser cliParser = new DefaultParser();
        CommandLine cli = null;
        try {
            cli = cliParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("cli parsing failed - " + e.getMessage());
            System.exit(1);
        }
        if (cli.hasOption("help")) {
            helpCommand(options);
            return;
        }
        if (cli.hasOption("scan")) {
            scanCommand(cli);
            return;
        }
        if (cli.hasOption("compile")) {
            compileCommand(cli);
            return;
        }

        System.err.println("Expected command line arguments.");
        System.exit(1);
    }
}
