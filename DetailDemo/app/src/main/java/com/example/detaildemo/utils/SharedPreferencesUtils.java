package com.example.detaildemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.data.bean.OneTimeTokenData;
import com.example.detaildemo.data.provider.data.MobileDeviceInfo;
import com.example.detaildemo.data.provider.data.SerializablePreferencesData;
import com.example.detaildemo.data.provider.data.UserInfoList;
import com.example.detaildemo.view.DlnaDmsItem;

import java.util.List;

public class SharedPreferencesUtils{

    /**
     * ワンタイムトークン.
     */
    private static final String ONE_TIME_TOKEN = "ONE_TIME_TOKEN";
    /**
     * アプリ再起動フラグ.
     */
    private static final String RESTART_FLAG = "RESTART_FLAG";
    /**
     * 新お知らせキー.
     */
    private static final String NOTICE_LAST_MIDIFIED_TIME_KEY = "NOTICE_LAST_MIDIFIED_TIME_KEY";
    /**
     * 新お知らせ未読キー.
     */
    private static final String UNREAD_NOTICE_NEWS_KEY = "UNREAD_NOTICE_NEWS_KEY";
    /**
     * 最後に取得したワンタイムパスワード.
     */
    private static final String LAST_ONE_TIME_PASSWORD = "LAST_ONE_TIME_PASSWORD";
    /**
     * 最後に取得したdアカウントのID.
     */
    private static final String LAST_D_ACCOUNT_ID = "BEFORE_D_ACCOUNT_ID";
    // 接続済み STB情報 保存キー
    /**
     * STB情報 親キー.
     */
    private static final String SHARED_KEY_SELECTED_STB_DATA_INFOMATION = "stb_data_info";
    /**
     * ユーザ契約情報キー.
     */
    private static final String USER_CONTRACT_INFO_SHARED_KEY = "USER_CONTRACT_INFO_SHARED_KEY";
    /**
     * SharedPreferences ペアリング情報保存キー 親キー.
     */
    private static final String SHARED_KEY_PAIRING_INFOMATION = "pairing_info";
    /**
     * ホーム画面ペアリング済み判定 保存キー.
     */
    private static final String SHARED_KEY_DECISION_PARING_SETTLED = "decision_paring_settled";
    /**
     * dアカウントの未認証状態を保存する.
     */
    private static final String D_ACCOUNT_UNCERTIFIED_DACCOUNT
            = "D_ACCOUNT_UNCERTIFIED_DACCOUNT";

    /** TVクリップキー一覧is_forceフラグ.*/
    private static final String CLIP_KEY_LIST_IS_FORCE_TV
            = "CLIP_KEY_LIST_IS_FORCE_TV";
    /** ビデオクリップキー一覧is_forceフラグ.*/
    private static final String CLIP_KEY_LIST_IS_FORCE_VOD
            = "CLIP_KEY_LIST_IS_FORCE_VOD";
    /**
     * ユーザ情報永続化キー.
     */
    private static final String USER_INFO_SERIALIZABLE_DATA_KEY
            = "user_info_serializable_data_key";
    /**
     * ユーザー情報取得日時.
     */
    private static final String LAST_USER_INFO_DATE = "LAST_USER_INFO_DATE";
    /**
     * ユーザ紐づけSTB機種.
     */
    private static final String USER_STB_NAME_SHARED_KEY = "USER_STB_NAME_SHARED_KEY";
    /**
     * ユーザエリアコードキー.
     */
    private static final String USER_AREA_CODE_SHARED_KEY = "USER_AREA_CODE_SHARED_KEY";
    /**
     * ユーザ年齢情報キー.
     */
    private static final String USER_AGE_REQ_SHARED_KEY = "USER_AGE_REQ_SHARED_KEY";
    /**
     * UUIDキー.
     */
    private static final String UUID_KEY = "UUID_KEY";

    /**
     * OTTプレイトークン.
     */
    private static final String OTT_PLAY_TOKEN = "OTT_PLAY_TOKEN";
    /**
     * 設定ファイル端末種別.
     */
    private static final String LAST_SETTING_FILE_MOBILE_CATEGORY
            = "LAST_SETTING_FILE_MOBILE_CATEGORY";

    /**
     * 設定ファイル端末種別リスト.
     */
    private static final String LAST_SETTING_FILE_MOBILE_CATEGORY_LIST
            = "LAST_SETTING_FILE_MOBILE_CATEGORY_LIST";

