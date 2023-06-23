package com.jungle.walker;

import com.jungle.ast.INode;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public interface IVisitor {
    boolean canVisit(@NotNull INode ast);

    void visit(@NotNull MethodVisitor mv, @NotNull INode ast);
}
