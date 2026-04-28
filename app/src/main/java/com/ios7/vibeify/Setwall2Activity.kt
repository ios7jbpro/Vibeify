package com.ios7.vibeify

import android.app.Activity
import android.app.ActivityOptions
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseBooleanArray
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.Random
import java.util.Timer
import java.util.TimerTask

class Setwall2Activity : AppCompatActivity() {
    private val timer = Timer()

    private lateinit var progressbar1: ProgressBar
    private lateinit var textview1: TextView
    private var dismissDelay: TimerTask? = null
    private lateinit var config: SharedPreferences
    private var coordinatorLayout: CoordinatorLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setwall2)
        initialize(savedInstanceState)
        initializeLogic()
    }

    private fun initialize(savedInstanceState: Bundle?) {
        progressbar1 = findViewById(R.id.progressbar1)
        textview1 = findViewById(R.id.textview1)
        config = getSharedPreferences("config", Activity.MODE_PRIVATE)
    }

    private fun initializeLogic() {
        progressbar1.visibility = View.GONE
        textview1.text = "Image failed to load. Going back in 5 seconds."
        dismissDelay =
            object : TimerTask() {
                override fun run() {
                    runOnUiThread { finish() }
                }
            }
        timer.schedule(dismissDelay, config.getString("timeout", "")!!.toDouble().toLong())
    }

    fun _convertToBottomSheet() {}

    override fun finish() {
        BottomSheetBehavior.from(coordinatorLayout!!.getChildAt(0))
            .state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun superFinish() {
        super.finish()
    }

    override fun setContentView(layoutResID: Int) {
        if (coordinatorLayout == null) {
            overridePendingTransition(0, 0)
            coordinatorLayout = CoordinatorLayout(this)
            makeActivityTransparent()
            coordinatorLayout!!.setBackgroundColor(0x80000000.toInt())
            coordinatorLayout!!.setOnClickListener { finish() }
        }

        coordinatorLayout!!.removeAllViews()
        val params =
            CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        val behavior = BottomSheetBehavior<View>()
        params.behavior = behavior
        behavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        superFinish()
                        overridePendingTransition(0, 0)
                    }
                }
            },
        )
        val inflated = layoutInflater.inflate(layoutResID, null)
        coordinatorLayout!!.addView(inflated, params)

        if (coordinatorLayout!!.parent != null) {
            (coordinatorLayout!!.parent as ViewGroup).removeView(coordinatorLayout)
        }
        super.setContentView(coordinatorLayout)
        inflated.post { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
    }

    private fun makeActivityTransparent() {
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(0))
        try {
            val getActivityOptions = Activity::class.java.getDeclaredMethod("getActivityOptions")
            getActivityOptions.isAccessible = true
            val options = getActivityOptions.invoke(this)
            var translucentConversionListenerClazz: Class<*>? = null
            for (clazz in Activity::class.java.declaredClasses) {
                if (clazz.simpleName.contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz
                }
            }
            val convertToTranslucent =
                Activity::class.java.getDeclaredMethod(
                    "convertToTranslucent",
                    translucentConversionListenerClazz,
                    ActivityOptions::class.java,
                )
            convertToTranslucent.isAccessible = true
            convertToTranslucent.invoke(this, null, options)
        } catch (_: Throwable) {
        }
    }

    @Deprecated("Legacy helper")
    fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Legacy helper")
    fun getLocationX(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[0]
    }

    @Deprecated("Legacy helper")
    fun getLocationY(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[1]
    }

    @Deprecated("Legacy helper")
    fun getRandom(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }

    @Deprecated("Legacy helper")
    fun getCheckedItemPositionsToArray(list: ListView): ArrayList<Double> {
        val result = ArrayList<Double>()
        val checked: SparseBooleanArray = list.checkedItemPositions
        for (index in 0 until checked.size()) {
            if (checked.valueAt(index)) {
                result.add(checked.keyAt(index).toDouble())
            }
        }
        return result
    }

    @Deprecated("Legacy helper")
    fun getDip(input: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            input.toFloat(),
            resources.displayMetrics,
        )
    }

    @Deprecated("Legacy helper")
    fun getDisplayWidthPixels(): Int = resources.displayMetrics.widthPixels

    @Deprecated("Legacy helper")
    fun getDisplayHeightPixels(): Int = resources.displayMetrics.heightPixels
}
