package com.jungle.error;

public class Helpers {
  public static String formatErrorMessage(String message, int line, int character) {
    return String.format("[L%d;C%d] %s", line, character, message);
  }
}
