package com.jungle.error;

import org.jetbrains.annotations.NotNull;

public class SaveError extends Error {
    public SaveError(@NotNull String message) {
        super(message);
    }

    public SaveError(@NotNull String message, @NotNull Throwable throwable) {
        super(message, throwable);
    }
}
