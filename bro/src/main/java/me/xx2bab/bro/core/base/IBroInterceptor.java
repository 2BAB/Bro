package me.xx2bab.bro.core.base;

import android.content.Context;
import android.content.Intent;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;

public interface IBroInterceptor {

    boolean onFindActivity(Context context, String target, Intent intent, BroProperties properties);

    boolean onStartActivity(Context context, String target, Intent intent, BroProperties properties);

    boolean onGetApi(Context context, String target, IBroApi api, BroProperties properties);

    boolean onGetModule(Context context, String target, IBroModule module, BroProperties properties);

}
