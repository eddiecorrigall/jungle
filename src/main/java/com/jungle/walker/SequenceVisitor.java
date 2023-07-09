package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class SequenceVisitor implements IVisitor {
    @NotNull
    private final MainVisitor mainVisitor;

    public SequenceVisitor(@NotNull final MainVisitor mainVisitor) {
        super();
        this.mainVisitor = mainVisitor;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return ast.getType() == NodeType.SEQUENCE;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        if (ast.getLeft() != null) {
            mainVisitor.visit(mv, ast.getLeft());
        }
        if (ast.getRight() != null) {
            mainVisitor.visit(mv, ast.getRight());
        }
    }
}
