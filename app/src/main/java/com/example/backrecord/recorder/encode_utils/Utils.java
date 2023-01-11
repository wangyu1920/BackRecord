package com.example.backrecord.recorder.encode_utils;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.AsyncTask;
import android.util.SparseArray;
import java.lang.reflect.Field;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public class Utils {
    static SparseArray<String> sAACProfiles = new SparseArray<>();
    static SparseArray<String> sAVCProfiles = new SparseArray<>();
    static SparseArray<String> sAVCLevels = new SparseArray<>();
    static SparseArray<String> sColorFormats = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    public interface Callback {
        void onResult(MediaCodecInfo[] mediaCodecInfoArr);
    }

    Utils() {
    }

    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    static final class EncoderFinder extends AsyncTask<String, Void, MediaCodecInfo[]> {
        private Callback func;

        EncoderFinder(Callback callback) {
            this.func = callback;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public MediaCodecInfo[] doInBackground(String... strArr) {
            return Utils.findEncodersByType(strArr[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(MediaCodecInfo[] mediaCodecInfoArr) {
            this.func.onResult(mediaCodecInfoArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void findEncodersByTypeAsync(String str, Callback callback) {
        new EncoderFinder(callback).execute(str);
    }

    public static MediaCodecInfo[] findEncodersByType(String str) {
        MediaCodecInfo[] codecInfos;
        MediaCodecList mediaCodecList = new MediaCodecList(1);
        ArrayList arrayList = new ArrayList();
        for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
            if (mediaCodecInfo.isEncoder()) {
                try {
                    if (mediaCodecInfo.getCapabilitiesForType(str) != null) {
                        arrayList.add(mediaCodecInfo);
                    }
                } catch (IllegalArgumentException unused) {
                }
            }
        }
        return (MediaCodecInfo[]) arrayList.toArray(new MediaCodecInfo[arrayList.size()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String avcProfileLevelToString(MediaCodecInfo.CodecProfileLevel codecProfileLevel) {
        if (sAVCProfiles.size() == 0 || sAVCLevels.size() == 0) {
            initProfileLevels();
        }
        int indexOfKey = sAVCProfiles.indexOfKey(codecProfileLevel.profile);
        String valueAt = indexOfKey >= 0 ? sAVCProfiles.valueAt(indexOfKey) : null;
        int indexOfKey2 = sAVCLevels.indexOfKey(codecProfileLevel.level);
        String valueAt2 = indexOfKey2 >= 0 ? sAVCLevels.valueAt(indexOfKey2) : null;
        if (valueAt == null) {
            valueAt = String.valueOf(codecProfileLevel.profile);
        }
        if (valueAt2 == null) {
            valueAt2 = String.valueOf(codecProfileLevel.level);
        }
        return valueAt + '-' + valueAt2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] aacProfiles() {
        if (sAACProfiles.size() == 0) {
            initProfileLevels();
        }
        String[] strArr = new String[sAACProfiles.size()];
        for (int i = 0; i < sAACProfiles.size(); i++) {
            strArr[i] = sAACProfiles.valueAt(i);
        }
        return strArr;
    }

    public static MediaCodecInfo.CodecProfileLevel toProfileLevel(String str) {
        String str2;
        if (sAVCProfiles.size() == 0 || sAVCLevels.size() == 0 || sAACProfiles.size() == 0) {
            initProfileLevels();
        }
        int indexOf = str.indexOf(45);
        if (indexOf > 0) {
            String substring = str.substring(0, indexOf);
            str2 = str.substring(indexOf + 1);
            str = substring;
        } else {
            str2 = null;
        }
        MediaCodecInfo.CodecProfileLevel codecProfileLevel = new MediaCodecInfo.CodecProfileLevel();
        if (str.startsWith("AVC")) {
            codecProfileLevel.profile = keyOfValue(sAVCProfiles, str);
        } else if (str.startsWith("AAC")) {
            codecProfileLevel.profile = keyOfValue(sAACProfiles, str);
        } else {
            try {
                codecProfileLevel.profile = Integer.parseInt(str);
            } catch (NumberFormatException unused) {
                return null;
            }
        }
        if (str2 != null) {
            if (str2.startsWith("AVC")) {
                codecProfileLevel.level = keyOfValue(sAVCLevels, str2);
            } else {
                try {
                    codecProfileLevel.level = Integer.parseInt(str2);
                } catch (NumberFormatException unused2) {
                    return null;
                }
            }
        }
        if (codecProfileLevel.profile <= 0 || codecProfileLevel.level < 0) {
            return null;
        }
        return codecProfileLevel;
    }

    private static <T> int keyOfValue(SparseArray<T> sparseArray, T t) {
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            T valueAt = sparseArray.valueAt(i);
            if (valueAt == t || valueAt.equals(t)) {
                return sparseArray.keyAt(i);
            }
        }
        return -1;
    }

    private static void initProfileLevels() {
        Field[] fields;
        SparseArray<String> sparseArray = null;
        for (Field field : MediaCodecInfo.CodecProfileLevel.class.getFields()) {
            if ((field.getModifiers() & 24) != 0) {
                String name = field.getName();
                if (name.startsWith("AVCProfile")) {
                    sparseArray = sAVCProfiles;
                } else if (name.startsWith("AVCLevel")) {
                    sparseArray = sAVCLevels;
                } else if (name.startsWith("AACObject")) {
                    sparseArray = sAACProfiles;
                }
                try {
                    sparseArray.put(field.getInt(null), name);
                } catch (IllegalAccessException unused) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String toHumanReadable(int i) {
        if (sColorFormats.size() == 0) {
            initColorFormatFields();
        }
        int indexOfKey = sColorFormats.indexOfKey(i);
        if (indexOfKey >= 0) {
            return sColorFormats.valueAt(indexOfKey);
        }
        return "0x" + Integer.toHexString(i);
    }

    static int toColorFormat(String str) {
        if (sColorFormats.size() == 0) {
            initColorFormatFields();
        }
        int keyOfValue = keyOfValue(sColorFormats, str);
        if (keyOfValue > 0) {
            return keyOfValue;
        }
        if (str.startsWith("0x")) {
            return Integer.parseInt(str.substring(2), 16);
        }
        return 0;
    }

    private static void initColorFormatFields() {
        Field[] fields;
        for (Field field : MediaCodecInfo.CodecCapabilities.class.getFields()) {
            if ((field.getModifiers() & 24) != 0) {
                String name = field.getName();
                if (name.startsWith("COLOR_")) {
                    try {
                        sColorFormats.put(field.getInt(null), name);
                    } catch (IllegalAccessException unused) {
                    }
                }
            }
        }
    }
}