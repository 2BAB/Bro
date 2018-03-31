package me.xx2bab.bro.defaultor;


import me.xx2bab.bro.base.IBroMonitor;
import me.xx2bab.bro.activity.ActivityRudder;
import me.xx2bab.bro.util.BroRuntimeLog;

public class DefaultMonitor implements IBroMonitor {

    @Override
    public void onActivityRudderException(int errorCode, ActivityRudder.Builder builder) {
        BroRuntimeLog.e("onActivityRudderException: " + errorCode);
    }

    @Override
    public void onModuleException(int errorCode) {
        BroRuntimeLog.e("onModuleException: " + errorCode);
    }

    @Override
    public void onApiException(int errorCode) {
        BroRuntimeLog.e("onApiException: " + errorCode);
    }
}
