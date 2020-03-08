package me.xx2bab.bro.core.util

import android.net.Uri
import android.os.Bundle
import java.util.*

object ConvertUtils {
    fun convertHashMapToBundle(hashMap: HashMap<String?, String?>?, b : Bundle?): Bundle {
        var bundle = b
        if (bundle == null) {
            bundle = Bundle()
        }
        if (hashMap != null) {
            for ((key, value) in hashMap) {
                bundle.putString(key, value)
            }
        }
        return bundle
    }

    fun convertHashMapToBundle(hashMap: HashMap<String?, String?>?): Bundle {
        val bundle = Bundle()
        return convertHashMapToBundle(hashMap, bundle)
    }

    fun convertHashMapToUrlParams(hashMap: HashMap<String?, String?>?): String {
        val builder = StringBuilder()
        if (hashMap != null) {
            for ((key, value) in hashMap) {
                builder.append(key).append("=").append(value).append("&")
            }
            builder.delete(builder.length - 1, builder.length)
        }
        return builder.toString()
    }

    @JvmStatic
    fun convertUriToStringWithoutParams(uri: Uri): String {
        val targetString = uri.toString()
        val questionMark = targetString.indexOf("?")
        return if (questionMark > 0) {
            targetString.substring(0, questionMark)
        } else {
            targetString
        }
    }
}