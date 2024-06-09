package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.compiler.operand.OperandStackContext;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public interface IVisitor {
    boolean canVisit(@NotNull INode ast);

    void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    );
}
