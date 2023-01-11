package com.example.backrecord.recorder.encoderconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.backrecord.recorder.encode_utils.RecorderHelper;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/**
 * 音频编码配置类
 * 创建ScreenRecorder实例时的参数之一
 * 用于指定录制音频的参数
 * Companion类用于从SharedPreferences读写配置参数并创建AudioEncodeConfig实例
 */
public final class AudioEncodeConfig implements Parcelable {
    private static final int DEFAULT_AUDIO_BITRATE = 80000;
    private static final int DEFAULT_AUDIO_CHANNEL_COUNT = 1;
    private static final String DEFAULT_AUDIO_CODEC_NAME = "OMX.google.aac.encoder";
    private static final String DEFAULT_AUDIO_MIME_TYPE = "audio/mp4a-latm";
    private static final int DEFAULT_AUDIO_PROFILE = 1;
    private static final int DEFAULT_AUDIO_SAMPLE_RATE = 44100;
    public static final int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final String KEY_AUDIO_BITRATE = "audio_bitrate";
    private static final String KEY_AUDIO_CHANNEL_COUNT = "audio_channel_count";
    private static final String KEY_AUDIO_CODEC_NAME = "audio_codec_name";
    private static final String KEY_AUDIO_MIME_TYPE = "audio_mime_type";
    private static final String KEY_AUDIO_PROFILE = "audio_profile";
    private static final String KEY_AUDIO_SAMPLE_RATE = "audio_sample_rate";
    public static final String KEY_AUDIO_SOURCE = "audio_source";
    private final int audioSource;
    private final int bitrate;
    private final int channelCount;
    private final String codecName;
    private final String mimeType;
    private final int profile;
    private final int sampleRate;
    public static final Companion Companion = new Companion(null);
    //---------------------------------------------------------
    private AudioPlaybackCaptureConfiguration audioPlaybackCaptureConfiguration=null;





    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public AudioEncodeConfig(String str, String str2, int i, int i2, int i3, int i4) {
        this(str, str2, i, i2, i3, i4, 0, 64, null);
        Intrinsics.checkNotNullParameter(str, "codecName");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
    }

    protected AudioEncodeConfig(Parcel in) {
        audioSource = in.readInt();
        bitrate = in.readInt();
        channelCount = in.readInt();
        codecName = in.readString();
        mimeType = in.readString();
        profile = in.readInt();
        sampleRate = in.readInt();
    }

    public static AudioEncodeConfig getNewSupportAudioEncodeConfig(Context c, AudioPlaybackCaptureConfiguration audioPlaybackCaptureConfiguration) {
        AudioEncodeConfig audioEncodeConfig = AudioEncodeConfig.Companion.readFromPrefs(c);
        audioEncodeConfig.setAudioPlaybackCaptureConfiguration(audioPlaybackCaptureConfiguration);
        return audioEncodeConfig;
    }

