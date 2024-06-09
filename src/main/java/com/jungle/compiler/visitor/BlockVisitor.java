package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class BlockVisitor extends AbstractClassPathVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(BlockVisitor.class.getName());

    @Nullable
    private MainVisitor mainVisitor;

    private MainVisitor getMainVisitor() {
        if (mainVisitor == null) {
            mainVisitor = new MainVisitor(getClassPath());
        }
        return mainVisitor;
    }

    public BlockVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.BLOCK.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit block " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected block");
        }

        if (ast.getLeft() == null) {
            throw new Error("block left AST must be defined");
        }

        if (ast.getRight() != null) {
            throw new Error("block right AST must NOT be defined");
        }

        getMainVisitor().visit(mv, ast.getLeft(), context);
    }
}
