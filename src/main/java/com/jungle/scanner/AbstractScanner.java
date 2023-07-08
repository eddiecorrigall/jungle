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
  private String code;

  @NotNull
  private final Iterator<String> lineIterator;

  private int position;
  private int lineNumber;
  private int characterNumber;

  public AbstractScanner(@NotNull Iterator<String> lineIterator) {
    super();
    this.code = "";
    this.lineIterator = lineIterator;
    if (hasNextLine()) {
      nextLine();
    }
  }

  protected void nextLine() {
    setCode(lineIterator.next() + '\n');
    setLineNumber(getLineNumber() + 1);
    setPosition(0);
    setCharacterNumber(1);
  }

  protected boolean hasNextLine() {
    return lineIterator.hasNext();
  }

  @NotNull
  protected String getCode() {
    return code;
  }

  protected void setCode(@NotNull String code) {
    this.code = code;
  }

  protected int getPosition() {
    return position;
  }

  protected void setPosition(int position) {
    if (position < 0) {
      throw new IndexOutOfBoundsException("position must not be negative");
    }
    this.position = position;
  }

  protected int getLineNumber() {
    return lineNumber;
  }

  protected void setLineNumber(int lineNumber) {
    if (lineNumber <= 0) {
      throw new UnsupportedOperationException("line number must be positive");
    }
    this.lineNumber = lineNumber;
  }

  protected int getCharacterNumber() {
    return characterNumber;
  }

  protected void setCharacterNumber(int characterNumber) {
    if (characterNumber <= 0) {
      throw new UnsupportedOperationException("character number must be positive");
    }
    this.characterNumber = characterNumber;
  }

  protected boolean isValidOffset(int offset) {
    return (getPosition() + offset) < getCode().length();
  }

  public boolean isDone() {
    if (hasNextLine()) return false;
    return !isValidOffset(0);
  }

  @Override
  @Nullable
  public abstract Iterable<IToken> scan();

  protected char consume() {
    if (!isDone()) {
      char c = getCode().charAt(getPosition());
      setPosition(getPosition() + 1);
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
      throw new UnsupportedOperationException(
        "offset cannot be negative"
      );
    }
    StringBuilder builder = new StringBuilder();
    while (!isDone() && offset > 0) {
      builder.append(consume());
      offset--;
    }
    return builder.toString();
  }

  @NotNull
  protected String consumeNumeric() {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = getCode().charAt(getPosition() + offset);
      if (!isNumeric(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NotNull
  protected String consumeAlphabetic() {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = getCode().charAt(getPosition() + offset);
      if (!isAlphabetic(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NotNull
  protected  String consumeUntilAndSkip(char terminal) {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = getCode().charAt(getPosition() + offset);
      if (c == terminal) break;
      offset++;
    }
    String s = consume(offset);
    consume(); // skip terminal
    return s;
  }

  public static void tokenize(
    BufferedReader reader,
    BufferedWriter writer
  ) throws IOException {
    Iterator<String> lineIterator = reader.lines().iterator();
    Scanner scanner = new Scanner(lineIterator);
    Iterable<IToken> tokenList = scanner.scan();
    Token.save(writer, tokenList);
  }
}
