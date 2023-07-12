package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class IdentifierVisitor implements IVisitor {
    @Nullable
    private OperandStackContext operandStackContext;

    @NotNull
    private OperandStackContext getOperandStackContext() {
        if (operandStackContext == null) {
            operandStackContext = OperandStackContext.getInstance();
        }
        return operandStackContext;
    }

    private IdentifierVisitor(@Nullable OperandStackContext operandStackContext) {
        super();
        this.operandStackContext = operandStackContext;
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
        System.out.println("visit identifier " + ast);

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
