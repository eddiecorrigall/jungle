package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.walker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.*;

public class Compiler {

    // region Helpers

    public static void writeClassFile(@NotNull String className, byte[] classData) throws IOException {
        Path classPath = Paths.get(className + ".class");
        try {
            Files.write(classPath, classData);
        } catch (IOException e) {
            System.err.println("failed to write to class file");
            throw e;
        }
    }

    // endregion

    public void compile(@NotNull IVisitor mainVisitor, @Nullable INode ast) {
        if (ast == null) {
            System.out.println("Warning: ast is null");
        }
        // Create bytes for entrypoint class
        ClassWriter initialClassWriter = visitMainClass(MainClassVisitor.MAIN_CLASS_NAME);
        // TODO: handle multi-class
        // mutate main class and method...
        ClassReader classReader = new ClassReader(initialClassWriter.toByteArray());
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor entrypoint = new MainClassVisitor(classWriter, mainVisitor, ast);
        classReader.accept(entrypoint, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        // ...
        try {
            writeClassFile(MainClassVisitor.MAIN_CLASS_NAME, classWriter.toByteArray());
        } catch (IOException e) {
            System.err.println("failed to compile");
            System.err.println(e);
        }
    }

    // region Emit Classes - ClassWriter factories

    @NotNull
    protected ClassWriter visitMainClass(@NotNull String className) {
        // class Entrypoint extends Object {}
        int flags = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
        ClassWriter cw = new ClassWriter(flags);
        cw.visit(
                V1_8,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                null);
        visitDefaultConstructor(cw);
        visitMainMethod(cw);
        cw.visitEnd();
        return cw;
    }

    // endregion

    // region Emit Methods - MethodVisitor factories
    @NotNull
    public static MethodVisitor visitDefaultConstructor(@NotNull ClassWriter cw) {
        // public ClassConstructor() { Object::super(); return; }
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
        // Code should be added here using a MethodVisitor...
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return mv;
    }

    @NotNull
    public static MethodVisitor visitMainMethod(@NotNull ClassWriter cw) {
        // public static void main(String[]) { return; }
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                MainMethodVisitor.MAIN_METHOD_NAME,
                "([Ljava/lang/String;)V",
                null,
                null);
        mv.visitCode();
        // Code should be added here using a MethodVisitor...
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return mv;
    }

    //endregion
}
