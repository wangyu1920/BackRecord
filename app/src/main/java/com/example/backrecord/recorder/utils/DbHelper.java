package com.example.backrecord.recorder.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;

import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: DbHelper.kt */
/* loaded from: classes5.dex */
public final class DbHelper {
    public static final DbHelper INSTANCE = new DbHelper();
    private static final String SELECTION = "bucket_display_name=?  AND  date_added >= ? ";
    private static final String[] projection;
    private static final Uri videoUri;

    private DbHelper() {
    }

    static {
        Uri uri;
        if (Build.VERSION.SDK_INT > 28) {
            uri = MediaStore.Video.Media.getContentUri("external_primary");
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        videoUri = uri;
        projection = new String[]{"_id", "_display_name", "width", "height", "_size", "date_added", "date_modified", "duration"};
    }

    @JvmStatic
    public static final Uri createFileUri(Context context, String displayName, VideoEncodeConfig video, String mimeType) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(displayName, "displayName");
        Intrinsics.checkNotNullParameter(video, "video");
        Intrinsics.checkNotNullParameter(mimeType, "mimeType");
        return createFileUri$default(context, displayName, video.getWidth(), video.getHeight(), mimeType, null, null, 96, null);
    }

    public static /* synthetic */ Uri createFileUri$default(Context context, String str, int i, int i2, String str2, Long l, Long l2, int i3, Object obj) {
        return createFileUri(context, str, i, i2, str2, (i3 & 32) != 0 ? null : l, (i3 & 64) != 0 ? null : l2);
    }

