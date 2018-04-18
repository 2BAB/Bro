package me.xx2bab.bro.api;

import java.util.List;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.base.BroErrorType;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.util.BroRuntimeLog;

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

        BroRuntimeLog.e("The Api Impl of \"" + apiInterface.getCanonicalName() + "\" is not found by Bro!");
        Bro.getBroMonitor().onApiException(BroErrorType.API_CANT_FIND_TARGET);
        return null;
    }

}