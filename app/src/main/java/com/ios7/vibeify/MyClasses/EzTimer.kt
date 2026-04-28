package com.ios7.vibeify.MyClasses

import android.os.Handler
import android.os.Looper

object EzTimer {
    private val handler = Handler(Looper.getMainLooper())

    @JvmStatic
    fun runWithDelay(delayMillis: Long, action: Runnable) {
        handler.postDelayed(action, delayMillis)
    }
}
