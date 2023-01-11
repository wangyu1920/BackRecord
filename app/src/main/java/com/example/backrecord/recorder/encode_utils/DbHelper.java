package com.example.backrecord.recorder.encode_utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/**
 * 用于产生文件Uri、更新文件、删除文件
 */
public final class DbHelper {
    public static final Uri videoUri = MediaStore.Video.Media.getContentUri("external_primary");
    public static final String VIDEO_FOLDER_PATH = "/storage/emulated/0/Movies/一键回录";

    private DbHelper() {
    }


    @JvmStatic
    public static Uri createFileUri(Context context, String displayName, VideoEncodeConfig video, String mimeType) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(displayName, "displayName");
        Intrinsics.checkNotNullParameter(video, "video");
        Intrinsics.checkNotNullParameter(mimeType, "mimeType");
        return createFileUri$default(context, displayName, video.getWidth(), video.getHeight(), mimeType, null, null, 96, null);
    }

    private static /* synthetic */ Uri createFileUri$default(Context context, String str, int i, int i2, String str2, Long l, Long l2, int i3, Object obj) {
        return createFileUri(context, str, i, i2, str2, (i3 & 32) != 0 ? null : l, (i3 & 64) != 0 ? null : l2);
    }

    @JvmStatic
    private static Uri createFileUri(Context context, String displayName, int i, int i2, String mimeType, Long l, Long l2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(displayName, "displayName");
        Intrinsics.checkNotNullParameter(mimeType, "mimeType");
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", displayName);
        contentValues.put("mime_type", mimeType);
        contentValues.put("width", i);
        contentValues.put("height", i2);
        contentValues.put("relative_path", Intrinsics.stringPlus(Environment.DIRECTORY_MOVIES, "/一键回录"));
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 1);
        if (l != null) {
            contentValues.put("_size", l);
        }
        if (l2 != null) {
            long longValue = l2;
            contentValues.put("duration", longValue);
        }
        try {
            return context.getContentResolver().insert(videoUri, contentValues);
        } catch (Exception unused) {
            return null;
        }
    }

    @JvmStatic
    public static void updatePendingZero(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_pending", 0);
        context.getContentResolver().update(uri, contentValues, null, null);
    }

    @JvmStatic
    public static void deletePendingOne(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null);
    }

}