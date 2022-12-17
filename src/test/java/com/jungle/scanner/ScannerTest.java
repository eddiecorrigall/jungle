package com.jungle.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
  public void testIsAlphabetic() {
    // TODO convert to parameterized test
    for (char c : Arrays.asList('0', ' ', '$', '&')) {
      assertFalse(Scanner.isAlphabetic(c));
    }
    for (char c : Arrays.asList('a', 'h', 'I', 'Z')) {
      assertTrue(Scanner.isAlphabetic(c));
    }
  }

  @Test
  public void consumeAlphabeticToEndOfFile() {
    scanner.load("while");
    assertEquals("while", scanner.consumeAlphabetical());
  }

  @Test
  public void consumeAlphabeticalNotToEndOfFile() {
    scanner.load("var x = 123;");
    assertEquals("var", scanner.consumeAlphabetical());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLoadPositionNegative() {
    scanner.load("console.log(\"hello world\");", -1);
  }

  @Test
  public void testConsumeChar() {
    scanner.load("hello", 4);
    assertEquals('o', scanner.consume());
    assertEquals(5, scanner.getPosition());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testConsumeOffsetNegative() {
    scanner.load("var abc = 123;\n");
    scanner.consume(-1);
  }

  @Test
  public void testConsumeOffsetZero() {
    scanner.load("hello", 4);
    assertEquals("", scanner.consume(0));
  }

  @Test
  public void testConsumeOffsetPositive() {
    scanner.load("var abc = 123;\n");
    assertEquals("var", scanner.consume(3));
    assertEquals(3, scanner.getPosition());
  }

  @Test
  public void testConsumeNumeric() {
    scanner.load("var abc = 123;\n", 10);
    assertEquals("123", scanner.consumeNumberical());
    assertEquals(13, scanner.getPosition());
  }

  @Test
  public void testConsumeUntilAndSkip() {
    scanner.load("var text = \"Hello world!\";", 12);
    assertEquals("Hello world!", scanner.consumeUntilAndSkip('\"'));
    assertEquals(25, scanner.getPosition());
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
      "}\n"                             // line 8
    );
    // line 1: "var inputNumber = 123;\n"
    assertEquals(new Token(TokenType.KEYWORD, "var"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SYMBOL, "inputNumber"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.EQUALS), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.NUMBER, "123"), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 2: "if (inputNumber % 2 == 0) {\n"
    assertEquals(new Token(TokenType.KEYWORD, "if"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.SYMBOL, "inputNumber"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.PERCENT), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.NUMBER, "2"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.EQUALS), scanner.scan());
    assertEquals(new Token(TokenType.EQUALS), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.NUMBER, "0"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_CURLY_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 3: "  println(\"It's even!\");\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "println"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.TEXT, "It's even!"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 4: "  return true;\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "return"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "true"), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 5: "} else {\n"
    assertEquals(new Token(TokenType.BRACKET_CURLY_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "else"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_CURLY_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 6: "  println(\"It's odd!\");\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "println"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_OPEN), scanner.scan());
    assertEquals(new Token(TokenType.TEXT, "It's odd!"), scanner.scan());
    assertEquals(new Token(TokenType.BRACKET_ROUND_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 7: "  return false;\n"
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "return"), scanner.scan());
    assertEquals(new Token(TokenType.SPACE), scanner.scan());
    assertEquals(new Token(TokenType.KEYWORD, "false"), scanner.scan());
    assertEquals(new Token(TokenType.SEMICOLON), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // line 8: "}\n"
    assertEquals(new Token(TokenType.BRACKET_CURLY_CLOSE), scanner.scan());
    assertEquals(new Token(TokenType.NEWLINE), scanner.scan());
    // terminal
    assertEquals(new Token(TokenType.TERMINAL), scanner.scan());
  }
}
