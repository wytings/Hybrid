package com.wytings.hybrid.utils;

import android.util.Log;

/**
 * Created by rex on 12/29/16.
 */

public class Logs {

    private static final String TAG = "hybrid";

    public static void w(Object msg) {
        Log.w(TAG, getMessage(msg));
    }

    public static void i(Object msg) {
        Log.i(TAG, getMessage(msg));
    }

    public static void e(Object msg) {
        Log.e(TAG, getMessage(msg));
    }

    private static String getMessage(Object msg) {
        return msg == null ? "null" : msg.toString();
    }
}
