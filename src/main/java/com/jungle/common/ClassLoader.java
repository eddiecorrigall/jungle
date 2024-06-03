package com.jungle.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.jungle.logger.FileLogger;

public class ClassLoader {

    @NotNull
    private static final FileLogger logger = new FileLogger(ClassLoader.class.getName());

    public static Class<?> loadClass(@NotNull String classPaths, @NotNull String className)
        throws MalformedURLException, ClassNotFoundException
    {
        logger.debug(String.format("loading class from %s", classPaths));
        List<URL> urls = new LinkedList<URL>();
        for (String path : classPaths.split(":")) {
            File file = new File(path);
            URL url = file.toURI().toURL();
            urls.add(url);
        }
        URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[0]));
        Class<?> clazz;
        try {
            clazz = urlClassLoader.loadClass(className);
        } finally {
            try {
                urlClassLoader.close();
            } catch (IOException e) {
            }
        }
        return clazz;
    }
}
