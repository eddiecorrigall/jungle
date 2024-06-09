package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.compiler.visitor.IVisitor;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JungleClassVisitor extends ClassVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(JungleClassVisitor.class.getSimpleName());

    @NotNull
    private final String targetMethodName;

    @NotNull
    private final IVisitor mainVisitor;

    @NotNull
    private final INode ast;

    public JungleClassVisitor(
        @NotNull String targetMethodName,
        @NotNull ClassVisitor classVisitor,
        @NotNull IVisitor mainVisitor,
        @NotNull INode ast
    ) {
        super(Opcodes.ASM5, classVisitor);
        this.targetMethodName = targetMethodName;
        this.mainVisitor = mainVisitor;
        this.ast = ast;
    }

    @Override
    public MethodVisitor visitMethod(int flags, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(flags, name, desc, signature, exceptions);
        boolean isTargetMethod = targetMethodName.equals(name);
        if (isTargetMethod) {
            logger.debug("emit custom instructions into class method");
            return new JungleMethodVisitor(mv, mainVisitor, ast);
        }
        return mv;
    }
}
