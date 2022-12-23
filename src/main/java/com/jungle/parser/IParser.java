package com.jungle.parser;

import org.eclipse.jdt.annotation.Nullable;

import com.jungle.ast.INode;

public interface IParser {
  @Nullable INode parse();
}
