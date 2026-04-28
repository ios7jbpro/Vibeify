package com.ios7.vibeify

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.SparseBooleanArray
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.Collections
import java.util.Comparator
import java.util.Random

object SketchwareUtil {
    const val TOP = 1
    const val CENTER = 2
    const val BOTTOM = 3

    @JvmStatic
    fun CustomToast(
        context: Context,
        message: String,
        textColor: Int,
        textSize: Int,
        bgColor: Int,
        radius: Int,
        gravity: Int,
    ) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        val view = toast.view ?: return
        val textView = view.findViewById<TextView>(android.R.id.message) ?: return
        textView.textSize = textSize.toFloat()
        textView.setTextColor(textColor)
        textView.gravity = Gravity.CENTER

        val gradientDrawable =
            GradientDrawable().apply {
                setColor(bgColor)
                cornerRadius = radius.toFloat()
            }
        view.background = gradientDrawable
        view.setPadding(15, 10, 15, 10)
        view.elevation = 10f

        when (gravity) {
            TOP -> toast.setGravity(Gravity.TOP, 0, 150)
            CENTER -> toast.setGravity(Gravity.CENTER, 0, 0)
            BOTTOM -> toast.setGravity(Gravity.BOTTOM, 0, 150)
        }
        toast.show()
    }

    @JvmStatic
    fun CustomToastWithIcon(
        context: Context,
        message: String,
        textColor: Int,
        textSize: Int,
        bgColor: Int,
        radius: Int,
        gravity: Int,
        icon: Int,
    ) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        val view = toast.view ?: return
        val textView = view.findViewById<TextView>(android.R.id.message) ?: return
        textView.textSize = textSize.toFloat()
        textView.setTextColor(textColor)
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        textView.gravity = Gravity.CENTER
        textView.compoundDrawablePadding = 10

        val gradientDrawable =
            GradientDrawable().apply {
                setColor(bgColor)
                cornerRadius = radius.toFloat()
            }
        view.background = gradientDrawable
        view.setPadding(10, 10, 10, 10)
        view.elevation = 10f

        when (gravity) {
            TOP -> toast.setGravity(Gravity.TOP, 0, 150)
            CENTER -> toast.setGravity(Gravity.CENTER, 0, 0)
            BOTTOM -> toast.setGravity(Gravity.BOTTOM, 0, 150)
        }
        toast.show()
    }

    @JvmStatic
    fun sortListMap(
        listMap: ArrayList<HashMap<String, Any>>,
        key: String,
        isNumber: Boolean,
        ascending: Boolean,
    ) {
        Collections.sort(
            listMap,
            Comparator { map1, map2 ->
                if (isNumber) {
                    val count1 = map1[key].toString().toInt()
                    val count2 = map2[key].toString().toInt()
                    if (ascending) {
                        count1.compareTo(count2)
                    } else {
                        count2.compareTo(count1)
                    }
                } else if (ascending) {
                    map1[key].toString().compareTo(map2[key].toString())
                } else {
                    map2[key].toString().compareTo(map1[key].toString())
                }
            },
        )
    }

    @JvmStatic
    fun CropImage(activity: Activity, path: String, requestCode: Int) {
        try {
            val intent = Intent("com.android.camera.action.CROP")
            val file = File(path)
            val contentUri = Uri.fromFile(file)
            intent.setDataAndType(contentUri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("outputX", 280)
            intent.putExtra("outputY", 280)
            intent.putExtra("return-data", false)
            activity.startActivityForResult(intent, requestCode)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(activity, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    @JvmStatic
    fun copyFromInputStream(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        try {
            while (true) {
                val count = inputStream.read(buffer)
                if (count == -1) {
                    break
                }
                outputStream.write(buffer, 0, count)
            }
            outputStream.close()
            inputStream.close()
        } catch (_: IOException) {
        }
        return outputStream.toString()
    }

    @JvmStatic
    fun hideKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    @JvmStatic
    fun showKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    @JvmStatic
    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun getLocationX(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[0]
    }

    @JvmStatic
    fun getLocationY(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[1]
    }

    @JvmStatic
    fun getRandom(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }

    @JvmStatic
    fun getCheckedItemPositionsToArray(list: ListView): ArrayList<Double> {
        val result = ArrayList<Double>()
        val checked = list.checkedItemPositions
        for (index in 0 until checked.size()) {
            if (checked.valueAt(index)) {
                result.add(checked.keyAt(index).toDouble())
            }
        }
        return result
    }

    @JvmStatic
    fun getDip(context: Context, input: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            input.toFloat(),
            context.resources.displayMetrics,
        )
    }

    @JvmStatic
    fun getDisplayWidthPixels(context: Context): Int = context.resources.displayMetrics.widthPixels

    @JvmStatic
    fun getDisplayHeightPixels(context: Context): Int = context.resources.displayMetrics.heightPixels

    @JvmStatic
    fun getAllKeysFromMap(map: Map<String, Any>?, output: ArrayList<String>?) {
        if (output == null) {
            return
        }
        output.clear()
        if (map.isNullOrEmpty()) {
            return
        }
        for ((key, _) in map) {
            output.add(key)
        }
    }
}
