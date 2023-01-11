package com.example.backrecord.activities;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backrecord.R;
import com.example.backrecord.recorder.encode_utils.DbHelper;
import com.example.backrecord.recorder.encode_utils.RecorderHelper;
import com.example.backrecord.recorder.encoderconfig.VideoEncodeConfig;
import com.example.backrecord.utils.DocumentFileUtils;
import com.example.backrecord.utils.MyRecyclerviewAdapter;
import com.example.backrecord.utils.OpenFileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {


    private static final String FLAG = "param1";
    public static final int FLAG_SETTING = 0;
    public static final int FLAG_MOVIES = 1;
    public static final int FLAG_HELP = 2;
    private int mFlag;
    private int fileNum=-1;
    private File oldLastFile;

    private int mParam1;
    View rootView;
    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    public static BlankFragment newInstance(int param1) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(FLAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(FLAG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            switch (mParam1) {
                case FLAG_SETTING:
                    mFlag = FLAG_SETTING;
                    rootView = inflater.inflate(R.layout.fragment_setting, container, false);
                    initSettingPage();
                    break;
                case FLAG_MOVIES:
                    mFlag = FLAG_MOVIES;
                    rootView = inflater.inflate(R.layout.fragment_movies, container, false);
                    initMoviesPage();
                    break;
                case FLAG_HELP:
                    mFlag = FLAG_HELP;
                    rootView = inflater.inflate(R.layout.fragment_help, container, false);
                    break;
            }
        }
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        //因为文件列表是经常变动的，所以在此处判断
        if (mFlag == FLAG_MOVIES) {
            reFlashMoviesPage();
        }
    }

    //判断当前recyclerview状态是否需要更新并自动更新
    @SuppressLint("NotifyDataSetChanged")
    private void reFlashMoviesPage() {
        try {
            File[] fileLists = (File[]) DocumentFileUtils.getFileLists(getContext(), DbHelper.VIDEO_FOLDER_PATH);
            if (fileLists.length == 0) {
                return;
            }
            //如果当前文件列表数量与之前相同并且列表尾部文件也与之前相同，不刷新页面
            if (fileLists.length == fileNum&&fileLists[fileLists.length-1].equals(oldLastFile)) {
                return;
            }
        } catch (FileNotFoundException e) {
            //没找到文件，去申请文件读写权限
            e.printStackTrace();
            Toast.makeText(getContext(),"需要读写文件权限",Toast.LENGTH_SHORT).show();
            DocumentFileUtils.startForRoot$File(getActivity(), 1314);
            return;
        }
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_movies_recyclerview);
        //如果recyclerview已经设置了adapter，更新adapter的数据
        if (recyclerView.getAdapter()!= null) {
            try {
                MyRecyclerviewAdapter adapter = (MyRecyclerviewAdapter) recyclerView.getAdapter();
                (adapter).data = getDataList();
                adapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {//没有设置adapter，去初始化recyclerview
            initMoviesPage();
        }
    }

    //获取视频文件数组，并设置fileNum和oldLastFile
    private List<MyRecyclerviewAdapter.ItemData> getDataList() throws FileNotFoundException {
        File[] files = (File[]) DocumentFileUtils.getFileLists(getContext(), DbHelper.VIDEO_FOLDER_PATH);
        fileNum = files.length;
        if (fileNum == 0) {
            return new LinkedList<>();
        }
        oldLastFile = files[fileNum - 1];
        System.out.println(files.length);
        List<MyRecyclerviewAdapter.ItemData> dataList = new LinkedList<>();
        for (File file : files) {
            MyRecyclerviewAdapter.ItemData itemData = new MyRecyclerviewAdapter.ItemData(file);
            dataList.add(itemData);
        }
        return dataList;
    }

    //从头初始化recyclerview
    private void initMoviesPage() {
        try {
            File file = new File(DbHelper.VIDEO_FOLDER_PATH.replace("/document/primary:", "/storage/emulated/0/"));
            if (!file.exists()) {
                Toast.makeText(getContext(),"还没有录制视频",Toast.LENGTH_SHORT).show();
                return;
            }
            List<MyRecyclerviewAdapter.ItemData> dataList = getDataList();
            RecyclerView recyclerView = rootView.findViewById(R.id.fragment_movies_recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            MyRecyclerviewAdapter adapter = new MyRecyclerviewAdapter(dataList);
            adapter.setOnItemClickListener(new MyRecyclerviewAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    OpenFileUtils.openFile(getContext(), adapter.data.get(position).file);
                }

                @Override
                public void onLongClick(int position) {
                    new AlertDialog.Builder(getContext())
                        .setTitle("删除此视频？")
                        .setMessage(adapter.data.get(position).file.getName())
                        .setNegativeButton("取消", null)
                        .setPositiveButton("删除", (dialog1, which) -> {
                            boolean delete = adapter.data.get(position).file.delete();
                            if (delete) {
                                adapter.data.remove(position);
                                adapter.notifyItemRemoved(position);
                                try {
                                    File[] files = (File[]) DocumentFileUtils.getFileLists(getContext(), DbHelper.VIDEO_FOLDER_PATH);
                                    fileNum = files.length;
                                    oldLastFile = files[fileNum - 1];
                                    System.out.println(files.length);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(),"需要读写文件权限",Toast.LENGTH_SHORT).show();
                                    DocumentFileUtils.startForRoot$File(getActivity(), 1314);
                                }
                            } else {
                                Toast.makeText(getContext(), "删除失败!", Toast.LENGTH_SHORT).show();
                            }

                        }).show();
                }
            });
            recyclerView.setAdapter(adapter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"需要读写文件权限",Toast.LENGTH_SHORT).show();
            DocumentFileUtils.startForRoot$File(getActivity(), 1314);
        }
    }



    @SuppressLint("SetTextI18n")
    private void initSettingPage() {
        Context context=this.getContext();
        if (context == null) {
            return;
        }
        TextView textView,expectedVideoSize;
        EditText duration;
        EditText multiple;
        EditText bitrate;
        EditText frameRate;
        Button saveConfig;
        expectedVideoSize = rootView.findViewById(R.id.set_expect_video_size);
        textView = rootView.findViewById(R.id.main_activity_text_view);
        if (Settings.canDrawOverlays(context)) {
            textView.setText("权限授权完毕");
            textView.setTextColor(Color.GREEN);
        } else {
            textView.setText("没浮窗权限，点击授权");
            textView.setTextColor(Color.RED);
        }
        textView.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 2084);
            }
            if (Settings.canDrawOverlays(context)) {
                textView.setText("权限授权完毕");
                textView.setTextColor(Color.GREEN);
            } else {
                textView.setText("没浮窗权限，点击授权");
                textView.setTextColor(Color.RED);
            }
        });
        duration = rootView.findViewById(R.id.set_duration);
        multiple = rootView.findViewById(R.id.set_multiple);
        bitrate = rootView.findViewById(R.id.set_bitrate);
        frameRate = rootView.findViewById(R.id.set_frame_rate);
        duration.addTextChangedListener(new TextChangeListener(expectedVideoSize,bitrate,duration));
        bitrate.addTextChangedListener(new TextChangeListener(expectedVideoSize,bitrate,duration));
        SharedPreferences configPreferences = RecorderHelper.getConfigPreferences(context);
        int i6 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_BITRATE, VideoEncodeConfig.DEFAULT_VIDEO_BITRATE);
        int i7 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_FRAME_RATE, 30);
        int i10 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_DURATION, 60);
