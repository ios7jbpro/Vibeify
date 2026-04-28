package com.ios7.vibeify

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ios7.vibeify.MyClasses.EzFade
import com.ios7.vibeify.MyClasses.EzTimer
import com.ios7.vibeify.MyClasses.EzTimerLooped
import java.io.File
import java.io.IOException
import java.util.Random

class WalldownloadActivity : AppCompatActivity() {
    private var walljsonlistmap = ArrayList<HashMap<String, Any>>()

    private lateinit var linear1: LinearLayout
    private lateinit var linearloadhires: LinearLayout
    private lateinit var linear4: LinearLayout
    private lateinit var textview1: TextView
    private lateinit var textview4: TextView
    private lateinit var linear7: LinearLayout
    private lateinit var linear9: LinearLayout
    private lateinit var textView3: TextView
    private lateinit var textview2: TextView
    private lateinit var imageview1: ImageView
    private lateinit var imageview3: ImageView
    private lateinit var button1: TextView
    private lateinit var button2: TextView
    private lateinit var button3: TextView
    private lateinit var button4: TextView
    private lateinit var textViewLoading: TextView
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var color1: LinearLayout
    private lateinit var color2: LinearLayout
    private lateinit var color3: LinearLayout
    private lateinit var color4: LinearLayout
    private lateinit var color5: LinearLayout
    private lateinit var color6: LinearLayout
    private lateinit var textView5: TextView
    private lateinit var colorpreviews: LinearLayout
    private lateinit var colorpreviewsloading: LinearLayout
    private lateinit var textViewCrop: TextView
    private lateinit var linearpreviewcard: LinearLayout
    private lateinit var linearpfppreview: LinearLayout
    private lateinit var imageviewpfp: ImageView
    private lateinit var linearanotherpfpclipper: LinearLayout
    private lateinit var imageviewpfp3: ImageView
    private lateinit var imageviewreactionpfp: ImageView
    private lateinit var reactionpfpclipper: LinearLayout
    private lateinit var linearprereact: LinearLayout

