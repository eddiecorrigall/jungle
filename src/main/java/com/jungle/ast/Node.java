package com.jungle.ast;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
    return new EqualsBuilder()
            .append(getValue(), otherNode.getValue())
            .append(getType(), otherNode.getType()).isEquals();
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

  @Nullable
  public static INode load(@NotNull BufferedReader reader) {
    // https://rosettacode.org/wiki/Compiler/code_generator#Java
    // Each line as an item in a binary array/tree
    // A line always has a node type with optional value
    // The node type and value are delimited by a tab character (\t)
    String line;
    try {
      line = reader.readLine();
    } catch (IOException e) {
      throw new Error("load ast - failed to read line");
    }
    if (line == null) {
      System.out.println("WARN: load ast - read line returned null");
      return null;
    }
    if (line.length() == 0) {
      System.out.println("load ast - end of input");
      return null;
    }
    if (line.trim().startsWith(";")) {
      System.out.println("---");
      System.out.println("terminated");
      return null;
    }
    int splitIndex = line.indexOf('\t');
    String type;
    String value;
    if (splitIndex < 0) {
      type = line;
      value = null;
    } else if (splitIndex == 0) {
      throw new Error("load ast - line missing type");
    } else {
      type = line.substring(0, splitIndex);
      value = line.substring(splitIndex + 1);
    }
    type = type.trim();
    System.out.println("---");
    System.out.println("type: " + type);
    System.out.println("value: " + value);
    Node node;
    try {
       node = new Node(NodeType.valueOf(type));
    } catch (IllegalArgumentException e) {
      throw new Error("load ast - line type invalid");
    }
    boolean isLeafNode = value != null;
    if (isLeafNode) {
      return node.withValue(value);
    }
    // TODO: breadth-first traversal or tail-recursion?
    return node
            .withLeft(load(reader))
            .withRight(load(reader));
  }

  @Nullable
  public static INode load(@NotNull String fileName) throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    return load(reader);
  }
}
