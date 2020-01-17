package com.example.detaildemo.data.webapiclient.jsonparser.data;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 番組切り替える場合画面に渡す用.
 */
public class TimerNoticeInfo implements Parcelable{

    /** タイトル.*/
    private String mTitle;
    /** サービスIDユニーク.*/
    private String mServiceIdUniq;
    /**サムネイルURL.*/
    private String mThumListURL = null;
    /**番組のパレンタル設定値.*/
    private String mRValue = null;
    /** 開始時間.*/
    private String mStartTime;
    /** 終了時間.*/
    private String mEndTime;
    /** コンテンツID.*/
    private String mContentsId;

    /**
     * タイトル取得.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * タイトル設定.
     * @param title タイトル
     */
    public void setTitle(final String title) {
        this.mTitle = title;
    }

    /**
     * サービスIDユニーク取得.
     */
    public String getServiceIdUniq() {
        return mServiceIdUniq;
    }

    /**
     * サービスIDユニーク設定.
     * @param serviceIdUniq サービスIDユニーク
     */
    public void setServiceIdUniq(final String serviceIdUniq) {
        this.mServiceIdUniq = serviceIdUniq;
    }

    /**
     * サムネイルURL取得.
     */
    public String getThumListURL() {
        return mThumListURL;
    }

    /**
     * サムネイルURL設定.
     * @param thumListURL サムネイルURL
     */
    public void setThumListURL(String thumListURL) {
        this.mThumListURL = thumListURL;
    }

    /**
     * 番組のパレンタル設定値取得.
     */
    public String getRValue() {
        return mRValue;
    }

    /**
     * 番組のパレンタル設定値設定.
     * @param rValue 番組のパレンタル設定値
     */
    public void setRValue(String rValue) {
        this.mRValue = rValue;
    }
    /** 開始時間を取得.
     * @return 開始時間
     */
    public String getStartTime() {
        return mStartTime;
    }

    /**
     * 開始設定.
     * @param startTime　開始時間
     */
    public void setStartTime(final String startTime) {
        this.mStartTime = startTime;
    }

    /**
     * 終了時間取得.
     * @return 終了時間
     */
    public String getEndTime() {
        return mEndTime;
    }

    /**
     * 終了時間設定.
     * @param endTime 終了時間
     */
    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
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
     * @param contentsId　コンテンツID
     */
    public void setContentsId(final String contentsId) {
        this.mContentsId = contentsId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mServiceIdUniq);
        dest.writeString(this.mThumListURL);
        dest.writeString(this.mRValue);
        dest.writeString(this.mStartTime);
        dest.writeString(this.mContentsId);
        dest.writeString(this.mEndTime);
    }

    /**コントラクター.*/
    public TimerNoticeInfo() {
    }

    /**
     * 画面に渡す必要な情報.
     * @param in source
     */
    private TimerNoticeInfo(final Parcel in) {
        this.mTitle = in.readString();
        this.mServiceIdUniq = in.readString();
        this.mThumListURL = in.readString();
        this.mRValue = in.readString();
        this.mStartTime = in.readString();
        this.mContentsId = in.readString();
        this.mEndTime = in.readString();
    }

    /**Creator.*/
    public static final Creator<TimerNoticeInfo> CREATOR = new Creator<TimerNoticeInfo>() {
        @Override
        public TimerNoticeInfo createFromParcel(final Parcel source) {
            return new TimerNoticeInfo(source);
        }

        @Override
        public TimerNoticeInfo[] newArray(final int size) {
            return new TimerNoticeInfo[size];
        }
    };
}
