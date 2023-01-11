package com.example.backrecord.recorder;

import android.annotation.SuppressLint;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.example.backrecord.recorder.encoder.AudioRecorder;
import com.example.backrecord.recorder.encoder.BaseEncoder;
import com.example.backrecord.recorder.encoder.Encoder;
import com.example.backrecord.recorder.encoder.VideoEncoder;
import com.example.backrecord.recorder.encoderconfig.AudioEncodeConfig;
import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;
import com.example.backrecord.recorder.encode_utils.Logger;
import com.example.backrecord.recorder.encode_utils.MuxerWrapper;
import com.example.backrecord.recorder.encode_utils.RecordPath;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 最核心的类
 * 录制屏幕，实现回录功能
 * 所有公共方法如下：
 *   public interface Callback: 回调接口
 *   public ScreenRecorder(VideoEncodeConfig videoEncodeConfig, AudioEncodeConfig audioEncodeConfig, RecordPath recordPath, VirtualDisplay virtualDisplay): 构造方法
 *   public final void quit(): 退出录制
 *   public void start(): 开始录制
 *   public void signalNext(RecordPath recordPath): 保存回录,指定下一次保存视频文件的地址
 *   public void setCallback(Callback callback): 设置回调
 */
public class ScreenRecorder {
    public static final String AUDIO_AAC = "audio/mp4a-latm";
    public static final int INVALID_INDEX = -1;
    private static final int MSG_ERROR = 3;
    private static final int MSG_NEXT = 1;
    private static final int MSG_START = 0;
    private static final int MSG_STOP = 2;
    private static final int STOP_WITH_EOS = 2;
    private static final String TAG = "ScreenRecorder";
    private static final boolean VERBOSE = false;
    public static final String VIDEO_AVC = "video/avc";
    //外部类
    private AudioRecorder mAudioEncoder;
    private long mAudioPtsOffset;
    private Callback mCallback;
    //外部类
    private RecordPath mDstPath;
    private final int mDuration;
    private CallbackHandler mHandler;
    //外部类
    private MuxerWrapper mMuxer;
    //外部类
    private VideoEncoder mVideoEncoder;
    private long mVideoPtsOffset;
    private VirtualDisplay mVirtualDisplay;
    private HandlerThread mWorker;
    private MediaFormat mVideoOutputFormat = null;
    private MediaFormat mAudioOutputFormat = null;
    private int mVideoTrackIndex = INVALID_INDEX;
    private int mAudioTrackIndex = INVALID_INDEX;
    private boolean mMuxerStarted = false;
    private final AtomicBoolean mForceQuit = new AtomicBoolean(false);
    private final AtomicBoolean mIsRunning = new AtomicBoolean(false);
    private final LinkedList<Integer> mPendingVideoEncoderBufferIndices = new LinkedList<>();
    private final LinkedList<Integer> mPendingAudioEncoderBufferIndices = new LinkedList<>();
    private final LinkedList<MediaCodec.BufferInfo> mPendingAudioEncoderBufferInfos = new LinkedList<>();
    private final LinkedList<MediaCodec.BufferInfo> mPendingVideoEncoderBufferInfos = new LinkedList<>();
    private final LinkedBlockingQueue<SampleData> mVideoCache = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<SampleData> mAudioCache = new LinkedBlockingQueue<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    public interface Callback {
        void onFileSaved(Uri uri);

        void onRecording(long j);

        void onRelease(Uri uri);

        void onStart();

        void onStop(Throwable th);
    }

    /**
     * 构造方法
     * @param videoEncodeConfig 视频配置
     * @param audioEncodeConfig 音频配置
     * @param recordPath 用于获取保存路径等
     * @param virtualDisplay 虚拟屏幕，此处虚拟屏幕不需要设置Surface,MediaCodec会添加Surface
     */
    public ScreenRecorder(VideoEncodeConfig videoEncodeConfig, AudioEncodeConfig audioEncodeConfig, RecordPath recordPath, VirtualDisplay virtualDisplay) {
        //外部类
        //外部类
        this.mVideoEncoder = new VideoEncoder(videoEncodeConfig);
        this.mAudioEncoder = audioEncodeConfig != null ? new AudioRecorder(audioEncodeConfig) : null;
        this.mDstPath = recordPath;
        this.mDuration = videoEncodeConfig.getDuration() * 1000000;
        this.mVirtualDisplay = virtualDisplay;
    }

