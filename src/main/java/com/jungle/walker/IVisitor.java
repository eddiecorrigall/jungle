package com.jungle.walker;

import com.jungle.ast.INode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public interface IVisitor {
    void visit(@NotNull MethodVisitor mv, @Nullable INode ast);
}
