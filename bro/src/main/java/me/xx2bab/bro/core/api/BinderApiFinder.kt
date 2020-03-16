package me.xx2bab.bro.core.api

import me.xx2bab.bro.common.IBroApi

class BinderApiFinder : IApiFinder {
    override fun <T : IBroApi> getApi(apiInterface: Class<T>): T? {
        return null
    }

    override fun getApi(nick: String): IBroApi? {
        return null
    }
}