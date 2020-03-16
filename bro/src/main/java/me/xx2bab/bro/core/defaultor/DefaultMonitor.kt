package me.xx2bab.bro.core.defaultor

import me.xx2bab.bro.core.activity.Builder
import me.xx2bab.bro.core.base.IBroMonitor
import me.xx2bab.bro.core.util.BroRuntimeLog.e

class DefaultMonitor : IBroMonitor {
    override fun onActivityRudderException(errorCode: Int, builder: Builder) {}
    override fun onModuleException(errorCode: Int) {
        e("onModuleException: $errorCode")
    }

    override fun onApiException(errorCode: Int) {
        e("onApiException: $errorCode")
    }
}