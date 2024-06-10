package com.jungle.compiler.visitor;

import org.jetbrains.annotations.NotNull;

import com.jungle.compiler.ICompilerOptions;

public abstract class AbstractVisitor implements IVisitor {
    @NotNull
    private final ICompilerOptions options;

    public ICompilerOptions getCompilerOptions() {
        return options;
    }

    public AbstractVisitor(@NotNull ICompilerOptions options) {
        super();
        this.options = options;
    }
}
