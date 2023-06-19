package com.jungle.examples;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.*;

/*
Compiled with javac

Compiled from "PrintNumber.java"
public class com.jungle.PrintNumber {
  public com.jungle.PrintNumber();
    Code:
       0: aload_0
       1: invokespecial #8                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #16                 // Field java/lang/System.out:Ljava/io/PrintStream;
       3: sipush        666
       6: invokevirtual #22                 // Method java/io/PrintStream.println:(I)V
       9: return
}

Compiled with this code

public class PrintNumber {
  public PrintNumber();
    Code:
       0: aload_0
       1: invokespecial #8                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #16                 // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #17                 // int 666
       5: invokevirtual #23                 // Method java/io/PrintStream.println:(I)V
       8: return
}
 */

public class MakePrintNumber {
  // Is it possible to define a number object that determines appropriate primative type?
  // Is it possible for developers to define their own custom variable types? I guess thats a class / object.

  private ClassWriter cw = new ClassWriter(0);

  public static void main(String[] args) throws IOException {
    MakePrintNumber program = new MakePrintNumber();
    program.compileAs("PrintNumber");
  }

  private void compileAs(String className) throws IOException {
    Path classPath = Paths.get(className + ".class");
    Files.write(classPath, serializeToBytes(className));
  }

  public byte[] serializeToBytes(String outputClassName) {
    cw.visit(
      V1_8,
      ACC_PUBLIC + ACC_SUPER,
      outputClassName,
      null,
      "java/lang/Object",
      null);
    defineStandardConstructor();
    defineMainMethod();
    cw.visitEnd();
    return cw.toByteArray();
  }

  void defineStandardConstructor() {
    MethodVisitor mv = cw.visitMethod(
      ACC_PUBLIC,
      "<init>",
      "()V",
      null,
      null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
      INVOKESPECIAL,
      "java/lang/Object",
      "<init>",
      "()V",
      false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  void defineMainMethod() {
    // public static void main(String[])
    MethodVisitor mv = cw.visitMethod(
      ACC_PUBLIC + ACC_STATIC,
      "main",
      "([Ljava/lang/String;)V",
      null,
      null);
    mv.visitCode();
    pushObject(mv, 1);
    pushObject(mv, 2);
    addIntegers(mv);
    println(mv);
    // return
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
  }

  void addIntegers(MethodVisitor mv) {
    // "add integers"
    mv.visitInsn(IADD);
  }

  void pushObject(MethodVisitor mv, Object value) {
    // "load constant"
    mv.visitLocalVariable(null, null, null, null, null, 0);
    mv.visitLdcInsn(value);
  }

  void println(MethodVisitor mv) {
    // System.out
    mv.visitFieldInsn(
      GETSTATIC,
      "java/lang/System",
      "out",
      "Ljava/io/PrintStream;");
    // "swap" last 2 words on stack to make the desired value first and the "out" Object second
    mv.visitInsn(SWAP); 
    // out.println()
    mv.visitMethodInsn(
      INVOKEVIRTUAL,
      "java/io/PrintStream",
      "println",
      "(I)V",
      false);
  }
}
