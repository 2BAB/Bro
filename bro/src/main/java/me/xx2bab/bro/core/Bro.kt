package me.xx2bab.bro.core

import android.content.Context
import android.support.annotation.VisibleForTesting
import me.xx2bab.bro.common.IBroApi
import me.xx2bab.bro.common.IBroModule
import me.xx2bab.bro.core.activity.ActivityRudder
import me.xx2bab.bro.core.activity.Builder
import me.xx2bab.bro.core.api.ApiRudder
import me.xx2bab.bro.core.module.ModuleRudder
import java.util.concurrent.atomic.AtomicBoolean

class Bro internal constructor(private val broContext: BroContext) {

    private lateinit var activityRudder: ActivityRudder
    private lateinit var apiRudder: ApiRudder
    private lateinit var moduleRudder: ModuleRudder

    private fun onCreate() {
        activityRudder = ActivityRudder(broContext)
        apiRudder = ApiRudder(broContext)
        moduleRudder = ModuleRudder(broContext)
        moduleRudder.onCreate()
    }

    fun startActivityFrom(context: Context): Builder {
        return activityRudder.startActivity(context)
    }

    fun <T : IBroApi> getApi(apiInterface: Class<T>): T? {
        return apiRudder.getApi(apiInterface)
    }

    fun getApi(apiInterface: String): IBroApi? {
        return apiRudder.getApi(apiInterface)
    }

    fun <T : IBroModule?> getModule(moduleClass: Class<T>): T? {
        return moduleRudder.getModule(moduleClass)
    }

    companion object {
        @JvmStatic
        private val initialized = AtomicBoolean()

        @JvmStatic
        @Volatile
        private var bro: Bro? = null

        @JvmStatic
        fun initialize(context: Context, builder: BroBuilder) {
            if (initialized.get()) {
                return
            }
            initialized.set(true)
            bro = builder.build(context.applicationContext)
            bro!!.onCreate()
        }

        @JvmStatic
        fun get(): Bro {
            // We tend to use non-null return value for better usage experience,
            // if the app didn't call initialize properly bro should throw NPE here.
            return bro!!
        }

        @JvmStatic
        @VisibleForTesting
        fun set(context: Context, builder: BroBuilder) {
            bro = builder.build(context.applicationContext)
        }
    }

}