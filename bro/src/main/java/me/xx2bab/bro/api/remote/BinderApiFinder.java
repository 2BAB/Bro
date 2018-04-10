package me.xx2bab.bro.api.remote;

import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import me.xx2bab.bro.api.IApiFinder;
import me.xx2bab.bro.common.IBroApi;

public class BinderApiFinder implements IApiFinder {

    private ConcurrentHashMap<String, IBroApi> cachedApis;

    public BinderApiFinder() {
        cachedApis = new ConcurrentHashMap<>();
    }

    @Override
    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        if (!IInterface.class.getCanonicalName().equals(apiInterface.getSuperclass().getCanonicalName())) {
            return null;
        }
        String canonicalName = apiInterface.getCanonicalName();
        if (cachedApis.containsKey(canonicalName)) {
            return (T) cachedApis.get(canonicalName);
        }
        try {
            IBinder binder = BinderManager.getInstance().getBinderPool().queryBinder(canonicalName);
            Class stub = Class.forName(apiInterface.getCanonicalName() + "$Stub");
            Method asInterface = stub.getMethod("asInterface", IBinder.class);
            IBroApi broApi = (IBroApi) asInterface.invoke(null, binder);
            cachedApis.put(canonicalName, broApi);
            return (T) broApi;
        } catch (Exception e) {
            return null;
        }
    }

}
