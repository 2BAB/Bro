package me.xx2bab.bro.base;

import me.xx2bab.bro.activity.ActivityRudder;

public interface IBroMonitor {

    void onActivityRudderException(int errorCode, ActivityRudder.Builder builder);

    void onModuleException(int errorCode);

    void onApiException(int errorCode);

}
