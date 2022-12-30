package com.jungle.scanner;

import org.jetbrains.annotations.NotNull;

import com.jungle.token.IToken;

public interface IScanner {
  void load(@NotNull String code, int startLineNumber);
  @NotNull IToken scan();
}
