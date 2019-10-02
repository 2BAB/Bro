package me.xx2bab.bro.core;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.atomic.AtomicBoolean;

import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.core.activity.ActivityRudder;
import me.xx2bab.bro.core.activity.Builder;
import me.xx2bab.bro.core.api.ApiRudder;
import me.xx2bab.bro.common.AbstractBroModule;
import me.xx2bab.bro.core.module.ModuleRudder;

public class Bro {

    private static AtomicBoolean initialized = new AtomicBoolean();
    private static volatile Bro bro;

    private ActivityRudder activityRudder;
    private ApiRudder apiRudder;
    private ModuleRudder moduleRudder;
    private BroContext broContext;

    Bro(BroContext broContext) {
        this.broContext = broContext;
    }

    private void onCreate() {
        activityRudder = new ActivityRudder(broContext);
        apiRudder = new ApiRudder(broContext);
        moduleRudder = new ModuleRudder(broContext);
        moduleRudder.onCreate();
    }

    public static void initialize(Context context, BroBuilder builder) {
        if (initialized.get()) {
            return;
        }
        initialized.set(true);
        bro = builder.build(context.getApplicationContext());
        bro.onCreate();
    }

    public static Bro get() {
        return bro;
    }

    @VisibleForTesting
    public static void set(Context context, BroBuilder builder) {
        bro = builder.build(context.getApplicationContext());
    }

    public Builder startActivityFrom(Context context) {
        return activityRudder.startActivity(context);
    }

    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return apiRudder.getApi(apiInterface);
    }

    public IBroApi getApi(String apiInterface) {
        return apiRudder.getApi(apiInterface);
    }

    public <T extends AbstractBroModule> T getModule(Class<T> moduleClass) {
        return moduleRudder.getModule(moduleClass);
    }

}
