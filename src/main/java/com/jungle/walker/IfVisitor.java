package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class IfVisitor extends BaseVisitor {
    public IfVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
    ) {
        super(operandStackTypeStack, symbolTable);
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public IfVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @NotNull
    private BlockVisitor blockVisitor;

    public IfVisitor withBlockVisitor(@NotNull BlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor;
        return this;
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NodeType.IF.equals(node.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        visit(mv, ast, CompareTo.ZERO);
    }

    protected void visit(@NotNull MethodVisitor mv, @NotNull INode ast, @NotNull CompareTo compareTo) {
        System.out.println("visit if " + ast);

        if (!canVisit(ast)) {
            return;
        }

        /* Transform to inverse for efficient jump operation
         *
         * EXAMPLE:
         *          IFNE #end
         * ifBlock: "block"
         * end:     ...
         *
         * EXAMPLE:
         *            IFNE #elseBlock
         * ifBlock:   "block"
         *            GOTO #end
         * elseBlock: "block"
         * end:        ...
         */
        int jumpCode;
        switch (compareTo) {
            case ZERO: jumpCode = Opcodes.IFNE; break;
            case NOT_ZERO: jumpCode = Opcodes.IFEQ; break;
            case LESS_THAN_ZERO: jumpCode = Opcodes.IFGE; break;
            case LESS_OR_EQUAL_THAN_ZERO: jumpCode = Opcodes.IFGT; break;
            case GREATER_THAN_ZERO: jumpCode = Opcodes.IFLE; break;
            case GREATER_OR_EQUAL_THAN_ZERO: jumpCode = Opcodes.IFLT; break;
            default: throw new Error("unhandled if comparison");
        }

        INode conditionNode = ast.getLeft();
        if (conditionNode == null) {
            throw new Error("expected if condition/expression");
        }

        expressionVisitor.visit(mv, conditionNode);
        if (operandStackTypeStack.peek() != OperandStackType.INTEGER) {
            throw new Error("if condition/expression expected to be type integer");
        }

        INode bodyNode = ast.getRight();
        if (bodyNode == null) {
            throw new Error("expected if block");
        }

        Label ifBlockLabel = new Label();
        Label elseBlockLabel = new Label();
        Label endLabel = new Label();

        boolean hasIfElseBlock = bodyNode.getType() == NodeType.IF_ELSE;
        if (hasIfElseBlock) {
            INode ifBlockNode = bodyNode.getLeft();
            if (ifBlockNode == null) {
                throw new Error("if block missing");
            }

            INode elseBlockNode = bodyNode.getRight();
            if (elseBlockNode == null) {
                throw new Error("else block missing");
            }

            // if-condition
            mv.visitJumpInsn(jumpCode, elseBlockLabel);

            // if-block
            mv.visitLabel(ifBlockLabel);
            blockVisitor.visit(mv, ifBlockNode);
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);

            // else-block
            mv.visitLabel(elseBlockLabel);
            blockVisitor.visit(mv, elseBlockNode);

            // end
            mv.visitLabel(endLabel);
        } else {
            // if-condition
            mv.visitJumpInsn(jumpCode, endLabel);

            // if-block
            blockVisitor.visit(mv, bodyNode);

            // end
            mv.visitLabel(endLabel);
        }
    }

    public void visit(
            @NotNull MethodVisitor mv,
            @NotNull CompareTo compareTo,
            @NotNull INode conditionNode,
            @NotNull INode ifBlockNode,
            @Nullable INode elseBlockNode
    ) {
        if (elseBlockNode == null) {
            visit(
                    mv,
                    new Node(NodeType.IF)
                        .withLeft(conditionNode)
                        .withRight(new Node(NodeType.BLOCK).withLeft(ifBlockNode)),
                    compareTo
            );
        } else {
            visit(
                    mv,
                    new Node(NodeType.IF)
                        .withLeft(conditionNode)
                        .withRight(new Node(NodeType.IF_ELSE)
                                .withLeft(new Node(NodeType.BLOCK).withLeft(ifBlockNode))
                                .withRight(new Node(NodeType.BLOCK).withLeft(elseBlockNode))
                        ),
                    compareTo
            );
        }
    }

    public void visit(
            @NotNull MethodVisitor mv,
            @NotNull INode conditionNode,
            @NotNull INode ifBlockNode,
            @Nullable INode elseBlockNode
    ) {
        visit(mv, CompareTo.ZERO, conditionNode, ifBlockNode, elseBlockNode);
    }
}

enum CompareTo {
    ZERO,
    NOT_ZERO,
    LESS_THAN_ZERO,
    LESS_OR_EQUAL_THAN_ZERO,
    GREATER_THAN_ZERO,
    GREATER_OR_EQUAL_THAN_ZERO,
}
