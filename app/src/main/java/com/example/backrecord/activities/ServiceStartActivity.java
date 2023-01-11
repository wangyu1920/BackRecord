package com.example.backrecord.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.backrecord.R;
import com.example.backrecord.service.MyCaptureService;

/**
 * 想要在Service中启动录屏功能，需要用service启动一个Activity
 * 设置好Activity的Theme，并且重写onAttachedToWindow()，使其悬浮透明
 * 在这个Activity的onCreate中prepareService
 * 在onActivityResult中startService，并finish()即可
 */
public class ServiceStartActivity extends AppCompatActivity {
    Activity activity;
    private static final String TAG = "ServiceStartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_start);
        //申请录屏权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, 2);
        }
        //启动服务
        new Handler().postDelayed(() -> MyCaptureService.prepareService(activity,1), 100);

    }

    //让Activity大小为0
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        lp.width = 0;
        lp.height = 0;
        getWindowManager().updateViewLayout(view,lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            boolean b = MyCaptureService.startService(this, new Intent(this, MyCaptureService.class), resultCode, data);
            if (b) {
                Log.d(TAG, "onActivityResult: startService successfully!");
            } else {
                Log.e(TAG, "onActivityResult: startService failed!" );
            }
            finish();
        }
    }

}