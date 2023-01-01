package com.example.backrecord.recorder.encoderconfig;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.WindowManager;

import com.example.backrecord.recorder.utils.Logger;
import com.example.backrecord.recorder.utils.RecorderHelper;
import com.example.backrecord.recorder.utils.Utils;
import com.example.backrecord.recorder.utils.VolumeUtilKt;

import kotlin.Pair;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/**
 * 参考VideoEncoderConfig
 */
/* compiled from: VideoEncodeConfig.kt */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public final class VideoEncodeConfig implements Parcelable {
    public static final int DEFAULT_VIDEO_BITRATE = 10000000;
    private static final String DEFAULT_VIDEO_CODEC_NAME = "OMX.qcom.video.encoder.avc";
    private static final int DEFAULT_VIDEO_DPI = 480;
    public static final int DEFAULT_VIDEO_DURATION = 15;
    public static final int DEFAULT_VIDEO_FRAME_RATE = 30;
    public static final int DEFAULT_VIDEO_HEIGHT = 720;
    private static final int DEFAULT_VIDEO_I_FRAME = 1;
    private static final int DEFAULT_VIDEO_LEVEL = 1;
    private static final String DEFAULT_VIDEO_MIME_TYPE = "video/avc";
    private static final int DEFAULT_VIDEO_ORIENTATION = 2;
    private static final int DEFAULT_VIDEO_PROFILE = 1;
    public static final int DEFAULT_VIDEO_WIDTH = 1560;
    public static final String KEY_VIDEO_BITRATE = "video_bitrate";
    private static final String KEY_VIDEO_CODEC_NAME = "video_codec_name";
    private static final String KEY_VIDEO_DPI = "video_dpi";
    public static final String KEY_VIDEO_DURATION = "video_duration";
    public static final String KEY_VIDEO_FRAME_RATE = "video_frame_rate";
    public static final String KEY_VIDEO_HEIGHT = "video_height";
    private static final String KEY_VIDEO_I_FRAME = "video_i_frame";
    private static final String KEY_VIDEO_LEVEL = "video_level";
    public static final String KEY_VIDEO_MIME_TYPE = "video_mime_type";
    private static final String KEY_VIDEO_ORIENTATION = "video_orientation";
    private static final String KEY_VIDEO_PROFILE = "video_profile";
    public static final String KEY_VIDEO_WIDTH = "video_width";
    private int bitrate;
    private final String codecName;
    private int dpi;
    private int duration;
    private final int framerate;
    private int height;
    private final int iframeInterval;
    private final Integer level;
    private final String mimeType;
    private int orientation;
    private final Integer profile;
    private int width;
    public static final Companion Companion = new Companion(null);


    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public VideoEncodeConfig(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this(str, str2, i, i2, i3, i4, i5, i6, i7, i8, null, null, 3072, null);
        Intrinsics.checkNotNullParameter(str, "codecName");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public VideoEncodeConfig(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Integer num) {
        this(str, str2, i, i2, i3, i4, i5, i6, i7, i8, num, null, 2048, null);
        Intrinsics.checkNotNullParameter(str, "codecName");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
    }

    protected VideoEncodeConfig(Parcel in) {
        bitrate = in.readInt();
        codecName = in.readString();
        dpi = in.readInt();
        duration = in.readInt();
        framerate = in.readInt();
        height = in.readInt();
        iframeInterval = in.readInt();
        if (in.readByte() == 0) {
            level = null;
        } else {
            level = in.readInt();
        }
        mimeType = in.readString();
        orientation = in.readInt();
        if (in.readByte() == 0) {
            profile = null;
        } else {
            profile = in.readInt();
        }
        width = in.readInt();
    }

    public static final Creator<VideoEncodeConfig> CREATOR = new Creator<VideoEncodeConfig>() {
        @Override
        public VideoEncodeConfig createFromParcel(Parcel in) {
            return new VideoEncodeConfig(in);
        }

        @Override
        public VideoEncodeConfig[] newArray(int size) {
            return new VideoEncodeConfig[size];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public VideoEncodeConfig(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Integer num, Integer num2) {
        Intrinsics.checkNotNullParameter(str, "codecName");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
        this.codecName = str;
        this.mimeType = str2;
        this.width = i;
        this.height = i2;
        this.bitrate = i3;
        this.framerate = i4;
        this.iframeInterval = i5;
        this.dpi = i6;
        this.duration = i7;
        this.orientation = i8;
        this.profile = num;
        this.level = num2;
    }

    public /* synthetic */ VideoEncodeConfig(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Integer num, Integer num2, int i9, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, i, i2, i3, i4, i5, i6, i7, i8, (i9 & VolumeUtilKt.FLAG_SHOW_UI_WARNINGS) != 0 ? null : num, (i9 & 2048) != 0 ? null : num2);
    }

    public final String getCodecName() {
        return this.codecName;
    }

    public final String getMimeType() {
        return this.mimeType;
    }

    public final int getWidth() {
        return this.width;
    }

    public final void setWidth(int i) {
        this.width = i;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void setHeight(int i) {
        this.height = i;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public final int getBitrate() {
        return this.bitrate;
    }

    public final int getFramerate() {
        return this.framerate;
    }

    public final int getIframeInterval() {
        return this.iframeInterval;
    }

    public final int getDpi() {
        return this.dpi;
    }

    public final void setDpi(int i) {
        this.dpi = i;
    }

    public final int getDuration() {
        return this.duration;
    }

    public final void setDuration(int i) {
        this.duration = i;
    }

    public final int getOrientation() {
        return this.orientation;
    }

    public final void setOrientation(int i) {
        this.orientation = i;
    }

    public final Integer getProfile() {
        return this.profile;
    }

    public final Integer getLevel() {
        return this.level;
    }


    public final MediaFormat toFormat() {
        Pair<Integer, Integer> updateSize = Companion.updateSize(new Pair<>(Integer.valueOf(this.width), Integer.valueOf(this.height)), this.orientation);
        checkConfig(this.codecName, this.orientation, ((Number) updateSize.getFirst()).intValue(), ((Number) updateSize.getSecond()).intValue(), this.mimeType);
        MediaFormat createVideoFormat = MediaFormat.createVideoFormat(this.mimeType, ((Number) updateSize.getFirst()).intValue(), ((Number) updateSize.getSecond()).intValue());
        Intrinsics.checkNotNullExpressionValue(createVideoFormat, "createVideoFormat(mimeTy… size.first, size.second)");
        createVideoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, 2130708361);
        createVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, getFramerate());
        createVideoFormat.setInteger(MediaFormat.KEY_MAX_FPS_TO_ENCODER, getFramerate());
        createVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, getIframeInterval());
        createVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, getBitrate());
        Integer profile = getProfile();
        if (profile != null) {
            profile.intValue();
            createVideoFormat.setInteger("profile", getProfile().intValue());
        }
        Integer level = getLevel();
        if (level != null) {
            level.intValue();
            createVideoFormat.setInteger("level", getLevel().intValue());
        }
        return createVideoFormat;
    }

    private final boolean checkConfig(String str, int i, int i2, int i3, String str2) {
        MediaCodecInfo mediaCodecInfo;
        MediaCodecInfo[] findEncodersByType = Utils.findEncodersByType(str2);
        Intrinsics.checkNotNullExpressionValue(findEncodersByType, "codecInfos");
        int length = findEncodersByType.length;
        int i4 = 0;
        while (true) {
            if (i4 >= length) {
                mediaCodecInfo = null;
                break;
            }
            mediaCodecInfo = findEncodersByType[i4];
            if (Intrinsics.areEqual(mediaCodecInfo.getName(), str)) {
                break;
            }
            i4++;
        }
        if (mediaCodecInfo == null) {
            Logger.d("Not found " + str + " Codec");
            return false;
        }
        String str3 = str + " size " + i2 + 'x' + i3 + " (" + ')';
        if (!mediaCodecInfo.getCapabilitiesForType(str2).getVideoCapabilities().isSizeSupported(i2, i3)) {
            Logger.e(Intrinsics.stringPlus("Not Support ", str3));
            return false;
        }
        Logger.d(Intrinsics.stringPlus("Support ", str3));
        return true;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */


    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Intrinsics.checkNotNullParameter(parcel, "dest");
        parcel.writeString(getCodecName());
        parcel.writeString(getMimeType());
        parcel.writeInt(getWidth());
        parcel.writeInt(getHeight());
        parcel.writeInt(getBitrate());
        parcel.writeInt(getFramerate());
        parcel.writeInt(getIframeInterval());
        parcel.writeInt(getDpi());
        parcel.writeInt(getDuration());
        parcel.writeInt(getOrientation());
        parcel.writeValue(getProfile());
        parcel.writeValue(getLevel());
    }

    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final void writeToPrefs(Context context, VideoEncodeConfig videoEncodeConfig) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(videoEncodeConfig, "video");
            SharedPreferences.Editor edit = RecorderHelper.getConfigPreferences(context).edit();
            edit.putString(VideoEncodeConfig.KEY_VIDEO_CODEC_NAME, videoEncodeConfig.getCodecName());
            edit.putString(VideoEncodeConfig.KEY_VIDEO_MIME_TYPE, videoEncodeConfig.getMimeType());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, videoEncodeConfig.getWidth());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, videoEncodeConfig.getHeight());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_BITRATE, videoEncodeConfig.getBitrate());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_FRAME_RATE, videoEncodeConfig.getFramerate());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_I_FRAME, videoEncodeConfig.getIframeInterval());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_DPI, videoEncodeConfig.getDpi());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_DURATION, videoEncodeConfig.getDuration());
            edit.putInt(VideoEncodeConfig.KEY_VIDEO_ORIENTATION, videoEncodeConfig.getOrientation());
            Integer profile = videoEncodeConfig.getProfile();
            if (profile != null) {
                profile.intValue();
                edit.putInt(VideoEncodeConfig.KEY_VIDEO_PROFILE, videoEncodeConfig.getProfile().intValue());
            }
            Integer level = videoEncodeConfig.getLevel();
            if (level != null) {
                level.intValue();
                edit.putInt(VideoEncodeConfig.KEY_VIDEO_LEVEL, videoEncodeConfig.getLevel().intValue());
            }
            edit.commit();
        }

        public final VideoEncodeConfig readFromPrefs(Context context, int i) {
            Integer num;
            Integer num2;
            int i2;//height
            int i3;//width
            Intrinsics.checkNotNullParameter(context, "context");
            SharedPreferences configPreferences = RecorderHelper.getConfigPreferences(context);
            String string = configPreferences.getString(VideoEncodeConfig.KEY_VIDEO_CODEC_NAME, VideoEncodeConfig.DEFAULT_VIDEO_CODEC_NAME);
            String string2 = configPreferences.getString(VideoEncodeConfig.KEY_VIDEO_MIME_TYPE, "video/avc");
            int i5;
            int i4;
            if (configPreferences.contains(VideoEncodeConfig.KEY_VIDEO_HEIGHT)) {
                i4 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, VideoEncodeConfig.DEFAULT_VIDEO_WIDTH);
                i5 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, VideoEncodeConfig.DEFAULT_VIDEO_HEIGHT);
            } else {
                WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
                //获取屏幕的宽、高，单位是像素
                int width = (int) (windowManager.getMaximumWindowMetrics().getBounds().width()/1.5);
                int height = (int) (windowManager.getMaximumWindowMetrics().getBounds().height()/1.5);
                i4 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, width);
                i5 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, height);
            }
            int i6 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_BITRATE, VideoEncodeConfig.DEFAULT_VIDEO_BITRATE);
            int i7 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_FRAME_RATE, 30);
            int i8 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_I_FRAME, 1);
            int i9 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_DPI, VideoEncodeConfig.DEFAULT_VIDEO_DPI);
            int i10 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_DURATION, 60);
            if (configPreferences.contains(VideoEncodeConfig.KEY_VIDEO_PROFILE)) {
                num = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_PROFILE, 1);
            } else {
                num = null;
            }
            Integer num3 = num;
            if (configPreferences.contains(VideoEncodeConfig.KEY_VIDEO_LEVEL)) {
                num2 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_LEVEL, 1);
            } else {
                num2 = null;
            }
            int min = Math.min(i4, i5);
            int max = Math.max(i4, i5);
            if (i == 1) {//竖屏
                i3 = min;
                i2 = max;
            } else {//横屏
                i2 = min;
                i3 = max;
            }
            Intrinsics.checkNotNull(string);
            Intrinsics.checkNotNull(string2);
            return new VideoEncodeConfig(string, string2, i3, i2, i6, i7, i8, i9, i10, i, num3, num2);
        }

        public final int getVideoDuration(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return RecorderHelper.getConfigPreferences(context).getInt(VideoEncodeConfig.KEY_VIDEO_DURATION, 15);
        }

        public final Pair<Integer, Integer> updateSize(Pair<Integer, Integer> pair, int i) {
            Intrinsics.checkNotNullParameter(pair, "size");
            int max = Math.max(((Number) pair.getFirst()).intValue(), ((Number) pair.getSecond()).intValue());
            int min = Math.min(((Number) pair.getFirst()).intValue(), ((Number) pair.getSecond()).intValue());
            if (i != 1) {
                if (i == 2) {
                    return new Pair<>(Integer.valueOf(max), Integer.valueOf(min));
                }
                return new Pair<>(Integer.valueOf(max), Integer.valueOf(min));
            }
            return new Pair<>(Integer.valueOf(min), Integer.valueOf(max));
        }
    }
}