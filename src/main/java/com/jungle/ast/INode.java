package com.jungle.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INode {
  @NotNull NodeType getType();
  @Nullable String getValue();
  @Nullable INode getLeft();
  @Nullable INode getRight();
}
