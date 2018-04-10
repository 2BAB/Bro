package me.xx2bab.bro;

import android.content.Context;

import me.xx2bab.bro.defaultor.DefaultInterceptor;
import me.xx2bab.bro.defaultor.DefaultMonitor;
import me.xx2bab.bro.base.IBroInterceptor;
import me.xx2bab.bro.base.IBroMonitor;
import me.xx2bab.bro.base.BroConfig;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroMap;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.activity.ActivityRudder;

public class Bro {

    private static IBroMap broMap;
    private static IBroInterceptor interceptor;
    private static IBroMonitor monitor;
    private static BroConfig config;

    private static BroManager broManager;

    public static Context appContext;

    public static void init(Context appContext,
                            IBroMap broMap,
                            IBroInterceptor interceptor,
                            IBroMonitor monitor,
                            BroConfig config) {
        Bro.appContext = appContext;
        Bro.broMap = broMap;
        Bro.interceptor = interceptor;
        Bro.monitor = monitor;
        Bro.config = config;

        if (interceptor == null) {
            Bro.interceptor = new DefaultInterceptor();
        }

        if (monitor == null) {
            Bro.monitor = new DefaultMonitor();
        }
        broManager = new BroManager(broMap);
    }

    public static IBroMap getBroMap() {
        return broMap;
    }

    public static IBroInterceptor getBroInterceptor() {
        return interceptor;
    }

    public static IBroMonitor getBroMonitor() {
        return monitor;
    }

    public static BroConfig getConfig() {
        return config;
    }

    public static ActivityRudder.Builder startActivityFrom(Context context) {
        return broManager.startPageFrom(context);
    }

    public static <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return broManager.getApi(apiInterface);
    }

    public static <T extends IBroModule> T getModuleContext(Class<T> moduleClass) {
        return broManager.getModule(moduleClass);
    }


}
