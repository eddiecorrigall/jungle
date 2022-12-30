package com.jungle.ast;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Node implements INode {
  @NotNull
  private final NodeType type;

  @Nullable
  private String value;

  @Nullable
  private INode left;

  @Nullable
  private INode right;

  public Node(@NotNull NodeType type) {
    super();
    this.type = type;
  }

  @NotNull
  public NodeType getType() {
    return type;
  }

  @Nullable
  public String getValue() {
    return value;
  }

  @NotNull
  public INode withValue(@Nullable String value) {
    this.value = value;
    return this;
  }

  @Nullable
  public INode getLeft() {
    return left;
  }

  @NotNull
  public Node withLeft(@Nullable INode left) {
    this.left = left;
    return this;
  }

  @Nullable
  public INode getRight() {
    return right;
  }

  @NotNull
  public Node withRight(@Nullable INode right) {
    this.right = right;
    return this;
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (other == this) return true;
    if (!(other instanceof Node)) return false;
    Node otherNode = (Node) other;
    if (!getType().equals(otherNode.getType())) return false;
    if (getValue() == null) {
      return otherNode.getValue() == null;
    }
    return getValue().equals(otherNode.getValue());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
      .append(getType())
      .append(getValue())
      .toHashCode();
  }

  @Override
  public String toString() {
    return String.format("<Node type='%s' value='%s' />", getType(), getValue());
  }
}
