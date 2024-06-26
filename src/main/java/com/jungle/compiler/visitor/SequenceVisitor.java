package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class SequenceVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(SequenceVisitor.class.getSimpleName());

    @Nullable
    private MainVisitor mainVisitor;

    private MainVisitor getMainVisitor() {
        if (mainVisitor == null) {
            mainVisitor = new MainVisitor(getCompilerOptions());
        }
        return mainVisitor;
    }

    public SequenceVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return ast.getType() == NodeType.SEQUENCE;
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit sequence " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected sequence");
        }

        if (ast.getLeft() != null) {
            getMainVisitor().visit(mv, ast.getLeft(), context);
        }

        if (ast.getRight() != null) {
            getMainVisitor().visit(mv, ast.getRight(), context);
        }
    }
}
