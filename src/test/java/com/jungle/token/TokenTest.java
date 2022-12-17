package com.jungle.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class TokenTest {
  @Test
  public void testEquals() {
    assertNotEquals(new Token(TokenType.PLUS), new Token(TokenType.MINUS));
    assertEquals(new Token(TokenType.TEXT), new Token(TokenType.TEXT));
    assertEquals(new Token(TokenType.TEXT, "test"), new Token(TokenType.TEXT, "test"));
    assertNotEquals(new Token(TokenType.TEXT), new Token(TokenType.TEXT, "test"));
  }

  @Test
  public void testHashCode() {
    assertNotEquals(new Token(TokenType.PLUS).hashCode(), new Token(TokenType.MINUS).hashCode());
    assertEquals(new Token(TokenType.TEXT).hashCode(), new Token(TokenType.TEXT).hashCode());
    assertEquals(new Token(TokenType.TEXT, "test").hashCode(), new Token(TokenType.TEXT, "test").hashCode());
    assertNotEquals(new Token(TokenType.TEXT).hashCode(), new Token(TokenType.TEXT, "test").hashCode());
  }
}
