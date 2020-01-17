package com.example.detaildemo.view.player;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detaildemo.R;
import com.example.detaildemo.adapter.PlayerChannelListRecyclerViewAdapter;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.utils.ContentDetailUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.ReducePlayerViewWrapper;

import java.util.List;

/**
 * Ott（全画面）プレイヤーレイアウト.
 */
public class OttFullScreenPlayerViewLayout extends RelativeLayout {

    /** アスペクト比(16:9)の16.*/
    private static final int SCREEN_RATIO_WIDTH_16 = 16;
    /** アスペクト比(16:9)の9.*/
    private static final int SCREEN_RATIO_HEIGHT_9 = 9;
    /**コントローラービューを非表示になるまでの待ち時間.*/
    private static final long HIDE_IN_3_SECOND = 3 * 1000;
    /** フルスクリーン状態でスワイプ動作アニメーション時間.*/
    private static final long SWIPE_ANIMATION_TIME = 200;
    /** 100 パーセント(float: 1.0f) .*/
    private static final float RATIO_100_PERSENT = 1.0f;
    /**ハンドラー.*/
    private static final Handler sCtrlHandler = new Handler( Looper.getMainLooper());
    /** コンテキスト.*/
    private Context mContext;
    /** アクティビティ.*/
    private Activity mActivity;
    /**プログレスバー処理.*/
    private LinearLayout mProgressBarLayout;
    /** SurfaceView.*/
    private SurfaceView mSurfaceView;
    /** チャンネルリストリサイクルビュー.*/
    public RecyclerView mChannelListRecyclerView;
    /** チャンネルリストビュー.*/
    public ScrollView mChannelListScrollView;
    /** スワイプ動作により、ビューのサイズとマージンを変更する用.*/
    private ReducePlayerViewWrapper mPlayerViewWrapper;
    /** プレイヤーレイアウト画面(全画面)に表示するチャンネルリストのアダプタ.*/
    private PlayerChannelListRecyclerViewAdapter mPlayerChannelListViewAdapter;
    /**プレイヤーイベントタイプ.*/
    private OttPlayerEventType mPlayerEventType = OttPlayerEventType.NONE;
    /**再生コールバック.*/
    private FullScreenPlayerViewListener mFullScreenPlayerViewListener;

    /** チャンネルリストビュー.*/
    public RelativeLayout mChannelListView;
    /** コンテンツ情報ビュー.*/
    private RelativeLayout mContentDetailView;
    /** 録画コントローラビューRelativeLayout.*/
    private RelativeLayout mRecordCtrlView;
    /**ビデオ再生停止TextView.*/
    private FrameLayout mVideoPlayPause;
    /**再生ImageView.*/
    private ImageView mVideoPlay;
    /**一時停止ImageView.*/
    private ImageView mVideoPause;
    /**全画面再生.*/
    private ImageView mVideoFullScreen;
    /**ビデオコントローラバーRelativeLayout.*/
    private RelativeLayout mVideoCtrlBar;
    /**サムネイルRelativeLayout.*/
    private RelativeLayout mThumbnailRelativeLayout;
    /**プレイヤーFrameLayout.*/
    private FrameLayout mFrameLayout;
    /**NOW ON AIRアイコン.*/
    private ImageView mPortraitLayout;
    /**チャンネル情報.*/
    private RelativeLayout mLandscapeLayout;
    /**チャンネルロゴ.*/
    private ImageView mChannellogo;
    /**チャンネル名.*/
    private TextView mChanneltitle;
    /** チャンネルロゴイメージ.*/
    private ImageView mSwipeChannelLogo;
    /** チャンネル名.*/
    private TextView mSwipeChannelName;
    /** チャンネルタイトル.*/
    private TextView mSwipeChannelTitle;
    /** チャンネル日付.*/
    private TextView mSwipeChannelDate;

    /** チャンネル名String.*/
    private String mChannelNameStr;
    /** 日付String.*/
    private String mDateStr;
    /**チャンネルアイコンURL.*/
    private String mTxtChannellogo;
    /**チャンネル名.*/
    private String mTxtChanneltitle;

    /** 端末幅さ.*/
    private int mScreenWidth = 0;
    /** 端末高さ.*/
    private int mScreenHeight = 0;
    /** 端末幅さ+nav.*/
    private int mScreenNavWidth = 0;
    /** 端末高さ+nav.*/
    private int mScreenNavHeight = 0;
    /** プレイヤービューの最初幅.*/
    private int mOriginalWidth;
    /** プレイヤービューの最初高さ.*/
    private int mOriginalHeight;
    /** スワイプ区間の設定値:Y.*/
    private float mMaxMarginY;
    /** スワイプ区間の設定値:X.*/
    private float mMaxMarginX;
    /** プレイヤービュー最小比率 .*/
    private float mPlayerViewMinRatio;
    /** チャンネルリスト高さサイズ.*/
    private float mChannelListHeight;
    /** フルスクリーン状態で画面をタップした場合チャンネルリスト表示の高さサイズ.*/
    private float mChannelListSmallHeight;
    /** フルスクリーン状態で放送番組リストの高さの50%.*/
    private float mChannelListHalfHeight;
    /**操作アイコン表示か.*/
    private boolean mIsHideOperate = true;
    /** プレイヤービューのスワイプ区間値の設定.*/
    private boolean mIsFirstMeasure = true;
    /** プレイヤービューのタップ動作.*/
    private boolean mIsPlayerViewClick = false;
    /** チャンネルリストビューの上部分を少し表示した状態.*/
    private boolean mIsLittleReducedScreen = false;
    /** ビュー描画成功.*/
    private boolean mDateFlg = false;
    /** チャンネルリストスクロール制御.*/
    private boolean mIsScrollable = true;

