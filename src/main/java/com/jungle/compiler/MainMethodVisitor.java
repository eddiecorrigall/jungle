package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.walker.IVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainMethodVisitor extends MethodVisitor {
    public static final String MAIN_METHOD_NAME = "main";

    @NotNull
    private final IVisitor mainVisitor;

    @Nullable
    private final INode ast;

    public MainMethodVisitor(MethodVisitor mv, @NotNull IVisitor mainVisitor, @Nullable INode ast) {
        super(Opcodes.ASM5, mv);
        this.mainVisitor = mainVisitor;
        this.ast = ast;
    }

    @Override
    public void visitInsn(final int opcode) {
        if (opcode == Opcodes.RETURN) {
            // Insert instructions just before existing return
            mainVisitor.visit(this, ast); // TODO: Should first argument be `this` or `mv`?
        }
        super.visitInsn(opcode);
    }
}
