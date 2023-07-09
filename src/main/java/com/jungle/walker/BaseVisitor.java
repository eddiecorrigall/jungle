package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.symbol.SymbolEntry;
import com.jungle.symbol.SymbolTable;
import com.jungle.symbol.SymbolType;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public abstract class BaseVisitor implements IVisitor {
    @NotNull
    protected final Stack<OperandStackType> operandStackTypeStack;

    @NotNull
    private final SymbolTable symbolTable;
    public BaseVisitor(
            @NotNull final Stack<OperandStackType> operandStackTypeStack,
            @NotNull final SymbolTable symbolTable
    ) {
        super();
        this.operandStackTypeStack = operandStackTypeStack;
        this.symbolTable = symbolTable;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void visitLoad(
            @NotNull MethodVisitor mv,
            @NotNull String variableName
    ) {
        // Get local variable index value and push on the operand stack
        System.out.println("visit load " + variableName);

        SymbolEntry entry = symbolTable.get(variableName);
        if (entry == null) {
            throw new Error("unknown variable name " + variableName);
        }

        mv.visitVarInsn(entry.getType().getLoadOpcode(), entry.getIndex());

        switch (entry.getType()) {
            case BOOLEAN: operandStackTypeStack.push(OperandStackType.BOOLEAN); break;
            case CHARACTER: operandStackTypeStack.push(OperandStackType.CHARACTER); break;
            case INTEGER: operandStackTypeStack.push(OperandStackType.INTEGER); break;
            case FLOAT: operandStackTypeStack.push(OperandStackType.FLOAT); break;
            case OBJECT: operandStackTypeStack.push(OperandStackType.REFERENCE_OBJECT); break;
            default: throw new Error("cannot push operand stack type - unhandled symbol type");
        }
    }

    public void visitStore(
            @NotNull MethodVisitor mv,
            @NotNull String variableName
    ) {
        // Pop value on the operand stack and set local variable index value
        System.out.println("visit store " + variableName);

        OperandStackType variableType = operandStackTypeStack.pop();
        SymbolType variableSymbolType = variableType.getSymbolType();

        SymbolEntry entry = symbolTable.get(variableName);
        boolean isNotDefined = entry == null;
        if (isNotDefined) {
            entry = symbolTable.set(variableName, variableSymbolType);
        } else {
            if (entry.getType() != variableSymbolType) {
                throw new Error(
                        "symbol type mismatch - expected symbol type " +
                                entry.getType() +
                                " but got " +
                                variableSymbolType
                );
            }
        }
        mv.visitVarInsn(entry.getType().getStoreOpcode(), entry.getIndex());
    }
}
