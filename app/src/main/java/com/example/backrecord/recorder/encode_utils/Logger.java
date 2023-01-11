package com.example.backrecord.recorder.encode_utils;

import android.util.Log;

/**
 * 打印日志
 * 原本该类很复杂
 * 我对此类做了简化
 */
public final class Logger {
    static final String TAG = "Logger";
    private Logger() {
    }

   

    public static void v(String str) {
        Log.v(TAG,str);
    }
    

    public static void d(String str) {
        Log.d(TAG, str);
    }

    
    public static void i(String str) {
        Log.i(TAG, str);
    }

    
    public static void w(String str) {
        Log.w(TAG, str);
    }
    
    
    public static void e(String str) {
        Log.e(TAG, str);
    }
    
    
    public static void e(String str, Throwable th) {
        Log.e(TAG, str,th);
    }
}