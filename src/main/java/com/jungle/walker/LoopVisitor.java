package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class LoopVisitor extends BaseVisitor {
    public LoopVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super(operandStackTypeStack, symbolTable);
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public LoopVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Nullable
    private BlockVisitor blockVisitor;

    public LoopVisitor withBlockVisitor(@NotNull BlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor;
        return this;
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.LOOP.equals(ast.getType());
    }
    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        /*
         loop: IFEQ #end
               "block"
               GOTO #loop
         end:  ...
         */
        System.out.println("visit loop " + ast);

        if (!canVisit(ast)) {
            return;
        }

        if (ast.getLeft() == null) {
            throw new Error("loop missing expression");
        }

        if (ast.getRight() == null) {
            throw new Error("loop missing body");
        }

        Label loopLabel = new Label();
        Label endLabel = new Label();

        // loop-condition
        mv.visitLabel(loopLabel);
        expressionVisitor.visit(mv, ast.getLeft());
        if (operandStackTypeStack.peek() != OperandStackType.INTEGER) {
            throw new Error("loop condition/expression expected to be type integer");
        }
        mv.visitJumpInsn(Opcodes.IFNE, endLabel);

        // loop-block
        blockVisitor.visit(mv, ast.getRight());
        mv.visitJumpInsn(Opcodes.GOTO, loopLabel);

        // end
        mv.visitLabel(endLabel);
    }
}
