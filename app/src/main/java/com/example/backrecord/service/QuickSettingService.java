package com.example.backrecord.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.example.backrecord.activities.ServiceStartActivity;

import java.util.List;

public class QuickSettingService extends TileService {
    public QuickSettingService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        if (isServiceRunning(this, "MyCaptureService")) {
            setLabel("停止回录");
        } else {
            setLabel("开始回录");
        }
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {

        Log.d("QuickStartService", "onClick: ");
        if (isServiceRunning(this, "MyCaptureService")) {
            stopService(new Intent(this, MyCaptureService.class));
            setLabel("开始回录");

        } else {
            startActivityAndCollapse(new Intent(this, ServiceStartActivity.class).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            ));
        }


    }

    private void setLabel(String label) {
        Tile qsTile = getQsTile();
        qsTile.setLabel(label);
        qsTile.updateTile();
    }

    /**
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(300);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：",className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：",serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }




}