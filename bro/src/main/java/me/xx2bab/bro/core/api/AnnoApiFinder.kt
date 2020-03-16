package me.xx2bab.bro.core.api

import me.xx2bab.bro.annotations.BroApi
import me.xx2bab.bro.annotations.BroSingleton
import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.IBroApi
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable
import me.xx2bab.bro.common.gen.anno.IBroApiInterfaceAndAliasMap
import me.xx2bab.bro.core.BroContext
import me.xx2bab.bro.core.base.BroErrorType
import me.xx2bab.bro.core.base.IBroInterceptor
import me.xx2bab.bro.core.base.IBroMonitor
import me.xx2bab.bro.core.util.BroRuntimeLog.e
import java.util.*

class AnnoApiFinder(private val broContext: BroContext) : IApiFinder {

    private val aliasInstanceMap: MutableMap<String, IBroApi> = HashMap()
    private val interceptor: IBroInterceptor = broContext.interceptor
    private val monitor: IBroMonitor = broContext.monitor

    @Suppress("UNCHECKED_CAST")
    override fun <T : IBroApi> getApi(apiInterface: Class<T>): T? {
        val alias = broContext.broRudder
                .getImplementationByInterface(IBroApiInterfaceAndAliasMap::class.java)
                .getAliasByInterface(apiInterface.canonicalName)
        return getApi(alias!!) as T?
    }

    @Suppress("UNCHECKED_CAST")
    override fun getApi(alias: String): IBroApi? {
        if (alias.isEmpty()) {
            e("The Api alias is EMPTY!")
            monitor.onApiException(BroErrorType.API_CANT_FIND_TARGET)
            return null
        }
        val instance: IBroApi?
        val properties: BroProperties?
        val aliasPropertiesMap = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable::class.java)
                .getRoutingMapByAnnotation(BroApi::class.java)
        // If api cache was enabled, looking up from cache map first
        if (aliasInstanceMap.containsKey(alias)) {
            instance = aliasInstanceMap[alias]
            properties = aliasPropertiesMap[alias]
        } else {
            try {
                properties = aliasPropertiesMap[alias]
                instance = Class.forName(properties!!.clazz).newInstance() as IBroApi
                instance.onCreate()
                if (broContext.apiCacheEnabled || properties.extraAnnotations.containsKey(
                                BroSingleton::class.java.canonicalName)) {
                    aliasInstanceMap[alias] = instance
                }
            } catch (e: Exception) {
                e("The Api alias \"$alias\" is not found by Bro!")
                monitor.onApiException(BroErrorType.API_CANT_FIND_TARGET)
                return null
            }
        }
        return if (interceptor.beforeGetApi(broContext.context.get()!!, alias, instance!!, properties)) {
            null
        } else instance
    }

}