package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.compiler.operand.OperandType;
import com.jungle.logger.FileLogger;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jungle.ast.NodeType.*;

public class LiteralVisitor implements IVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(LiteralVisitor.class.getName());

    private static final Set<NodeType> LITERALS = new HashSet<>(Arrays.asList(
            LITERAL_BOOLEAN,
            LITERAL_CHARACTER,
            LITERAL_INTEGER,
            LITERAL_FLOAT,
            LITERAL_STRING
    ));

    public LiteralVisitor() {
        super();
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return LITERALS.contains(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        logger.debug("visit literal " + ast);

        if (!canVisit(ast)) {
            throw new Error("expected literal");
        }

        if (ast.getRawValue() == null) {
            throw new Error("literal missing value");
        }

        Object objectValue;
        OperandType type;

        switch (ast.getType()) {
            case LITERAL_BOOLEAN: {
                objectValue = ast.getBooleanValue();
                type = OperandType.BOOLEAN;
            } break;
            case LITERAL_CHARACTER: {
                objectValue = ast.getCharacterValue();
                type = OperandType.CHAR;
            } break;
            case LITERAL_INTEGER: {
                objectValue = ast.getIntegerValue();
                type = OperandType.INTEGER;
            } break;
            case LITERAL_FLOAT: {
                objectValue = ast.getFloatValue();
                type = OperandType.FLOAT;
            } break;
            case LITERAL_STRING: {
                objectValue = ast.getStringValue();
                type = OperandType.OBJECT;
            } break;
            default: throw new Error("unhandled literal");
        }

        mv.visitLdcInsn(objectValue);
        context.push(type);
    }
}
