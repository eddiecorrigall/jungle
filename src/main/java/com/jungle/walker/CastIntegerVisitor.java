package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

import static org.objectweb.asm.Opcodes.F2I;

public class CastIntegerVisitor implements IVisitor {

    @NotNull
    private final Stack<OperandStackType> operandStackTypeStack;

    @NotNull
    private final SymbolTable symbolTable;

    public CastIntegerVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super();
        this.operandStackTypeStack = operandStackTypeStack;
        this.symbolTable = symbolTable;
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public CastIntegerVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit cast integer " + ast);

        if (ast.getType() != NodeType.CAST_INTEGER) {
            throw new Error("expected cast integer");
        }

        if (ast.getLeft() == null) {
            throw new Error("cast integer missing expression");
        }

        expressionVisitor.visit(mv, ast.getLeft());
        OperandStackType type = operandStackTypeStack.pop();
        switch (type) {
            case INTEGER: {
                System.out.println("WARN: value is already an integer");
            } break;
            case FLOAT: {
                mv.visitInsn(F2I);
            } break;
            default: {
                throw new Error("integer cast not supported for " + ast);
            }
        }
        operandStackTypeStack.push(OperandStackType.INTEGER);
    }
}
