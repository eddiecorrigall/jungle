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
  private String rawValue;

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

  // region value

  @Override
  @Nullable
  public String getRawValue() {
    return rawValue;
  }

  @NotNull
  public INode withRawValue(@Nullable String rawValue) {
    this.rawValue = rawValue;
    return this;
  }

  @Override
  @NotNull
  public Boolean getBooleanValue() {
    if (getRawValue() == null) {
      throw new Error("failed to parse value as boolean - value is null");
    }
    return Boolean.valueOf(getRawValue());
  }

  @Override
  @NotNull
  public Character getCharacterValue() {
    String stringValue = getStringValue();
    if (stringValue.length() != 1) {
      throw new Error("failed to parse character as integer");
    }
    return stringValue.charAt(0);
  }

  @Override
  @NotNull
  public Integer getIntegerValue() {
    if (getRawValue() == null) {
      throw new Error("failed to parse value as integer - value is null");
    }
    try {
      return Integer.parseInt(getRawValue());
    } catch (NumberFormatException e) {
      throw new Error("failed to parse value as integer", e);
    }
  }

  @Override
  @NotNull
  public Float getFloatValue() {
    if (getRawValue() == null) {
      throw new Error("failed to parse value as float - value is null");
    }
    try {
      return Float.parseFloat(getRawValue());
    } catch (NumberFormatException e) {
      throw new Error("failed to parse value as float", e);
    }
  }

  @Override
  @NotNull
  public String getStringValue() {
    if (getRawValue() == null) {
      throw new Error("failed to parse value as string - value is null");
    }
    return getRawValue();
  }

  // endregion

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
    return getRawValue() != null;
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (other == this) return true;
    if (!(other instanceof Node)) return false;
    Node otherNode = (Node) other;
    return new EqualsBuilder()
            .append(getRawValue(), otherNode.getRawValue())
            .append(getType(), otherNode.getType()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
      .append(getType())
      .append(getRawValue())
      .toHashCode();
  }

  @Override
  public String toString() {
    return String.format("<Node type='%s' value='%s' />", getType(), getRawValue());
  }

  // region Serialization

  protected static char TERMINAL = ';';
  protected static char DELIMITER_FIELD = '\t';
  protected static char DELIMITER_LINE = '\n';

  // region Save AST
  @NotNull
  private static final FileLogger saveLogger = new FileLogger("SaveAst");

  public static void save(@NotNull BufferedWriter writer, @Nullable INode node) {
    // Non-recursive traversal
    Stack<INode> nodeStack = new Stack<>();
    nodeStack.push(node);
    while (nodeStack.size() > 0) {
      INode nextNode = nodeStack.pop();
      saveLogger.debug("---");
      if (nextNode == null) {
        saveLogger.debug("node is terminal");
        try {
          writer.write(TERMINAL);
        } catch (IOException e) {
          throw new SaveError("failed to write terminal", e);
        }
      } else {
        saveLogger.debug("type: " + nextNode.getType());
        saveLogger.debug("value: " + nextNode.getRawValue());
        try {
          writer.write(nextNode.getType().name());
        } catch (IOException e) {
          String message = "failed to write node type";
          saveLogger.error(message, e);
          throw new SaveError(message);
        }
        if (nextNode.isLeaf()) {
          saveLogger.debug("node is leaf");
          try {
            writer.write(DELIMITER_FIELD);
            writer.write(nextNode.getRawValue());
          } catch (IOException e) {
            String message = "failed to write node value";
            saveLogger.error(message, e);
            throw new SaveError(message);
          }
        } else {
          saveLogger.debug("node is parent");
          nodeStack.push(nextNode.getRight());
          nodeStack.push(nextNode.getLeft());
        }
      }
      try {
        writer.write(DELIMITER_LINE);
      } catch (IOException e) {
        String message = "failed to write line delimiter";
        saveLogger.error(message, e);
        throw new SaveError(message);
      }
    }
  }

  // endregion

  // region Load AST

  private static final FileLogger loadLogger = new FileLogger("LoadAst");

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
      loadLogger.error(message, e);
      throw new LoadError(message);
    }
    boolean hasStreamEnded = line == null;
    if (hasStreamEnded) {
      loadLogger.warn("end of stream reached");
      return null;
    }
    boolean hasEmptyLine = line.length() == 0;
    if (hasEmptyLine) {
      loadLogger.debug("line is empty");
      return null;
    }
    if (line.trim().startsWith(Character.toString(TERMINAL))) {
      loadLogger.debug("line is terminal");
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
      loadLogger.error(message);
      throw new LoadError(message);
    } else {
      type = line.substring(0, splitIndex);
      value = line.substring(splitIndex + 1);
    }
    type = type.trim();
    loadLogger.debug("---");
    loadLogger.debug("type: " + type);
    loadLogger.debug("value: " + value);
    Node node;
    try {
       node = new Node(NodeType.valueOf(type));
    } catch (IllegalArgumentException e) {
      String message = "line type invalid";
      loadLogger.error(message, e);
      throw new LoadError(message);
    }
    boolean isLeafNode = value != null;
    if (isLeafNode) {
      return node.withRawValue(value);
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
