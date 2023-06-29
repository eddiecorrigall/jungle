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

  @Override
  @Nullable
  public abstract INode parse();

  @Nullable
  public IToken getCurrentToken() {
    return token;
  }

  public void nextToken() {
    if (tokenIterator.hasNext()) {
      token = tokenIterator.next();
    } else {
      token = null;
    }
  }

  protected void consumeWhitespace() {
    while (true) {
      if (getCurrentToken() == null) break;
      if (getCurrentToken().getType() == TokenType.TERMINAL) break;
      switch (getCurrentToken().getType()) {
        case SPACE: case TAB: case NEWLINE: {
          nextToken();
          continue;
        }
      }
      break;
    }
  }

  protected boolean accept(@NotNull TokenType tokenType) {
    return getCurrentToken() != null && getCurrentToken().getType() == tokenType;
  }

  protected boolean acceptKeyword(@NotNull String keywordValue) {
    return accept(TokenType.KEYWORD)
            && getCurrentToken() != null
            && keywordValue.equals(getCurrentToken().getValue());
  }

  @Nullable
  protected String expect(@NotNull TokenType tokenType) {
    if (accept(tokenType) && getCurrentToken() != null) {
      String value = getCurrentToken().getValue();
      nextToken();
      return value;
    }
    throw new Error("expected token type " + tokenType.name() + " but got token " + getCurrentToken());
  }

  protected void expectKeyword(@NotNull String expectedTokenValue) {
    String observedTokenValue = expect(TokenType.KEYWORD);
    if (!Objects.equals(expectedTokenValue, observedTokenValue)) {
      throw new Error("expected keyword token with value " + expectedTokenValue);
    }
  }
}
