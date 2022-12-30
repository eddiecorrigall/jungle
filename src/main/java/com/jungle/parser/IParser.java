package com.jungle.parser;

import org.jetbrains.annotations.Nullable;

import com.jungle.ast.INode;

public interface IParser {
  @Nullable INode parse();
}
