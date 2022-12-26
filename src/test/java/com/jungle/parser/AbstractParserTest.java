package com.jungle.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Before;
import org.junit.Test;

import com.jungle.ast.INode;
import com.jungle.scanner.IScanner;
import com.jungle.scanner.Scanner;
import com.jungle.token.TokenType;

public class AbstractParserTest {
  @NonNull IScanner scanner = new Scanner(); // TODO: Use mock IScanner
  @NonNull AbstractParser parser = new AbstractParser(scanner) {
    @Override
    @Nullable
    public INode parse() {
      throw new NotImplementedException();
    }
  };

  @Before
  public void setup() {
    scanner.load("print + 3 4", 1);
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
