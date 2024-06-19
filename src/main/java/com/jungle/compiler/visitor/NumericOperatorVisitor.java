package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.ICompilerOptions;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jungle.ast.NodeType.*;

public class NumericOperatorVisitor extends AbstractVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(NumericOperatorVisitor.class.getName());

    private static final Set<NodeType> NUMERIC_OPERATORS = new HashSet<>(Arrays.asList(
            OPERATOR_ADD,
            OPERATOR_SUBTRACT,
            OPERATOR_MULTIPLY,
            OPERATOR_DIVIDE,
            OPERATOR_MODULO
    ));

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    public ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getCompilerOptions());
        }
        return expressionVisitor;
    }

    public NumericOperatorVisitor(@NotNull ICompilerOptions options) {
        super(options);
    }

    @Override
    public boolean canVisit(@NotNull INode node) {
        return NUMERIC_OPERATORS.contains(node.getType());
    }

    private OperandType convert(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context,
        @NotNull OperandType fromType,
        @NotNull OperandType toType
    ) {
        if (fromType == OperandType.INTEGER && toType == OperandType.CHAR) {
            mv.visitInsn(Opcodes.I2C);
            context.push(OperandType.CHAR); // final type
            return OperandType.INTEGER; // computation type
        }
        if (fromType == OperandType.OBJECT && toType == OperandType.INTEGER) {
            // invoke int Object::hashCode() on both left and right objects
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/Object",
                "hashCode",
                "()I",
                false
            );
            context.push(OperandType.INTEGER); // final type
            return OperandType.INTEGER; // computation type
        }
        throw new Error("conversion not possible");
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit binary operator " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected numeric operator");
        }

        if (ast.getLeft() == null) {
            throw new Error("binary operator missing left expression");
        }

        if (ast.getRight() == null) {
            throw new Error("binary operator missing right expression");
        }

        // prepare for operation...

        getExpressionVisitor().visit(mv, ast.getLeft(), context);
        OperandType leftExpressionType = context.pop();

        getExpressionVisitor().visit(mv, ast.getRight(), context);
        OperandType rightExpressionType = context.pop();

        logger.debug("left is " + ast.getLeft());
        logger.debug("right is " + ast.getRight());

        // prepare for operation - cast types...

        OperandType operandType;

        /* TODO:
         * This conversion may work for the mandelbrot program,
         * but what about scenarios where the programmer wants to preserve accuracy?
         * 
         * We want explicit conversions, right?
         */
        if (leftExpressionType == OperandType.INTEGER && rightExpressionType == OperandType.CHAR) {
            // Note: left ast is previous operand stack item
            mv.visitInsn(Opcodes.SWAP);
            operandType = convert(mv, ast, context, OperandType.INTEGER, OperandType.CHAR);
            // TODO: can swap restore be conditional based on operator?
            mv.visitInsn(Opcodes.SWAP); // restore order
        } else if (leftExpressionType == OperandType.CHAR && rightExpressionType == OperandType.INTEGER) {
            // Note: right ast is next operand stack item
            operandType = convert(mv, ast, context, OperandType.INTEGER, OperandType.CHAR);
        } else if (leftExpressionType == OperandType.OBJECT) {
            // Shallow comparison
            mv.visitInsn(Opcodes.SWAP); // switch to object
            operandType = convert(mv, ast, context, OperandType.OBJECT, OperandType.INTEGER);
            mv.visitInsn(Opcodes.SWAP); // restore order
        } else if (rightExpressionType == OperandType.OBJECT) {
            // Shallow comparison
            operandType = convert(mv, ast, context, OperandType.OBJECT, OperandType.INTEGER);
        }
         else if (leftExpressionType.equals(rightExpressionType)) {
            operandType = leftExpressionType;
            context.push(operandType);
        } else {
            throw new Error("binary operator left or right expression requires type cast " + ast);
        }

        // perform operation...

        switch (ast.getType()) {
            case OPERATOR_ADD: mv.visitInsn(operandType.getAddOpcode()); break;
            case OPERATOR_SUBTRACT: mv.visitInsn(operandType.getSubtractOpcode()); break; // order matters
            case OPERATOR_MULTIPLY: mv.visitInsn(operandType.getMultiplyOpcode()); break;
            case OPERATOR_DIVIDE: mv.visitInsn(operandType.getDivideOpcode()); break; // order matters
            case OPERATOR_MODULO: mv.visitInsn(operandType.getModuloOpcode()); break; // order matters
            default: throw new Error("unhandled operation " + ast);
        }
    }
}
