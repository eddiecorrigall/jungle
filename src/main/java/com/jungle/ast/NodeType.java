package com.jungle.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum NodeType {
  SEQUENCE,

  LITERAL_BOOLEAN,
  LITERAL_INTEGER,
  LITERAL_FLOAT,
  LITERAL_STRING,
  LITERAL_CHARACTER,

  CAST_INTEGER,
  CAST_FLOAT,
  CAST_CHARACTER,

  OPERATOR_ADD,
  OPERATOR_SUBTRACT,
  OPERATOR_MULTIPLY,
  OPERATOR_DIVIDE,
  OPERATOR_MODULO,

  TYPE_BOOLEAN,
  TYPE_CHARACTER,
  TYPE_INTEGER,

  ASSIGN,
  IDENTIFIER,

  PRINT;

  public static Set<NodeType> LITERALS = new HashSet<NodeType>(Arrays.asList(
          LITERAL_BOOLEAN,
          LITERAL_CHARACTER,
          LITERAL_INTEGER,
          LITERAL_FLOAT,
          LITERAL_STRING
  ));

  public static Set<NodeType> BINARY_OPERATORS = new HashSet<>(Arrays.asList(
          OPERATOR_ADD,
          OPERATOR_SUBTRACT,
          OPERATOR_MULTIPLY,
          OPERATOR_DIVIDE,
          OPERATOR_MODULO
  ));

  public static Set<NodeType> TYPES = new HashSet<>(Arrays.asList(
          TYPE_BOOLEAN,
          TYPE_INTEGER
  ));
}