    public static final Creator<AudioEncodeConfig> CREATOR = new Creator<AudioEncodeConfig>() {
        @Override
        public AudioEncodeConfig createFromParcel(Parcel in) {
            return new AudioEncodeConfig(in);
        }

        @Override
        public AudioEncodeConfig[] newArray(int size) {
            return new AudioEncodeConfig[size];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AudioEncodeConfig(String str, String str2, int i, int i2, int i3, int i4, int i5) {
        Intrinsics.checkNotNullParameter(str, "codecName");
        Intrinsics.checkNotNullParameter(str2, "mimeType");
        this.codecName = str;
        this.mimeType = str2;
        this.bitrate = i;
        this.sampleRate = i2;
        this.channelCount = i3;
        this.profile = i4;
        this.audioSource = i5;
    }

    public /* synthetic */ AudioEncodeConfig(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, i, i2, i3, i4, (i6 & 64) != 0 ? 8 : i5);
    }

    public final String getCodecName() {
        return this.codecName;
    }

    public final String getMimeType() {
        return this.mimeType;
    }

    public final int getBitrate() {
        return this.bitrate;
    }

    public final int getSampleRate() {
        return this.sampleRate;
    }

    public final int getChannelCount() {
        return this.channelCount;
    }

    public final int getProfile() {
        return this.profile;
    }

    public final int getAudioSource() {
        return this.audioSource;
    }

    public AudioPlaybackCaptureConfiguration getAudioPlaybackCaptureConfiguration() {
        return audioPlaybackCaptureConfiguration;
    }

    public void setAudioPlaybackCaptureConfiguration(AudioPlaybackCaptureConfiguration audioPlaybackCaptureConfiguration) {
        this.audioPlaybackCaptureConfiguration = audioPlaybackCaptureConfiguration;
    }

    public final MediaFormat toFormat() {
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat(this.mimeType, this.sampleRate, this.channelCount);
        Intrinsics.checkNotNullExpressionValue(createAudioFormat, "createAudioFormat(mimeTy…sampleRate, channelCount)");
        createAudioFormat.setInteger("aac-profile", getProfile());
        createAudioFormat.setInteger("bitrate", getBitrate());
        return createAudioFormat;
    }



    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Intrinsics.checkNotNullParameter(parcel, "dest");
        parcel.writeString(getCodecName());
        parcel.writeString(getMimeType());
        parcel.writeInt(getBitrate());
        parcel.writeInt(getSampleRate());
        parcel.writeInt(getChannelCount());
        parcel.writeInt(getProfile());
        parcel.writeInt(getAudioSource());
    }

    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final void writeToPrefs(Context context, AudioEncodeConfig audioEncodeConfig) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(audioEncodeConfig, "audio");
            SharedPreferences.Editor edit = RecorderHelper.getConfigPreferences(context).edit();
            edit.putString(AudioEncodeConfig.KEY_AUDIO_CODEC_NAME, audioEncodeConfig.getCodecName());
            edit.putString(AudioEncodeConfig.KEY_AUDIO_MIME_TYPE, audioEncodeConfig.getMimeType());
            edit.putInt(AudioEncodeConfig.KEY_AUDIO_BITRATE, audioEncodeConfig.getBitrate());
            edit.putInt(AudioEncodeConfig.KEY_AUDIO_SAMPLE_RATE, audioEncodeConfig.getSampleRate());
            edit.putInt(AudioEncodeConfig.KEY_AUDIO_CHANNEL_COUNT, audioEncodeConfig.getChannelCount());
            edit.putInt(AudioEncodeConfig.KEY_AUDIO_PROFILE, audioEncodeConfig.getProfile());
            edit.putInt(AudioEncodeConfig.KEY_AUDIO_SOURCE, audioEncodeConfig.getAudioSource());
            edit.apply();
        }

        public final AudioEncodeConfig readFromPrefs(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            SharedPreferences configPreferences = RecorderHelper.getConfigPreferences(context);
            String string = configPreferences.getString(AudioEncodeConfig.KEY_AUDIO_CODEC_NAME, AudioEncodeConfig.DEFAULT_AUDIO_CODEC_NAME);
            String string2 = configPreferences.getString(AudioEncodeConfig.KEY_AUDIO_MIME_TYPE, "audio/mp4a-latm");
            int i = configPreferences.getInt(AudioEncodeConfig.KEY_AUDIO_BITRATE, AudioEncodeConfig.DEFAULT_AUDIO_BITRATE);
            int i2 = configPreferences.getInt(AudioEncodeConfig.KEY_AUDIO_SAMPLE_RATE, AudioEncodeConfig.DEFAULT_AUDIO_SAMPLE_RATE);
            int i3 = configPreferences.getInt(AudioEncodeConfig.KEY_AUDIO_CHANNEL_COUNT, 1);
            int i4 = configPreferences.getInt(AudioEncodeConfig.KEY_AUDIO_PROFILE, 1);
            int i5 = configPreferences.getInt(AudioEncodeConfig.KEY_AUDIO_SOURCE, DEFAULT_AUDIO_SOURCE);
            Intrinsics.checkNotNull(string);
            Intrinsics.checkNotNull(string2);
            return new AudioEncodeConfig(string, string2, i, i2, i3, i4, i5);
        }
    }
}