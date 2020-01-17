package com.example.detaildemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.detaildemo.R;
import com.example.detaildemo.activity.BaseActivity;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.bean.ClipRequestData;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.provider.data.OtherContentsDetailData;
import com.example.detaildemo.utils.ContentDetailUtils;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DataBaseUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.UserInfoUtils;
import com.example.detaildemo.view.RatingBarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  * コンテンツ詳細画面表示用Fragment.
 */
public class DtvContentsDetailFragment extends Fragment{
    /** Appコンテクスト.*/
    private Context mContext = null;
    /** コンテクスト.*/
    private Context mActivity = null;
    /** フラグメントビュー.*/
    private View mView = null;
    /** 詳細情報.*/
    private OtherContentsDetailData mOtherContentsDetailData = null;
    /** スタッフビュー.*/
    private LinearLayout mStaffLayout = null;
    /** コピーライト.*/
    private TextView mTxtCopyright = null;
    /** タイトル/詳細一部文字.*/
    private TextView mTxtTitleShortDetail = null;
    /** タイトル/詳細全文字.*/
    private TextView mTxtTitleAllDetail = null;
    /** 評価.*/
    private RatingBarLayout mRatingBar;
    /** moreボタン.*/
    private TextView mTxtMoreText = null;
    /** ヘッダー.*/
    private TextView mTextHeader = null;
    /** サブヘッダー.*/
    private TextView mTextSubHeader = null;
    /** サービスアイコン.*/
    private ImageView mImgServiceIcon = null;
    /** サービスアイコン dTV(白背景ロゴ).*/
    private ImageView mImgServiceIconDtv = null;
    /** 「モバイル専用」ラベルを表示するビュー.*/
    private ImageView mImgServiceForMobile = null;
    /** チャンネル名.*/
    private TextView mTxtChannelName = null;
    /** チャンネル日付.*/
    private TextView mTxtChannelDate = null;
    /** 全文表示フラグ.*/
    private boolean mIsAllText = false;
    /** クリップボタン.*/
    private ImageView mClipButton = null;
    /** 録画ボタン.*/
    private ImageView mRecordButton = null;
//    /** 録画リスナー.*/
//    private RecordingReservationIconListener mIconClickListener = null;
    /** フラグメント表示リスナー.*/
    private ContentsDetailFragmentListener mContentsDetailFragmentListener = null;
    /** 正常取得成功.*/
    private boolean mFinishFlg = false;
    /** ビュー描画成功.*/
    private boolean mDateFlg = false;
    /** スタッフ文字サイズ(title).*/
    private final static int TEXT_SIZE_12 = 12;
    /** スタッフ文字サイズ(内容).*/
    private final static int TEXT_SIZE_14 = 14;
    /** パラメータ名「4kflg」 1.*/
    private final static int LABEL_STATUS_4KFLG_1 = 1;
    /** adinfo_array の adtype 9.*/
    private final static int LABEL_STATUS_ADTYPE_9 = 9;
    /** adinfo_array の adtype 2.*/
    private final static int LABEL_STATUS_ADTYPE_2 = 2;
    /** start padding.*/
    private final static int START_PADDING = 16;

    /**フラグメントスクロールリスナー.*/
    public interface ContentsDetailFragmentListener {
        /**
         * Fragment見えるのコールバック.
         * @param isVisibleToUser    true:表示 false:非表示
         * @param dtvContentsDetailFragment フラグメント
         */
        void onUserVisibleHint(boolean isVisibleToUser, DtvContentsDetailFragment dtvContentsDetailFragment);
    }

    /**
     * リスナー設定.
     * @param mContentsDetailFragmentListener mContentsDetailFragmentListener
     */
    public void setContentsDetailFragmentScrollListener(final ContentsDetailFragmentListener mContentsDetailFragmentListener) {
        this.mContentsDetailFragmentListener = mContentsDetailFragmentListener;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
        mActivity = null;
    }

