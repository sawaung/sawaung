package io.gonative.android.utils;

import android.util.Log;

import io.gonative.android.BuildConfig;


public class LogUtils {
    public static void Print(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.e(tag, "==========" + text);
    }
}