    /**再生操作種別.*/
    private enum OttPlayerControlType {
        /**初期値.*/
        PLAYER_CONTROL_NONE,
        /**再生・一時停止.*/
        PLAYER_CONTROL_PLAY_PAUSE,
        /**拡大・縮小.*/
        PLAYER_CONTROL_FULL_SCREEN
    }

    /**コントローラービュー操作タイプ.*/
    public enum OttPlayerEventType {
        /**フルスクリーンボタンタップ.*/
        ZOOM_OUT_TAP,
        /**縮小ボタンタップ.*/
        ZOOM_IN_TAP,
        /**操作なし.*/
        NONE
    }

    /**
     * （全画面）プレイヤーリスナー.
     */
    public interface FullScreenPlayerViewListener {

        /**
         * 横、縦チェンジコールバック.
         * @param isLandscape 横
         */
        void onOttScreenOrientationChangeCallBack(final boolean isLandscape);

        /**
         * サムネイル取得コールバック.
         * @param url url
         * @param imageView imageView
         */
        void onOttScreenSetLogoImageCallBack(final String url, final ImageView imageView);
    }

    /**
     * コンストラクタ.
     * @param context コンテクスト
     */
    public OttFullScreenPlayerViewLayout(final Context context) {
        this(context, null);
    }

