package me.xx2bab.bro.core.defaultor

import android.content.Context
import android.content.Intent
import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.IBroApi
import me.xx2bab.bro.common.IBroModule
import me.xx2bab.bro.core.base.IBroInterceptor

class DefaultInterceptor : IBroInterceptor {
    override fun beforeFindActivity(context: Context, target: String, intent: Intent, properties: BroProperties?): Boolean {
        return false
    }

    override fun beforeStartActivity(context: Context, target: String, intent: Intent, properties: BroProperties?): Boolean {
        return false
    }

    override fun beforeGetApi(context: Context, target: String, api: IBroApi, properties: BroProperties?): Boolean {
        return false
    }

    override fun beforeGetModule(context: Context, target: String, module: IBroModule, properties: BroProperties?): Boolean {
        return false
    }
}