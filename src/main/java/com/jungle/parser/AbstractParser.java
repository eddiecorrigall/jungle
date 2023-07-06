package com.jungle.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;
import com.jungle.token.IToken;
import com.jungle.token.TokenType;

import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractParser implements IParser {
  @NotNull
  private final Iterator<IToken> tokenIterator;

  @Nullable
  private IToken token;

  public AbstractParser(@NotNull Iterator<IToken> tokenIterator) {
    super();
    this.tokenIterator = tokenIterator;
  }

  public AbstractParser(@NotNull Iterable<IToken> tokenIterable) {
    this(tokenIterable.iterator());
  }

  @Override
  @Nullable
  public abstract INode parse();

  @Nullable
  protected IToken getCurrentToken() {
    return token;
  }

  protected void nextToken() {
    if (tokenIterator.hasNext()) {
      token = tokenIterator.next();
    } else {
      token = null;
    }
  }

  protected void consumeWhitespace() {
    while (true) {
      if (getCurrentToken() == null) break;
      if (accepts(TokenType.TERMINAL)) break;
      if (accepts(TokenType.SPACE, TokenType.TAB, TokenType.NEWLINE)) {
        nextToken();
        continue;
      }
      break;
    }
  }

  private boolean accept(@NotNull TokenType tokenType) {
    return getCurrentToken() != null && tokenType.equals(getCurrentToken().getType());
  }

  protected boolean accepts(@NotNull TokenType... tokenTypes) {
    for (TokenType tokenType : tokenTypes) {
      if (accept(tokenType)) {
        return true;
      }
    }
    return false;
  }

  protected boolean acceptKeyword(@NotNull String keywordValue) {
    return accept(TokenType.KEYWORD)
            && getCurrentToken() != null
            && keywordValue.equals(getCurrentToken().getValue());
  }

  protected boolean acceptKeywords(@NotNull String... keywordValues) {
    for (String keywordValue : keywordValues) {
      if (acceptKeyword(keywordValue)) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  protected String expect(@NotNull TokenType expectedTokenType) {
    if (accept(expectedTokenType)) {
      assert getCurrentToken() != null;
      String value = getCurrentToken().getValue();
      nextToken();
      return value;
    }
    throw newError("expected token type " + expectedTokenType.name() + " but got token " + getCurrentToken());
  }

  protected void expectKeyword(@NotNull String expectedTokenValue) {
    String observedTokenValue = expect(TokenType.KEYWORD);
    if (!Objects.equals(expectedTokenValue, observedTokenValue)) {
      throw newError("expected keyword token with value " + expectedTokenValue);
    }
  }

  protected Error newError(@NotNull String message) {
    return new Error(message + " " + getCurrentToken());
  }
}
