package com.jungle.parser;

import com.jungle.token.IToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.token.TokenType;

import java.util.function.Supplier;

import static com.jungle.scanner.Scanner.*;

public class Parser extends AbstractParser {

  public Parser(@NotNull Iterable<IToken> tokenIterable) {
    super(tokenIterable);
  }

  @Override
  @Nullable
  public INode parse() {
    nextToken();
    return parseSequence();
  }

  // region helpers

  @Nullable
  protected INode parseParenthesis(@NotNull Supplier<INode> parseCallback) {
    /*
     * parenthesis := "(" <parse-callback-node> ")" ;
     */
    consumeWhitespace();
    expect(TokenType.BRACKET_ROUND_OPEN);
    INode expressionNode = parseCallback.get();
    consumeWhitespace();
    expect(TokenType.BRACKET_ROUND_CLOSE);
    return expressionNode;
  }

  // endregion

  // region identifier

  protected INode parseIdentifier() {
    consumeWhitespace();
    String identifierName = expect(TokenType.SYMBOL);
    return new Node(NodeType.IDENTIFIER).withRawValue(identifierName);
  }

  // endregion

  // region numeric

  @Nullable
  protected INode parseNumber() {
    /* Leaf
     * integer := { "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" } ;
     * decimal := integer "." integer ;
     * number := integer | decimal ;
     */
    String integerPart = expect(TokenType.NUMBER);
    if (accepts(TokenType.DOT)) {
      // Decimal
      nextToken();
      String fractionalPart = expect(TokenType.NUMBER);
      // TODO: handle double
      return new Node(NodeType.LITERAL_FLOAT)
              .withRawValue(integerPart + '.' + fractionalPart);
    } else {
      // Integer
      return new Node(NodeType.LITERAL_INTEGER)
              .withRawValue(integerPart);
    }
  }

