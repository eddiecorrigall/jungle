package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class IdentifierVisitor extends BaseVisitor {
    public IdentifierVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super(operandStackTypeStack, symbolTable);
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return node.getType().equals(NodeType.IDENTIFIER);
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit identifier " + ast);

        if (!canVisit(ast)) {
            return;
        }

        String variableName = ast.getValue();
        if (variableName == null) {
            throw new Error("identifier missing name");
        }

        visitLoad(mv, variableName);
    }
}
