package me.xx2bab.bro.sample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.core.base.BroConfig;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.core.activity.ActivityRudder;
import me.xx2bab.bro.sample.defaultpage.SampleDefaultActivity;

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
                .setDefaultActivity(SampleDefaultActivity.class)
                .setLogEnable(true)
                .build();
        IBroInterceptor interceptor = new IBroInterceptor() {

            @Override
            public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
                return false;
            }

            @Override
            public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
                Log.i("App", properties.toJsonString());
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
                interceptor,
                monitor,
                config);
    }
}
