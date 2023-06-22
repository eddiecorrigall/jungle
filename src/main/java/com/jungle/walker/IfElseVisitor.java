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

public class IfElseVisitor extends BaseVisitor {
    public IfElseVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super(operandStackTypeStack, symbolTable);
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public IfElseVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @NotNull
    private BlockVisitor blockVisitor;

    public IfElseVisitor withBlockVisitor(@NotNull BlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor;
        return this;
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit if " + ast);
        if (ast.getType() != NodeType.IF) {
            throw new Error("expected type if");
        }
        INode conditionNode = ast.getLeft();
        if (conditionNode == null) {
            throw new Error("expected if condition/expression");
        }
        expressionVisitor.visit(mv, conditionNode);
        if (operandStackTypeStack.peek() != OperandStackType.INTEGER) {
            throw new Error("expected if condition/expression to be integer");
        }
        INode bodyNode = ast.getRight();
        if (bodyNode == null) {
            throw new Error("expected if block");
        }
        boolean hasIfElseBlock = bodyNode.getType() == NodeType.IF_ELSE;
        Label continueLabel = new Label();
        mv.visitJumpInsn(Opcodes.IFNE, continueLabel);
        if (hasIfElseBlock) {
            INode ifBlockNode = ast.getRight().getLeft();
            if (ifBlockNode == null) {
                throw new Error("if block missing");
            }
            INode elseBlockNode = ast.getRight().getRight();
            if (elseBlockNode == null) {
                throw new Error("else block missing");
            }
            blockVisitor.visit(mv, ifBlockNode);
            mv.visitLabel(continueLabel);
            blockVisitor.visit(mv, elseBlockNode);
        } else {
            INode ifBlockNode = ast.getRight();
            blockVisitor.visit(mv, ifBlockNode);
            mv.visitLabel(continueLabel);
        }
    }
}
