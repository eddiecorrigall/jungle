package com.jungle.ast;

import com.jungle.error.LoadError;
import com.jungle.error.SaveError;
import com.jungle.logger.FileLogger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Stack;

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
  public boolean isLeaf() {
    return getValue() != null;
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

  // region Serialization

  protected static char TERMINAL = ';';
  protected static char DELIMITER_FIELD = '\t';
  protected static char DELIMITER_LINE = '\n';

  // region Save AST
  @NotNull
  private static final FileLogger saveLog = new FileLogger("SaveAst");

  public static void save(@NotNull BufferedWriter writer, @Nullable INode node) {
    // Non-recursive traversal
    Stack<INode> nodeStack = new Stack<>();
    nodeStack.push(node);
    while (nodeStack.size() > 0) {
      INode nextNode = nodeStack.pop();
      saveLog.debug("---");
      if (nextNode == null) {
        saveLog.debug("node is terminal");
        try {
          writer.write(TERMINAL);
        } catch (IOException e) {
          throw new SaveError("failed to write terminal", e);
        }
      } else {
        saveLog.debug("type: " + nextNode.getType());
        saveLog.debug("value: " + nextNode.getValue());
        try {
          writer.write(nextNode.getType().name());
        } catch (IOException e) {
          String message = "failed to write node type";
          saveLog.error(message, e);
          throw new SaveError(message);
        }
        if (nextNode.isLeaf()) {
          saveLog.debug("node is leaf");
          try {
            writer.write(DELIMITER_FIELD);
            writer.write(nextNode.getValue());
          } catch (IOException e) {
            String message = "failed to write node value";
            saveLog.error(message, e);
            throw new SaveError(message);
          }
        } else {
          saveLog.debug("node is parent");
          nodeStack.push(nextNode.getRight());
          nodeStack.push(nextNode.getLeft());
        }
      }
      try {
        writer.write(DELIMITER_LINE);
      } catch (IOException e) {
        String message = "failed to write line delimiter";
        saveLog.error(message, e);
        throw new SaveError(message);
      }
    }
  }

  // endregion

  // region Load AST

  private static final FileLogger loadLog = new FileLogger("LoadAst");

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
      String message = "failed to read file";
      loadLog.error(message, e);
      throw new LoadError(message);
    }
    boolean hasStreamEnded = line == null;
    if (hasStreamEnded) {
      loadLog.warn("end of stream reached");
      return null;
    }
    boolean hasEmptyLine = line.length() == 0;
    if (hasEmptyLine) {
      loadLog.debug("line is empty");
      return null;
    }
    if (line.trim().startsWith(Character.toString(TERMINAL))) {
      loadLog.debug("line is terminal");
      return null;
    }
    int splitIndex = line.indexOf(DELIMITER_FIELD);
    String type;
    String value;
    if (splitIndex < 0) {
      type = line;
      value = null;
    } else if (splitIndex == 0) {
      String message = "line missing type";
      loadLog.error(message);
      throw new LoadError(message);
    } else {
      type = line.substring(0, splitIndex);
      value = line.substring(splitIndex + 1);
    }
    type = type.trim();
    loadLog.debug("---");
    loadLog.debug("type: " + type);
    loadLog.debug("value: " + value);
    Node node;
    try {
       node = new Node(NodeType.valueOf(type));
    } catch (IllegalArgumentException e) {
      String message = "line type invalid";
      loadLog.error(message, e);
      throw new LoadError(message);
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
  public static INode load(@NotNull String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    return load(reader);
  }

  // endregion

  // endregion
}
