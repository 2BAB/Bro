package me.xx2bab.bro.defaultor;

import android.content.Context;
import android.content.Intent;

import me.xx2bab.bro.base.IBroInterceptor;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;

public class DefaultInterceptor implements IBroInterceptor {

    @Override
    public boolean onFindActivity(Context context, String target, Intent intent, BroProperties properties) {
        return false;
    }

    @Override
    public boolean onStartActivity(Context context, String target, Intent intent, BroProperties properties) {
        return false;
    }

    @Override
    public boolean onGetApi(Context context, String target, IBroApi api, BroProperties properties) {
        return false;
    }

    @Override
    public boolean onGetModule(Context context, String target, IBroModule module, BroProperties properties) {
        return false;
    }

}
