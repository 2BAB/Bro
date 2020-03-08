package me.xx2bab.bro.core.util

import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.gen.GenOutputs.Companion.generateClassNameForImplementation
import java.util.*

class BroRudder {
    private val implCache: MutableMap<Class<*>, Any>
    @Suppress("UNCHECKED_CAST")
    fun <T> getImplementationByInterface(interfaze: Class<T>): T {
        val cacheRes = implCache[interfaze]
        return if (cacheRes != null) {
            cacheRes as T
        } else try {
            val implClassName = generateClassNameForImplementation(interfaze)
            val res = Class.forName(Constants.GEN_PACKAGE_NAME + "." + implClassName).newInstance()
            implCache[interfaze] = res
            res as T
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    init {
        implCache = HashMap()
    }
}