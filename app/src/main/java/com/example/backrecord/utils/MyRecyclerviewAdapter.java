package com.example.backrecord.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backrecord.R;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MyRecyclerviewAdapter extends RecyclerView.Adapter<MyRecyclerviewAdapter.ViewHolder> {

    private static final String TAG = "MyRecyclerviewAdapter";
    public List<ItemData> data;
    private final MyHandler myHandler;

    public MyRecyclerviewAdapter(List<ItemData> data) {
        this.data = data;
        Log.d(TAG, "MyRecyclerviewAdapter: "+data.size());
        myHandler = new MyHandler( new CancellationSignal());
    }

    /**
     * 这个方法用于创建条目的View
     *
     * @param parent   根据RecyclerView设置的LayoutManager生成的ViewGroup
     * @param viewType 视图类型，因为RecyclerView不同于ListView,可以设置不同类型的
     *                 ItemView,此变量用于区分不同的视图类型。viewType是自己定义的，
     *                 需要重写 public int getItemViewType(int position)方法。
     *                 例如：对于QQ空间，有些是文字加配图的视图，有些是短视频视图，创建
     *                 他们对应的ViewHolder的方式肯定是不同的，此处用viewType加以区分
     * @return 你定义的ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.recyclerview_item, null);
        return new ViewHolder(v);
    }

    /**
     * 当创建好了ViewHolder之后将调用此方法绑定ViewHolder中的View的数据
     *
     * @param holder   holder
     * @param position position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(data.get(position),position);
        //这里是设置onClickListener后面加的代码
        holder.initOnClickListener(onItemClickListener);
        Log.d(TAG, "onBindViewHolder: "+position);

    }


    //返回条目个数
    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        Log.e(TAG, "getItemCount: 0" );
        return 0;
    }


    /**
     * RecyclerView是由一个个子条目Item组成的，子条目由一个装有ViewHolder的ViewGroup容器
     * 管理，ViewHolder中又装着一个View，这个View是你自定义的，由ViewHolder保存管理，你需要
     * 自定义ViewHolder实现对你的条目Item中内容的管理
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView videoName,videoSize;
        View itemView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.item_image);
            videoName = itemView.findViewById(R.id.item_text);
            videoSize = itemView.findViewById(R.id.item_text2);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemData itemData,int position) {
            myHandler.setVideoThumbnail(itemData,position,imageView);
            videoName.setText(itemData.file.getName());
            videoSize.setText(FileSizeUtil.getAutoFileOrFilesSize(itemData.file.getPath()));
        }


        //对相应的控件设置点击监听器
        public void initOnClickListener(OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(this.getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onLongClick(this.getAdapterPosition());
                }
                return false;
            });
        }
    }

    /**
     * 设置点击监听器
     */
    public interface OnItemClickListener {
        void onClick(int position);
        void onLongClick(int position);
    }


    //创建实例
    OnItemClickListener onItemClickListener;

    //创建对外设置监听的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    private static class MyHandler extends Handler {
        public final int MSG_START = -1;
        public final int MSG_FINISH = -2;
        public final int MSG_CANCEL = -3;
        public final int MSG_FAILED = -4;

        private final CancellationSignal cancellationSignal;

        public MyHandler(CancellationSignal cancellationSignal) {
            super(Looper.getMainLooper());
            this.cancellationSignal = cancellationSignal;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.arg1) {

                case MSG_START://arg2=index;obj=Object[]{File,ImageView}
                    onStart(msg);
                    break;
                case MSG_CANCEL:
                    cancellationSignal.cancel();
                    break;
                case MSG_FAILED://arg2=index;obj=Exception
                    ((Exception)msg.obj).printStackTrace();
                    break;
                case MSG_FINISH://obj=Object[]{Bitmap,ImageView}
                    Log.d(TAG, "handleMessage: finish");
                    onFinish(msg);
                    break;
                default:
            }
        }

        private void onFinish(Message msg) {
            ImageView imageView = (ImageView)((Object[]) msg.obj)[1];
            Bitmap bitmap = (Bitmap) ((Object[]) msg.obj)[0];
            imageView.setImageBitmap(bitmap);
        }

        public void setVideoThumbnail(ItemData itemData,int position, ImageView imageView) {
            Message message = Message.obtain();
            message.arg1 = MSG_START;
            message.arg2 = position;
            message.obj = new Object[]{
                    itemData.file, imageView
            };
            sendMessageDelayed(message,50);
        }


        private void onStart(Message msg) {
            File file = (File)((Object[]) msg.obj)[0];
            ImageView imageView = (ImageView)((Object[]) msg.obj)[1];
            int height = imageView.getHeight();
            int width = imageView.getWidth();
            Message message = obtainMessage();
            new Thread(() -> {
                try {
                    Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(file, new Size(width, height), cancellationSignal);
                    message.arg1 = MSG_FINISH;
                    message.obj = new Object[]{videoThumbnail, imageView};
                } catch (IOException e) {
                    message.obj = e;
                    message.arg1 = MSG_FAILED;
                    message.arg2 = msg.arg2;
                }finally {
                    sendMessage(message);
                }
            }).start();

        }
    }
    //修改onBindViewHolder方法,并且在ViewHolder中设置对应的方法，对相应的View设置相应的onClickListener

    public static class ItemData {
        public ItemData(File file) {
            this.file = file;
        }

        public File file;
    }


}
