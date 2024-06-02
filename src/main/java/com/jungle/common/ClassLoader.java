package com.jungle.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class ClassLoader {

    public static Class<?> load(@NotNull String classPaths, @NotNull String className)
        throws MalformedURLException, ClassNotFoundException
    {
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

    public static Class<?> load(@NotNull String className)
        throws MalformedURLException, ClassNotFoundException
    {
        // TODO: break-out configuration
        String classPath = System.getenv("JUNGLEPATH");
        if (classPath == null) {
            classPath = ".";
        }
        System.out.println(String.format("loading class from %s", classPath));
        return load(classPath, className);
    }
}
