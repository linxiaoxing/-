package com.example.detaildemo.data.bean;


import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 番組クラス.
 * 　　機能： 番組の属性を纏めるクラスである.
 */
public class ScheduleInfo {

    /** タイトル.*/
    private String title;
    /** 番組詳細.*/
    private String detail;
    /** 開始時間.*/
    private String startTime;
    /** 終了時間.*/
    private String endTime;
    /** サムネイル url.*/
    private String imageUrl;
    /** サムネイル url(詳細画面用).*/
    private String imageDetailUrl;
    /** チャンネル ID.*/
    private String chNo;
    /** パレンタル情報.*/
    private String mRValue = null;
    /** 表示タイプ.*/
    private String mDispType = null;
    /** クリップ判定情報.*/
    private String mSearchOk = null;
    /** dTVフラグ.*/
    private String mDtv = null;
    /** dTVタイプ.*/
    private String mDtvType = null;
    /** コンテンツタイプ.*/
    private String mContentType = null;
    /** TVサービス種別.*/
    private String mTvService = null;
    /** vodStartDate.*/
    private String mVodStartDate = null;
    /** サービスID.*/
    private String mServiceId = null;
    /** サービスIDユニーク.*/
    private String mServiceIdUniq = null;
    /** イベントID.*/
    private String mEventId = null;
    /** タイトルID.*/
    private String mTitleId = null;
    /** crid.*/
    private String mCrId = null;
    /** クリップリクエスト用データ.*/
    private ClipRequestData mClipRequestData = null;
    /** 時間単価換算.*/
    private static final float FORMAT = 1000 * 60 * 60;
    /** クリップ可否.*/
    private boolean mClipExec = false;
    /** クリップ未/済.*/
    private boolean mClipStatus = false;
    /** コンテンツID.*/
    private String mContentsId = null;
    /** 4KFlg.*/
    private String m4KFlg;
    /** 描画順序最適化用オフセット位置(現在表示中Y位置からのオフセット絶対値).*/
    private int mDrawOffset = 0;
    /** OTTフラグ.*/
    private String mOttFlg = null;
    /** モバイル向けiOSフラグ(1 OR なし,1の場合ドコテレアプリiOSへ提供できるコンテンツ).*/
    private String mMov_iosflg = null;
    /** モバイル向けAndroidフラグ(1 OR なし,1の場合ドコテレアプリAndroidへ提供できるコンテンツ).*/
    private String mMov_androidflg = null;
    /** モバイル向け最高画質(1080p, 720p, 480p).*/
    private String mMov_resolution = null;
    /** ダウンロード可否フラグ(1 OR なし,1の場合ダウンロード配信できるコンテンツ).*/
    private String mDownload = null;
    /** 外部出力可否フラグ(1 OR なし,1の場合外部出力できるコンテンツ).*/
    private String mExternal_output = null;
    /** OTTマスク(1 OR なし,1の場合、OTT向けストリームがマスクされているコンテンツ).*/
    private String mOtt_mask = null;

    /**
     * タイトルを取得する.
     * @return タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * タイトルを設定する.
     * @param title タイトル
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * 詳細を取得する.
     * @return 番組詳細
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 詳細を設定する.
     * @param detail 番組詳細
     */
    public void setDetail(final String detail) {
        this.detail = detail;
    }

    /**
     * 開始時間を取得する.
     * @return 開始時間
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * 開始時間を設定する.
     * @param startTime 開始時間
     */
    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    /**
     * 終了時間を取得する.
     * @return 終了時間
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * 終了時間を設定する.
     * @param endTime 終了時間
     */
    public void setEndTime(final String endTime) {
        this.endTime = endTime;
    }

    /**
     * サムネイル urlを取得する.
     * @return サムネイル url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * サムネイル urlを設定する.
     * @param imageUrl サムネイル url
     */
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     *サムネイル url(詳細画面用)を取得.
     * @return サムネイル url(詳細画面用)
     */
    public String getImageDetailUrl() {
        return imageDetailUrl;
    }

