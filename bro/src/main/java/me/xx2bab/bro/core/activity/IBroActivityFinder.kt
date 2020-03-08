package me.xx2bab.bro.core.activity

import android.content.Context
import android.content.Intent
import me.xx2bab.bro.core.BroContext

interface IBroActivityFinder {
    fun find(context: Context, intent: Intent, broContext: BroContext): Intent?
}