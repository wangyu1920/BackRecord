package com.example.backrecord;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;
import com.example.backrecord.recorder.utils.RecorderHelper;


public class MainActivity extends AppCompatActivity {
    Context context;
    TextView textView;
    EditText duration;
    EditText multiple;
    EditText bitrate;
    EditText frameRate;
    EditText mimeType;
    Button saveConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        textView = findViewById(R.id.main_activity_text_view);
        textView.setText(judgePermission());
        textView.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 2084);
            }
            textView.setText(judgePermission());
        });
        initConfigViews();
    }

    private void initConfigViews() {
        duration = ((EditText) findViewById(R.id.set_duration));
        multiple = ((EditText) findViewById(R.id.set_multiple));
        bitrate = ((EditText) findViewById(R.id.set_bitrate));
        frameRate = ((EditText) findViewById(R.id.set_frame_rate));
        mimeType = ((EditText) findViewById(R.id.set_mime_type));
        SharedPreferences configPreferences = RecorderHelper.getConfigPreferences(context);
        String string2 = configPreferences.getString(VideoEncodeConfig.KEY_VIDEO_MIME_TYPE, "video/avc");
        int i6 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_BITRATE, VideoEncodeConfig.DEFAULT_VIDEO_BITRATE);
        int i7 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_FRAME_RATE, 30);
        int i10 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_DURATION, 60);
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        int width = (int) (windowManager.getMaximumWindowMetrics().getBounds().width());
        int height = (int) (windowManager.getMaximumWindowMetrics().getBounds().height());
        int theShort = Math.min(width, height);
        int i4 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, (int) (width/1.5));
        int i5 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, (int) (height/1.5));
        int theSaveShort = Math.min(i4, i5);
        float multiple1 = ((float) theShort) / theSaveShort;
        bitrate.setText(String.valueOf(i6));
        mimeType.setText(string2);
        frameRate.setText(String.valueOf(i7));
        duration.setText(String.valueOf(i10));
        multiple.setText(String.valueOf(multiple1));
        saveConfig = findViewById(R.id.set_save_config);
        saveConfig.setOnClickListener(v -> {
            int bitrate1 = Integer.parseInt(bitrate.getText().toString());
            int frameRate1 = Integer.parseInt(frameRate.getText().toString());
            int duration1 = Integer.parseInt(duration.getText().toString());
            float multiple2 = Float.parseFloat(multiple.getText().toString());
            String mimeType1 = mimeType.getText().toString();
            int s1= (int) (width/multiple2);
            int s2= (int) (height/multiple2);
            configPreferences.edit().putInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, s1)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, s2)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_BITRATE, bitrate1)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_FRAME_RATE,frameRate1)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_DURATION,duration1)
                    .putString(VideoEncodeConfig.KEY_VIDEO_MIME_TYPE,mimeType1)
                    .apply();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("", "onPause: ");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    public String judgePermission() {
        //判断安卓版本
        //需要申请的权限
//        String [] permission={
//        };
//        String[] desc={
//                "存储权限"
//        };
        boolean isAllGranted=true;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("需要以下权限：\n");
//        for(int i=0;i<permission.length;i++){
//            //判断是否有权限
//            if(this.checkSelfPermission(permission[i]) != PackageManager.PERMISSION_GRANTED){
//                stringBuilder.append(desc[i]).append("\n");
//                isAllGranted = false;
//            }
//        }
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            stringBuilder.append("悬浮窗权限").append("\n");
            isAllGranted = false;
        }
        stringBuilder.append("点击进行授权");
        return isAllGranted?"所需权限已授予":stringBuilder.toString();
    }



}
