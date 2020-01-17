package com.example.detaildemo.data.bean;

import android.widget.ImageView;

import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.StringUtils;

/**
 * コンテンツデータ管理クラス.
 */
public class ContentsData {

    /**ランキング順位.*/
    private String mRank = null;
    /**時間.*/
    private String mTime = null;
    /**メインタイトル.*/
    private String mTitle = null;
    /**エピソードタイトル.*/
    private String mEpisodeTitle = null;
    /**評価ポイント.*/
    private String mRatStar = null;
    /**サムネイルURL(リスト).*/
    private String mThumListURL = null;
    /**サムネイルURL(詳細画面).*/
    private String mThumDetailURL = null;
    /**録画予約ステータス.*/
    //private int mRecordingReservationStatus = RecordingReservationListDataProvider.RECORD_RESERVATION_SYNC_STATUS_ALREADY_REFLECT;
    /**チャンネル名.*/
    private String mChannelName = null;
    /**録画番組用 コピー残り回数.*/
    private int mAllowedUse = 0;
    /**クリップ状態.*/
    private String mSearchOk = null;
    /**コンテンツ識別子.*/
    private String mCrid = null;
    /**サービスID.*/
    private String mServiceId = null;
    /**サービスIDユニーク.*/
    private String mServiceIdUniq = null;
    /**イベントID.*/
    private String mEventId = null;
    /**タイトルID.*/
    private String mTitleId = null;
    /**エピソードID.*/
    private String mEpisodeId = null;
    /**番組種別.*/
    private String mDispType = null;
    /**日時（epoc秒）	(VOD用)コンテンツ自体の有効開始日時(PITのみ).*/
    private long mAvailStartDate = 0;
    /**日時（epoc秒）	(VOD用)コンテンツ自体の有効期限日時(PITのみ).*/
    private long mAvailEndDate = 0;
    /**番組のパレンタル設定値.*/
    private String mRValue = null;
    /**放送開始日時.*/
    private String mPublishStartDate = null;
    /**放送終了日時.*/
    private String mPublishEndDate = null;
    /**視聴通知判定.*/
    private String mIsNotify = null;
    /**コンテンツタイプ.*/
    private String mContentsType = null;
    /**サービス種別.*/
    private String mTvService = null;
    /**VOD配信開始日時  日時（epoc秒）.*/
    private long mVodStartDate = 0;
    /**VOD配信終了日時  日時（epoc秒）.*/
    private long mVodEndDate = 0;
    /**dTVフラグ.*/
    private String mDtv = null;
    /**コンテンツID.*/
    private String mContentsId = null;
    /**あらすじ.*/
    private String mSynop = null;
    /**カテゴリID.*/
    private String mCategoryId = null;
    /**開始時刻.*/
    private String mStartViewing = null;
    /**終了時刻.*/
    private String mEndViewing = null;
    /**reserved1.*/
    private String mReserved1 = null;
    /**reserved2.*/
    private String mReserved2 = null;
    /**reserved3.*/
    private String mReserved3 = null;
    /**reserved4.*/
    private String mReserved4 = null;
    /**reserved5.*/
    private String mReserved5 = null;
    /**reserved6.*/
    private String mReserved6 = null;
    /**reserved7.*/
    private String mReserved7 = null;
    /**reserved8.*/
    private String mReserved8 = null;
    /**reserved9.*/
    private String mReserved9 = null;
    /**reserved10.*/
    private String mReserved10 = null;
    /**mobileViewingFlg.*/
    private String  mobileViewingFlg = null;
    /**作品種別.*/
    private String mTitleKind = null;
    /**chsvod.*/
    private String  mChsVod = null;
    /**estFlg.*/
    private String  mEstFlg = null;
    /**クリップボタン.*/
    private ImageView mClipButton = null;
    /**クリップリクエストデータ.*/
    private ClipRequestData mRequestData = new ClipRequestData();
    /**ダウンロードフラグ.*/
    private int mDownloadFlg = 0;
    /**ダウンロードステータス（進捗）.*/
    private String mDownloadStatus = null;
    /**ダウンロードパス.*/
    private String mDlFileFullPath = null;
    /**ダウンロードボタンを隠すフラグ.*/
    private boolean mDownloadBtnHide = false;
    /**チャンネルID.*/
    private String mChannelId = null;
    /**チャンネルNo.*/
    private String mChannelNo = null;
    /**おすすめ順.*/
    private String mRecommendOrder = null;
    /**画面ID.*/
    private String mPageId = null;
    /**ユーザグループID.*/
    private String mGroupId = null;
    /**レコメンド手法ID.*/
    private String mRecommendMethodId = null;
    /**dTVタイプ.*/
    private String mDtvType = null;
    /**クリップ可否.*/
    private boolean mClipExec = false;
    /**クリップ未/済.*/
    private boolean mClipStatus = false;
    /**クリップ状態に変更があった場合に真.*/
    private boolean mIsClipStatusUpdate = false;
    /**期限切れコンテンツフラグ(クリップ(VOD)のみ.*/
    private boolean mIsAfterLimitContents = false;
    /**サブタイトル.*/
    private String mSubTitle = null;
    /**description1.*/
    private String mDescription1 = null;
    /**description2.*/
    private String mDescription2 = null;
    /**description3.*/
    private String mDescription3 = null;
    /** 購入情報取得フラグ.*/
    private boolean mIsRental = false;
    /** コピーライト.*/
    private String mCopyright = null;
    /** コンテンツ尺長.*/
    private int mDurationTime;
    /** あらすじ情報表示状態.*/
    private boolean mIsShowSynop = false;
    /** 全文表示状態.*/
    private boolean mIsShowSynopAll = false;
    /** メタデータ.*/
    private VodMetaFullData mVodMetaFullData;
    /** 話数番号.*/
    private String mEpisodeNumber;
    /** 話数表記.*/
    private String mEpisodeNumberNotation;
    /** 他サービスエピソードか.*/
    private  boolean mIsOtherServiceEpisode = false;
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
    /** チャンネルのタイプ.*/
    private String mChannelType;
    /** 視聴可否ステータス.*/
    private ContentUtils.ViewIngType mViewIngType = ContentUtils.ViewIngType.NONE_STATUS;
    /** パーチャスID.*/
    private String mPurchaseId;
    /** サブパーチャスID.*/
    private String mSubPurchaseId;
    /** チャンネルパックのパーチャスID.*/
    private String mChannelPackPurchaseId;
    /** チャンネルパックのサブパーチャスID.*/
    private String mChannelPackSubPurchaseId;
    /**チャンネルサムネイルURL.*/
    private String mChannelThumListURL = null;
    /** OTT DRM保護フラグ.*/
    private String mOttDrmflg;

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
     * メタデータ取得.
     */
    public VodMetaFullData getVodMetaFullData() {
        return mVodMetaFullData;
    }

