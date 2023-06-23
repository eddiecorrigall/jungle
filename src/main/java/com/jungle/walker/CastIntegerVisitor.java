package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class CastIntegerVisitor extends BaseVisitor {
    public CastIntegerVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super(operandStackTypeStack, symbolTable);
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public CastIntegerVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.CAST_INTEGER.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit cast integer " + ast);

        if (!canVisit(ast)) {
            return;
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
                mv.visitInsn(Opcodes.F2I);
            } break;
            default: {
                throw new Error("integer cast not supported for " + ast);
            }
        }
        operandStackTypeStack.push(OperandStackType.INTEGER);
    }
}