    /**
     * コンストラクタ.
     * @param context コンテクスト
     * @param attrs attrs
     */
    public OttFullScreenPlayerViewLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * コンストラクタ.
     * @param context コンテクスト
     * @param attrs attrs
     * @param defStyleAttr defStyleAttr
     */
    public OttFullScreenPlayerViewLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mActivity = (Activity) context;
    }

    /**
     *　コントロールビューを非表示にする.
     */
    private final Runnable mHideCtrlViewThread = new Runnable() {

        @Override
        public void run() {
            DTVTLogger.start();
            hideCtrlViewIfVisible();

            if (mIsLittleReducedScreen && mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setLandscapedPlayerViewToFullScreen(false);
            }
            DTVTLogger.end();
        }
    };

    /**
     * プレイヤー初期化.
     * @param fullScreenPlayerViewListener 再生コールバック
     */
    public void setOrientationChangeListener(final FullScreenPlayerViewListener fullScreenPlayerViewListener) {
        this.mFullScreenPlayerViewListener = fullScreenPlayerViewListener;
    }

    /**
     * Ottプレイヤービュー初期化.
     * @return SurfaceHolder SurfaceHolder
     */
    public SurfaceHolder initPlayerView() {
        if (mSurfaceView == null) {
            mSurfaceView = new SurfaceView(mContext);
            RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mSurfaceView.setLayoutParams(layoutParams);
            this.addView(mSurfaceView);
            this.setVisibility( View.VISIBLE);
            showProgress(true);
        }
        mActivity.getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        requestFocus();

        this.removeView(mRecordCtrlView);
        this.getKeepScreenOn();
        mRecordCtrlView = (RelativeLayout) View.inflate(mContext, R.layout.tv_player_ctrl_video_record, null);
        mVideoPlayPause = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_player_pause_fl);
        mVideoPlay = mRecordCtrlView.findViewById(R.id.tv_player_control_play);
        mVideoPlay.setImageResource(R.mipmap.mediacontrol_icon_white_play_arrow);
        mVideoPause = mRecordCtrlView.findViewById(R.id.tv_player_control_pause);
        mVideoPause.setImageResource(R.mipmap.mediacontrol_icon_white_stop);
        mVideoFullScreen = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_now_on_air_full_screen_iv);
        mVideoCtrlBar = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_control_bar_iv);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                mScreenWidth, mScreenHeight);
        setScreenSize(mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, layoutParams);
        this.addView(mRecordCtrlView);
        initLiveLayout();
        hideCtrlView(false);
        return mSurfaceView.getHolder();
    }

    /**
     * 再生パラメータ設定.
     * @param ottPlayerStartData OTT再生情報
     */
    public void initMediaInfo(final OttPlayerStartData ottPlayerStartData) {
        mTxtChannellogo = ottPlayerStartData.getThumbnail();
        mTxtChanneltitle = ottPlayerStartData.getTitle();
        mChannelNameStr = ottPlayerStartData.getChannelName();
        mDateStr = ottPlayerStartData.getChannelDate();
    }

    /**
     * NOW ON AIR レイアウト初期化.
     */
    private void initLiveLayout() {
        RelativeLayout mLiveLayout = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_now_on_air_rl);
        mPortraitLayout = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_now_on_air_portrait);
        mLandscapeLayout = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_now_on_air_landscape);
        mChannellogo = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_chanel_logo);
        mChanneltitle = mRecordCtrlView.findViewById(R.id.tv_player_ctrl_video_record_chanel_title);
        mFullScreenPlayerViewListener.onOttScreenSetLogoImageCallBack(mTxtChannellogo, mChannellogo);
        if (!TextUtils.isEmpty(mTxtChanneltitle)) {
            mChanneltitle.setText(mTxtChanneltitle);
        }
        mLiveLayout.setVisibility(VISIBLE);
    }

    /**
     * initReducePlayerView mView.
     */
    private void initReducePlayerView(final LinearLayout.LayoutParams playerParams) {
        DTVTLogger.start();

        mOriginalWidth = playerParams.width;
        mOriginalHeight = playerParams.height;

        // 初回設定：スワイプの最大値
        if (mIsFirstMeasure) {
            mChannelListHeight = getResources().getDimension(R.dimen.contents_detail_player_channel_list_item_view_height);
            mChannelListSmallHeight = getResources().getDimension(R.dimen.contents_detail_player_channel_list_item_view_small_height);
            mChannelListHalfHeight = getResources().getDimension(R.dimen.contents_detail_player_channel_list_item_view_half_height);
            mPlayerViewMinRatio = RATIO_100_PERSENT - mChannelListHeight / mOriginalHeight;
            mMaxMarginY = mChannelListHeight;
            mMaxMarginX = mPlayerViewMinRatio * mOriginalWidth;
            mIsFirstMeasure = false;
        }

        if (mContentDetailView == null) {
            mContentDetailView = (RelativeLayout) View.inflate(mActivity, R.layout.tv_player_video_content_detail, null);
            LayoutParams layoutParams = new LayoutParams((int) mMaxMarginX, mOriginalHeight);
            mContentDetailView.setLayoutParams(layoutParams);
            this.addView(mContentDetailView);

            LinearLayout mSwipeContentDetailLayout = mContentDetailView.findViewById(R.id.content_detail_layout);
            mSwipeChannelLogo = mContentDetailView.findViewById(R.id.tv_player_ctrl_video_record_swipe_chanel_logo);
            mSwipeChannelName = mContentDetailView.findViewById(R.id.tv_player_ctrl_video_record_swipe_chanel_name);
            mSwipeChannelTitle = mContentDetailView.findViewById(R.id.tv_player_ctrl_video_record_swipe_chanel_title);
            mSwipeChannelDate = mContentDetailView.findViewById(R.id.tv_player_ctrl_video_record_swipe_chanel_date);
            // 縦画面切り替えボタン
            LinearLayout mSwitchButtonLayout = mContentDetailView.findViewById(R.id.tv_player_ctrl_now_on_air_full_screen_iv_swipe_layout);

            mSwitchButtonLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLandscapedPlayerViewToFullScreen(true);
                    mPlayerEventType = OttPlayerEventType.ZOOM_IN_TAP;
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    initPlayerView();
                    setPlayerEvent();
                    showControlViewAfterPlayerEvent(mPlayerEventType);
                    // プレイヤーからコールバックがないため、表示後は"PlayerEventType"を"NONE"にする
                    mPlayerEventType = OttPlayerEventType.NONE;
                }
            });
            mSwipeContentDetailLayout.setOnTouchListener(new RecordCtrlViewOnTouchListener());
            mContentDetailView.setVisibility(INVISIBLE);
        }

        if (mChannelNameStr != null) {
            mSwipeChannelName.setText(mChannelNameStr);
        }

        if (mTxtChanneltitle != null) {
            mSwipeChannelTitle.setText(mTxtChanneltitle);
        }

        if (mDateStr != null) {
            mSwipeChannelDate.setText(mDateStr);
        }

        if (mChannelListView == null) {
            mChannelListView = (RelativeLayout) View.inflate(mActivity, R.layout.tv_player_video_channel_list, null);
            LayoutParams channelListLayoutParams = new LayoutParams(mOriginalWidth, (int) mMaxMarginY);
            mChannelListView.setLayoutParams(channelListLayoutParams);
            mChannelListRecyclerView = mChannelListView.findViewById(R.id.channel_list_recyclerview);
            mChannelListScrollView = mChannelListView.findViewById(R.id.channel_list_scrollview);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false) {
                @Override
                public boolean canScrollHorizontally() {
                    return mIsScrollable && super.canScrollHorizontally();
                }
            };
            mChannelListRecyclerView.setLayoutManager(layoutManager);
            this.addView(mChannelListView);

            mPlayerChannelListViewAdapter = new PlayerChannelListRecyclerViewAdapter(mActivity);
            mChannelListRecyclerView.setAdapter(mPlayerChannelListViewAdapter);
            mChannelListRecyclerView.setLayoutManager(layoutManager);
            mChannelListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            sCtrlHandler.postDelayed(mHideCtrlViewThread, HIDE_IN_3_SECOND);
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            sCtrlHandler.removeCallbacks(mHideCtrlViewThread);
                            break;
                        default:
                            break;
                    }
                }
            });
            mChannelListScrollView.setOnTouchListener(new ChannelListOnTouchListener());
        }

        if (mPlayerViewWrapper == null) {
            mPlayerViewWrapper = new ReducePlayerViewWrapper(mSurfaceView, mChannelListView,
                    mContentDetailView, mProgressBarLayout, mRecordCtrlView);
            mPlayerViewWrapper.setRuleToTopAndLeft();
            mPlayerViewWrapper.setContentDetailLeftMargin(mOriginalWidth);
            mPlayerViewWrapper.setChannelListTopMargin(mOriginalHeight);
        } else {
            mPlayerViewWrapper.setRecordCtrlView(mRecordCtrlView);
        }
        mFullScreenPlayerViewListener.onOttScreenSetLogoImageCallBack(mTxtChannellogo, mSwipeChannelLogo);
        DTVTLogger.end();
    }

    // TODO　削除する予定：仮実装 ---START
    /**
     * BG → FG プレイヤービューをリスタートする
     */
    public void restartPlayerViewFromBg() {
        if (mSurfaceView == null) {
            mSurfaceView = new SurfaceView(mContext);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mSurfaceView.setLayoutParams(layoutParams);
            this.addView(mSurfaceView);
            this.setVisibility(View.VISIBLE);
        }
        showProgress(true);
    }
    // TODO　削除する予定：仮実装 ---END

    /**
     * 再生中のくるくる処理.
     * @param isShow true 表示　false 非表示
     */
    public void showProgress(final boolean isShow) {
        if (mProgressBarLayout == null) {
            mProgressBarLayout = ContentDetailUtils.getProgressView(mContext);
            mProgressBarLayout.setVisibility(GONE);
        }
        if (isShow) {
            if (mProgressBarLayout.getVisibility() == VISIBLE) {
                return;
            }
            mProgressBarLayout.getChildAt(1).setVisibility(GONE);
            this.removeView(mProgressBarLayout);
            this.addView(mProgressBarLayout);
            mProgressBarLayout.setVisibility(View.VISIBLE);
        } else {
            this.removeView(mProgressBarLayout);
            mProgressBarLayout.setVisibility(View.GONE);
            if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                showControlViewAfterPlayerEvent(mPlayerEventType);
            }
        }
    }

    /**
     * 横画面視聴中にバックキーボタンをタップした際に、縦画面に戻る処理.
     */
    public void tapBackKey() {
        // 縦画面に戻る際、PlayerViewを元の状態に戻る
        setLandscapedPlayerViewToFullScreen(true);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initPlayerView();
        setPlayerEvent();
        showControlViewAfterPlayerEvent(mPlayerEventType);
    }

    /**
     * スクリーンサイズ.
     * @param width スクリーン幅さ
     * @param height スクリーン高さ
     */
    public void setScreenSize(final int width, final int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    /**
     * スクリーンサイズ + NavigationBar.
     * @param width スクリーン幅さ + NavigationBar
     * @param height スクリーン高さ + NavigationBar
     */
    public void setScreenNavigationBarSize(final int width, final int height) {
        mScreenNavWidth = width;
        mScreenNavHeight = height;
    }

    /**
     * スクリーン幅さ.
     * @return スクリーン幅
     */
    private int getWidthDensity() {
        return mScreenWidth;
    }

    /**
     * スクリーン高さ.
     * @return スクリーン高
     */
    private int getHeightDensity() {
        return mScreenHeight;
    }

    /**
     * スクリーン幅さ+ NavigationBar.
     * @return スクリーン高+ナビゲーション
     */
    private int getScreenNavHeight() {
        return mScreenNavHeight;
    }

    /**
     * スクリーン高さ+ NavigationBar.
     * @return スクリーン幅+ナビゲーション
     */
    private int getScreenNavWidth() {
        return mScreenNavWidth;
    }

    /**
     * set screen size.
     *
     * @param mIsLandscape 端末の縦横判定
     * @param playerParams LayoutParams
     */
    private void setScreenSize(final boolean mIsLandscape, final LinearLayout.LayoutParams playerParams) {
        DTVTLogger.start();
        if (mIsLandscape) {
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            playerParams.height = getScreenNavHeight();
            playerParams.width = getScreenNavWidth();
            if (playerParams.height > playerParams.width) {
                playerParams.height = getScreenNavWidth();

                playerParams.width = getScreenNavWidth() / ContentDetailUtils.SCREEN_RATIO_HEIGHT_9 * ContentDetailUtils.SCREEN_RATIO_WIDTH_16;
                if (playerParams.width < getScreenNavHeight()) {
                    playerParams.gravity = Gravity.CENTER_HORIZONTAL;
                } else {
                    playerParams.width = getScreenNavHeight();
                    playerParams.height = getScreenNavHeight() / ContentDetailUtils.SCREEN_RATIO_WIDTH_16 * ContentDetailUtils.SCREEN_RATIO_HEIGHT_9;
                    if (playerParams.height < getScreenNavWidth()) {
                        playerParams.setMargins(0, (getScreenNavWidth() - playerParams.height) / 2, 0, 0);
                    }
                }
            }
            mScreenWidth = playerParams.width;
            initReducePlayerView(playerParams);
            mFullScreenPlayerViewListener.onOttScreenOrientationChangeCallBack(true);
            mVideoFullScreen.setBackgroundResource(R.mipmap.icon_normal_zoom_out);
        } else {
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            mScreenWidth = getWidthDensity();
            if (getHeightDensity() < getWidthDensity()) {
                playerParams.width = getHeightDensity();
            }

            if (playerParams.width < getScreenNavWidth()) {
                playerParams.width = getScreenNavWidth();
            }
            playerParams.height = (getScreenNavWidth() * SCREEN_RATIO_HEIGHT_9 / SCREEN_RATIO_WIDTH_16);
            mFullScreenPlayerViewListener.onOttScreenOrientationChangeCallBack(false);
            mVideoFullScreen.setBackgroundResource(R.mipmap.icon_normal_zoom_in);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(playerParams.width, playerParams.height);
        mFrameLayout.setLayoutParams(params);
        mThumbnailRelativeLayout.setLayoutParams(playerParams);

        DTVTLogger.end();
    }

    /**
     * set player event.
     */
    public void setPlayerEvent() {
        DTVTLogger.start();
        if (null == mRecordCtrlView) {
            DTVTLogger.end();
            return;
        }
        mRecordCtrlView.setOnTouchListener(new RecordCtrlViewOnTouchListener());
        DTVTLogger.end();
    }

    /**
     * NOW ON AIR レイアウト表示.
     *
     * @param isLandscape 端末の縦横判定
     */
    private void showLiveLayout(final boolean isLandscape) {
        if (isLandscape) {
            mPortraitLayout.setVisibility(INVISIBLE);
            mLandscapeLayout.setVisibility(VISIBLE);
        } else {
            mLandscapeLayout.setVisibility(INVISIBLE);
            mPortraitLayout.setVisibility(VISIBLE);
        }
    }

    /**
     * ペアレントビューを設定.
     * @param mThumbnailRelativeLayout ペアレントビュー
     */
    public void setParentLayout(final RelativeLayout mThumbnailRelativeLayout) {
        this.mThumbnailRelativeLayout = mThumbnailRelativeLayout;
    }

    /**
     * FrameLayoutビューを設定.
     * @param frameLayout FrameLayoutビュー
     */
    public void setFrameLayout(final FrameLayout frameLayout) {
        this.mFrameLayout = frameLayout;
    }

    /**
     * チャンネルリストビューを設定（全画面）.
     *
     * @param contentsDatas contentsData
     * @param serviceIdUniq サービスIDユニーク
     */
    public void setChannelListRecyclerViewData(final List<ContentsData> contentsDatas, final String serviceIdUniq) {
        if (mChannelListRecyclerView == null) {
            return;
        }
        mPlayerChannelListViewAdapter.setRecyclerViewData(contentsDatas, serviceIdUniq);
    }

    /**
     * 再生操作種別取得.
     *
     * @param motionEvent モーションイベント
     * @return 再生操作種別
     */
    private OttPlayerControlType getPlayerControlType(final MotionEvent motionEvent) {
        OttPlayerControlType controlType = OttPlayerControlType.PLAYER_CONTROL_NONE;
        // どのアイコンがタップ、長押しされているか判定.
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();
        if (touchX >= mVideoPlayPause.getX() && touchX <=  mVideoPlayPause.getX() + mVideoPlayPause.getWidth()
                && touchY >= mVideoPlayPause.getY() && touchY <= mVideoPlayPause.getY() + mVideoPlayPause.getHeight()) {
            controlType = OttPlayerControlType.PLAYER_CONTROL_PLAY_PAUSE;
        } else if (touchX >= mVideoFullScreen.getX() && touchX <= mVideoFullScreen.getX() + mVideoFullScreen.getWidth()
                && touchY >= mVideoFullScreen.getY() && touchY <= mVideoFullScreen.getY() + mVideoFullScreen.getHeight()) {
            controlType = OttPlayerControlType.PLAYER_CONTROL_FULL_SCREEN;
        }
        return controlType;
    }

    /**
     * プレイヤー横画面時にサイズとマージンの更新.
     *
     * @param recordDistanceY スワイプ距離
     */
    private void updatePlayerView(final int recordDistanceY) {
        // スワイプ距離がスワイプ最大設定値を超える場合に、下の処理を実行しない
        mIsScrollable = false;
        int newMarginY = mPlayerViewWrapper.getMarginBottom() - recordDistanceY;
        if (newMarginY < 0) {
            return;
        }
        mPlayerViewWrapper.setRuleForProgressBar();

        final float marginPercent = (mMaxMarginY - newMarginY) / mMaxMarginY;

        if (marginPercent <= 0) {
            return;
        }

        final float videoPercent = mPlayerViewMinRatio + (RATIO_100_PERSENT - mPlayerViewMinRatio) * marginPercent;

        if (mPlayerViewMinRatio == videoPercent) {
            // 番組情報エリアの全表示状態で番組情報を表示する
            mContentDetailView.setVisibility(VISIBLE);
        } else {
            // 番組情報エリアの全表示状態でない場合に、番組情報を表示しない
            mContentDetailView.setVisibility(INVISIBLE);
        }

        mPlayerViewWrapper.setWidth(mOriginalWidth * videoPercent);
        mPlayerViewWrapper.setHeight(mOriginalHeight * videoPercent);
        mPlayerViewWrapper.setRecordWidth(mOriginalWidth * videoPercent);
        mPlayerViewWrapper.setRecordHeight(mOriginalHeight * videoPercent);

        mPlayerViewWrapper.setMarginBottom(newMarginY);
        mPlayerViewWrapper.setRecordMarginBottom(newMarginY);
        mPlayerViewWrapper.setContentDetailLeftMargin(mOriginalWidth * videoPercent);
        mPlayerViewWrapper.setContentDetailBottomMargin(mOriginalHeight * (RATIO_100_PERSENT - videoPercent));
        mPlayerViewWrapper.setChannelListTopMargin(mOriginalHeight * videoPercent);
        mPlayerViewWrapper.setProgressBarTopMargin(mOriginalHeight * videoPercent);
        mPlayerViewWrapper.setProgressBarLeftMargin(mOriginalWidth * videoPercent);
    }

    /**
     * プレイヤー横画面時に全画面へ変更する際のアニメーション.
     *
     * @param isGoPortraitScreen 縦画面に戻る判定
     */
    private void setLandscapedPlayerViewToFullScreen(final boolean isGoPortraitScreen) {
        if (mPlayerViewWrapper == null) {
            return;
        }
        mIsLittleReducedScreen = false;
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "width", mPlayerViewWrapper.getWidth(), mOriginalWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "height", mPlayerViewWrapper.getHeight(), mOriginalHeight),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "recordWidth", mPlayerViewWrapper.getRecordWidth(), mOriginalWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "recordHeight", mPlayerViewWrapper.getRecordHeight(), mOriginalHeight),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "marginBottom", mPlayerViewWrapper.getMarginBottom(), 0),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "recordMarginBottom", mPlayerViewWrapper.getRecordMarginBottom(), 0),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "channelListTopMargin", mPlayerViewWrapper.getChannelListTopMargin(), mOriginalHeight),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "contentDetailLeftMargin", mPlayerViewWrapper.getContentDetailLeftMargin(), mOriginalWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "contentDetailBottomMargin", mPlayerViewWrapper.getContentDetailBottomMargin(), 0)
        );
        set.setDuration(SWIPE_ANIMATION_TIME).start();
        if (mContentDetailView.getVisibility() == VISIBLE) {
            mContentDetailView.setVisibility(INVISIBLE);
        }
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                if (isGoPortraitScreen) {
                    mPlayerViewWrapper.setProgressBarForGoPortraitScreen();
                    mPlayerViewWrapper = null;
                } else {
                    mPlayerViewWrapper.setRuleForProgressBarInFullScreen();
                }
            }
        });
        hideCtrlViewIfVisible();
        mIsScrollable = true;
    }

    /**
     * ワンタップ状態を表示するアニメーション.
     */
    private void goLittleReducedScreen() {
        AnimatorSet set = new AnimatorSet();
        final float smallHeightSize = mChannelListSmallHeight;
        final float playerViewWidth = mOriginalWidth * (RATIO_100_PERSENT - smallHeightSize / mOriginalHeight);
        final float playerViewHeight = mOriginalHeight * (RATIO_100_PERSENT - smallHeightSize / mOriginalHeight);
        mPlayerViewWrapper.setRuleForProgressBar();
        mPlayerViewWrapper.setProgressBarTopMargin(playerViewHeight);
        mPlayerViewWrapper.setProgressBarLeftMargin(playerViewWidth);
        mPlayerViewWrapper.setRecordWidth(mOriginalWidth);
        final float width = mIsLittleReducedScreen ? mPlayerViewWrapper.getWidth() : mOriginalWidth;
        final float height = mIsLittleReducedScreen ? mPlayerViewWrapper.getHeight() : mOriginalHeight;
        set.playTogether(
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "width", width, playerViewWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "height", height, mOriginalHeight - smallHeightSize),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "recordHeight", height, mOriginalHeight - smallHeightSize),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "marginBottom", mPlayerViewWrapper.getMarginBottom(), (int) smallHeightSize),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "recordMarginBottom", mPlayerViewWrapper.getRecordMarginBottom(), (int) smallHeightSize),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "channelListTopMargin", mPlayerViewWrapper.getChannelListTopMargin(), (int) (mOriginalHeight - smallHeightSize)),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "contentDetailLeftMargin", mPlayerViewWrapper.getContentDetailLeftMargin(), playerViewWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "contentDetailBottomMargin", mPlayerViewWrapper.getContentDetailBottomMargin(), (int) smallHeightSize)
        );
        set.setDuration(SWIPE_ANIMATION_TIME).start();

        hideCtrlViewAfterOperate();
        mIsLittleReducedScreen = true;
        mIsScrollable = true;
    }

    /**
     * プレイヤー縮小状態を表示するアニメーション.
     */
    private void goMinScreen() {
        mIsLittleReducedScreen = false;
        AnimatorSet set = new AnimatorSet();
        final float playerViewHeight = mOriginalHeight - mMaxMarginY;
        final float playerViewWidth = mOriginalWidth * (playerViewHeight / mOriginalHeight);
        mPlayerViewWrapper.setRuleForProgressBar();
        mPlayerViewWrapper.setProgressBarTopMargin(playerViewHeight);
        mPlayerViewWrapper.setProgressBarLeftMargin(playerViewWidth);
        set.playTogether(
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "width", mPlayerViewWrapper.getWidth(), playerViewWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "height", mPlayerViewWrapper.getHeight(), playerViewHeight),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "recordWidth", mPlayerViewWrapper.getRecordWidth(), playerViewWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "recordHeight", mPlayerViewWrapper.getRecordHeight(), playerViewHeight),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "marginBottom", mPlayerViewWrapper.getMarginBottom(), (int) Math.ceil(mMaxMarginY)),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "recordMarginBottom", mPlayerViewWrapper.getRecordMarginBottom(), (int) Math.ceil(mMaxMarginY)),
                ObjectAnimator.ofInt(mPlayerViewWrapper, "channelListTopMargin", mPlayerViewWrapper.getChannelListTopMargin(), (int) (mOriginalHeight - mMaxMarginY)),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "contentDetailLeftMargin", mPlayerViewWrapper.getContentDetailLeftMargin(), playerViewWidth),
                ObjectAnimator.ofFloat(mPlayerViewWrapper, "contentDetailBottomMargin", mPlayerViewWrapper.getContentDetailBottomMargin(), (int) mMaxMarginY)
        );
        set.setDuration(SWIPE_ANIMATION_TIME).start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mContentDetailView.setVisibility(VISIBLE);
                mIsScrollable = true;
                if (mDateStr != null) {
                    if (mSwipeChannelDate.getText().toString().contains( DateUtils.DATE_HYPHEN)) {
                        mSwipeChannelDate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (mDateFlg) {
                                    mSwipeChannelDate.getViewTreeObserver().removeOnGlobalLayoutListener(this);//リスナー解除。
                                }
                                if (mSwipeChannelDate.getLineCount() > 0) {
                                    if (mSwipeChannelDate.getLineCount() > 1) { //TextViewの内容が2行以上の場合
                                        String dateStr = mDateStr.replace(DateUtils.DATE_HYPHEN, DateUtils.DATE_HYPHEN + DateUtils.DATE_NEWLINE); //改行する。
                                        mSwipeChannelDate.setText(dateStr);
                                    }
                                    mDateFlg = true;
                                }
                            }
                        });
                    }
                }
            }
        });
        hideCtrlViewIfVisible();
    }

    /**
     * ワンタップ時の表示に戻る OR 全画面表示にする.
     *
     * @param channelListHeight チャンネルリストの高さ
     */
    private void goOneTouchOrFullScreen(final int channelListHeight) {
        // 放送番組リストの高さの50%未満引き出していた場合(指を離した時)
        if (mIsLittleReducedScreen && channelListHeight >= mChannelListSmallHeight) {
            // ワンタップ表示状態からのスワイプする時、ワンタップ表示(高さ)から放送番組リスト(高さ)の50%までのスワイプの場合
            goLittleReducedScreen();

            // ワンタップ表示状態に戻る
            showControlView();
            mIsHideOperate = false;
            showLiveLayout(true);
            mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_landscape);
        } else {
            // 放送番組リストの高さの50%未満引き出していた場合は全画面表示にする場合(全画面)
            // ワンタップ表示状態からのスワイプする時、ワンタップ表示(高さ)未満のスワイプの場合
            setLandscapedPlayerViewToFullScreen(false);
        }
    }

    /**
     * hide video ctrl mView.
     * @param isFromActivity Activity
     */
    public void hideCtrlView(final boolean isFromActivity) {
        DTVTLogger.start();
        if (!isFromActivity) {
            mVideoPlayPause.setVisibility(View.INVISIBLE);
            mVideoCtrlBar.setVisibility(View.INVISIBLE);
            mPortraitLayout.setVisibility(INVISIBLE);
            mLandscapeLayout.setVisibility(INVISIBLE);
        }
        DTVTLogger.end();
    }


    /**
     * hide ctrl mView.
     */
    private void hideCtrlViewAfterOperate() {
        DTVTLogger.start();
        if (sCtrlHandler != null) {
            sCtrlHandler.removeCallbacks(mHideCtrlViewThread);
            sCtrlHandler.postDelayed(mHideCtrlViewThread, HIDE_IN_3_SECOND);
        }
        DTVTLogger.end();
    }

    /**
     * hide video ctrl mView.
     */
    private void hideCtrlViewIfVisible() {
        DTVTLogger.start();
        if (mVideoPlayPause.getVisibility() != View.GONE) {
            mVideoPlayPause.setVisibility(View.INVISIBLE);
        }
        if (mPortraitLayout.getVisibility() != View.GONE) {
            mPortraitLayout.setVisibility(INVISIBLE);
        }
        if (mLandscapeLayout.getVisibility() != View.GONE) {
            mLandscapeLayout.setVisibility(INVISIBLE);
        }
        if (mVideoCtrlBar.getVisibility() != View.GONE) {
            mVideoCtrlBar.setVisibility(View.INVISIBLE);
        }
        if (mSurfaceView.getVisibility() != View.GONE) {
            mSurfaceView.setBackgroundResource(0);
        }
        DTVTLogger.end();
    }

    /**
     * show ctrl view.
     */
    private void showControlView() {
        DTVTLogger.start();
        if (mVideoCtrlBar != null) {
            mVideoCtrlBar.setVisibility(View.VISIBLE);
        }
        DTVTLogger.end();
    }

    /**
     * サムネイル取得処理を止める.
     */
    public void stopThumbnailConnect() {
        if (mPlayerChannelListViewAdapter != null) {
            mPlayerChannelListViewAdapter.stopConnect();
        }
    }

    /**
     * 止めたサムネイル取得処理を再度取得可能な状態にする.
     */
    public void enableThumbnailConnect() {
        if (mPlayerChannelListViewAdapter != null) {
            mPlayerChannelListViewAdapter.enableConnect();
        }
    }

    /**
     * フルスクリーンボタンタップ後の表示処理.
     * @param playerEventType プレイヤーイベント
     */
    private void showControlViewAfterPlayerEvent(final OttPlayerEventType playerEventType) {
        DTVTLogger.start();
        showControlView();
        if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            showLiveLayout(true);
        } else {
            showLiveLayout(false);
        }
        switch (playerEventType) {
            case ZOOM_OUT_TAP:
                mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_landscape);
                break;
            case ZOOM_IN_TAP:
                mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_portrait);
                break;
            default:
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_portrait);
                } else {
                    mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_landscape);
                }
                break;
        }
        hideCtrlViewAfterOperate();
        DTVTLogger.end();
    }

    /**
     * コントローラビュータッチアクション.
     */
    private class RecordCtrlViewOnTouchListener implements OnTouchListener {
        private int mRecordLstY;
        private int mRecordDistanceY;

        @Override
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            DTVTLogger.start();
            int y = (int) motionEvent.getRawY();
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsPlayerViewClick = true;
                    mPlayerEventType = OttPlayerEventType.NONE;
                    if (mVideoCtrlBar.getVisibility() == View.VISIBLE) {
                        mIsHideOperate = false;
                        OttPlayerControlType controlType = getPlayerControlType(motionEvent);
                        if (controlType == OttPlayerControlType.PLAYER_CONTROL_FULL_SCREEN) {
                            //表示画像変更処理
                            if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                mPlayerEventType = OttPlayerEventType.ZOOM_IN_TAP;
                            } else {
                                mPlayerEventType = OttPlayerEventType.ZOOM_OUT_TAP;
                            }
                        }
                        if (mPlayerEventType != OttPlayerEventType.NONE) {
                            // 操作ボタン長押し時は非表示にしない
                            sCtrlHandler.removeCallbacks(mHideCtrlViewThread);
                        }
                    } else {
                        mIsHideOperate = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mIsHideOperate) {
                        // ボタン非表示にする
                        OttPlayerControlType controlType = getPlayerControlType(motionEvent);

                        if (controlType == OttPlayerControlType.PLAYER_CONTROL_FULL_SCREEN) {
                            if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                mPlayerEventType = OttPlayerEventType.ZOOM_IN_TAP;
                                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                setLandscapedPlayerViewToFullScreen(true);
                            } else {
                                mPlayerEventType = OttPlayerEventType.ZOOM_OUT_TAP;
                                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            }
                            initPlayerView();
                            setPlayerEvent();
                            showControlViewAfterPlayerEvent(mPlayerEventType);
                            // プレイヤーからコールバックがないため、表示後は"PlayerEventType"を"NONE"にする
                            mPlayerEventType = OttPlayerEventType.NONE;
                            return true;
                        }
                        // 操作ボタンタップ後であれば、onPlayerEvent()内の処理で3秒後に非表示。画面タップ時は、すぐ非表示
                        if (mPlayerEventType != OttPlayerEventType.NONE) {
                            hideCtrlViewAfterOperate();
                        } else {
                            hideCtrlViewIfVisible();
                        }
                        mIsHideOperate = true;
                    } else {
                        // ボタン表示する
                        showControlView();
                        mIsHideOperate = false;

                        if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            showLiveLayout(true);
                        } else {
                            showLiveLayout(false);
                        }
                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_portrait);
                        } else {
                            mSurfaceView.setBackgroundResource(R.mipmap.movie_material_mask_overlay_gradation_landscape);
                        }
                        hideCtrlViewAfterOperate();
                    }

                    if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            && mPlayerViewWrapper != null) {
                        if (mIsPlayerViewClick) {
                            // スクリーン画面をタップする際
                            if (mSurfaceView.getWidth() == mOriginalWidth) {
                                // フルスクリーン状態で画面をタップした場合
                                if (!mIsHideOperate) {
                                    goLittleReducedScreen();
                                }
                            } else {
                                // プレイヤー縮小状態でプレイヤーをタップした場合
                                setLandscapedPlayerViewToFullScreen(false);
                            }
                        } else {
                            final int channelListHeight = mIsLittleReducedScreen ? mPlayerViewWrapper.getMarginBottom() :
                                    mOriginalHeight - mPlayerViewWrapper.getHeight();
                            if (channelListHeight < mOriginalHeight) {
                                if (channelListHeight < mChannelListHalfHeight) {
                                    goOneTouchOrFullScreen(channelListHeight);
                                } else {
                                    // 放送番組リストの高さの50%以上引き出していた場合は縮小表示にする(指を離した時)
                                    goMinScreen();
                                }
                            } else {
                                // コンテンツ詳細初回表示時
                                if (!mIsHideOperate) {
                                    goLittleReducedScreen();
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        mRecordDistanceY = y - mRecordLstY;
                        if (Math.abs(mRecordDistanceY) > 1) {
                            mIsPlayerViewClick = false;
                            sCtrlHandler.removeCallbacks(mHideCtrlViewThread);
                            if (mPlayerViewWrapper != null) {
                                updatePlayerView(mRecordDistanceY);
                                hideCtrlViewIfVisible();
                            }
                        }
                    }
                    break;
            }

            mRecordLstY = y;
            DTVTLogger.end();
            return true;
        }
    }

    /**
     * チャンネルリストタッチアクション.
     */
    private class ChannelListOnTouchListener implements OnTouchListener {
        private int mRecordLastY = 0;
        private int mRecordDistanceY;

        @Override
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            int y = (int) motionEvent.getRawY();
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsPlayerViewClick = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    mRecordDistanceY = y - mRecordLastY;
                    if (Math.abs(mRecordDistanceY) > 1 &&  mRecordDistanceY <= mChannelListHeight) {
                        mIsPlayerViewClick = false;
                        sCtrlHandler.removeCallbacks(mHideCtrlViewThread);
                        if (mPlayerViewWrapper != null) {
                            updatePlayerView(mRecordDistanceY);
                            hideCtrlViewIfVisible();
                        }
                    }
                    mRecordLastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (mPlayerViewWrapper != null) {
                        if (!mIsPlayerViewClick) {
                            final int channelListHeight = mIsLittleReducedScreen ? mPlayerViewWrapper.getMarginBottom() :
                                    mOriginalHeight - mPlayerViewWrapper.getHeight();
                            if (channelListHeight < mChannelListHalfHeight) {
                                goOneTouchOrFullScreen(channelListHeight);
                            } else {
                                // 放送番組リストの高さの50%以上引き出していた場合は縮小表示にする(指を離した時)
                                goMinScreen();
                            }
                        }
                    }
                    mRecordLastY = 0;
                    break;
            }
            return true;
        }
    }
}
