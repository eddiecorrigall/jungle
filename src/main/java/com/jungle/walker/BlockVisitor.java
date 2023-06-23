package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class BlockVisitor implements IVisitor {
    @NotNull
    final IVisitor mainVisitor;

    public BlockVisitor(@NotNull IVisitor mainVisitor) {
        super();
        this.mainVisitor = mainVisitor;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.BLOCK.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit block " + ast);

        if (!canVisit(ast)) {
            return;
        }

        if (ast.getLeft() == null) {
            throw new Error("block left AST must be defined");
        }

        if (ast.getRight() != null) {
            throw new Error("block right AST must NOT be defined");
        }

        mainVisitor.visit(mv, ast.getLeft());
    }
}
