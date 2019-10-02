package me.xx2bab.bro.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractBroModule {

    private WeakReference<Context> contextWeakReference;
    private Map<Class, Object> singletonObjCache;

    public abstract Set<Class<? extends IBroApi>> dependencies();

    public void onCreate(Context context) {
        contextWeakReference = new WeakReference<>(context);
        singletonObjCache = new HashMap<>(8);
    }

    @Nullable
    public Context getContext() {
        return contextWeakReference == null ? null : contextWeakReference.get();
    }

    @Nullable
    public <T> T getDependency(@NonNull Class<T> clazz) {
        try {
            Object instance = Class.forName(clazz.getCanonicalName()).newInstance();
            return (T) instance;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T getDependencyBySingleton(Class<T> clazz) {
        Object instance = singletonObjCache.get(clazz);
        if (instance != null) {
            return (T) instance;
        }
        try {
            instance = Class.forName(clazz.getCanonicalName()).newInstance();
            singletonObjCache.put(clazz, instance);
            return (T) instance;
        } catch (Exception e) {
            return null;
        }
    }

}
