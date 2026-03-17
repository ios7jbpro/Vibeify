package com.ios7.vibeify.MyClasses;

import android.os.Handler;
import android.os.Looper;

public class EzTimer {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    // Method to run code with delay
    public static void runWithDelay(long delayMillis, Runnable action) {
        handler.postDelayed(action, delayMillis);
    }
}
