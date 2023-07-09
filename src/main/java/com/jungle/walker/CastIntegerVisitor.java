package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class CastIntegerVisitor extends BaseVisitor {
    @NotNull
    private final IVisitor expressionVisitor;

    public CastIntegerVisitor(
            @NotNull final Stack<OperandStackType> operandStackTypeStack,
            @NotNull final SymbolTable symbolTable,
            @NotNull final IVisitor expressionVisitor
    ) {
        super(operandStackTypeStack, symbolTable);
        this.expressionVisitor = expressionVisitor;
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
        OperandStackType type = popOperandStackType();
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
        pushOperandStackType(OperandStackType.INTEGER);
    }
}
