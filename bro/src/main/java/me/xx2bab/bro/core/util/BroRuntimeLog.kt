package me.xx2bab.bro.core.util

import android.util.Log

object BroRuntimeLog {

    private const val TAG = "[Bro] "

    @JvmField
    var logEnabled = true

    @JvmStatic
    fun i(msg: String?) {
        if (!logEnabled) {
            return
        }
        Log.i(TAG, msg)
    }

    @JvmStatic
    fun e(msg: String?) {
        if (!logEnabled) {
            return
        }
        Log.e(TAG, msg)
    }
}