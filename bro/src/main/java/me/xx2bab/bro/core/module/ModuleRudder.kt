package me.xx2bab.bro.core.module

import android.util.Log
import me.xx2bab.bro.annotations.BroApi
import me.xx2bab.bro.annotations.BroModule
import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.IBroModule
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable
import me.xx2bab.bro.common.gen.anno.IBroApiInterfaceAndAliasMap
import me.xx2bab.bro.core.BroContext
import me.xx2bab.bro.core.base.BroErrorType
import me.xx2bab.bro.core.base.IBroInterceptor
import me.xx2bab.bro.core.base.IBroMonitor
import me.xx2bab.bro.core.util.BroRuntimeLog
import java.util.*

class ModuleRudder(private val broContext: BroContext) {

    private val moduleInstanceMap: MutableMap<String, ModuleEntity>
    private val interceptor: IBroInterceptor = broContext.interceptor
    private val monitor: IBroMonitor = broContext.monitor
    private val dag: DAG<String> = DAG()

    init {
        moduleInstanceMap = HashMap()
        val map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable::class.java)
                .getRoutingMapByAnnotation(BroModule::class.java)
        for ((_, value) in map!!) {
            val name = value!!.clazz
            try {
                val instance = Class.forName(name).newInstance() as IBroModule
                val bean = ModuleEntity(name, instance, value)
                moduleInstanceMap[name] = bean
                if (instance.getLaunchDependencies() == null) {
                    dag.addPrerequisite(name, null)
                } else {
                    val set = instance.getLaunchDependencies()
                    if (set != null) {
                        for (apiClazz in set) {
                            dag.addPrerequisite(name, getAttachedModule(apiClazz.canonicalName))
                        }
                    }
                }
            } catch (e: Exception) {
                BroRuntimeLog.e("Bro Module named " + name + " newInstance() failed : " + e.message)
                monitor.onModuleException(BroErrorType.MODULE_CLASS_NOT_FOUND_ERROR)
            }
        }
    }

    fun onCreate() {
        val res = dag.topologicalSort()
        if (res == null) {
            BroRuntimeLog.e("Bro topologicalSort result is emptu!")
        } else {
            for (moduleName in res) {
                if (moduleInstanceMap[moduleName] != null) {
                    moduleInstanceMap[moduleName]!!.instance.onCreate(broContext.context.get())
                }
            }
        }
    }

    fun <T : IBroModule?> getModule(moduleName: Class<T>): T? {
        var broModule: IBroModule? = null
        var properties: BroProperties? = null
        for ((key, value) in moduleInstanceMap) {
            if (key == moduleName.canonicalName) {
                broModule = value.instance
                properties = value.properties
                break
            }
        }
        if (interceptor.beforeGetModule(broContext.context.get()!!,
                        moduleName.canonicalName!!,
                        broModule,
                        properties)) {
            return null
        }
        if (broModule == null) {
            BroRuntimeLog.e("The Module Alias \"" + moduleName.canonicalName + "\" is not found by Bro!")
            monitor.onModuleException(BroErrorType.MODULE_CANT_FIND_TARGET)
            return null
        }
        return broModule as T
    }

    /**
     * TODO: refactor it by generating a better map for indexing
     */
    private fun getAttachedModule(apiClass: String): String {
        val apiImplAlias = broContext.broRudder
                .getImplementationByInterface(IBroApiInterfaceAndAliasMap::class.java)
                .getAliasByInterface(apiClass)
        val map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable::class.java)
                .getRoutingMapByAnnotation(BroApi::class.java)
        if (map!!.containsKey(apiImplAlias)) {
            return map[apiImplAlias]!!.module
        }
        throw IllegalArgumentException("Couldn't find api's corresponding module.")
    }

}