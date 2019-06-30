package me.xx2bab.bro.core.base;

import me.xx2bab.bro.core.activity.Builder;

public interface IBroMonitor {

    void onActivityRudderException(int errorCode, Builder builder);

    void onModuleException(int errorCode);

    void onApiException(int errorCode);

}
