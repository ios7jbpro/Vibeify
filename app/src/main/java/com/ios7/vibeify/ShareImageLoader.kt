package com.ios7.vibeify

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ios7.vibeify.MyClasses.EzTimerLooped

class ShareImageLoader : AppCompatActivity() {
    private lateinit var config: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(false)
        enableEdgeToEdge()
        setContentView(R.layout.activity_share_image_loader)
        config = getSharedPreferences("config", Activity.MODE_PRIVATE)

        val loopedTimer = EzTimerLooped()
        loopedTimer.start(50) {
            if (config.getString("isImageReady", "") == "1") {
                config.edit().putString("isImageReady", "0").apply()
                finish()
                loopedTimer.stop()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            return true
        }
        return super.onTouchEvent(event)
    }
}
