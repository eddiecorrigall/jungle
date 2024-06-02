package com.jungle.compiler.visitor;

import com.jungle.ast.INode;
import com.jungle.ast.NodeType;
import com.jungle.common.ClassLoader;

import java.net.MalformedURLException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MultitaskVisitor implements IVisitor {
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

    @Override
    public boolean canVisit(@NotNull INode ast) {
        return NodeType.MULTITASK.equals(ast.getType());
    }

    @Override
    public void visit(@NotNull MethodVisitor mv, @NotNull INode ast) {
        if (!canVisit(ast)) {
            throw new Error("expected multitask");
        }
        if (ast.getLeft() == null) {
            throw new Error("missing multitask class");
        }
        boolean isClassStringLiteral = NodeType.LITERAL_STRING.equals(ast.getLeft().getType());
        if (!isClassStringLiteral) {
            throw new Error("expected multitask class to be string literal");
        }

        /* Note: It makes sense to find and validate the user defined class (UDC) before running.
         * However, we want to avoid including the UDC in the compiler classpath.
         * At compile time, the compiler must be provided the UDC classpath to complete the compilation.
         */
        Class<?> clazz;
        String clazzName = ast.getLeft().getStringValue();
        try {
            // Note: Class.forName() does not consider classpath
            clazz = ClassLoader.load(clazzName);
        } catch (ClassNotFoundException e) {
            throw new Error("multitask class not found - " + clazzName, e);
        } catch (MalformedURLException e) {
            throw new Error("malformed jungle classpath", e);
        }
        if (!hasInterface(clazz, Runnable.class.getName())) {
            throw new Error("expected multitask class to be runnable");
        }
        if (!hasPublicDefaultConstructor(clazz)) {
            throw new Error("expected multitask class to have public default constructor");
        }
        String clazzOwner = clazzName.replace('.', '/'); // eg. "com/jungle/examples/RunnableTest";

        // new Thread...
        String threadClassType = "java/lang/Thread";
        mv.visitTypeInsn(Opcodes.NEW, threadClassType);
        mv.visitInsn(Opcodes.DUP);

            // new Multitask()
            mv.visitTypeInsn(Opcodes.NEW, clazzOwner);
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL, clazzOwner, "<init>", "()V", false);

        // new Thread(new Multitask())
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL, threadClassType, "<init>", "(Ljava/lang/Runnable;)V", false);

        // Thread::start()
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, threadClassType, "start", "()V", false);
    }
}