package com.ios7.vibeify.MyClasses

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View

object EzBlur {
    @JvmStatic
    fun setBlur(view: View?, radius: Float) {
        if (view == null) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect =
                RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
            view.setRenderEffect(blurEffect)
        }
    }

    @JvmStatic
    fun clearBlur(view: View?) {
        if (view == null) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        }
    }
}
