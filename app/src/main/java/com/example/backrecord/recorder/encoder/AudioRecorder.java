package com.example.backrecord.recorder.encoder;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.SparseLongArray;

import com.example.backrecord.recorder.encoderconfig.AudioEncodeConfig;
import com.example.backrecord.recorder.encode_utils.RecorderHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public class AudioRecorder implements Encoder {
    private static final int LAST_FRAME_ID = -1;
    private static final int MSG_DRAIN_OUTPUT = 2;
    private static final int MSG_FEED_INPUT = 1;
    private static final int MSG_PREPARE = 0;
    private static final int MSG_RELEASE = 5;
    private static final int MSG_RELEASE_OUTPUT = 3;
    private static final int MSG_STOP = 4;
    private static final String TAG = "AudioRecorder";
    private static final boolean VERBOSE = false;
    private AudioRecord mAudioRecord;
    private int mAudioSource;
    private BaseEncoder.Callback mCallback;
    private CallbackDelegate mCallbackDelegate;
    private int mChannelConfig;
    private int mChannelsSampleRate;
    private final AudioEncoder mEncoder;
    private RecordHandler mRecordHandler;
    private final HandlerThread mRecordThread;
    private int mSampleRate;
    private int mFormat = 2;
    private AtomicBoolean mForceStop = new AtomicBoolean(false);
    private SparseLongArray mFramesUsCache = new SparseLongArray(2);

    //---------------内录声音的的适配-------------------------------------------
    private AudioPlaybackCaptureConfiguration audioPlaybackCaptureConfiguration = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioRecorder(AudioEncodeConfig audioEncodeConfig) {
        this.mEncoder = new AudioEncoder(audioEncodeConfig);
        int sampleRate = audioEncodeConfig.getSampleRate();
        this.mSampleRate = sampleRate;
        this.mChannelsSampleRate = sampleRate * audioEncodeConfig.getChannelCount();
        this.mChannelConfig = audioEncodeConfig.getChannelCount() == 2 ? 12 : 16;
        this.mRecordThread = new HandlerThread(TAG);
        this.mAudioSource = audioEncodeConfig.getAudioSource();

        //--------------------------------------------
        if (audioEncodeConfig.getAudioPlaybackCaptureConfiguration() != null) {
            this.audioPlaybackCaptureConfiguration = audioEncodeConfig.getAudioPlaybackCaptureConfiguration();
        }
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void setCallback(Encoder.Callback callback) {
        this.mCallback = (BaseEncoder.Callback) callback;
    }

    public void setCallback(BaseEncoder.Callback callback) {
        this.mCallback = callback;
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void prepare() throws IOException {
        this.mCallbackDelegate = new CallbackDelegate((Looper) Objects.requireNonNull(Looper.myLooper(), "Should prepare in HandlerThread"), this.mCallback);
        this.mRecordThread.start();
        RecordHandler recordHandler = new RecordHandler(this.mRecordThread.getLooper());
        this.mRecordHandler = recordHandler;
        recordHandler.sendEmptyMessage(0);
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void stop() {
        if (8 == this.mAudioSource) {
            RecorderHelper.setLoopBackFlag(false);
        }
        CallbackDelegate callbackDelegate = this.mCallbackDelegate;
        if (callbackDelegate != null) {
            callbackDelegate.removeCallbacksAndMessages(null);
        }
        this.mForceStop.set(true);
        RecordHandler recordHandler = this.mRecordHandler;
        if (recordHandler != null) {
            recordHandler.sendEmptyMessage(4);
        }
    }

    @Override // com.zui.game.service.highlight.Encoder
    public void release() {
        RecordHandler recordHandler = this.mRecordHandler;
        if (recordHandler != null) {
            recordHandler.sendEmptyMessage(5);
        }
        this.mRecordThread.quitSafely();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void releaseOutputBuffer(int i) {
        Message.obtain(this.mRecordHandler, 3, i, 0).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer getOutputBuffer(int i) {
        return this.mEncoder.getOutputBuffer(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    public static class CallbackDelegate extends Handler {
        private BaseEncoder.Callback mCallback;

        CallbackDelegate(Looper looper, BaseEncoder.Callback callback) {
            super(looper);
            this.mCallback = callback;
        }

        void onError(final Encoder encoder, final Exception exc) {
            Message.obtain(this, new Runnable() { // from class: com.zui.game.service.highlight.AudioRecorder$CallbackDelegate$$ExternalSyntheticLambda2
                {

//                    AudioRecorder.CallbackDelegate.this = this;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    AudioRecorder.CallbackDelegate.this.m2x2c8b9a0f(encoder, exc);
                }
            }).sendToTarget();
        }

        /* renamed from: lambda$onError$0$com-zui-game-service-highlight-AudioRecorder$CallbackDelegate  reason: not valid java name */
        public /* synthetic */ void m2x2c8b9a0f(Encoder encoder, Exception exc) {
            BaseEncoder.Callback callback = this.mCallback;
            if (callback != null) {
                callback.onError(encoder, exc);
            }
        }

        void onOutputFormatChanged(final BaseEncoder baseEncoder, final MediaFormat mediaFormat) {
            Message.obtain(this, new Runnable() { // from class: com.zui.game.service.highlight.AudioRecorder$CallbackDelegate$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AudioRecorder.CallbackDelegate.this.m4x9696a484(baseEncoder, mediaFormat);
                }
            }).sendToTarget();
        }

        /* renamed from: lambda$onOutputFormatChanged$1$com-zui-game-service-highlight-AudioRecorder$CallbackDelegate  reason: not valid java name */
        public /* synthetic */ void m4x9696a484(BaseEncoder baseEncoder, MediaFormat mediaFormat) {
            BaseEncoder.Callback callback = this.mCallback;
            if (callback != null) {
                callback.onOutputFormatChanged(baseEncoder, mediaFormat);
            }
        }

        void onOutputBufferAvailable(final BaseEncoder baseEncoder, final int i, final MediaCodec.BufferInfo bufferInfo) {
            Message.obtain(this, new Runnable() { // from class: com.zui.game.service.highlight.AudioRecorder$CallbackDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AudioRecorder.CallbackDelegate.this.m3xf6da83b1(baseEncoder, i, bufferInfo);
                }
            }).sendToTarget();
        }

        /* renamed from: lambda$onOutputBufferAvailable$2$com-zui-game-service-highlight-AudioRecorder$CallbackDelegate  reason: not valid java name */
        public /* synthetic */ void m3xf6da83b1(BaseEncoder baseEncoder, int i, MediaCodec.BufferInfo bufferInfo) {
            BaseEncoder.Callback callback = this.mCallback;
            if (callback != null) {
                callback.onOutputBufferAvailable(baseEncoder, i, bufferInfo);
            }
        }
    }

    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    private class RecordHandler extends Handler {
        private LinkedList<MediaCodec.BufferInfo> mCachedInfos;
        private LinkedList<Integer> mMuxingOutputBufferIndices;
        private int mPollRate;

        RecordHandler(Looper looper) {
            super(looper);
            this.mCachedInfos = new LinkedList<>();
            this.mMuxingOutputBufferIndices = new LinkedList<>();
            this.mPollRate = 2048000 / AudioRecorder.this.mSampleRate;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {

                @SuppressLint("MissingPermission")
                AudioRecord createAudioRecord = new AudioRecord.Builder()
                        .setAudioFormat(new AudioFormat.Builder()
                                .setSampleRate(mSampleRate)
                                .setChannelMask(mChannelConfig)
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .build())
                        .setBufferSizeInBytes(AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mFormat))
                        .setAudioPlaybackCaptureConfig(audioPlaybackCaptureConfiguration)
                        .build();

                if (createAudioRecord == null) {
                    if (8 == AudioRecorder.this.mAudioSource) {
                    }
                    createAudioRecord = AudioRecorder.createAudioRecord(AudioRecorder.this.mSampleRate, AudioRecorder.this.mChannelConfig, AudioRecorder.this.mFormat, 1);
                }
                if (createAudioRecord != null) {
                    if (8 == AudioRecorder.this.mAudioSource) {
                        RecorderHelper.setLoopBackFlag(true);
                    }
                    createAudioRecord.startRecording();
                    AudioRecorder.this.mAudioRecord = createAudioRecord;
                    try {
                        AudioRecorder.this.mEncoder.prepare();
                    } catch (Exception e) {
                        AudioRecorder.this.mCallbackDelegate.onError(AudioRecorder.this, e);
                        return;
                    }
                } else {
                    AudioRecorder.this.mCallbackDelegate.onError(AudioRecorder.this, new IllegalArgumentException());
                    return;
                }
            } else if (i != 1) {
                if (i == 2) {
                    offerOutput();
                    pollInputIfNeed();
                    return;
                } else if (i == 3) {
                    AudioRecorder.this.mEncoder.releaseOutputBuffer(message.arg1);
                    this.mMuxingOutputBufferIndices.poll();
                    pollInputIfNeed();
                    return;
                } else if (i == 4) {
                    if (AudioRecorder.this.mAudioRecord != null) {
                        AudioRecorder.this.mAudioRecord.stop();
                    }
                    AudioRecorder.this.mEncoder.stop();
                    return;
                } else if (i != 5) {
                    return;
                } else {
                    if (AudioRecorder.this.mAudioRecord != null) {
                        AudioRecorder.this.mAudioRecord.release();
                        AudioRecorder.this.mAudioRecord = null;
                    }
                    AudioRecorder.this.mEncoder.release();
                    return;
                }
            }
            if (AudioRecorder.this.mForceStop.get()) {
                return;
            }
            int pollInput = pollInput();
            if (pollInput >= 0) {
                AudioRecorder.this.feedAudioEncoder(pollInput);
                if (AudioRecorder.this.mForceStop.get()) {
                    return;
                }
                sendEmptyMessage(2);
                return;
            }
            sendEmptyMessageDelayed(1, this.mPollRate);
        }

        private void offerOutput() {
            while (!AudioRecorder.this.mForceStop.get()) {
                MediaCodec.BufferInfo poll = this.mCachedInfos.poll();
                if (poll == null) {
                    poll = new MediaCodec.BufferInfo();
                }
                int dequeueOutputBuffer = AudioRecorder.this.mEncoder.getEncoder().dequeueOutputBuffer(poll, 1L);
                if (dequeueOutputBuffer == -2) {
                    AudioRecorder.this.mCallbackDelegate.onOutputFormatChanged(AudioRecorder.this.mEncoder, AudioRecorder.this.mEncoder.getEncoder().getOutputFormat());
                }
                if (dequeueOutputBuffer < 0) {
                    poll.set(0, 0, 0L, 0);
                    this.mCachedInfos.offer(poll);
                    return;
                }
                this.mMuxingOutputBufferIndices.offer(Integer.valueOf(dequeueOutputBuffer));
                AudioRecorder.this.mCallbackDelegate.onOutputBufferAvailable(AudioRecorder.this.mEncoder, dequeueOutputBuffer, poll);
            }
        }

        private int pollInput() {
            return AudioRecorder.this.mEncoder.getEncoder().dequeueInputBuffer(0L);
        }

        private void pollInputIfNeed() {
            if (this.mMuxingOutputBufferIndices.size() > 1 || AudioRecorder.this.mForceStop.get()) {
                return;
            }
            removeMessages(1);
            sendEmptyMessageDelayed(1, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void feedAudioEncoder(int i) {
        int read;
        if (i < 0 || this.mForceStop.get()) {
            return;
        }
        AudioRecord audioRecord = (AudioRecord) Objects.requireNonNull(this.mAudioRecord, "maybe release");
        boolean z = audioRecord.getRecordingState() == 1;
        ByteBuffer inputBuffer = this.mEncoder.getInputBuffer(i);
        int position = inputBuffer.position();
        int i2 = (z || (read = audioRecord.read(inputBuffer, inputBuffer.limit())) < 0) ? 0 : read;
        this.mEncoder.queueInputBuffer(i, position, i2, calculateFrameTimestamp(i2 << 3), z ? 4 : 1);
    }

    private long calculateFrameTimestamp(int i) {
        int i2 = i >> 4;
        long j = this.mFramesUsCache.get(i2, -1L);
        if (j == -1) {
            j = (1000000 * i2) / this.mChannelsSampleRate;
            this.mFramesUsCache.put(i2, j);
        }
        long elapsedRealtimeNanos = (SystemClock.elapsedRealtimeNanos() / 1000) - j;
        long j2 = this.mFramesUsCache.get(-1, -1L);
        if (j2 == -1) {
            j2 = elapsedRealtimeNanos;
        }
        if (elapsedRealtimeNanos - j2 < (j << 1)) {
            elapsedRealtimeNanos = j2;
        }
        this.mFramesUsCache.put(-1, j + elapsedRealtimeNanos);
        return elapsedRealtimeNanos;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static AudioRecord createAudioRecord(int i, int i2, int i3, int i4) {
        int minBufferSize = AudioRecord.getMinBufferSize(i, i2, i3);
        if (minBufferSize <= 0) {
            return null;
        }
        @SuppressLint("MissingPermission") AudioRecord audioRecord = new AudioRecord(i4, i, i2, i3, minBufferSize * 2);
        if (audioRecord.getState() == 0) {
            return null;
        }
        return audioRecord;
    }
}