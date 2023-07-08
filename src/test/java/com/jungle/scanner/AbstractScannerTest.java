package com.jungle.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

// TODO: Test abstract class only

public class AbstractScannerTest {
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
  public void testIsNumeric() {
    // TODO convert to parameterized test
    for (char c : Arrays.asList('\0', ' ', '.', '-', 'f')) {
      assertFalse(Scanner.isNumeric(c));
    }
    for (char c : Arrays.asList('4', '0', '9')) {
      assertTrue(Scanner.isNumeric(c));
    }
  }

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

  @Test(expected = IndexOutOfBoundsException.class)
  public void testSetPositionNegative() {
    Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    scanner.setPosition(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetLineNumberNegative() {
    Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    scanner.setLineNumber(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetLineNumberZero() {
    Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    scanner.setLineNumber(0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetCharacterNumberNegative() {
  Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    scanner.setCharacterNumber(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetCharacterNumberZero() {
    Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    scanner.setCharacterNumber(0);
  }

  @Test
  public void testConsume_singleLine() {
    Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    assertEquals(1, scanner.getCharacterNumber());
    assertEquals('H', scanner.consume());
    assertEquals(2, scanner.getCharacterNumber());
    assertEquals('e', scanner.consume());
    assertEquals(3, scanner.getCharacterNumber());
    assertEquals('l', scanner.consume());
    assertEquals(4, scanner.getCharacterNumber());
    assertEquals('l', scanner.consume());
    assertEquals(5, scanner.getCharacterNumber());
    assertEquals('o', scanner.consume());
    assertEquals(6, scanner.getCharacterNumber());
    assertEquals('\n', scanner.consume());
    assertEquals(7, scanner.getCharacterNumber());
    assertEquals('\0', scanner.consume());
    assertEquals('\0', scanner.consume());
    assertEquals('\0', scanner.consume());
    assertEquals(7, scanner.getCharacterNumber());
  }

  @Test
  public void testConsume_multipleLines() {
    Scanner scanner = new Scanner(Arrays.asList("Hello", "World!"));
    assertEquals(1, scanner.getCharacterNumber());
    assertEquals('H', scanner.consume());
    assertEquals(2, scanner.getCharacterNumber());
    assertEquals('e', scanner.consume());
    assertEquals(3, scanner.getCharacterNumber());
    assertEquals('l', scanner.consume());
    assertEquals(4, scanner.getCharacterNumber());
    assertEquals('l', scanner.consume());
    assertEquals(5, scanner.getCharacterNumber());
    assertEquals('o', scanner.consume());
    assertEquals(6, scanner.getCharacterNumber());
    assertEquals('\n', scanner.consume());
    assertEquals(1, scanner.getCharacterNumber());
    assertEquals('W', scanner.consume());
    assertEquals(2, scanner.getCharacterNumber());
    assertEquals('o', scanner.consume());
    assertEquals(3, scanner.getCharacterNumber());
    assertEquals('r', scanner.consume());
    assertEquals(4, scanner.getCharacterNumber());
    assertEquals('l', scanner.consume());
    assertEquals(5, scanner.getCharacterNumber());
    assertEquals('d', scanner.consume());
    assertEquals(6, scanner.getCharacterNumber());
    assertEquals('!', scanner.consume());
    assertEquals(7, scanner.getCharacterNumber());
    assertEquals('\n', scanner.consume());
    assertEquals(8, scanner.getCharacterNumber());
    assertEquals('\0', scanner.consume());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testConsumeUntil_invalidNegativeCharacterNumber() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    scanner.consume(-1);
  }

  @Test
  public void testConsumeUntil_validCharacterNumber() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    assertEquals("", scanner.consume(0));
    assertEquals(1, scanner.getCharacterNumber());
    assertEquals("var", scanner.consume(3));
    assertEquals(4, scanner.getCharacterNumber());
  }

  @Test()
  public void testConsumeUntil_invalidPositiveCharacterNumber() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    String result = scanner.consume(100);
    assertEquals("var abc = 123;\n", result);
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
  public void testConsumeUntilAndSkip() {
    Scanner scanner = new Scanner(Collections.singletonList("var text = \"Hello world!\";"));
    scanner.consume(12);
    assertEquals("Hello world!", scanner.consumeUntilAndSkip('\"'));
    assertEquals(26, scanner.getCharacterNumber());
  }
}
