package com.jungle.error;

import org.jetbrains.annotations.NotNull;

public class LoadError extends Error {
    public LoadError(@NotNull String message) {
        super(message);
    }

    public LoadError(@NotNull String message, @NotNull Throwable throwable) {
        super(message, throwable);
    }
}
