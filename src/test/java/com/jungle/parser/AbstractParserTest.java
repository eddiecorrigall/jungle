package com.jungle.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jungle.token.IToken;
import com.jungle.token.Token;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import com.jungle.ast.INode;
import com.jungle.token.TokenType;

import java.util.LinkedList;
import java.util.List;

public class AbstractParserTest {
  @NotNull AbstractParser parser;

  @Before
  public void setup() {
    List<IToken> tokenList = new LinkedList<>();
    tokenList.add(new Token(TokenType.KEYWORD).withValue("print"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.PLUS));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("3"));
    tokenList.add(new Token(TokenType.SPACE));
    tokenList.add(new Token(TokenType.NUMBER).withValue("4"));
    tokenList.add(new Token(TokenType.TERMINAL));

    parser = new AbstractParser(tokenList.iterator()) {
      @Override
      @Nullable
      public INode parse() {
        throw new NotImplementedException();
      }
    };
  }

  @Test
  public void testFirstCurrentTokenIsUndefined() {
    assertNull(parser.getCurrentToken());
  }

  @Test
  public void testNextToken() {
    parser.nextToken();
    assertNotNull(parser.getCurrentToken());
    assertEquals(TokenType.KEYWORD, parser.getCurrentToken().getType());
  }

  @Test
  public void testAccept() {
    parser.nextToken();
    assertTrue(parser.accept(TokenType.KEYWORD));

    parser.nextToken();
    assertTrue(parser.accept(TokenType.SPACE));

    parser.nextToken();
    assertTrue(parser.accept(TokenType.PLUS));

    parser.nextToken();
    assertTrue(parser.accept(TokenType.SPACE));

    parser.nextToken();
    assertTrue(parser.accept(TokenType.NUMBER));

    parser.nextToken();
    assertTrue(parser.accept(TokenType.SPACE));

    parser.nextToken();
    assertTrue(parser.accept(TokenType.NUMBER));
  }

  @Test
  public void testNotAccept() {
    parser.nextToken();
    assertFalse(parser.accept(TokenType.PLUS));
  }

  @Test
  public void testExpect() {
    parser.nextToken();
    assertEquals("print", parser.expect(TokenType.KEYWORD));
    assertNull(parser.expect(TokenType.SPACE));
    assertNull(parser.expect(TokenType.PLUS));
    assertNull(parser.expect(TokenType.SPACE));
    assertEquals("3", parser.expect(TokenType.NUMBER));
    assertNull(parser.expect(TokenType.SPACE));
    assertEquals("4", parser.expect(TokenType.NUMBER));
  }

  @Test(expected = Error.class)
  public void testNotExpect() {
    parser.nextToken();
    parser.expect(TokenType.PLUS);
  }

  @Test
  public void testExpectKeyword() {
    parser.nextToken();
    parser.expectKeyword("print");
  }
}
