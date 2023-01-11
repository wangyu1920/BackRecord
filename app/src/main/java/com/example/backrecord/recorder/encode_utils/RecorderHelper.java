package com.example.backrecord.recorder.encode_utils;

import android.content.Context;
import android.content.SharedPreferences;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/**
 * 录制帮助类
 * 第一个方法用了反射不知道干啥的
 * 第二个方法用于获取SharedPreferences，给Audio/VideoEncoderConfig类的Companion，用于读取/写入参数
 */
public final class RecorderHelper {
    public static final long DOUBLE_UP_DURATION = 1000;
    public static final String HIGHLIGHT_FOLDER = "一键回录";
    public static final RecorderHelper INSTANCE = new RecorderHelper();
    private static final String KEY_ENTER_GAME_TIME = "enter_game_time";
    private static final String KEY_EXIT_GAME_TIME = "exit_game_time";
    private static final String KEY_HL_BOOT_UP = "hl_boot_up";
    private static final String LOOPBACKFLAG = "LoopBackFlag=";
    public static final String MIMETYPE_VIDEO_MP4 = "video/mp4";

    private RecorderHelper() {
    }

    @JvmStatic
    public static final void setLoopBackFlag(boolean z) {
        try {
            Class<?> cls = Class.forName("android.media.AudioSystem");
            cls.getMethod("setParameters", String.class).invoke(cls, Intrinsics.stringPlus(LOOPBACKFLAG, Boolean.valueOf(z)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JvmStatic
    public static final SharedPreferences getConfigPreferences(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        SharedPreferences sharedPreferences = context.getSharedPreferences("screen_recorder_config", 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "context.getSharedPrefere…g\", Context.MODE_PRIVATE)");
        return sharedPreferences;
    }

    public static long getEnterGameTimeSeconds(Context context) {
        return 180000;
    }
}