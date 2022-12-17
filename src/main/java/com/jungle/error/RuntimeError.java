package com.jungle.error;

public class RuntimeError extends Error {
  public RuntimeError(String message, int line, int character) {
    super(Helpers.formatErrorMessage(message, line, character));
  }
}