    @JvmStatic
    public static final Uri createFileUri(Context context, String displayName, int i, int i2, String mimeType, Long l, Long l2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(displayName, "displayName");
        Intrinsics.checkNotNullParameter(mimeType, "mimeType");
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", displayName);
        contentValues.put("mime_type", mimeType);
        contentValues.put("width", Integer.valueOf(i));
        contentValues.put("height", Integer.valueOf(i2));
        if (Build.VERSION.SDK_INT > 28) {
            contentValues.put("relative_path", Intrinsics.stringPlus(Environment.DIRECTORY_MOVIES, "/一键回录"));
            contentValues.put("is_pending", (Integer) 1);
        } else {
            String absolutePath = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), displayName).getAbsolutePath();
            Logger.d(Intrinsics.stringPlus("data=", absolutePath));
            contentValues.put("_data", absolutePath);
        }
        if (l != null) {
            contentValues.put("_size", Long.valueOf(l.longValue()));
        }
        if (l2 != null) {
            long longValue = l2.longValue();
            if (Build.VERSION.SDK_INT >= 29) {
                contentValues.put("duration", Long.valueOf(longValue));
            }
        }
        try {
            return context.getContentResolver().insert(videoUri, contentValues);
        } catch (Exception unused) {
            return null;
        }
    }

    @JvmStatic
    public static final void updatePendingZero(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        if (Build.VERSION.SDK_INT > 28) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("is_pending", (Integer) 0);
            context.getContentResolver().update(uri, contentValues, null, null);
        }
    }

    @JvmStatic
    public static final VideoItem query(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        long parseId = ContentUris.parseId(uri);
        ContentResolver contentResolver = context.getContentResolver();
        String[] strArr = {String.valueOf(parseId)};
        Uri videoUri2 = videoUri;
        Cursor query = contentResolver.query(videoUri2, projection, "_id=? ", strArr, null);
        if (query != null) {
            Cursor cursor = query;
            Throwable th = null;
            try {
                Cursor cursor2 = cursor;
                try {
                    if (cursor2.moveToFirst() && cursor2.getCount() == 1) {
                        VideoItem.Companion companion = VideoItem.Companion;
                        Intrinsics.checkNotNullExpressionValue(videoUri2, "videoUri");
                        VideoItem videoItem$default = VideoItem.Companion.toVideoItem$default(companion, cursor2, videoUri2, null, 4, null);
                        return videoItem$default;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Unit unit = Unit.INSTANCE;
            } finally {
            }
        }
        return null;
    }

    @JvmStatic
    public static final boolean existVideos(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        long enterGameTimeSeconds = RecorderHelper.getEnterGameTimeSeconds(context);
        Logger.d(Intrinsics.stringPlus(" ", Long.valueOf(enterGameTimeSeconds)));
        if (enterGameTimeSeconds == -1) {
            return false;
        }
        Cursor query = context.getContentResolver().query(videoUri, new String[]{"_id"}, SELECTION, INSTANCE.createQueryArgs(enterGameTimeSeconds), null);
        if (query != null) {
            Cursor cursor = query;
            Throwable th = null;
            try {
                Cursor cursor2 = cursor;
                if (cursor2.getCount() > 0) {
                    Logger.i(Intrinsics.stringPlus("Exist videos: ", Integer.valueOf(cursor2.getCount())));
                    return true;
                }
                Unit unit = Unit.INSTANCE;
            } finally {
            }
        }
        Logger.w("something wrong, no exist videos");
        return false;
    }

    public static /* synthetic */ ArrayList query$default(Context context, List list, Long l, int i, Object obj) {
        if ((i & 2) != 0) {
            list = null;
        }
        if ((i & 4) != 0) {
            l = null;
        }
        return query(context, list, l);
    }

    @JvmStatic
    public static final ArrayList<VideoItem> query(Context context, List<Long> list, Long l) {
        Intrinsics.checkNotNullParameter(context, "context");
        long enterGameTimeSeconds = RecorderHelper.getEnterGameTimeSeconds(context);
        if (enterGameTimeSeconds == -1) {
            return new ArrayList<>();
        }
        Logger.d(Intrinsics.stringPlus(" ", Long.valueOf(enterGameTimeSeconds)));
        ContentResolver contentResolver = context.getContentResolver();
        String str = SELECTION;

        String str2 = str;
        String[] createQueryArgs = INSTANCE.createQueryArgs(enterGameTimeSeconds);

        Uri videoUri2 = videoUri;
        Cursor query = contentResolver.query(videoUri2, projection, str2, createQueryArgs, "date_added DESC");
        if (query != null) {
            Cursor cursor = query;
            Throwable th = null;
            try {
                VideoItem.Companion companion = VideoItem.Companion;
                Intrinsics.checkNotNullExpressionValue(videoUri2, "videoUri");
                ArrayList<VideoItem> list2 = companion.toList(cursor, videoUri2, l);
                return list2;
            } finally {
            }
        } else {
            return new ArrayList<>();
        }
    }
    public static /* synthetic */ String replaceFirst$default(String str, char c, char c2, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        return StringsKt.replaceFirst(str, c, c2, z);
    }

    public static /* synthetic */ String joinToString$default(Object[] objArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
        }
        if ((i2 & 2) != 0) {
        }
        CharSequence charSequence5 = charSequence2;
        if ((i2 & 4) != 0) {
        }
        CharSequence charSequence6 = charSequence3;
        if ((i2 & 8) != 0) {
            i = -1;
        }
        int i3 = i;
        if ((i2 & 16) != 0) {
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return ArraysKt.joinToString(objArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }


    @JvmStatic
    public static final int delete(Context context, List<? extends Uri> uris) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uris, "uris");
        if (uris.isEmpty()) {
            return 0;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (uris.size() == 1) {
            return contentResolver.delete(uris.get(0), null, null);
        }
        List<? extends Uri> list = uris;
        ArrayList arrayList = new ArrayList(10);
        for (Uri uri : list) {
            arrayList.add(ContentProviderOperation.newDelete(uri).build());
        }
        ContentProviderResult[] applyBatch = new ContentProviderResult[0];
        try {
            applyBatch = contentResolver.applyBatch("media", new ArrayList<>(arrayList));
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Intrinsics.checkNotNullExpressionValue(applyBatch, "resolver.applyBatch(Medi…Y, ArrayList(operations))");
        int i = 0;
        for (ContentProviderResult contentProviderResult : applyBatch) {
            int i2 = contentProviderResult.count;
            i += i2;
        }
        return i;
    }

    @JvmStatic
    public static final int delete(Context context, Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        return delete(context, CollectionsKt.listOf(uri));
    }

    @JvmStatic
    public static final void deletePendingOne(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (Build.VERSION.SDK_INT > 28) {
            context.getContentResolver().delete(videoUri, " is_pending=1 ", null);
        }
    }

    private final String[] createQueryArgs(long j) {
        return new String[]{RecorderHelper.HIGHLIGHT_FOLDER, String.valueOf(j)};
    }
}