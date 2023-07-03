package com.jungle.error;

import org.jetbrains.annotations.NotNull;

public class CompilerError extends Error {
    public CompilerError(@NotNull String message) {
        super(message);
    }
    public CompilerError(@NotNull String message, @NotNull Throwable throwable) {
        super(message, throwable);
    }
}
