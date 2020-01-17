package com.example.detaildemo.data.provider.data;


import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.utils.OttContentUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ott視聴開始機能データ.
 */
public class OttPlayerStartData {
    /**kind.*/
    private String mKind = null;
    /**コンテンツID.*/
    private String mContentId = null;
    /**再生url.*/
    private String mPlayUrl = null;
    /**drmモード.*/
    private String mDrmMode = null;
    /**ライセンスサーバURL.*/
    private String mLaUrl = null;
    /**DRM情報.*/
    private String mCustomData = null;
    /** チャンネルのアイコン.*/
    private String mThumbnail;
    /**タイトル.*/
    private String mTitle;
    /**日付.*/
    private String mChannelDate;
    /**チャンネル名.*/
    private String mChannelName;
    /**サービスIDユニーク.*/
    private String mServiceIdUniq = null;

    /**
     * kind取得.
     * @return kind
     */
    public String getKind() {
        return mKind;
    }

    /**
     * kind設定.
     * @param kind kind
     */
    public void setKind(final String kind) {
        this.mKind = kind;
    }

    /**
     * コンテンツID取得.
     * @return コンテンツID
     */
    public String getContentId() {
        return mContentId;
    }

    /**
     * コンテンツID設定.
     * @param contentId コンテンツID
     */
    public void setContentId(final String contentId) {
        this.mContentId = contentId;
    }

    /**
     * 再生url取得.
     * @return 再生url
     */
    public String getPlayUrl() {
        return mPlayUrl;
    }

    /**
     * 再生url設定.
     * @param playUrl 再生url
     */
    public void setPlayUrl(final String playUrl) {
        this.mPlayUrl = playUrl;
    }

    /**
     * drmモード取得.
     * @return drmモード
     */
    public String getDrmMode() {
        return mDrmMode;
    }

    /**
     * drmモード設定.
     * @param drmMode drmモード
     */
    public void setDrmMode(final String drmMode) {
        this.mDrmMode = drmMode;
    }

    /**
     * ライセンスサーバURL取得.
     * @return ライセンスサーバURL
     */
    public String getLaUrl() {
        return mLaUrl;
    }

    /**
     * DライセンスサーバURL設定.
     * @param laUrl ライセンスサーバURL
     */
    public void setLaUrl(final String laUrl) {
        this.mLaUrl = laUrl;
    }

    /**
     * DRM情報取得.
     * @return DRM情報
     */
    public String getCustomData() {
        return mCustomData;
    }

    /**
     * DRM情報設定.
     * @param customData DRM情報
     */
    public void setCustomData(final String customData) {
        this.mCustomData = customData;
    }

    /**
     * thumbnailを取得.
     * @return thumbnail
     */
    public String getThumbnail() {
        return mThumbnail;
    }
    /**
     * thumbnail.
     * @param thumbnail チャンネルID
     */
    public void setThumbnail(final String thumbnail) {
        this.mThumbnail = thumbnail;
    }

    /**
     *タイトル取得.
     * @return タイトル
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     *タイトル設定.
     * @param mTitle タイトル
     */
    public void setTitle(final String mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * 日付取得.
     * @return 日付
     */
    public String getChannelDate() {
        return mChannelDate;
    }

    /**
     * 日付設定.
     * @param mChannelDate 日付
     */
    public void setChannelDate(final String mChannelDate) {
        this.mChannelDate = mChannelDate;
    }

    /**
     * チャンネル名取得.
     * @return チャンネル名
     */
    public String getChannelName() {
        return mChannelName;
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
     * チャンネル名設定.
     * @param mChannelName チャンネル名
     */
    public void setChannelName(final String mChannelName) {
        this.mChannelName = mChannelName;
    }

    /**キー名：配列データ.*/
    private static final String[] mPlayList = {
            OttContentUtils.OTT_PLAY_START_KIND, OttContentUtils.OTT_PLAY_START_CONTENT_ID,
            OttContentUtils.OTT_PLAY_START_PLAY_URL, OttContentUtils.OTT_PLAY_START_DRM_MODE,
            OttContentUtils.OTT_PLAY_START_LA_URL, OttContentUtils.OTT_PLAY_START_CUSTOM_DATA
    };

    /**
     * Ott視聴開始レスポンス（フル版）の json データをデータオブジェクトに変換.
     * @param jsonObj 　json データ
     */
    public void setData(final JSONObject jsonObj) {
        // 再生開始用情報リスト
        try {
            if (jsonObj != null) {
                // 配列データ
                for (String item : mPlayList) {
                    if (!jsonObj.isNull(item)) {
                        setMember(item, jsonObj.get(item));
                    }
                }
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }

    /**
     * キーとキーの値をメンバーにセットする.
     * @param key  キー
     * @param data キーの値
     */
    private void setMember(final String key, final Object data) {
        //キーに値があれば、それを元に値を格納する
        if (!TextUtils.isEmpty(key)) {
            switch (key) {
                case OttContentUtils.OTT_PLAY_START_KIND:
                    setKind(String.valueOf(data));
                    break;
                case OttContentUtils.OTT_PLAY_START_CONTENT_ID:
                    setContentId(String.valueOf(data));
                    break;
                case OttContentUtils.OTT_PLAY_START_PLAY_URL:
                    setPlayUrl(String.valueOf(data));
                    break;
                case OttContentUtils.OTT_PLAY_START_DRM_MODE:
                    setDrmMode(String.valueOf(data));
                    break;
                case OttContentUtils.OTT_PLAY_START_LA_URL:
                    setLaUrl(String.valueOf(data));
                    break;
                case OttContentUtils.OTT_PLAY_START_CUSTOM_DATA:
                    setCustomData(String.valueOf(data));
                    break;
                default:
                    break;
            }
        }
    }
}