package com.example.backrecord.service;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.icu.text.SimpleDateFormat;
import android.media.AudioAttributes;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.backrecord.R;
import com.example.backrecord.recorder.ScreenRecorder;
import com.example.backrecord.recorder.encoderconfig.AudioEncodeConfig;
import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;
import com.example.backrecord.recorder.encode_utils.DbHelper;
import com.example.backrecord.recorder.encode_utils.RecordPath;
import com.example.backrecord.recorder.encode_utils.RecorderHelper;

import java.util.Date;
import java.util.Objects;

/**
 * 该类封装了基本的截屏方法
 * 包括：
 *      服务的启动方法
 *      截屏的系列准备操作
 * 使用建议：
 *      1.继承该类，重写onCreate()和onStartCommand2()，按照你的需求定制功能
 *      2.启动Service可以调用prepareService()和startService()方法
 *      3.也可以通过启动一个独立的透明悬浮Activity来启动，参考ServiceStartActivity
 *      4.为mImageReader设置监听器可以读取每一帧图像，但是需要记得把Image.close()
 * 兼容性：
 *      本demo仅支持安卓11+设备，低版本请自行适配
 *
 */
public class CaptureScreenService extends Service {
    private static final String DATA = "data";
    private static final String RESULT_CODE = "result_code";

    public static final String VirtualDisplayName = "Highlight";

    protected VideoEncodeConfig videoEncodeConfig;
    protected MediaProjection mediaProjection;
    protected VirtualDisplay virtualDisplay;
    protected ScreenRecorder recorder;
    protected RecordPath recordPath;

    protected WindowManager mWindowManager;//createFloatWindow()方法自动创建其实例
    protected View rootView;

    private Service context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int mResultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
        Log.d(TAG, "onStartCommand: "+mResultCode);
        if (rootView == null) {
            //创建悬浮窗
            rootView = createFloatWindow(R.layout.float_window, R.id.float_video_img);
        }
        if (mResultCode == Activity.RESULT_OK) {//录屏服务开启
            //发布广播，这样才能创建MediaProjection
            notification();
            Intent mResultData = intent.getParcelableExtra(DATA);
            // 声明一个媒体投影管理器对象
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            mediaProjection = mediaProjectionManager.getMediaProjection(mResultCode, Objects.requireNonNull(mResultData));


        }

