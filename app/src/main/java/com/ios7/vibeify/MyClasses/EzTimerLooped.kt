package com.ios7.vibeify.MyClasses

import android.os.Handler
import android.os.Looper

class EzTimerLooped {
    private var action: Runnable? = null
    private var intervalMillis: Long = 0
    private var running = false

    fun start(intervalMillis: Long, action: Runnable) {
        if (running) {
            return
        }

        this.intervalMillis = intervalMillis
        this.action = action
        running = true
        runLoop()
    }

    private fun runLoop() {
        if (!running) {
            return
        }

        action?.run()
        handler.postDelayed({ runLoop() }, intervalMillis)
    }

    fun stop() {
        running = false
    }

    fun isRunning(): Boolean {
        return running
    }

    companion object {
        private val handler = Handler(Looper.getMainLooper())
    }
}
