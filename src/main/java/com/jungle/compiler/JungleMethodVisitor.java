package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.visitor.IVisitor;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JungleMethodVisitor extends MethodVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(JungleMethodVisitor.class.getSimpleName());

    @NotNull
    private final IVisitor mainVisitor;

    @NotNull
    private final INode ast;

    @NotNull
    private final OperandStackContext context;

    public JungleMethodVisitor(MethodVisitor mv, @NotNull IVisitor mainVisitor, @NotNull INode ast) {
        super(Opcodes.ASM5, mv);
        this.mainVisitor = mainVisitor;
        this.ast = ast;
        this.context = new OperandStackContext();
    }

    @Override
    public void visitInsn(final int opcode) {
        if (opcode == Opcodes.RETURN) {
            logger.debug("emit instructions just before existing RETURN operation");
            mainVisitor.visit(this, ast, context);
        }
        super.visitInsn(opcode);
    }
}
