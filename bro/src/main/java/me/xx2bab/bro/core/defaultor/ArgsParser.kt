package me.xx2bab.bro.core.defaultor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.xx2bab.bro.core.R
import me.xx2bab.bro.core.util.Constants

internal object ArgsParser {
    @JvmStatic
    fun parseHintOfType(intent: Intent, context: Context): String {
        val type = intent.getIntExtra(Constants.KEY_DEFAULT_PAGE_TYPE, Constants.DEFAULT_PAGE_NOT_FOUND)
        return parseHintOfType(type, context)
    }

    fun parseHintOfType(bundle: Bundle, context: Context): String {
        val type = bundle.getInt(Constants.KEY_DEFAULT_PAGE_TYPE)
        return parseHintOfType(type, context)
    }

    private fun parseHintOfType(type: Int, context: Context): String {
        return when (type) {
            Constants.DEFAULT_PAGE_NOT_FOUND -> {
                context.resources.getString(R.string.default_page_not_found)
            }
            Constants.DEFAULT_PAGE_PERMISSION_DENIED -> {
                context.resources.getString(R.string.default_page_permission_denied)
            }
            Constants.DEFAULT_PAGE_NOT_NEW_INSTANCE_METHOD -> {
                context.resources.getString(R.string.default_page_not_new_instance_method)
            }
            Constants.DEFAULT_PAGE_UNKNOWN_ERROR -> {
                context.resources.getString(R.string.default_page_unknown_error)
            }
            else -> {
                context.resources.getString(R.string.default_page_not_found)
            }
        }
    }
}