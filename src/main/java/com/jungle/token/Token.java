package com.jungle.token;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class Token implements IToken {
  @NonNull
  private final TokenType type;

  @Nullable
  private String value;

  int lineNumber, characterNumber;

  public Token(@NonNull TokenType type) {
    super();
    this.type = type;
  }

  @Override
  @NonNull
  public TokenType getType() {
    return type;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public int getCharacterNumber() {
    return characterNumber;
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

  @NonNull
  public Token withValue(@NonNull String value) {
    this.value = value;
    return this;
  }

  @NonNull
  public Token withPosition(int lineNumber, int characterNumber) {
    this.lineNumber = lineNumber;
    this.characterNumber = characterNumber;
    return this;
  }
}
