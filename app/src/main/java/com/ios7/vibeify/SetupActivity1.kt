package com.ios7.vibeify

import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.ios7.vibeify.MyClasses.EzBlur
import com.ios7.vibeify.MyClasses.EzTimer
import com.ios7.vibeify.MyClasses.EzTimerLooped

class SetupActivity1 : AppCompatActivity() {
    private lateinit var button: TextView
    private lateinit var nextbutton1: TextView
    private lateinit var nextbutton2: TextView
    private lateinit var nextbutton3: TextView
    private lateinit var backgroundlayout: LinearLayout
    private lateinit var backgroundTopleft: LinearLayout
    private lateinit var backgroundTopright: LinearLayout
    private lateinit var backgroundBottomleft: LinearLayout
    private lateinit var backgroundBottomright: LinearLayout
    private lateinit var mainframe: FrameLayout
    private lateinit var setupProgress1: LinearLayout
    private lateinit var setupProgress2: LinearLayout
    private lateinit var setupProgress3: LinearLayout
    private lateinit var setupGui: LinearLayout
    private lateinit var quitGui: LinearLayout
    private lateinit var textView60: TextView
    private lateinit var leavebutton: TextView
    private lateinit var cancelbutton: TextView
    private lateinit var config: SharedPreferences
    private lateinit var switchColorPreviews: Switch
    private lateinit var switchDisableAnims: Switch
    private var quitState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setup1)

        backgroundlayout = findViewById(R.id.backgroundlayout)
        backgroundTopleft = findViewById(R.id.background_topleft)
        backgroundTopright = findViewById(R.id.background_topright)
        backgroundBottomleft = findViewById(R.id.background_bottomleft)
        backgroundBottomright = findViewById(R.id.background_bottomright)
        nextbutton1 = findViewById(R.id.nextbutton1)
        nextbutton2 = findViewById(R.id.nextbutton2)
        nextbutton3 = findViewById(R.id.nextbutton3)
        button = findViewById(R.id.skipbutton)
        mainframe = findViewById(R.id.mainframe)
        leavebutton = findViewById(R.id.leavebutton)
        cancelbutton = findViewById(R.id.cancelbutton)
        setupProgress1 = findViewById(R.id.SetupProgress1)
        setupProgress2 = findViewById(R.id.SetupProgress2)
        setupProgress3 = findViewById(R.id.SetupProgress3)
        setupGui = findViewById(R.id.setupGui)
        quitGui = findViewById(R.id.quitGui)
        textView60 = findViewById(R.id.textView60)
        switchColorPreviews = findViewById(R.id.switchColorPreviews)
        switchDisableAnims = findViewById(R.id.switchDisableAnims)

        switchColorPreviews.isChecked = true
        config = getSharedPreferences("config", MODE_PRIVATE)
        config.edit().putString("colorextraction", "1").commit()

        backgroundTopleft.clipToOutline = true
        backgroundlayout.clipToOutline = true
        backgroundTopright.clipToOutline = true
        backgroundBottomleft.clipToOutline = true
        backgroundBottomright.clipToOutline = true

        setupProgress2.visibility = View.GONE
        setupProgress2.alpha = 0f
        setupProgress3.visibility = View.GONE
        setupProgress3.alpha = 0f
        mainframe.alpha = 0f
        backgroundlayout.alpha = 0f
        backgroundTopleft.alpha = 0f
        backgroundTopright.alpha = 0f
        backgroundBottomleft.alpha = 0f
        backgroundBottomright.alpha = 0f
        button.alpha = 0f
        nextbutton1.alpha = 0f
        quitGui.visibility = View.GONE
        quitGui.alpha = 0f
        EzBlur.setBlur(backgroundlayout, 10f)

        fadeToAlpha(mainframe, 1f, 0.05f, 1500, 25, "Timer1")
        fadeToAlpha(backgroundlayout, 0.2f, 0.01f, 1700, 25, "Timer2")
        fadeToAlpha(backgroundTopleft, 1f, 0.01f, 1900, 1, "Timer3")
        fadeToAlpha(backgroundTopright, 1f, 0.01f, 2300, 1, "Timer4")
        fadeToAlpha(backgroundBottomleft, 1f, 0.01f, 2700, 1, "Timer5")
        fadeToAlpha(backgroundBottomright, 1f, 0.01f, 2700, 1, "Timer6")
        fadeToAlpha(button, 1f, 0.05f, 4300, 1, null)
        fadeToAlpha(nextbutton1, 1f, 0.05f, 4500, 1, null)

        button.setOnClickListener {
            config.edit().putString("setupcomplete", "1").commit()
            finish()
        }

        nextbutton1.setOnClickListener {
            fadeOutIn(setupProgress1, setupProgress2, "Prog1lpha", "Prog2lpha")
        }

        nextbutton2.setOnClickListener {
            val loopedTimer15 = EzTimerLooped()
            loopedTimer15.start(1) {
                if (kotlin.math.abs(setupProgress2.alpha - 0f) < 0.1f) {
                    loopedTimer15.stop()
                } else {
                    setupProgress2.alpha = setupProgress2.alpha - 0.08f
                    Log.d("DEBUG", "Prog2lpha: ${setupProgress2.alpha}")
                }
            }
            EzTimer.runWithDelay(200) {
                setupProgress2.visibility = View.GONE
                setupProgress3.visibility = View.VISIBLE
                val loopedTimer16 = EzTimerLooped()
                loopedTimer16.start(1) {
                    if (kotlin.math.abs(setupProgress3.alpha - 1f) < 0.1f) {
                        loopedTimer16.stop()
                        TapTargetView.showFor(
                            this,
                            TapTarget
                                .forView(
                                    nextbutton3,
                                    "Pay attention!",
                                    "Tooltips like this one will be shown throught the app to guide you!",
                                ).outerCircleColor(R.color.backgroundviolent)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.textprimary)
                                .titleTextSize(20)
                                .titleTextColor(R.color.textprimary)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.textprimary)
                                .textColor(R.color.textprimary)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.background)
                                .drawShadow(true)
                                .cancelable(true)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60),
                        )
                    } else {
                        setupProgress3.alpha = setupProgress3.alpha + 0.08f
                        Log.d("DEBUG", "Prog3lpha: ${setupProgress3.alpha}")
                    }
                }
            }
        }

        nextbutton3.setOnClickListener {
            val loopedTimer18 = EzTimerLooped()
            loopedTimer18.start(1) {
                if (kotlin.math.abs(setupProgress3.alpha - 0f) < 0.1f) {
                    config.edit().putString("setupcomplete", "1").commit()
                    finish()
                    loopedTimer18.stop()
                } else {
                    setupProgress3.alpha = setupProgress3.alpha - 0.08f
                }
            }
        }

        switchColorPreviews.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            config.edit().putString("colorextraction", if (isChecked) "1" else "0").commit()
        }

        switchDisableAnims.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            config.edit().putString("disableanims", if (checked) "1" else "0").commit()
        }

        cancelbutton.setOnClickListener {
            swapQuitPanels(quitGui, setupGui)
            quitState = 0
        }

        leavebutton.setOnClickListener { finish() }
    }

    private fun fadeToAlpha(
        view: View,
        target: Float,
        step: Float,
        delayMs: Long,
        intervalMs: Long,
        debugLabel: String?,
    ) {
        EzTimer.runWithDelay(delayMs) {
            val loopedTimer = EzTimerLooped()
            loopedTimer.start(intervalMs) {
                if (kotlin.math.abs(view.alpha - target) < 0.01f) {
                    loopedTimer.stop()
                    if (debugLabel != null) {
                        Log.d("DEBUG", "$debugLabel stopped")
                    }
                } else {
                    view.alpha = (view.alpha + step).coerceAtMost(target)
                    if (debugLabel != null) {
                        Log.d("DEBUG", "Alpha: ${view.alpha}")
                    }
                }
            }
        }
    }

    private fun fadeOutIn(from: View, to: View, fromLabel: String, toLabel: String) {
        val fadeOutTimer = EzTimerLooped()
        fadeOutTimer.start(1) {
            if (kotlin.math.abs(from.alpha - 0f) < 0.1f) {
                fadeOutTimer.stop()
            } else {
                from.alpha = from.alpha - 0.08f
                Log.d("DEBUG", "$fromLabel: ${from.alpha}")
            }
        }
        EzTimer.runWithDelay(200) {
            from.visibility = View.GONE
            to.visibility = View.VISIBLE
            val fadeInTimer = EzTimerLooped()
            fadeInTimer.start(1) {
                if (kotlin.math.abs(to.alpha - 1f) < 0.1f) {
                    fadeInTimer.stop()
                } else {
                    to.alpha = to.alpha + 0.08f
                    Log.d("DEBUG", "$toLabel: ${to.alpha}")
                }
            }
        }
    }

    private fun swapQuitPanels(from: View, to: View) {
        val fadeOutTimer = EzTimerLooped()
        fadeOutTimer.start(1) {
            if (kotlin.math.abs(from.alpha - 0f) < 0.1f) {
                fadeOutTimer.stop()
            } else {
                from.alpha = from.alpha - 0.08f
            }
        }
        EzTimer.runWithDelay(200) {
            from.visibility = View.GONE
            to.visibility = View.VISIBLE
            val fadeInTimer = EzTimerLooped()
            fadeInTimer.start(1) {
                if (kotlin.math.abs(to.alpha - 1f) < 0.1f) {
                    fadeInTimer.stop()
                } else {
                    to.alpha = to.alpha + 0.08f
                }
            }
        }
    }

    override fun onBackPressed() {
        if (quitState == 0) {
            swapQuitPanels(setupGui, quitGui)
            quitState = 1
        } else {
            EzTimer.runWithDelay(250) { textView60.text = ">Quit Setup?<" }
            EzTimer.runWithDelay(500) { textView60.text = "Quit Setup?" }
            EzTimer.runWithDelay(750) { textView60.text = ">Quit Setup?<" }
            EzTimer.runWithDelay(1000) { textView60.text = "Quit Setup?" }
            EzTimer.runWithDelay(1250) { textView60.text = ">Quit Setup?<" }
            EzTimer.runWithDelay(1500) { textView60.text = "Quit Setup?" }
        }
    }
}
