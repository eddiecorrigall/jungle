package com.jungle.parser;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.ast.NodeType;
import com.jungle.scanner.IScanner;
import com.jungle.token.TokenType;

public class Parser extends AbstractParser {
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

  @Nullable
  protected INode parseStatement() {
    return null; // TODO
  }
}