        onStartCommand2(intent);
        //如果该方法执行失败会再次重启服务执行（并将上次的intent再次传入，所以本方法永远不会接收到null的intent）
        return Service.START_REDELIVER_INTENT;
    }

    /**
     * 不建议继承onStartCommand()方法，这里提供一个方法代替之
     * @param intent startService时传入的intent
     */
    protected void onStartCommand2(Intent intent){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.quit();
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (mWindowManager != null && rootView != null) {
            mWindowManager.removeView(rootView);
        }
    }



    /**
     * 调用此方法请求录屏，如果申请权限成功，后续可以在onActivityResult()方法中调用startService()来启动本Service。
     * @param activity 只能是Activity
     * @param requestCode requestCode，将在onActivityResult()中用于检验
     */
    public static void prepareService(Activity activity,int requestCode) {
        Intent captureIntent = ((MediaProjectionManager) activity.getSystemService(MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent();
        activity.startActivityForResult(captureIntent,requestCode);
    }

    /**
     * 必须在onActivityResult()调用此方法启动该Service
     * @param activity 启动Service的上下文
     * @param intent 必须setComponent，指定你的 子类Service名字。若存入其他数据，可在onStartCommand2()中取出处理
     * @param resultCode 返回码
     * @param data 返回的数据
     * @return  是否成功启动Service
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean startService(Activity activity, Intent intent, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return false;
        }
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        ComponentName componentName = activity.startService(intent);
        return componentName != null;
    }

    public void notification() {
        //Call Start foreground with notification
        String NOTIFICATION_TICKER = "ticker";
        String NOTIFICATION_CHANNEL_ID = "channel_id";
        String NOTIFICATION_CHANNEL_NAME = "录屏服务通知";
        String NOTIFICATION_CHANNEL_DESC = "用于维持录屏";
        int NOTIFICATION_ID = 333;
        Intent notificationIntent = new Intent(this, this.getClass());
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("回录服务")
                .setContentText("正在录屏...")
                .setTicker(NOTIFICATION_TICKER)
                .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(NOTIFICATION_CHANNEL_DESC);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        startForeground(NOTIFICATION_ID, notification); //必须使用此方法显示通知，不能使用notificationManager.notify，否则还是会报上面的错误
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected ScreenRecorder getScreenRecorder() {
        videoEncodeConfig = getVideoEncodeConfig(isVertical(this));
        //虚拟屏幕通过MediaProjection获取，传入一系列传过来的参数
        //可能创建时会出错，捕获异常
        try {
            // 声明一个虚拟显示层对象
            virtualDisplay = mediaProjection.createVirtualDisplay(VirtualDisplayName, videoEncodeConfig.getWidth(), videoEncodeConfig.getHeight(), videoEncodeConfig.getDpi(),
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, null, null, null);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"virtualDisplay创建录屏异常，请退出重试！",Toast.LENGTH_SHORT).show();
        }
        RecordPath recordPath2 = getRecordPath();
        ScreenRecorder recorder =new ScreenRecorder(videoEncodeConfig, getDefaultAudioEncodeConfig(), recordPath2,virtualDisplay);
        recorder.setCallback(new ScreenRecorder.Callback() {
            @Override
            public void onFileSaved(Uri uri) {
                Log.d(TAG, "onFileSaved: "+uri.getEncodedPath());
                DbHelper.updatePendingZero(context,uri);
            }

            @Override
            public void onRecording(long j) {
            }

            @Override
            public void onRelease(Uri uri) {
                Log.d(TAG, "onRelease: ");
            }

            @Override
            public void onStart() {
                Log.d(TAG, "onStart: ");
            }

            @Override
            public void onStop(Throwable th) {
                Log.d(TAG, "onStop: "+ recordPath.getUri().getEncodedPath());
                DbHelper.deletePendingOne(context,recordPath.getUri());
            }
        });
        return recorder;
    }

    protected RecordPath getRecordPath() {
        StringBuilder builder = new StringBuilder();
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-hh时mm分ss秒");
        builder.append(simpleDateFormat.format(date)).append("-");
        int i1 = videoEncodeConfig.getHeight();
        int i2 = videoEncodeConfig.getWidth();
        builder.append(i1).append("x").append(i2).append("-");
        builder.append(videoEncodeConfig.getDuration()).append("s");
        recordPath = new RecordPath(this, DbHelper.createFileUri(this, builder.toString(), videoEncodeConfig, "video/mp4"));
        return recordPath;
    }

    protected AudioEncodeConfig getDefaultAudioEncodeConfig() {
        return AudioEncodeConfig.getNewSupportAudioEncodeConfig(this,
                new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                        .excludeUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .build());
    }

    /**
     * 得到VideoEncodeConfig实例
     * 从SharedPreferences中获取视频编码参数,创建实例。
     * 如果SharedPreferences中没有保存参数，则视频长宽使用设备屏幕长宽/1.25(864p)
     * 其余参数使用VideoEncodeConfig中的默认参数，并且在创建完成后自动保存参数到SharedPreferences
     * @param isVertical 是否竖屏
     * @return VideoEncodeConfig实例
     */
    protected VideoEncodeConfig getVideoEncodeConfig(boolean isVertical) {
        VideoEncodeConfig videoEncodeConfig = VideoEncodeConfig.Companion.readFromPrefs(this, isVertical ? 1 : 2);
        if (!RecorderHelper.getConfigPreferences(this).contains(VideoEncodeConfig.KEY_VIDEO_HEIGHT)) {
            VideoEncodeConfig.Companion.writeToPrefs(this,videoEncodeConfig);
        }
        return videoEncodeConfig;
    }

    //获取当前屏幕状态
    protected static boolean isVertical(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            return false;
            //其他情况均按竖屏处理
        } else return ori == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 创建一个可拖动的悬浮窗
     * @param rootViewXml 悬浮窗XML布局文件
     * @param rootViewId 根视图ID，用于实现拖动
     * @return 悬浮窗根View
     */
    private View createFloatWindow(@LayoutRes int rootViewXml, @IdRes int rootViewId) {

        View rootView= LayoutInflater.from(this).inflate(rootViewXml, null);
        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(rootView, params);
        //adding an touchListener to make drag movement of the floating widget
        final long[] add = {0,0};
        rootView.findViewById(rootViewId).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: "+add[0]);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setSelected(true);
                        add[0] = 0;
                        add[1] = System.currentTimeMillis();
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setSelected(false);
                        Log.d(TAG, "onStartCommand: onClick"+add[0]+" "+add[1]+" "+System.currentTimeMillis());
                        if (((System.currentTimeMillis()-add[1])<200)&&(add[0]<30)) {
                            if (recorder == null) {//没有开启录屏服务，去开启
                                initRecorder();
                                ((ImageView)rootView.findViewById(R.id.float_video_img)).setImageResource(R.drawable.back_record);
                            } else {//录制
                                recorder.signalNext(getRecordPath());
                                Toast.makeText(context, "视频保存中...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        int addY = (int) (event.getRawY() - initialTouchY);
                        int addX = (int) (event.getRawX() - initialTouchX);
                        add[0] += Math.abs(addY);
                        add[0] += Math.abs(addX);
                        params.x = initialX + addX;
                        params.y = initialY + addY;
                        mWindowManager.updateViewLayout(rootView, params);
                        return false;
                }
                return false;
            }
        });
        rootView.findViewById(R.id.float_video_img).setOnLongClickListener(v -> {
            Log.d(TAG, "onStartCommand: onLongClick"+add[0]);
            if (add[0] < 100) {
                context.stopSelf();
            }
            return false;
        });
        return rootView;
    }

    private void initRecorder() {
        recorder = getScreenRecorder();
        recorder.start();
    }


}
