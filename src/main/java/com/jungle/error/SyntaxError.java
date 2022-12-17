package com.jungle.error;

public class SyntaxError extends CompileError {
  public SyntaxError(String message, int line, int character) {
    super(message, line, character);
  }
}
