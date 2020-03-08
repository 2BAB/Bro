package me.xx2bab.bro.core.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import me.xx2bab.bro.core.BroContext
import me.xx2bab.bro.core.base.BroErrorType
import me.xx2bab.bro.core.util.BroRuntimeLog.e

class Builder(val context: Context, val broContext: BroContext) {
    var category: String? = null
    var extras: Bundle? = null
    var flags = INVALIDATE
    var requestCode = INVALIDATE
    var isDryRun = false
    lateinit var uri: Uri

    fun withCategory(category: String?): Builder {
        this.category = category
        return this
    }

    fun withExtras(extras: Bundle?): Builder {
        this.extras = extras
        return this
    }

    fun withFlags(flags: Int): Builder {
        this.flags = flags
        return this
    }

    fun forResult(requestCode: Int): Builder {
        this.requestCode = requestCode
        return this
    }

    fun dryRun(): Builder {
        isDryRun = true
        return this
    }

    fun toUri(targetUri: Uri): ActivityNaviProcessor {
        uri = targetUri
        return ActivityNaviProcessor(this)
    }

    fun toUrl(url: String?): ActivityNaviProcessor {
        return try {
            val uri = Uri.parse(url)
            toUri(uri)
        } catch (e: Exception) {
            e(e.message)
            broContext.monitor.onActivityRudderException(
                    BroErrorType.PAGE_MISSING_ARGUMENTS, this)
            ActivityNaviProcessor(this)
        }
    }

    companion object {
        const val INVALIDATE = -128
    }

}