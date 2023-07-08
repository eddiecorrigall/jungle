package com.jungle.scanner;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

// TODO: Test abstract class only

public class AbstractScannerTest {
  @Test(expected = IndexOutOfBoundsException.class)
  public void testSetPositionNegative() {
    Scanner scanner = new Scanner(Collections.singletonList("Hello"));
    scanner.setCharacterIndex(-1);
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
  public void testConsume_invalidNegativeCharacterNumber() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    scanner.consume(-1);
  }

  @Test
  public void testConsume_validCharacterNumber() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    assertEquals("", scanner.consume(0));
    assertEquals(1, scanner.getCharacterNumber());
    assertEquals("var", scanner.consume(3));
    assertEquals(4, scanner.getCharacterNumber());
  }

  @Test()
  public void testConsume_invalidPositiveCharacterNumber() {
    Scanner scanner = new Scanner(Collections.singletonList("var abc = 123;"));
    String result = scanner.consume(100);
    assertEquals("var abc = 123;\n", result);
  }

  @Test
  public void testConsumeUntilAndSkip() {
    Scanner scanner = new Scanner(Collections.singletonList("var text = \"Hello world!\";"));
    scanner.consume(12);
    assertEquals("Hello world!", scanner.consumeUntilAndSkip('\"'));
    assertEquals(26, scanner.getCharacterNumber());
  }
}
