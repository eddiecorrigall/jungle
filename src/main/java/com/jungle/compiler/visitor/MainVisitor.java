package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class MainVisitor implements IVisitor {
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
            assignmentVisitor = new AssignmentVisitor();
        }
        return assignmentVisitor;
    }

    @Nullable
    private BlockVisitor blockVisitor;

    @NotNull
    private BlockVisitor getBlockVisitor() {
        if (blockVisitor == null) {
            blockVisitor = new BlockVisitor();
        }
        return blockVisitor;
    }

    @Nullable
    private AssertVisitor assertVisitor;

    @NotNull
    private AssertVisitor getAssertVisitor() {
        if (assertVisitor == null) {
            assertVisitor = new AssertVisitor();
        }
        return assertVisitor;
    }

    @Nullable
    private PrintVisitor printVisitor;

    @NotNull
    private PrintVisitor getPrintVisitor() {
        if (printVisitor == null) {
            printVisitor = new PrintVisitor();
        }
        return printVisitor;
    }

    @Nullable
    private IfVisitor ifVisitor;

    @NotNull
    private IfVisitor getIfVisitor() {
        if (ifVisitor == null) {
            ifVisitor = new IfVisitor();
        }
        return ifVisitor;
    }

    @Nullable
    private LoopVisitor loopVisitor;

    @NotNull
    private LoopVisitor getLoopVisitor() {
        if (loopVisitor == null) {
            loopVisitor = new LoopVisitor();
        }
        return loopVisitor;
    }

    @Nullable
    private MultitaskVisitor multitaskVisitor;

    @NotNull
    private MultitaskVisitor getMultitaskVisitor() {
        if (multitaskVisitor == null) {
            multitaskVisitor = new MultitaskVisitor();
        }
        return multitaskVisitor;
    }

    @Nullable
    private SequenceVisitor sequenceVisitor;

    @NotNull
    private SequenceVisitor getSequenceVisitor() {
        if (sequenceVisitor == null) {
            sequenceVisitor = new SequenceVisitor();
        }
        return sequenceVisitor;
    }

    // endregion

    private MainVisitor(
            @NotNull final LiteralVisitor literalVisitor,
            @NotNull final AssignmentVisitor assignmentVisitor,
            @NotNull final BlockVisitor blockVisitor,
            @NotNull final AssertVisitor assertVisitor,
            @NotNull final PrintVisitor printVisitor,
            @NotNull final IfVisitor ifVisitor,
            @NotNull final LoopVisitor loopVisitor,
            @NotNull final SequenceVisitor sequenceVisitor
    ) {
        super();
        this.literalVisitor = literalVisitor;
        this.assignmentVisitor = assignmentVisitor;
        this.blockVisitor = blockVisitor;
        this.assertVisitor = assertVisitor;
        this.printVisitor = printVisitor;
        this.ifVisitor = ifVisitor;
        this.loopVisitor = loopVisitor;
        this.sequenceVisitor = sequenceVisitor;
    }

    public MainVisitor() {
        super();
    }

    public boolean canVisit(@NotNull INode ast) {
        return true; // TODO
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit main " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected main");
        }

        if (getSequenceVisitor().canVisit(ast)) {
            getSequenceVisitor().visit(mv, ast);
            return;
        }

        if (getBlockVisitor().canVisit(ast)) {
            getBlockVisitor().visit(mv, ast);
            return;
        }

        if (getLiteralVisitor().canVisit(ast)) {
            getLiteralVisitor().visit(mv, ast);
            return;
        }

        if (getAssignmentVisitor().canVisit(ast)) {
            getAssignmentVisitor().visit(mv, ast);
            return;
        }

        if (getAssertVisitor().canVisit(ast)) {
            getAssertVisitor().visit(mv, ast);
            return;
        }

        if (getPrintVisitor().canVisit(ast)) {
            getPrintVisitor().visit(mv, ast);
            return;
        }

        if (getIfVisitor().canVisit(ast)) {
            getIfVisitor().visit(mv, ast);
            return;
        }

        if (getLoopVisitor().canVisit(ast)) {
            getLoopVisitor().visit(mv, ast);
            return;
        }

        if (getMultitaskVisitor().canVisit(ast)) {
            getMultitaskVisitor().visit(mv, ast);
            return;
        }

        throw new Error("unexpected node " + ast);
    }
}
