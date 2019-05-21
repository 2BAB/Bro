package me.xx2bab.bro.core.api;

import me.xx2bab.bro.common.IBroApi;

public class BinderApiFinder implements IApiFinder {

    @Override
    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return null;
    }

    @Override
    public IBroApi getApi(String nick) {
        return null;
    }

}
