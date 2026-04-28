package com.ios7.vibeify

import android.app.WallpaperManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.ios7.vibeify.MyClasses.EzFade
import com.ios7.vibeify.databinding.ActivityCropWallpaperBinding
import java.io.IOException

class CropWallpaper : AppCompatActivity() {
    private lateinit var binding: ActivityCropWallpaperBinding
    private lateinit var cropImageView: com.canhub.cropper.CropImageView
    private lateinit var cropScreen: LinearLayout
    private lateinit var confirmScreen: LinearLayout
    private lateinit var textViewTopbar: TextView
    private lateinit var nextButton: TextView
    private lateinit var goBackButton: TextView
    private lateinit var setWallpaperButton: TextView
    private lateinit var cropPreview: ImageView
    private lateinit var imageview1: ImageView
    private lateinit var imageview3: ImageView
    private lateinit var textview2: TextView
    private lateinit var time2: TextView
    private lateinit var linear7: LinearLayout
    private lateinit var linear9: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCropWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cropImageView = findViewById(R.id.cropImageView)
        cropScreen = findViewById(R.id.cropScreen)
        cropScreen.visibility = View.GONE
        confirmScreen = findViewById(R.id.confirmScreen)
        textViewTopbar = findViewById(R.id.textViewTopbar)
        nextButton = findViewById(R.id.nextButton)
        nextButton.isEnabled = false
        nextButton.setBackgroundResource(R.color.backgroundviolent)
        nextButton.text = "Loading, wait..."
        goBackButton = findViewById(R.id.goBackButton)
        setWallpaperButton = findViewById(R.id.setWallpaperButton)
        cropPreview = findViewById(R.id.cropPreview)
        imageview1 = findViewById(R.id.imageview1)
        imageview3 = findViewById(R.id.imageview3)
        textview2 = findViewById(R.id.textview2)
        time2 = findViewById(R.id.time2)
        linear7 = findViewById(R.id.linear7)
        linear9 = findViewById(R.id.linear9)
        confirmScreen.visibility = View.GONE
        goBackButton.visibility = View.GONE
        setWallpaperButton.visibility = View.GONE
        linear7.clipToOutline = true
        linear9.clipToOutline = true

        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.warning_dialog, null)
        bottomSheet.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheet.setContentView(bottomSheetView)
        val dismissButton = bottomSheetView.findViewById<MaterialButton>(R.id.dissmissButton)
        dismissButton.setOnClickListener { bottomSheet.dismiss() }

        nextButton.setOnClickListener {
            EzFade.crossfade(cropScreen, confirmScreen, 500)
            cropScreen.visibility = View.GONE
            confirmScreen.visibility = View.VISIBLE
            goBackButton.visibility = View.VISIBLE
            setWallpaperButton.visibility = View.VISIBLE
            nextButton.visibility = View.GONE
            textViewTopbar.text = "Preview"
            cropPreview.setImageBitmap(cropImageView.croppedImage)
            cropPreview.visibility = View.GONE
            imageview1.setImageBitmap(cropImageView.croppedImage)
            imageview3.setImageBitmap(cropImageView.croppedImage)
            val bitmap = (imageview1.drawable as BitmapDrawable).bitmap
            Palette.from(bitmap).generate { _ -> }
        }

        goBackButton.setOnClickListener {
            cropScreen.visibility = View.VISIBLE
            confirmScreen.visibility = View.GONE
            goBackButton.visibility = View.GONE
            setWallpaperButton.visibility = View.GONE
            nextButton.visibility = View.VISIBLE
        }

        setWallpaperButton.setOnClickListener {
            val wallManager = WallpaperManager.getInstance(applicationContext)
            try {
                Toast.makeText(applicationContext, "Loading in cropped image and setting wallpaper...", Toast.LENGTH_SHORT).show()
                wallManager.clear()
                wallManager.setBitmap(cropImageView.croppedImage)
            } catch (ex: IOException) {
                ex.printStackTrace()
                Toast.makeText(applicationContext, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
            }
        }

        val link = intent.getParcelableExtra<Uri>("link")
        try {
            Glide.with(applicationContext)
                .asBitmap()
                .load(link)
                .into(
                    object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?,
                        ) {
                            try {
                                nextButton.isEnabled = true
                                nextButton.setBackgroundResource(R.drawable.activetab)
                                nextButton.text = "Next"
                                cropImageView.setImageBitmap(resource)
                                EzFade.fadeIn(cropScreen, 500)
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                                val clipboard =
                                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Error:", e.message)
                                clipboard.setPrimaryClip(clip)
                            }
                        }

                        override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
                    },
                )
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}
