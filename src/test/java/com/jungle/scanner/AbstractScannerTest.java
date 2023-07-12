package com.jungle.scanner;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import com.jungle.token.IToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class AbstractScannerTest {
  private static class TestScanner extends AbstractScanner {
    public TestScanner(@NotNull Iterable<String> lineIterable) {
      super(lineIterable.iterator());
    }

    @Override
    public @Nullable Iterable<IToken> scan() {
      throw new UnsupportedOperationException("not implemented");
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testSetPositionNegative() {
    TestScanner scanner = new TestScanner(Collections.singletonList("Hello"));
    scanner.setCharacterIndex(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetLineNumberNegative() {
    TestScanner scanner = new TestScanner(Collections.singletonList("Hello"));
    scanner.setLineNumber(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetLineNumberZero() {
    TestScanner scanner = new TestScanner(Collections.singletonList("Hello"));
    scanner.setLineNumber(0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetCharacterNumberNegative() {
    TestScanner scanner = new TestScanner(Collections.singletonList("Hello"));
    scanner.setCharacterNumber(-1);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetCharacterNumberZero() {
    TestScanner scanner = new TestScanner(Collections.singletonList("Hello"));
    scanner.setCharacterNumber(0);
  }

  @Test
  public void testConsume_singleLine() {
    TestScanner scanner = new TestScanner(Collections.singletonList("Hello"));
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
    TestScanner scanner = new TestScanner(Arrays.asList("Hello", "World!"));
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
    TestScanner scanner = new TestScanner(Collections.singletonList("var abc = 123;"));
    scanner.consume(-1);
  }

  @Test
  public void testConsume_validCharacterNumber() {
    TestScanner scanner = new TestScanner(Collections.singletonList("var abc = 123;"));
    assertEquals("", scanner.consume(0));
    assertEquals(1, scanner.getCharacterNumber());
    assertEquals("var", scanner.consume(3));
    assertEquals(4, scanner.getCharacterNumber());
  }

  @Test()
  public void testConsume_invalidPositiveCharacterNumber() {
    TestScanner scanner = new TestScanner(Collections.singletonList("var abc = 123;"));
    String result = scanner.consume(100);
    assertEquals("var abc = 123;\n", result);
  }
}
