package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class SequenceVisitor implements IVisitor {
    @Nullable
    private MainVisitor mainVisitor;

    private MainVisitor getMainVisitor() {
        if (mainVisitor == null) {
            mainVisitor = new MainVisitor();
        }
        return mainVisitor;
    }

    private SequenceVisitor(@NotNull final MainVisitor mainVisitor) {
        super();
        this.mainVisitor = mainVisitor;
    }

    public SequenceVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return ast.getType() == NodeType.SEQUENCE;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit sequence " + ast);

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
