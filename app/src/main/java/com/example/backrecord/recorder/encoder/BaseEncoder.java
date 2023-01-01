package com.example.backrecord.recorder.encoder;


import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Looper;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public abstract class BaseEncoder implements Encoder {
    private Callback mCallback;
    private final MediaCodec.Callback mCodecCallback = new MediaCodec.Callback() { // from class: com.zui.game.service.highlight.BaseEncoder.1
        @Override // android.media.MediaCodec.Callback
        public void onInputBufferAvailable(MediaCodec mediaCodec, int i) {
            BaseEncoder.this.mCallback.onInputBufferAvailable(BaseEncoder.this, i);
        }

        @Override // android.media.MediaCodec.Callback
        public void onOutputBufferAvailable(MediaCodec mediaCodec, int i, MediaCodec.BufferInfo bufferInfo) {
            BaseEncoder.this.mCallback.onOutputBufferAvailable(BaseEncoder.this, i, bufferInfo);
        }

        @Override // android.media.MediaCodec.Callback
        public void onError(MediaCodec mediaCodec, MediaCodec.CodecException codecException) {
            BaseEncoder.this.mCallback.onError(BaseEncoder.this, codecException);
        }

        @Override // android.media.MediaCodec.Callback
        public void onOutputFormatChanged(MediaCodec mediaCodec, MediaFormat mediaFormat) {
            BaseEncoder.this.mCallback.onOutputFormatChanged(BaseEncoder.this, mediaFormat);
        }
    };
    private String mCodecName;
    private MediaCodec mEncoder;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    public static abstract class Callback implements Encoder.Callback {
        void onInputBufferAvailable(BaseEncoder baseEncoder, int i) {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onOutputBufferAvailable(BaseEncoder baseEncoder, int i, MediaCodec.BufferInfo bufferInfo) {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onOutputFormatChanged(BaseEncoder baseEncoder, MediaFormat mediaFormat) {
        }
    }

    protected abstract MediaFormat createMediaFormat();

    protected void onEncoderConfigured(MediaCodec mediaCodec) {
    }

    BaseEncoder() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseEncoder(String str) {
        this.mCodecName = str;
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void setCallback(Encoder.Callback callback) {
        if (!(callback instanceof Callback)) {
            throw new IllegalArgumentException();
        }
        setCallback((Callback) callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCallback(Callback callback) {
        if (this.mEncoder != null) {
            throw new IllegalStateException("mEncoder is not null");
        }
        this.mCallback = callback;
    }

    @SuppressLint("WrongConstant")
    @Override // com.zui.game.service.highlight.Encoder
    public void prepare() throws IOException {
        if (Looper.myLooper() == null || Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("should run in a HandlerThread");
        }
        if (this.mEncoder != null) {
            throw new IllegalStateException("prepared!");
        }

        MediaFormat createMediaFormat = createMediaFormat();
        MediaCodec createEncoder = createEncoder(createMediaFormat.getString("mime"));
        try {
            if (this.mCallback != null) {
                createEncoder.setCallback(this.mCodecCallback);
            }
            createEncoder.configure(createMediaFormat, (Surface) null, (MediaCrypto) null, 1);
            onEncoderConfigured(createEncoder);
            createEncoder.start();
            this.mEncoder = createEncoder;
        } catch (MediaCodec.CodecException e) {
            throw e;
        }
    }

    private MediaCodec createEncoder(String str) throws IOException {
        try {
            String str2 = this.mCodecName;
            if (str2 != null) {
                return MediaCodec.createByCodecName(str2);
            }
        } catch (IOException e) {
        }
        return MediaCodec.createEncoderByType(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MediaCodec getEncoder() {
        return (MediaCodec) Objects.requireNonNull(this.mEncoder, "doesn't prepare()");
    }

    public final ByteBuffer getOutputBuffer(int i) {
        return getEncoder().getOutputBuffer(i);
    }

    public final ByteBuffer getInputBuffer(int i) {
        return getEncoder().getInputBuffer(i);
    }

    public final void queueInputBuffer(int i, int i2, int i3, long j, int i4) {
        getEncoder().queueInputBuffer(i, i2, i3, j, i4);
    }

    public final void releaseOutputBuffer(int i) {
        getEncoder().releaseOutputBuffer(i, false);
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void stop() {
        MediaCodec mediaCodec = this.mEncoder;
        if (mediaCodec != null) {
            mediaCodec.stop();
        }
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void release() {
        MediaCodec mediaCodec = this.mEncoder;
        if (mediaCodec != null) {
            mediaCodec.release();
            this.mEncoder = null;
        }
    }
}