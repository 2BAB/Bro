package me.xx2bab.bro.core.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.text.TextUtils
import me.xx2bab.bro.core.BroContext
import java.util.*

class PackageManagerActivityFinder : IBroActivityFinder {

    override fun find(context: Context, intent: Intent, broContext: BroContext): Intent? {
        intent.setPackage(context.packageName)
        var resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo == null) { // support
            val list = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo = optimum(context, list)
        }
        if (resolveInfo != null) {
            intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
            return if (intent.component == null) {
                null
            } else intent
        }
        return null
    }

    private fun optimum(context: Context, list: List<ResolveInfo>?): ResolveInfo? {
        if (list == null) return null else if (list.size == 1) {
            return list[0]
        }
        val resolveInfo = ArrayList<SortedResolveInfo>()
        for (info in list) {
            if (!TextUtils.isEmpty(info.activityInfo.packageName)) {
                if (info.activityInfo.packageName.endsWith(context.packageName)) {
                    resolveInfo.add(SortedResolveInfo(info, info.priority, 1))
                } else {
                    val p1 = info.activityInfo.packageName
                    val p2 = context.packageName
                    val l1 = p1.split("\\.").toTypedArray()
                    val l2 = p2.split("\\.").toTypedArray()
                    if (l1.size >= 2 && l2.size >= 2) {
                        if (l1[0] == l2[0] && l1[1] == l2[1]) resolveInfo.add(SortedResolveInfo(info, info.priority, 0))
                    }
                }
            }
        }
        return if (resolveInfo.size > 0) {
            if (resolveInfo.size > 1) resolveInfo.sort()
            val ret = resolveInfo[0].info
            resolveInfo.clear()
            ret
        } else null
    }

    /**
     * @author Oasis
     */
    inner class SortedResolveInfo(val info: ResolveInfo,
                                  private val weight: Int,
                                  private val same: Int) : Comparable<SortedResolveInfo> {

        override fun compareTo(other: SortedResolveInfo): Int {
            if (this == other) return 0
            // order descending by priority
            return when {
                other.weight != weight -> other.weight - weight
                other.same != same -> other.same - same
                System.identityHashCode(this) < System.identityHashCode(other) -> -1
                else -> 1
            }
        }

    }
}