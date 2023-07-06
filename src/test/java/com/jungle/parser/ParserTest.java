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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    Parser parser = new Parser(tokenList);
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
    tokenList.add(new Token(TokenType.KEYWORD).withValue("false"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("true"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseBooleanExpression();

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
    tokenList.add(new Token(TokenType.KEYWORD).withValue("false"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("true"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("true"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseBooleanExpression();
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

  @Test(expected = Error.class)
  public void testParseExpressionBoolean_booleanAndNumeric() {
    // Note: mixing boolean and integer not allowed
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("not"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.PLUS));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("0"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    parser.parseBooleanExpression();
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
    Parser parser = new Parser(tokenList);
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
    tokenList.add(new Token(TokenType.KEYWORD).withValue("false"));
    tokenList.add(new Token(TokenType.BRACKET_ROUND_CLOSE));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.BRACKET_CURLY_OPEN));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("assert"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("true"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.BRACKET_CURLY_CLOSE));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
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

  @Test
  public void testStatement_decrement() {
    // i = - i 1
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.SYMBOL).withValue("i"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.EQUALS));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.MINUS));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.SYMBOL).withValue("i"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("1"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseStatementSymbol();

    assertNotNull(ast);
    assertEquals(NodeType.ASSIGN, ast.getType());
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.IDENTIFIER, ast.getLeft().getType());
    assertEquals("i", ast.getLeft().getValue());

    assertNotNull(ast.getRight());
    assertEquals(NodeType.OPERATOR_SUBTRACT, ast.getRight().getType());
    assertNull(ast.getRight().getValue());

    assertNotNull(ast.getRight().getLeft());
    assertEquals(NodeType.IDENTIFIER, ast.getRight().getLeft().getType());
    assertEquals("i", ast.getRight().getLeft().getValue());

    assertNotNull(ast.getRight().getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getRight().getRight().getType());
    assertEquals("1", ast.getRight().getRight().getValue());
  }

  @Test
  public void testParseStatementAssert_mixedExpression() {
    // assert not equals true false
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("assert"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("not"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("equals"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("true"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.KEYWORD).withValue("false"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseStatementAssert();

    assertNotNull(ast);
    assertEquals(NodeType.ASSERT, ast.getType());
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft());
    assertEquals(NodeType.OPERATOR_NOT, ast.getLeft().getType());
    assertNull(ast.getValue());

    assertNotNull(ast.getLeft().getLeft());
    assertEquals(NodeType.OPERATOR_EQUAL, ast.getLeft().getLeft().getType());
    assertNull(ast.getLeft().getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft().getLeft());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getLeft().getLeft().getType());
    assertEquals("1", ast.getLeft().getLeft().getLeft().getValue());

    assertNotNull(ast.getLeft().getLeft().getRight());
    assertEquals(NodeType.LITERAL_INTEGER, ast.getLeft().getLeft().getRight().getType());
    assertEquals("0", ast.getLeft().getLeft().getRight().getValue());
  }

  @Test
  public void testParseTextLiteral_emptyString() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.TEXT).withValue(""));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseTextLiteral();

    assertNotNull(ast);
    assertEquals(NodeType.LITERAL_STRING, ast.getType());
    assertEquals("", ast.getValue());
  }

  @Test
  public void testParseTextLiteral_singleCharacter() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.TEXT).withValue("X"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseTextLiteral();

    assertNotNull(ast);
    assertEquals(NodeType.LITERAL_CHARACTER, ast.getType());
    assertEquals("X", ast.getValue());
  }

  @Test
  public void testParseTextLiteral_string() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.TEXT).withValue("0xDEADBEEF"));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    INode ast = parser.parseTextLiteral();

    assertNotNull(ast);
    assertEquals(NodeType.LITERAL_STRING, ast.getType());
    assertEquals("0xDEADBEEF", ast.getValue());
  }

  @Test(expected = Error.class)
  public void testParseTextLiteral_error() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.TEXT));
    tokenList.add(new Token(TokenType.TERMINAL));
    Parser parser = new Parser(tokenList);
    parser.nextToken();

    parser.parseTextLiteral();
  }
}
