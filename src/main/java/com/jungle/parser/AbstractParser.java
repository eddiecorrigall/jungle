package com.jungle.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;
import com.jungle.scanner.IScanner;
import com.jungle.token.IToken;
import com.jungle.token.TokenType;

public abstract class AbstractParser implements IParser {
  private IToken token;
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

  protected boolean accept(@NotNull TokenType tokenType) {
    return getCurrentToken().getType() == tokenType;
  }

  protected String expect(@NotNull TokenType tokenType) {
    if (accept(tokenType)) {
      String value = getCurrentToken().getValue();
      nextToken();
      return value;
    }
    throw new Error("expected token type " + tokenType.name() + " but got token " + getCurrentToken());
  }

  protected void expectKeyword(@NotNull String keywordValue) {
    if (!keywordValue.equals(expect(TokenType.KEYWORD))) {
      throw new Error("expected keyword token with value " + keywordValue);
    }
  }
}