    /**
     * メタデータ設定.
     * @param vodMetaFullData メタデータ
     */
    public void setVodMetaFullData(final VodMetaFullData vodMetaFullData) {
        this.mVodMetaFullData = vodMetaFullData;
    }

    /**
     * あらすじ情報全文表示状態取得.
     */
    public boolean isSynopIsAllShow() {
        return mIsShowSynopAll;
    }

    /**
     * あらすじ情報全文表示状態設定.
     * @param isShowSynopAll isShowSynopAll
     */
    public void setIsShowSynopAll(final boolean isShowSynopAll) {
        this.mIsShowSynopAll = isShowSynopAll;
    }

    /**
     * あらすじ情報表示状態取得.
     */
    public boolean isShowSynop() {
        return mIsShowSynop;
    }

    /**
     * あらすじ情報表示状態設定.
     * @param mIsShowSynop mIsShowSynop
     */
    public void setIsShowSynop(final boolean mIsShowSynop) {
        this.mIsShowSynop = mIsShowSynop;
    }

    /**
     * コンテンツ尺長取得.
     */
    public int getDurTime() {
        return mDurationTime;
    }

    /**
     * コンテンツ尺長設定.
     * @param durationTime コンテンツ尺長
     */
    public void setDurTime(final int durationTime) {
        this.mDurationTime = durationTime;
    }

    /**
     * 日時（epoc秒）	(VOD用)コンテンツ自体の有効開始日時(PITのみ)取得.
     * @return 日時（epoc秒）	(VOD用)コンテンツ自体の有効開始日時(PITのみ)
     */
    public long getAvailStartDate() {
        return mAvailStartDate;
    }

    /**
     * 日時（epoc秒）	(VOD用)コンテンツ自体の有効開始日時(PITのみ)設定.
     * @param availStartDate 日時（epoc秒）	(VOD用)コンテンツ自体の有効開始日時(PITのみ)
     */
    public void setAvailStartDate(final long availStartDate) {
        this.mAvailStartDate = availStartDate;
    }

