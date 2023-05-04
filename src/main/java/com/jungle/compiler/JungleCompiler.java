package com.jungle.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.ClassWriter.*;

public class JungleCompiler {
  class Context {
    @NotNull
    public final ClassWriter classWriter;

    @NotNull
    public final Node ast;

    public Context(@NotNull Node ast) {
      super();
      this.classWriter = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
      this.ast = ast;
    }
  }

  public void compile(@NotNull String className, @NotNull Node ast) throws IOException {
    Context context = new Context(ast);
    Path classPath = Paths.get(className + ".class");
    Files.write(classPath, compileToBytes(className, context));
  }

  public byte[] compileToBytes(String outputClassName, Context context) {
    context.classWriter.visit(
      V1_8,
      ACC_PUBLIC + ACC_SUPER,
      outputClassName,
      null,
      "java/lang/Object",
      null
    );
    visitStandardConstructor(context);
    visitMainMethod(context);
    context.classWriter.visitEnd();
    return context.classWriter.toByteArray();
  }

  void visitStandardConstructor(Context context) {
    MethodVisitor mv = context.classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  void visitMainMethod(Context context) {
    MethodVisitor mv = context.classWriter.visitMethod(
      ACC_PUBLIC + ACC_STATIC,
      "main",
      "([Ljava/lang/String;)V",
      null,
      null);
    mv.visitCode();

    compile(mv, context.ast);
    println(mv);

    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  void pushObject(MethodVisitor mv, Object value) {
    // "load constant"
    mv.visitLdcInsn(value);
  }

  void println(MethodVisitor mv) {
    // Print what ever value is on the stack
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

  /*
  void compile(MethodVisitor mv, @Nullable Node node) {
    if (node == null) return;
    compile(mv, node.getRight());
    compile(mv, node.getLeft());
    switch (node.getType()) {
      case LITERAL_INT: {
        int value = Integer.parseInt(node.getValue());
        pushObject(mv, value);
        System.out.println("push literal int " + value);
      } break;
      case ADD: {
        mv.visitInsn(IADD);
        System.out.println("add integer");
      } break;
      case MULTIPLY: {
        mv.visitInsn(IMUL);
        System.out.println("multiply integer");
      } break;
    }
  }
  */

  public static boolean isInteger(@Nullable INode node) {
    if (node == null) return false;
    switch (node.getType()) {
      case CAST_INTEGER: case LITERAL_INTEGER: return true;
      case ADD: case SUBTRACT: case MULTIPLY: case DIVIDE: {
        return isInteger(node.getLeft()) && isInteger(node.getRight());
      }
      default: return false;
    }
  }

  public static boolean isFloat(@Nullable INode node) {
    if (node == null) return false;
    switch (node.getType()) {
      case CAST_FLOAT: case LITERAL_FLOAT: return true;
      case ADD: case SUBTRACT: case MULTIPLY: case DIVIDE: {
        return isFloat(node.getLeft()) && isFloat(node.getRight());
      }
      default: return false;
    }
  }

  void compile(MethodVisitor mv, @NotNull INode ast) {
    // How do you know if it should compile an integer expression or a double expression?
    List<INode> visitedNodes = new LinkedList<INode>();
    Stack<INode> nodeStack = new Stack<INode>();
    // Traverse tree
    nodeStack.push(ast);
    while (!nodeStack.isEmpty()) {
      INode node = nodeStack.pop();
      visitedNodes.add(node);
      if (node.getLeft() != null) {
        nodeStack.push(node.getLeft());
      }
      if (node.getRight() != null) {
        nodeStack.push(node.getRight());
      }
    }
    Collections.reverse(visitedNodes);
    System.out.println(visitedNodes);
    // Compile in the order
    while (!visitedNodes.isEmpty()) {
      INode node = visitedNodes.remove(0);
      switch (node.getType()) {
        case LITERAL_INTEGER: {
          int value = Integer.parseInt(node.getValue());
          pushObject(mv, value);
          System.out.println("push literal int " + value);
        } break;
        case LITERAL_FLOAT: {
          float value = Float.parseFloat(node.getValue());
          pushObject(mv, value);
          System.out.println("push literal float " + value);
        } break;
        case CAST_INTEGER: {
          // TODO: evaluate expression first?
          // TODO: determine the type of number on the stack
          mv.visitInsn(F2I);
          System.out.println("float to integer");
        } break;
        case ADD: {
          if (isInteger(node)) {
            mv.visitInsn(IADD);
            System.out.println("add integers");
          } else if (isFloat(node)) {
            mv.visitInsn(FADD);
            System.out.println("add floats");
          } else {
            throw new Error("cannot add node [" + node + "]");
          }
        } break;
        case SUBTRACT: {
          if (isInteger(node)) {
            mv.visitInsn(ISUB);
            System.out.println("subtract integers");
          } else if (isFloat(node)) {
            mv.visitInsn(FSUB);
            System.out.println("subtract floats");
          } else {
            throw new Error("cannot subtract node [" + node + "]");
          }
        } break;
        case MULTIPLY: {
          if (isInteger(node)) {
            mv.visitInsn(IMUL);
            System.out.println("multiply integers");
          } else if (isFloat(node)) {
            mv.visitInsn(FMUL);
            System.out.println("multiply floats");
          } else {
            throw new Error("cannot multiply node [" + node + "]");
          }
        } break;
        case DIVIDE: {
          if (isInteger(node)) {
            mv.visitInsn(IDIV);
            System.out.println("divide integers");
          } else if (isFloat(node)) {
            mv.visitInsn(FDIV);
            System.out.println("divide floats");
          } else {
            throw new Error("cannot divide node [" + node + "]");
          }
        } break;
        default: throw new Error("unknown node type [" + node.getType() + "]");
      }
    }
  }
}
