package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolEntry;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class IdentifierVisitor implements IVisitor {
    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;
    @NotNull
    private final SymbolTable symbolTable;

    public IdentifierVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super();
        this.operandStackTypeStack = operandStackTypeStack;
        this.symbolTable = symbolTable;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @Nullable INode ast) {
        System.out.println("visit identifier " + ast);
        if (ast == null) {
            return;
        }
        if (ast.getType() != NodeType.IDENTIFIER) {
            throw new Error("expected identifier but got node " + ast);
        }
        String name = ast.getValue();
        SymbolEntry entry = symbolTable.get(name);
        if (entry == null) {
            throw new Error("unknown symbol " + name);
        }
        // ...
        switch (entry.getType()) {
            case BOOLEAN: operandStackTypeStack.push(OperandStackType.BOOLEAN); break;
            case CHARACTER: operandStackTypeStack.push(OperandStackType.CHARACTER); break;
            case INTEGER: operandStackTypeStack.push(OperandStackType.INTEGER); break;
            case FLOAT: operandStackTypeStack.push(OperandStackType.FLOAT); break;
            case OBJECT: operandStackTypeStack.push(OperandStackType.REFERENCE_OBJECT); break;
            default: throw new Error("cannot push operand stack type - unhandled symbol type");
        }
        // Load value from local variable index to put its value on the operand stack
        mv.visitVarInsn(entry.getType().getLoadOpcode(), entry.getIndex());
    }
}
