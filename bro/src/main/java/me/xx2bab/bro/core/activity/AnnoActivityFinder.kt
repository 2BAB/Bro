package me.xx2bab.bro.core.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import me.xx2bab.bro.annotations.BroActivity
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable
import me.xx2bab.bro.core.BroContext
import me.xx2bab.bro.core.util.ConvertUtils

class AnnoActivityFinder : IBroActivityFinder {
    override fun find(context: Context, intent: Intent, broContext: BroContext): Intent? {
        val name = ConvertUtils.convertUriToStringWithoutParams(intent.data)
        val map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable::class.java)
                .getRoutingMapByAnnotation(BroActivity::class.java)
        val (activityName) = map!![name] ?: return null
        return try {
            if (TextUtils.isEmpty(activityName)) {
                return null
            }
            val destActivityCls = Class.forName(activityName)
            intent.setClass(context, destActivityCls)
            intent
        } catch (e: ClassNotFoundException) {
            null
        }
    }
}