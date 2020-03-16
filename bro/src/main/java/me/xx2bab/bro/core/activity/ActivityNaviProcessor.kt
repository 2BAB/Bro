package me.xx2bab.bro.core.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import me.xx2bab.bro.annotations.BroActivity
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable
import me.xx2bab.bro.core.base.BroErrorType
import me.xx2bab.bro.core.util.BroRuntimeLog
import me.xx2bab.bro.core.util.BroRuntimeLog.e
import me.xx2bab.bro.core.util.ConvertUtils.convertUriToStringWithoutParams

class ActivityNaviProcessor {
    private val builder: Builder
    private var isIntentValidate: Boolean
    private var isIntercepted: Boolean

    private fun findActivity(intent: Intent): Intent? {
        for (finder in builder.broContext.activityFinders) {
            var temp: Intent? = Intent(intent)
            temp = finder.find(builder.context, temp!!, builder.broContext)
            if (temp != null) {
                return temp
            }
        }
        return null
    }

    private fun injectParamsFromUri(originIntent: Intent, uri: Uri?) {
        if (uri == null) {
            return
        }
        var bundle = originIntent.extras
        if (bundle == null) {
            bundle = Bundle()
        }
        try {
            val keySet = uri.queryParameterNames
            for (key in keySet) {
                val originValue = bundle.getString(key)
                if (TextUtils.isEmpty(originValue)) {
                    bundle.putString(key, uri.getQueryParameter(key))
                }
            }
        } catch (e: Exception) {
            e(e.message)
        }
        originIntent.putExtras(bundle)
    }

    @SuppressLint("WrongConstant")
    constructor(builder: Builder) { // Primary constructor doesn't support "return" statement
        val monitor = builder.broContext.monitor
        val interceptor = builder.broContext.interceptor
        this.builder = builder
        isIntentValidate = true
        isIntercepted = false
//        if (builder.context == null || builder.uri == null || builder.broContext.activityFinders == null) {
//            isIntentValidate = false
//            monitor.onActivityRudderException(
//                    BroErrorType.PAGE_MISSING_ARGUMENTS, null)
//            return
//        }
        val name = convertUriToStringWithoutParams(builder.uri)
        val properties = builder.broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable::class.java)
                .getRoutingMapByAnnotation(BroActivity::class.java)[name]
        val originIntent = Intent()
        originIntent.data = builder.uri
        try {
            if (interceptor.beforeFindActivity(builder.context,
                            builder.uri.toString(), originIntent, properties)) {
                isIntercepted = true
                return
            }
        } catch (e: Exception) {
            e(e.message)
        }
        val intent = findActivity(originIntent)
        if (intent == null) {
            isIntentValidate = false
            monitor.onActivityRudderException(
                    BroErrorType.PAGE_CANT_FIND_TARGET, builder)
            return
        }
        if (!TextUtils.isEmpty(builder.category)) {
            intent.addCategory(builder.category)
        }
        if (builder.extras != null) {
            intent.putExtras(builder.extras!!)
        }
        if (builder.flags != Builder.INVALIDATE) {
            intent.addFlags(builder.flags)
        }
        injectParamsFromUri(intent, builder.uri)
        try {
            if (interceptor.beforeStartActivity(builder.context,
                            builder.uri.toString(), intent, properties)) {
                isIntercepted = true
                return
            }
        } catch (e: Exception) {
            e(e.message)
        }
        if (builder.isDryRun) {
            return
        }
        if (builder.context is Activity) {
            if (builder.requestCode > 0) {
                (builder.context as Activity).startActivityForResult(intent, builder.requestCode)
            } else {
                builder.context.startActivity(intent) // cast is necessary
            }
            val transition = builder.broContext.activityTransition
            val enter = transition[0]
            val exit = transition[1]
            if (enter > 0 && exit > 0) {
                (builder.context as Activity).overridePendingTransition(enter, exit)
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            builder.context.startActivity(intent)
        }
    }
}