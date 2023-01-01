package com.example.backrecord.recorder.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: VideoItem.kt */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public class VideoItem implements Parcelable {
    private boolean checked;
    private long coverId;
    private final long dateAdded;
    private final long dateModified;
    private final long duration;
    private final int height;
    private boolean highlight;
    private final long id;
    private final String name;
    private final long size;
    private final Uri uri;
    private final int width;
    public static final Companion Companion = new Companion(null);
    public static final Parcelable.Creator<VideoItem> CREATOR = new Parcelable.Creator<VideoItem>() { // from class: com.zui.game.service.highlight.model.VideoItem$Companion$CREATOR$1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VideoItem createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "source");
            return new VideoItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VideoItem[] newArray(int i) {
            return new VideoItem[i];
        }
    };
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @JvmStatic
    public static final String formatDate(long j) {
        return Companion.formatDate(j);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public VideoItem(long j, String str, Uri uri, int i, int i2, long j2, long j3, long j4, long j5, boolean z, boolean z2, long j6) {
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.checkNotNullParameter(uri, "uri");
        this.id = j;
        this.name = str;
        this.uri = uri;
        this.width = i;
        this.height = i2;
        this.size = j2;
        this.dateAdded = j3;
        this.dateModified = j4;
        this.duration = j5;
        this.highlight = z;
        this.checked = z2;
        this.coverId = j6;
    }

    public /* synthetic */ VideoItem(long j, String str, Uri uri, int i, int i2, long j2, long j3, long j4, long j5, boolean z, boolean z2, long j6, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(j, str, uri, i, i2, j2, j3, j4, j5, (i3 & 512) != 0 ? false : z, (i3 & VolumeUtilKt.FLAG_SHOW_UI_WARNINGS) != 0 ? false : z2, (i3 & 2048) != 0 ? -1L : j6);
    }

    public final long getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final Uri getUri() {
        return this.uri;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public final long getSize() {
        return this.size;
    }

    public final long getDateAdded() {
        return this.dateAdded;
    }

    public final long getDateModified() {
        return this.dateModified;
    }

    public final long getDuration() {
        return this.duration;
    }

    public final boolean getHighlight() {
        return this.highlight;
    }

    public final void setHighlight(boolean z) {
        this.highlight = z;
    }

    public final boolean getChecked() {
        return this.checked;
    }

    public final void setChecked(boolean z) {
        this.checked = z;
    }

    public final long getCoverId() {
        return this.coverId;
    }

    public final void setCoverId(long j) {
        this.coverId = j;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public VideoItem(android.os.Parcel r22) {
        /*
            r21 = this;
            r0 = r22
            java.lang.String r1 = "source"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
            long r3 = r22.readLong()
            java.lang.String r5 = r22.readString()
            kotlin.jvm.internal.Intrinsics.checkNotNull(r5)
            java.lang.String r1 = "source.readString()!!"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r1)
            java.lang.Class<android.net.Uri> r1 = android.net.Uri.class
            java.lang.ClassLoader r1 = r1.getClassLoader()
            android.os.Parcelable r1 = r0.readParcelable(r1)
            kotlin.jvm.internal.Intrinsics.checkNotNull(r1)
            java.lang.String r2 = "source.readParcelable<Ur…class.java.classLoader)!!"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            r6 = r1
            android.net.Uri r6 = (android.net.Uri) r6
            int r7 = r22.readInt()
            int r8 = r22.readInt()
            long r9 = r22.readLong()
            long r11 = r22.readLong()
            long r13 = r22.readLong()
            long r15 = r22.readLong()
            int r1 = r22.readInt()
            r2 = 1
            if (r2 != r1) goto L4d
            r1 = r2
            goto L4e
        L4d:
            r1 = 0
        L4e:
            int r0 = r22.readInt()
            if (r2 != r0) goto L57
            r18 = r2
            goto L59
        L57:
            r18 = 0
        L59:
            long r19 = r22.readLong()
            r2 = r21
            r17 = r1
            r2.<init>(r3, r5, r6, r7, r8, r9, r11, r13, r15, r17, r18, r19)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zui.game.service.highlight.model.VideoItem.<init>(android.os.Parcel):void");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Intrinsics.checkNotNullParameter(parcel, "dest");
        parcel.writeLong(getId());
        parcel.writeString(getName());
        parcel.writeParcelable(getUri(), 0);
        parcel.writeInt(getWidth());
        parcel.writeInt(getHeight());
        parcel.writeLong(getSize());
        parcel.writeLong(getDateAdded());
        parcel.writeLong(getDateModified());
        parcel.writeLong(getDuration());
        parcel.writeInt(getChecked() ? 1 : 0);
        parcel.writeInt(getHighlight() ? 1 : 0);
        parcel.writeLong(getCoverId());
    }

    public String toDebugLog() {
        StringBuilder append = new StringBuilder().append("VideoItem(id:").append(this.id).append(" a:");
        SimpleDateFormat simpleDateFormat = DATE_FORMAT;
        long j = 1000;
        return append.append((Object) simpleDateFormat.format(new Date(this.dateAdded * j))).append(" m:").append((Object) simpleDateFormat.format(new Date(this.dateModified * j))).append(" d:").append(((float) this.duration) / 1000).append(" c:").append(this.checked).append(" hl:").append(this.highlight).append(')').toString();
    }

    /* compiled from: VideoItem.kt */
    @Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J-\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u000b¢\u0006\u0002\u0010\u0013J'\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u000b¢\u0006\u0002\u0010\u0015R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lcom/zui/game/service/highlight/model/VideoItem$Companion;", "", "()V", "CREATOR", "Landroid/os/Parcelable$Creator;", "Lcom/zui/game/service/highlight/model/VideoItem;", "DATE_FORMAT", "Ljava/text/SimpleDateFormat;", "formatDate", "", "date", "", "toList", "Ljava/util/ArrayList;", "cursor", "Landroid/database/Cursor;", "videoUri", "Landroid/net/Uri;", "highlightId", "(Landroid/database/Cursor;Landroid/net/Uri;Ljava/lang/Long;)Ljava/util/ArrayList;", "toVideoItem", "(Landroid/database/Cursor;Landroid/net/Uri;Ljava/lang/Long;)Lcom/zui/game/service/highlight/model/VideoItem;", "highlight_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ ArrayList toList$default(Companion companion, Cursor cursor, Uri uri, Long l, int i, Object obj) {
            if ((i & 4) != 0) {
                l = null;
            }
            return companion.toList(cursor, uri, l);
        }

        public final ArrayList<VideoItem> toList(Cursor cursor, Uri uri, Long l) {
            Intrinsics.checkNotNullParameter(cursor, "cursor");
            Intrinsics.checkNotNullParameter(uri, "videoUri");
            ArrayList<VideoItem> arrayList = new ArrayList<>();
            try {
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    do {
                        arrayList.add(toVideoItem(cursor, uri, l));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        public static /* synthetic */ VideoItem toVideoItem$default(Companion companion, Cursor cursor, Uri uri, Long l, int i, Object obj) {
            if ((i & 4) != 0) {
                l = null;
            }
            return companion.toVideoItem(cursor, uri, l);
        }

        @SuppressLint("Range")
        public final VideoItem toVideoItem(Cursor cursor, Uri uri, Long l) {
            Intrinsics.checkNotNullParameter(cursor, "cursor");
            Intrinsics.checkNotNullParameter(uri, "videoUri");
            long j = cursor.getLong(cursor.getColumnIndex("_id"));
            String string = cursor.getString(cursor.getColumnIndex("_display_name"));
            int i = cursor.getInt(cursor.getColumnIndex("width"));
            int i2 = cursor.getInt(cursor.getColumnIndex("height"));
            long j2 = cursor.getLong(cursor.getColumnIndex("_size"));
            long j3 = cursor.getLong(cursor.getColumnIndex("date_added"));
            long j4 = cursor.getLong(cursor.getColumnIndex("date_modified"));
            long j5 = cursor.getLong(cursor.getColumnIndex("duration"));
            boolean z = l != null && l.longValue() == j;
            Uri withAppendedId = ContentUris.withAppendedId(uri, j);
            Intrinsics.checkNotNullExpressionValue(withAppendedId, "withAppendedId(\n        …ideoUri, id\n            )");
            Intrinsics.checkNotNullExpressionValue(string, "displayName");
            return new VideoItem(j, string, withAppendedId, i, i2, j2, j3, j4, j5, z, false, 0L, 3072, null);
        }

        @JvmStatic
        public final String formatDate(long j) {
            String format = VideoItem.DATE_FORMAT.format(new Date(j));
            Intrinsics.checkNotNullExpressionValue(format, "DATE_FORMAT.format(Date(date))");
            return format;
        }
    }
}