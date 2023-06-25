package com.jungle.scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jungle.token.IToken;
import com.jungle.token.Token;
import com.jungle.token.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Scanner extends AbstractScanner {
  public static final List<String> KEYWORDS = Arrays.asList(
          "assert",
          "if",
          "loop",
          "print",
          "not",
          "and",
          "or"
  );

  @NotNull
  private final Set<String> keywords;

  protected boolean isKeyword(@NotNull String keyword) {
    return keywords.contains(keyword);
  }

  public Scanner(Collection<String> keywords) {
    super();
    this.keywords = new HashSet<>();
    this.keywords.addAll(keywords);
  }

  public Scanner() {
    this(KEYWORDS);
  }

  @Override
  @Nullable
  public IToken scan() {
    int lineNumber = getLineNumber();
    int characterNumber = getCharacterNumber();
    Token token;
    if (isDone()) {
      return null; // end of line
    } else {
      char c = consume();
      switch (c) {
        case '=': token = new Token(TokenType.EQUALS); break;
        case '+': token = new Token(TokenType.PLUS); break;
        case '-': token = new Token(TokenType.MINUS); break;
        case '*': token = new Token(TokenType.ASTERISK); break;
        case '%': token = new Token(TokenType.PERCENT); break;
        case '/': token = new Token(TokenType.SLASH_RIGHT); break;
        case '\\': token = new Token(TokenType.SLASH_LEFT); break;
        case '<': token = new Token(TokenType.BRACKET_ANGLE_OPEN); break;
        case '>': token = new Token(TokenType.BRACKET_ANGLE_CLOSE); break;
        case '{': token = new Token(TokenType.BRACKET_CURLY_OPEN); break;
        case '}': token = new Token(TokenType.BRACKET_CURLY_CLOSE); break;
        case '(': token = new Token(TokenType.BRACKET_ROUND_OPEN); break;
        case ')': token = new Token(TokenType.BRACKET_ROUND_CLOSE); break;
        case '[': token = new Token(TokenType.BRACKET_SQUARE_OPEN); break;
        case ']': token = new Token(TokenType.BRACKET_SQUARE_CLOSE); break;
        case ';': token = new Token(TokenType.SEMICOLON); break;
        case ':': token = new Token(TokenType.COLON); break;
        case ',': token = new Token(TokenType.COMMA); break;
        case '.': token = new Token(TokenType.DOT); break;
        case '|': token = new Token(TokenType.PIPE); break;
        // Text
        case '"': token = new Token(TokenType.TEXT).withValue(consumeUntilAndSkip('"')); break;
        case '`': token = new Token(TokenType.TEXT).withValue(consumeUntilAndSkip('`')); break;
        case '\'': token = new Token(TokenType.TEXT).withValue(consumeUntilAndSkip('\'')); break;
        // Whitespace
        case ' ': token = new Token(TokenType.SPACE); break;
        case '\n': token = new Token(TokenType.NEWLINE); break;
        case '\t': token = new Token(TokenType.TAB); break;
        case '\r': token = new Token(TokenType.RETURN); break;
        default: {
          if (isNumeric(c)) {
            String s = c + consumeNumerical();
            token = new Token(TokenType.NUMBER).withValue(s);
          } else if (isAlphabetic(c)) {
            String s = c + consumeAlphabetical();
            if (isKeyword(s)) {
              token = new Token(TokenType.KEYWORD).withValue(s);
            } else {
              token = new Token(TokenType.SYMBOL).withValue(s);
            }
          } else {
            token = new Token(TokenType.UNKNOWN);
          }
        } break;
      }
    }
    return token.withPosition(lineNumber, characterNumber);
  }

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    tokenize(reader, writer, new Scanner());
    writer.flush();
    writer.close();
    reader.close();
  }
}
