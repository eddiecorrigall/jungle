package com.jungle.compiler.operand;

import com.jungle.compiler.symbol.SymbolEntry;
import com.jungle.compiler.symbol.SymbolTable;
import com.jungle.compiler.symbol.SymbolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class OperandStackContext {
    // region Singleton Factory
    @Nullable
    private static OperandStackContext operandStackContext = null;
    @NotNull
    public static OperandStackContext getInstance() {
        if (operandStackContext == null) {
            operandStackContext = new OperandStackContext();
        }
        return operandStackContext;
    }
    // endregion

    // Track the node type that goes onto the jvm stack to catch semantic errors before they are runtime errors
    // When the jvm instruction adds to the stack, add the node type to this compile-time stack
    // When the jvm instruction removes from the stack, remove the type from this compile-time stack
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;
    @NotNull
    private final SymbolTable symbolTable;

    public OperandStackContext() {
        super();
        this.operandStackTypeStack = new Stack<>();
        this.symbolTable = new SymbolTable();
    }

    @NotNull
    public OperandStackType peek() {
        return operandStackTypeStack.peek();
    }

    @NotNull
    public OperandStackType pop() {
        return operandStackTypeStack.pop();
    }

    public void push(@NotNull OperandStackType type) {
        operandStackTypeStack.push(type);
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
        System.out.println("visit load " + variableName);

        SymbolEntry entry = getSymbolTable().get(variableName);
        if (entry == null) {
            throw new Error("unknown variable name " + variableName);
        }

        mv.visitVarInsn(entry.getType().getLoadOpcode(), entry.getIndex());

        switch (entry.getType()) {
            case BOOLEAN: push(OperandStackType.BOOLEAN); break;
            case CHARACTER: push(OperandStackType.CHARACTER); break;
            case INTEGER: push(OperandStackType.INTEGER); break;
            case FLOAT: push(OperandStackType.FLOAT); break;
            case OBJECT: push(OperandStackType.REFERENCE_OBJECT); break;
            default: throw new Error("cannot push operand stack type - unhandled symbol type");
        }
    }

    public void visitStore(
            @NotNull MethodVisitor mv,
            @NotNull String variableName
    ) {
        // Pop value off the operand stack and set local variable index value
        System.out.println("visit store " + variableName);

        OperandStackType variableType = pop();
        SymbolType variableSymbolType = variableType.getSymbolType();

        SymbolEntry entry = getSymbolTable().get(variableName);
        boolean isNotDefined = entry == null;
        if (isNotDefined) {
            entry = getSymbolTable().set(variableName, variableSymbolType);
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
