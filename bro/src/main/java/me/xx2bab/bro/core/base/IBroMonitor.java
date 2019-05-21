package me.xx2bab.bro.core.base;

import me.xx2bab.bro.core.activity.ActivityRudder;

public interface IBroMonitor {

    void onActivityRudderException(int errorCode, ActivityRudder.Builder builder);

    void onModuleException(int errorCode);

    void onApiException(int errorCode);

}
