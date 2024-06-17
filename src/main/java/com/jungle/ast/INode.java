package com.jungle.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INode {
  @NotNull NodeType getType();

  // values
  @Nullable String getRawValue();
  @NotNull Boolean getBooleanValue();
  @NotNull Character getCharacterValue();
  @NotNull Integer getIntegerValue();
  @NotNull Float getFloatValue();
  @NotNull String getStringValue();

  // children
  @Nullable INode getLeft();
  @Nullable INode getRight();

  // helpers
  boolean isLeaf();
}
