package com.jungle.error;

public class CompileError extends Error {
  public CompileError(String message, int line, int character) {
    super(Helpers.formatErrorMessage(message, line, character));
  }
}
