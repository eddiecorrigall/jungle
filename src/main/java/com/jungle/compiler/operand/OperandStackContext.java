package com.jungle.compiler.operand;

import com.jungle.compiler.symbol.SymbolEntry;
import com.jungle.compiler.symbol.SymbolTable;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class OperandStackContext {
    @NotNull
    private static final FileLogger logger = new FileLogger(OperandStackContext.class.getName());

    // Track the node type that goes onto the jvm stack to catch semantic errors before they are runtime errors
    // When the jvm instruction adds to the stack, add the node type to this compile-time stack
    // When the jvm instruction removes from the stack, remove the type from this compile-time stack
    @NotNull
    private final Stack<OperandType> operandTypeStack;

    @NotNull
    private final SymbolTable symbolTable;

    public OperandStackContext() {
        super();
        this.operandTypeStack = new Stack<>();
        this.symbolTable = new SymbolTable();
    }

    public boolean isEmpty() {
        return operandTypeStack.isEmpty();
    }

    @NotNull
    public OperandType peek() {
        return operandTypeStack.peek();
    }

    @NotNull
    public OperandType pop() {
        return operandTypeStack.pop();
    }

    public void push(@NotNull OperandType type) {
        operandTypeStack.push(type);
    }

    @NotNull
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void visitLoad(
            @NotNull MethodVisitor mv,
            @NotNull String variableName
    ) {
        // Get local variable index value and push variable value onto the operand stack
        logger.debug("visit load " + variableName);

        SymbolEntry entry = getSymbolTable().get(variableName);
        if (entry == null) {
            throw new Error("unknown variable name " + variableName);
        }

        mv.visitVarInsn(entry.getType().getLoadOpcode(), entry.getIndex());
        push(entry.getType());
    }

    public void visitStore(
            @NotNull MethodVisitor mv,
            @NotNull String variableName
    ) {
        // Pop value off the operand stack and set local variable index value
        logger.debug("visit store " + variableName);

        OperandType variableType = pop();

        SymbolEntry entry = getSymbolTable().get(variableName);
        boolean isNotDefined = entry == null;
        if (isNotDefined) {
            entry = getSymbolTable().set(variableName, variableType);
        } else {
            if (entry.getType() != variableType) {
                throw new Error(
                        "symbol type mismatch - expected symbol type " +
                                entry.getType() +
                                " but got " +
                                variableType
                );
            }
        }
        mv.visitVarInsn(entry.getType().getStoreOpcode(), entry.getIndex());
    }
}