    @Override
    public Context getContext() {
        this.mActivity = getActivity();
        return mActivity;
    }

    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        //コンテンツ詳細表示に必要なデータを取得する
        mOtherContentsDetailData = getArguments().getParcelable(ContentUtils.RECOMMEND_INFO_BUNDLE_KEY);
        return initView(container);
    }


    /**
     * 各タブ画面は別々に実現して表示されること.
     *
     * @param container コンテナ
     * @return view
     */
    private View initView(final ViewGroup container) {
        DTVTLogger.start();
        if (null == mView) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.dtv_contents_detail_fragment, container, false);
        }
        //サービス/提供元
        mImgServiceIcon = mView.findViewById(R.id.dtv_contents_detail_fragment_service_provider);
        mImgServiceIconDtv = mView.findViewById(R.id.dtv_contents_detail_fragment_service_provider_dtv);
        mImgServiceForMobile = mView.findViewById(R.id.dtv_contents_detail_fragment_service_provider_for_mobile);

        //ヘッダー
        mTextHeader = mView.findViewById(R.id.dtv_contents_detail_fragment_contents_title);
        mTextSubHeader = mView.findViewById(R.id.dtv_contents_detail_fragment_contents_sub_title);
        mTxtChannelName = mView.findViewById(R.id.dtv_contents_detail_fragment_channel_name);
        mTxtChannelDate = mView.findViewById(R.id.dtv_contents_detail_fragment_channel_date);
        //省略
        mTxtTitleShortDetail = mView.findViewById(R.id.dtv_contents_detail_fragment_detail_info);
        //評価
        mRatingBar = mView.findViewById(R.id.dtv_contents_detail_fragment_rating);
        //全表示
        mTxtTitleAllDetail = mView.findViewById( R.id.dtv_contents_detail_fragment_all_info);
        //more
        mTxtMoreText = mView.findViewById(R.id.dtv_contents_detail_fragment_more_button);
        //スタッフ情報
        mStaffLayout = mView.findViewById(R.id.dtv_contents_detail_fragment_staff);
        //コピーライト
        mTxtCopyright = mView.findViewById(R.id.dtv_contents_detail_fragment_copyright);
        mTxtMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //moreボタン押下で、全文表示に切り替える
                mIsAllText = true;
                mTxtTitleShortDetail.setVisibility(View.GONE);
                mTxtTitleAllDetail.setVisibility(View.VISIBLE);
                mTxtMoreText.setVisibility(View.GONE);
            }
        });
        mRecordButton = mView.findViewById(R.id.dtv_contents_detail_fragment_rec_iv);
        mClipButton = mView.findViewById(R.id.contents_detail_clip_button);

        if (mOtherContentsDetailData != null) {
            setClipButton(mClipButton);
            setDetailData();
        } else {
            mOtherContentsDetailData = new OtherContentsDetailData();
        }
        return mView;
    }

    /**
     * クリップボタンの表示/非表示を.
     *
     * @param clipButton クリップボタン
     */
    private void setClipButton(final ImageView clipButton) {

        //他サービスならクリップボタン非表示
        if (mOtherContentsDetailData != null) {
            if (mOtherContentsDetailData.isClipExec()) {
                clipButton.setVisibility(View.VISIBLE);
                if (!UserInfoUtils.getClipActive(mContext)) {
                    //未ログイン又は未契約時はクリップボタンを非活性にする
                    clipButton.setImageResource(R.mipmap.icon_tap_circle_normal_clip);
                } else {
                    if (mOtherContentsDetailData.isClipStatus()) {
                        clipButton.setImageResource(R.drawable.common_clip_detail_active_selector);
                        clipButton.setTag( BaseActivity.CLIP_ACTIVE_STATUS);
                    } else {
                        clipButton.setImageResource(R.drawable.common_clip_detail_normal_selector);
                        clipButton.setTag(BaseActivity.CLIP_OPACITY_STATUS);
                    }

                    clipButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            //クリップボタンイベント
                            ClipRequestData data = setClipData(mOtherContentsDetailData.getVodMetaFullData());
                            //同じ画面で複数回クリップ操作をした時にクリップ済/未の判定ができないため、タグでクリップ済/未を判定する
                            Object clipTag = mClipButton.getTag();
                            if (clipTag.equals(BaseActivity.CLIP_ACTIVE_STATUS)) {
                                data.setClipStatus(true);
                            } else {
                                data.setClipStatus(false);
                            }
                            if (getContext() != null) {
                                ((BaseActivity) mActivity).sendClipRequest(data, clipButton);
                            }
                        }
                    });
                }
            } else {
                clipButton.setVisibility(View.GONE);
            }
        } else {
            clipButton.setVisibility(View.GONE);
        }
    }

    /**
     * クリップリクエストに必要なデータを作成する(コンテンツ詳細用).
     *
     * @param metaFullData VODメタデータ
     * @return Clipリクエストに必要なデータ
     */
    private static ClipRequestData setClipData(final VodMetaFullData metaFullData) {
        ClipRequestData requestData = null;
        if (metaFullData != null) {
            //コンテンツ詳細は、メタデータを丸ごと持っているため、そのまま利用する
            requestData = new ClipRequestData();
            requestData.setDispType(metaFullData.getDisp_type());
            requestData.setContentType(metaFullData.getmContent_type());
            requestData.setCrid(metaFullData.getCrid());
            requestData.setServiceId(metaFullData.getmService_id());
            requestData.setEventId(metaFullData.getmEvent_id());
            requestData.setTitleId(metaFullData.getTitle_id());
            requestData.setTitle(metaFullData.getTitle());
            requestData.setRValue(metaFullData.getR_value());
            requestData.setLinearStartDate(String.valueOf(metaFullData.getPublish_start_date()));
            requestData.setLinearEndDate(String.valueOf(metaFullData.getPublish_end_date()));
            requestData.setSearchOk(metaFullData.getmSearch_ok());
            requestData.setTvService(metaFullData.getmTv_service());
            requestData.setVodStartDate(metaFullData.getmVod_start_date());
            requestData.setIsNotify(metaFullData.getDisp_type(), metaFullData.getmContent_type(),
                    metaFullData.getmVod_start_date(), metaFullData.getmTv_service(), metaFullData.getDtv(), String.valueOf(metaFullData.getM4kflg()));
        }
        return requestData;
    }


    /**
     * 各Viewにコンテンツの詳細情報を渡す.
     */
    private void setDetailData() {
        //ネットワーク接続が無い場合は通信処理は即座に終わるので、フラグメントの初期化終了前にここにきてしまう。
        //その場合は、処理をスキップする判定
        if (mTextHeader == null) {
            //ヌルの場合は何もできないので帰る
            DTVTLogger.debug("mTextHeader not init");
            return;
        }
        if (mOtherContentsDetailData.isOttContent()) {
            //OTTコンテンツはリモコン非表示のため、paddingBottomは要らない
            int paddingStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    START_PADDING, mContext.getResources().getDisplayMetrics());
            mView.setPadding(paddingStart, 0, 0, 0);
        }

        //タイトル
        mTextHeader.setText(mOtherContentsDetailData.getTitle());
        //サブタイトル
        if (!TextUtils.isEmpty(mOtherContentsDetailData.getEpititle())) {
            mTextSubHeader.setText(mOtherContentsDetailData.getEpititle());
        } else {
            mTextSubHeader.setVisibility(View.GONE);
        }
        //サービスアイコン
        int serviceIcon = ContentUtils.getContentsServiceName(mOtherContentsDetailData.getServiceId());
        if (mOtherContentsDetailData.getServiceId() == ContentUtils.DAZN_CONTENTS_SERVICE_ID) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.contents_detail_service_icon_label_dazn_width_height),
                    (int) getResources().getDimension(R.dimen.contents_detail_service_icon_label_dazn_width_height));
            mImgServiceIcon.setLayoutParams(layoutParams);
        }
        mImgServiceIcon.setImageResource(serviceIcon);
        String dtv = mOtherContentsDetailData.getDtv();

        //dtvの場合
        if (ContentUtils.DTV_FLAG_ONE.equals(dtv)) {
            mImgServiceIconDtv.setVisibility(View.VISIBLE);
            mImgServiceIconDtv.setImageResource(R.mipmap.label_service_dtv_white);
        }

        //OTTの場合
        if (mOtherContentsDetailData.isOttContent()) {
            // 「モバイル専用」タブを表示
            mImgServiceForMobile.setVisibility(View.VISIBLE);
            mImgServiceForMobile.setImageResource(R.mipmap.label_for_mobile);

            // 録画予約ボタンを非表示
            mRecordButton.setVisibility(View.GONE);
        }
        //評価値設定
        setRatingBar();
        //チャンネル名
        if (ContentUtils.isChanelNameDisplay(mOtherContentsDetailData.getContentCategory())
                || (mOtherContentsDetailData.getChannelName() != null && !mOtherContentsDetailData.getChannelName().isEmpty())) {
            mTxtChannelName.setVisibility(View.VISIBLE);
            mTxtChannelName.setText(mOtherContentsDetailData.getChannelName());
        } else {
            mTxtChannelName.setVisibility(View.GONE);
        }
        setLabelStatus();
        //日付
        final String date = mOtherContentsDetailData.getChannelDate();
        if (!TextUtils.isEmpty(date)) {
            mTxtChannelDate.setVisibility(View.VISIBLE);
            SpannableString spannableString = new SpannableString(date);
            int subCount = 0;
            if (date.contains(getString(R.string.contents_detail_hikari_d_channel_miss_viewing))) {
                subCount = 3;
            }
            //「見逃し」は黄色文字で表示する
            spannableString.setSpan(new ForegroundColorSpan( ContextCompat.getColor(getContext(), R.color.contents_detail_video_miss_color)),
                    0, subCount, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTxtChannelDate.setText(spannableString);

            if (mTxtChannelDate.getText().toString().contains(DateUtils.DATE_HYPHEN)) {
                mTxtChannelDate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mDateFlg) {
                            mTxtChannelDate.getViewTreeObserver().removeOnGlobalLayoutListener(this);//リスナー解除。
                        }
                        if (mTxtChannelDate.getLineCount() > 0) {
                            if (mTxtChannelDate.getLineCount() > 1) { //TextViewの内容が2行以上の場合
                                String dateStr = date.replace( DateUtils.DATE_HYPHEN, DateUtils.DATE_HYPHEN + DateUtils.DATE_NEWLINE); //改行する。
                                mTxtChannelDate.setText(dateStr);
                            }
                            mDateFlg = true;
                        }
                    }
                });
            }
        } else {
            mTxtChannelDate.setVisibility(View.GONE);
        }
        if (mOtherContentsDetailData.getStaffInfo() != null) {
            setStaff();
        } else {
            mStaffLayout.setVisibility(View.GONE);
        }
        setClipButton(mClipButton);
        showDescription();
        showCopyRight();
    }

    /**
     * ラベルを設定する.
     */
    private void setLabelStatus() {
        LinearLayout labelStatus = mView.findViewById(R.id.dtv_contents_detail_fragment_label_status_ll);
        labelStatus.removeAllViews();
        List<Integer> labelStatusList = new ArrayList<>();
        //NEW アイコン
        if (ContentUtils.isOtherService(mOtherContentsDetailData.getServiceId())) {
            ContentUtils.ContentsType contentsType = ContentUtils.
                    getContentsTypeByRecommend(mOtherContentsDetailData.getServiceId(), mOtherContentsDetailData.getCategoryId());
            if (contentsType != ContentUtils.ContentsType.TV) {
                if (!TextUtils.isEmpty(mOtherContentsDetailData.getmStartDate())) {
                    if (DateUtils.isInOneWeek(mOtherContentsDetailData.getmStartDate())) {
                        labelStatusList.add(R.mipmap.label_status_new);
                    }
                }
            }
        } else {
            if (DataBaseUtils.isNumber(mOtherContentsDetailData.getmStartDate())) {
                if (DateUtils.isInOneWeek(Long.parseLong(mOtherContentsDetailData.getmStartDate()))) {
                    labelStatusList.add(R.mipmap.label_status_new);
                }
            }
        }
        //4Kアイコン
        if (LABEL_STATUS_4KFLG_1 == mOtherContentsDetailData.getM4kflg()) {
            labelStatusList.add(R.mipmap.label_status_4k);
        }
//        //音声アイコン
//        setAdtype(labelStatusList);
//        //コピー制限アイコン
//        setCopy(labelStatusList);
//        //Rアイコン
//        setRvalue(labelStatusList);
        if (labelStatusList.size() > 0) {
            labelStatus.setVisibility(View.VISIBLE);
            for (int i = 0; i < labelStatusList.size(); i++) {
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i != 0) {
                    if (isAdded()) {
                        imageParams.setMargins((int) getResources().getDimension(R.dimen.contents_detail_clip_margin),
                                (int) getResources().getDimension(R.dimen.contents_tab_top_margin),
                                (int) getResources().getDimension(R.dimen.contents_tab_top_margin),
                                (int) getResources().getDimension(R.dimen.contents_tab_top_margin));
                    }
                }
                Context context = getContext();
                if (context != null) {
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(labelStatusList.get(i));
                    imageView.setLayoutParams(imageParams);
                    labelStatus.addView(imageView);
                }
            }
        } else {
            labelStatus.setVisibility(View.GONE);
        }
    }

    /**
     * あらすじ情報の更新.
     */
    private void showDescription() {
        String contentsDetailInfo = selectDetail();
        if (!TextUtils.isEmpty(contentsDetailInfo)) {
            final String replaceString = contentsDetailInfo.replace("\\n", "\n");
            mTxtTitleShortDetail.setText(replaceString);
            mTxtTitleAllDetail.setText(replaceString);
        }
    }

    /**
     * スタッフ情報を表示する.
     */
    private void setStaff() {
        if (mStaffLayout == null) {
            return;
        }
        Map<String, String> staffInfo = mOtherContentsDetailData.getStaffInfo();
        mStaffLayout.setVisibility(View.VISIBLE);
        mStaffLayout.removeAllViews();
        Context context = getContext();
        if (context != null && staffInfo != null) {
            for (String key: staffInfo.keySet()) {
                TextView categoryName = ContentDetailUtils.createStaffTextView(context);
                categoryName.setText(key);
                categoryName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_12);
                categoryName.setLayoutParams(ContentDetailUtils.createStaffTextViewParams(context));
                mStaffLayout.addView(categoryName);
                TextView detailName = ContentDetailUtils.createStaffTextView(context);
                detailName.setText(staffInfo.get(key));
                detailName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_14);
                detailName.setLayoutParams(ContentDetailUtils.createStaffTextViewParams(context));
                mStaffLayout.addView(detailName);
            }
        }
    }

    /**
     * 次の優先順位で、商品詳細を返却する.
     * 1:商品詳細2(あらすじ)
     * 2:商品詳細1(解説)
     * 3:商品詳細3(みどころ)
     *
     * @return 商品詳細文字列
     */
    private String selectDetail() {
        if (!TextUtils.isEmpty(mOtherContentsDetailData.getDetail())) {
            //"あらすじ"を返却
            return mOtherContentsDetailData.getDetail();
        } else {
            return "";
        }
    }

    /**
     * コピーライトの更新.
     */
    private void showCopyRight() {
        String copyRight = mOtherContentsDetailData.getCopyRight();
        if (!TextUtils.isEmpty(copyRight)) {
            mTxtCopyright.setVisibility(View.VISIBLE);
            final String replaceString = copyRight.replace("\\n", "\n");
            mTxtCopyright.setText(replaceString);
        } else {
            mTxtCopyright.setVisibility(View.GONE);
        }
    }

    /**
     * 評価値レイアウト設定.
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    private void setRatingBar() {
        //評価値の表示非表示判定
        ContentUtils.ContentsType contentsType = mOtherContentsDetailData.getContentCategory();
        if (contentsType != null) {
            switch (contentsType) {
                //VODの場合
                case HIKARI_TV_VOD:
                case HIKARI_IN_DTV:
                case HIKARI_IN_DCH_MISS:
                case HIKARI_IN_DCH_RELATION:
                case DCHANNEL_VOD_OVER_31:
                case DCHANNEL_VOD_31:
                    //評価
                    mRatingBar.setVisibility(View.VISIBLE);
                    mRatingBar.setMiniFlg(false);
                    mRatingBar.setRating((float) mOtherContentsDetailData.getRating());
                    break;
                default:
                    mRatingBar.setVisibility(View.GONE);
                    break;
            }
        }
    }


    /**
     * 録画予約アイコンの表示ステータス確認.
     * @return  true:表示中
     * 　　　　　false:非表示
     */
    public boolean checkVisibilityRecordingReservationIcon() {
        return mRecordButton.getVisibility() == View.VISIBLE;
    }

    /**
     * 録画予約アイコンの 表示/非表示 を切り替え.
     *
     * @param viewIngType 視聴可否判定結果
     * @param contentsType コンテンツ種別
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    public void changeVisibilityRecordingReservationIcon(final ContentUtils.ViewIngType viewIngType, final ContentUtils.ContentsType contentsType) {
        DTVTLogger.start();
        if (mRecordButton != null) {
            mRecordButton.setVisibility(View.GONE);
            //視聴可否判定結果 コンテンツ種別取得済みかつひかりTV番組のみ録画ボタンを表示
            if (viewIngType != null
                    && !viewIngType.equals(ContentUtils.ViewIngType.NONE_STATUS)
                    && contentsType != null
                    && ContentUtils.isHikariTvProgram(contentsType)) {
                //未ログイン又は未契約時は録画ボタンを非活性
                if (!UserInfoUtils.getClipActive(mContext)
                        || !ContentUtils.isEnableDisplay(viewIngType)
                        || !ContentUtils.isRecordButtonDisplay(contentsType)) {
                    if (contentsType.equals(ContentUtils.ContentsType.HIKARI_TV_WITHIN_TWO_HOUR)
                            || contentsType.equals(ContentUtils.ContentsType.HIKARI_TV_NOW_ON_AIR)
                            || contentsType.equals(ContentUtils.ContentsType.HIKARI_IN_DCH_TV_WITHIN_TWO_HOUR)
                            || contentsType.equals(ContentUtils.ContentsType.HIKARI_IN_DCH_TV_NOW_ON_AIR)) {
                        //放送開始時間が2時間以内なら録画ボタン非表示
                        mRecordButton.setVisibility(View.GONE);
                    } else {
                        mRecordButton.setImageResource(R.mipmap.icon_tap_circle_normal_rec);
                        mRecordButton.setVisibility(View.VISIBLE);
                        mRecordButton.setOnClickListener(null);
                    }
                } else {
                    mRecordButton.setImageResource(R.drawable.recording_reservation_rec_icon_selector);
                    mRecordButton.setVisibility(View.VISIBLE);
                }
            }
        }
        noticeRefresh();
        DTVTLogger.end();
    }

    /**
     * 再取得防止ため.
     */
    public boolean isRequestFinish() {
        return mFinishFlg;
    }

    /**
     * 詳細情報の更新.
     */
    public void noticeRefresh() {
        setDetailData();
    }

    /**
     * 詳細情報を更新する.
     *
     * @return 詳細情報
     */
    public OtherContentsDetailData getOtherContentsDetailData() {
        if (mOtherContentsDetailData == null) {
            mOtherContentsDetailData = new OtherContentsDetailData();
        }
        return mOtherContentsDetailData;
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        if (null != mContentsDetailFragmentListener) {
            mContentsDetailFragmentListener.onUserVisibleHint(isVisibleToUser, this);
        }
    }

    /**
     * 詳細情報を設定する.
     *
     * @param otherContentsDetailData 詳細情報
     */
    public void setOtherContentsDetailData(final OtherContentsDetailData otherContentsDetailData) {
        mOtherContentsDetailData = otherContentsDetailData;
    }

    /**
     * チャンネル情報の更新.
     */
    public void refreshChannelInfo() {
        if (mOtherContentsDetailData != null
                && !TextUtils.isEmpty(mOtherContentsDetailData.getChannelName())) {
            if (mTxtChannelName != null) {
                mTxtChannelName.setVisibility(View.VISIBLE);
                mTxtChannelName.setText(mOtherContentsDetailData.getChannelName());
            }
        }
    }
}
