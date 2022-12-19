package com.jungle.token;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public interface IToken {
  @NonNull TokenType getType();
  @Nullable String getValue();
  int getLineNumber();
  int getCharacterNumber();
}
