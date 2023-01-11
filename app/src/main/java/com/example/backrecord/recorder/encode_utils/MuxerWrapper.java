package com.example.backrecord.recorder.encode_utils;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;

import kotlin.jvm.internal.Intrinsics;

/**
 * 用来管理Muxer的类
 * MediaCodec的输出流写入Muxer封装成mp4视频文件
 */
public final class MuxerWrapper {
    private FileDescriptor fd;
    private MediaMuxer muxer;

    public MuxerWrapper(FileDescriptor fileDescriptor) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        this.fd = fileDescriptor;
    }

    public final FileDescriptor getFd() {
        return this.fd;
    }

    public final void setFd(FileDescriptor fileDescriptor) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "<set-?>");
        this.fd = fileDescriptor;
    }

    public final void prepare() {
        try {
            this.muxer = new MediaMuxer(this.fd, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void start() {
        MediaMuxer mediaMuxer = this.muxer;
        if (mediaMuxer == null) {
            return;
        }
        mediaMuxer.start();
    }

    public final int addTrack(MediaFormat mediaFormat) {
        Intrinsics.checkNotNullParameter(mediaFormat, "format");
        MediaMuxer mediaMuxer = this.muxer;
        if (mediaMuxer == null) {
            return -1;
        }
        return mediaMuxer.addTrack(mediaFormat);
    }

    public final void writeSampleData(int i, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        Intrinsics.checkNotNullParameter(byteBuffer, "byteBuf");
        Intrinsics.checkNotNullParameter(bufferInfo, "bufferInfo");
        MediaMuxer mediaMuxer = this.muxer;
        if (mediaMuxer == null) {
            return;
        }
        mediaMuxer.writeSampleData(i, byteBuffer, bufferInfo);
    }

    public final void release() {
        try {
            MediaMuxer mediaMuxer = this.muxer;
            if (mediaMuxer != null) {
                mediaMuxer.stop();
            }
            MediaMuxer mediaMuxer2 = this.muxer;
            if (mediaMuxer2 != null) {
                mediaMuxer2.release();
            }
        } catch (Exception unused) {
        }
        this.muxer = null;
    }
}