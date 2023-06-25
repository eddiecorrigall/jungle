package com.jungle.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.scanner.IScanner;
import com.jungle.token.TokenType;

import static com.jungle.scanner.Scanner.KEYWORD_ASSERT;
import static com.jungle.scanner.Scanner.KEYWORD_IF;
import static com.jungle.scanner.Scanner.KEYWORD_LOOP;
import static com.jungle.scanner.Scanner.KEYWORD_PRINT;
import static com.jungle.scanner.Scanner.KEYWORD_AND;
import static com.jungle.scanner.Scanner.KEYWORD_OR;
import static com.jungle.scanner.Scanner.KEYWORD_NOT;

public class Parser extends AbstractParser {
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
    while (true) {
      if (getCurrentToken() == null) break;
      if (accept(TokenType.TERMINAL)) break;
      if (accept(TokenType.NEWLINE)) {
        nextToken();
        sequence = new Node(NodeType.SEQUENCE)
          .withLeft(sequence);
        continue;
      }
      sequence = new Node(NodeType.SEQUENCE)
          .withLeft(sequence)
          .withRight(parseStatement());
    }
    return sequence;
  }

  @Nullable
  protected INode parseNumber() {
    /* Leaf
     * integer = { "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" } ;
     * number = integer [ "." integer ] ;
     */
    String integerPart = expect(TokenType.NUMBER);
    if (accept(TokenType.DOT)) {
      // Decimal
      nextToken();
      String fractionalPart = expect(TokenType.NUMBER);
      // TODO: handle double
      return new Node(NodeType.LITERAL_FLOAT)
              .withValue(integerPart + '.' + fractionalPart);
    } else {
      // Integer
      return new Node(NodeType.LITERAL_INTEGER)
              .withValue(integerPart);
    }
  }

  @Nullable
  public INode parseNumericBinaryOperation(NodeType type) {
    // TODO: be explicit about mapping the operator, this should not be a helper
    nextToken(); // skip operator
    INode leftExpression = parseExpression();
    INode rightExpression = parseExpression();
    return new Node(type)
            .withLeft(leftExpression)
            .withRight(rightExpression);
  }

  @Nullable
  public INode parseText() {
    String textValue = expect(TokenType.TEXT);
    if (textValue == null) {
      throw new Error("text token missing value");
    }
    boolean isSingleCharacter = textValue.length() == 1;
    NodeType type = isSingleCharacter
            ? NodeType.LITERAL_CHARACTER
            : NodeType.LITERAL_STRING;
    return new Node(type).withValue(textValue);
  }

  @Nullable
  protected INode parseBooleanExpression() {
    if (getCurrentToken() == null) return null;
    String keywordValue = expect(TokenType.KEYWORD);
    if (keywordValue == null) {
      throw new Error("keyword token missing value");
    }
    switch (keywordValue) {
      case KEYWORD_AND: {
        return new Node(NodeType.OPERATOR_AND)
                .withLeft(parseExpression())
                .withRight(parseExpression());
      }
      case KEYWORD_OR: {
        return new Node(NodeType.OPERATOR_OR)
                .withLeft(parseExpression())
                .withRight(parseExpression());
      }
      case KEYWORD_NOT: {
        return new Node(NodeType.OPERATOR_NOT)
                .withLeft(parseExpression());
      }
      default: throw new Error("not a binary expression " + getCurrentToken());
    }
  }

  @Nullable
  protected INode parseExpression() {
    /*
     * expression = whitespace expression
     *            | expression_parenthesis
     *            | number
     *            | text
     *            | ( "+" | "-" | "*" | "/" | "%" ) expression expression
     *            | ( "add" | "or" ) expression expression
     *            | "not" expression
     *            ;
     */
    consumeWhitespace();
    if (getCurrentToken() == null) {
      return null;
    }
    switch (getCurrentToken().getType()) {
      case BRACKET_ROUND_OPEN: return parseExpressionParenthesis();
      case NUMBER: return parseNumber();
      case TEXT: return parseText();
      case PLUS: return parseNumericBinaryOperation(NodeType.OPERATOR_ADD);
      case MINUS: return parseNumericBinaryOperation(NodeType.OPERATOR_SUBTRACT);
      case ASTERISK: return parseNumericBinaryOperation(NodeType.OPERATOR_MULTIPLY);
      case SLASH_RIGHT: return parseNumericBinaryOperation(NodeType.OPERATOR_DIVIDE);
      case PERCENT: return parseNumericBinaryOperation(NodeType.OPERATOR_MODULO);
      case KEYWORD: {
        String keywordValue = getCurrentToken().getValue();
        if (keywordValue == null) {
          throw new Error("keyword token missing value");
        }
        switch (keywordValue) {
          case KEYWORD_AND:
          case KEYWORD_OR:
          case KEYWORD_NOT: return parseBooleanExpression();
          default: break;
        }
      }
    }
    throw new Error("token does not correspond with any expression " + getCurrentToken());
  }

  @Nullable
  protected INode parseExpressionParenthesis() {
    /*
     * expression_parenthesis = "(" expression ")"
     */
    expect(TokenType.BRACKET_ROUND_OPEN);
    INode expression = parseExpression();
    expect(TokenType.BRACKET_ROUND_CLOSE);
    return expression;
  }

  @Nullable
  protected INode parseAssert() {
    /*
     * statement_assert = "assert" expression
     */
    expectKeyword(KEYWORD_ASSERT);
    return new Node(NodeType.ASSERT).withLeft(parseExpression());
  }

  @Nullable
  protected INode parsePrint() {
    /*
     * statement_print = "print" expression
     */
    expectKeyword(KEYWORD_PRINT);
    return new Node(NodeType.PRINT).withLeft(parseExpression());
  }

  @Nullable
  protected INode parseStatementKeyword() {
    /*
     * statement_keyword = statement_print
     *                   | ( "and" | "or" | "not" ) expression
     */
    if (getCurrentToken() == null) return null;
    String keywordValue = getCurrentToken().getValue();
    if (keywordValue == null) {
      throw new Error("keyword token missing value");
    }
    switch (keywordValue) {
      case KEYWORD_ASSERT: return parseAssert();
      case KEYWORD_PRINT: return parsePrint();
      case KEYWORD_AND:
      case KEYWORD_OR:
      case KEYWORD_NOT: return parseBooleanExpression();
    }
    throw new Error("unknown keyword " + keywordValue);
  }

  @Nullable
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
