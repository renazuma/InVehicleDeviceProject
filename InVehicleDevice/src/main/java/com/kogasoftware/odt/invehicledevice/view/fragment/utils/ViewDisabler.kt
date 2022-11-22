package com.kogasoftware.odt.invehicledevice.view.fragment.utils

import android.os.Handler
import android.view.View
import kotlin.jvm.JvmOverloads

/**
 * ボタンなどの多重操作の防止
 */
object ViewDisabler {
    @JvmOverloads
    @JvmStatic
    fun disable(view: View, timeoutMillis: Int = 400) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, timeoutMillis.toLong())
    }
}
