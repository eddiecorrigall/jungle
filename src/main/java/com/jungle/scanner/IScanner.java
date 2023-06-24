package com.jungle.scanner;

import org.jetbrains.annotations.NotNull;

import com.jungle.token.IToken;
import org.jetbrains.annotations.Nullable;

public interface IScanner {
  void load(@NotNull String code, int startLineNumber);
  @Nullable IToken scan();
}
