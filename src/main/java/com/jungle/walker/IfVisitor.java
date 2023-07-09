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
    @NotNull
    private final IVisitor expressionVisitor;

    @NotNull
    private final IVisitor blockVisitor;

    public IfVisitor(
            @NotNull final Stack<OperandStackType> operandStackTypeStack,
            @NotNull final SymbolTable symbolTable,
            @NotNull final IVisitor expressionVisitor,
            @NotNull final IVisitor blockVisitor
    ) {
        super(operandStackTypeStack, symbolTable);
        this.expressionVisitor = expressionVisitor;
        this.blockVisitor = blockVisitor;
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NodeType.IF.equals(node.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        visit(mv, CompareTo.ZERO, ast);
    }

    protected void visit(@NotNull MethodVisitor mv, @NotNull CompareTo compareTo, @NotNull INode ast) {
        System.out.println("visit if " + ast);

        if (!canVisit(ast)) {
            return;
        }

        /* Transform to inverse for efficient jump operation
         *
         * EXAMPLE:
         *          IFEQ #end
         * ifBlock: "block"
         * end:     ...
         *
         * EXAMPLE:
         *            IFEQ #elseBlock
         * ifBlock:   "block"
         *            GOTO #end
         * elseBlock: "block"
         * end:        ...
         */
        int jumpCode;
        switch (compareTo) {
            case ZERO: jumpCode = Opcodes.IFEQ; break;
            case NONZERO: jumpCode = Opcodes.IFNE; break;
            case LESS_THAN_ZERO: jumpCode = Opcodes.IFLT; break;
            case LESS_OR_EQUAL_THAN_ZERO: jumpCode = Opcodes.IFLE; break;
            case GREATER_THAN_ZERO: jumpCode = Opcodes.IFGT; break;
            case GREATER_OR_EQUAL_THAN_ZERO: jumpCode = Opcodes.IFGE; break;
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
                    compareTo,
                    new Node(NodeType.IF)
                        .withLeft(conditionNode)
                        .withRight(new Node(NodeType.BLOCK).withLeft(ifBlockNode))
            );
        } else {
            visit(
                    mv,
                    compareTo,
                    new Node(NodeType.IF)
                        .withLeft(conditionNode)
                        .withRight(new Node(NodeType.IF_ELSE)
                                .withLeft(new Node(NodeType.BLOCK).withLeft(ifBlockNode))
                                .withRight(new Node(NodeType.BLOCK).withLeft(elseBlockNode))
                        )
            );
        }
    }

    public void visit(
            @NotNull MethodVisitor mv,
            @NotNull INode conditionNode,
            @NotNull INode ifBlockNode,
            @Nullable INode elseBlockNode
    ) {
        visit(mv, CompareTo.NONZERO, conditionNode, ifBlockNode, elseBlockNode);
    }
}

enum CompareTo {
    ZERO,
    NONZERO,
    LESS_THAN_ZERO,
    LESS_OR_EQUAL_THAN_ZERO,
    GREATER_THAN_ZERO,
    GREATER_OR_EQUAL_THAN_ZERO,
}
