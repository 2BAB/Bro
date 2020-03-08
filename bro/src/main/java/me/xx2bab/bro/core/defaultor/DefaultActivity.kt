package me.xx2bab.bro.core.defaultor

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import me.xx2bab.bro.core.defaultor.ArgsParser.parseHintOfType

class DefaultActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val hint = parseHintOfType(intent, this)
        setContentView(generateDefaultView(this, hint))
    }

    private fun generateDefaultView(context: Context, errorHint: String?): View {
        val root = RelativeLayout(context)
        val notice = TextView(context)
        if (errorHint == null) {
            notice.text = "?"
        } else {
            notice.text = errorHint
        }
        val layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        root.addView(notice, layoutParams)
        return root
    }
}