    /**
     * サムネイル url(詳細画面用)を設定.
     * @param imageDetailUrl サムネイル url(詳細画面用)
     */
    public void setImageDetailUrl(final String imageDetailUrl) {
        this.imageDetailUrl = imageDetailUrl;
    }

    /**
     * チャンネルNOを取得する.
     * @return チャンネル番号
     */
    public String getChNo() {
        return chNo;
    }

    /**
     * チャンネルNOを設定する.
     * @param chNo チャンネル番号
     */
    public void setChNo(final String chNo) {
        this.chNo = chNo;
    }

    /**
     * クリップリクエスト用データ取得する.
     * @return クリップリクエスト用データ
     */
    public ClipRequestData getClipRequestData() {
        return mClipRequestData;
    }

    /**
     * クリップリクエスト用データ設定する.
     * @param mClipRequestData クリップリクエスト用データ
     */
    public void setClipRequestData(final ClipRequestData mClipRequestData) {
        this.mClipRequestData = mClipRequestData;
    }

    /**
     *パレンタル情報取得.
     * @return パレンタル情報
     */
    public String getRValue() {
        return mRValue;
    }

    /**
     *パレンタル情報設定.
     * @param mRValue パレンタル情報
     */
    public void setRValue(final String mRValue) {
        this.mRValue = mRValue;
    }

    /**
     *表示タイプ取得.
     * @return 表示タイプ
     */
    public String getDispType() {
        return mDispType;
    }

    /**
     *表示タイプ設定.
     * @param mDispType 表示タイプ
     */
    public void setDispType(final String mDispType) {
        this.mDispType = mDispType;
    }

    /**
     * クリップ判定情報取得.
     * @return クリップ判定情報
     */
    public String getSearchOk() {
        return mSearchOk;
    }

    /**
     * クリップ判定情報設定.
     * @param mSearchOk クリップ判定情報
     */
    public void setSearchOk(final String mSearchOk) {
        this.mSearchOk = mSearchOk;
    }

    /**
     * dTVフラグ取得.
     * @return dTVフラグ
     */
    public String getDtv() {
        return mDtv;
    }

    /**
     * dTVフラグ設定.
     * @param mDtv dTVフラグ
     */
    public void setDtv(final String mDtv) {
        this.mDtv = mDtv;
    }

    /**
     * dTVタイプ取得.
     * @return dTVタイプ
     */
    public String getDtvType() {
        return mDtvType;
    }

    /**
     * dTVタイプ設定.
     * @param mDtvType dTVタイプ
     */
    public void setDtvType(final String mDtvType) {
        this.mDtvType = mDtvType;
    }

    /**
     * クリップ可否取得.
     * @return クリップ可否
     */
    public boolean isClipExec() {
        return mClipExec;
    }

    /**
     * クリップ可否設定.
     * @param mClipExec クリップ可否
     */
    public void setClipExec(final boolean mClipExec) {
        this.mClipExec = mClipExec;
    }

    /**
     * コンテンツタイプ取得.
     * @return コンテンツタイプ
     */
    public String getContentType() {
        return mContentType;
    }

    /**
     * コンテンツタイプ設定.
     * @param mContentType コンテンツタイプ
     */
    public void setContentType(final String mContentType) {
        this.mContentType = mContentType;
    }

    /**
     * クリップ未/済取得.
     * @return  クリップ未/済
     */
    public boolean isClipStatus() {
        return mClipStatus;
    }

    /**
     * コンテンツID取得.
     * @return コンテンツID
     */
    public String getContentsId() {
        return mContentsId;
    }

    /**
     * コンテンツID設定.
     * @param mContentsId コンテンツID
     */
    public void setContentsId(final String mContentsId) {
        this.mContentsId = mContentsId;
    }

    /**
     * クリップ未/済設定.
     * @param mClipStatus クリップ未/済
     */
    public void setClipStatus(final boolean mClipStatus) {
        this.mClipStatus = mClipStatus;
    }

