package me.xx2bab.bro.core.activity

import android.content.Context
import me.xx2bab.bro.core.BroContext

class ActivityRudder(private val broContext: BroContext) {
    fun startActivity(context: Context): Builder {
        return Builder(context, broContext)
    }

}