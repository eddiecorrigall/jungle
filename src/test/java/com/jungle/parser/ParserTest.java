package com.jungle.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.jungle.token.IToken;
import com.jungle.token.Token;
import com.jungle.token.TokenType;
import org.junit.Test;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;

import java.util.LinkedList;
import java.util.List;

public class ParserTest {
  @Test
  public void testParseSequence_singleNewline() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.NEWLINE));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseSequence();
    assertNull(ast);
  }

  @Test
  public void testParseSequence_doubleNewline() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.NEWLINE));
    tokenList.add(new Token(TokenType.NEWLINE));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseSequence();

    assertNull(ast);
  }

  @Test
  public void testParseSequence_doubleNewLineThenStatement() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.NEWLINE));
    tokenList.add(new Token(TokenType.NEWLINE));
    tokenList.add(new Token(TokenType.SYMBOL).withValue("i"));
    tokenList.add(new Token(TokenType.EQUALS));
    tokenList.add(new Token(TokenType.NUMBER).withValue("10"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseSequence();

    assertNotNull(ast);
    assertEquals(NodeType.SEQUENCE, ast.getType());
  }

  @Test
  public void testParseNumber_positiveInteger() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.NUMBER).withValue("123"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseNumber();

    assertNotNull(ast);
    assertEquals(NodeType.LITERAL_INTEGER, ast.getType());
    assertEquals("123", ast.getValue());

    assertNull(ast.getLeft());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseNumber_positiveFloat() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.NUMBER).withValue("123"));
    tokenList.add(new Token(TokenType.DOT));
    tokenList.add(new Token(TokenType.NUMBER).withValue("456"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseNumber();
    assertNotNull(ast);
    assertEquals(NodeType.LITERAL_FLOAT, ast.getType());
    assertEquals("123.456", ast.getValue());
    assertNull(ast.getLeft());
    assertNull(ast.getRight());
  }

  @Test
  public void testParseExpression_negativeInteger() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.MINUS));
    tokenList.add(new Token(TokenType.NUMBER).withValue("0"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("123"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseExpression();
    assertNotNull(ast);
    assertEquals(NodeType.OPERATOR_SUBTRACT, ast.getType());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getType());
    assertEquals("0", ast.getLeft().getValue());

    assertNotNull(ast.getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getRight().getType());
    assertEquals("123", ast.getRight().getValue());
  }

  @Test
  public void testParseStatementPrint_character() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.TEXT).withValue("0"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseStatementPrint();
    assertNotNull(ast);
    assertEquals(NodeType.PRINT, ast.getType());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_CHARACTER, ast.getLeft().getType());
    assertEquals("0", ast.getLeft().getValue());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseStatementPrint_string() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.TEXT).withValue("Hello world!"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseStatementPrint();
    assertNotNull(ast);
    assertEquals(NodeType.PRINT, ast.getType());
    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_STRING, ast.getLeft().getType());
    assertEquals("Hello world!", ast.getLeft().getValue());
    assertNull(ast.getRight());
  }

  @Test
  public void testParseStatementPrint_stringWithParenthesis() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    tokenList.add(new Token(TokenType.BRACKET_ROUND_OPEN));
    tokenList.add(new Token(TokenType.TEXT).withValue("Hello world!"));
    tokenList.add(new Token(TokenType.BRACKET_ROUND_CLOSE));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseStatementPrint();

    assertNotNull(ast);
    assertEquals(NodeType.PRINT, ast.getType());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_STRING, ast.getLeft().getType());
    assertEquals("Hello world!", ast.getLeft().getValue());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseStatementPrint_float() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("123"));
    tokenList.add(new Token(TokenType.DOT));
    tokenList.add(new Token(TokenType.NUMBER).withValue("456"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseStatementPrint();

    assertNotNull(ast);
    assertEquals(NodeType.PRINT, ast.getType());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_FLOAT, ast.getLeft().getType());
    assertEquals("123.456", ast.getLeft().getValue());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseExpressionBoolean_andOperation() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("and"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("0"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseExpressionBoolean();

    assertNotNull(ast);
    assertEquals(NodeType.OPERATOR_AND, ast.getType());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getType());
    assertEquals("0", ast.getLeft().getValue());

    assertNotNull(ast.getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getRight().getType());
    assertEquals("1", ast.getRight().getValue());
  }

  @Test
  public void testParseExpressionBoolean_nestedBoolean() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("not"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("and"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("or"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("0"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseExpressionBoolean();
    assertNotNull(ast);
    assertEquals(NodeType.OPERATOR_NOT, ast.getType());
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.OPERATOR_AND, ast.getLeft().getType());
    assertNull(ast.getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft());
    assertEquals(NodeType.OPERATOR_OR, ast.getLeft().getLeft().getType());
    assertNull(ast.getLeft().getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft().getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getLeft().getLeft().getType());
    assertEquals("0", ast.getLeft().getLeft().getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft().getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getLeft().getRight().getType());
    assertEquals("1", ast.getLeft().getLeft().getRight().getValue());

    assertNotNull(ast.getLeft().getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getRight().getType());
    assertEquals("1", ast.getLeft().getRight().getValue());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseExpressionBoolean_booleanAndNumeric() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("not"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.PLUS));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("0"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseExpressionBoolean();

    assertNotNull(ast);
    assertEquals(NodeType.OPERATOR_NOT, ast.getType());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.OPERATOR_ADD, ast.getLeft().getType());
    assertNull(ast.getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getLeft().getType());
    assertEquals("1", ast.getLeft().getLeft().getValue());

    assertNull(ast.getLeft().getLeft().getLeft());
    assertNull(ast.getLeft().getLeft().getRight());

    assertNotNull(ast.getLeft().getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getRight().getType());
    assertEquals("0", ast.getLeft().getRight().getValue());

    assertNull(ast.getLeft().getRight().getLeft());
    assertNull(ast.getLeft().getRight().getRight());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseStatementBlock() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.BRACKET_CURLY_OPEN));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.BRACKET_CURLY_CLOSE));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseStatementBlock();

    assertNotNull(ast);
    assertEquals(NodeType.BLOCK, ast.getType());
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.SEQUENCE, ast.getLeft().getType());
    assertNull(ast.getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft());
    assertEquals(NodeType.PRINT, ast.getLeft().getLeft().getType());
    assertNull(ast.getLeft().getLeft().getValue());

    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getLeft().getLeft().getType());
    assertEquals("1", ast.getLeft().getLeft().getLeft().getValue());

    assertNull(ast.getLeft().getLeft().getRight());

    assertNull(ast.getLeft().getRight());

    assertNull(ast.getRight());
  }

  @Test
  public void testParseStatementLoop() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("loop"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.BRACKET_ROUND_OPEN));
    tokenList.add(new Token(TokenType.NUMBER).withValue("0"));
    tokenList.add(new Token(TokenType.BRACKET_ROUND_CLOSE));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.BRACKET_CURLY_OPEN));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("assert"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.BRACKET_CURLY_CLOSE));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList.iterator());
    parser.nextToken();

    INode ast = parser.parseStatementLoop();

    assertNotNull(ast);
    assertEquals(NodeType.LOOP, ast.getType());
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getType());
    assertEquals("0", ast.getLeft().getValue());

    assertNotNull(ast.getRight());
    assertEquals(NodeType.BLOCK, ast.getRight().getType());
    assertNull(ast.getRight().getValue());

    assertNotNull(ast.getRight().getLeft());
    assertEquals(NodeType.SEQUENCE, ast.getRight().getLeft().getType());
    assertNull(ast.getRight().getLeft().getValue());

    assertNotNull(ast.getRight().getLeft().getLeft());
    assertEquals(NodeType.ASSERT, ast.getRight().getLeft().getLeft().getType());
    assertNull(ast.getRight().getLeft().getLeft().getValue());

    assertNotNull(ast.getRight().getLeft().getLeft().getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getRight().getLeft().getLeft().getLeft().getType());
    assertEquals("1", ast.getRight().getLeft().getLeft().getLeft().getValue());

    assertNull(ast.getRight().getLeft().getLeft().getRight());

    assertNull(ast.getRight().getRight());
  }
}
