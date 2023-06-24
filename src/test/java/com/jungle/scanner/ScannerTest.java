package com.jungle.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.jungle.token.Token;
import com.jungle.token.TokenType;

public class ScannerTest {
  Scanner scanner;

  @Before
  public void setup() {
    scanner = new Scanner(Arrays.asList(
      "if", "else", "var", "true", "false", "return", "println"
    ));
  }

  @Test
  public void testScan() {
    scanner.load(
      "var inputNumber = 123;\n"      + // line 1
      "if (inputNumber % 2 == 0) {\n" + // line 2
      "  println(\"It's even!\");\n"  + // line 3
      "  return true;\n"              + // line 4
      "} else {\n"                    + // line 5
      "  println(\"It's odd!\");\n"   + // line 6
      "  return false;\n"             + // line 7
      "}\n",                            // line 8
      1
    );
    // line 1: "var inputNumber = 123;\n"
    assertEquals(new Token(TokenType.KEYWORD).withValue("var"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SYMBOL).withValue("inputNumber"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.EQUALS), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.NUMBER).withValue("123"), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 2: "if (inputNumber % 2 == 0) {\n"
    assertEquals(new Token(TokenType.KEYWORD).withValue("if"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.SYMBOL).withValue("inputNumber"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.PERCENT), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.NUMBER).withValue("2"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.EQUALS), scanner.scan());
    assertEquals(new Token(TokenType.EQUALS), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.NUMBER).withValue("0"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_CURLY_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 3: "  println(\"It's even!\");\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("println"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.TEXT).withValue("It's even!"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 4: "  return true;\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("return"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("true"), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 5: "} else {\n"
    assertEquals(new Token(TokenType.BRACKET_CURLY_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("else"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_CURLY_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 6: "  println(\"It's odd!\");\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("println"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.TEXT).withValue("It's odd!"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 7: "  return false;\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("return"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD).withValue("false"), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 8: "}\n"
    assertEquals(new Token(TokenType.BRACKET_CURLY_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // terminal
    assertNull(scanner.scan());
  }
}
