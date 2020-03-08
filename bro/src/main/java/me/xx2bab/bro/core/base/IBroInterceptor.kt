package me.xx2bab.bro.core.base

import android.content.Context
import android.content.Intent
import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.IBroApi
import me.xx2bab.bro.common.IBroModule

interface IBroInterceptor {
    fun beforeFindActivity(context: Context,
                           target: String,
                           intent: Intent,
                           properties: BroProperties?): Boolean

    fun beforeStartActivity(context: Context,
                            target: String,
                            intent: Intent,
                            properties: BroProperties?): Boolean

    fun beforeGetApi(context: Context,
                     target: String,
                     api: IBroApi,
                     properties: BroProperties?): Boolean

    fun beforeGetModule(context: Context,
                        target: String,
                        module: IBroModule?,
                        properties: BroProperties?): Boolean
}