package com.ios7.vibeify.MyClasses

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object EzPrefs {
    private const val PREF_NAME = "ezprefs"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun putString(context: Context, key: String, value: String) {
        val success = getPrefs(context).edit().putString(key, value).commit()
        Log.d("EzPrefs", "putString key=$key value=$value success=$success")
    }

    @JvmStatic
    fun getString(context: Context, key: String, defValue: String): String? {
        val result = getPrefs(context).getString(key, defValue)
        Log.d("EzPrefs", "getString key=$key result=$result")
        return result
    }

    @JvmStatic
    fun putInt(context: Context, key: String, value: Int) {
        getPrefs(context).edit().putInt(key, value).apply()
    }

    @JvmStatic
    fun getInt(context: Context, key: String, defValue: Int): Int {
        return getPrefs(context).getInt(key, defValue)
    }

    @JvmStatic
    fun putBoolean(context: Context, key: String, value: Boolean) {
        getPrefs(context).edit().putBoolean(key, value).apply()
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String, defValue: Boolean): Boolean {
        return getPrefs(context).getBoolean(key, defValue)
    }

    @JvmStatic
    fun remove(context: Context, key: String) {
        getPrefs(context).edit().remove(key).apply()
    }

    @JvmStatic
    fun clear(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
