package com.jungle.ast;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jungle.token.IToken;

public class Node implements INode {
  @NonNull
  private NodeType type;

  @Nullable
  private String value;

  @Nullable
  private IToken token;

  @Nullable
  private INode left;

  @Nullable
  private INode right;

  public Node(@NonNull NodeType type) {
    super();
    this.type = type;
  }

  public Node(@NonNull NodeType type, @NonNull IToken token) {
    this(type);
    withValue(token.getValue());
    this.token = token;
  }

  @NonNull
  public NodeType getType() {
    return type;
  }

  @Nullable
  public String getValue() {
    return value;
  }

  @NonNull
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

  @NonNull
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

  @NonNull
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
