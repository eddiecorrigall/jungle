package com.jungle.logger;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractLogger implements ILogger {
    @NotNull
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARN;

    @NotNull
    public static LogLevel getLogLevel() {
        String logLevelString = System.getenv("LOG_LEVEL");
        if (logLevelString == null || logLevelString.length() == 0) {
            return DEFAULT_LOG_LEVEL;
        }
        try {
            return LogLevel.valueOf(logLevelString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT_LOG_LEVEL;
        }
    }

    @NotNull
    private final Logger logger;

    @NotNull
    protected Logger getLogger() {
        return logger;
    }

    protected AbstractLogger(String name) {
        super();
        this.logger = Logger.getLogger(name);
        switch (getLogLevel()) {
            case DEBUG: getLogger().setLevel(Level.FINE); break;
            case INFO: getLogger().setLevel(Level.INFO); break;
            case WARN: getLogger().setLevel(Level.WARNING); break;
            case ERROR: getLogger().setLevel(Level.SEVERE); break;
            default: throw new UnsupportedOperationException();
        }
    }

    @Override
    public void debug(@NotNull String message) {
        getLogger().log(Level.FINE, message);
    }

    @Override
    public void info(@NotNull String message) {
        getLogger().log(Level.INFO, message);
    }

    @Override
    public void warn(@NotNull String message) {
        getLogger().log(Level.WARNING, message);
    }

    @Override
    public void error(@NotNull String message) {
        getLogger().log(Level.SEVERE, message);
    }

    @Override
    public void error(@NotNull String message, Throwable t) {
        getLogger().log(Level.SEVERE, message, t);
    }
}
