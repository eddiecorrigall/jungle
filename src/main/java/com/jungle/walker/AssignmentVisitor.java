package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolEntry;
import com.jungle.symbol.SymbolTable;
import com.jungle.symbol.SymbolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class AssignmentVisitor implements IVisitor {
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;
    @NotNull
    private final SymbolTable symbolTable;
    @Nullable
    private IVisitor expressionVisitor;

    public AssignmentVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super();
        this.operandStackTypeStack = operandStackTypeStack;
        this.symbolTable = symbolTable;
    }

    @NotNull
    public AssignmentVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {
        // assignment = identifier "=" expression
        if (ast == null) {
            return;
        }
        if (ast.getType() != NodeType.ASSIGN) {
            throw new Error("expected assignment but got node " + ast);
        }
        if (ast.getLeft() == null || ast.getLeft().getType() != NodeType.IDENTIFIER) {
            throw new Error("expected left ast to be identifier");
        }
        // push expression value(s) onto operand stack...
        expressionVisitor.visit(mv, ast.getRight());
        OperandStackType resolvedOperandStackType = operandStackTypeStack.pop();
        SymbolType resolvedSymbolType = resolvedOperandStackType.getSymbolType();
        String name = ast.getLeft().getValue();
        SymbolEntry entry = symbolTable.get(name);
        boolean isNotDefined = entry == null;
        if (isNotDefined) {
            entry = symbolTable.set(name, resolvedSymbolType);
        } else {
            if (entry.getType() != resolvedSymbolType) {
                throw new Error(
                        "symbol type mismatch - expected symbol type " +
                        entry.getType() +
                        " but got " +
                        resolvedSymbolType
                );
            }
        }
        mv.visitVarInsn(entry.getType().getStoreOpcode(), entry.getIndex());
    }
}
