package com.jungle.ast;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jungle.token.IToken;

public interface INode {
  @NonNull NodeType getType();
  @Nullable String getValue();
  @Nullable IToken getToken();
  @Nullable INode getLeft();
  @Nullable INode getRight();
}
