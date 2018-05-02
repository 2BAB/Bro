package me.xx2bab.bro.livereload.core;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Map;

public class DispatchClassloader extends ClassLoader {

    public DispatchClassloader(ClassLoader origin) {
        super(origin.getParent());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (LiveReload.instance.plugadgets == null || LiveReload.instance.plugadgets.isEmpty()) {
            throw new ClassNotFoundException(name);
        }
        for (Map.Entry<String, PlugadgetInfo> entry : LiveReload.instance.plugadgets.entrySet()) {
            if (name.startsWith(entry.getKey())) {
                try {
                    Class<?> clz = entry.getValue().classloader.findClass(name);
                    if (clz != null) {
                        return clz;
                    }
                } catch (Exception e) {
                    // nothing
                }
            }
        }

        /*for (Map.Entry<String, PlugadgetClassloader> entry : classloaderHashMap.entrySet()) {
            if (!name.startsWith(entry.getKey())) {
                try {
                    Class<?> clz = entry.getValue().findClass(name);
                    if (clz != null) {
                        return clz;
                    }
                } catch (Exception e) {
                    // nothing
                }
            }
        }*/

        throw new ClassNotFoundException(name);
    }

    public static void injectClassloader(Context baseContext) {
        ClassLoader pathClassloader = baseContext.getClassLoader();
        DispatchClassloader dispatchClassloader = new DispatchClassloader(pathClassloader);
        final Class<?> clz = ClassLoader.class;
        try {
            final Field parentField = clz.getDeclaredField("parent");
            parentField.setAccessible(true);
            parentField.set(pathClassloader, dispatchClassloader);
        } catch (Exception e) {
            Log.i("Bro", e.getMessage());
        }
    }
}
