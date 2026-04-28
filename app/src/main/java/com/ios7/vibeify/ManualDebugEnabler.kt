package com.ios7.vibeify

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ios7.vibeify.MyClasses.EzTimer

class ManualDebugEnabler : AppCompatActivity() {
    private lateinit var config: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manual_debug_enabler)
        config = getSharedPreferences("config", Activity.MODE_PRIVATE)

        if (config.getString("forcedDebug", "") == "1") {
            Log.d("MANDEBUG", "Turning off forcedDebug")
            config.edit().putString("forcedDebug", "0").commit()
            config.edit().putString("debugMode", "0").commit()
        } else {
            Log.d("MANDEBUG", "Turning on forcedDebug")
            config.edit().putString("forcedDebug", "1").commit()
            config.edit().putString("debugMode", "1").commit()
        }

        Log.d("MANDEBUG", "Delaying for 3 seconds")
        EzTimer.runWithDelay(3000) {
            val intent = Intent(this, MainActivity::class.java)
            Log.d("MANDEBUG", "Restarting MainActivity")
            startActivity(intent)
            Log.d("MANDEBUG", "Exiting manualdebug enabler")
            finish()
        }
    }
}
