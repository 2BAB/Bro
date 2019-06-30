package me.xx2bab.bro.core.defaultor;


import me.xx2bab.bro.core.activity.Builder;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.util.BroRuntimeLog;

public class DefaultMonitor implements IBroMonitor {

    @Override
    public void onActivityRudderException(int errorCode, Builder builder) {

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
