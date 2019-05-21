package me.xx2bab.bro.core;

import android.content.Context;

import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroMap;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.core.activity.ActivityRudder;
import me.xx2bab.bro.core.base.BroConfig;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.defaultor.DefaultInterceptor;
import me.xx2bab.bro.core.defaultor.DefaultMonitor;

public class Bro {

    private static IBroMap broMap;
    private static IBroInterceptor interceptor;
    private static IBroMonitor monitor;
    private static BroConfig config;

    private static BroManager broManager;

    public static Context appContext;

    public static void init(Context appContext,
                            IBroInterceptor interceptor,
                            IBroMonitor monitor,
                            BroConfig config) {
        Bro.appContext = appContext;
        Bro.broMap = getBroMap();
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

    public static synchronized IBroMap getBroMap() {
        if (broMap == null) {
            try {
                broMap = (IBroMap) Class.forName(Constants.MERGED_MAP_PACKAGE_NAME + "."
                        + Constants.MERGED_MAP_FILE_NAME).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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

    public static IBroApi getApi(String apiInterface) {
        return broManager.getApi(apiInterface);
    }

    public static IBroModule getModule(String moduleNick) {
        return broManager.getModule(moduleNick);
    }


}
