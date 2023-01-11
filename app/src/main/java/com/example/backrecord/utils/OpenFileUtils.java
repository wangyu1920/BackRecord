package com.example.backrecord.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.backrecord.BuildConfig;

import java.io.File;


/** 该类用于打开各种文件，手机会自动选择对应的工具打开对应的文件格式
 *
 * 用例：OpenFileUtils.openFile(context,file);
 *
 * 使用该类需要在AndroidManifest中添加provider:
 *      <provider
 *             android:name="androidx.core.content.FileProvider"
 *             android:authorities="${applicationId}"
 *             android:exported="false"
 *             android:grantUriPermissions="true">
 *             <meta-data
 *                 android:name="android.support.FILE_PROVIDER_PATHS"
 *                 android:resource="@xml/file_paths_public" />
 *         </provider>
 *
 * 并且建立xml文件夹并创建名为file_paths_public.xml的文件，内容为：
 *      <?xml version="1.0" encoding="utf-8"?>
 *      <paths xmlns:android="http://schemas.android.com/apk/res/android">
 *          <external-path path="." name="external_storage_root" />
 *      </paths>
 *
 */
public class OpenFileUtils {
    /**
     * 声明各种类型文件的dataType
     **/
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";

    private static final String DATA_TYPE_VIDEO = "video/*";

    private static final String DATA_TYPE_AUDIO = "audio/*";

    private static final String DATA_TYPE_HTML = "text/html";

    private static final String DATA_TYPE_IMAGE = "image/*";

    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";

    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";

    private static final String DATA_TYPE_WORD = "application/msword";

    private static final String DATA_TYPE_CHM = "application/x-chm";

    private static final String DATA_TYPE_TXT = "text/plain";

    private static final String DATA_TYPE_PDF = "application/pdf";

    /**
     * 未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
     */
    private static final String DATA_TYPE_ALL = "*/*";

    /**
     * 打开文件
     * @param mContext Context
     * @param file File
     */
    @RequiresApi(24)
    public static void openFile(Context mContext, File file) {
        if (file == null || mContext == null) {
            return;
        }
        if (!file.exists()) {
            return;

        }

        // 取得文件扩展名
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

        // 依扩展名的类型决定MimeType
        switch (end) {
            case "3gp":

            case "mp4":

                openVideoFileIntent(mContext, file);

                break;

            case "m4a":

            case "mp3":

            case "mid":

            case "xmf":

            case "ogg":

            case "wav":

                openAudioFileIntent(mContext, file);

                break;

            case "doc":

            case "docx":

                commonOpenFileWithType(mContext, file, DATA_TYPE_WORD);

                break;

            case "xls":

            case "xlsx":

                commonOpenFileWithType(mContext, file, DATA_TYPE_EXCEL);

                break;

            case "jpg":

            case "gif":

            case "png":

            case "jpeg":

            case "bmp":

                commonOpenFileWithType(mContext, file, DATA_TYPE_IMAGE);

                break;

            case "txt":

                commonOpenFileWithType(mContext, file, DATA_TYPE_TXT);

                break;

            case "htm":

            case "html":

                commonOpenFileWithType(mContext, file, DATA_TYPE_HTML);

                break;

            case "apk":

                commonOpenFileWithType(mContext, file, DATA_TYPE_APK);

                break;

            case "ppt":

                commonOpenFileWithType(mContext, file, DATA_TYPE_PPT);

                break;

            case "pdf":

                commonOpenFileWithType(mContext, file, DATA_TYPE_PDF);

                break;

            case "chm":

                commonOpenFileWithType(mContext, file, DATA_TYPE_CHM);

                break;

            default:

                commonOpenFileWithType(mContext, file, DATA_TYPE_ALL);

                break;

        }

    }


    private static void commonOpenFileWithType(Context mContext, File file, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.addCategory(Intent.CATEGORY_DEFAULT);

        FileProviderUtils.setIntentDataAndType(mContext, intent, type, file, true);

        mContext.startActivity(intent);

    }



    private static void openVideoFileIntent(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("oneshot", 0);

        intent.putExtra("configchange", 0);

        FileProviderUtils.setIntentDataAndType(mContext, intent, DATA_TYPE_VIDEO, file, false);

        mContext.startActivity(intent);

    }



    private static void openAudioFileIntent(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("oneshot", 0);

        intent.putExtra("configchange", 0);

        FileProviderUtils.setIntentDataAndType(mContext, intent, DATA_TYPE_AUDIO, file, false);

        mContext.startActivity(intent);

    }


    private static class FileProviderUtils {
        private static Uri getUriForFile(Context mContext, File file) {
            Uri fileUri;

            fileUri = getUriForFile24(mContext, file);

            return fileUri;

        }

        private static Uri getUriForFile24(Context mContext, File file) {

            return FileProvider.getUriForFile(mContext,

                    BuildConfig.APPLICATION_ID,

                    file);

        }

        public static void setIntentDataAndType(Context mContext,

                                                Intent intent,

                                                String type,

                                                File file,

                                                boolean writeAble) {
            intent.setDataAndType(getUriForFile(mContext, file), type);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            }

        }

    }


}