    /**
     * 日時（epoc秒）	(VOD用)コンテンツ自体の有効期限日時(PITのみ)取得.
     * @return 日時（epoc秒）	(VOD用)コンテンツ自体の有効期限日時(PITのみ)
     */
    public long getAvailEndDate() {
        return mAvailEndDate;
    }

    /**
     * 日時（epoc秒）	(VOD用)コンテンツ自体の有効期限日時(PITのみ)設定.
     * @param availEndDate 日時（epoc秒）	(VOD用)コンテンツ自体の有効期限日時(PITのみ)
     */
    public void setAvailEndDate(final long availEndDate) {
        this.mAvailEndDate = availEndDate;
    }

    /**
     * VOD配信開始日時  日時（epoc秒）取得.
     * @return VOD配信開始日時  日時（epoc秒）
     */
    public long getVodStartDate() {
        return mVodStartDate;
    }

    /**
     * VOD配信開始日時  日時（epoc秒）設定.
     * @param vodStartDate VOD配信開始日時  日時（epoc秒）
     */
    public void setVodStartDate(final long vodStartDate) {
        this.mVodStartDate = vodStartDate;
    }

    /**
     * VOD配信終了日時  日時（epoc秒）取得.
     * @return VOD配信終了日時  日時（epoc秒）
     */
    public long getVodEndDate() {
        return mVodEndDate;
    }

    /**
     * VOD配信終了日時  日時（epoc秒）設定.
     * @param vodEndDate VOD配信終了日時  日時（epoc秒）
     */
    public void setVodEndDate(final long vodEndDate) {
        this.mVodEndDate = vodEndDate;
    }

    /**
     * description1取得.
     * @return description1
     */
    public String getDescription1() {
        return mDescription1;
    }

    /**
     * description1設定.
     * @param mDescription1 description1
     */
    public void setDescription1(final String mDescription1) {
        this.mDescription1 = mDescription1;
    }

    /**
     * description2取得.
     * @return description2
     */
    public String getDescription2() {
        return mDescription2;
    }

    /**
     * description2設定.
     * @param mDescription2 description2
     */
    public void setDescription2(final String mDescription2) {
        this.mDescription2 = mDescription2;
    }

    /**
     * description3取得.
     * @return description3
     */
    public String getDescription3() {
        return mDescription3;
    }

    /**
     * description3設定.
     * @param mDescription3 description3
     */
    public void setDescription3(final String mDescription3) {
        this.mDescription3 = mDescription3;
    }

    /**
     * サブタイトル取得.
     * @return サブタイトル
     */
    public String getSubTitle() {
        return mSubTitle;
    }

    /**
     * サブタイトル設定.
     * @param mSubTitle サブタイトル
     */
    public void setSubTitle(final String mSubTitle) {
        this.mSubTitle = mSubTitle;
    }

    /**
     * ランキング順位取得.
     * @return ランキング順位
     */
    public String getRank() {
        return mRank;
    }

    /**
     * ランキング順位設定.
     * @param rank ランキング順位
     */
    public void setRank(final String rank) {
        this.mRank = rank;
    }

    /**
     * 時間取得.
     * @return 時間
     */
    public String getTime() {
        return mTime;
    }

    /**
     * 時間設定.
     * @param time 時間
     */
    public void setTime(final String time) {
        this.mTime = time;
    }

    /**
     * タイトル取得.
     * @return タイトル
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
     * エピソードタイトル取得.
     * @return  title エピソードタイトル
     */
    public String getEpisodeTitle() {
        return mEpisodeTitle;
    }

    /**
     * エピソードタイトル設定.
     * @param episodeTitle エピソードタイトル
     */
    public void setEpisodeTitle(final String episodeTitle) {
        this.mEpisodeTitle = episodeTitle;
    }

    /**
     * 評価ポイント取得.
     * @return 評価ポイント
     */
    public String getRatStar() {
        return mRatStar;
    }

    /**
     * 評価ポイント設定.
     * @param ratStar 評価ポイント
     */
    public void setRatStar(final String ratStar) {
        //不正な値が入った場合のためUtilメソッドを通してから格納
        this.mRatStar = StringUtils.toRatString(ratStar);
    }

    /**
     * サムネイルURL(リスト)取得.
     * @return サムネイルURL(リスト)
     */
    public String getThumURL() {
        return mThumListURL;
    }

