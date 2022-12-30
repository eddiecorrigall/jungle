package com.jungle.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.scanner.IScanner;
import com.jungle.scanner.Scanner;

public class ParserTest {
  @NotNull IScanner scanner = new Scanner();
  @Nullable Parser parser;

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
  public void testParseSequence_doubleNewlineSequence() {
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
}
