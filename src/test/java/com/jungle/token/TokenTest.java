package com.jungle.token;

import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TokenTest {
  @Test
  public void testEquals() {
    assertNotEquals(new Token(TokenType.PLUS), new Token(TokenType.MINUS));
    assertEquals(new Token(TokenType.TEXT), new Token(TokenType.TEXT));
    assertEquals(new Token(TokenType.TEXT).withValue("test"), new Token(TokenType.TEXT).withValue("test"));
    assertNotEquals(new Token(TokenType.TEXT), new Token(TokenType.TEXT).withValue("test"));
  }

  @Test
  public void testHashCode() {
    assertNotEquals(
            new Token(TokenType.PLUS).hashCode(),
            new Token(TokenType.MINUS).hashCode());
    assertEquals(
            new Token(TokenType.TEXT).hashCode(),
            new Token(TokenType.TEXT).hashCode());
    assertEquals(
            new Token(TokenType.TEXT).withValue("test").hashCode(),
            new Token(TokenType.TEXT).withValue("test").hashCode());
    assertNotEquals(
            new Token(TokenType.TEXT).hashCode(),
            new Token(TokenType.TEXT).withValue("test").hashCode());
  }

  @Test
  public void testLoad_empty() {
    BufferedReader reader = new BufferedReader(new StringReader(""));
    List<IToken> tokenList = Token.load(reader);
    assertEquals(0, tokenList.size());
  }

  @Test
  public void testLoad_singleLine() {
    BufferedReader reader = new BufferedReader(new StringReader(
            "1\t1\tTERMINAL"
    ));
    List<IToken> tokenList = Token.load(reader);

    assertEquals(1, tokenList.size());

    IToken theToken = tokenList.get(0);
    assertEquals(TokenType.TERMINAL, theToken.getType());
    assertNull(theToken.getValue());
    assertEquals(1, theToken.getLineNumber());
    assertEquals(1, theToken.getCharacterNumber());
  }

  @Test
  public void testLoad_tokenValue() {
    BufferedReader reader = new BufferedReader(new StringReader(
            "2\t7\tSYMBOL\tmyInteger"
    ));
    List<IToken> tokenList = Token.load(reader);

    assertEquals(1, tokenList.size());

    IToken theToken = tokenList.get(0);
    assertEquals(TokenType.SYMBOL, theToken.getType());
    assertEquals("myInteger", theToken.getValue());
    assertEquals(2, theToken.getLineNumber());
    assertEquals(7, theToken.getCharacterNumber());
  }

  @Test
  public void testLoad_tokenValueWithTab() {
    BufferedReader reader = new BufferedReader(new StringReader(
            "5\t3\tTEXT\tCount:\t123 "
    ));
    List<IToken> tokenList = Token.load(reader);

    assertEquals(1, tokenList.size());

    IToken theToken = tokenList.get(0);
    assertEquals(TokenType.TEXT, theToken.getType());
    assertEquals("Count:\t123 ", theToken.getValue());
    assertEquals(5, theToken.getLineNumber());
    assertEquals(3, theToken.getCharacterNumber());
  }

  @Test
  public void testLoad_tokenValueWithEscapedTab() {
    BufferedReader reader = new BufferedReader(new StringReader(
            "5\t3\tTEXT\tCount:\\t123 "
    ));
    List<IToken> tokenList = Token.load(reader);

    assertEquals(1, tokenList.size());

    IToken theToken = tokenList.get(0);
    assertEquals(TokenType.TEXT, theToken.getType());
    assertEquals("Count:\t123 ", theToken.getValue());
    assertEquals(5, theToken.getLineNumber());
    assertEquals(3, theToken.getCharacterNumber());
  }

  @Test
  public void testLoad_multiLine() {
    BufferedReader reader = new BufferedReader(new StringReader(
            "1\t1\tKEYWORD\tprint\n" +
            "1\t6\tBRACKET_ROUND_OPEN\n" +
            "1\t7\tTEXT\tHello\tworld!\\n\n" +
            "1\t21\tBRACKET_ROUND_CLOSE\n" +
            "1\t22\tNEWLINE\n" +
            "2\t1\tTERMINAL"
    ));
    List<IToken> tokenList = Token.load(reader);

    assertEquals(6, tokenList.size());

    assertEquals(TokenType.KEYWORD, tokenList.get(0).getType());
    assertEquals("print", tokenList.get(0).getValue());
    assertEquals(1, tokenList.get(0).getLineNumber());
    assertEquals(1, tokenList.get(0).getCharacterNumber());

    assertEquals(TokenType.BRACKET_ROUND_OPEN, tokenList.get(1).getType());
    assertNull(tokenList.get(1).getValue());
    assertEquals(1, tokenList.get(1).getLineNumber());
    assertEquals(6, tokenList.get(1).getCharacterNumber());

    assertEquals(TokenType.TEXT, tokenList.get(2).getType());
    assertEquals("Hello\tworld!\n", tokenList.get(2).getValue());
    assertEquals(1, tokenList.get(2).getLineNumber());
    assertEquals(7, tokenList.get(2).getCharacterNumber());

    assertEquals(TokenType.BRACKET_ROUND_CLOSE, tokenList.get(3).getType());
    assertNull(tokenList.get(3).getValue());
    assertEquals(1, tokenList.get(3).getLineNumber());
    assertEquals(21, tokenList.get(3).getCharacterNumber());

    assertEquals(TokenType.NEWLINE, tokenList.get(4).getType());
    assertNull(tokenList.get(4).getValue());
    assertEquals(1, tokenList.get(4).getLineNumber());
    assertEquals(22, tokenList.get(4).getCharacterNumber());

    assertEquals(TokenType.TERMINAL, tokenList.get(5).getType());
    assertNull(tokenList.get(5).getValue());
    assertEquals(2, tokenList.get(5).getLineNumber());
    assertEquals(1, tokenList.get(5).getCharacterNumber());
  }

  @Test
  public void testSaveAndLoad() throws IOException {
    List<IToken> inputTokenList = new LinkedList<>();
    inputTokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    inputTokenList.add(new Token(TokenType.BRACKET_ROUND_OPEN));
    inputTokenList.add(new Token(TokenType.KEYWORD).withValue("Hello\tworld!\\n"));
    inputTokenList.add(new Token(TokenType.BRACKET_ROUND_CLOSE));
    inputTokenList.add(new Token(TokenType.NEWLINE));
    inputTokenList.add(new Token(TokenType.TERMINAL));

    StringWriter stringWriter = new StringWriter();
    BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
    Token.save(bufferedWriter, inputTokenList);

    bufferedWriter.flush();

    StringReader stringReader = new StringReader(stringWriter.toString());
    BufferedReader bufferedReader = new BufferedReader(stringReader);
    List<IToken> outputTokenList = Token.load(bufferedReader);

    assertEquals(inputTokenList, outputTokenList);
  }
}