    /**
     * TVサービス種別取得.
     * @return TVサービス種別
     */
    public String getTvService() {
        return mTvService;
    }

    /**
     * TVサービス種別設定.
     * @param mTvService TVサービス種別
     */
    public void setTvService(final String mTvService) {
        this.mTvService = mTvService;
    }

    /**
     * VodStartDate取得.
     * @return VodStartDate
     */
    public String getVodStartDate() {
        return mVodStartDate;
    }

    /**
     * VodStartDate種別設定.
     * @param mVodStartDate mVodStartDate
     */
    public void setVodStartDate(final String mVodStartDate) {
        this.mVodStartDate = mVodStartDate;
    }

    /**
     * サービスID取得.
     * @return サービスID
     */
    public String getServiceId() {
        return mServiceId;
    }

    /**
     * サービスID設定.
     * @param mServiceId サービスID
     */
    public void setServiceId(final String mServiceId) {
        this.mServiceId = mServiceId;
    }

    /**
     * サービスIDユニーク取得.
     * @return サービスIDユニーク
     */
    public String getServiceIdUniq() {
        return mServiceIdUniq;
    }

    /**
     * サービスIDユニーク設定.
     * @param mServiceIdUniq サービスIDユニーク
     */
    public void setServiceIdUniq(final String mServiceIdUniq) {
        this.mServiceIdUniq = mServiceIdUniq;
    }

    /**
     * イベントID取得.
     * @return イベントID
     */
    public String getEventId() {
        return mEventId;
    }

    /**
     * イベントID設定.
     * @param mEventId イベントID
     */
    public void setEventId(final String mEventId) {
        this.mEventId = mEventId;
    }

    /**
     * タイトルID取得.
     * @return タイトルID
     */
    public String getTitleId() {
        return mTitleId;
    }

    /**
     * タイトルID設定.
     * @param mTitleId タイトルID
     */
    public void setTitleId(final String mTitleId) {
        this.mTitleId = mTitleId;
    }

    /**
     * コンテンツ識別子取得.
     * @return コンテンツ識別子
     */
    public String getCrId() {
        return mCrId;
    }

    /**
     * コンテンツ識別子設定.
     * @param mCrId コンテンツ識別子設定
     */
    public void setCrId(final String mCrId) {
        this.mCrId = mCrId;
    }

    /**
     * 描画順序最適化用オフセット位置設定.
     * @return 描画順序最適化用オフセット
     */
    public int getDrawOffset() {
        return mDrawOffset;
    }

    /**
     * 描画順序最適化用オフセット位置取得.
     * @param drawOffset 描画順序最適化用オフセット
     */
    public void setDrawOffset(final int drawOffset) {
        this.mDrawOffset = drawOffset;
    }

    /**
     * 4kFlgを取得.
     */
    public String get4KFlg() {
        return m4KFlg;
    }

    /**
     * 4KFlgを設定.
     * @param flg4K 4KFlg
     */
    public void set4KFlg(final String flg4K) {
        this.m4KFlg = flg4K;
    }

