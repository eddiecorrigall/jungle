package com.jungle.logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class FileLogger extends AbstractLogger {
    private static final String LOG_FILE = "jungle.log";

    @Nullable
    private static FileHandler fileHandler = null;

    @NotNull
    protected static FileHandler getFileHandler() throws IOException {
        if (fileHandler == null) {
            fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
        }
        return fileHandler;
    }

    public FileLogger(@NotNull String name) {
        super(name);
        try {
            getLogger().addHandler(getFileHandler());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}