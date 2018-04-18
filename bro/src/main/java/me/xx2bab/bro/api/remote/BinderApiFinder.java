package me.xx2bab.bro.api.remote;

import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.api.ApiEntity;
import me.xx2bab.bro.api.IApiFinder;
import me.xx2bab.bro.base.BroErrorType;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.util.BroRuntimeLog;

public class BinderApiFinder implements IApiFinder {

    private ConcurrentHashMap<String, ApiEntity> cachedApis;

    public BinderApiFinder() {
        cachedApis = new ConcurrentHashMap<>();
    }

    @Override
    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        // only parsing the aidl interface
        if (!IInterface.class.getCanonicalName().equals(apiInterface.getSuperclass().getCanonicalName())) {
            return null;
        }

        // finding from cache
        String canonicalName = apiInterface.getCanonicalName();
        if (cachedApis.containsKey(canonicalName)) {
            return (T) cachedApis.get(canonicalName).instance;
        }

        // finding by binder pool
        try {
            IBinder binder = BinderManager.getInstance().getBinderPool().queryBinder(canonicalName);
            Class stub = Class.forName(canonicalName + "$Stub");
            Method asInterface = stub.getMethod("asInterface", IBinder.class);
            IBroApi api = (IBroApi) asInterface.invoke(null, binder);
            api.onInit();
            doCaching(canonicalName, api);
            return (T) api;
        } catch (Exception e) {
            BroRuntimeLog.e("Bro Provider named " + canonicalName
                    + " init failed : " + e.getMessage());
            Bro.getBroMonitor().onApiException(BroErrorType.API_INIT_ERROR);
            return null;
        }
    }

    private void doCaching(String apiCanonicalName, IBroApi api) {
        ApiEntity entity = new ApiEntity();
        entity.nick = apiCanonicalName;
        entity.instance = api;
        entity.properties = Bro.getBroMap().getBroApiMap().get(apiCanonicalName);
        cachedApis.put(apiCanonicalName, entity);
    }

}
