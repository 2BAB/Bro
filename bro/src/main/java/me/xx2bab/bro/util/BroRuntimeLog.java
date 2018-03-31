package me.xx2bab.bro.util;

import android.util.Log;

import me.xx2bab.bro.Bro;

public class BroRuntimeLog {

    private static final String TAG = "== Bro ==";

    public static void i(String msg) {
        if (!Bro.getConfig().isLogEnabled()) {
            return;
        }
        Log.i(TAG, msg);
    }

    public static void e(String msg) {
        if (!Bro.getConfig().isLogEnabled()) {
            return;
        }
        Log.e(TAG, msg);
    }

}
