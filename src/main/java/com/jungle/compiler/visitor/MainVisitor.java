package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class MainVisitor extends AbstractClassPathVisitor {

    @NotNull
    private static final FileLogger logger = new FileLogger(MainVisitor.class.getSimpleName());

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
    private AssignmentVisitor assignmentVisitor;

    @NotNull
    private AssignmentVisitor getAssignmentVisitor() {
        if (assignmentVisitor == null) {
            assignmentVisitor = new AssignmentVisitor(getClassPath());
        }
        return assignmentVisitor;
    }

    @Nullable
    private BlockVisitor blockVisitor;

    @NotNull
    private BlockVisitor getBlockVisitor() {
        if (blockVisitor == null) {
            blockVisitor = new BlockVisitor(getClassPath());
        }
        return blockVisitor;
    }

    @Nullable
    private AssertVisitor assertVisitor;

    @NotNull
    private AssertVisitor getAssertVisitor() {
        if (assertVisitor == null) {
            assertVisitor = new AssertVisitor(getClassPath());
        }
        return assertVisitor;
    }

    @Nullable
    private PrintVisitor printVisitor;

    @NotNull
    private PrintVisitor getPrintVisitor() {
        if (printVisitor == null) {
            printVisitor = new PrintVisitor(getClassPath());
        }
        return printVisitor;
    }

    @Nullable
    private IfVisitor ifVisitor;

    @NotNull
    private IfVisitor getIfVisitor() {
        if (ifVisitor == null) {
            ifVisitor = new IfVisitor(getClassPath());
        }
        return ifVisitor;
    }

    @Nullable
    private LoopVisitor loopVisitor;

    @NotNull
    private LoopVisitor getLoopVisitor() {
        if (loopVisitor == null) {
            loopVisitor = new LoopVisitor(getClassPath());
        }
        return loopVisitor;
    }

    @Nullable
    private MultitaskVisitor multitaskVisitor;

    @NotNull
    private MultitaskVisitor getMultitaskVisitor() {
        if (multitaskVisitor == null) {
            multitaskVisitor = new MultitaskVisitor(getClassPath());
        }
        return multitaskVisitor;
    }

    @Nullable
    private SequenceVisitor sequenceVisitor;

    @NotNull
    private SequenceVisitor getSequenceVisitor() {
        if (sequenceVisitor == null) {
            sequenceVisitor = new SequenceVisitor(getClassPath());
        }
        return sequenceVisitor;
    }

    // endregion

    public MainVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    public boolean canVisit(@NotNull INode ast) {
        return true; // TODO
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit main " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected main");
        }

        if (getSequenceVisitor().canVisit(ast)) {
            getSequenceVisitor().visit(mv, ast, context);
            return;
        }

        if (getBlockVisitor().canVisit(ast)) {
            getBlockVisitor().visit(mv, ast, context);
            return;
        }

        if (getLiteralVisitor().canVisit(ast)) {
            getLiteralVisitor().visit(mv, ast, context);
            return;
        }

        if (getAssignmentVisitor().canVisit(ast)) {
            getAssignmentVisitor().visit(mv, ast, context);
            return;
        }

        if (getAssertVisitor().canVisit(ast)) {
            getAssertVisitor().visit(mv, ast, context);
            return;
        }

        if (getPrintVisitor().canVisit(ast)) {
            getPrintVisitor().visit(mv, ast, context);
            return;
        }

        if (getIfVisitor().canVisit(ast)) {
            getIfVisitor().visit(mv, ast, context);
            return;
        }

        if (getLoopVisitor().canVisit(ast)) {
            getLoopVisitor().visit(mv, ast, context);
            return;
        }

        if (getMultitaskVisitor().canVisit(ast)) {
            getMultitaskVisitor().visit(mv, ast, context);
            return;
        }

        throw new Error("unexpected node " + ast);
    }
}
