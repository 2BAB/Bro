package me.xx2bab.bro.core;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

import me.xx2bab.bro.common.IBroRoutingTable;
import me.xx2bab.bro.core.activity.IActivityFinder;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;

public class BroContext {

    public IBroInterceptor interceptor;
    public IBroMonitor monitor;
    public IBroRoutingTable routingTable;
    public WeakReference<Context> context;
    public boolean moduleAutoInitEnabled;
    public boolean apiAutoInitEnabled;
    public Class fallbackActivityCls;
    public List<IActivityFinder> activityFinders;
    public int[] activityTransition;

}
