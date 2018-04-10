package me.xx2bab.bro.api;

import java.util.List;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.common.IBroApi;

public class ApiRudder {

    private List<IApiFinder> apiFinders;

    public ApiRudder() {
        apiFinders = Bro.getConfig().getApiFinders();
    }

    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        for (IApiFinder apiFinder : apiFinders) {
            T api = apiFinder.getApi(apiInterface);
            if (api != null) {
                return api;
            }
        }
        return null;
    }

}
