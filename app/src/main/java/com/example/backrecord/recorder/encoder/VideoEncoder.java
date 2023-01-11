package com.example.backrecord.recorder.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;

import java.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public class VideoEncoder extends BaseEncoder {
    private static final boolean VERBOSE = false;
    private VideoEncodeConfig mConfig;
    private Surface mSurface;

    public VideoEncoder(VideoEncodeConfig videoEncodeConfig) {
        super(videoEncodeConfig.getCodecName());
        this.mConfig = videoEncodeConfig;
    }

    @Override // com.zui.game.service.highlight.BaseEncoder
    protected void onEncoderConfigured(MediaCodec mediaCodec) {
        this.mSurface = mediaCodec.createInputSurface();
    }

    @Override // com.zui.game.service.highlight.BaseEncoder
    protected MediaFormat createMediaFormat() {
        return this.mConfig.toFormat();
    }

    public Surface getInputSurface() {
        return (Surface) Objects.requireNonNull(this.mSurface, "doesn't prepare()");
    }

    @Override // com.zui.game.service.highlight.BaseEncoder, com.zui.game.service.highlight.Encoder
    public void release() {
        Surface surface = this.mSurface;
        if (surface != null) {
            surface.release();
            this.mSurface = null;
        }
        super.release();
    }
}
