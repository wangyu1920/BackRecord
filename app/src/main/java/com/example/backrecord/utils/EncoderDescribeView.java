package com.example.backrecord.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 该类继承TextView,自动SetText。展示设备支持的所有编码器的参数。
 */
public class EncoderDescribeView extends androidx.appcompat.widget.AppCompatTextView {

    boolean isSet=false;

    public EncoderDescribeView(@NonNull Context context) {
        super(context);
    }

    public EncoderDescribeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EncoderDescribeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setMediaCodecInfo() {
        MediaCodecInfo[] info1 = new MediaCodecList(MediaCodecList.REGULAR_CODECS).getCodecInfos();
        StringBuilder builder = new StringBuilder();
        for (MediaCodecInfo info : info1
        ) {
            if (!info.isEncoder()) {
                continue;
            }
            String[] supportedTypes = info.getSupportedTypes();
            String name = info.getName();
            boolean softwareOnly = info.isSoftwareOnly();
            boolean vendor = info.isVendor();
            boolean hardwareAccelerated = info.isHardwareAccelerated();
            builder.append("name: ").append(name).append("\n");
            builder.append("支持的编码格式:\n");
            for (String s : supportedTypes
            ) {
                builder.append(s).append("\n");
            }
            builder.append("硬件解码: ").append((!softwareOnly)).append("\n");
            builder.append("提供方: ").append(vendor ? "厂商" : "Android平台").append("\n");
            builder.append("硬件加速: ").append(hardwareAccelerated).append("\n").append("\n");
        }
        String text = builder.toString();
        setText(text);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isSet) {
            setMediaCodecInfo();
            isSet = true;
        }
        super.onDraw(canvas);
    }
}
