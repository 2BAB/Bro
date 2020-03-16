package me.xx2bab.bro.core

import android.content.Context
import android.content.Intent
import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.IBroApi
import me.xx2bab.bro.common.IBroModule
import me.xx2bab.bro.core.activity.AnnoActivityFinder
import me.xx2bab.bro.core.activity.Builder
import me.xx2bab.bro.core.activity.IBroActivityFinder
import me.xx2bab.bro.core.activity.PackageManagerActivityFinder
import me.xx2bab.bro.core.base.IBroInterceptor
import me.xx2bab.bro.core.base.IBroMonitor
import me.xx2bab.bro.core.defaultor.DefaultActivity
import me.xx2bab.bro.core.util.BroRudder
import me.xx2bab.bro.core.util.BroRuntimeLog
import java.lang.ref.WeakReference
import java.util.*

class BroBuilder {
    private var interceptor: IBroInterceptor? = null
    private var monitor: IBroMonitor? = null
    private var logEnabled = true
    private var apiCacheEnabled = false
    private var activityCls: Class<*>? = null
    private var activityFinders: List<IBroActivityFinder>? = null
    private var activityTransition: IntArray? = null

    fun setInterceptor(interceptor: IBroInterceptor?): BroBuilder {
        this.interceptor = interceptor
        return this
    }

    fun setMonitor(monitor: IBroMonitor?): BroBuilder {
        this.monitor = monitor
        return this
    }

    fun setLogEnable(logEnabled: Boolean): BroBuilder {
        this.logEnabled = logEnabled
        return this
    }

    fun setApiCacheEnable(apiCacheEnabled: Boolean): BroBuilder {
        this.apiCacheEnabled = apiCacheEnabled
        return this
    }

    fun setDefaultActivity(activityCls: Class<*>?): BroBuilder {
        this.activityCls = activityCls
        return this
    }

    fun setActivityFinders(pageFinders: MutableList<IBroActivityFinder>?): BroBuilder {
        activityFinders = pageFinders
        return this
    }

    fun setActivityTransition(enterAnim: Int, exitAnim: Int): BroBuilder {
        if (enterAnim > 0 && exitAnim > 0) {
            activityTransition!![0] = enterAnim
            activityTransition!![1] = exitAnim
        } else {
            throw IllegalArgumentException("Bro ActivityRudder Transition Arguments is Illegal")
        }
        return this
    }

    fun build(context: Context): Bro {
        if (interceptor == null) {
            interceptor = object : IBroInterceptor {
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
        }
        if (monitor == null) {
            monitor = object : IBroMonitor {
                override fun onActivityRudderException(errorCode: Int, builder: Builder) {}
                override fun onModuleException(errorCode: Int) {}
                override fun onApiException(errorCode: Int) {}
            }
        }
        if (activityCls == null) {
            activityCls = DefaultActivity::class.java
        }
        if (activityFinders == null) {
            activityFinders = listOf(AnnoActivityFinder(),
                    PackageManagerActivityFinder())
        }
        if (activityTransition == null) {
            activityTransition = intArrayOf(-1, -1)
        }
        val broContext = BroContext(interceptor!!,
                monitor!!,
                BroRudder(),
                WeakReference(context),
                activityCls!!,
                activityFinders!!,
                activityTransition!!,
                apiCacheEnabled
        )
        BroRuntimeLog.logEnabled = logEnabled
        return Bro(broContext)
    }
}