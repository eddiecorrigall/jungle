package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

public class AssignmentVisitor extends AbstractClassPathVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(AssignmentVisitor.class.getName());

    @Nullable
    private ExpressionVisitor expressionVisitor;

    @NotNull
    private ExpressionVisitor getExpressionVisitor() {
        if (expressionVisitor == null) {
            expressionVisitor = new ExpressionVisitor(getClassPath());
        }
        return expressionVisitor;
    }

    public AssignmentVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.ASSIGN.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit assignment " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected assignment");
        }

        INode identifierNode = ast.getLeft();
        if (identifierNode == null || identifierNode.getType() != NodeType.IDENTIFIER) {
            throw new Error("expected left AST to be identifier");
        }

        String variableName = identifierNode.getRawValue();
        if (variableName == null) {
            throw new Error("identifier missing variable name");
        }

        INode expressionNode = ast.getRight();
        if (expressionNode == null) {
            throw new Error("expected right AST to be expression");
        }

        // evaluate expression to have value pushed onto operand stack...
        getExpressionVisitor().visit(mv, expressionNode, context);

        // store operand stack value and assign variable to index...
        context.visitStore(mv, variableName);
    }
}
