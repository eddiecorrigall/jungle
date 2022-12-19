package com.jungle.scanner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import com.jungle.error.SyntaxError;
import com.jungle.token.Token;
import com.jungle.token.TokenType;;

public class Scanner extends AbstractScanner {
  public final Set<String> keywords;

  public Scanner(Collection<String> keywords) {
    super();
    this.keywords = new HashSet<>();
    this.keywords.addAll(keywords);
  }

  @Override
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
      case '.': return new Token(TokenType.DOT);
      case '|': return new Token(TokenType.PIPE);
      // Text
      case '"': return new Token(TokenType.TEXT, consumeUntilAndSkip('"'));
      case '`': return new Token(TokenType.TEXT, consumeUntilAndSkip('`'));
      case '\'': return new Token(TokenType.TEXT, consumeUntilAndSkip('\''));
      // Whitespace
      case ' ': return new Token(TokenType.SPACE);
      case '\n': return new Token(TokenType.NEWLINE);
      case '\t': return new Token(TokenType.TAB);
      case '\r': return new Token(TokenType.RETURN);
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
    throw new SyntaxError(
      "Unknown character " + c,
      getLineNumber(),
      getCharacterNumber()
    );
  }
}
