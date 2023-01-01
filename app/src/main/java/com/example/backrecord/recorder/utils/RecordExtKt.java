package com.example.backrecord.recorder.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RecordExt.kt */
/* loaded from: classes5.dex */
public final class RecordExtKt {
    private static final String TAG = "VideoListExt";

    public static final Map<Long, ArrayList<VideoItem>> groupByTimeSeconds(List<? extends VideoItem> list, long j) {
        Intrinsics.checkNotNullParameter(list, "<this>");
        long dateAdded = list.get(0).getDateAdded();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (VideoItem videoItem : list) {
            if (linkedHashMap.isEmpty()) {
                linkedHashMap.put(Long.valueOf(dateAdded), CollectionsKt.arrayListOf(videoItem));
            } else {
                ArrayList arrayList = (ArrayList) linkedHashMap.get(Long.valueOf(dateAdded));
                if (dateAdded - videoItem.getDateAdded() <= j) {
                    Intrinsics.checkNotNull(arrayList);
                    arrayList.add(videoItem);
                } else {
                    dateAdded -= ((dateAdded - videoItem.getDateAdded()) / j) * j;
                    linkedHashMap.put(Long.valueOf(dateAdded), CollectionsKt.arrayListOf(videoItem));
                }
            }
        }
        return linkedHashMap;
    }
//
//    public static final List<VideoItem> expand(List<? extends VideoItem> list) {
//        Intrinsics.checkNotNullParameter(list, "<this>");
//        ArrayList arrayList = new ArrayList();
//        for (VideoItem videoItem : list) {
//            if (videoItem instanceof VideoBurst) {
//                if (videoItem.getChecked()) {
//                    ((VideoBurst) videoItem).getItems().get(0).setChecked(true);
//                }
//                arrayList.addAll(((VideoBurst) videoItem).getItems());
//            } else {
//                arrayList.add(videoItem);
//            }
//        }
//        return arrayList;
//    }
//
//    public static final List<VideoItem> collapseByTimeMills(List<? extends VideoItem> list, long j) {
//        Object createVideoBurst;
//        VideoItem videoItem;
//        Intrinsics.checkNotNullParameter(list, "<this>");
//        ArrayList arrayList = new ArrayList();
//        for (VideoItem videoItem2 : list) {
//            if (arrayList.isEmpty()) {
//                arrayList.add(CollectionsKt.arrayListOf(videoItem2));
//            } else {
//                Object obj = arrayList.get(arrayList.size() - 1);
//                Intrinsics.checkNotNullExpressionValue(obj, "acc[acc.size - 1]");
//                ArrayList arrayList2 = (ArrayList) obj;
//                long j2 = 1000;
//                long abs = Math.abs(((videoItem2.getDateModified() * j2) - videoItem2.getDuration()) - (((VideoItem) CollectionsKt.last((List<? extends Object>) arrayList2)).getDateModified() * j2));
//                Logger.d('[' + videoItem.getId() + Soundex.SILENT_MARKER + videoItem2.getId() + "]duration=" + abs + " <= " + j);
//                if (abs <= j) {
//                    arrayList2.add(videoItem2);
//                } else {
//                    arrayList.add(CollectionsKt.arrayListOf(videoItem2));
//                }
//            }
//        }
//        ArrayList<ArrayList> arrayList3 = arrayList;
//        ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList3, 10));
//        for (ArrayList arrayList5 : arrayList3) {
//            if (arrayList5.size() == 1) {
//                createVideoBurst = arrayList5.get(0);
//            } else {
//                createVideoBurst = VideoBurst.Companion.createVideoBurst(arrayList5);
//            }
//            arrayList4.add((VideoItem) createVideoBurst);
//        }
//        return arrayList4;
//    }
//
//    public static final List<VideoItem> collapse(List<? extends VideoItem> list) {
//        Object createVideoBurst;
//        Intrinsics.checkNotNullParameter(list, "<this>");
//        ArrayList arrayList = new ArrayList();
//        for (VideoItem videoItem : list) {
//            if (arrayList.isEmpty()) {
//                arrayList.add(CollectionsKt.arrayListOf(videoItem));
//            } else {
//                Object obj = arrayList.get(arrayList.size() - 1);
//                Intrinsics.checkNotNullExpressionValue(obj, "acc[acc.size - 1]");
//                ArrayList arrayList2 = (ArrayList) obj;
//                if (((VideoItem) CollectionsKt.first((List<? extends Object>) arrayList2)).getId() == videoItem.getCoverId()) {
//                    arrayList2.add(videoItem);
//                } else {
//                    arrayList.add(CollectionsKt.arrayListOf(videoItem));
//                }
//            }
//        }
//        ArrayList<ArrayList> arrayList3 = arrayList;
//        ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList3, 10));
//        for (ArrayList arrayList5 : arrayList3) {
//            if (arrayList5.size() == 1) {
//                createVideoBurst = arrayList5.get(0);
//            } else {
//                createVideoBurst = VideoBurst.Companion.createVideoBurst(arrayList5);
//            }
//            arrayList4.add((VideoItem) createVideoBurst);
//        }
//        return arrayList4;
//    }


}