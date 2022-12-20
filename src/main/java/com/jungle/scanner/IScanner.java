package com.jungle.scanner;

import org.eclipse.jdt.annotation.NonNull;

import com.jungle.token.IToken;

public interface IScanner {
  void load(@NonNull String code, int startLineNumber);
  @NonNull IToken scan();
}
