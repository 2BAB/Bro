package me.xx2bab.bro.core.api;

import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.core.BroContext;

public class ApiRudder {

    private IApiFinder annoApiFinder;

    public ApiRudder(BroContext broContext) {
        // todo: multiple impl
        annoApiFinder = new AnnoApiFinder(broContext);
    }

    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return annoApiFinder.getApi(apiInterface);
    }

    public IBroApi getApi(String nick) {
        return annoApiFinder.getApi(nick);
    }


}
