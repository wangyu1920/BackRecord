package com.example.backrecord.recorder.encoder;

import android.media.MediaFormat;

import com.example.backrecord.recorder.encoderconfig.AudioEncodeConfig;

/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public class AudioEncoder extends BaseEncoder {
    private final AudioEncodeConfig mConfig;

    public AudioEncoder(AudioEncodeConfig audioEncodeConfig) {
        super(audioEncodeConfig.getCodecName());
        this.mConfig = audioEncodeConfig;
    }

    @Override // com.zui.game.service.highlight.BaseEncoder
    protected MediaFormat createMediaFormat() {
        return this.mConfig.toFormat();
    }
}