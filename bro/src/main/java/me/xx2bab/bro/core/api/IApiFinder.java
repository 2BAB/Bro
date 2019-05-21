package me.xx2bab.bro.core.api;

import me.xx2bab.bro.common.IBroApi;

public interface IApiFinder {

    <T extends IBroApi> T getApi(Class<T> apiInterface);

    IBroApi getApi(String nick);

}
