package com.jungle.ast;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jungle.token.IToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Node implements INode {
  @NotNull
  private NodeType type;

  @Nullable
  private String value;

  @Nullable
  private IToken token;

  @Nullable
  private INode left;

  @Nullable
  private INode right;

  public Node(@NotNull NodeType type) {
    super();
    this.type = type;
  }

  public Node(@NotNull NodeType type, @NotNull IToken token) {
    this(type);
    withValue(token.getValue());
    this.token = token;
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
  public IToken getToken() {
    return token;
  }

  @Nullable
  public INode getLeft() {
    return left;
  }

  public void setLeft(@Nullable INode left) {
    this.left = left;
  }

  @NotNull
  public Node withLeft(@Nullable INode left) {
    setLeft(left);
    return this;
  }

  @Nullable
  public INode getRight() {
    return right;
  }

  public void setRight(@Nullable INode right) {
    this.right = right;
  }

  @NotNull
  public Node withRight(@Nullable INode right) {
    setRight(right);
    return this;
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (other == this) return true;
    Node otherNode = (Node) other;
    if (otherNode == null) return false;
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
