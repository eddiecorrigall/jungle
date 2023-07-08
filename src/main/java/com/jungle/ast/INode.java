package com.jungle.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INode {
  @NotNull NodeType getType();
  @Nullable String getRawValue();
  @NotNull Boolean getBooleanValue();
  @NotNull Character getCharacterValue();
  @NotNull Integer getIntegerValue();
  @NotNull Float getFloatValue();
  @NotNull String getStringValue();
  @Nullable INode getLeft();
  @Nullable INode getRight();

  boolean isLeaf();
}
