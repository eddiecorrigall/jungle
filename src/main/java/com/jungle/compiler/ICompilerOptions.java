package com.jungle.compiler;

import org.jetbrains.annotations.NotNull;

public interface ICompilerOptions {
    @NotNull
    String getClassPath();

    @NotNull
    String getTargetPath();
}
