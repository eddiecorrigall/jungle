package com.jungle.compiler;

import com.jungle.ast.INode;
import com.jungle.error.CompilerError;
import com.jungle.logger.FileLogger;
import com.jungle.compiler.visitor.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.objectweb.asm.Opcodes.*;

public class Compiler implements ICompilerOptions {
    @NotNull
    private static final FileLogger logger = new FileLogger(Compiler.class.getSimpleName());

    @NotNull
    private final String classPath;

    @Override
    @NotNull
    public String getClassPath() {
        return classPath;
    }

    @NotNull
    private final String targetPath;

    @Override
    @NotNull
    public String getTargetPath() {
        return targetPath;
    }

    public Compiler(
        @NotNull String classPath,
        @NotNull String targetPath
    ) {
        super();
        this.classPath = classPath;
        this.targetPath = targetPath;
    }

    public Compiler(@NotNull ICompilerOptions options) {
        super();
        this.classPath = options.getClassPath();
        this.targetPath = options.getTargetPath();
    }

    // region Helpers

    @NotNull
    public static String asInternalClassName(@NotNull String className) {
        return className.replace('.', '/');
    }

    protected void writeClassFile(@NotNull String className, byte[] classData) {
        // TODO: new class files should be placed in a target directory
        // ...
        String classFileString = getTargetPath() + '/' + className.replace('.', '/') + ".class";
        File classFile = new File(classFileString);
        logger.debug(String.format("write class file - %s", classFile.toString()));
        // Create necessary parent directories for class file
        File classParentFile = classFile.getParentFile();
        if (classParentFile != null) {
            classParentFile.mkdirs();
        }
        try {
            boolean hasCreatedNewFile = classFile.createNewFile();
            if (!hasCreatedNewFile) {
                logger.debug(String.format("class already exists - %s", className));
            }
        } catch (IOException e) {
            String message = "failed to create class file";
            logger.error(message, e);
            throw new CompilerError(message);
        }
        try {
            Files.write(classFile.toPath(), classData);
        } catch (IOException e) {
            String message = "failed to write to class file";
            logger.error(message, e);
            throw new CompilerError(message);
        }
    }

    // endregion

    public void compileMain(@NotNull String mainClassName, @NotNull IVisitor languageVisitor, @Nullable INode ast) {
        ClassWriter initialClassWriter = visitMainClass(mainClassName); // template
        ClassWriter classWriter = visitEntrypoint("main", initialClassWriter, languageVisitor, ast);
        writeClassFile(mainClassName, classWriter.toByteArray());
    }

    public void compileRunnable(@NotNull String runnableClassName, @NotNull IVisitor languageVisitor, @Nullable INode ast) {
        ClassWriter initialClassWriter = visitRunnableClass(runnableClassName); // template
        ClassWriter classWriter = visitEntrypoint("run", initialClassWriter, languageVisitor, ast);
        writeClassFile(runnableClassName, classWriter.toByteArray());
    }

    @NotNull
    public ClassWriter visitEntrypoint(
        @NotNull String targetMethodName,
        @NotNull ClassWriter initialClassWriter,
        @NotNull IVisitor languageVisitor,
        @Nullable INode ast
    ) {
        if (ast == null) {
            String message = "AST is null - source files must contain code";
            logger.error(message);
            throw new CompilerError(message);
        }
        logger.debug("generating entrypoint class from initial class");
        ClassReader classReader = new ClassReader(initialClassWriter.toByteArray());
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor entrypoint = new JungleClassVisitor(targetMethodName, classWriter, languageVisitor, ast);
        logger.debug("traversing AST");
        classReader.accept(entrypoint, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        logger.debug("writing class file");
        return classWriter;
    }

    // region Emit Classes - ClassWriter factories

    @NotNull
    protected ClassWriter visitMainClass(@NotNull String className) {
        // class Entrypoint extends Object {}
        logger.debug("visit main class");
        int flags = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
        ClassWriter cw = new ClassWriter(flags);
        cw.visit(
                V1_8,
                ACC_PUBLIC + ACC_SUPER,
                asInternalClassName(className),
                null,
                "java/lang/Object",
                null
        );
        visitDefaultConstructor(cw);
        visitMainMethod(cw);
        cw.visitEnd();
        return cw;
    }

    @NotNull
    public static ClassWriter visitRunnableClass(@NotNull String className) {
        logger.debug("visit runnable class");
        int flags = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
        ClassWriter cw = new ClassWriter(flags);
        cw.visit(
                V1_8,
                ACC_PUBLIC + ACC_SUPER,
                asInternalClassName(className),
                null,
                "java/lang/Object",
                new String[]{"java/lang/Runnable"}
        );
        visitDefaultConstructor(cw);
        visitRunnableRunMethod(cw);
        cw.visitEnd();
        return cw;
    }

    // endregion

    // region Emit Methods - MethodVisitor factories

    public static void visitDefaultConstructor(@NotNull ClassWriter cw) {
        // public ClassConstructor() { Object::super(); return; }
        logger.debug("visit default constructor");
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn( // super()
                INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        // Use a MethodVisitor to emit code before the return statement
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    public static void visitMainMethod(@NotNull ClassWriter cw) {
        // public static void main(String[]) { return; }
        logger.debug("visit main method");
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null);
        mv.visitCode();
        // Use a MethodVisitor to emit code before the return statement
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    public static void visitRunnableRunMethod(@NotNull ClassWriter cw) {
        // public void run() { return; }
        logger.debug("visit runnable run method");
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "run",
                "()V",
                null,
                null);
        mv.visitCode();
        // Use a MethodVisitor to emit code before the return statement
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    //endregion
}
