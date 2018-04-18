package me.xx2bab.bro.api.local;

import android.os.IInterface;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.api.ApiEntity;
import me.xx2bab.bro.api.IApiFinder;
import me.xx2bab.bro.base.BroErrorType;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.util.BroRuntimeLog;

public class AnnoApiFinder implements IApiFinder {

    private List<ApiEntity> apiEntityList;
    private ConcurrentHashMap<String, ApiEntity> cachedApis;

    public AnnoApiFinder() {
        cachedApis = new ConcurrentHashMap<>();
    }

    @Override
    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        // ignoring the aidl interface
        if (IInterface.class.getCanonicalName().equals(apiInterface.getSuperclass().getCanonicalName())) {
            return null;
        }

        // finding from cache
        if (cachedApis.containsKey(apiInterface.getCanonicalName())) {
            return (T) cachedApis.get(apiInterface.getCanonicalName());
        }

        if (!Bro.getBroMap().getBroApiMap().containsKey(apiInterface.getCanonicalName())) {
            return null;
        }

        BroProperties broProperties = Bro.getBroMap().getBroApiMap().get(apiInterface.getCanonicalName());
        IBroApi api;
        try {
            api = (IBroApi) Class.forName(broProperties.clazz).newInstance();
            api.onInit();
            doCaching(apiInterface.getCanonicalName(), api, broProperties);
            if (Bro.getBroInterceptor().onGetApi(Bro.appContext,
                    apiInterface.getCanonicalName(),
                    api,
                    broProperties)) {
                return null;
            }

        } catch (Exception e) {
            BroRuntimeLog.e("Bro Provider named " + apiInterface.getCanonicalName()
                    + " init failed : " + e.getMessage());
            Bro.getBroMonitor().onApiException(BroErrorType.API_INIT_ERROR);
            return null;
        }

        return (T) api;
    }


    private void doCaching(String apiCanonicalName, IBroApi api, BroProperties broProperties) {
        ApiEntity entity = new ApiEntity();
        entity.nick = apiCanonicalName;
        entity.instance = api;
        entity.properties = broProperties;
        cachedApis.put(apiCanonicalName, entity);
    }

}
