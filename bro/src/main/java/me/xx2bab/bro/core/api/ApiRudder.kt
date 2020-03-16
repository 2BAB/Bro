package me.xx2bab.bro.core.api

import me.xx2bab.bro.common.IBroApi
import me.xx2bab.bro.core.BroContext

class ApiRudder(broContext: BroContext) {
    private val annoApiFinder: IApiFinder

    init { // todo: multiple impl
        annoApiFinder = AnnoApiFinder(broContext)
    }

    fun <T : IBroApi> getApi(apiInterface: Class<T>): T? {
        return annoApiFinder.getApi(apiInterface)
    }

    fun getApi(nick: String): IBroApi? {
        return annoApiFinder.getApi(nick)
    }

}