    /**
     * 端末種別TRUEの場合.
     */
    private static final char SETTING_FILE_MOBILE_CATEGORY_FLAG_TRUE
            = '1';
    /** 5G機種フラグindex.*/
    private static final int DEVICE_CATEGRORY_5G_FLG_INDEX = 0;

    /** 2画面対応フラグindex.*/
    private static final int DEVICE_CATEGRORY_DUAL_SCREEN_FLG_INDEX
            = 1;


    /**
     * 独自の削除メソッドがある接続済みSTB情報以外の、dアカウントユーザー切り替え時の削除対象
     * 新しい物を追加した場合は、基本的にこの配列に名前を追加してください。
     * (チュートリアル表示済み判定以外は全て消すことになった。アプリ再起動フラグは自動で消えるので対象外).
     */
    private final static String[] DELETE_PREFERENCES_NAME = {
            //dアカウント未認証状態
            D_ACCOUNT_UNCERTIFIED_DACCOUNT
    };

    /**
     * ワンタイムトークン関連情報の取得.
     *
     * @param context コンテキスト
     * @return ワンタイムトークン情報構造体
     */
    public static OneTimeTokenData getOneTimeTokenData(final Context context) {
        //プリファレンスから読み込む
        SharedPreferences data = context.getSharedPreferences(
                ONE_TIME_TOKEN, Context.MODE_PRIVATE);
        String buffer = data.getString(ONE_TIME_TOKEN, "");

        //復号する
        String afterBuffer = StringUtils.getClearString(context, buffer);

        //読み込んだ物を分割
        OneTimeTokenData tokenData = new OneTimeTokenData(afterBuffer);


        return tokenData;
    }

