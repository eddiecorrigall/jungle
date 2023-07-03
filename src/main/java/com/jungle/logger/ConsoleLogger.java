package com.jungle.logger;

import org.jetbrains.annotations.NotNull;

import java.util.logging.*;

public class ConsoleLogger extends AbstractLogger {
    public ConsoleLogger(@NotNull String name) {
        super(name);
        getLogger().addHandler(new ConsoleHandler());
    }
}
