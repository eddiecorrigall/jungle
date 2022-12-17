package com.jungle.scanner;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import com.jungle.error.SyntaxError;
import com.jungle.token.Token;
import com.jungle.token.TokenType;;

public class Scanner {
  public final Set<String> keywords;
  private String code;
  private int position;

  private int line;
  private int character;

  public Scanner(Collection<String> keywords) {
    super();
    this.keywords = new HashSet<>();
    this.keywords.addAll(keywords);
  }

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

  protected char consume() {
    if (!isDone()) {
      char c = code.charAt(getPosition());
      position += 1;
      if (c == '\n') {
        line++;
        character = 0;
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
      char c = code.charAt(getPosition() + offset);
      if (!isNumeric(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NonNull
  protected String consumeAlphabetical() {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = code.charAt(getPosition() + offset);
      if (!isAlphabetic(c)) break;
      offset++;
    }
    return consume(offset);
  }

  @NonNull
  protected  String consumeUntilAndSkip(char terminal) {
    int offset = 0;
    while (isValidOffset(offset)) {
      char c = code.charAt(getPosition() + offset);
      if (c == terminal) break;
      offset++;
    }
    String s = consume(offset);
    consume(); // skip terminal
    return s;
  }

  protected boolean isValidOffset(int offset) {
    return (getPosition() + offset) < code.length();
  }

  public boolean isDone() {
    return !isValidOffset(0);
  }

  public int getPosition() {
    return position;
  }

  public void load(String code, int position) {
    this.code = code;
    if (position < 0) {
      throw new UnsupportedOperationException("position cannot be negative");
    }
    this.position = position;
    this.line = 0;
    this.character = 0;
  }

  public void load(String code) {
    load(code, 0);
  }

  @NonNull
  public Token scan() {
    if (isDone()) {
      return new Token(TokenType.TERMINAL);
    }
    char c = consume();
    switch (c) {
      case '=': return new Token(TokenType.EQUALS);
      case '+': return new Token(TokenType.PLUS);
      case '-': return new Token(TokenType.MINUS);
      case '*': return new Token(TokenType.ASTERISK);
      case '%': return new Token(TokenType.PERCENT);
      case '/': return new Token(TokenType.SLASH_RIGHT);
      case '\\': return new Token(TokenType.SLASH_LEFT);
      case '{': return new Token(TokenType.BRACKET_CURLY_OPEN);
      case '}': return new Token(TokenType.BRACKET_CURLY_CLOSE);
      case '(': return new Token(TokenType.BRACKET_ROUND_OPEN);
      case ')': return new Token(TokenType.BRACKET_ROUND_CLOSE);
      case '[': return new Token(TokenType.BRACKET_SQUARE_OPEN);
      case ']': return new Token(TokenType.BRACKET_SQUARE_CLOSE);
      case ';': return new Token(TokenType.SEMICOLON);
      case ':': return new Token(TokenType.COLON);
      case ',': return new Token(TokenType.COMMA);
      // Text
      case '"': return new Token(TokenType.TEXT, consumeUntilAndSkip('"'));
      case '`': return new Token(TokenType.TEXT, consumeUntilAndSkip('`'));
      case '\'': return new Token(TokenType.TEXT, consumeUntilAndSkip('\''));
      // Whitespace
      case ' ': return new Token(TokenType.SPACE);
      case '\n': return new Token(TokenType.NEWLINE);
      case '\t': return new Token(TokenType.TAB);
      case '\r': return new Token(TokenType.RETURN);
      case '.': return new Token(TokenType.DOT);
    }
    if (isNumeric(c)) {
      String s = c + consumeNumberical();
      return new Token(TokenType.NUMBER, s);
    } else if (isAlphabetic(c)) {
      String s = c + consumeAlphabetical();
      if (keywords.contains(s)) {
        return new Token(TokenType.KEYWORD, s);
      } else {
        return new Token(TokenType.SYMBOL, s);
      }
    }
    throw new SyntaxError("Unknown character " + c, line, character);
  }

  public List<Token> scanAll() {
    List<Token> tokens = new LinkedList<>();
    while (!isDone()) {
      tokens.add(scan());
    }
    return tokens;
  }
}