    /**
     * ワンタイムトークン関連情報の書き込み.
     *
     * @param context   コンテキスト
     * @param tokenData 書き込むワンタイムトークン
     */
    public static void setOneTimeTokenData(final Context context, final OneTimeTokenData tokenData) {
        DTVTLogger.start();
        //書き込み用の文字列を作成する
        String buffer = tokenData.makeOneTimeTokenString();

        //暗号化
        String afterBuffer = StringUtils.getCipherString(context, buffer);

        //書き込む
        SharedPreferences data = context.getSharedPreferences(
                ONE_TIME_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(ONE_TIME_TOKEN, afterBuffer);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * ワンタイムトークンの削除を行う.
     *
     * @param context コンテキスト
     */
    public static void deleteOneTimeTokenData(final Context context) {
        DTVTLogger.start();
        SharedPreferences deleteData = context.getSharedPreferences(
                ONE_TIME_TOKEN, Context.MODE_PRIVATE);
        deleteData.edit().clear().apply();
        DTVTLogger.end();
    }

    /**
     * 再起動フラグを設定.
     *
     * @param context     コンテキスト
     * @param restartFlag 再起動フラグの設定値
     */
    public static void setSharedPreferencesRestartFlag(final Context context, final boolean restartFlag) {
        DTVTLogger.start("set restart flag = " + restartFlag);
        SharedPreferences data = context.getSharedPreferences(
                RESTART_FLAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean(RESTART_FLAG, restartFlag);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 再起動フラグを取得.
     *
     * @param context コンテキスト
     * @return 再起動フラグ
     */
    public static boolean getSharedPreferencesRestartFlag(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                RESTART_FLAG, Context.MODE_PRIVATE);

        //デフォルト値はfalseで取得して返す
        return data.getBoolean(RESTART_FLAG, false);
    }

    /**
     * dアカウントログアウトおよび切り替えるなど時に新着お知らせ表示のFlagをリセット
     * @param context
     */
    public static void removeNewlyNoticeFlag(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(NOTICE_LAST_MIDIFIED_TIME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.remove(NOTICE_LAST_MIDIFIED_TIME_KEY);
        editor.remove(UNREAD_NOTICE_NEWS_KEY);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 取得したワンタイムパスワードを保存.
     *
     * @param context コンテキスト
     * @param pass    保存するワンタイムパスワード
     */
    public static void setSharedPreferencesOneTimePass(final Context context, final String pass) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                LAST_ONE_TIME_PASSWORD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(LAST_ONE_TIME_PASSWORD, pass);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 最後に保存したワンタイムパスワードを取得.
     *
     * @param context コンテキスト
     * @return ワンタイムパスワード
     */
    public static String getSharedPreferencesOneTimePass(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                LAST_ONE_TIME_PASSWORD, Context.MODE_PRIVATE);

        return data.getString(LAST_ONE_TIME_PASSWORD, "");
    }

    /**
     * 取得したdアカウントを保存.
     *
     * @param context コンテキスト
     * @param id      保存するdアカウントID
     */
    public static void setSharedPreferencesDaccountId(final Context context, final String id) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                LAST_D_ACCOUNT_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(LAST_D_ACCOUNT_ID, id);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 最後に保存したdアカウントを取得.
     *
     * @param context コンテキスト
     * @return dアカウント
     */
    public static String getSharedPreferencesDaccountId(final Context context) {
        SharedPreferences data = context.getSharedPreferences(
                LAST_D_ACCOUNT_ID, Context.MODE_PRIVATE);

        return data.getString(LAST_D_ACCOUNT_ID, "");
    }

    /**
     * 一部の設定値以外のプリファレンスを削除する.
     *
     * @param context コンテキスト
     */
    public static void clearAlmostSharedPreferences(final Context context) {
        //接続済みSTB情報系列には削除処理があるので使用する(一見一つしか削除していないが、元締めを消しているのですべて消える)
        resetSharedPreferencesStbInfo(context);

        //他の情報の削除を行う
        for (String deleteKey : DELETE_PREFERENCES_NAME) {
            SharedPreferences deleteData = context.getSharedPreferences(deleteKey, Context.MODE_PRIVATE);
            deleteData.edit().clear().apply();
        }
    }

    /**
     * SharedPreferences内の接続済みSTBのデータをクリア.
     *
     * @param context コンテキスト
     */
    public static void resetSharedPreferencesStbInfo(final Context context) {
        DTVTLogger.debug("resetSharedPreferencesStbInfo");
        SharedPreferences data = context.getSharedPreferences(
                SHARED_KEY_SELECTED_STB_DATA_INFOMATION, Context.MODE_PRIVATE);
        data.edit().clear().apply();
//        setSharedPreferencesDecisionParingSettled(context, false);
//        setSharedPreferencesStbConnect(context, false);
//        setTvClipKeyListIsForce(context, true);
//        setVodClipKeyListIsForce(context, true);
    }

    /**
     * 取得したユーザ契約情報を保存.
     *
     * @param context コンテキスト
     * @param contractInfo 保存するユーザ契約情報
     */
    public static void setSharedPreferencesContractInfo(final Context context, final String contractInfo) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_CONTRACT_INFO_SHARED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(USER_CONTRACT_INFO_SHARED_KEY, contractInfo);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 最後に保存した契約情報を取得.
     *
     * @param context コンテキスト
     * @return 契約情報
     */
    public static String getSharedPreferencesContractInfo(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_CONTRACT_INFO_SHARED_KEY, Context.MODE_PRIVATE);

        //保存した契約情報がない場合は「none」を返却
        return data.getString(USER_CONTRACT_INFO_SHARED_KEY, UserInfoUtils.CONTRACT_INFO_NONE);
    }

    /**
     * ユーザ情報永続化.
     *
     * @param context                     コンテキスト
     * @param serializablePreferencesData ユーザ情報
     */
    public static void setSharedPreferencesSerializableData(
            final Context context,
            final SerializablePreferencesData serializablePreferencesData) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_INFO_SERIALIZABLE_DATA_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        String preferencesData = StringUtils.toPreferencesDataBase64(serializablePreferencesData);
        editor.putString(USER_INFO_SERIALIZABLE_DATA_KEY, preferencesData);
        editor.apply();
        //今の日時を取得日時とする
        SharedPreferencesUtils.setSharedPreferencesUserInfoDate(context, System.currentTimeMillis() / 1000);
        DTVTLogger.end();
    }

    /**
     * 永続化ユーザ情報取得.
     *
     * @param context コンテキスト
     * @return ユーザ情報
     */
    public static List<UserInfoList> getSharedPreferencesUserInfo(final Context context) {
        SharedPreferences data = context.getSharedPreferences(
                USER_INFO_SERIALIZABLE_DATA_KEY, Context.MODE_PRIVATE);

        SerializablePreferencesData preferencesData
                = StringUtils.toPreferencesData(data.getString(USER_INFO_SERIALIZABLE_DATA_KEY, null));
        if (preferencesData == null) {
            return null;
        }
        //保存したひかりTVfordocomo視聴年齢値が無いときはnullを返却
        return preferencesData.getUserInfoList();
    }

    /**
     * ユーザー情報の最終取得日時を保存.
     *
     * @param context コンテキスト
     * @param getTime 取得日時
     */
    private static void setSharedPreferencesUserInfoDate(final Context context, final long getTime) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                LAST_USER_INFO_DATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putLong(LAST_USER_INFO_DATE, getTime);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * ユーザー情報の最終取得日時を取得.
     *
     * @param context コンテキスト
     * @return 最終取得日時
     */
    public static long getSharedPreferencesUserInfoDate(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                LAST_USER_INFO_DATE, Context.MODE_PRIVATE);

        //最終取得日時を返す。存在していなければ、最小値を返す
        return data.getLong(LAST_USER_INFO_DATE, Long.MIN_VALUE);
    }

    /**
     * TVクリップキー一覧is_forceフラグを設定.
     *
     * @param context コンテキスト
     * @param isForce
     */
    public static void setTvClipKeyListIsForce(Context context, final boolean isForce) {
        DTVTLogger.start();

        if (context != null) {
            SharedPreferences data = context.getSharedPreferences(
                    CLIP_KEY_LIST_IS_FORCE_TV, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean(CLIP_KEY_LIST_IS_FORCE_TV, isForce);
            editor.apply();
        }

        DTVTLogger.end();
    }

    /**
     * TVクリップキー一覧is_forceフラグを取得.
     *
     * @param context コンテキスト
     * @return TVクリップキー一覧is_forceフラグ
     */
    public static boolean getTvClipKeyListIsForce(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                CLIP_KEY_LIST_IS_FORCE_TV, Context.MODE_PRIVATE);

        //保存したdアカウント未認証状態を返却
        return data.getBoolean(CLIP_KEY_LIST_IS_FORCE_TV, true);
    }

    /**
     * ビデオクリップキー一覧is_forceフラグを設定.
     *
     * @param context コンテキスト
     * @param isForce
     */
    public static void setVodClipKeyListIsForce(Context context, final boolean isForce) {
        DTVTLogger.start();

        if (context != null) {
            SharedPreferences data = context.getSharedPreferences(
                    CLIP_KEY_LIST_IS_FORCE_VOD, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean(CLIP_KEY_LIST_IS_FORCE_VOD, isForce);
            editor.apply();
        }

        DTVTLogger.end();
    }

    /**
     * ビデオクリップキー一覧is_forceフラグを取得.
     *
     * @param context コンテキスト
     * @return ビデオクリップキー一覧is_forceフラグ
     */
    public static boolean getVodClipKeyListIsForce(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                CLIP_KEY_LIST_IS_FORCE_VOD, Context.MODE_PRIVATE);

        //保存したdアカウント未認証状態を返却
        return data.getBoolean(CLIP_KEY_LIST_IS_FORCE_VOD, true);
    }

    /**
     * dアカウントが未認証である事を明示する処理.
     *
     * dアカウントの値が空白か否かでは、ログオフとログオンは判定できるが、未認証は判定できない。
     * dアカウントアプリから未認証を返された際にtrueをセットして、直接分かるようにする
     * @param context コンテキスト
     * @param certifiedFlag dアカウントが未認証ならばtrue
     */
    public static void setUncertifiedDaccount(Context context, final boolean certifiedFlag) {
        DTVTLogger.start();

        if (context != null) {
            SharedPreferences data = context.getSharedPreferences(
                    D_ACCOUNT_UNCERTIFIED_DACCOUNT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean(D_ACCOUNT_UNCERTIFIED_DACCOUNT, certifiedFlag);
            editor.apply();
        }

        DTVTLogger.end();
    }

    /**
     * 新着お知らせの既読状態を取得
     * @param context   コンテキスト
     * @return  ローカルの既読状態
     */
    public static boolean getUnreadNewlyNotice(final Context context) {
        SharedPreferences data = context.getSharedPreferences(UNREAD_NOTICE_NEWS_KEY, Context.MODE_PRIVATE);
        return data.getBoolean(UNREAD_NOTICE_NEWS_KEY, false);
    }

    /**
     * 新着お知らせの既読状態を設定
     * @param context   コンテキスト
     * @param unread    読書状態
     */
    public static void setUnreadNewlyNotice(final Context context, final boolean unread) {
        DTVTLogger.start("unread = " + unread);
        SharedPreferences data = context.getSharedPreferences(UNREAD_NOTICE_NEWS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean(UNREAD_NOTICE_NEWS_KEY, unread);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * ペアリング状態を保存(Home画面用).
     *
     * @param context      コンテキスト
     * @param paringStatus ペアリング状態
     */
    public static void setSharedPreferencesDecisionParingSettled(final Context context, final boolean paringStatus) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                SHARED_KEY_PAIRING_INFOMATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean(SHARED_KEY_DECISION_PARING_SETTLED, paringStatus);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * ペアリング状態を取得(Home画面用).
     *
     * @param context コンテキスト
     * @return ペアリング状態
     */
    public static boolean getSharedPreferencesDecisionParingSettled(final Context context) {
        SharedPreferences data = context.getSharedPreferences(
                SHARED_KEY_PAIRING_INFOMATION, Context.MODE_PRIVATE);

        return data.getBoolean(SHARED_KEY_DECISION_PARING_SETTLED, false);
    }

    /**
     * 取得したユーザのSTBとの紐づけるSTB登録機種（TT01、TT02等）を保存.
     *
     * @param context コンテキスト
     * @param stbInfo 保存するSTB情報
     */
    public static void setSharedKeyPreferencesStbInfo(final Context context, final String stbInfo) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_STB_NAME_SHARED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(USER_STB_NAME_SHARED_KEY, stbInfo);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * ユーザのSTBとの紐づけるSTB機種を取得.
     *
     * @param context コンテキスト
     * @return STB登録機種
     */
    static String getSharedKeyPreferencesStbInfo(final Context context) {
        DTVTLogger.start();
        if (context == null) {
            return null;
        }
        SharedPreferences data = context.getSharedPreferences(
                USER_STB_NAME_SHARED_KEY, Context.MODE_PRIVATE);
        //保存したSTB登録機種がない場合はnullに設定
        return data.getString(USER_STB_NAME_SHARED_KEY, null);
    }

    /**
     * 最後に保存したエリアコードを取得.
     *
     * @param context コンテキスト
     * @return エリアコード
     */
    static String getSharedKeyPreferencesAreaCode(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_AREA_CODE_SHARED_KEY, Context.MODE_PRIVATE);
        //保存したエリアコードがない場合は空文字を返却
        return data.getString(USER_AREA_CODE_SHARED_KEY, "");
    }

    /**
     * 取得したエリアコードを保存.
     *
     * @param context コンテキスト
     * @param areaCode 保存するエリアコード
     */
    public static void setSharedKeyPreferencesAreaCode(final Context context, final String areaCode) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_AREA_CODE_SHARED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(USER_AREA_CODE_SHARED_KEY, areaCode);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 取得したユーザ視聴制限情報を保存.
     *
     * @param context コンテキスト
     * @param age     保存するユーザ年齢制限情報
     */
    public static void setSharedPreferencesAgeReq(final Context context, final int age) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_AGE_REQ_SHARED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(USER_AGE_REQ_SHARED_KEY, age);
        editor.apply();
        DTVTLogger.end();
    }

    /**
     * 最後に保存した年齢情報を取得.
     *
     * @param context コンテキスト
     * @return 年齢情報
     */
    public static int getSharedPreferencesAgeReq(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                USER_AGE_REQ_SHARED_KEY, Context.MODE_PRIVATE);

        //保存した年齢情報がない場合はPG12を返却
        return data.getInt(USER_AGE_REQ_SHARED_KEY, StringUtils.USER_AGE_REQ_PG12);
    }

    /**
     * 端末種別フラグ配列を取得.
     *
     * @param context コンテキスト
     * @return 端末情報
     */
    public static MobileDeviceInfo getSharedPreferencesSettingFileMobileCategory(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                LAST_SETTING_FILE_MOBILE_CATEGORY, Context.MODE_PRIVATE);

        //端末種別を返す。存在していなければ、"0000"を返す
        String strCategroy = data.getString(
                LAST_SETTING_FILE_MOBILE_CATEGORY, DtvtConstants.DEFAULT_MOBILE_CATEGORY);

        char arrCategory[] = strCategroy.toCharArray();
        boolean flags[] = new boolean[arrCategory.length];
        for (int i = 0; i < arrCategory.length; i++) {
            flags[i] = arrCategory[i] == SETTING_FILE_MOBILE_CATEGORY_FLAG_TRUE;
        }

        MobileDeviceInfo deviceInfo = new MobileDeviceInfo();
        deviceInfo.setIs5GSupported(flags[DEVICE_CATEGRORY_5G_FLG_INDEX]);//5G機種フラグ
        deviceInfo.setIsDualScreen(flags[DEVICE_CATEGRORY_DUAL_SCREEN_FLG_INDEX]);//２画面対応フラグ

        return deviceInfo;
    }