    /**
     * サムネイルURL(リスト)設定.
     * @param thumURL サムネイルURL(リスト)
     */
    public void setThumURL(final String thumURL) {
        this.mThumListURL = thumURL;
    }

    /**
     * サムネイルURL(詳細画面)取得.
     * @return サムネイルURL(詳細画面)
     */
    public String getThumDetailURL() {
        return mThumDetailURL;
    }

    /**
     * サムネイルURL(詳細画面)設定.
     * @param mThumDetailURL サムネイルURL(詳細画面)
     */
    public void setThumDetailURL(final String mThumDetailURL) {
        this.mThumDetailURL = mThumDetailURL;
    }

//    /**
//     * 録画予約ステータス設定取得.
//     * @param status 録画予約ステータス
//     */
//    public void setRecordingReservationStatus(final int status) {
//        mRecordingReservationStatus = status;
//    }
//
//    /**
//     * 録画予約ステータス取得.
//     * @return 録画予約ステータス
//     */
//    public int getRecordingReservationStatus() {
//        return mRecordingReservationStatus;
//    }

    /**
     * チャンネル名取得.
     * @return チャンネル名
     */
    public String getChannelName() {
        return mChannelName;
    }

    /**
     * チャンネル名設定.
     * @param channelName チャンネル名
     */
    public void setChannelName(final String channelName) {
        this.mChannelName = channelName;
    }

    /**
     * 録画番組用 コピー残り回数取得.
     * @return 録画番組用 コピー残り回数
     */
    public int getAllowedUse() {
        return mAllowedUse;
    }

    /**
     * 録画番組用 コピー残り回数設定.
     * @param allowedUse 録画番組用 コピー残り回数
     */
    public void setAllowedUse(final int allowedUse) {
        this.mAllowedUse = allowedUse;
    }

    /**
     * クリップ状態取得.
     * @return クリップ状態
     */
    public String getSearchOk() {
        return mSearchOk;
    }

    /**
     * クリップ状態設定.
     * @param mSearchOk クリップ状態
     */
    public void setSearchOk(final String mSearchOk) {
        this.mSearchOk = mSearchOk;
    }

    /**
     * コンテンツ識別子取得.
     * @return コンテンツ識別子
     */
    public String getCrid() {
        return mCrid;
    }

