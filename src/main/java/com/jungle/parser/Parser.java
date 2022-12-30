package com.jungle.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.scanner.IScanner;
import com.jungle.token.TokenType;

public class Parser extends AbstractParser {
  public static final String KEYWORD_PRINT = "print";

  public Parser(@NotNull IScanner scanner) {
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
     * sequence = { statement } | "\n" ;
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

  @NotNull
  protected INode parseNumber() {
    /* Leaf
     * integer = { "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" } ;
     * number = integer [ "." integer ] ;
     */
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

  public INode parseBinaryOperationAs(NodeType type) {
    // Helper
    nextToken(); // skip operator
    INode leftExpression = parseExpression();
    INode rightExpression = parseExpression();
    return new Node(type)
            .withLeft(leftExpression)
            .withRight(rightExpression);
  }

  public INode parseText() {
    String textValue = expect(TokenType.TEXT);
    boolean isSingleCharacter = textValue.length() == 1;
    NodeType type = isSingleCharacter
            ? NodeType.LITERAL_CHARACTER
            : NodeType.LITERAL_STRING;
    return new Node(type).withValue(textValue);
  }

  protected void consumeWhitespace() {
    while (true) {
      switch (getCurrentToken().getType()) {
        case SPACE: case TAB: case NEWLINE: {
          nextToken();
          continue;
        }
      }
      break;
    }
  }

  @NotNull
  protected INode parseExpression() {
    /*
     * expression = expression_parenthesis
     *            | number
     *            | ( "+" | "-" | "*" | "/" | "%" ) expression expression
     *            | ( " " | "\t" | "\n" ) expression
     *            ;
     */
    consumeWhitespace();
    switch (getCurrentToken().getType()) {
      case BRACKET_ROUND_OPEN: return parseExpressionParenthesis();
      case NUMBER: return parseNumber();
      case TEXT: return parseText();
      case PLUS: return parseBinaryOperationAs(NodeType.ADD);
      case MINUS: return parseBinaryOperationAs(NodeType.SUBTRACT);
      case ASTERISK: return parseBinaryOperationAs(NodeType.MULTIPLY);
      case SLASH_RIGHT: return parseBinaryOperationAs(NodeType.DIVIDE);
      case PERCENT: return parseBinaryOperationAs(NodeType.MODULO);
      default: break;
    }
    throw new Error("token does not correspond with any expression " + getCurrentToken());
  }

  @NotNull
  protected INode parseExpressionParenthesis() {
    /*
     * expression_parenthesis = "(" expression ")"
     */
    expect(TokenType.BRACKET_ROUND_OPEN);
    INode expression = parseExpression();
    expect(TokenType.BRACKET_ROUND_CLOSE);
    return expression;
  }

  @NotNull
  protected INode parsePrint() {
    /*
     * statement_print = "print" expression "\n"
     */
    expectKeyword(KEYWORD_PRINT);
    INode expressionParenthesis = parseExpression();
    expect(TokenType.NEWLINE);
    return new Node(NodeType.PRINT)
      .withLeft(expressionParenthesis);
  }

  @NotNull
  protected INode parseStatementKeyword() {
    /*
     * statement_keyword = statement_print | ...
     */
    String keywordValue = getCurrentToken().getValue();
    if (KEYWORD_PRINT.equals(keywordValue)) {
      return parsePrint();
    }
    throw new Error("unknown keyword " + keywordValue);
  }

  protected INode parseStatement() {
    /*
     * statement = statement_keyword | ...
     */
    if (accept(TokenType.KEYWORD)) {
      return parseStatementKeyword();
    }
    throw new Error("unrecognized statement with token " + getCurrentToken());
  }
}
