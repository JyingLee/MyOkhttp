package com.jying.myokhttp;

import android.util.Log;

/**
 * Created by Jying on 2018/1/15.
 */

public class L {
    private static final String TAG = "MyOkHttp";
    private static boolean debug = true;

    public static void e(String s) {
        if (debug) {
            Log.e(TAG, s);
        }
    }
}
