package com.example.backrecord.recorder.encode_utils;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

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

    public RecordPath(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        this.context = context;
        this.uri = uri;
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