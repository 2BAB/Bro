package me.xx2bab.bro.core.base

import me.xx2bab.bro.core.activity.Builder

interface IBroMonitor {
    fun onActivityRudderException(errorCode: Int, builder: Builder)
    fun onModuleException(errorCode: Int)
    fun onApiException(errorCode: Int)
}