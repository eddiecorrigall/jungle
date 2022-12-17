package com.jungle.token;

import java.util.Objects;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class Token {
  private int row;
  private int column;

  @NonNull
  private final TokenType type;

  @Nullable
  private final String value;

  public Token(@NonNull TokenType type, @Nullable String value) {
    super();
    Objects.requireNonNull(type);
    this.type = type;
    this.value = value;
    this.row = -1;
    this.column = -1;
  }

  public Token(@NonNull TokenType type) {
    this(type, null);
  }

  public Token withRow(int row) {
    this.row = row;
    return this;
  }

  public Token withColumn(int column) {
    this.column = column;
    return this;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public TokenType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (other == this) return true;
    Token otherToken = (Token) other;
    if (otherToken == null) return false;
    if (!getType().equals(otherToken.getType())) return false;
    if (getValue() == null) {
      return otherToken.getValue() == null;
    }
    return getValue().equals(otherToken.getValue());
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
    return String.format("<Token type=%s value=%s />", getType(), getValue());
  }
}
