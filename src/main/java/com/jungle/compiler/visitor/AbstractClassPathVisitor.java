package com.jungle.compiler.visitor;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractClassPathVisitor implements IVisitor {
    /* When a classpath is required, inherit this abstract class for the property */

    @NotNull
    private final String classPath;

    @NotNull
    public String getClassPath() {
        return classPath;
    }

    public AbstractClassPathVisitor(@NotNull final String classPath) {
        super();
        this.classPath = classPath;
    }
}
