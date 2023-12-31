package com.jungle.examples;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;

public class Examples {
    /* (1+2) * i32(4/2)
     * => * + 1 2 i32 / 4 2
     * => 6
     */
    public static final INode EXPRESSION_INT_FLOAT = new Node(NodeType.OPERATOR_MULTIPLY)
            .withLeft(
                    new Node(NodeType.OPERATOR_ADD)
                            .withLeft(new Node(NodeType.LITERAL_INTEGER).withRawValue("1"))
                            .withRight(new Node(NodeType.LITERAL_INTEGER).withRawValue("2"))
            )
            .withRight(
                    new Node(NodeType.CAST_INTEGER)
                            .withLeft(
                                    new Node(NodeType.OPERATOR_DIVIDE)
                                            .withLeft(new Node(NodeType.LITERAL_FLOAT).withRawValue("4"))
                                            .withRight(new Node(NodeType.LITERAL_FLOAT).withRawValue("2"))
                            )
            )
            ;

    public static final INode EXPRESSION_IDENTIFIER = new Node(NodeType.OPERATOR_ADD)
            .withLeft(
                    new Node(NodeType.LITERAL_INTEGER).withRawValue("5")
            )
            .withRight(
                    new Node(NodeType.IDENTIFIER).withRawValue("theVariable")
            );

    public static final INode ASSIGNMENT = new Node(NodeType.ASSIGN)
            .withLeft(new Node(NodeType.IDENTIFIER).withRawValue("theVariable"))
            .withRight(new Node(NodeType.LITERAL_INTEGER).withRawValue("6"))
            ;
}
