package com.jungle.walker;

import com.jungle.ast.INode;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public interface IVisitor {
    void visit(@NotNull MethodVisitor mv, @NotNull INode ast);
}
