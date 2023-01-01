package com.example.backrecord.recorder.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import kotlin.jvm.internal.Intrinsics;

/**
 * 创建ScreenRecorder实例时的参数之一
 * 用于指定录制生成的文件的保存地址
 */
public final class RecordPath {
    private final Context context;
    private final Uri uri;

    /**
     * 简单的构造方法
     * @param context 上下文，用于获取设备音频文件夹
     * @param virtualDisplayName VirtualDisplay的name属性
     * @param videoEncodeConfig 视频编码配配置类的实例
     * @return RecordPath实例
     */
    public RecordPath(Context context, String virtualDisplayName, VideoEncodeConfig videoEncodeConfig) {
        uri = createFileUri(context, virtualDisplayName, videoEncodeConfig);
        this.context = context;
    }

    public static Uri createFileUri(Context context, String str, int i, int i2, String str2, Long l, Long l2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(str, "displayName");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", str);
        contentValues.put("mime_type", str2);
        contentValues.put("width", i);
        contentValues.put("height", i2);
        contentValues.put("relative_path", Intrinsics.stringPlus(Environment.DIRECTORY_MOVIES, "/一键回录"));
        contentValues.put("is_pending", String.valueOf(1));
        if (l != null) {
            contentValues.put("_size", l);
        }
        if (l2 != null) {
            long longValue = l2;
            contentValues.put("duration", longValue);
        }
        try {
            return context.getContentResolver().insert(MediaStore.Video.Media.getContentUri("external_primary"), contentValues);
        } catch (Exception unused) {

            return null;
        }
    }
    public static Uri createFileUri(Context context, String str, VideoEncodeConfig videoEncodeConfig, String str2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(str, "displayName");
        Intrinsics.checkNotNullParameter(videoEncodeConfig, "video");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
        return createFileUri$default(context, str, videoEncodeConfig.getWidth(), videoEncodeConfig.getHeight(), str2, null, null, 96, null);
    }

    /**
     * 主要的生成方法
     * @param context 上下文，用于获取设备音频文件夹
     * @param displayName VirtualDisplay的name属性
     * @param videoEncodeConfig 视频编码配配置类的实例
     * @return Uri，用于生成RecordPath
     */
    public static Uri createFileUri(Context context, String displayName, VideoEncodeConfig videoEncodeConfig) {
        return createFileUri(context, displayName, videoEncodeConfig, "video/mp4");
    }

    public static /* synthetic */ Uri createFileUri$default(Context context, String str, int i, int i2, String str2, Long l, Long l2, int i3, Object obj) {
        return createFileUri(context, str, i, i2, str2, (i3 & 32) != 0 ? null : l, (i3 & 64) != 0 ? null : l2);
    }




    public static /* synthetic */ RecordPath copy$default(RecordPath recordPath, Context context, Uri uri, int i, Object obj) {
        if ((i & 1) != 0) {
            context = recordPath.context;
        }
        if ((i & 2) != 0) {
            uri = recordPath.uri;
        }
        return recordPath.copy(context, uri);
    }

    public final Context component1() {
        return this.context;
    }

    public final Uri component2() {
        return this.uri;
    }

    public final RecordPath copy(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        return new RecordPath(context, uri);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RecordPath) {
            RecordPath recordPath = (RecordPath) obj;
            return Intrinsics.areEqual(this.context, recordPath.context) && Intrinsics.areEqual(this.uri, recordPath.uri);
        }
        return false;
    }

    public int hashCode() {
        return (this.context.hashCode() * 31) + this.uri.hashCode();
    }

    public String toString() {
        return "RecordPath(context=" + this.context + ", uri=" + this.uri + ')';
    }

    public RecordPath(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        this.context = context;
        this.uri = uri;
    }

    public final Context getContext() {
        return this.context;
    }

    public final Uri getUri() {
        return this.uri;
    }

    public final FileDescriptor getFd() {
        FileDescriptor fileDescriptor;
        ParcelFileDescriptor openFileDescriptor = null;
        try {
            openFileDescriptor = this.context.getContentResolver().openFileDescriptor(this.uri, "w");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (openFileDescriptor == null || (fileDescriptor = openFileDescriptor.getFileDescriptor()) == null || !fileDescriptor.valid()) {
            return null;
        }
        return fileDescriptor;
    }
}