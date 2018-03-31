package me.xx2bab.bro.api;

import java.util.HashMap;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;

public class ApiRudder {

    private IApiFinder annoApiFinder;

    public ApiRudder(HashMap<String, BroProperties> apiMap) {
        // todo: multiple impl
        annoApiFinder = new AnnoApiFinder(apiMap);
    }

    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return annoApiFinder.getApi(apiInterface);
    }

    public IBroApi getApi(String nick) {
        return annoApiFinder.getApi(nick);
    }


}