    /**
     * モバイル向けiOSフラグ取得.
     * @return モバイル向けiOSフラグ
     */
    public String getMoviosFlg() {
        return mMov_iosflg;
    }
    /**
     * モバイル向けiOSフラグ設定.
     * @param moviosFlg モバイル向けiOSフラグ
     */
    public void setMoviosFlg(final String moviosFlg) {
        this.mMov_iosflg = moviosFlg;
    }
    /**
     * モバイル向けAndroidフラグ取得.
     * @return モバイル向けAndroidフラグ
     */
    public String getMovAndroidFlg() {
        return mMov_androidflg;
    }
    /**
     * モバイル向けAndroidフラグ設定.
     * @param movAndroidFlg モバイル向けAndroidフラグ
     */
    public void setMovAndroidFlg(final String movAndroidFlg) {
        this.mMov_androidflg = movAndroidFlg;
    }
    /**
     * モバイル向け最高画質取得.
     * @return モバイル向け最高画質
     */
    public String getMovResolution() {
        return mMov_resolution;
    }
    /**
     * モバイル向け最高画質設定.
     * @param movResolution モバイル向け最高画質
     */
    public void setMovResolution(final String movResolution) {
        this.mMov_resolution = movResolution;
    }
    /**
     * ダウンロード可否フラグ取得.
     * @return ダウンロード可否フラグ
     */
    public String getDownload() {
        return mDownload;
    }
    /**
     * ダウンロード可否フラグ設定.
     * @param download ダウンロード可否フラグ
     */
    public void setDownload(final String download) {
        this.mDownload = download;
    }
    /**
     * 外部出力可否フラグ取得.
     * @return 外部出力可否フラグ
     */
    public String getExternalOutput() {
        return mExternal_output;
    }
    /**
     * 外部出力可否フラグ設定.
     * @param externalOutput 外部出力可否フラグ設定
     */
    public void setExternalOutput(final String externalOutput) {
        this.mExternal_output = externalOutput;
    }
    /**
     * OTTマスク取得.
     * @return OTTマスク
     */
    public String getOttMask() {
        return mOtt_mask;
    }
    /**
     * OTTマスク設定.
     * @param ottMask OTTマスク
     */
    public void setOttMask(final String ottMask) {
        this.mOtt_mask = ottMask;
    }
    /**
     * OTTフラグ取得.
     **/
    public String getOttFlg() {
        return mOttFlg;
    }
    /**
     * OTTフラグ設定.
     * @param ottFlg ottFlg
     **/
    public void setOttFlg(final String ottFlg) {
        this.mOttFlg = ottFlg;
    }

    /**
     * 開始時間よりmarginの取得.
     *
     * @return 前の間隔
     */
    public float getMarginTop() {
        String standardTime = "";
        float diffHours = 0;
        if (startTime != null) {
            //将来のEpoch秒対応のため、ここでEpoch or 日付を吸収する
            startTime = DateUtils.formatDateCheckToEpoch(startTime);
            String curStartDay = startTime.substring(0, 10);
            int hour = Integer.parseInt(startTime.substring(11, 13));
            if (hour >= 0 && hour < 4) {
                SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_YYYYMMDDE, Locale.JAPAN);
                Date date = new Date();
                try {
                    curStartDay = curStartDay.replace("/", "-");
                    date = sdf.parse(curStartDay);
                } catch (ParseException e) {
                    DTVTLogger.debug(e);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                curStartDay = sdf.format(calendar.getTime());
            }
            standardTime = curStartDay + DateUtils.DATE_STANDARD_START;
        }
        Date startTime = DateUtils.stringToDate(standardTime);
        Date endTime = DateUtils.stringToDate(getFormatDate(this.startTime));
        try {
            diffHours = (endTime.getTime() - startTime.getTime()) / FORMAT;
        } catch (Exception e) {
            DTVTLogger.error("response is null");
        }
        if (diffHours < 0) {
            diffHours = 0;
        }
        return diffHours;
    }
    /**
     * Stringを切り取る.
     *
     * @param date 日付.
     * @return 変換後のデータ
     */
    private String getFormatDate(final String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        //将来のEpoch秒対応のため、ここでEpoch or 日付を吸収する
        String convertDate = DateUtils.formatDateCheckToEpoch(date);
        return convertDate.substring(0, 10) + convertDate.substring(11, 19);
    }

    /**
     * 番組高さ取得.
     *
     * @return 高さ
     */
    public float getMyHeight() {
        Date startTime = DateUtils.stringToDate(getFormatDate(this.startTime));
        Date endTime = DateUtils.stringToDate(getFormatDate(this.endTime));
        if (startTime == null || endTime == null) {
            return 0L;
        }
        float diffHours = (endTime.getTime() - startTime.getTime()) / FORMAT;
        if (diffHours < 0) {
            diffHours = 0;
        }
        return diffHours;
    }

}
