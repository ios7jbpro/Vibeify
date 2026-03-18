package com.ios7.vibeify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ios7.vibeify.MyClasses.EzTimer

class AppRestarterKotlin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_restarter)
        
        Log.d("RESTART", "AppRestarterKotlin started")
        
        EzTimer.runWithDelay(750) {
            val intent = Intent(this, MainKotlinActivity::class.java)
            Log.d("MANDEBUG", "Restarting MainKotlinActivity from AppRestarterKotlin")
            startActivity(intent)
            Log.d("MANDEBUG", "Exiting AppRestarterKotlin")
            finish()
        }
    }
}
