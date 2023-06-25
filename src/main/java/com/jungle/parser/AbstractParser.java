package com.jungle.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;
import com.jungle.scanner.IScanner;
import com.jungle.token.IToken;
import com.jungle.token.TokenType;

import java.util.Objects;

public abstract class AbstractParser implements IParser {
  @Nullable
  private IToken token;

  @NotNull
  private final IScanner scanner;

  public AbstractParser(@NotNull IScanner scanner) {
    this.scanner = scanner;
  }

  @Override
  @Nullable
  public abstract INode parse();

  public IToken getCurrentToken() {
    return token;
  }

  public void nextToken() {
    token = scanner.scan();
  }

  protected void consumeWhitespace() {
    while (true) {
      if (getCurrentToken() == null) break;
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

  @Nullable
  protected String expect(@NotNull TokenType tokenType) {
    if (accept(tokenType)) {
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