    /**
     * コンテンツ識別子設定.
     * @param mCrid コンテンツ識別子
     */
    public void setCrid(final String mCrid) {
        this.mCrid = mCrid;
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
     * 番組種別取得.
     * @return 番組種別
     */
    public String getDispType() {
        return mDispType;
    }

    /**
     * 番組種別設定.
     * @param mDispType 番組種別
     */
    public void setDispType(final String mDispType) {
        this.mDispType = mDispType;
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
     * エピソードID取得.
     * @return エピソードID
     */
    public String getEpisodeId() {
        return mEpisodeId;
    }

    /**
     * エピソードID設定.
     * @param mEpisodeId エピソードID
     */
    public void setEpisodeId(final String mEpisodeId) {
        this.mEpisodeId = mEpisodeId;
    }

    /**
     * 番組のパレンタル設定値取得.
     * @return 番組のパレンタル設定値
     */
    public String getRValue() {
        return mRValue;
    }

    /**
     * 番組のパレンタル設定値設定.
     * @param mRValue 番組のパレンタル設定値
     */
    public void setRValue(final String mRValue) {
        this.mRValue = mRValue;
    }

    /**
     * 放送開始日時取得.
     * @return 放送開始日時
     */
    public String getPublishStartDate() {
        return mPublishStartDate;
    }

    /**
     * 放送開始日時設定.
     * @param mPublishStartDate 放送開始日時
     */
    public void setPublishStartDate(final String mPublishStartDate) {
        this.mPublishStartDate = mPublishStartDate;
    }

    /**
     * 放送終了日時取得.
     * @return 放送終了日時
     */
    public String getPublishEndDate() {
        return mPublishEndDate;
    }

    /**
     * 放送終了日時設定.
     * @param mPublishEndDate 放送終了日時
     */
    public void setPublishEndDate(final String mPublishEndDate) {
        this.mPublishEndDate = mPublishEndDate;
    }

    /**
     * コンテンツタイプ取得.
     * @return コンテンツタイプ
     */
    public String getContentsType() {
        return mContentsType;
    }

    /**
     * コンテンツタイプ設定.
     * @param mContentsType コンテンツタイプ
     */
    public void setContentsType(final String mContentsType) {
        this.mContentsType = mContentsType;
    }

    /**
     * 視聴通知判定取得.
     * @return 視聴通知判定
     */
    public String isNotify() {
        return mIsNotify;
    }

    /**
     * 視聴通知判定設定.
     * @param mIsNotify 視聴通知判定
     */
    public void setIsNotify(final String mIsNotify) {
        this.mIsNotify = mIsNotify;
    }

    /**
     * クリップボタン取得.
     * @return クリップボタン
     */
    public ImageView getClipButton() {
        return mClipButton;
    }

    /**
     * クリップボタン設定.
     * @param mClipButton クリップボタン
     */
    public void setClipButton(final ImageView mClipButton) {
        this.mClipButton = mClipButton;
    }

    /**
     * サービス種別取得.
     * @return サービス種別
     */
    public String getTvService() {
        return mTvService;
    }

    /**
     * サービス種別設定.
     * @param mTvService サービス種別
     */
    public void setTvService(final String mTvService) {
        this.mTvService = mTvService;
    }

    /**
     * dTVタイプ取得.
     * @return dTVタイプ
     */
    public String getDtv() {
        return mDtv;
    }

    /**
     * dTVタイプ設定.
     * @param mDtv dTVタイプ
     */
    public void setDtv(final String mDtv) {
        this.mDtv = mDtv;
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
     * あらすじ取得.
     * @return あらすじ
     */
    public String getSynop() {
        return mSynop;
    }

    /**
     * あらすじ設定.
     * @param mSynop あらすじ
     */
    public void setSynop(final String mSynop) {
        this.mSynop = mSynop;
    }

    /**
     * カテゴリID取得.
     * @return カテゴリID
     */
    public String getCategoryId() {
        return mCategoryId;
    }

    /**
     * カテゴリID設定.
     * @param mCategoryId カテゴリID
     */
    public void setCategoryId(final String mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    /**
     * 開始時刻取得.
     * @return 開始時刻
     */
    public String getStartViewing() {
        return mStartViewing;
    }

    /**
     * 開始時刻設定.
     * @param mStartViewing 開始時刻
     */
    public void setStartViewing(final String mStartViewing) {
        this.mStartViewing = mStartViewing;
    }

    /**
     * 終了時刻取得.
     * @return 終了時刻
     */
    public String getEndViewing() {
        return mEndViewing;
    }

    /**
     * 終了時刻設定.
     * @param mEndViewing 終了時刻
     */
    public void setEndViewing(final String mEndViewing) {
        this.mEndViewing = mEndViewing;
    }

    /**
     * reserved1取得.
     * @return reserved1
     */
    public String getReserved1() {
        return mReserved1;
    }

    /**
     * reserved1設定.
     * @param mReserved1 reserved1
     */
    public void setReserved1(final String mReserved1) {
        this.mReserved1 = mReserved1;
    }

    /**
     * reserved2取得.
     * @return reserved2
     */
    public String getReserved2() {
        return mReserved2;
    }

    /**
     * reserved2設定.
     * @param mReserved2 reserved2
     */
    public void setReserved2(final String mReserved2) {
        this.mReserved2 = mReserved2;
    }

    /**
     * reserved3取得.
     * @return reserved3
     */
    public String getReserved3() {
        return mReserved3;
    }

    /**
     * reserved3設定.
     * @param mReserved3 reserved3
     */
    public void setReserved3(final String mReserved3) {
        this.mReserved3 = mReserved3;
    }

    /**
     * reserved4取得.
     * @return reserved4
     */
    public String getReserved4() {
        return mReserved4;
    }

    /**
     * reserved4設定.
     * @param mReserved4 reserved4
     */
    public void setReserved4(final String mReserved4) {
        this.mReserved4 = mReserved4;
    }

    /**
     * reserved5取得.
     * @return reserved5
     */
    public String getReserved5() {
        return mReserved5;
    }

    /**
     * reserved5設定.
     * @param mReserved5 reserved5
     */
    public void setReserved5(final String mReserved5) {
        this.mReserved5 = mReserved5;
    }

    /**
     * reserved6取得.
     * @return reserved6
     */
    public String getReserved6() {
        return mReserved6;
    }

    /**
     * reserved6設定.
     * @param mReserved6 reserved6
     */
    public void setReserved6(final String mReserved6) {
        this.mReserved6 = mReserved6;
    }

    /**
     * reserved7取得.
     * @return reserved7
     */
    public String getReserved7() {
        return mReserved7;
    }

    /**
     * reserved7設定.
     * @param mReserved7 reserved7
     */
    public void setReserved7(final String mReserved7) {
        this.mReserved7 = mReserved7;
    }

    /**
     * reserved8取得.
     * @return reserved8
     */
    public String getReserved8() {
        return mReserved8;
    }

    /**
     * reserved8設定.
     * @param mReserved8 reserved8
     */
    public void setReserved8(final String mReserved8) {
        this.mReserved8 = mReserved8;
    }

    /**
     * reserved9取得.
     * @return reserved9
     */
    public String getReserved9() {
        return mReserved9;
    }

    /**
     * reserved9設定.
     * @param mReserved9 reserved9
     */
    public void setReserved9(final String mReserved9) {
        this.mReserved9 = mReserved9;
    }

    /**
     * reserved10取得.
     * @return reserved10
     */
    public String getReserved10() {
        return mReserved10;
    }

    /**
     * reserved10設定.
     * @param mReserved10 reserved10
     */
    public void setReserved10(final String mReserved10) {
        this.mReserved10 = mReserved10;
    }

    /**
     * クリップリクエストデータ取得.
     * @return クリップリクエストデータ
     */
    public ClipRequestData getRequestData() {
        return mRequestData;
    }

    /**
     * クリップリクエストデータ設定.
     * @param mRequestData クリップリクエストデータ
     */
    public void setRequestData(final ClipRequestData mRequestData) {
        this.mRequestData = mRequestData;
    }

    /**
     * ダウンロードフラグ取得.
     * @return ダウンロードフラグ
     */
    public int getDownloadFlg() {
        return mDownloadFlg;
    }

    /**
     * ダウンロードフラグ設定.
     * @param mDownloadFlg ダウンロードフラグ
     */
    public void setDownloadFlg(final int mDownloadFlg) {
        this.mDownloadFlg = mDownloadFlg;
    }

    /**
     * ダウンロードステータス（進捗）取得.
     * @return ダウンロードステータス（進捗）
     */
    public String getDownloadStatus() {
        return mDownloadStatus;
    }

    /**
     * ダウンロードステータス（進捗）設定.
     * @param mDownloadStatus ダウンロードステータス（進捗）
     */
    public void setDownloadStatus(final String mDownloadStatus) {
        this.mDownloadStatus = mDownloadStatus;
    }

    public boolean isDownloadBtnHide() {
        return mDownloadBtnHide;
    }

    public void setDownloadBtnHide(final boolean downloadBtnHide) {
        this.mDownloadBtnHide = downloadBtnHide;
    }

    /**
     * チャンネルID取得.
     * @return チャンネルID
     */
    public String getChannelId() {
        return mChannelId;
    }

    /**
     * チャンネルID設定.
     * @param mChannelId チャンネルID
     */
    public void setChannelId(final String mChannelId) {
        this.mChannelId = mChannelId;
    }

    /**
     * チャンネルNoを取得.
     *
     * @return チャンネルNo
     */
    public String getChannelNo() {
        return mChannelNo;
    }

    /**
     * チャンネルNoを設定.
     *
     * @param channelNo チャンネルNo
     */
    public void setChannelNo(final String channelNo) {
        this.mChannelNo = channelNo;
    }

    /**
     * おすすめ順取得.
     * @return おすすめ順
     */
    public String getRecommendOrder() {
        return mRecommendOrder;
    }

    /**
     * おすすめ順設定.
     * @param mRecommendOrder おすすめ順
     */
    public void setRecommendOrder(final String mRecommendOrder) {
        this.mRecommendOrder = mRecommendOrder;
    }

    /**
     * 画面ID取得.
     * @return 画面ID
     */
    public String getPageId() {
        return mPageId;
    }

    /**
     * 画面ID設定.
     * @param mPageId 画面ID
     */
    public void setPageId(final String mPageId) {
        this.mPageId = mPageId;
    }

    /**
     * ユーザグループID取得.
     * @return ユーザグループID
     */
    public String getGroupId() {
        return mGroupId;
    }

    /**
     * ユーザグループID設定.
     * @param mGroupId ユーザグループID
     */
    public void setGroupId(final String mGroupId) {
        this.mGroupId = mGroupId;
    }

    /**
     * レコメンド手法ID取得.
     * @return レコメンド手法ID
     */
    public String getRecommendMethodId() {
        return mRecommendMethodId;
    }

    /**
     * レコメンド手法ID設定.
     * @param mRecommendMethodId レコメンド手法ID
     */
    public void setRecommendMethodId(final String mRecommendMethodId) {
        this.mRecommendMethodId = mRecommendMethodId;
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
     * クリップ未/済取得.
     * @return クリップ未/済
     */
    public boolean isClipStatus() {
        return mClipStatus;
    }

    /**
     * クリップ未/済設定.
     * @param mClipStatus クリップ未/済
     */
    public void setClipStatus(final boolean mClipStatus) {
        this.mClipStatus = mClipStatus;
    }

    /**
     * ダウンロードパス設定.
     * @param path ダウンロードパス
     */
    public void setDlFileFullPath(final String path) {
        mDlFileFullPath = path;
    }

    /**
     * ダウンロードパス取得.
     * @return ダウンロードパス
     */
    public String getDlFileFullPath() {
        return mDlFileFullPath;
    }

    /**
     * mobileViewingFlg取得.
     * @return mobileViewingFlg
     */
    public String getMobileViewingFlg() {
        return mobileViewingFlg;
    }

    /**
     * mobileViewingFlg設定.
     * @param mobileViewingFlg mobileViewingFlg
     */
    public void setMobileViewingFlg(final String mobileViewingFlg) {
        this.mobileViewingFlg = mobileViewingFlg;
    }

    /**
     * 作品種別取得.
     * @return 作品種別
     */
    public String getTitleKind() {
        return mTitleKind;
    }

    /**
     * 作品種別設定.
     * @param titleKind 作品種別
     */
    public void setTitleKind(final String titleKind) {
        this.mTitleKind = titleKind;
    }

    /**
     * chsvod取得.
     * @return chsvod
     */
    public String getChsVod() {
        return mChsVod;
    }

    /**
     * chsvod設定.
     * @param mChsVod chsvod
     */
    public void setChsVod(final String mChsVod) {
        this.mChsVod = mChsVod;
    }

    /**
     * estFlg取得.
     * @return estFlg
     */
    public String getEstFlg() {
        return mEstFlg;
    }

    /**
     * estFlg.
     * @param mEstFlg estFlg
     */
    public void setEstFlg(final String mEstFlg) {
        this.mEstFlg = mEstFlg;
    }

    /**
     * クリップ状態に変更があった場合に真取得.
     * @return クリップ状態に変更があった場合に真
     */
    public boolean isClipStatusUpdate() {
        return mIsClipStatusUpdate;
    }

    /**
     * クリップ状態設定.
     * @param mClipStatusUpdate クリップ状態に変更があった場合に真
     */
    public void setClipStatusUpdate(final boolean mClipStatusUpdate) {
        this.mIsClipStatusUpdate = mClipStatusUpdate;
    }

    /**
     * setEpisodeNumber.
     * @param episodeNumber　episodeNumber
     */
    public void setEpisodeNumber(final String episodeNumber) {
        this.mEpisodeNumber = episodeNumber;
    }

    /**
     * getEpisodeNumber.
     * @return episodeNumber
     */
    public String getEpisodeNumber() {
        return mEpisodeNumber;
    }

    /**
     * getEpisodeNumberNotation.
     * @return episodeNumberNotation
     */
    public String getEpisodeNumberNotation() {
        return mEpisodeNumberNotation;
    }

    /**
     * setEpisodeNumberNotation.
     * @param episodeNumberNotation episodeNumberNotation
     */
    public void setEpisodeNumberNotation(final String episodeNumberNotation) {
        this.mEpisodeNumberNotation = episodeNumberNotation;
    }

    /**
     * isOtherServiceEpisode.
     * @return otherServiceEpisode
     */
    public boolean isOtherServiceEpisode() {
        return mIsOtherServiceEpisode;
    }

    /**
     * setOtherServiceEpisode.
     * @param otherServiceEpisode otherServiceEpisode
     */
    public void setOtherServiceEpisode(final boolean otherServiceEpisode) {
        mIsOtherServiceEpisode = otherServiceEpisode;
    }
    /**
     * 期限切れコンテンツフラグ取得.
     *
     * @return 期限切れコンテンツフラグ
     */
    public boolean isAfterLimitContents() {
        return mIsAfterLimitContents;
    }

    /**
     * 期限切れコンテンツフラグ設定.
     *
     * @param mIsAfterLimitContents 期限切れコンテンツフラグ
     */
    public void setIsAfterLimitContents(final boolean mIsAfterLimitContents) {
        this.mIsAfterLimitContents = mIsAfterLimitContents;
    }

    /**
     * 子コンテンツを持っているか.
     * @return 子コンテンツ持ちのコンテンツの場合、true
     */
    public boolean hasChildContentList() {
        boolean result = false;
        do {
            if (mDispType == null) {
                break;
            }
            if (mDispType.equals( DtvtConstants.DISP_TYPE_SERIES_SVOD)
                    || mDispType.equals(DtvtConstants.DISP_TYPE_WIZARD)
                    || mDispType.equals(DtvtConstants.DISP_TYPE_VIDEO_PACKAGE)
                    || mDispType.equals(DtvtConstants.DISP_TYPE_SUBSCRIPTION_PACKAGE)) {
                result = true;
                break;
            }
        } while (false);
        return result;
    }

    /**
     * 購入情報取得フラグ.
     * @return 購入情報取得フラグ
     */
    public boolean isRental() {
        return mIsRental;
    }

    /**
     * 購入情報取得フラグ.
     * @param isRental 購入情報取得フラグ
     */
    public void setIsRental(final boolean isRental) {
        this.mIsRental = isRental;
    }

    /**
     * copyright取得.
     * @return copyright
     */
    public String getCopyright() {
        return mCopyright;
    }

    /**
     * copyright.
     * @param copyright copyright
     */
    public void setCopyright(final String copyright) {
        this.mCopyright = copyright;
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
     * チャンネルのタイプ取得.
     * @return チャンネルのタイプ
     */
    public String getChannelType() { return mChannelType; }
    /**
     * チャンネルのタイプ設定.
     * @param channelType チャンネルのタイプ
     */
    public void setChannelType(String channelType) { this.mChannelType = channelType; }

    public ContentUtils.ViewIngType getViewIngType() {
        return mViewIngType;
    }

    public void setViewIngType(ContentUtils.ViewIngType viewIngType) {
        this.mViewIngType = viewIngType;
    }
    /**
     * puidを取得.
     * @return パーチャスID
     */
    public String getPurchaseId() {
        return mPurchaseId;
    }
    /**
     * puidを設定.
     * @param puId パーチャスID
     */
    public void setPurchaseId(final String puId) {
        this.mPurchaseId = puId;
    }
    /**
     * sub_puidを取得.
     * @return サブパーチャスID
     */
    public String getSubPurchaseId() {
        return mSubPurchaseId;
    }
    /**
     * sub_puidを設定.
     * @param subPuId サブパーチャスID
     */
    public void setSubPurchaseId(final String subPuId) {
        this.mSubPurchaseId = subPuId;
    }
    /**
     * CHPACKのpuidを取得.
     * @return チャンネルパックのパーチャスID
     */
    public String getChannelPackPurchaseId() {
        return mChannelPackPurchaseId;
    }
    /**
     * CHPACKのpuidを設定.
     * @param chPackPuId チャンネルパックのパーチャスID
     */
    public void setChannelPackPurchaseId(final String chPackPuId) {
        this.mChannelPackPurchaseId = chPackPuId;
    }
    /**
     * CHPACKのsub_puidを取得.
     * @return チャンネルパックのサブパーチャスID
     */
    public String getChannelPackSubPurchaseId() {
        return mChannelPackSubPurchaseId;
    }
    /**
     * CHPACKのsub_puidを設定.
     * @param chPackSubPuId チャンネルパックのサブパーチャスID
     */
    public void setChannelPackSubPurchaseId(final String chPackSubPuId) {
        this.mChannelPackSubPurchaseId = chPackSubPuId;
    }
    /**
     * チャンネルサムネイルURLを取得.
     * @return チャンネルサムネイルURL
     */
    public String getChannelThumListURL() {
        return mChannelThumListURL;
    }
    /**
     * チャンネルサムネイルURLを設定.
     * @param channelThumListURL チャンネルサムネイルURL
     */
    public void setChannelThumListURL(String channelThumListURL) {
        this.mChannelThumListURL = channelThumListURL;
    }

    /**
     * OTT DRM保護フラグ設定.
     * @param ottDrmflg OTT DRM保護フラグ
     */
    public void setOttDrmflg(final String ottDrmflg) {
        this.mOttDrmflg = ottDrmflg;
    }

    /**
     * OTT DRM保護フラグ取得.
     * @return TT DRM保護フラグ
     */
    public String getOttDrmflg() {
        return mOttDrmflg;
    }
}

