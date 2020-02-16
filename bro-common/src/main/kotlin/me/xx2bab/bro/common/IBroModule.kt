package me.xx2bab.bro.common

import android.content.Context

interface IBroModule {

    fun getLaunchDependencies(): Set<Class<out IBroApi>>?

    fun onCreate(context: Context?)

}