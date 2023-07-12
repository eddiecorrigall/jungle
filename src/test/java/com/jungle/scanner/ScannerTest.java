package com.jungle.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jungle.token.Token;
import com.jungle.token.TokenType;

public class ScannerTest {
  @Test
  public void testConsumeAlphabet_entireLine() {
    Scanner scanner = new Scanner(Collections.singletonList("while"));
    assertEquals("while", scanner.consumeAlphabetic());
    assertEquals(6, scanner.getCharacterNumber());
  }

  @Test
  public void testConsumeAlphabetic_firstWord() {
    Scanner scanner = new Scanner(Collections.singletonList("var x = 123;"));
    assertEquals("var", scanner.consumeAlphabetic());
    assertEquals(4, scanner.getCharacterNumber());
  }

  @Test
  public void testConsumeNumeric() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    scanner.consume(10);
    assertEquals(11, scanner.getCharacterNumber());
    assertEquals("123", scanner.consumeNumeric());
    assertEquals(14, scanner.getCharacterNumber());
  }

  @Test
  public void testScan() {
    List<String> keywords = Arrays.asList("if", "else", "var", "true", "false", "return", "println");
    Scanner scanner = new Scanner(Arrays.asList(
      "var inputNumber = 123;",      // line 1
      "if (inputNumber % 2 == 0) {", // line 2
      "  println(\"It's even!\");",  // line 3
      "  return true;",              // line 4
      "} else {",                    // line 5
      "  println(\"It's odd!\");",   // line 6
      "  return false;",             // line 7
      "}"                            // line 8
    ).iterator(), keywords);
    // line 1: "var inputNumber = 123;\n"
    assertEquals(new Token(TokenType.KEYWORD).withValue("var"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.SYMBOL).withValue("inputNumber"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.EQUALS), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.NUMBER).withValue("123"), scanner.scanToken());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 2: "if (inputNumber % 2 == 0) {\n"
    assertEquals(new Token(TokenType.KEYWORD).withValue("if"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scanToken());
    assertEquals(new Token(TokenType.SYMBOL).withValue("inputNumber"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.PERCENT), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.NUMBER).withValue("2"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.EQUALS), scanner.scanToken());
    assertEquals(new Token(TokenType.EQUALS), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.NUMBER).withValue("0"), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_CURLY_OPEN), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 3: "  println(\"It's even!\");\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("println"), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scanToken());
    assertEquals(new Token(TokenType.TEXT).withValue("It's even!"), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scanToken());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 4: "  return true;\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("return"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("true"), scanner.scanToken());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 5: "} else {\n"
    assertEquals(new Token(TokenType.BRACKET_CURLY_CLOSE), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("else"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_CURLY_OPEN), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 6: "  println(\"It's odd!\");\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("println"), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scanToken());
    assertEquals(new Token(TokenType.TEXT).withValue("It's odd!"), scanner.scanToken());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scanToken());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 7: "  return false;\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("return"), scanner.scanToken());
    assertEquals(new Token(TokenType.SPACE), scanner.scanToken());
    assertEquals(new Token(TokenType.KEYWORD).withValue("false"), scanner.scanToken());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // line 8: "}\n"
    assertEquals(new Token(TokenType.BRACKET_CURLY_CLOSE), scanner.scanToken());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scanToken());
    // terminal
    assertEquals(new Token(TokenType.TERMINAL), scanner.scanToken());
  }

  @Test
  public void testConsumeUntilAndSkip() {
    Scanner scanner = new Scanner(Collections.singletonList("var text = \"Hello world!\";"));
    scanner.consume(12);
    assertEquals("Hello world!", scanner.consumeUntilAndSkip('\"'));
    assertEquals(26, scanner.getCharacterNumber());
  }
}
