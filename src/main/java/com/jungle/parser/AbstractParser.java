package com.jungle.parser;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jungle.ast.INode;
import com.jungle.scanner.IScanner;
import com.jungle.token.IToken;
import com.jungle.token.TokenType;

public abstract class AbstractParser implements IParser {
  private IToken token;
  private IScanner scanner;

  public AbstractParser(@NonNull IScanner scanner) {
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

  protected boolean accept(@NonNull TokenType tokenType) {
    return getCurrentToken().getType() == tokenType;
  }

  protected String expect(@NonNull TokenType tokenType) {
    if (accept(tokenType)) {
      String value = getCurrentToken().getValue();
      nextToken();
      return value;
    }
    throw new Error("expected token " + tokenType.name());
  }
}