    public final void quit() {
        this.mForceQuit.set(true);
        if (!this.mIsRunning.get()) {
            release();
        } else {
            signalStop(false);
        }
    }

    public void start() {
        if (this.mWorker != null) {
            throw new IllegalStateException();
        }
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mWorker = handlerThread;
        handlerThread.start();
        CallbackHandler callbackHandler = new CallbackHandler(this.mWorker.getLooper());
        this.mHandler = callbackHandler;
        callbackHandler.sendEmptyMessage(MSG_START);
    }

    public void signalNext(RecordPath recordPath) {
        Message obtain = Message.obtain(this.mHandler, MSG_NEXT, recordPath);
        CallbackHandler callbackHandler = this.mHandler;
        Logger.e(mVideoCache.size()+"dsadsadasd-------------------------------------");
        if (callbackHandler != null && obtain != null) {
            callbackHandler.sendMessageAtFrontOfQueue(obtain);
        } else {
            Logger.d("can not start next one");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void next(RecordPath recordPath) {
        Logger.i("next :: dstPath = " + recordPath);
        this.mMuxerStarted = false;
        long currentTimeMillis = System.currentTimeMillis();
        writeCacheSample();
        Logger.d("Write cache sample used " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        this.mMuxer.release();
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onFileSaved(this.mDstPath.getUri());
        }
        this.mDstPath = recordPath;
        FileDescriptor fd = recordPath.getFd();
        if (fd == null) {
            throw new IllegalArgumentException();
        }
        this.mMuxer.setFd(fd);
        this.mMuxer.prepare();
        Logger.d("next :: video cache size =" + this.mVideoCache.size());
        Logger.d("next :: audio cache size =" + this.mAudioCache.size());
        startMuxerIfReady();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    private class CallbackHandler extends Handler {
        CallbackHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == MSG_START) {
                try {
                    ScreenRecorder.this.record();
                    if (ScreenRecorder.this.mCallback != null) {
                        ScreenRecorder.this.mCallback.onStart();
                    }
                } catch (Exception e) {
                    message.obj = e;
                    ScreenRecorder.this.handleError(message);
                }
            } else if (i != MSG_NEXT) {
                if (i == MSG_STOP || i == MSG_ERROR) {
                    ScreenRecorder.this.handleError(message);
                }
            } else {
                try {
                    ScreenRecorder.this.next(message.obj instanceof RecordPath ? (RecordPath) message.obj : null);
                } catch (Exception e2) {
                    message.obj = e2;
                    ScreenRecorder.this.handleError(message);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void handleError(Message message) {
        stopEncoders();
        if (message.arg1 != MSG_STOP) {
            signalEndOfStream();
        }
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onStop((Throwable) message.obj);
        }
        release();
    }

    @SuppressLint("WrongConstant")
    private void signalEndOfStream() {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer allocate = ByteBuffer.allocate(0);
        bufferInfo.set(0, 0, 0L, 4);
        Logger.d("Signal EOS to muxer ");
        int i = this.mVideoTrackIndex;
        if (i != INVALID_INDEX) {
            writeSampleData(i, bufferInfo, allocate);
        }
        int i2 = this.mAudioTrackIndex;
        if (i2 != INVALID_INDEX) {
            writeSampleData(i2, bufferInfo, allocate);
        }
        this.mVideoTrackIndex = INVALID_INDEX;
        this.mAudioTrackIndex = INVALID_INDEX;
    }

    private void record() {
        Logger.i("record :: mDstPa   th = " + this.mDstPath + ", " + Thread.currentThread());
        if (this.mIsRunning.get() || this.mForceQuit.get()) {
            throw new IllegalStateException();
        }
        this.mIsRunning.set(true);
        try {
            FileDescriptor fd = this.mDstPath.getFd();
            if (fd == null) {
                throw new IllegalArgumentException();
            }
            MuxerWrapper muxerWrapper = new MuxerWrapper(fd);
            this.mMuxer = muxerWrapper;
            muxerWrapper.prepare();
            prepareVideoEncoder();
            prepareAudioEncoder();
            this.mVirtualDisplay.setSurface(this.mVideoEncoder.getInputSurface());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void muxVideo(int i, MediaCodec.BufferInfo bufferInfo, String str) {
        if (!this.mIsRunning.get()) {
            Logger.w("muxVideo: Already stopped!");
        } else if (this.mVideoTrackIndex == INVALID_INDEX) {
            this.mPendingVideoEncoderBufferIndices.add(i);
            this.mPendingVideoEncoderBufferInfos.add(bufferInfo);
        } else {
            writeSampleData(this.mVideoTrackIndex, bufferInfo, this.mVideoEncoder.getOutputBuffer(i));
            this.mVideoEncoder.releaseOutputBuffer(i);
            if ((bufferInfo.flags & 4) != 0) {
                this.mVideoTrackIndex = INVALID_INDEX;
                signalStop(true);
            }
        }
    }

    private void muxAudio(int i, MediaCodec.BufferInfo bufferInfo) {
        if (!this.mIsRunning.get()) {
            Logger.w("muxAudio: Already stopped!");
        } else if (this.mAudioTrackIndex == INVALID_INDEX) {
            this.mPendingAudioEncoderBufferIndices.add(i);
            this.mPendingAudioEncoderBufferInfos.add(bufferInfo);
        } else {
            writeSampleData(this.mAudioTrackIndex, bufferInfo, this.mAudioEncoder.getOutputBuffer(i));
            this.mAudioEncoder.releaseOutputBuffer(i);
            if ((bufferInfo.flags & 4) != 0) {
                this.mAudioTrackIndex = INVALID_INDEX;
                signalStop(true);
            }
        }
    }

    private void writeSampleData(int i, MediaCodec.BufferInfo bufferInfo, ByteBuffer byteBuffer) {
        Callback callback;
        if ((bufferInfo.flags & STOP_WITH_EOS) != 0) {
            bufferInfo.size = 0;
        }
        boolean z = (bufferInfo.flags & 4) != 0;
        if (bufferInfo.size != 0 || z) {
            if (bufferInfo.presentationTimeUs != 0) {
                if (i == this.mVideoTrackIndex) {
                    resetVideoPts(bufferInfo);
                } else if (i == this.mAudioTrackIndex) {
                    resetAudioPts(bufferInfo);
                }
            }
            if (!z && (callback = this.mCallback) != null) {
                callback.onRecording(bufferInfo.presentationTimeUs);
            }
        } else {
            byteBuffer = null;
        }
        if (byteBuffer != null) {
            byteBuffer.position(bufferInfo.offset);
            byteBuffer.limit(bufferInfo.offset + bufferInfo.size);
            cacheSampleData(i, byteBuffer, bufferInfo);
        }
    }

    private static class SampleData {
        public ByteBuffer buffer;
        public MediaCodec.BufferInfo bufferInfo;
        public int trackIndex;

        SampleData(int i, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
            this.trackIndex = i;
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(byteBuffer.remaining());
            allocateDirect.put(byteBuffer);
            this.buffer = allocateDirect;
            this.bufferInfo = bufferInfo;
        }
    }

    private void cacheSampleData(int i, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        SampleData sampleData = new SampleData(i, byteBuffer, bufferInfo);
        if (i == this.mVideoTrackIndex) {
            this.mVideoCache.add(sampleData);
        } else if (i == this.mAudioTrackIndex) {
            this.mAudioCache.add(sampleData);
        }
        long j = sampleData.bufferInfo.presentationTimeUs;
        if (j > this.mDuration) {
            removeNoUseOldestFrame(this.mVideoCache, j);
            removeNoUseOldestFrame(this.mAudioCache, j);
        }
    }

    private void removeNoUseOldestFrame(LinkedBlockingQueue<SampleData> linkedBlockingQueue, long j) {
        for (SampleData next : linkedBlockingQueue) {
            if (j - next.bufferInfo.presentationTimeUs <= this.mDuration) {
                return;
            }
            next.buffer.clear();
            linkedBlockingQueue.remove(next);
        }
    }

    private void writeCacheSample() {
        Logger.i("Write cached sample data");
        Logger.d("video cache size: " + this.mVideoCache.size());
        for (SampleData next : this.mVideoCache) {
            this.mMuxer.writeSampleData(this.mVideoTrackIndex, next.buffer, next.bufferInfo);
        }
        Logger.d("audio cache size: " + this.mAudioCache.size());
        for (SampleData next2 : this.mAudioCache) {
            this.mMuxer.writeSampleData(this.mAudioTrackIndex, next2.buffer, next2.bufferInfo);
        }
        Logger.i("Write cached sample data finished");
    }

    private void resetAudioPts(MediaCodec.BufferInfo bufferInfo) {
        if (this.mAudioPtsOffset == 0) {
            this.mAudioPtsOffset = bufferInfo.presentationTimeUs;
            bufferInfo.presentationTimeUs = 0L;
            return;
        }
        bufferInfo.presentationTimeUs -= this.mAudioPtsOffset;
    }

    private void resetVideoPts(MediaCodec.BufferInfo bufferInfo) {
        if (this.mVideoPtsOffset == 0) {
            this.mVideoPtsOffset = bufferInfo.presentationTimeUs;
            bufferInfo.presentationTimeUs = 0L;
            return;
        }
        bufferInfo.presentationTimeUs -= this.mVideoPtsOffset;
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void resetVideoOutputFormat(MediaFormat mediaFormat) {
        this.mVideoOutputFormat = mediaFormat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void resetAudioOutputFormat(MediaFormat mediaFormat) {
        this.mAudioOutputFormat = mediaFormat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void startMuxerIfReady() {
        MediaFormat mediaFormat;
        Logger.i("startMuxerIfReady ::");
        if (this.mMuxerStarted || (mediaFormat = this.mVideoOutputFormat) == null) {
            return;
        }
        if (this.mAudioEncoder != null && this.mAudioOutputFormat == null) {
            return;
        }
        this.mVideoTrackIndex = this.mMuxer.addTrack(mediaFormat);
        this.mAudioTrackIndex = this.mAudioEncoder == null ? INVALID_INDEX : this.mMuxer.addTrack(this.mAudioOutputFormat);
        this.mMuxer.start();
        this.mMuxerStarted = true;
        if (this.mPendingVideoEncoderBufferIndices.isEmpty() && this.mPendingAudioEncoderBufferIndices.isEmpty()) {
            return;
        }
        while (true) {
            MediaCodec.BufferInfo poll = this.mPendingVideoEncoderBufferInfos.poll();
            if (poll == null) {
                break;
            }
            muxVideo(this.mPendingVideoEncoderBufferIndices.poll(), poll, "startMuxerIfReady");
        }
        if (this.mAudioEncoder == null) {
            return;
        }
        while (true) {
            MediaCodec.BufferInfo poll2 = this.mPendingAudioEncoderBufferInfos.poll();
            if (poll2 == null) {
                return;
            }
            muxAudio(this.mPendingAudioEncoderBufferIndices.poll(), poll2);
        }
    }

    private void prepareVideoEncoder() throws IOException {
        this.mVideoEncoder.setCallback(new BaseEncoder.Callback() { // from class: com.zui.game.service.highlight.ScreenRecorder.1
            boolean ranIntoError = false;

            @Override // com.zui.game.service.highlight.BaseEncoder.Callback
            public void onOutputBufferAvailable(BaseEncoder baseEncoder, int i, MediaCodec.BufferInfo bufferInfo) {
                try {
                    ScreenRecorder.this.muxVideo(i, bufferInfo, "onOutputBufferAvailable");
                } catch (Exception e) {
                    Logger.e("Muxer encountered an error! ", e);
                    Message.obtain(ScreenRecorder.this.mHandler, 3, e).sendToTarget();
                }
            }

            @Override // com.zui.game.service.highlight.Encoder.Callback
            public void onError(Encoder encoder, Exception exc) {
                this.ranIntoError = true;
                Logger.e("VideoEncoder ran into an error! ", exc);
                Message.obtain(ScreenRecorder.this.mHandler, 3, exc).sendToTarget();
            }

            @Override // com.zui.game.service.highlight.BaseEncoder.Callback
            public void onOutputFormatChanged(BaseEncoder baseEncoder, MediaFormat mediaFormat) {
                ScreenRecorder.this.resetVideoOutputFormat(mediaFormat);
                ScreenRecorder.this.startMuxerIfReady();
            }
        });
        this.mVideoEncoder.prepare();
    }

    private void prepareAudioEncoder() throws IOException {
        AudioRecorder audioRecorder = this.mAudioEncoder;
        if (audioRecorder == null) {
            return;
        }
        audioRecorder.setCallback(new BaseEncoder.Callback() { // from class: com.zui.game.service.highlight.ScreenRecorder.2
            boolean ranIntoError = false;

            @Override // com.zui.game.service.highlight.BaseEncoder.Callback
            public void onOutputBufferAvailable(BaseEncoder baseEncoder, int i, MediaCodec.BufferInfo bufferInfo) {
                try {
                    ScreenRecorder.this.muxAudio(i, bufferInfo);
                } catch (Exception e) {
                    Logger.e("Muxer encountered an error! ", e);
                    Message.obtain(ScreenRecorder.this.mHandler, 3, e).sendToTarget();
                }
            }

            @Override // com.zui.game.service.highlight.BaseEncoder.Callback
            public void onOutputFormatChanged(BaseEncoder baseEncoder, MediaFormat mediaFormat) {
                ScreenRecorder.this.resetAudioOutputFormat(mediaFormat);
                ScreenRecorder.this.startMuxerIfReady();
            }

            @Override // com.zui.game.service.highlight.Encoder.Callback
            public void onError(Encoder encoder, Exception exc) {
                this.ranIntoError = true;
                Logger.e("AudioRecorder ran into an error! ", exc);
                Message.obtain(ScreenRecorder.this.mHandler, 3, exc).sendToTarget();
            }
        });
        audioRecorder.prepare();
    }

    private void signalStop(boolean z) {
        this.mHandler.sendMessageAtFrontOfQueue(Message.obtain(this.mHandler, MSG_STOP, z ? MSG_STOP : MSG_START, MSG_START));
    }

    private void stopEncoders() {
        this.mIsRunning.set(false);
        this.mPendingAudioEncoderBufferInfos.clear();
        this.mPendingAudioEncoderBufferIndices.clear();
        this.mPendingVideoEncoderBufferInfos.clear();
        this.mPendingVideoEncoderBufferIndices.clear();
        try {
            VideoEncoder videoEncoder = this.mVideoEncoder;
            if (videoEncoder != null) {
                videoEncoder.stop();
            }
        } catch (IllegalStateException ignored) {
        }
        try {
            AudioRecorder audioRecorder = this.mAudioEncoder;
            if (audioRecorder != null) {
                audioRecorder.stop();
            }
        } catch (IllegalStateException ignored) {
        }
    }

    private void releaseVirtualDisplay() {
        VirtualDisplay virtualDisplay = this.mVirtualDisplay;
        if (virtualDisplay != null) {
            virtualDisplay.setSurface(null);
            this.mVirtualDisplay.release();
            this.mVirtualDisplay = null;
        }
    }

    private void release() {
        this.mAudioOutputFormat = null;
        this.mVideoOutputFormat = null;
        this.mAudioTrackIndex = INVALID_INDEX;
        this.mVideoTrackIndex = INVALID_INDEX;
        this.mMuxerStarted = false;
        releaseVirtualDisplay();
        HandlerThread handlerThread = this.mWorker;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mWorker = null;
        }
        VideoEncoder videoEncoder = this.mVideoEncoder;
        if (videoEncoder != null) {
            videoEncoder.release();
            this.mVideoEncoder = null;
        }
        AudioRecorder audioRecorder = this.mAudioEncoder;
        if (audioRecorder != null) {
            audioRecorder.release();
            this.mAudioEncoder = null;
        }
        MuxerWrapper muxerWrapper = this.mMuxer;
        if (muxerWrapper != null) {
            muxerWrapper.release();
            this.mMuxer = null;
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.onRelease(this.mDstPath.getUri());
            }
        }
        this.mHandler = null;
    }

    protected void finalize() {
        if (this.mVirtualDisplay != null) {
            Logger.e("release() not called");
            release();
        }
    }
}