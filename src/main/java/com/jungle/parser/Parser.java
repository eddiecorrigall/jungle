package com.jungle.parser;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.scanner.IScanner;
import com.jungle.token.TokenType;

public class Parser extends AbstractParser {
  public static final String KEYWORD_PRINT = "print";

  public Parser(@NonNull IScanner scanner) {
    super(scanner);
  }

  @Override
  @Nullable
  public INode parse() {
    nextToken();
    return parseSequence();
  }

  @Nullable
  protected INode parseSequence() {
    /*
     * sequence = { statement } | "\n";
     */
    INode sequence = null;
    while (!accept(TokenType.TERMINAL)) {
      if (accept(TokenType.NEWLINE)) {
        nextToken();
        sequence = new Node(NodeType.SEQUENCE)
          .withLeft(sequence);
      } else {
        sequence = new Node(NodeType.SEQUENCE)
          .withLeft(sequence)
          .withRight(parseStatement());
      }
    }
    return sequence;
  }

  @NonNull
  protected INode parseNumber() {
    // Leaf
    String integerPart = expect(TokenType.NUMBER);
    if (accept(TokenType.DOT)) {
      nextToken();
      String floatPart = expect(TokenType.NUMBER);
      return new Node(NodeType.LITERAL_FLOAT)
        .withValue(integerPart + '.' + floatPart);
    } else {
      return new Node(NodeType.LITERAL_INTEGER)
        .withValue(integerPart);
    }
  }

  @NonNull
  protected INode parseExpression() {
    switch (getCurrentToken().getType()) {
      case NUMBER: parseNumber();
      default: break;
    }
    throw new Error("not an expression");
  }

  @NonNull
  protected INode parseExpressionParenthesis() {
    expect(TokenType.BRACKET_ROUND_OPEN);
    INode expression = parseExpression();
    expect(TokenType.BRACKET_ROUND_CLOSE);
    return expression;
  }

  @NonNull
  protected INode parseStatementPrint() {
    expectKeyword(KEYWORD_PRINT);
    INode expressionParenthesis = parseExpressionParenthesis();
    expect(TokenType.NEWLINE);
    return new Node(NodeType.PRINT)
      .withLeft(expressionParenthesis);
  }

  @NonNull
  protected INode parseKeyword() {
    String keywordValue = getCurrentToken().getValue();
    if (KEYWORD_PRINT.equals(keywordValue)) {
      return parseStatementPrint();
    }
    throw new Error("unknown keyword " + keywordValue);
  }

  protected INode parseStatement() {
    if (accept(TokenType.KEYWORD)) {
      return parseKeyword();
    }
    throw new Error("unrecognized statement with token " + getCurrentToken());
  }
}