    /**
     * 接続済みSTB情報を取得.
     *
     * @param context コンテキスト
     * @return STB情報
     */
    public static DlnaDmsItem getSharedPreferencesStbInfo(final Context context) {
        DTVTLogger.debug("getSharedPreferencesStbInfo");
        DlnaDmsItem item = new DlnaDmsItem();
        SharedPreferences data = context.getSharedPreferences(
                SHARED_KEY_SELECTED_STB_DATA_INFOMATION, Context.MODE_PRIVATE);

//        item.mUdn = data.getString(SHARED_KEY_SELECTED_STB_DATA_INFOMATION_UDN, SHARED_GET_STRING_DEFAULT);
//        item.mControlUrl = data.getString(SHARED_KEY_SELECTED_STB_DATA_INFOMATION_CONTROL_URL, SHARED_GET_STRING_DEFAULT);
//        item.mHttp = data.getString(SHARED_KEY_SELECTED_STB_DATA_INFOMATION_HTTP, SHARED_GET_STRING_DEFAULT);
//        item.mFriendlyName = data.getString(SHARED_KEY_SELECTED_STB_DATA_INFOMATION_FRIENDLY_NAME, SHARED_GET_STRING_DEFAULT);
//        item.mIPAddress = data.getString(SHARED_KEY_SELECTED_STB_DATA_INFOMATION_IPADDRESS, SHARED_GET_STRING_DEFAULT);

        return item;
    }

