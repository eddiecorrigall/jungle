package com.jungle.scanner;

import com.jungle.token.IToken;
import org.jetbrains.annotations.Nullable;

public interface IScanner {
  @Nullable Iterable<IToken> scan();
}
