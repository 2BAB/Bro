package me.xx2bab.bro.core;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.core.activity.AnnoActivityFinder;
import me.xx2bab.bro.core.activity.Builder;
import me.xx2bab.bro.core.activity.IBroActivityFinder;
import me.xx2bab.bro.core.activity.PackageManagerActivityFinder;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.defaultor.DefaultActivity;
import me.xx2bab.bro.core.util.BroRudder;
import me.xx2bab.bro.core.util.BroRuntimeLog;

public class BroBuilder {

    private IBroInterceptor interceptor;
    private IBroMonitor monitor;

    private boolean logEnabled = true;
    private boolean apiCacheEnabled = false;
    private Class activityCls;
    private List<IBroActivityFinder> activityFinders;
    private int[] activityTransition;

    public BroBuilder setInterceptor(IBroInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public BroBuilder setMonitor(IBroMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    public BroBuilder setLogEnable(boolean logEnabled) {
        this.logEnabled = logEnabled;
        return this;
    }

    public BroBuilder setApiCacheEnable(boolean apiCacheEnabled) {
        this.apiCacheEnabled = apiCacheEnabled;
        return this;
    }

    public BroBuilder setDefaultActivity(Class activityCls) {
        this.activityCls = activityCls;
        return this;
    }

    public BroBuilder setActivityFinders(List<IBroActivityFinder> pageFinders) {
        this.activityFinders = pageFinders;
        return this;
    }

    public BroBuilder setActivityTransition(int enterAnim, int exitAnim) {
        if (enterAnim > 0 && exitAnim > 0) {
            activityTransition[0] = enterAnim;
            activityTransition[1] = exitAnim;
        } else {
            throw new IllegalArgumentException("Bro ActivityRudder Transition Arguments is Illegal");
        }
        return this;
    }

    Bro build(Context context) {
        if (interceptor == null) {
            interceptor = new IBroInterceptor() {
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
            };
        }

        if (monitor == null) {
            monitor = new IBroMonitor() {
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
        }

        if (activityCls == null) {
            activityCls = DefaultActivity.class;
        }

        if (activityFinders == null) {
            activityFinders = new ArrayList<>();
            activityFinders.add(new AnnoActivityFinder());
            activityFinders.add(new PackageManagerActivityFinder());
        }

        if (activityTransition == null) {
            activityTransition = new int[]{-1, -1};
        }

        BroContext broContext = new BroContext();

        broContext.context = new WeakReference<>(context);
        broContext.broRudder = new BroRudder();
        broContext.interceptor = interceptor;
        broContext.monitor = monitor;
        broContext.fallbackActivityCls = activityCls;
        broContext.activityFinders = activityFinders;
        broContext.activityTransition = activityTransition;
        broContext.apiCacheEnabled = apiCacheEnabled;

        BroRuntimeLog.logEnabled = logEnabled;
        return new Bro(broContext);
    }

}
