package me.xx2bab.bro.sample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.core.BroBuilder;
import me.xx2bab.bro.core.activity.AnnoActivityFinder;
import me.xx2bab.bro.core.activity.Builder;
import me.xx2bab.bro.core.activity.IBroActivityFinder;
import me.xx2bab.bro.core.activity.PackageManagerActivityFinder;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.sample.defaultpage.SampleDefaultActivity;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.init first if it is needed, then init BroPlugadget
        // TODO: Add new BroPlugadget initialization here.
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBro();
    }

    private void initBro() {
        IBroInterceptor interceptor = new IBroInterceptor() {

            @Override
            public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
                return false;
            }

            @Override
            public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
//                String loginAnnotation = "me.xx2bab.bro.sample.common.annotation.RequireLoginSession";
//                if (properties.extraAnnotations.containsKey(loginAnnotation)) {
//                    Intent i = new Intent(context, LoginActivty.class);
//                    i.putExtra("orginalTarget", intent);
//                    startActivity(i);
//                    return true;
//                }
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
            public void onActivityRudderException(int errorCode, Builder builder) {

            }

            @Override
            public void onModuleException(int errorCode) {

            }

            @Override
            public void onApiException(int errorCode) {

            }
        };
        List<IBroActivityFinder> finders = new ArrayList<>();
        finders.add(new AnnoActivityFinder());
        finders.add(new PackageManagerActivityFinder());

        BroBuilder broBuilder = new BroBuilder()
                .setDefaultActivity(SampleDefaultActivity.class)
                .setActivityFinders(finders)
                .setLogEnable(false)
                .setMonitor(monitor)
                .setInterceptor(interceptor);

        Bro.initialize(this, broBuilder);
    }
}