    /**
     * UUID保存.
     *
     * @param context コンテキスト
     * @param uuid UUID
     */
    public static void setUuid(final Context context, final String uuid) {
        DTVTLogger.start();

        if (context != null) {
            SharedPreferences data = context.getSharedPreferences(
                    UUID_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString(UUID_KEY, uuid);
            editor.apply();
        }

        DTVTLogger.end();
    }

    /**
     * UUID取得.
     *
     * @param context コンテキスト
     * @return UUID
     */
    public static String getUuid(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                UUID_KEY, Context.MODE_PRIVATE);

        //保存したUUIDを返却
        return data.getString(UUID_KEY, null);
    }

    /**
     * 保存したOTTリニアコンテンツ用プレイトークンの取得.
     *
     * TODO: プレイトークンの書き込みは別スプリント
     * @param context コンテキスト
     * @return プレイトークン
     */
    public static String getOttPlayToken(final Context context) {
        DTVTLogger.start();
        SharedPreferences data = context.getSharedPreferences(
                OTT_PLAY_TOKEN, Context.MODE_PRIVATE);

        //保存したdアカウント未認証状態を返却
        return data.getString(OTT_PLAY_TOKEN, null);
    }

    /**
     *  保存したOTTリニアコンテンツ用プレイトークンの削除.
     *
     * @param context コンテキスト
     */
    public static void deleteOttPlayToken(final Context context) {
        DTVTLogger.start();
        SharedPreferences deleteData = context.getSharedPreferences(
                OTT_PLAY_TOKEN, Context.MODE_PRIVATE);
        deleteData.edit().clear().apply();
        DTVTLogger.end();
    }

    /**
     * プレイトークン保存.
     * @param context コンテキスト
     * @param playToken プレイトークン
     */
    private void savePlayToken(final Context context, final String playToken) {
        SharedPreferencesUtils.saveOttPlayToken(context, playToken);
    }

    /**
     * プレイトークンの保存.
     * @param context コンテキスト
     * @param playToken playToken
     */
    public static void saveOttPlayToken(final Context context, final String playToken) {
        DTVTLogger.start();
        if (context != null) {
            SharedPreferences data = context.getSharedPreferences(
                    OTT_PLAY_TOKEN, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString(OTT_PLAY_TOKEN, playToken);
            editor.apply();
        }

        DTVTLogger.end();
    }
}
