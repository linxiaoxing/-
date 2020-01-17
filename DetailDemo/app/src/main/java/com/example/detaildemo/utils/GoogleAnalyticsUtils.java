package com.example.detaildemo.utils;


import android.util.SparseArray;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;

/**
 * GoogleAnalytics送信用.
 */
public class GoogleAnalyticsUtils {

//    /**
//     * 実行中のクラス名を取得します.
//     * @param stackTraceElement stackTraceElement
//     * @return クラス名.
//     */
//    private static String getCurrentClassName(final StackTraceElement[] stackTraceElement) {
//        if (stackTraceElement.length >= 3) {
//            String className = stackTraceElement[2].getClassName();
//            className = className.substring(className.lastIndexOf(ContentUtils.STR_DOT) + 1);
//            if (className.contains(ContentUtils.STR_DOLLAR)) {
//                className = className.substring(0, className.lastIndexOf(ContentUtils.STR_DOLLAR));
//            }
//            return className;
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 実行中のメソッド名を取得します.
//     *
//     * @return メソッド名.
//     * @param stackTraceElement stackTraceElement
//     */
//    private static String getCurrentMethodName(final StackTraceElement[] stackTraceElement) {
//        if (stackTraceElement.length >= 3) {
//            return stackTraceElement[2].getMethodName();
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * getClassNameAndMethodName.
//     * @return nameString
//     * @param stackTraceElement stackTraceElement
//     */
//    public static String getClassNameAndMethodName(final StackTraceElement[] stackTraceElement) {
//        return getCurrentClassName(stackTraceElement) + ContentUtils.STR_COLON + getCurrentMethodName(stackTraceElement);
//    }
//
//    /**
//     * エラーレポートを送信する.
//     *
//     * @param classMethodName        クラス名とメソッド名
//     * @param errorCodeIdentifier 　エラーコードorエラー識別子
//     */
//    public static void sendErrorReport(final String classMethodName, final String errorCodeIdentifier) {
//        DTVTLogger.start();
//        DTVTLogger.debug(" classMethodName: " + classMethodName + " errorCodeIdentifier: " + errorCodeIdentifier);
//        String category = TrackerManager.shared().getAppContext().getString( R.string.google_analytics_category_event_error_report);
//        sendEventInfo(category, errorCodeIdentifier, classMethodName, null);
//        DTVTLogger.end();
//    }
//
//    /**
//     * イベントを送る.
//     *
//     * @param category イベントのカテゴリ
//     * @param action イベントのアクション
//     * @param customDimensions カスタム ディメンション
//     * @param label ラベル
//     */
//    public static void sendEventInfo(final String category,
//                                     final String action,
//                                     final String label,
//                                     SparseArray<String> customDimensions) {
//        DTVTLogger.start();
//        Tracker tracker = TrackerManager.shared().getDefaultTracker();
//        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();
//        builder.setCategory(category);
//        builder.setAction(action == null ? "" : action);
//        builder.setLabel(label == null ? "" : label);
//        DTVTLogger.debug("[GA][E]:" + " category: " + category + " action: " + action + " label: " + label + " eventValue: " + 1);
//        //valueデフォルト値1
//        builder.setValue(1);
//        if (customDimensions == null) {
//            customDimensions = new SparseArray<>();
//        }
//        DcmTracker dcmTracker =
//                ((TvtDcmAnalyticsApplication) TrackerManager.shared().getAppContext()).getTracker();
//        customDimensions.put(ContentUtils.CUSTOMDIMENSION_DCM_ANALYTICS,
//                dcmTracker.getUserId(TrackerManager.shared().getAppContext()));
//        for (int i = 0; i < customDimensions.size(); i++) {
//            int key = customDimensions.keyAt(i);
//            String value = customDimensions.get(key);
//            builder.setCustomDimension(key, value);
//            DTVTLogger.debug("[GA][CD]:" + key + ":" + value);
//        }
//        tracker.send(builder.build());
//        DTVTLogger.end();
//    }
//
//    /**
//     * スクリーン・ビューを送る.
//     *
//     * @param screenName スクリーン名
//     * @param customDimensions カスタム ディメンション
//     */
//    public static void sendScreenViewInfo(final String screenName, SparseArray<String> customDimensions) {
//        DTVTLogger.start();
//        Tracker tracker = TrackerManager.shared().getDefaultTracker();
//        tracker.setScreenName(screenName);
//        DTVTLogger.debug("[GA][SN]:" + screenName);
//        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
//        if (customDimensions == null) {
//            customDimensions = new SparseArray<>();
//        }
//        DcmTracker dcmTracker =
//                ((TvtDcmAnalyticsApplication) TrackerManager.shared().getAppContext()).getTracker();
//        customDimensions.put(ContentUtils.CUSTOMDIMENSION_DCM_ANALYTICS,
//                dcmTracker.getUserId(TrackerManager.shared().getAppContext()));
//        for (int i = 0; i < customDimensions.size(); i++) {
//            int key = customDimensions.keyAt(i);
//            String value = customDimensions.get(key);
//            builder.setCustomDimension(key, value);
//            DTVTLogger.debug("[GA][CD]:" + key + ":" + value);
//        }
//        tracker.send(builder.build());
//        DTVTLogger.end();
//    }
//
//    /**
//     * サーバーからSuccessレスポンス または　キャッシュにはUserInfoがある →　GAを送信
//     *
//     * @param userInfoLists
//     * @return
//     */
//    public static boolean canSendGA(List<UserInfoList> userInfoLists) {
//        return (userInfoLists != null) && (userInfoLists.size() != 0);
//    }

}
