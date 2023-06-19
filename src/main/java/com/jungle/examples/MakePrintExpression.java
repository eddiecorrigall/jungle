package com.jungle.examples;

import java.io.IOException;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;

public class MakePrintExpression {
  public static void main(String[] args) throws IOException {
    /*
    // 1 + 2*3
    // => + 1 * 2 3
    // => 7
    Node ast = new Node(NodeType.ADD)
      .withLeft(new Node(NodeType.LITERAL_INT, "1"))
      .withRight(
        new Node(NodeType.MULTIPLY)
          .withLeft(new Node(NodeType.LITERAL_INT, "2"))
          .withRight(new Node(NodeType.LITERAL_INT, "3"))
      )
      ;
    */
    /*
    // 1/2 - 3*4
    // => - / 1 2 * 3 4
    // => -12
    Node ast = new Node(NodeType.SUBTRACT)
      .withLeft(
        new Node(NodeType.DIVIDE)
          .withLeft(new Node(NodeType.LITERAL_INTEGER, "1"))
          .withRight(new Node(NodeType.LITERAL_INTEGER, "2"))
      )
      .withRight(
        new Node(NodeType.MULTIPLY)
          .withLeft(new Node(NodeType.LITERAL_INTEGER, "3"))
          .withRight(new Node(NodeType.LITERAL_INTEGER, "4"))
      )
      ;
    */
    // (1+2) * i32(4/2)
    // => * + 1 2 i32 / 4 2
    // => 6
    Node ast = new Node(NodeType.OPERATOR_MULTIPLY)
      .withLeft(
        new Node(NodeType.OPERATOR_ADD)
          .withLeft(new Node(NodeType.LITERAL_INTEGER).withValue("1"))
          .withRight(new Node(NodeType.LITERAL_INTEGER).withValue("2"))
      )
      .withRight(
        new Node(NodeType.CAST_INTEGER)
          .withLeft(
            new Node(NodeType.OPERATOR_DIVIDE)
              .withLeft(new Node(NodeType.LITERAL_FLOAT).withValue("4"))
              .withRight(new Node(NodeType.LITERAL_FLOAT).withValue("2"))
          )
      )
      ;
    /*
    System.out.println("IS INTEGER? " + JungleCompiler.isInteger(ast));
    System.out.println("IS FLOAT? " + JungleCompiler.isFloat(ast));
    JungleCompiler compiler = new JungleCompiler();
    compiler.compile("PrintExpression", ast);
     */
  }
}
