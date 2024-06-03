package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class SequenceVisitor extends AbstractClassPathVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(SequenceVisitor.class.getSimpleName());

    @Nullable
    private MainVisitor mainVisitor;

    private MainVisitor getMainVisitor() {
        if (mainVisitor == null) {
            mainVisitor = new MainVisitor(getClassPath());
        }
        return mainVisitor;
    }

    public SequenceVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return ast.getType() == NodeType.SEQUENCE;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        logger.debug("visit sequence " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected sequence");
        }

        if (ast.getLeft() != null) {
            getMainVisitor().visit(mv, ast.getLeft());
        }

        if (ast.getRight() != null) {
            getMainVisitor().visit(mv, ast.getRight());
        }
    }
}
