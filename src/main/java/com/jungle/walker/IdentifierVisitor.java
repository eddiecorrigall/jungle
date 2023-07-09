package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class IdentifierVisitor implements IVisitor {
    @NotNull
    private final OperandStackContext operandStackContext;

    public IdentifierVisitor(@NotNull final OperandStackContext operandStackContext) {
        super();
        this.operandStackContext = operandStackContext;
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return node.getType().equals(NodeType.IDENTIFIER);
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit identifier " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected identifier");
        }

        String variableName = ast.getRawValue();
        if (variableName == null) {
            throw new Error("identifier missing name");
        }

        operandStackContext.visitLoad(mv, variableName);
    }
}
