package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandStackType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jungle.ast.NodeType.*;

public class LiteralVisitor implements IVisitor {
    private static final Set<NodeType> LITERALS = new HashSet<>(Arrays.asList(
            LITERAL_BOOLEAN,
            LITERAL_CHARACTER,
            LITERAL_INTEGER,
            LITERAL_FLOAT,
            LITERAL_STRING
    ));

    @Nullable
    private OperandStackContext operandStackContext;

    private OperandStackContext getOperandStackContext() {
        if (operandStackContext == null) {
            operandStackContext = OperandStackContext.getInstance();
        }
        return operandStackContext;
    }

    private LiteralVisitor(@NotNull final OperandStackContext operandStackContext) {
       super();
       this.operandStackContext = operandStackContext;
    }

    public LiteralVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return LITERALS.contains(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        System.out.println("visit literal " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected literal");
        }

        if (ast.getRawValue() == null) {
            throw new Error("literal missing value");
        }

        Object objectValue;
        OperandStackType type;

        switch (ast.getType()) {
            case LITERAL_BOOLEAN: {
                objectValue = ast.getBooleanValue();
                type = OperandStackType.BOOLEAN;
            } break;
            case LITERAL_CHARACTER: {
                objectValue = ast.getCharacterValue();
                type = OperandStackType.CHARACTER;
            } break;
            case LITERAL_INTEGER: {
                objectValue = ast.getIntegerValue();
                type = OperandStackType.INTEGER;
            } break;
            case LITERAL_FLOAT: {
                objectValue = ast.getFloatValue();
                type = OperandStackType.FLOAT;
            } break;
            case LITERAL_STRING: {
                objectValue = ast.getStringValue();
                type = OperandStackType.REFERENCE_OBJECT;
            } break;
            default: throw new Error("unhandled literal");
        }

        mv.visitLdcInsn(objectValue);
        getOperandStackContext().push(type);
    }
}
