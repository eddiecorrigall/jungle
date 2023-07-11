package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.operand.OperandStackContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class AssignmentVisitor implements IVisitor {
    @Nullable
    private OperandStackContext operandStackContext;

    private OperandStackContext getOperandStackContext() {
        if (operandStackContext == null) {
            operandStackContext = OperandStackContext.getInstance();
        }
        return operandStackContext;
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor();
        }
        return expressionVisitor;
    }

    private AssignmentVisitor(
            @Nullable OperandStackContext operandStackContext,
            @Nullable ExpressionVisitor expressionVisitor
    ) {
        super();
        this.operandStackContext = operandStackContext;
        this.expressionVisitor = expressionVisitor;
    }

    public AssignmentVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.ASSIGN.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit assignment " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected assignment");
        }

        INode identifierNode = ast.getLeft();
        if (identifierNode == null || identifierNode.getType() != NodeType.IDENTIFIER) {
            throw new Error("expected left AST to be identifier");
        }

        String variableName = identifierNode.getRawValue();
        if (variableName == null) {
            throw new Error("identifier missing variable name");
        }

        INode expressionNode = ast.getRight();
        if (expressionNode == null) {
            throw new Error("expected right AST to be expression");
        }

        // evaluate expression to have value pushed onto operand stack...
        getExpressionVisitor().visit(mv, expressionNode);

        // store operand stack value and assign variable to index...
        getOperandStackContext().visitStore(mv, variableName);
    }
}
