package com.jungle.scanner;

import java.util.*;

import com.jungle.token.IToken;
import com.jungle.token.Token;
import com.jungle.token.TokenType;
import org.jetbrains.annotations.NotNull;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;

public class Scanner extends AbstractScanner {
  public static final String KEYWORD_ASSERT = "assert";
  public static final String KEYWORD_IF = "if";
  public static final String KEYWORD_ELSE = "else";
  public static final String KEYWORD_LOOP = "loop";
  public static final String KEYWORD_PRINT = "print";
  public static final String KEYWORD_MULTITASK = "multitask";
  public static final String KEYWORD_AND = "and";
  public static final String KEYWORD_OR = "or";
  public static final String KEYWORD_NOT = "not";
  public static final String KEYWORD_EQUALS = "equals";
  public static final String KEYWORD_GREATER_THAN = "greaterThan";
  public static final String KEYWORD_LESS_THAN = "lessThan";
  public static final String KEYWORD_TRUE = "true";
  public static final String KEYWORD_FALSE = "false";

  // TODO: keyword "in" - if x in 1...3 { }

  public static final List<String> KEYWORDS = Arrays.asList(
          KEYWORD_ASSERT,
          KEYWORD_IF,
          KEYWORD_ELSE,
          KEYWORD_LOOP,
          KEYWORD_PRINT,
          KEYWORD_MULTITASK,
          KEYWORD_AND,
          KEYWORD_OR,
          KEYWORD_NOT,
          KEYWORD_EQUALS,
          KEYWORD_GREATER_THAN,
          KEYWORD_LESS_THAN,
          KEYWORD_TRUE,
          KEYWORD_FALSE
  );

  @NotNull
  private final Set<String> keywords;

  protected boolean isKeyword(@NotNull String keyword) {
    return keywords.contains(keyword);
  }

  public Scanner(@NotNull Iterator<String> lineIterator, @NotNull Collection<String> keywords) {
    super(lineIterator);
    this.keywords = new HashSet<>();
    this.keywords.addAll(keywords);
  }

  public Scanner(@NotNull Iterator<String> lineIterator) {
    this(lineIterator, KEYWORDS);
  }

  public Scanner(@NotNull Iterable<String> lineIterable) {
    this(lineIterable.iterator(), KEYWORDS);
  }

  @NotNull
  protected String consumeNumeric() {
    return consumeUntil((c) -> !isDigit(c));
  }

  @NotNull
  protected String consumeAlphabetic() {
    return consumeUntil((c) -> !isAlphabetic(c));
  }

  @NotNull
  protected  String consumeUntilAndSkip(char terminal) {
    String s = consumeUntil((c) -> c == terminal);
    consume(); // skip terminal
    return s;
  }

  @NotNull
  public IToken scanToken() {
    int lineNumber = getLineNumber();
    int characterNumber = getCharacterNumber();
    Token token;
    char c = consume();
    switch (c) {
      case '=':
        token = new Token(TokenType.EQUALS);
        break;
      case '+':
        token = new Token(TokenType.PLUS);
        break;
      case '-':
        token = new Token(TokenType.MINUS);
        break;
      case '*':
        token = new Token(TokenType.ASTERISK);
        break;
      case '%':
        token = new Token(TokenType.PERCENT);
        break;
      case '/':
        token = new Token(TokenType.SLASH_RIGHT);
        break;
      case '\\':
        token = new Token(TokenType.SLASH_LEFT);
        break;
      case '<':
        token = new Token(TokenType.BRACKET_ANGLE_OPEN);
        break;
      case '>':
        token = new Token(TokenType.BRACKET_ANGLE_CLOSE);
        break;
      case '{':
        token = new Token(TokenType.BRACKET_CURLY_OPEN);
        break;
      case '}':
        token = new Token(TokenType.BRACKET_CURLY_CLOSE);
        break;
      case '(':
        token = new Token(TokenType.BRACKET_ROUND_OPEN);
        break;
      case ')':
        token = new Token(TokenType.BRACKET_ROUND_CLOSE);
        break;
      case '[':
        token = new Token(TokenType.BRACKET_SQUARE_OPEN);
        break;
      case ']':
        token = new Token(TokenType.BRACKET_SQUARE_CLOSE);
        break;
      case ';':
        token = new Token(TokenType.SEMICOLON);
        break;
      case ':':
        token = new Token(TokenType.COLON);
        break;
      case ',':
        token = new Token(TokenType.COMMA);
        break;
      case '.':
        token = new Token(TokenType.DOT);
        break;
      case '|':
        token = new Token(TokenType.PIPE);
        break;
      // Text
      case '"':
        token = new Token(TokenType.TEXT).withValue(consumeUntilAndSkip('"'));
        break;
      case '`':
        token = new Token(TokenType.TEXT).withValue(consumeUntilAndSkip('`'));
        break;
      case '\'':
        token = new Token(TokenType.TEXT).withValue(consumeUntilAndSkip('\''));
        break;
      // Whitespace
      case ' ':
        token = new Token(TokenType.SPACE);
        break;
      case '\n':
        token = new Token(TokenType.NEWLINE);
        break;
      case '\t':
        token = new Token(TokenType.TAB);
        break;
      case '\r':
        token = new Token(TokenType.RETURN);
        break;
      case '\0':
        token = new Token(TokenType.TERMINAL);
        break;
      default: {
        if (isDigit(c)) {
          String s = c + consumeNumeric();
          token = new Token(TokenType.NUMBER).withValue(s);
        } else if (isAlphabetic(c)) {
          String s = c + consumeAlphabetic();
          if (isKeyword(s)) {
            token = new Token(TokenType.KEYWORD).withValue(s);
          } else {
            token = new Token(TokenType.SYMBOL).withValue(s);
          }
        } else {
          token = new Token(TokenType.UNKNOWN);
        }
      }
      break;
    }
    return token.withPosition(lineNumber, characterNumber);
  }

  @NotNull
  public Iterable<IToken> scan() {
    List<IToken> tokenList = new LinkedList<>();
    IToken token;
    do {
      token = scanToken();
      tokenList.add(token);
    } while (!TokenType.TERMINAL.equals(token.getType()));
    return tokenList;
  }
}
