package com.jungle.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.token.IToken;

public interface INode {
  @NotNull NodeType getType();
  @Nullable String getValue();
  @Nullable IToken getToken();
  @Nullable INode getLeft();
  @Nullable INode getRight();
}
