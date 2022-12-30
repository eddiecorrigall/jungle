package com.jungle.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IToken {
  @NotNull TokenType getType();
  @Nullable String getValue();
  int getLineNumber();
  int getCharacterNumber();
}
