package me.xx2bab.bro.api;

import me.xx2bab.bro.common.IBroApi;

public interface IApiFinder {

    <T extends IBroApi> T getApi(Class<T> apiInterface);

}
