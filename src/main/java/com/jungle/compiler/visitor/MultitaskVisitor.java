package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.common.ClassLoader;
import com.jungle.compiler.Compiler;
import com.jungle.compiler.operand.OperandStackContext;
import com.jungle.logger.FileLogger;

import java.net.MalformedURLException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MultitaskVisitor extends AbstractClassPathVisitor {
    @NotNull
    private static final FileLogger logger = new FileLogger(MultitaskVisitor.class.getSimpleName());

    @Nullable
    private MainVisitor mainVisitor;

    private MainVisitor getMainVisitor() {
        if (mainVisitor == null) {
            mainVisitor = new MainVisitor(getClassPath());
        }
        return mainVisitor;
    }

    protected static boolean hasInterface(@NotNull Class<?> clazz, @NotNull String interfaceName) {
        for (Class<?> clazzInterface : clazz.getInterfaces()) {
            if (interfaceName.equals(clazzInterface.getName())) {
                return true;
            }
        }
        return false;
    }

    protected static boolean hasPublicDefaultConstructor(@NotNull Class<?> clazz) {
        // TODO: check for public default constructor on multitask class
        return true; // TODO
    }

    public MultitaskVisitor(@NotNull final String classPath) {
        super(classPath);
    }

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.MULTITASK.equals(ast.getType());
    }

    @Override
    public void visit(
        @NotNull MethodVisitor mv,
        @NotNull INode ast,
        @NotNull OperandStackContext context
    ) {
        if (!canVisit(ast)) {
            throw new Error("expected multitask");
        }
        if (ast.getLeft() == null) {
            throw new Error("missing multitask class");
        }
        String className = ast.getLeft().getStringValue();
        boolean hasInlineCode = ast.getRight() != null;
        if (hasInlineCode) {
            // generate the new class file using inline code
            logger.debug(String.format("compile inline multitask class - %s", className));
            Compiler compiler = new Compiler();
            logger.info("inline code - " + ast.getRight());
            compiler.compileRunnable(className, getMainVisitor(), ast.getRight());
        }
        boolean hasClassStringLiteral = NodeType.LITERAL_STRING.equals(ast.getLeft().getType());
        if (!hasClassStringLiteral) {
            throw new Error("expected multitask class to be string literal");
        }
        /* Note: It makes sense to find and validate the user class for the program before running.
         * However, we want to avoid including the user class in the compiler classpath.
         * At compile time, the compiler must be provided the isolated classpath to complete the compilation.
         */
        Class<?> clazz;
        try {
            // Note: Class.forName() does not consider classpath
            clazz = ClassLoader.loadClass(getClassPath(), className);
        } catch (ClassNotFoundException e) {
            throw new Error("multitask class not found - " + className, e);
        } catch (MalformedURLException e) {
            throw new Error("malformed jungle classpath", e);
        }
        if (!hasInterface(clazz, Runnable.class.getName())) {
            throw new Error("expected multitask class to be runnable");
        }
        if (!hasPublicDefaultConstructor(clazz)) {
            throw new Error("expected multitask class to have public default constructor");
        }
        String ownerClassName = Compiler.asInternalClassName(className); // eg. "com/jungle/examples/RunnableTest";

        // new Thread...
        String threadClassType = "java/lang/Thread";
        mv.visitTypeInsn(Opcodes.NEW, threadClassType);
        mv.visitInsn(Opcodes.DUP);

            // new Multitask()
            mv.visitTypeInsn(Opcodes.NEW, ownerClassName);
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL, ownerClassName, "<init>", "()V", false);

        // new Thread(new Multitask())
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL, threadClassType, "<init>", "(Ljava/lang/Runnable;)V", false);

        // Thread::start()
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, threadClassType, "start", "()V", false);
    }
}
