package me.xx2bab.bro.sample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.base.BroConfig;
import me.xx2bab.bro.base.IBroInterceptor;
import me.xx2bab.bro.base.IBroMonitor;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.activity.ActivityRudder;
import me.xx2bab.bro.sample.defaultpage.DefaultActivity;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.init first if it need
        // Then init bro
        initBro(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initBro(Context baseContext) {
        BroConfig config = new BroConfig.Builder()
                .setDefaultActivity(DefaultActivity.class)
                .setLogEnable(true)
                .build();
        IBroInterceptor interceptor = new IBroInterceptor() {

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
        };
        IBroMonitor monitor = new IBroMonitor() {

            @Override
            public void onActivityRudderException(int errorCode, ActivityRudder.Builder builder) {

            }

            @Override
            public void onModuleException(int errorCode) {

            }

            @Override
            public void onApiException(int errorCode) {

            }
        };

        Bro.init(baseContext,
                new BroInfoMapImpl(),
                interceptor,
                monitor,
                config);
    }
}
