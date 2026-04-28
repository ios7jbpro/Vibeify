package com.ios7.vibeify.MyClasses

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class EzIntent(context: Context, cls: Class<*>) {
    private val intent = Intent(context, cls)

    fun setAction(action: String): EzIntent {
        intent.action = action
        return this
    }

    fun setFlags(flags: Int): EzIntent {
        intent.flags = flags
        return this
    }

    fun addFlag(flag: Int): EzIntent {
        intent.addFlags(flag)
        return this
    }

    fun putExtra(name: String, value: Boolean): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: Byte): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: Short): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: Int): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: Long): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: Float): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: Double): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtra(name: String, value: String): EzIntent {
        intent.putExtra(name, value)
        return this
    }

    fun putExtras(extras: Bundle): EzIntent {
        intent.putExtras(extras)
        return this
    }

    fun startActivity(context: Context) {
        context.startActivity(intent)
    }

    fun startActivityForResult(context: Context, requestCode: Int) {
        if (context is Activity) {
            context.startActivityForResult(intent, requestCode)
            return
        }
        throw IllegalArgumentException("Context must be an instance of Activity.")
    }

    fun getIntent(): Intent = intent
}
