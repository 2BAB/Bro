package me.xx2bab.bro.core.api

import me.xx2bab.bro.common.IBroApi

interface IApiFinder {
    fun <T : IBroApi> getApi(apiInterface: Class<T>): T?
    fun getApi(alias: String): IBroApi?
}