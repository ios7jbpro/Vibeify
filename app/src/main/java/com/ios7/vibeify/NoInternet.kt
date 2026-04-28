package com.ios7.vibeify

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ios7.vibeify.MyClasses.EzTimer

class NoInternet : AppCompatActivity() {
    private lateinit var tx: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_no_internet)
        tx = findViewById(R.id.textView19)

        EzTimer.runWithDelay(1000) {
            tx.text = "No internet connection detected.\nTerminating the app in 5 seconds."
            EzTimer.runWithDelay(1000) {
                tx.text = "No internet connection detected.\nTerminating the app in 4 seconds."
                EzTimer.runWithDelay(1000) {
                    tx.text = "No internet connection detected.\nTerminating the app in 3 seconds."
                    EzTimer.runWithDelay(1000) {
                        tx.text =
                            "No internet connection detected.\nTerminating the app in 2 seconds."
                        EzTimer.runWithDelay(1000) {
                            tx.text =
                                "No internet connection detected.\nTerminating the app in 1 second."
                            EzTimer.runWithDelay(1000) {
                                tx.text = "Exiting the app..."
                                EzTimer.runWithDelay(1000) {
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
