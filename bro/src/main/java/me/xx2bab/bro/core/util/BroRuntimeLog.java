package me.xx2bab.bro.core.util;

import android.util.Log;

public class BroRuntimeLog {

    private static final String TAG = "[Bro] ";
    public static boolean logEnabled = true;

    public static void i(String msg) {
        if (logEnabled) {
            return;
        }
        Log.i(TAG, msg);
    }

    public static void e(String msg) {
        if (logEnabled) {
            return;
        }
        Log.e(TAG, msg);
    }

}