    private lateinit var selectedItemList: SharedPreferences
    private lateinit var fetchJson: RequestNetwork
    private var fetchJsonRequestListener: RequestNetwork.RequestListener? = null
    private lateinit var wallLink: SharedPreferences
    private lateinit var config: SharedPreferences
    private lateinit var temporaryCache: SharedPreferences
    private var allhexvals = ""
    private var isPfp = false
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.walldownload)
        initialize(savedInstanceState)
        initializeLogic()
    }

    private fun initialize(savedInstanceState: Bundle?) {
        linear1 = findViewById(R.id.linear1)
        linearloadhires = findViewById(R.id.linearloadhires)
        linear4 = findViewById(R.id.linear4)
        textview1 = findViewById(R.id.textview1)
        textview4 = findViewById(R.id.textview4)
        linear7 = findViewById(R.id.linear7)
        linear9 = findViewById(R.id.linear9)
        textView3 = findViewById(R.id.textView3)
        textview2 = findViewById(R.id.textview2)
        textViewLoading = findViewById(R.id.textViewLoading)
        progressBarLoading = findViewById(R.id.progress_bar_loading)
        imageview1 = findViewById(R.id.imageview1)
        imageview3 = findViewById(R.id.imageview3)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        color1 = findViewById(R.id.color1)
        color2 = findViewById(R.id.color2)
        color3 = findViewById(R.id.color3)
        color4 = findViewById(R.id.color4)
        color5 = findViewById(R.id.color5)
        color6 = findViewById(R.id.color6)
        textView5 = findViewById(R.id.textView5)
        colorpreviews = findViewById(R.id.colorpreviews)
        textViewCrop = findViewById(R.id.textViewCrop)
        colorpreviewsloading = findViewById(R.id.colorpreviewsloading)
        linearpreviewcard = findViewById(R.id.linearpreviewcard)
        linearpfppreview = findViewById(R.id.linearpfppreview)
        imageviewpfp = findViewById(R.id.imageviewpfp)
        linearanotherpfpclipper = findViewById(R.id.linearanotherpfpclipper)
        imageviewpfp3 = findViewById(R.id.imageviewpfp3)
        imageviewreactionpfp = findViewById(R.id.imageviewreactionpfp)
        reactionpfpclipper = findViewById(R.id.reactionpfpclipper)
        linearprereact = findViewById(R.id.linearprereact)
        linearpfppreview.visibility = View.GONE

        selectedItemList = getSharedPreferences("selectedItemList", Activity.MODE_PRIVATE)
        fetchJson = RequestNetwork(this)
        wallLink = getSharedPreferences("wallLink", Activity.MODE_PRIVATE)
        config = getSharedPreferences("config", Activity.MODE_PRIVATE)
        temporaryCache = getSharedPreferences("temporaryCache", Activity.MODE_PRIVATE)

        textViewCrop.setOnClickListener {
            val intentCrop = Intent(applicationContext, CropWallpaper::class.java)
            intentCrop.putExtra(
                "link",
                Uri.parse(
                    config.getString("repo", "") +
                        walljsonlistmap[selectedItemList.getString("selectedWall", "0")!!.toInt()]["link"].toString(),
                ),
            )
            startActivity(intentCrop)
        }

        button1.setOnClickListener {
            val intentDownloadRemoteWall = Intent(Intent.ACTION_VIEW)
            intentDownloadRemoteWall.data =
                Uri.parse(
                    config.getString("repo", "") +
                        walljsonlistmap[selectedItemList.getString("selectedWall", "0")!!.toInt()]["link"].toString(),
                )
            startActivity(intentDownloadRemoteWall)
        }

        button1.setOnLongClickListener {
            if (config.getString("debugMode", "") == "1") {
                ClipboardUtils.copyTextToClipboard(applicationContext, config.getString("repo", "") + wallLink.getString("wallLink", ""))
                true
            } else {
                false
            }
        }

        button2.setOnClickListener {
            showLoadingDialog()
            Glide.with(applicationContext)
                .asBitmap()
                .load(Uri.parse(config.getString("repo", "") + wallLink.getString("wallLink", "")))
                .into(
                    object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Thread {
                                val wallManager = WallpaperManager.getInstance(applicationContext)
                                try {
                                    wallManager.clear()
                                    wallManager.setBitmap(resource)
                                    runOnUiThread {
                                        hideLoadingDialog()
                                        Toast.makeText(applicationContext, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (ex: IOException) {
                                    ex.printStackTrace()
                                    runOnUiThread {
                                        hideLoadingDialog()
                                        Toast.makeText(applicationContext, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }.start()
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            hideLoadingDialog()
                            Toast.makeText(applicationContext, "Failed to load image", Toast.LENGTH_SHORT).show()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    },
                )
        }

        button2.setOnLongClickListener {
            if (config.getString("debugMode", "") == "1") {
                ClipboardUtils.copyTextToClipboard(applicationContext, allhexvals)
                true
            } else {
                Toast.makeText(applicationContext, "Using the legacy wallpaper loader", Toast.LENGTH_SHORT).show()
                wallLink.edit()
                    .putString(
                        "wallLink",
                        config.getString("repo", "") +
                            walljsonlistmap[selectedItemList.getString("selectedWall", "0")!!.toInt()]["link"].toString(),
                    ).apply()
                startActivity(Intent(applicationContext, Setwall1Activity::class.java))
                true
            }
        }

        button3.setOnClickListener {
            ShareCompat.IntentBuilder
                .from(this)
                .setText(config.getString("repo", "") + intent.getStringExtra("wallpaperLink"))
                .setType("text/plain")
                .setChooserTitle("Share URL with")
                .startChooser()
        }

        button4.setOnClickListener { view ->
            val imageUrl = config.getString("repo", "") + intent.getStringExtra("wallpaperLink")
            EzFade.crossfade(linear1, linearloadhires)
            Glide.with(view.context)
                .asFile()
                .load(imageUrl)
                .into(
                    object : CustomTarget<File>() {
                        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                            EzFade.crossfade(linearloadhires, linear1)
                            val contentUri =
                                FileProvider.getUriForFile(
                                    view.context,
                                    "com.ios7.vibeify.fileprovider",
                                    resource,
                                )

                            val shareIntent =
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "image/jpeg"
                                    putExtra(Intent.EXTRA_STREAM, contentUri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            view.context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    },
                )
        }

        fetchJsonRequestListener =
            object : RequestNetwork.RequestListener {
                override fun onResponse(
                    tag: String,
                    response: String,
                    responseHeaders: HashMap<String, Any>,
                ) {
                    walljsonlistmap =
                        Gson().fromJson(
                            response,
                            object : TypeToken<ArrayList<HashMap<String, Any>>>() {}.type,
                        )
                    loadPreview()
                }

                override fun onErrorResponse(tag: String, message: String) {}
            }
    }

    private fun loadPreview() {
        try {
            Glide.with(applicationContext)
                .load(
                    Uri.parse(
                        config.getString("repo", "") +
                            walljsonlistmap[selectedItemList.getString("selectedWall", "0")!!.toInt()]["lowprew"].toString(),
                    ),
                ).into(
                    object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            imageview1.setImageDrawable(resource)
                            imageview3.setImageDrawable(resource)
                            imageviewpfp.setImageDrawable(resource)
                            imageviewpfp3.setImageDrawable(resource)
                            imageviewreactionpfp.setImageDrawable(resource)
                            linearanotherpfpclipper.clipToOutline = true
                            reactionpfpclipper.clipToOutline = true
                            linearprereact.clipToOutline = true
                            extractPalette()
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            showColorExtractionFailed()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    },
                )
        } catch (e: Exception) {
            Log.e("WallpaperDebug", "Exception: ${e.message}")
        }

        try {
            textview1.text =
                walljsonlistmap[selectedItemList.getString("selectedWall", "0")!!.toInt()]["name"].toString()
            var original = textview1.text.toString()
            isPfp = false
            if (original.startsWith("fpfp.")) {
                isPfp = true
                linearpfppreview.visibility = View.VISIBLE
                button2.visibility = View.GONE
                textViewCrop.visibility = View.GONE
                original = original.substring("fpfp.".length)
                textview1.text = original
            }
        } catch (e: Exception) {
            Log.e("WallpaperDebug", "Exception: ${e.message}")
        }

        if (config.getString("wallpaperName", "").isNullOrEmpty()) {
            textview1.text = config.getString("categoryName", "")
        }

        wallLink.edit()
            .putString(
                "wallLink",
                walljsonlistmap[selectedItemList.getString("selectedWall", "0")!!.toInt()]["link"].toString(),
            ).apply()

        if (isPfp) {
            linearpreviewcard.visibility = View.GONE
            textView3.visibility = View.GONE
            EzTimer.runWithDelay(150) {
                colorpreviews.visibility = View.GONE
            }
            val loopedTimer = EzTimerLooped()
            loopedTimer.start(100) {
                colorpreviews.visibility = View.GONE
            }
            colorpreviewsloading.visibility = View.GONE
        }
    }

    private fun extractPalette() {
        val drawable = imageview1.drawable
        if (drawable !is BitmapDrawable) {
            showColorExtractionFailed()
            return
        }
        val bitmap = drawable.bitmap
        if (bitmap == null || bitmap.width == 0 || bitmap.height == 0) {
            showColorExtractionFailed()
            return
        }

        Palette.from(bitmap).generate { palette ->
            val safePalette = palette ?: run {
                showColorExtractionFailed()
                return@generate
            }
            val defaultColor = Color.DKGRAY
            val vibrant = safePalette.getDominantColor(defaultColor)
            val muted = safePalette.getMutedColor(defaultColor)
            val vibrantLight = safePalette.getLightVibrantColor(defaultColor)
            val vibrantDark = safePalette.getDarkVibrantColor(defaultColor)
            val mutedLight = safePalette.getLightMutedColor(defaultColor)
            val mutedDark = safePalette.getDarkMutedColor(defaultColor)

            if (config.getString("debugMode", "") == "1") {
                val vibrantHex = String.format("#%08X", vibrant)
                val mutedHex = String.format("#%08X", muted)
                val vibrantLightHex = String.format("#%08X", vibrantLight)
                val vibrantDarkHex = String.format("#%08X", vibrantDark)
                val mutedLightHex = String.format("#%08X", mutedLight)
                val mutedDarkHex = String.format("#%08X", mutedDark)
                allhexvals =
                    "INTERNAL:$vibrant $muted $mutedDark $mutedLight $vibrantLight $vibrantDark\nHEX:$vibrantHex $mutedHex $mutedDarkHex $mutedLightHex $vibrantLightHex $vibrantDarkHex"
                textview4.text = "$allhexvals\n*VALUE AUTOMATICALLY COPIED TO CLIPBOARD*"
                textview4.textSize = 8f
                ClipboardUtils.copyTextToClipboard(applicationContext, allhexvals)
                button1.text = "Download\nHOLD FOR URL"
                button2.text = "Set wallpaper\nHOLD FOR HEX"
            }

            val colors = intArrayOf(vibrant, muted, mutedDark, mutedLight, vibrantLight, vibrantDark)
            for (color in colors) {
                if (Color.alpha(color) == 0) {
                    showColorExtractionFailed()
                    return@generate
                }
            }

            color1.setBackgroundColor(vibrant)
            color2.setBackgroundColor(muted)
            color3.setBackgroundColor(mutedDark)
            color4.setBackgroundColor(mutedLight)
            color5.setBackgroundColor(vibrantLight)
            color6.setBackgroundColor(vibrantDark)
            textview2.setTextColor(vibrantLight)
            colorpreviews.visibility = View.VISIBLE
            colorpreviewsloading.visibility = View.GONE
            textView5.visibility = View.GONE

            setupColorClick(color1, vibrant)
            setupColorClick(color2, muted)
            setupColorClick(color3, mutedDark)
            setupColorClick(color4, mutedLight)
            setupColorClick(color5, vibrantLight)
            setupColorClick(color6, vibrantDark)

            if (config.getString("colorextraction", "") == "0") {
                colorpreviews.visibility = View.GONE
                colorpreviewsloading.visibility = View.GONE
                textView3.visibility = View.GONE
                textview2.setTextColor(Color.WHITE)
            }
        }
    }

    private fun setupColorClick(colorView: View, color: Int) {
        colorView.setOnClickListener {
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            ClipboardUtils.copyTextToClipboard(applicationContext, hexColor)
        }
    }

    private fun showColorExtractionFailed() {
        color1.visibility = View.GONE
        color2.visibility = View.GONE
        color3.visibility = View.GONE
        color4.visibility = View.GONE
        color5.visibility = View.GONE
        color6.visibility = View.GONE
        textView5.visibility = View.VISIBLE
        textview2.setTextColor(Color.WHITE)
        colorpreviews.visibility = View.GONE
        colorpreviewsloading.visibility = View.GONE
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.setwall2, null)
            builder.setView(view)
            builder.setCancelable(false)
            loadingDialog = builder.create()
            loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }

    private fun initializeLogic() {
        ViewCompat.setOnApplyWindowInsetsListener(linear1) { view, windowInsets ->
            val insets: Insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        fetchJson.startRequestNetwork(
            RequestNetworkController.GET,
            config.getString("repo", "") + temporaryCache.getString("directrepo", ""),
            "",
            fetchJsonRequestListener,
        )
        linear7.clipToOutline = true
        linear9.clipToOutline = true
        textViewCrop.clipToOutline = true
        colorpreviews.visibility = View.GONE

        if (config.getString("wallTutComplete", "") != "1") {
            EzTimer.runWithDelay(1000) {
                if (!isPfp) {
                    TapTargetSequence(this)
                        .targets(
                            tutorialTarget(button3, "Sharing", "You can now share images! Give it a try(this won't be shown again)"),
                            tutorialTarget(button4, "Images", "You also can share images! Click the button, give it a few seconds to download"),
                            tutorialTarget(button2, "Set a wallpaper", "Give it a try yourself! Set this very image as your wallpaper!"),
                        ).listener(
                            object : TapTargetSequence.Listener {
                                override fun onSequenceFinish() {
                                    config.edit().putString("wallTutComplete", "1").commit()
                                }

                                override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {}

                                override fun onSequenceCanceled(lastTarget: TapTarget) {
                                    config.edit().putString("wallTutComplete", "1").commit()
                                    Snackbar.make(linear4, "Tutorial cancelled", Snackbar.LENGTH_SHORT)
                                        .setAction("Dismiss") {
                                            config.edit().putString("wallTutComplete", "1").commit()
                                        }.show()
                                }
                            },
                        ).start()
                }
            }
        }
    }

    private fun tutorialTarget(view: View, title: String, description: String): TapTarget {
        return TapTarget.forView(view, title, description)
            .outerCircleColor(R.color.backgroundviolent)
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
            .targetRadius(60)
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
