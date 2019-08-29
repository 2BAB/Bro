package me.xx2bab.bro.core.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xx2bab.bro.core.BroContext;

public class PackageManagerActivityFinder implements IBroActivityFinder {

    public PackageManagerActivityFinder() {
    }

    @Override
    public Intent find(Context context, Intent intent, BroContext broContext) {
        intent.setPackage(context.getPackageName());
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo == null) { // support
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            resolveInfo = optimum(context, list);
        }

        if (resolveInfo != null) {
            intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            if (intent.getComponent() == null) {
                return null;
            }
            return intent;
        }

        return null;
    }

    protected ResolveInfo optimum(Context context, final List<ResolveInfo> list) {

        if (list == null)
            return null;
        else if (list.size() == 1) {
            return list.get(0);
        }

        final ArrayList<SortedResolveInfo> resolveInfo = new ArrayList<>();

        for (final ResolveInfo info : list) {

            if (!TextUtils.isEmpty(info.activityInfo.packageName)) {
                if (info.activityInfo.packageName.endsWith(context.getPackageName())) {
                    resolveInfo.add(new SortedResolveInfo(info, info.priority, 1));
                } else {
                    final String p1 = info.activityInfo.packageName;
                    final String p2 = context.getPackageName();
                    final String[] l1 = p1.split("\\.");
                    final String[] l2 = p2.split("\\.");
                    if (l1.length >= 2 && l2.length >= 2) {
                        if (l1[0].equals(l2[0]) && l1[1].equals(l2[1]))
                            resolveInfo.add(new SortedResolveInfo(info, info.priority, 0));
                    }
                }
            }
        }

        if (resolveInfo.size() > 0) {
            if (resolveInfo.size() > 1)
                Collections.sort(resolveInfo);
            final ResolveInfo ret = resolveInfo.get(0).info;
            resolveInfo.clear();
            return ret;
        } else
            return null;
    }


    /**
     * @author Oasis
     */
    protected final class SortedResolveInfo implements Comparable<SortedResolveInfo> {

        public SortedResolveInfo(final ResolveInfo info, final int weight, final int same) {
            this.info = info;
            this.weight = weight;
            this.same = same;
        }

        private final ResolveInfo info;
        private int weight = 0;
        private int same = 0;

        @Override
        public int compareTo(final SortedResolveInfo other) {
            if (this == other)
                return 0;

            // order descending by priority
            if (other.weight != this.weight)
                return other.weight - this.weight;
                // order descending by same package
            else if (other.same != this.same)
                return other.same - this.same;
                // then randomly
            else if (System.identityHashCode(this) < System.identityHashCode(other))
                return -1;
            else
                return 1;
        }
    }

}
