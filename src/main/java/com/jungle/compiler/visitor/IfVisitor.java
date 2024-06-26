package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IfVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(IfVisitor.class.getName());

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    @Nullable
    private BlockVisitor blockVisitor;

    @NotNull
    private BlockVisitor getBlockVisitor() {
        if (blockVisitor == null) {
            blockVisitor = new BlockVisitor(getCompilerOptions());
        }
        return blockVisitor;
    }

    public IfVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NodeType.IF.equals(node.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        visit(mv, CompareTo.ZERO, ast, context);
    }

    protected void visit(
        @NotNull MethodVisitor mv,
        @NotNull CompareTo compareTo,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit if " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected if");
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

        if (conditionNode.getType() == NodeType.NOOP) {
            logger.debug("assuming that the condition result is already on the operand stack");
        } else {
            try {
                getExpressionVisitor().visit(mv, conditionNode, context);
            } catch (Throwable t) {
                throw new Error("if condition/expression cannot be evaluated", t);
            }
        }

        OperandType conditionType = context.peek();

        if (conditionType == OperandType.FLOAT || conditionType == OperandType.DOUBLE) {
            logger.warn("you may need to convert from a floating-point expression to integer-like");
            throw new Error("if condition/expression cannot evaluate floating-point values");
        }

        if (OperandType.INTEGER_COMPUTATIONAL_TYPES.contains(conditionType) == false) {
            throw new Error("if condition/expression expected to be integer-like");
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
            getBlockVisitor().visit(mv, ifBlockNode, context);
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);

            // else-block
            mv.visitLabel(elseBlockLabel);
            getBlockVisitor().visit(mv, elseBlockNode, context);

            // end
            mv.visitLabel(endLabel);
        } else {
            // if-condition
            mv.visitJumpInsn(jumpCode, endLabel);

            // if-block
            getBlockVisitor().visit(mv, bodyNode, context);

            // end
            mv.visitLabel(endLabel);
        }
    }

    public void visit(
            @NotNull MethodVisitor mv,
            @NotNull CompareTo compareTo,
            @NotNull INode conditionNode,
            @NotNull INode ifBlockNode,
            @Nullable INode elseBlockNode,
            @NotNull OperandStackContext context
    ) {
        if (elseBlockNode == null) {
            visit(
                    mv,
                    compareTo,
                    new Node(NodeType.IF)
                        .withLeft(conditionNode)
                        .withRight(new Node(NodeType.BLOCK).withLeft(ifBlockNode)),
                    context
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
                        ),
                    context
            );
        }
    }

    public void visit(
            @NotNull MethodVisitor mv,
            @NotNull INode conditionNode,
            @NotNull INode ifBlockNode,
            @Nullable INode elseBlockNode,
            @NotNull OperandStackContext context
    ) {
        visit(mv, CompareTo.NONZERO, conditionNode, ifBlockNode, elseBlockNode, context);
    }
}

enum CompareTo {
    ZERO,
    NONZERO,
    LESS_THAN_ZERO,
    LESS_OR_EQUAL_THAN_ZERO,
    GREATER_THAN_ZERO,
    GREATER_OR_EQUAL_THAN_ZERO;

    public int getOpcode() {
        switch (this) {
            case ZERO: return Opcodes.IFEQ;
            case NONZERO: return Opcodes.IFNE;
            case LESS_THAN_ZERO: return Opcodes.IFLT;
            case LESS_OR_EQUAL_THAN_ZERO: return Opcodes.IFLE;
            case GREATER_THAN_ZERO: return Opcodes.IFGT;
            case GREATER_OR_EQUAL_THAN_ZERO: return Opcodes.IFGE;
            default: throw new Error("unhandled if comparison");
        }
    }
}
