package com.jungle.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

// TODO: Test abstract class only

public class AbstractScannerTest {
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
    scanner.load("while", 1);
    assertEquals("while", scanner.consumeAlphabetical());
  }

  @Test
  public void consumeAlphabeticalNotToEndOfFile() {
    scanner.load("var x = 123;", 1);
    assertEquals("var", scanner.consumeAlphabetical());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testSetPositionNegative() {
    scanner.setPosition(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetLineNumberNegative() {
    scanner.setLineNumber(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetLineNumberZero() {
    scanner.setLineNumber(0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetCharacterNumberNegative() {
    scanner.setCharacterNumber(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetCharacterNumberZero() {
    scanner.setCharacterNumber(0);
  }

  @Test
  public void testConsumeChar() {
    scanner.load("hello", 1);
    scanner.setPosition(4);
    assertEquals('o', scanner.consume());
    assertEquals(5, scanner.getPosition());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testConsumeOffsetNegative() {
    scanner.load("var abc = 123;\n", 1);
    scanner.consume(-1);
  }

  @Test
  public void testConsumeOffsetZero() {
    scanner.load("hello", 1);
    scanner.setPosition(4);
    assertEquals("", scanner.consume(0));
  }

  @Test
  public void testConsumeOffsetPositive() {
    scanner.load("var abc = 123;\n", 1);
    assertEquals("var", scanner.consume(3));
    assertEquals(3, scanner.getPosition());
  }

  @Test
  public void testConsumeNumeric() {
    scanner.load("var abc = 123;\n", 1);
    scanner.setPosition(10);
    assertEquals("123", scanner.consumeNumerical());
    assertEquals(13, scanner.getPosition());
  }

  @Test
  public void testConsumeUntilAndSkip() {
    scanner.load("var text = \"Hello world!\";", 1);
    scanner.setPosition(12);
    assertEquals("Hello world!", scanner.consumeUntilAndSkip('\"'));
    assertEquals(25, scanner.getPosition());
  }
}
