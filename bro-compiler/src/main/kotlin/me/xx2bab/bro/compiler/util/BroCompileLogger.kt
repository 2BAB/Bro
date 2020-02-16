package me.xx2bab.bro.compiler.util

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

object BroCompileLogger {

    private const val TAG = "[Bro]: [bro-compiler] "
    private var messager: Messager? = null

    @JvmStatic
    fun setMessager(msg: Messager?) {
        messager = msg
    }

    @JvmStatic
    fun i(msg: String) {
        val msgWithPrefix = "$TAG[Info] $msg"
        if (messager != null) {
            messager!!.printMessage(Diagnostic.Kind.NOTE, msgWithPrefix)
        } else {
            println(msgWithPrefix)
        }
    }

    @JvmStatic
    fun e(msg: String) {
        val msgWithPrefix = "$TAG[Error] $msg"
        if (messager != null) {
            messager!!.printMessage(Diagnostic.Kind.ERROR, msgWithPrefix)
        } else {
            System.err.println(msgWithPrefix)
        }
    }

}