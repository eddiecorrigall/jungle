package com.jungle.token;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Token implements IToken {
  @NotNull
  private final TokenType type;

  @Nullable
  private String value;

  int lineNumber, characterNumber;

  public Token(@NotNull TokenType type) {
    super();
    this.type = type;
  }

  @Override
  @NotNull
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

  @NotNull
  public Token withValue(@NotNull String value) {
    this.value = value;
    return this;
  }

  @NotNull
  public Token withPosition(int lineNumber, int characterNumber) {
    this.lineNumber = lineNumber;
    this.characterNumber = characterNumber;
    return this;
  }

  public static String getTokenAsLine(@NotNull IToken token) {
    return String.format(
            "%d\t%d\t%s\n",
            token.getLineNumber(),
            token.getCharacterNumber(),
            token.getValue() == null
                    ? token.getType().name()
                    : token.getType().name() + '\t' + StringEscapeUtils.escapeJava(token.getValue())
    );
  }

  public static void save(@NotNull BufferedWriter writer, @NotNull List<IToken> tokenList) throws IOException {
    // For each token list item,
    // convert the IToken to a line of text, and
    // write to the BufferedWriter.

    Iterator<IToken> tokenIterator = tokenList.iterator();
    while (tokenIterator.hasNext()) {
      IToken token = tokenIterator.next();
      if (token == null) {
        break;
      }
      writer.write(getTokenAsLine(token));
    }
  }

  @NotNull
  public static List<IToken> load(@NotNull BufferedReader reader) {
    // For each line read from the BufferedReader,
    // convert the line of text to a IToken and append it to a List<IToken>, and
    // then return the entire List.

    List<IToken> tokenList = new LinkedList<>();

    Iterator<String> lineIterator = reader.lines().iterator();
    while (lineIterator.hasNext()) {
      String line = lineIterator.next();

      if (line.trim().equals(StringUtils.EMPTY)) {
        continue;
      }

      // A line has at least 3 part and at most 4 parts.
      // Each part is delimited by a tab character.
      // To ensure that the token value is not accidentally partitioned, we set a split limit of 4.
      // This means that the split return value will be an array of at most for items, and
      // it will preserve the token value if that value contains tab characters.
      String[] lineParts = line.split("\t", 4);

      boolean isInvalidToken = lineParts.length < 3;
      if (isInvalidToken) {
        throw new Error("invalid token " + line);
      }

      int lineNumber;
      try {
        lineNumber = Integer.parseInt(lineParts[0]);
      } catch (NumberFormatException e) {
        throw new Error("invalid line number", e);
      }

      int characterNumber;
      try {
        characterNumber = Integer.parseInt(lineParts[1]);
      } catch (NumberFormatException e) {
        throw new Error("invalid character position", e);
      }

      TokenType tokenType;
      try {
        tokenType = TokenType.valueOf(lineParts[2]);
      } catch (IllegalArgumentException e) {
        throw new Error("invalid token type", e);
      }

      Token token = new Token(tokenType).withPosition(lineNumber, characterNumber);

      boolean hasValue = lineParts.length > 3;
      if (hasValue) {
        token.withValue(StringEscapeUtils.unescapeJava(lineParts[3]));
      }

      tokenList.add(token);
    }

    return tokenList;
  }
}
