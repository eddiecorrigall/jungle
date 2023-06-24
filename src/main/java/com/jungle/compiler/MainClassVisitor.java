package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.walker.IVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainClassVisitor extends ClassVisitor {
    @NotNull
    private final IVisitor mainVisitor;

    @Nullable
    private final INode ast;

    public MainClassVisitor(ClassVisitor classVisitor, @NotNull IVisitor mainVisitor, @Nullable INode ast) {
        super(Opcodes.ASM5, classVisitor);
        this.mainVisitor = mainVisitor;
        this.ast = ast;
    }

    @Override
    public MethodVisitor visitMethod(int flags, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(flags, name, desc, signature, exceptions);
        boolean isMainMethod = MainMethodVisitor.MAIN_METHOD_NAME.equals(name);
        if (isMainMethod) {
            // Emit custom instructions
            return new MainMethodVisitor(mv, mainVisitor, ast);
        }
        return mv;
    }
}
