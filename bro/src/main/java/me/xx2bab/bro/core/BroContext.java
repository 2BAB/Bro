package me.xx2bab.bro.core;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

import me.xx2bab.bro.core.activity.IBroActivityFinder;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.util.BroRudder;

public class BroContext {

    public IBroInterceptor interceptor;
    public IBroMonitor monitor;
    public BroRudder broRudder;
    public WeakReference<Context> context;
    public Class fallbackActivityCls;
    public List<IBroActivityFinder> activityFinders;
    public int[] activityTransition;
    public boolean apiCacheEnabled;

}
