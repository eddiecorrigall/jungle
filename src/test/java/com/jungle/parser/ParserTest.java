package com.jungle.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.scanner.IScanner;
import com.jungle.scanner.Scanner;

public class ParserTest {
  @NotNull IScanner scanner = new Scanner();
  @NotNull Parser parser;

  @Before
  public void setup() {
    parser = new Parser(scanner);
  }

  @Test
  public void testParseSequence_singleNewline() {
    scanner.load("\n", 1);
    parser.nextToken();
    INode ast = parser.parseSequence();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.SEQUENCE);
  }

  @Test
  public void testParseSequence_doubleNewline() {
    scanner.load("\n\n", 1);
    parser.nextToken();
    INode ast = parser.parseSequence();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.SEQUENCE);
    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.SEQUENCE);
    assertNull(ast.getLeft().getLeft());
    assertNull(ast.getLeft().getRight());
    assertNull(ast.getRight());
  }

  @Test
  public void testParseNumber_positiveInteger() {
    scanner.load("123", 1);
    parser.nextToken();
    INode ast = parser.parseNumber();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getValue(), "123");
    assertNull(ast.getLeft());
    assertNull(ast.getRight());
  }

  @Test
  public void testParseNumber_positiveFloat() {
    scanner.load("123.456", 1);
    parser.nextToken();
    INode ast = parser.parseNumber();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.LITERAL_FLOAT);
    assertEquals(ast.getValue(), "123.456");
    assertNull(ast.getLeft());
    assertNull(ast.getRight());
  }

  @Test
  public void testParseExpression_negativeInteger() {
    scanner.load("-0 123", 1);
    parser.nextToken();
    INode ast = parser.parseExpression();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.OPERATOR_SUBTRACT);
    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getValue(), "0");
    assertNotNull(ast.getRight());
    assertEquals(ast.getRight().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getRight().getValue(), "123");
  }

  @Test
  public void testParsePrint_character() {
    scanner.load("print \"0\"\n", 1);
    parser.nextToken();
    INode ast = parser.parsePrint();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.PRINT);
    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.LITERAL_CHARACTER);
    assertEquals(ast.getLeft().getValue(), "0");
    assertNull(ast.getRight());
  }

  @Test
  public void testParsePrint_string() {
    scanner.load("print \"Hello world!\"\n", 1);
    parser.nextToken();
    INode ast = parser.parsePrint();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.PRINT);
    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.LITERAL_STRING);
    assertEquals(ast.getLeft().getValue(), "Hello world!");
    assertNull(ast.getRight());
  }

  @Test
  public void testParsePrint_stringWithParenthesis() {
    scanner.load("print(\"Hello world!\")\n", 1);
    parser.nextToken();
    INode ast = parser.parsePrint();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.PRINT);
    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.LITERAL_STRING);
    assertEquals(ast.getLeft().getValue(), "Hello world!");
    assertNull(ast.getRight());
  }

  @Test
  public void testParsePrint_float() {
    scanner.load("print 123.456\n", 1);
    parser.nextToken();
    INode ast = parser.parsePrint();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.PRINT);
    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.LITERAL_FLOAT);
    assertEquals(ast.getLeft().getValue(), "123.456");
    assertNull(ast.getRight());
  }

  @Test
  public void testParseBooleanExpression_andOperation() {
    scanner.load("and 0 1\n", 1);
    parser.nextToken();
    INode ast = parser.parseBooleanExpression();
    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.OPERATOR_AND);

    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getValue(), "0");

    assertNotNull(ast.getRight());
    assertEquals(ast.getRight().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getRight().getValue(), "1");
  }

  @Test
  public void testParseBooleanExpression_nestedBoolean() {
    scanner.load("not and or 0 1 1\n", 1);
    parser.nextToken();
    INode ast = parser.parseBooleanExpression();

    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.OPERATOR_NOT);
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.OPERATOR_AND);
    assertNull(ast.getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft());
    assertEquals(ast.getLeft().getLeft().getType(), NodeType.OPERATOR_OR);
    assertNull(ast.getLeft().getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft().getLeft());
    assertEquals(ast.getLeft().getLeft().getLeft().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getLeft().getLeft().getValue(), "0");

    assertNotNull(ast.getLeft().getLeft().getRight());
    assertEquals(ast.getLeft().getLeft().getRight().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getLeft().getRight().getValue(), "1");

    assertNotNull(ast.getLeft().getRight());
    assertEquals(ast.getLeft().getRight().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getRight().getValue(), "1");

    assertNull(ast.getRight());
  }

  @Test
  public void testParseBooleanExpression_booleanAndNumeric() {
    scanner.load("not + 1 0\n", 1);
    parser.nextToken();
    INode ast = parser.parseBooleanExpression();

    assertNotNull(ast);
    assertEquals(ast.getType(), NodeType.OPERATOR_NOT);

    assertNotNull(ast.getLeft());
    assertEquals(ast.getLeft().getType(), NodeType.OPERATOR_ADD);
    assertNull(ast.getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft());
    assertEquals(ast.getLeft().getLeft().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getLeft().getValue(), "1");

    assertNull(ast.getLeft().getLeft().getLeft());
    assertNull(ast.getLeft().getLeft().getRight());

    assertNotNull(ast.getLeft().getRight());
    assertEquals(ast.getLeft().getRight().getType(), NodeType.LITERAL_INTEGER);
    assertEquals(ast.getLeft().getRight().getValue(), "0");

    assertNull(ast.getLeft().getRight().getLeft());
    assertNull(ast.getLeft().getRight().getRight());

    assertNull(ast.getRight());
  }
}
