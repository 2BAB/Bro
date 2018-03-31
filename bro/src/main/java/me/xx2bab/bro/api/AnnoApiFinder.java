package me.xx2bab.bro.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.base.BroErrorType;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.util.BroRuntimeLog;

public class AnnoApiFinder implements IApiFinder {

    private List<ApiEntity> apiEntityList;
    private HashMap<String, BroProperties> apiMap;

    public AnnoApiFinder(HashMap<String, BroProperties> apiMap) {
        this.apiMap = apiMap;
        initApiEntity();
    }

    @Override
    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        IBroApi broApi = null;
        BroProperties properties = null;
        for (ApiEntity bean : apiEntityList) {
            if (bean.interfaze.equals(apiInterface.getCanonicalName())) {
                broApi = bean.instance;
                properties = bean.properties;
                break;
            }
        }

        if (Bro.getBroInterceptor().onGetApi(Bro.appContext,
                apiInterface.getCanonicalName(),
                broApi,
                properties)) {
            return null;
        }
        if (broApi == null) {
            BroRuntimeLog.e("The Api Impl of \"" + apiInterface.getCanonicalName() + "\" is not found by Bro!");
            Bro.getBroMonitor().onApiException(BroErrorType.API_CANT_FIND_TARGET);
            return null;
        }
        return (T) broApi;
    }

    @Override
    public IBroApi getApi(String nick) {
        IBroApi api = null;
        BroProperties properties = null;
        for (ApiEntity bean : apiEntityList) {
            if (bean.nick.equals(nick)) {
                api = bean.instance;
                properties = bean.properties;
                break;
            }
        }
        if (!Bro.getBroInterceptor().onGetApi(Bro.appContext,
                nick,
                api,
                properties)) {
            return null;
        }
        if (api == null) {
            BroRuntimeLog.e("The Api Nick \"" + nick + "\" is not found by Bro!");
            Bro.getBroMonitor().onApiException(BroErrorType.API_CANT_FIND_TARGET);
            return null;
        }
        return api;
    }

    public void initApiEntity() {
        apiEntityList = new ArrayList<>();
        for (Map.Entry<String, BroProperties> entry : apiMap.entrySet()) {
            try {
                JSONObject extraParam = JSON.parseObject(entry.getValue().extraParams);
                ApiEntity entity = new ApiEntity();
                entity.nick = entry.getKey();
                entity.instance = (IBroApi) Class.forName(entry.getValue().clazz).newInstance();
                entity.interfaze = extraParam.getString("ApiInterface");
                entity.properties = entry.getValue();

                entity.instance.onInit(); // todo: specify order

                apiEntityList.add(entity);
            } catch (Exception e) {
                BroRuntimeLog.e("Bro Provider named " + entry.getValue().clazz + " init failed : " + e.getMessage());
                Bro.getBroMonitor().onApiException(BroErrorType.API_INIT_ERROR);
            }
        }
    }

}
