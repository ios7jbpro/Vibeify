package com.ios7.vibeify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ios7.vibeify.MyClasses.EzTimer

class AppRestarter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_restarter)
        EzTimer.runWithDelay(750) {
            val intent = Intent(this, MainActivity::class.java)
            Log.d("MANDEBUG", "Restarting MainActivity")
            startActivity(intent)
            Log.d("MANDEBUG", "Exiting manualdebug enabler")
            finish()
        }
    }
}
