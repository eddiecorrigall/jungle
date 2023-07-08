package com.jungle.walker;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.symbol.SymbolTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class BooleanOperatorVisitor extends BaseVisitor {
    @NotNull
    public static INode PUSH_TRUE_NODE = new Node(NodeType.LITERAL_INTEGER).withRawValue("1");

    @NotNull
    public static INode PUSH_FALSE_NODE = new Node(NodeType.LITERAL_INTEGER).withRawValue("0");

    public BooleanOperatorVisitor(
            @NotNull Stack<OperandStackType> operandStackTypeStack,
            @NotNull SymbolTable symbolTable
            ) {
        super(operandStackTypeStack, symbolTable);
    }

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public BooleanOperatorVisitor withExpressionVisitor(@NotNull ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        return this;
    }

    @Nullable
    private IfVisitor ifVisitor;

    @NotNull
    public BooleanOperatorVisitor withIfElseVisitor(@NotNull IfVisitor ifVisitor) {
        this.ifVisitor = ifVisitor;
        return this;
    }

    public static final Set<NodeType> BOOLEAN_OPERATORS = new HashSet<>(Arrays.asList(
            // unary
            NodeType.OPERATOR_NOT,

            // binary
            NodeType.OPERATOR_AND,
            NodeType.OPERATOR_OR,
            // binary - compare
            NodeType.OPERATOR_EQUAL,
            NodeType.OPERATOR_LESS_THAN,
            NodeType.OPERATOR_GREATER_THAN
    ));
    @Override
    public boolean canVisit(@NotNull INode ast) {
        return BOOLEAN_OPERATORS.contains(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit boolean operator " + ast);

        // Optimization idea:
        //  if the left expression has a shorter depth (fewer operations) than the right,
        //  which expression should be traversed to optimize for short circuit logic?
        // Follow up:
        //  is it better to evaluate batches of operators, then test short circuit?

        if (!canVisit(ast)) {
            return;
        }

        if (ast.getLeft() == null) {
            throw new Error("boolean operator missing left expression");
        }

        switch (ast.getType()) {
            case OPERATOR_AND: {
                // When one boolean input is false (non-zero operand value), the expression is false
                if (ast.getRight() == null) {
                    throw new Error("boolean operator missing right expression");
                }
                ifVisitor.visit(
                        mv,
                        CompareTo.ZERO,     // when 0 (false), jump to else
                        ast.getLeft(),      // if-condition
                        ast.getRight(),     // if-block: compute other half of AND operation
                        PUSH_FALSE_NODE     // else-block: short circuit
                );
            } break;
            case OPERATOR_OR: {
                // When one boolean input is true, the expression is true
                if (ast.getRight() == null) {
                    throw new Error("boolean operator missing right expression");
                }
                ifVisitor.visit(
                        mv,
                        CompareTo.NONZERO, // when non-0 (true), jump to else
                        ast.getLeft(),     // if-condition
                        ast.getRight(),    // if-block: compute other half of OR operation
                        PUSH_TRUE_NODE     // else-block: short circuit
                );
            } break;
            case OPERATOR_NOT: {
                /* When condition is true, return false.
                 * When condition is false, return true.
                 */
                if (ast.getRight() != null) {
                    throw new Error("boolean (unary) operator with unexpected right expression");
                }
                ifVisitor.visit(
                        mv,
                        CompareTo.ZERO,  // when 0 (false), jump to else
                        ast.getLeft(),   // if-condition
                        PUSH_FALSE_NODE, // if-block: when condition is true then return false
                        PUSH_TRUE_NODE   // else-block: when condition is false then return true
                );
            } break;
            case OPERATOR_EQUAL: {
                /*
                 * result = left - right
                 * if (result == 0) return true
                 * else return false
                 */
                if (ast.getRight() == null) {
                    throw new Error("boolean operator missing right expression");
                }
                ifVisitor.visit(
                        mv,
                        CompareTo.NONZERO, // when non-0 (true), jump to else
                        new Node(NodeType.OPERATOR_SUBTRACT)
                                .withLeft(ast.getLeft())
                                .withRight(ast.getRight()),
                        PUSH_TRUE_NODE,
                        PUSH_FALSE_NODE
                );
            } break;
            case OPERATOR_LESS_THAN: {
                /*
                 * result = left - right
                 * if (result < 0) return true
                 * else return false
                 */
                if (ast.getRight() == null) {
                    throw new Error("boolean operator missing right expression");
                }
                ifVisitor.visit(
                        mv,
                        CompareTo.GREATER_OR_EQUAL_THAN_ZERO, // when >= 0, jump to else
                        new Node(NodeType.OPERATOR_SUBTRACT)
                                .withLeft(ast.getLeft())
                                .withRight(ast.getRight()),
                        PUSH_TRUE_NODE,
                        PUSH_FALSE_NODE
                );
            } break;
            case OPERATOR_GREATER_THAN: {
                /*
                 * result = left - right
                 * if (result > 0) return true
                 * else return false
                 */
                if (ast.getRight() == null) {
                    throw new Error("boolean operator missing right expression");
                }
                ifVisitor.visit(
                        mv,
                        CompareTo.LESS_OR_EQUAL_THAN_ZERO, // when <= 0, jump to else
                        new Node(NodeType.OPERATOR_SUBTRACT)
                                .withLeft(ast.getLeft())
                                .withRight(ast.getRight()),
                        PUSH_TRUE_NODE,
                        PUSH_FALSE_NODE
                );
            } break;
            default: {
                throw new Error("boolean operator expected");
            }
        }
    }
}