  @Nullable
  public INode parseNumberExpression() {
    /*
     * number_expression := number
     *                    | "(" number_expression ")"
     *                    | ( "+" | "-" | "*" | "/" | "%" ) expression expression
     *                    ;
     */
    consumeWhitespace();
    if (accepts(TokenType.NUMBER)) {
      return parseNumber();
    }
    if (accepts(TokenType.BRACKET_ROUND_OPEN)) {
      return parseParenthesis(this::parseNumberExpression);
    }
    if (accepts(TokenType.PLUS)) {
      expect(TokenType.PLUS);
      return new Node(NodeType.OPERATOR_ADD)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (accepts(TokenType.MINUS)) {
      expect(TokenType.MINUS);
      return new Node(NodeType.OPERATOR_SUBTRACT)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (accepts(TokenType.ASTERISK)) {
      expect(TokenType.ASTERISK);
      return new Node(NodeType.OPERATOR_MULTIPLY)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (accepts(TokenType.SLASH_RIGHT)) {
      expect(TokenType.SLASH_RIGHT);
      return new Node(NodeType.OPERATOR_DIVIDE)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (accepts(TokenType.PERCENT)) {
      expect(TokenType.PERCENT);
      return new Node(NodeType.OPERATOR_MODULO)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    throw newError("not a number expression");
  }

  // endregion

  // region text

  @Nullable
  public INode parseTextLiteral() {
    /*
     * character := <single-character> ;
     * string := { character } ;
     * text_literal = character | string ;
     */
    String textValue = expect(TokenType.TEXT);
    if (textValue == null) {
      throw newError("text token missing value");
    }
    // Note: a character literal cannot be empty, but a string literal can be empty
    boolean isSingleCharacter = textValue.length() == 1;
    NodeType type = isSingleCharacter
            ? NodeType.LITERAL_CHARACTER
            : NodeType.LITERAL_STRING;
    return new Node(type).withRawValue(textValue);
  }

  @Nullable
  protected INode parseTextExpression() {
    /*
     * text_expression := text_literal ;
     */
    if (accepts(TokenType.TEXT)) {
      return parseTextLiteral();
    }
    throw newError("not a text expression");
  }

  // endregion

  // region boolean

  @NotNull
  protected INode parseBooleanLiteral() {
    if (acceptKeyword(KEYWORD_TRUE)) {
      expectKeyword(KEYWORD_TRUE);
      return new Node(NodeType.LITERAL_INTEGER).withRawValue("1");
    }
    if (acceptKeyword(KEYWORD_FALSE)) {
      expectKeyword(KEYWORD_FALSE);
      return new Node(NodeType.LITERAL_INTEGER).withRawValue("0");
    }
    throw newError("not a boolean literal");
  }

  @Nullable
  protected INode parseBooleanExpression() {
    /*
     * boolean_expression := boolean_literal
     *                     | "(" boolean_expression ")"
     *                     | ( "and" | "or" | "greaterThan" | "lessThan" | "equals" ) expression expression
     *                     | "not" expression
     *                     ;
     */
    consumeWhitespace();
    // region literal
    if (acceptKeywords(KEYWORD_TRUE, KEYWORD_FALSE)) {
      return parseBooleanLiteral();
    }
    // endregion
    // region parenthesis
    if (accepts(TokenType.BRACKET_ROUND_OPEN)) {
      return parseParenthesis(this::parseBooleanExpression);
    }
    // endregion
    // region predicate - boolean operator
    if (acceptKeyword(KEYWORD_AND)) {
      expectKeyword(KEYWORD_AND);
      return new Node(NodeType.OPERATOR_AND)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (acceptKeyword(KEYWORD_OR)) {
      expectKeyword(KEYWORD_OR);
      return new Node(NodeType.OPERATOR_OR)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (acceptKeyword(KEYWORD_EQUALS)) {
      expectKeyword(KEYWORD_EQUALS);
      return new Node(NodeType.OPERATOR_EQUAL)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (acceptKeyword(KEYWORD_GREATER_THAN)) {
      expectKeyword(KEYWORD_GREATER_THAN);
      return new Node(NodeType.OPERATOR_GREATER_THAN)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    if (acceptKeyword(KEYWORD_LESS_THAN)) {
      expectKeyword(KEYWORD_LESS_THAN);
      return new Node(NodeType.OPERATOR_LESS_THAN)
              .withLeft(parseExpression())
              .withRight(parseExpression());
    }
    // endregion
    // region predicate - unary operator
    if (acceptKeyword(KEYWORD_NOT)) {
      expectKeyword(KEYWORD_NOT);
      return new Node(NodeType.OPERATOR_NOT)
              .withLeft(parseExpression());
    }
    // endregion
    throw newError("not a boolean expression");
  }

  // endregion

  // region expression

  @Nullable
  protected INode parseExpression() {
    /*
     * expression := identifier
     *             | "(" expression ")"
     *             | boolean_expression
     *             | numeric_expression
     *             | text_expression
     *             ;
     */
    consumeWhitespace();
    if (accepts(TokenType.SYMBOL)) {
      return parseIdentifier();
    }
    if (accepts(TokenType.BRACKET_ROUND_OPEN)) {
      return parseParenthesis(this::parseExpression);
    }
    boolean isBooleanExpression = acceptKeywords(
            KEYWORD_AND,
            KEYWORD_OR,
            KEYWORD_NOT,
            KEYWORD_EQUALS,
            KEYWORD_GREATER_THAN,
            KEYWORD_LESS_THAN,
            KEYWORD_TRUE,
            KEYWORD_FALSE
    );
    if (isBooleanExpression) {
      return parseBooleanExpression();
    }
    boolean isTextExpression = accepts(TokenType.TEXT);
    if (isTextExpression) {
      return parseTextExpression();
    }
    boolean isNumberExpression = accepts(
            TokenType.NUMBER,
            TokenType.PLUS,
            TokenType.MINUS,
            TokenType.ASTERISK,
            TokenType.SLASH_RIGHT,
            TokenType.PERCENT
    );
    if (isNumberExpression) {
      return parseNumberExpression();
    }
    throw newError("not an expression");
  }

  // endregion

  // region statement

  @Nullable
  protected INode parseStatementAssert() {
    /*
     * statement_assert := "assert" boolean_expression ;
     */
    expectKeyword(KEYWORD_ASSERT);
    return new Node(NodeType.ASSERT).withLeft(parseBooleanExpression());
  }

  @Nullable
  protected INode parseStatementLoop() {
    /*
     * statement_loop := "loop" boolean_expression statement_block ;
     */
    expectKeyword(KEYWORD_LOOP);
    INode expressionNode = parseBooleanExpression();
    INode blockNode = parseStatementBlock();
    return new Node(NodeType.LOOP)
            .withLeft(expressionNode)
            .withRight(blockNode);
  }

  @Nullable
  protected INode parseStatementPrint() {
    /*
     * statement_print := "print" expression ;
     */
    expectKeyword(KEYWORD_PRINT);
    return new Node(NodeType.PRINT).withLeft(parseExpression());
  }

  @Nullable
  protected INode parseStatementIf() {
    /*
     * statement_if := "if" boolean_expression statement_block [ "else" statement_block ] ;
     */
    expectKeyword(KEYWORD_IF);
    Node ifNode = new Node(NodeType.IF).withLeft(parseBooleanExpression());
    INode ifBlockNode = parseStatementBlock();
    consumeWhitespace();
    if (acceptKeyword(KEYWORD_ELSE)) {
      expectKeyword(KEYWORD_ELSE);
      INode elseBlockNode = parseStatementBlock();
      return ifNode.withRight(
              new Node(NodeType.IF_ELSE)
                      .withLeft(ifBlockNode)
                      .withRight(elseBlockNode)
      );
    } else {
      return ifNode.withRight(ifBlockNode);
    }
  }

  @Nullable
  protected INode parseStatementBlock() {
    /*
     * statement_block := "{" sequence "}" ;
     */
    consumeWhitespace();
    expect(TokenType.BRACKET_CURLY_OPEN);
    INode sequenceNode = null;
    while (true) {
      consumeWhitespace();
      if (getCurrentToken() == null) break;
      if (accepts(TokenType.TERMINAL)) break;
      if (accepts(TokenType.BRACKET_CURLY_CLOSE)) break;
      if (sequenceNode == null) {
        sequenceNode = new Node(NodeType.SEQUENCE)
                .withLeft(parseStatement());
      } else {
        sequenceNode = new Node(NodeType.SEQUENCE)
                .withLeft(sequenceNode)
                .withRight(parseStatement());
      }
    }
    expect(TokenType.BRACKET_CURLY_CLOSE);
    return new Node(NodeType.BLOCK).withLeft(sequenceNode);
  }

  @Nullable
  protected INode parseStatementKeyword() {
    /*
     * statement_keyword := statement_assert
     *                   | statement_loop
     *                   | statement_print
     *                   | statement_if
     *                   ;
     */
    consumeWhitespace();
    if (acceptKeyword(KEYWORD_ASSERT)) {
      return parseStatementAssert();
    }
    if (acceptKeyword(KEYWORD_LOOP)) {
      return parseStatementLoop();
    }
    if (acceptKeyword(KEYWORD_PRINT)) {
      return parseStatementPrint();
    }
    if (acceptKeyword(KEYWORD_IF)) {
      return parseStatementIf();
    }
    throw newError("not a keyword statement");
  }

  @Nullable
  protected INode parseStatementSymbol() {
    /*
     * statement_symbol := symbol "=" expression ;
     */
    consumeWhitespace();
    String symbolValue = expect(TokenType.SYMBOL);
    if (symbolValue == null) {
      throw new Error("symbol token missing value");
    }
    consumeWhitespace();
    if (accepts(TokenType.EQUALS)) {
      expect(TokenType.EQUALS);
      return new Node(NodeType.ASSIGN)
              .withLeft(new Node(NodeType.IDENTIFIER).withRawValue(symbolValue))
              .withRight(parseExpression());
    }
    throw newError("not a symbol statement");
  }

  @Nullable
  protected INode parseStatement() {
    /*
     * statement := statement_block
     *           | statement_keyword
     *           | statement_symbol
     *           ;
     */
    consumeWhitespace();
    if (accepts(TokenType.BRACKET_CURLY_OPEN)) {
      return parseStatementBlock();
    }
    if (accepts(TokenType.KEYWORD)) {
      return parseStatementKeyword();
    }
    if (accepts(TokenType.SYMBOL)) {
      return parseStatementSymbol();
    }
    throw newError("not a statement");
  }

  // endregion

  // region sequence

  @Nullable
  protected INode parseSequence() {
    /*
     * sequence := { statement } | "\n" ;
     */
    INode sequenceNode = null;
    while (true) {
      if (getCurrentToken() == null) break;
      if (accepts(TokenType.TERMINAL)) break;
      if (accepts(TokenType.NEWLINE)) {
        nextToken();
        continue;
      }
      if (sequenceNode == null) {
        sequenceNode = new Node(NodeType.SEQUENCE)
                .withLeft(parseStatement());
      } else {
        sequenceNode = new Node(NodeType.SEQUENCE)
                .withLeft(sequenceNode)
                .withRight(parseStatement());
      }
    }
    return sequenceNode;
  }

  // endregion
}
