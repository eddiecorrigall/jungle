package com.jungle.scanner;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.jungle.token.Token;

public abstract class AbstractScanner {
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

  private String code;
  private int position;
  private int lineNumber;
  private int characterNumber;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public int getPosition() {
    return position;
  }

  protected void setPosition(int position) {
    this.position = position;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public int getCharacterNumber() {
    return characterNumber;
  }

  public void setCharacterNumber(int characterNumber) {
    this.characterNumber = characterNumber;
  }

  @NonNull
  public abstract Token scan();

  public List<Token> scanAll() {
    List<Token> tokens = new LinkedList<>();
    while (!isDone()) {
      tokens.add(scan());
    }
    return tokens;
  }

  protected boolean isValidOffset(int offset) {
    return (getPosition() + offset) < getCode().length();
  }

  public boolean isDone() {
    return !isValidOffset(0);
  }

  public void load(String code, int position) {
    setCode(code);
    if (position < 0) {
      throw new UnsupportedOperationException("position cannot be negative");
    }
    setPosition(position);
    setLineNumber(1);
    setCharacterNumber(1);
  }

  public void load(String code) {
    load(code, 0);
  }

  protected char consume() {
    if (!isDone()) {
      char c = getCode().charAt(getPosition());
      setPosition(getPosition() + 1);
      if (c == '\n') {
        setLineNumber(getLineNumber() + 1);
        setCharacterNumber(0);
      }
      return c;
    }
    return '\0';
  }

  @NonNull
  protected String consume(int offset) {
    if (offset < 0) {
      throw new UnsupportedOperationException(
        "offset cannot be negative"
      );
    }
    StringBuffer buffer = new StringBuffer();
    while (!isDone() && offset > 0) {
      buffer.append(consume());
      offset--;
    }
    return buffer.toString();
  }

  @NonNull
  protected String consumeNumberical() {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = getCode().charAt(getPosition() + offset);
      if (!isNumeric(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NonNull
  protected String consumeAlphabetical() {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = getCode().charAt(getPosition() + offset);
      if (!isAlphabetic(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NonNull
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
}
