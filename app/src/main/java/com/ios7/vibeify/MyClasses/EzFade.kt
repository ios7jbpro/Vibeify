package com.ios7.vibeify.MyClasses

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

object EzFade {
    private const val DEFAULT_DURATION = 300L

    @JvmStatic
    fun fadeIn(view: View?) {
        fadeIn(view, DEFAULT_DURATION)
    }

    @JvmStatic
    fun fadeOut(view: View?) {
        fadeOut(view, DEFAULT_DURATION)
    }

    @JvmStatic
    fun fadeIn(view: View?, duration: Long) {
        if (view == null) {
            return
        }

        view.visibility = View.VISIBLE
        val animation = AlphaAnimation(0f, 1f).apply {
            this.duration = duration
            fillAfter = true
        }
        view.alpha = 1f
        view.startAnimation(animation)
        view.bringToFront()
    }

    @JvmStatic
    fun fadeOut(view: View?, duration: Long) {
        if (view == null) {
            return
        }

        val animation = AlphaAnimation(1f, 0f).apply {
            this.duration = duration
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = View.GONE
                }
            })
        }
        view.alpha = 1f
        view.startAnimation(animation)
        view.bringToFront()
    }

    @JvmStatic
    fun crossfade(fromView: View?, toView: View?) {
        crossfade(fromView, toView, DEFAULT_DURATION)
    }

    @JvmStatic
    fun crossfade(fromView: View?, toView: View?, duration: Long) {
        if (fromView == null || toView == null) {
            return
        }

        val fadeOut = AlphaAnimation(1f, 0f).apply {
            this.duration = duration
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    fromView.visibility = View.GONE
                }
            })
        }

        toView.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            this.duration = duration
            fillAfter = true
        }

        fromView.startAnimation(fadeOut)
        toView.alpha = 1f
        toView.startAnimation(fadeIn)
        toView.bringToFront()
    }
}
