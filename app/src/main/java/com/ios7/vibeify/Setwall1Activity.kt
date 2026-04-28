package com.ios7.vibeify

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.IOException
import java.util.Random
import java.util.Timer
import java.util.TimerTask

class Setwall1Activity : AppCompatActivity() {
    private val timer = Timer()

    private lateinit var imageview1: ImageView
    private lateinit var wallLink: SharedPreferences
    private var loadDelay: TimerTask? = null
    private val loadDialogIntent = Intent()
    private lateinit var config: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setwall1)
        initialize(savedInstanceState)
        initializeLogic()
    }

    private fun initialize(savedInstanceState: Bundle?) {
        imageview1 = findViewById(R.id.imageview1)
        wallLink = getSharedPreferences("wallLink", Activity.MODE_PRIVATE)
        config = getSharedPreferences("config", Activity.MODE_PRIVATE)
    }

    private fun initializeLogic() {
        Toast.makeText(applicationContext, "Loading in high-res and setting wallpaper...", Toast.LENGTH_SHORT).show()
        Glide.with(applicationContext)
            .load(Uri.parse(wallLink.getString("wallLink", "")))
            .into(
                object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?,
                    ) {
                        imageview1.setImageDrawable(resource)
                        val bitmapImg: Bitmap = (imageview1.drawable as BitmapDrawable).bitmap

                        val wallManager = WallpaperManager.getInstance(applicationContext)
                        try {
                            wallManager.clear()
                            wallManager.setBitmap(bitmapImg)
                        } catch (_: IOException) {
                        }
                        finish()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        loadDialogIntent.setClass(applicationContext, Setwall2Activity::class.java)
                        startActivity(loadDialogIntent)
                        loadDelay =
                            object : TimerTask() {
                                override fun run() {
                                    runOnUiThread {
                                        Log.e("WallpaperDebug", "Cannot load the wallpaper:${wallLink.getString("wallLink", "")}")
                                        finish()
                                    }
                                }
                            }
                        timer.schedule(loadDelay, config.getString("timeout", "")!!.toDouble().toLong())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                },
            )
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
