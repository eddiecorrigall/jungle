package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.compiler.visitor.IVisitor;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainMethodVisitor extends MethodVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(MainMethodVisitor.class.getSimpleName());

    public static final String MAIN_METHOD_NAME = "main";

    @NotNull
    private final IVisitor mainVisitor;

    @NotNull
    private final INode ast;

    public MainMethodVisitor(MethodVisitor mv, @NotNull IVisitor mainVisitor, @NotNull INode ast) {
        super(Opcodes.ASM5, mv);
        this.mainVisitor = mainVisitor;
        this.ast = ast;
    }

    @Override
    public void visitInsn(final int opcode) {
        if (opcode == Opcodes.RETURN) {
            logger.debug("emit instructions just before existing RETURN operation");
            mainVisitor.visit(this, ast);
        }
        super.visitInsn(opcode);
    }
}
