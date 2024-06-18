package com.jungle.ast;

import org.jetbrains.annotations.Nullable;

public enum NodeType {
  SEQUENCE,
  BLOCK,

  LITERAL_BOOLEAN,
  LITERAL_CHARACTER,
  LITERAL_INTEGER,
  LITERAL_FLOAT,
  LITERAL_STRING,

  CAST_CHAR,
  CAST_BYTE,
  CAST_SHORT,
  CAST_INTEGER,
  CAST_LONG,
  CAST_FLOAT,
  CAST_DOUBLE,

  // region Binary Operators - math
  OPERATOR_ADD,
  OPERATOR_SUBTRACT,
  OPERATOR_MULTIPLY,
  OPERATOR_DIVIDE,
  OPERATOR_MODULO,
  // endregion

  // region Binary Operators - conditional
  OPERATOR_AND,
  OPERATOR_OR,
  OPERATOR_EQUAL,
  OPERATOR_GREATER_THAN,
  OPERATOR_LESS_THAN,
  // endregion

  // region Unary Operators
  OPERATOR_NOT,
  // endregion

  ASSIGN,
  IDENTIFIER,

  /* new Node(IF)
   *   .withLeft("expression")
   *   .withRight("block"")
   */
  IF,

  /* new Node(IF)
   *   .withLeft("expression")
   *   .withRight(
   *     new Node(IF_ELSE)
   *       .withLeft("block")  // if-block
   *       .withRight("block") // else-block
   *   )
   */
  IF_ELSE,

  LOOP,

  ASSERT,
  PRINT,
  MULTITASK,
  SLEEP,

  TRUE,
  FALSE;

  public boolean equals(@Nullable INode ast) {
    throw new UnsupportedOperationException("cannot compare node to node type");
  }
}