//        -------------------------
        expectedVideoSize.setText("预计文件大小："+ i6 * i10 / (8 * 1024 * 1024) +"MB");
//        -------------------------
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        int width = windowManager.getMaximumWindowMetrics().getBounds().width();
        int height = windowManager.getMaximumWindowMetrics().getBounds().height();
        int theShort = Math.min(width, height);
        int i4 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, (int) (width/VideoEncodeConfig.DEFAULT_VIDEO_MULTIPLE));
        int i5 = configPreferences.getInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, (int) (height/VideoEncodeConfig.DEFAULT_VIDEO_MULTIPLE));
        int theSaveShort = Math.min(i4, i5);
        float multiple1 = ((float) theShort) / theSaveShort;
        bitrate.setText(String.valueOf(i6));
        frameRate.setText(String.valueOf(i7));
        duration.setText(String.valueOf(i10));
        multiple.setText(String.valueOf(multiple1));
        saveConfig = rootView.findViewById(R.id.set_save_config);
        saveConfig.setOnClickListener(v -> {
            int bitrate1 = Integer.parseInt(bitrate.getText().toString());
            int frameRate1 = Integer.parseInt(frameRate.getText().toString());
            int duration1 = Integer.parseInt(duration.getText().toString());
            float multiple2 = Float.parseFloat(multiple.getText().toString());
            int s1= (int) (width/multiple2);
            int s2= (int) (height/multiple2);
            configPreferences.edit().putInt(VideoEncodeConfig.KEY_VIDEO_WIDTH, s1)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_HEIGHT, s2)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_BITRATE, bitrate1)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_FRAME_RATE,frameRate1)
                    .putInt(VideoEncodeConfig.KEY_VIDEO_DURATION,duration1)
                    .apply();
        });
    }

    private static class TextChangeListener implements TextWatcher {
        TextView textView;
        EditText editText1, editText2;

        public TextChangeListener(TextView textView, EditText editText1, EditText editText2) {
            this.textView = textView;
            this.editText1 = editText1;
            this.editText2 = editText2;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {
            String editText2Text = editText2.getText().toString();
            if (editText2Text.equals("")) {
                return;
            }
            int bitrate1 = Integer.parseInt(editText1.getText().toString());
            int duration1 = Integer.parseInt(editText2.getText().toString());
            if (bitrate1 > 0 && duration1 > 0) {
                textView.setText("预计文件大小：" + bitrate1 * duration1 / (8 * 1024 * 1024) + "MB");
            } else {
                textView.setText("非法参数！");
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
