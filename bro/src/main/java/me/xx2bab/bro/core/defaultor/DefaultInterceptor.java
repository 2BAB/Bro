package me.xx2bab.bro.core.defaultor;

import android.content.Context;
import android.content.Intent;

import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;

public class DefaultInterceptor implements IBroInterceptor {

    @Override
    public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
        return false;
    }

    @Override
    public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
        return false;
    }

    @Override
    public boolean beforeGetApi(Context context, String target, IBroApi api, BroProperties properties) {
        return false;
    }

    @Override
    public boolean beforeGetModule(Context context, String target, IBroModule module, BroProperties properties) {
        return false;
    }

}
