package me.xx2bab.bro.compiler.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class GradleClassLoader {

    private URLClassLoader urlClassLoader;

    public GradleClassLoader(String[] classPaths) {
        List<URL> urls = new ArrayList<>();
        for(String classPath : classPaths) {
            try {
                urls.add(new URL(classPath));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Classloader URL " + classPath
                        + "is invalid.");
            }
        }
        urlClassLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
    }

    public Class load(String clazz) throws ClassNotFoundException {
        return urlClassLoader.loadClass(clazz);
    }

}
