package com.jungle.examples;

import java.io.IOException;
import org.objectweb.asm.ClassReader;

public class MakeClassPrinter {
  public static void main(String[] args) throws IOException {
    ClassPrinter cp = new ClassPrinter();
    ClassReader cr = new ClassReader("java.lang.Runnable");
    cr.accept(cp, 0);
  }
}
