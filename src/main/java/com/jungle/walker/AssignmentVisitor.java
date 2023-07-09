package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class AssignmentVisitor extends BaseVisitor {
    @NotNull
    private final IVisitor expressionVisitor;

    public AssignmentVisitor(
            @NotNull final Stack<OperandStackType> operandStackTypeStack,
            @NotNull final SymbolTable symbolTable,
            @NotNull final IVisitor expressionVisitor
    ) {
        super(operandStackTypeStack, symbolTable);
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.ASSIGN.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit assignment " + ast);

        if (!canVisit(ast)) {
            return;
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
        expressionVisitor.visit(mv, expressionNode);

        // store operand stack value and assign variable to index...
        visitStore(mv, variableName);
    }
}
