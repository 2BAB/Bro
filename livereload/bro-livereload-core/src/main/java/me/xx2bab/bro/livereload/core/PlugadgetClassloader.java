package me.xx2bab.bro.livereload.core;

import dalvik.system.DexClassLoader;

public class PlugadgetClassloader extends DexClassLoader {

    public PlugadgetClassloader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
