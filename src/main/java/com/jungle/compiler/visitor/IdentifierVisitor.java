package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class IdentifierVisitor implements IVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(IdentifierVisitor.class.getName());

    @Nullable
    private OperandStackContext operandStackContext;

    @NotNull
    private OperandStackContext getOperandStackContext() {
        if (operandStackContext == null) {
            operandStackContext = OperandStackContext.getInstance();
        }
        return operandStackContext;
    }

    public IdentifierVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return node.getType().equals(NodeType.IDENTIFIER);
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        logger.debug("visit identifier " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected identifier");
        }

        String variableName = ast.getRawValue();
        if (variableName == null) {
            throw new Error("identifier missing name");
        }

        getOperandStackContext().visitLoad(mv, variableName);
    }
}
