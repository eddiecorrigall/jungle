package com.jungle.scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import com.jungle.token.Token;
import org.jetbrains.annotations.NotNull;

import com.jungle.token.IToken;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScanner implements IScanner {
  protected static boolean isLowercaseAlphabetic(char c) {
    return 'a' <= c && c <= 'z';
  }

  protected static boolean isUppercaseAlphabetic(char c) {
    return 'A' <= c && c <= 'Z';
  }

  protected static boolean isAlphabetic(char c) {
    return isLowercaseAlphabetic(c) || isUppercaseAlphabetic(c);
  }

  protected static boolean isNumeric(char c) {
    return '0' <= c && c <= '9';
  }

  @NotNull
  private String line;
  private int lineNumber;
  @NotNull
  private final Iterator<String> lineIterator;

  private int characterIndex;
  private int characterNumber;

  public AbstractScanner(@NotNull Iterator<String> lineIterator) {
    super();
    this.line = "";
    this.lineIterator = lineIterator;
    if (hasNextLine()) {
      nextLine();
    }
  }

  private void nextLine() {
    setLine(lineIterator.next() + '\n');
    setLineNumber(getLineNumber() + 1);
    setCharacterIndex(0);
    setCharacterNumber(1);
  }

  private boolean hasNextLine() {
    return lineIterator.hasNext();
  }

  @NotNull
  protected String getLine() {
    return line;
  }

  private void setLine(@NotNull String line) {
    this.line = line;
  }

  protected int getCharacterIndex() {
    return characterIndex;
  }

  void setCharacterIndex(int characterIndex) {
    if (characterIndex < 0) {
      throw new IndexOutOfBoundsException("position must be non-negative");
    }
    this.characterIndex = characterIndex;
  }

  protected int getLineNumber() {
    return lineNumber;
  }

  void setLineNumber(int lineNumber) {
    if (lineNumber <= 0) {
      throw new UnsupportedOperationException("line number must be positive");
    }
    this.lineNumber = lineNumber;
  }

  protected int getCharacterNumber() {
    return characterNumber;
  }

  void setCharacterNumber(int characterNumber) {
    if (characterNumber <= 0) {
      throw new UnsupportedOperationException("character number must be positive");
    }
    this.characterNumber = characterNumber;
  }

  private boolean isValidCharacterIndexOffset(int offset) {
    return (getCharacterIndex() + offset) < getLine().length();
  }

  private boolean hasNextCharacter() {
    if (hasNextLine()) return false;
    return !isValidCharacterIndexOffset(0);
  }

  @Override
  @Nullable
  public abstract Iterable<IToken> scan();

  protected char consume() {
    if (!hasNextCharacter()) {
      char c = getLine().charAt(getCharacterIndex());
      setCharacterIndex(getCharacterIndex() + 1);
      setCharacterNumber(getCharacterNumber() + 1);
      if (c == '\n') {
        if (hasNextLine()) {
          nextLine();
        }
      }
      return c;
    }
    return '\0';
  }

  @NotNull
  protected String consume(int offset) {
    if (offset < 0) {
      throw new UnsupportedOperationException("offset must be non-negative");
    }
    StringBuilder builder = new StringBuilder();
    while (!hasNextCharacter() && offset > 0) {
      builder.append(consume());
      offset--;
    }
    return builder.toString();
  }

  @NotNull
  protected String consumeNumeric() {
    int offset = 0;
    while (isValidCharacterIndexOffset(offset)) {
      char c = getLine().charAt(getCharacterIndex() + offset);
      if (!isNumeric(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NotNull
  protected String consumeAlphabetic() {
    int offset = 0;
    while (isValidCharacterIndexOffset(offset)) {
      char c = getLine().charAt(getCharacterIndex() + offset);
      if (!isAlphabetic(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NotNull
  protected  String consumeUntilAndSkip(char terminal) {
    int offset = 0;
    while (isValidCharacterIndexOffset(offset)) {
      char c = getLine().charAt(getCharacterIndex() + offset);
      if (c == terminal) break;
      offset++;
    }
    String s = consume(offset);
    consume(); // skip terminal
    return s;
  }
}
