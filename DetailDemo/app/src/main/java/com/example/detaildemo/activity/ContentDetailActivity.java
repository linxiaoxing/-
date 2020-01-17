package com.example.detaildemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UserState;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.bean.ScheduleInfo;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfoList;
import com.example.detaildemo.data.manager.ClipKeyListDataManager;
import com.example.detaildemo.data.manager.TimerNoticeManager;
import com.example.detaildemo.data.provider.ContentsDetailDataProvider;
import com.example.detaildemo.data.provider.OttPlayerDataProvider;
import com.example.detaildemo.data.provider.OttProgramListDataProvider;
import com.example.detaildemo.data.provider.ScaledDownProgramListDataProvider;
import com.example.detaildemo.data.provider.UserInfoDataProvider;
import com.example.detaildemo.data.provider.data.OtherContentsDetailData;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RoleListMetaData;
import com.example.detaildemo.data.webapiclient.jsonparser.data.TimerNoticeInfo;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerStartResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedChannelListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedVodListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.RemoteRecordingReservationResultResponse;
import com.example.detaildemo.fragment.DtvContentsBroadcastFragment;
import com.example.detaildemo.fragment.DtvContentsChannelFragment;
import com.example.detaildemo.fragment.DtvContentsDetailFragment;
import com.example.detaildemo.fragment.DtvContentsDetailFragmentFactory;
import com.example.detaildemo.fragment.DtvContentsEpisodeFragment;
import com.example.detaildemo.fragment.DtvContentsOttPlayerFragment;
import com.example.detaildemo.utils.ClipUtils;
import com.example.detaildemo.utils.ContentDetailUtils;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.NetWorkUtils;
import com.example.detaildemo.utils.OttContentUtils;
import com.example.detaildemo.utils.SharedPreferencesUtils;
import com.example.detaildemo.utils.StringUtils;
import com.example.detaildemo.utils.UserInfoUtils;
import com.example.detaildemo.view.CustomDialog;
import com.example.detaildemo.view.TabItemLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDetailActivity extends BaseActivity implements View.OnClickListener,
        DtvContentsDetailFragment.ContentsDetailFragmentListener,
        TabItemLayout.OnClickTabTextListener,
        DtvContentsChannelFragment.ChangedScrollLoadListener,
        DtvContentsEpisodeFragment.ChangedScrollLoadListener,
        DtvContentsBroadcastFragment.ContentsBroadcastFragmentListener,
        ContentsDetailDataProvider.ApiDataProviderCallback,
        ScaledDownProgramListDataProvider.ApiDataProviderCallback,
        OttProgramListDataProvider.OttChannelProgramCallback,
        DtvContentsOttPlayerFragment.DtvContentsOttPlayerFragmentListener,
        OttPlayerDataProvider.OttPlayerProviderCallback {

    // region variable
    /** コンテンツ詳細 start HorizontalScrollView.*/
    private TabItemLayout mTabLayout = null;
    /** タブー名.*/
    private String[] mTabNames = null;
    /**サムネイルレイアウト.*/
    private LinearLayout mThumbnailBtn = null;
    /**サムネイルイメージビュー.*/
    private ImageView mThumbnail = null;
    /**サムネイルRelativeLayout.*/
    private RelativeLayout mThumbnailRelativeLayout = null;
    /**サムネイルアイコン、メッセージレイアウト.*/
    private LinearLayout mContractLeadingView = null;
    /** ViewPager.*/
    private ViewPager mViewPager = null;
    /* player start */
    /**FrameLayout.*/
    private FrameLayout mFrameLayout = null;
    /** コンテンツ詳細データ.*/
    private OtherContentsDetailData mDetailData = null;
    /** コンテンツ詳細フラグメント.*/
    private DtvContentsDetailFragment mContentsDetailFragment = null;
    /** コンテンツ詳細フラグメントファクトリー.*/
    private DtvContentsDetailFragmentFactory mFragmentFactory = null;
    /** コンテンツOTTフラグメント.*/
    private DtvContentsBroadcastFragment mDtvContentsBroadcastFragment = null;
    /** ユーザ情報DataProvider. */
    private UserInfoDataProvider mUserInfoDataProvider = null;
    /** フルメタデータ.*/
    private VodMetaFullData mDetailFullData = null;
    /** 番組切替時に情報を受け取るBroadcastReceiver. */
    private BroadcastReceiver mOttContentChangeReceiver = null;
    /** OTT１日チャンネルリスト取得データプロバイダー. */
    private OttProgramListDataProvider mOttProgramListDataProvider = null;
    /** コンテンツ詳細データプロバイダー.*/
    private ContentsDetailDataProvider mContentsDetailDataProvider = null;
    /** 縮小番組表データプロバイダー .*/
    private ScaledDownProgramListDataProvider mScaledDownProgramListDataProvider = null;
    /** コンテンツ種別1のコンテンツ種別名のひかりTVタイプ.*/
    private ContentUtils.HikariType mHikariType = null;
    /** コンテンツ種別.*/
    private ContentUtils.ContentsType mContentsType = null;
    /** サービスIDユニーク.*/
    private String mServiceIdUniq = null;
//    /** リモコンレイアウト. */
//    private RemoteControllerView mRemoteControllerView = null;
    /** コンテンツタイプ(Google Analytics用).*/
    private ContentDetailUtils.ContentTypeForGoogleAnalytics contentType = null;
    /** チャンネルリストフラグメント.*/
    private DtvContentsChannelFragment mChannelFragment = null;
    /** OTTプレイヤー再生用フラグメント. */
    private DtvContentsOttPlayerFragment mDtvContentsOttPlayerFragment;
    /** OTTプレイヤープロバイダー. */
    private OttPlayerDataProvider mOttPlayerDataProvider;
    /** 視聴可否ステータス.*/
    private ContentUtils.ViewIngType mViewIngType = null;
    /** 全チャンネルリスト.*/
    private ArrayList<ChannelInfo> mAllChannelList;
    /** チャンネルリスト.*/
    private List<ContentsData> mChannelList;
    /** チャンネル日付.*/
    private String mChannelDate = null;
    /** 日付リスト.*/
    private String[] mDateList = null;
    /** 対象コンテンツのチャンネルデータ.*/
    private ChannelInfo mChannel = null;
    /**再生アイコン.*/
    private ImageView mPlayIcon;
    /** OTTリクエスト成功フラグ. */
    private boolean mIsOttRequestSuccess = false;
    /** pure dchチャンネル名取得.*/
    private boolean mIsOtherServiceDtvChLoading = false;
    /** OTT送信失敗時、再送信が必要ですけど、同時にタブして２つの送信を行わないフラグ. */
    private boolean mIsOttRequestRunning = false;
    /**リモコンコントローラービジブルか.*/
    private boolean mIsControllerVisible = false;
    /** 放送視聴可否.*/
    private boolean mIsH4dPlayer = false;
    /** プレイヤー前回のポジション.*/
    private int mPlayStartPosition;
    /** 日付インディーズ.*/
    private int mDateIndex = 0;
    /** 前回ViewPagerのタブ位置.*/
    private int mViewPagerIndex = DEFAULT_TAB_INDEX;
    /** 前回ViewPagerのタブ位置.*/
    private static final int DEFAULT_TAB_INDEX = -1;
    /** 視聴可能期限.*/
    private long mEndDate = 0L;
    /** Vod視聴可能期限.*/
    private long mVodEndDate = 0L;
    /** Vod視聴可能期限文字列.*/
    private String mVodEndDateText = null;
    /** 再生停止フラグ.*/
    private boolean mIsPlayerPaused = false;
    /** HashMap.*/
    private HashMap<String, String> mHashMap;
    /** DataCount.*/
    private int mDataCount;
    /** first visible item position default value.*/
    private int mSelectPosition = 0;
    /** topPosition.*/
    private int mToPosition = 0;
    /**インデント.*/
    private Intent mIntent = null;
    /**表示状態.*/
    private int mDisplayState = 0;
    /** 他サービスフラグ.*/
    private boolean mIsOtherService = false;
    /** タブ表示区別.*/
    private ContentDetailUtils.TabType tabType;
    /** ディスプレイ幅.*/
    private int mWidth;
    /** ディスプレイ高さ.*/
    private int mHeight;
    /** ナビゲーションバー含むディスプレイ高さ.*/
    private int mScreenNavHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DTVTLogger.start();
        if (savedInstanceState != null) {
            mPlayStartPosition = savedInstanceState.getInt(ContentDetailUtils.PLAY_START_POSITION);
            mViewPagerIndex = savedInstanceState.getInt( ContentDetailUtils.VIEWPAGER_INDEX);
            mIsPlayerPaused = savedInstanceState.getBoolean(ContentDetailUtils.IS_PLAYER_PAUSED);
            mHashMap = (HashMap<String, String>) savedInstanceState.getSerializable(ContentDetailUtils.EPISODE_TAB_DATA_HASH_MAP);
            mDataCount = savedInstanceState.getInt(ContentDetailUtils.EPISODE_TAB_DATA_COUNT);
            mSelectPosition = savedInstanceState.getInt(ContentDetailUtils.EPISODE_TAB_FIRST_VISIBLE_ITEM_POSITION);
            mToPosition = savedInstanceState.getInt(ContentDetailUtils.EPISODE_TAB_TOP_POSITION);
            savedInstanceState.clear();
        }
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeBlack);
        setStatusBarColor(R.color.contents_header_background);
        setContentView(R.layout.dtv_contents_detail_main_layout);
        initView();
        DTVTLogger.end();
    }

    @Override
    protected void onResume() {
        DTVTLogger.start();
        super.onResume();
        //checkOttPlayer();
        mDetailFullData = new VodMetaFullData();
        mDetailFullData.setmService_id("428");
        mDetailFullData.setCid("428");
        mDetailFullData.setCrid("428");
        if (mOttPlayerDataProvider != null && mDetailFullData != null){
            startOttPlayer();
        }
        if (mDtvContentsOttPlayerFragment != null) {
            mDtvContentsOttPlayerFragment.restartPlayerViewFromBg();
        }
        DTVTLogger.end();
    }

    @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
    @Override
    protected void onPause() {
        super.onPause( );
        DTVTLogger.start( );
        if (mOttPlayerDataProvider != null) {
            mOttPlayerDataProvider.stopPlay( );
        }
        DTVTLogger.end();
    }

    @Override
    protected void onDestroy() {
        DTVTLogger.start( );
        if (mOttPlayerDataProvider != null) {
            mOttPlayerDataProvider.stopPlay();
        }
        DTVTLogger.end();
        super.onDestroy();
    }

    /**
     * ビュー初期化.
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    private void initView() {
        mIntent = getIntent();
        setScreenSize();
        mThumbnailBtn = findViewById(R.id.dtv_contents_detail_main_layout_thumbnail_btn);
        mThumbnailBtn.setOnClickListener(this);
        mThumbnail = findViewById(R.id.dtv_contents_detail_main_layout_thumbnail);
        setPlayerLayoutParams(mThumbnail);
        mThumbnailRelativeLayout = findViewById(R.id.dtv_contents_detail_layout);
        mContractLeadingView = findViewById(R.id.contract_leading_view);
        Object object = mIntent.getParcelableExtra(ContentDetailUtils.RECORD_LIST_KEY);
//        if (object instanceof RecordedContentsDetailData) { //プレイヤーで再生できるコンテンツ
//            mDisplayState = ContentDetailUtils.PLAYER_ONLY;
//            RecordedContentsDetailData playerData = mIntent.getParcelableExtra(ContentDetailUtils.RECORD_LIST_KEY);
//            if (!TextUtils.isEmpty(playerData.getTitle())) {
//                setTitleText(playerData.getTitle());
//                setActionName(playerData.getTitle());
//            }
//            if (ContentsAdapter.DOWNLOAD_STATUS_COMPLETED == playerData.getDownLoadStatus() || !playerData.isRemote()) {
//                initPlayer(playerData);
//            } else {
//                setRemotePlayArrow(playerData);
//            }
//            if (!playerData.isLive()) {
//                showPlayerOnlyView(playerData);
//            }
//            super.sendScreenView(getString(R.string.google_analytics_screen_name_player_recording), //録画再生カスタムディメンション送信
//                    ContentDetailUtils.getRecordPlayerCustomDimensions(ContentDetailActivity.this, playerData.getTitle()));
//        }
        //ヘッダーの設定
        String sourceClass = mIntent.getStringExtra(DtvtConstants.SOURCE_SCREEN);
        if (sourceClass != null && !sourceClass.isEmpty()) {
            //赤ヘッダーである遷移元クラス名を保持
            setSourceScreenClass(sourceClass);
            enableHeaderBackIcon(false);
        } else {
            //詳細画面から詳細画面への遷移時は戻るアイコンを表示
            enableHeaderBackIcon(true);
        }
        setHeaderColor(false);
        enableGlobalMenuIcon(true);
        changeGlobalMenuIconToCloseIcon();
        initContentsView();
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setTabVisibility(false);
        }
    }

    /**
     * 再生用プレイヤー.
     */
    private void initOttFragment() {
        if (mDtvContentsOttPlayerFragment == null) {
            mDtvContentsOttPlayerFragment = new DtvContentsOttPlayerFragment();
            mDtvContentsOttPlayerFragment.setOttPlayerFragmentListener(this);
            mDtvContentsOttPlayerFragment.setScreenSize(mWidth, mHeight);
            mDtvContentsOttPlayerFragment.setParentLayout(mThumbnailRelativeLayout);
            mDtvContentsOttPlayerFragment.setScreenNavigationBarSize(mScreenNavHeight);
            FrameLayout frameLayout = findViewById(R.id.dtv_contents_detail_main_layout_ott_frame_layout);
            frameLayout.setVisibility(View.VISIBLE);
            setPlayerLayoutParams(frameLayout);
            mDtvContentsOttPlayerFragment.setFrameLayout(frameLayout);
            getSupportFragmentManager().beginTransaction().replace(R.id.dtv_contents_detail_main_layout_ott_frame_layout, mDtvContentsOttPlayerFragment).commit();
        }
    }

    /**
     * 詳細Viewの初期化.
     */
    private void initContentsView() {
        switch (mDisplayState) {
            case ContentDetailUtils.PLAYER_AND_CONTENTS_DETAIL:
            case ContentDetailUtils.CONTENTS_DETAIL_ONLY:
                mViewPager = findViewById(R.id.dtv_contents_detail_main_layout_vp);
                setBaseLayoutBackgroundColor(R.color.contents_detail_thumbnail_background);
                initContentData();
                break;
            case ContentDetailUtils.PLAYER_ONLY:
            default:
                break;
        }
    }

    /**
     * データの初期化.
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "EnumSwitchStatementWhichMissesCases"})
    private void initContentData() {
        DTVTLogger.start();
//        mFrameLayout = findViewById(R.id.header_watch_by_tv);
        String contentsId = mIntent.getStringExtra(ContentUtils.PLALA_INFO_BUNDLE_KEY);
//        createRemoteControllerView(true);
//        findViewById(R.id.remote_control_view).setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(contentsId)) { //ぷらら
            if (mDetailData == null) {
                mDetailData = new OtherContentsDetailData();
            }
            mDetailData.setContentsId(contentsId);
        } else {
            mDetailData = mIntent.getParcelableExtra(ContentUtils.RECOMMEND_INFO_BUNDLE_KEY);
            if (mDetailData != null) {
                if (ContentUtils.isOtherService(mDetailData.getServiceId())) { //検レコ
                    mIsOtherService = true;
                }
            } else {
                showProgressBar(false);
            }
        }
        tabType = ContentDetailUtils.TabType.TV_CH_BROADCAST;
        mContentsDetailFragment = getDetailFragment();
        mContentsDetailFragment.setContentsDetailFragmentScrollListener(this);
        createViewPagerAdapter();
        DTVTLogger.end();
    }

    /**
     * tabのレイアウトを設定.
     */
    private void initTab() {
        DTVTLogger.start();
        if (mTabLayout == null) {
            mTabLayout = new TabItemLayout(this);
            mTabLayout.setTabClickListener(this);
            mTabLayout.initTabView(mTabNames, TabItemLayout.ActivityType.CONTENTS_DETAIL_ACTIVITY);
            RelativeLayout tabRelativeLayout = findViewById(R.id.rl_dtv_contents_detail_tab);
            tabRelativeLayout.setVisibility(View.VISIBLE);
            tabRelativeLayout.addView(mTabLayout);
        } else {
            mTabLayout.resetTabView(mTabNames);
        }
        DTVTLogger.end();
    }

    /**
     * ビューページング作成.
     */
    private void createViewPagerAdapter() {
        mTabNames = ContentDetailUtils.getTabNames(tabType, ContentDetailActivity.this);
        initTab();
        mViewPager.clearOnPageChangeListeners();
        mViewPager.setAdapter(new ContentsDetailPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                // スクロールによるタブ切り替え
                super.onPageSelected(position);
                if (mUserInfoDataProvider == null) {
                    mUserInfoDataProvider = new UserInfoDataProvider(getApplicationContext());
                }
                if (mUserInfoDataProvider.isH4dUser()
                        && ContentDetailUtils.isOttContent(mDetailFullData)
                        && !mIsOttRequestSuccess
                        && !mIsOttRequestRunning) {
                    // タブに対してタップやスワイプをする時、もし前回取得は失敗だったら全OTTチャンネルリストを再取得
                    registerBroadcastOtt();
                    getOttProgramFromDataProvider(DateUtils.getNowTimeFormatEpoch(), false);  // 今日分をOTTチャンネル取得
                }
                mTabLayout.setTab(position);
                if (mIsControllerVisible && tabType == ContentDetailUtils.TabType.VOD_EPISODE) {
                    if (position == ContentDetailUtils.CONTENTS_DETAIL_CHANNEL_EPISODE_TAB_POSITION) {
                        setRemoteControllerViewVisibility(View.GONE);
                    } else {
                        setRemoteControllerViewVisibility(View.VISIBLE);
                    }
                    if (tabType == ContentDetailUtils.TabType.VOD_EPISODE && position == ContentDetailUtils.CONTENTS_DETAIL_INFO_TAB_POSITION) {
                        setActionName(getTitleText().toString());
                    }
                } else if (mIsControllerVisible && tabType == ContentDetailUtils.TabType.TV_CH_BROADCAST) {
                    if (position == ContentDetailUtils.CONTENTS_DETAIL_CHANNEL_BROADCAST_TAB_POSITION) {
                        //放送番組タブにて表示しない
                        setRemoteControllerViewVisibility(View.GONE);
                    } else {
                        setRemoteControllerViewVisibility(View.VISIBLE);
                    }
                }
                if (mDetailFullData != null && !ContentUtils.TV_SERVICE_FLAG_DCH_IN_HIKARI.equals(mDetailFullData.getmTv_service())) {
                    //タブが番組詳細にあり、かつ録画予約ボタンが表示している場合
                    if ((tabType == ContentDetailUtils.TabType.TV_CH || tabType == ContentDetailUtils.TabType.TV_CH_BROADCAST)
                            && position == ContentDetailUtils.CONTENTS_DETAIL_INFO_TAB_POSITION
                            && getDetailFragment().checkVisibilityRecordingReservationIcon()) {
                        //放送開始時刻が2時間未満であるかどうかの判定
                        if (DateUtils.isWithInTwoHour(mDetailFullData.getPublish_start_date())) {
                            mContentsType = ContentUtils.ContentsType.HIKARI_TV_WITHIN_TWO_HOUR;
                            getDetailFragment().changeVisibilityRecordingReservationIcon(mViewIngType, mContentsType);
                        }
                    }
                }
                sendScreenViewForPosition(position);
            }
        });
        if (tabType == ContentDetailUtils.TabType.TV_CH_BROADCAST) {
            mViewPager.setOffscreenPageLimit(2);
            if (mDetailFullData != null) {
                boolean isNowOnAir = ContentUtils.isNowOnAir(mDetailFullData.getPublish_start_date(),mDetailFullData.getPublish_end_date());
                if (isNowOnAir) {
                    //TVコンテンツのコンテンツ詳細を表示した際のファーストビューとすること
                    mViewPager.setCurrentItem(ContentDetailUtils.CONTENTS_DETAIL_CHANNEL_BROADCAST_TAB_POSITION);
                }
            }
        }
        if (mViewPagerIndex >= 0 && tabType == ContentDetailUtils.TabType.VOD_EPISODE) {
            mViewPager.setCurrentItem(mViewPagerIndex);
            mViewPagerIndex = DEFAULT_TAB_INDEX;
        }
    }


    /**
     * 詳細tabを取得.
     * @return 現在表示しているfragment
     */
    private DtvContentsDetailFragment getDetailFragment() {
        if (mContentsDetailFragment == null) {
            Fragment currentFragment = getFragmentFactory().createFragment(0, tabType);
            mContentsDetailFragment = (DtvContentsDetailFragment) currentFragment;
        }
        return mContentsDetailFragment;
    }

    /**
     * フラグメントファクトリー取得.
     * @return DtvContentsDetailFragmentFactory
     */
    private DtvContentsDetailFragmentFactory getFragmentFactory() {
        if (mFragmentFactory == null) {
            mFragmentFactory = new DtvContentsDetailFragmentFactory();
        }
        return mFragmentFactory;
    }

    /**
     * 番組情報/チャンネルタブの表示設定.
     * @param visible 表示要否
     */
    private void setTabVisibility(final boolean visible) {
        switch (mDisplayState) {
            case ContentDetailUtils.CONTENTS_DETAIL_ONLY:
            case ContentDetailUtils.PLAYER_AND_CONTENTS_DETAIL:
                if (visible) {
                    findViewById(R.id.dtv_contents_detail_main_layout_vp).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl_dtv_contents_detail_tab).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.dtv_contents_detail_main_layout_vp).setVisibility(View.GONE);
                    findViewById(R.id.rl_dtv_contents_detail_tab).setVisibility(View.GONE);
                }
                break;
            case ContentDetailUtils.PLAYER_ONLY:
                if (visible) {
                    findViewById(R.id.dtv_contents_detail_player_only).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.dtv_contents_detail_player_only).setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * プロセスバーを表示する.
     * @param showProgressBar プロセスバーを表示するかどうか
     */
    private void showProgressBar(final boolean showProgressBar) {
        View view = findViewById(R.id.contents_detail_scroll_layout);
        if (view == null) {
            return;
        }
        if (showProgressBar) {
            if (!NetWorkUtils.isOnline(this)) {
                return;
            }
            view.setVisibility(View.INVISIBLE);
            setRemoteProgressVisible(View.VISIBLE);
        } else {
//            if (!mIsScreenViewSent && !mIsH4dPlayer && mDetailFullData != null) {
//                sendScreenViewForPosition(ContentDetailUtils.CONTENTS_DETAIL_INFO_TAB_POSITION);
//                mIsScreenViewSent = true;
//            }
            setRemoteProgressVisible(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserVisibleHint(boolean isVisibleToUser, DtvContentsDetailFragment dtvContentsDetailFragment) {
        if (!isVisibleToUser || dtvContentsDetailFragment.isRequestFinish()) {
            return;
        }
//        if (mIsOtherService) {
//            //getContentDetailInfoFromSearchServer(1);
//        } else {
            getContentDetailDataFromPlala(mDetailData.getContentsId());
//        }
    }

    @Override
    public void onClickTab(int position) {
        DTVTLogger.start("position = " + position);
        if (null != mViewPager) {
            mViewPager.setCurrentItem(position);
            if (mChannelFragment != null && position == 1) {
                if (mDetailFullData != null && (TextUtils.isEmpty(mDetailFullData.getmTv_service())
                        || ContentUtils.isBsOrTtbProgramPlala(mDetailFullData.getmTv_service()))) {
                    if (mChannelFragment.getContentsData() != null && mChannelFragment.getContentsData().size() > 0) {
                        mChannelFragment.setNotifyDataChanged();
                    }
                }
            }

            //TODO
            if (position == 2) {
                getBroadcastFragment();
                // タブに対してタップやスワイプをする時、もし前回取得は失敗だったら全OTTチャンネルリストを再取得
                //registerBroadcastOtt();
                getOttProgramFromDataProvider(DateUtils.getNowTimeFormatEpoch(), false);
            }
        }
    }

    @Override
    public void onChannelLoadMore() {

    }

    @Override
    public void onUserVisibleHint(boolean isVisibleToUser, DtvContentsChannelFragment fragment) {

    }

    @Override
    public void onUserVisibleHint(boolean isVisibleToUser, DtvContentsBroadcastFragment fragment) {

    }

    @Override
    public void onEpisodeLoadMore(int position) {

    }

    @Override
    public void onItemClickCallback(ContentsData contentsData, boolean isThumbnailTap) {

    }

    @Override
    public void onUserVisibleHint(boolean isVisibleToUser, DtvContentsEpisodeFragment fragment) {

    }

    @Override
    public void onContentsDetailInfoCallback(VodMetaFullData contentsDetailInfo, boolean clipStatus) {
        runOnUiThread( new Runnable( ){
            @Override
            public void run() {
                getChannelInfo();
            }
        } );
    }

    @Override
    public void onRoleListCallback(ArrayList<RoleListMetaData> roleListInfo) {

    }

    @Override
    public void recordingReservationResult(RemoteRecordingReservationResultResponse response) {

    }

    @Override
    public void onRentalVodListCallback(PurchasedVodListResponse response) {

    }

    @Override
    public void onRentalChListCallback(PurchasedChannelListResponse response) {

    }

    @Override
    public void channelInfoCallback(final ChannelInfoList channelsInfo, String[] serviceIdUniq) {
        DTVTLogger.start();
        runOnUiThread(new Runnable() {
            @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
            @Override
            public void run() {
                if (channelsInfo != null && channelsInfo.getChannels() != null) {
                    List<ChannelInfo> channels = channelsInfo.getChannels();
                    ContentDetailUtils.sort(channels);
                    if (channels.size() > 0) {
                        if (mViewPager.getCurrentItem() == ContentDetailUtils.CONTENTS_DETAIL_CHANNEL_EPISODE_TAB_POSITION) {
                            mChannelFragment = getChannelFragment();
                            ChannelInfo channelInfo = channels.get(0);
                            ArrayList<ScheduleInfo> scheduleInfos = channelInfo.getSchedules();
                            if (mDateIndex == 1) {
                                mChannelFragment.clearContentsData();
                            }
                            boolean isFirst = false;
                            for (int i = 0; i < scheduleInfos.size(); i++) {
                                ScheduleInfo scheduleInfo = scheduleInfos.get(i);
                                String endTime = scheduleInfo.getEndTime();
                                String startTime = scheduleInfo.getStartTime();
                                if (!ContentUtils.checkScheduleDate(startTime, endTime)) {
                                    continue;
                                }
                                if (!ContentUtils.isLastDate(endTime)) {
                                    if (mDateList != null) {
                                        ContentsData contentsData = new ContentsData();
                                        if (!isFirst) {
                                            if (mDateIndex == 1) {
                                                if (ContentUtils.isNowOnAir(startTime, endTime)) {
                                                    contentsData.setChannelName(getString(R.string.home_label_now_on_air));
                                                }
                                            }
                                            contentsData.setSubTitle(ContentDetailUtils.getDateForChannel(mChannelDate, getApplicationContext()));
                                            isFirst = true;
                                        }
                                        ContentDetailUtils.setContentsData(contentsData, scheduleInfo);
                                        mChannelFragment.addContentsData(contentsData);
                                    }
                                }
                            }
                            if (mDateIndex == 1) {
                                getChannelDetailByPageNo();
                            } else {
                                checkClipStatus(ContentDetailUtils.CLIP_BUTTON_CHANNEL_UPDATE);
                            }
                        }
                    }
                    channelLoadCompleted();
                } else {
                    mChannelFragment = getChannelFragment();
                    mDateIndex--;
                    if (mChannelFragment.getContentsData() == null || mChannelFragment.getContentsData().size() == 0) {
                        if (!NetWorkUtils.isOnline(ContentDetailActivity.this)) {
                            mChannelFragment.loadFailed();
                            showGetDataFailedToast(getString(R.string.network_nw_error_message));
                        } else {
                            mChannelFragment.loadComplete();
                        }
                    } else {
                        mChannelFragment.loadComplete();
                    }
                }
                showProgressBar(false);
            }
        });
        DTVTLogger.end();
    }

    @Override
    public void channelListCallback(final ArrayList<ChannelInfo> channels) {
        DTVTLogger.start();
        runOnUiThread(new Runnable() {
            @SuppressWarnings("OverlyComplexMethod")
            @Override
            public void run() {
                if (mViewPagerIndex >= 0) {
                    mViewPager.setCurrentItem(mViewPagerIndex);
                    mViewPagerIndex = DEFAULT_TAB_INDEX;
                }
                /* H4d契約済みの場合、全チャンネルリストを取得完了時にOTTの１日分全チャンネルリスト取得を実施 */
                mAllChannelList = channels;
                if (mUserInfoDataProvider == null) {
                    mUserInfoDataProvider = new UserInfoDataProvider(getApplicationContext());
                }
                if (mUserInfoDataProvider.isH4dUser()
                        && ContentDetailUtils.isOttContent(mDetailFullData)
                        && !mIsOttRequestSuccess) {
                    registerBroadcastOtt();
                    getOttProgramFromDataProvider(DateUtils.getNowTimeFormatEpoch(), false); // 今日分をOTTチャンネル取得
                }
                if (channels == null || channels.isEmpty()) {
                    if (!mIsOtherServiceDtvChLoading) {
                        if (tabType == ContentDetailUtils.TabType.TV_CH) {
                            tabType = ContentDetailUtils.TabType.VOD;
                            createViewPagerAdapter();
                        }
                    } else {
                        showProgressBar(false);
                    }
                    showErrorToast(ContentDetailUtils.ErrorType.channelListGet);
                    return;
                }
                DtvContentsDetailFragment detailFragment = getDetailFragment();
                if (mIsOtherServiceDtvChLoading) {
                    OtherContentsDetailData detailData = detailFragment.getOtherContentsDetailData();
                    ChannelInfo mChannelInfo = ContentDetailUtils.setPureDchChannelName(channels, mDetailData.getChannelId());
                    if (mChannelInfo != null) {
                        mChannel = mChannelInfo;
                        detailData.setChannelName(mChannelInfo.getTitle());
                        mDetailData = detailData;
                    }
                    detailFragment.noticeRefresh();
                    showProgressBar(false);
                    return;
                }
                //DBに保存されているUserInfoから契約情報を確認する
                String contractInfo = UserInfoUtils.getUserContractInfo( SharedPreferencesUtils.getSharedPreferencesUserInfo(ContentDetailActivity.this));
                DTVTLogger.debug("contractInfo: " + contractInfo);
                //コンテンツタイプ取得 TODO delete
                ContentUtils.ContentsType type = mDetailData.getContentCategory();
                DTVTLogger.debug("display thumbnail contents type = " + type);
                mContentsType = type;
                if (mContentsType != null) {
                    //TODO delete
                    mServiceIdUniq = "ttb_0428";
                    //チャンネル情報取得して、更新する
                    if (!TextUtils.isEmpty(mServiceIdUniq)) {
                        for (int i = 0; i < channels.size(); i++) {
                            ContentsData contentsData = new ContentsData();
                            ChannelInfo channel = channels.get(i);
                            if (mServiceIdUniq.equals(channel.getServiceIdUniq())) {
                                mChannel = channel;
                                //チャンネル情報取得完了前にタブ切替されていた場合はここでチャンネルタブ表示処理を開始する
                               // if (mViewPager.getCurrentItem() == ContentDetailUtils.CONTENTS_DETAIL_CHANNEL_EPISODE_TAB_POSITION) {
                                    showChannelProgressBar(true);
                                    getChannelDetailData(mChannel);
                               // }
                                String channelName = channel.getTitle();
                                OtherContentsDetailData detailData = detailFragment.getOtherContentsDetailData();
                                if (detailData != null) {
                                    detailData.setChannelName(channelName);
                                    detailFragment.setOtherContentsDetailData(detailData);
                                }
                                contentsData.setTitle(channel.getTitle());
                                contentsData.setChannelNo(String.valueOf(channel.getChannelNo()));
                                contentsData.setThumDetailURL(channel.getThumbnail());
                                contentsData.setThumURL(channel.getThumbnail());
                                contentsData.setServiceIdUniq(channel.getServiceIdUniq());
                                mChannelFragment.addContentsData(contentsData);
                                break;
                            }
                        }
                        mChannelFragment.setNotifyDataChanged();
                        detailFragment.refreshChannelInfo();
                    }
                }
                //TV番組の視聴可否判定に入らなかったものはここで視聴可否判定する
                if (mViewIngType == null || mViewIngType.equals(ContentUtils.ViewIngType.NONE_STATUS)) {
                    mViewIngType = ContentUtils.getViewingType(contractInfo, mDetailFullData, mChannel);
                }
                if (mChannel == null) {
                    if (tabType == ContentDetailUtils.TabType.TV_CH) {
                        tabType = ContentDetailUtils.TabType.VOD;
                        createViewPagerAdapter();
                    }
                }
                if (mDetailFullData != null) {
                    checkWatchContents(mViewIngType);
                }
                //コンテンツ種別ごとの視聴可否判定を実行
                getViewingTypeRequest(mViewIngType);
            }
        });
        getOttProgramFromDataProvider(DateUtils.getNowTimeFormatEpoch(), false);
        DTVTLogger.end();
    }

    @Override
    public void onOttChannelProgramCallback(final ChannelInfoList channelsInfo, final String[] serviceIdUniq, final boolean isUpdate) {
        DTVTLogger.start();
        if (channelsInfo != null && channelsInfo.getChannels() != null && channelsInfo.getChannels().size() != 0) {
            List<ChannelInfo> currentOttChannel = new ArrayList<>();
            for (ChannelInfo item : channelsInfo.getChannels()) {
                if (item.getServiceIdUniq().equals(mDetailFullData.getServiceIdUniq())) {
                    currentOttChannel.add(item);
                    break;
                }
            }
            // 番組切替のため、Timerを設定
            DTVTLogger.debug("TimerNoticeManager start");
            mIsOttRequestSuccess = true;
            TimerNoticeManager.shared().setContext(getApplication());
            if (isUpdate) {
                TimerNoticeManager.shared().updateChannelInfo(currentOttChannel);
            } else {
                TimerNoticeManager.shared().onBind(currentOttChannel);
            }
        } else {
            mIsOttRequestSuccess = false;
            if (isUpdate) {
                // 番組切替時失敗場合、Dialogを表示 (OTTのコンテンツ詳細表示時とタブ切り替え時Dialog表示なし）
                String errorMessage = getString(R.string.common_get_data_failed_message);
                showCommonControlErrorDialog(errorMessage, CustomDialog.ShowDialogType.OTT_CHANNEL_PROGRAM_GET_ERROR, null, null, null);
                mIsOttRequestRunning = false;
            } else {
                if (!TextUtils.isEmpty(mServiceIdUniq)) {
                    List<ChannelInfo> currentChannelList = new ArrayList<>();
                    ChannelInfo channelInfo = new ChannelInfo();
                    channelInfo.setServiceIdUniq(mServiceIdUniq);
                    ArrayList<ScheduleInfo> scheduleInfoList = new ArrayList<>();
                    ScheduleInfo scheduleInfo = new ScheduleInfo();
                    if (mDetailFullData != null) {
                        scheduleInfo.setEndTime(DateUtils.formatEpochToString(mDetailFullData.getPublish_end_date()));
                        scheduleInfo.setTitle(mDetailFullData.getTitle());
                    }
                    scheduleInfo.setServiceIdUniq(mServiceIdUniq);
                    scheduleInfoList.add(scheduleInfo);
                    channelInfo.setSchedules(scheduleInfoList);
                    currentChannelList.add(channelInfo);
                    TimerNoticeManager.shared().setContext(getApplication());
                    TimerNoticeManager.shared().onBind(currentChannelList);
                }
            }
        }
        DTVTLogger.end();
    }

    @Override
    public void mobileTvScheduleListCallback(final List<ContentsData> mobileChannelList) {
        //DbThreadからのコールバックではUIスレッドとして扱われないため
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mobileChannelList != null && mobileChannelList.size() > 0) {
                    if (mDtvContentsOttPlayerFragment != null) {
                        if (mDtvContentsBroadcastFragment != null) {
                            mDtvContentsBroadcastFragment.setContentsData(mobileChannelList);
                            mDtvContentsBroadcastFragment.setNotifyDataChanged();
                        }
                        mChannelList = mobileChannelList;
                        mDtvContentsOttPlayerFragment.setChannelListData(mChannelList);
                    }
                } else {
                    // 番組切替時失敗場合、Dialogを表示 (OTTのコンテンツ詳細表示時とタブ切り替え時Dialog表示なし）
                    String errorMessage = getString(R.string.common_get_data_failed_message);
                    showCommonControlErrorDialog(errorMessage, CustomDialog.ShowDialogType.OTT_CHANNEL_PROGRAM_GET_ERROR, null, null, null);
                }
            }
        });
    }

    @Override
    public void clipKeyResult() {

    }

    @Override
    public void onUserVisibleHint(final DtvContentsOttPlayerFragment dtvContentsDetailFragment) {
        if (dtvContentsDetailFragment.getOttPlayerData() != null) {
            return;
        }
        startOttPlayer();
    }

    @Override
    public void onOttErrorCallback(int what, int extra) {
        //TODO：仮実装エラー文言表示
        String msg = "再生ができませんでした(NP%s)";
        showCommonControlErrorDialog(msg.replace("%s", String.valueOf(extra)), null, null, null, null);
    }

    @Override
    public void onOttScreenOrientationChangeCallBack(boolean isLandscape) {
        if (isLandscape) {
            setTitleVisibility(false);
            setTabVisibility(false);
            setRemoteControllerViewVisibility(View.GONE);
        } else {
            setTitleVisibility(true);
            setTabVisibility(true);
            if (tabType == ContentDetailUtils.TabType.TV_CH_BROADCAST) {
                setRemoteControllerViewVisibility(View.GONE);
            } else {
                setRemoteControllerViewVisibility(View.VISIBLE);
            }
        }
        initDisplayMetrics();
        int width = getWidthDensity();
        initDisplayMetrics();
        int height = getHeightDensity();
        mDtvContentsOttPlayerFragment.setScreenSize(width, height);
    }

    /**
     * 前回playTokenの有無チェック.
     */
    private void checkOttPlayer() {
        if (mOttPlayerDataProvider == null) {
            mOttPlayerDataProvider = new OttPlayerDataProvider(this);
        }
        mOttPlayerDataProvider.stopPlay();
    }

    /**
     * ott再生開始IF取得.
     */
    private void startOttPlayer() {
        List<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put( OttContentUtils.OTT_PLAY_START_KIND, OttContentUtils.OTT_PLAY_START_KIND_MAIN);
        hashMap.put(OttContentUtils.OTT_PLAY_START_SERVICE_ID, mDetailFullData.getmService_id());
        hashMap.put(OttContentUtils.OTT_PLAY_START_CID, mDetailFullData.getCid());
        hashMap.put(OttContentUtils.OTT_PLAY_START_CRID, mDetailFullData.getCrid());
        list.add(hashMap);
        if (mOttPlayerDataProvider == null) {
            mOttPlayerDataProvider = new OttPlayerDataProvider(this);
        }
        mOttPlayerDataProvider.startPlay(OttContentUtils.OTT_PLAY_START_PLAY_TYPE_1, OttContentUtils.OTT_PLAY_START_QUALITY_DEFAULT,
                OttContentUtils.OTT_PLAY_START_VIEW_CONTINUE_FLG_DEFAULT, list);
    }

    @Override
    public void onStartPlayerCallback(OttPlayerStartResponse ottPlayerStartResponse) {
        if (ottPlayerStartResponse != null && ottPlayerStartResponse.getOttPlayerStartData() != null) {
            List<OttPlayerStartData> ottPlayerStartDatas = ottPlayerStartResponse.getOttPlayerStartData();
            //TODO リクエストのパラメータとリスポンス結果紐づけできないため、一旦固定にする
            OttPlayerStartData ottPlayerStartData = ContentDetailUtils.filterOttPlayerData(ottPlayerStartDatas, "0451", "0451");
            if (mDtvContentsOttPlayerFragment != null && ottPlayerStartData != null) {
                ottPlayerStartData.setThumbnail("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0408.webp");
                ottPlayerStartData.setTitle(mDetailData.getTitle());
                ottPlayerStartData.setChannelDate(mDetailData.getChannelDate());
                ottPlayerStartData.setChannelName(mDetailData.getChannelName());
                mDetailFullData.setServiceIdUniq("ttb_0408");
                ottPlayerStartData.setServiceIdUniq(mDetailFullData.getServiceIdUniq());
                mDtvContentsOttPlayerFragment.setOttPlayerData(ottPlayerStartData);
                mDtvContentsOttPlayerFragment.startPlayer();
                showPlayIcon(false);
                mContractLeadingView.setVisibility(View.GONE);
                mThumbnail.setVisibility(View.GONE);
                mThumbnailBtn.setVisibility(View.GONE);
            } else {
                showCommonControlErrorDialog(getString(R.string.contents_detail_ott_player_error_common_msg), null, null, null, null);
            }
        } else {
            showCommonControlErrorDialog(getString(R.string.contents_detail_ott_player_error_common_msg), null, null, null, null);
        }
    }

    @Override
    public void onStopPlayerCallback(boolean isSuccess) {
        if (isSuccess) {
            initOttFragment();
        } else {
            showCommonControlErrorDialog(getString(R.string.contents_detail_ott_player_error_msg), null, null, null, null);
        }
    }

    @Override
    public void onKeepAliveCallback(boolean isAlive) {
        DTVTLogger.debug("onKeepAliveCallback is : " + isAlive);
    }

    /**
     * コンテンツ詳細用ページャアダプター.
     */
    private class ContentsDetailPagerAdapter extends FragmentStatePagerAdapter{
        /**
         * コンストラクタ.
         * @param fm FragmentManager
         */
        ContentsDetailPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            Fragment fragment = getFragmentFactory().createFragment(position, tabType);
            //Fragmentへデータを渡す
            Bundle args = new Bundle();
            //TODO TEST
            if (mDetailData != null) {
                mDetailData.setContentCategory(ContentUtils.ContentsType.HIKARI_IN_DTV);
                mDetailData.setChannelDate("2020/10/10 - 2020/11/11 見逃し");
                mDetailData.setClipExec(true);
                mDetailData.setTitle("ふしぎエンドレス");
                mDetailData.setChannelName("ＮＨＫＥテレ１東京");
//            mDetailData.setThumURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0408.webp");
                mDetailData.setDtv("1");
                mDetailData.setContentsId("0480");
//            mDetailData.setCopyright("\\n");

                args.putParcelable(ContentUtils.RECOMMEND_INFO_BUNDLE_KEY, mDetailData);
            }
            if (position == 1) {
                if (fragment instanceof DtvContentsChannelFragment) {
                    ((DtvContentsChannelFragment) fragment).setScrollCallBack(ContentDetailActivity.this);
                } else {
                    ((DtvContentsEpisodeFragment) fragment).setScrollCallBack(ContentDetailActivity.this);
                }
            } else if (position == 2) {
                ((DtvContentsBroadcastFragment) fragment).setContentsDetailFragmentListener(ContentDetailActivity.this);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabNames.length;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return mTabNames[position];
        }
    }

    /**
     * OTT用のBroadcastReceiverを登録
     */
    private void registerBroadcastOtt() {
        // NowOnAirと未来のOTTコンテンツのみ、BroadcastReceiver登録
        if (mOttContentChangeReceiver == null
                && !ContentUtils.isPastContent(mDetailFullData.getPublish_end_date())) {
            mOttContentChangeReceiver = new OttBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ContentUtils.CONTENTS_CHANGE_ACTION);
            filter.addAction(ContentUtils.CHANNEL_COMPLETED_ACTION);
            LocalBroadcastManager.getInstance(getApplication()).registerReceiver(mOttContentChangeReceiver, filter);
        }
    }

    /**
     * コンテンツ詳細画面で番組切替用のBroadcastReceiver.
     */
    private class OttBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            DTVTLogger.start();
            final TimerNoticeInfo timerNoticeInfo = intent.getParcelableExtra(ContentUtils.NOTICE_INFO);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ContentUtils.CONTENTS_CHANGE_ACTION.equals(intent.getAction())) {
                        // NowOnAirと未来のOTTコンテンツのみ、UI更新
                        if (timerNoticeInfo.getServiceIdUniq().equals(mDetailFullData.getServiceIdUniq())
                                && !ContentUtils.isPastContent(DateUtils.getSecondEpochTime(timerNoticeInfo.getEndTime()))) {
                            if (mContentsDetailFragment != null) {
                                //番組詳細UIを更新
                                getContentDetailDataFromPlala(timerNoticeInfo.getContentsId());
                            }
                        }
                    } else {
                        if (mIsOttRequestSuccess) {
                            //本日番組全て完了する場合、翌日の番組を取得
                            getOttProgramFromDataProvider(DateUtils.getTomorrowTimeFormatEpoch(), true); // 翌日分をOTTチャンネル取得
                        } else {
                            //　次のコンテンツは無くなった場合、もう一回OTTリストを取得
                            mIsOttRequestRunning = true;
                            getOttProgramFromDataProvider(DateUtils.getNowTimeFormatEpoch(), true); // 本日分をOTTチャンネル取得
                        }
                    }
                }
            });
            DTVTLogger.end();
        }
    }

    /**
     * getOttProgramFromDataProvider.
     * @param timeFormatEpoch timeFormatEpoch
     * @param isNeedShowError isShowError
     */
    private void getOttProgramFromDataProvider(final long timeFormatEpoch, final boolean isNeedShowError) {
        if (mOttProgramListDataProvider == null) {
            mOttProgramListDataProvider = new OttProgramListDataProvider(this);
        }
        mOttProgramListDataProvider.getAllChannel(DateUtils.formatEpochToSimpleDate(timeFormatEpoch), mAllChannelList, isNeedShowError);
        mOttProgramListDataProvider.getTvScheduleMobileListData();
    }

    /**
     * コンテンツ詳細データ取得.
     *
     * @param contentId コンテンツID
     */
    private void getContentDetailDataFromPlala(final String contentId) {
        DTVTLogger.start();
        mContentsDetailDataProvider = new ContentsDetailDataProvider(this);
        if (contentId != null) {
            DTVTLogger.debug("contentId:" + contentId);
            String[] cRid = new String[1];
            cRid[cRid.length - 1] = contentId;
            UserInfoDataProvider userInfoDataProvider = new UserInfoDataProvider(this);
            int ageReq = userInfoDataProvider.getUserAge();
            String areaCode = UserInfoUtils.getAreaCode(this);
            mContentsDetailDataProvider.getContentsDetailData(cRid, "", ageReq, areaCode);
        } else {
            DTVTLogger.debug("contentId取得失敗しました。");
        }
        DTVTLogger.end();
    }

    /**
     * リモートコントローラーViewのVisibilityを変更.
     *
     * @param visibility 表示非表示指定
     */
    protected void setRemoteControllerViewVisibility(final int visibility) {
//        if (mRemoteControllerView != null) {
//            findViewById(R.id.base_remote_controller_rl).setVisibility(visibility);
//        }
    }

    /**
     * 表示中タブの内容によってスクリーン情報を送信する.
     * @param position 表示中タブ
     */
    private void sendScreenViewForPosition(final int position) {
        if (contentType == null) {
            return;
        }
        String screenName;
        String serviceName;
        String contentsType1;
        String contentsType2 = null;
        if (mIsOtherService) {
            screenName = getOtherScreenName();
            serviceName = ContentUtils.getServiceName(ContentDetailActivity.this, mDetailData.getServiceId());
            contentsType1 = ContentUtils.getContentsType1(ContentDetailActivity.this, mDetailData.getServiceId());
            switch (contentType) {
                case TV:
                    contentsType2 = getString(R.string.google_analytics_custom_dimension_contents_type2_live);
                    break;
                case VOD:
                    contentsType2 = getString(R.string.google_analytics_custom_dimension_contents_type2_void);
                    break;
                default:
                    break;
            }
        } else {
            String tabName = mTabNames[position];
            screenName = ContentDetailUtils.getScreenNameMap(contentType, ContentDetailActivity.this, mIsH4dPlayer).get(tabName);
            serviceName = getString(R.string.google_analytics_custom_dimension_service_h4d);
            contentsType1 = ContentUtils.getContentsType1(ContentDetailActivity.this, mHikariType);
            switch (contentType) {
                case TV:
                    contentsType2 = getString(R.string.google_analytics_custom_dimension_contents_type2_live);
                    break;
                case VOD:
                    contentsType2 = getString(R.string.google_analytics_custom_dimension_contents_type2_void);
                    break;
                default:
                    break;
            }
        }
        if (screenName == null) {
            return;
        }
        SparseArray<String> customDimensions = null;
        if (!TextUtils.isEmpty(contentsType1) && !TextUtils.isEmpty(contentsType2)) {
            String loginStatus = null;
            if (!mIsOtherService && !mIsH4dPlayer) {
                UserState userState = UserInfoUtils.getUserState(ContentDetailActivity.this);
                if (UserState.LOGIN_NG.equals(userState)) {
                    loginStatus = getString(R.string.google_analytics_custom_dimension_login_ng);
                } else {
                    loginStatus = getString(R.string.google_analytics_custom_dimension_login_ok);
                }
            }
            String contentName = getTitleText().toString();
            if (TextUtils.isEmpty(contentName) && mDetailFullData != null) {
                contentName = mDetailFullData.getTitle();
            }
            customDimensions = ContentUtils.getCustomDimensions(loginStatus, serviceName, contentsType1, contentsType2, contentName);
        }
        //super.sendScreenView(screenName, customDimensions);
    }

    /**
     * 他サービススクリーン名取得.
     * @return 他サービススクリーン名
     */
    private String getOtherScreenName() {
        String screenName = null;
        if (mViewPager.getCurrentItem() == ContentDetailUtils.CONTENTS_DETAIL_CHANNEL_EPISODE_TAB_POSITION
                &&  tabType == ContentDetailUtils.TabType.VOD_EPISODE) {
            if (ContentUtils.D_ANIMATION_CONTENTS_SERVICE_ID == mDetailData.getServiceId()) {
                screenName = getString(R.string.google_analytics_screen_name_content_detail_other_danime_episode);
            } else if (ContentUtils.DTV_CONTENTS_SERVICE_ID == mDetailData.getServiceId()) {
                screenName = getString(R.string.google_analytics_screen_name_content_detail_other_dtv_episode);
            }
        } else {
            screenName = ContentUtils.getOtherServiceScreenName(ContentDetailActivity.this, mDetailData.getServiceId());
        }
        return screenName;
    }


    /**
     * チャンネルエリアのプロセスバーを表示する.
     * @param showProgressBar プロセスバーを表示するかどうか
     */
    private void showChannelProgressBar(final boolean showProgressBar) {
        if (mChannelFragment == null) {
            mChannelFragment = getChannelFragment();
            //フラグメント取得失敗時のヌルチェックを追加
            if (mChannelFragment == null) {
                return;
            }
        }
        if (showProgressBar) {
            // オフライン時は表示しない
            if (!NetWorkUtils.isOnline(this)) {
                return;
            }
            mChannelFragment.showProgress(true);
        } else {
            //リモートビューを非表示にする
            mChannelFragment.hideRemoteControllerView(mDetailFullData == null ? 0 : mDetailFullData.getOttFlg(), this);
            mChannelFragment.showProgress(false);
        }
    }

    /**
     * チャンネル情報を取得.
     */
    private void getChannelInfo() {
        if (mScaledDownProgramListDataProvider == null) {
            mScaledDownProgramListDataProvider = new ScaledDownProgramListDataProvider(this);
        }
        mScaledDownProgramListDataProvider.getChannelList(0, 0, "", JsonConstants.CH_SERVICE_TYPE_INDEX_ALL);
    }

    /**
     * チャンネルフラグメント取得.
     * @return currentFragment
     */
    private DtvContentsChannelFragment getChannelFragment() {
        if (tabType != ContentDetailUtils.TabType.TV_CH && tabType != ContentDetailUtils.TabType.TV_CH_BROADCAST) {
            return null;
        }
        Fragment currentFragment = getFragmentFactory().createFragment(1, tabType);
        return (DtvContentsChannelFragment) currentFragment;
    }

    /**
     * ページングでチャンネルデータ取得.
     */
    private void getChannelDetailByPageNo() {
        DTVTLogger.start();
        if (mScaledDownProgramListDataProvider == null) {
            mScaledDownProgramListDataProvider = new ScaledDownProgramListDataProvider(this);
        }
        String[] serviceIdUniqs = new String[]{mChannel.getServiceIdUniq()};
        mDateList = null;
        if (mDateIndex <= 6) { //一週間以内
            mDateList = new String[1];
        }
        if (mDateList != null) {
            try {
                mDateList = DateUtils.getDateList(mDateIndex);
                mChannelDate = DateUtils.getChannelDate(mDateIndex);
            } catch (ParseException e) {
                channelLoadCompleted();
                DTVTLogger.debug(e);
            }
            if (serviceIdUniqs[0] == null) {
                DTVTLogger.error("No channel number");
                channelLoadCompleted();
                return;
            }
            mDateIndex++;
            mScaledDownProgramListDataProvider.setAreaCode(UserInfoUtils.getAreaCode(this));
            mScaledDownProgramListDataProvider.getProgram(serviceIdUniqs, mDateList);
        } else {
            channelLoadCompleted();
        }
        DTVTLogger.end();
    }

    /**
     * チャンネルロード完了.
     */
    private void channelLoadCompleted() {
        DtvContentsChannelFragment channelFragment = getChannelFragment();
        if (channelFragment != null) {
            channelFragment.loadComplete();
            showChannelProgressBar(false);
        }
    }

    /**
     * クリップボタンの更新.
     * @param targetId 更新対象
     */
    private void checkClipStatus(final int targetId) {
        DTVTLogger.start();
        ClipKeyListDataManager manager = new ClipKeyListDataManager(ContentDetailActivity.this);
        List<Map<String, String>> mapList = manager.selectClipAllList();
        switch (targetId) {
            case ContentDetailUtils.CLIP_BUTTON_ALL_UPDATE:
                checkDetailClipStatus(mapList);
                if (tabType == ContentDetailUtils.TabType.TV_CH || tabType == ContentDetailUtils.TabType.TV_CH_BROADCAST) {
                    checkChannelClipStatus(mapList);
                }
                break;
            case ContentDetailUtils.CLIP_BUTTON_CHANNEL_UPDATE:
                if (tabType == ContentDetailUtils.TabType.TV_CH || tabType == ContentDetailUtils.TabType.TV_CH_BROADCAST) {
                    checkChannelClipStatus(mapList);
                }
                break;
            default:
                break;
        }
        DTVTLogger.end();
    }

    /**
     * 詳細情報のクリップボタン更新.
     * @param mapList クリップリスト(全件)
     */
    private void checkDetailClipStatus(final List<Map<String, String>> mapList) {
        DTVTLogger.start();
        DtvContentsDetailFragment dtvContentsDetailFragment = getDetailFragment();
        OtherContentsDetailData detailData = dtvContentsDetailFragment.getOtherContentsDetailData();
        if (detailData != null) {
            detailData.setClipStatus( ClipUtils.setClipStatusVodMetaData(mDetailFullData, mapList));
            dtvContentsDetailFragment.setOtherContentsDetailData(detailData);
            dtvContentsDetailFragment.noticeRefresh();
        }
        DTVTLogger.end();
    }


    /**
     * チャンネル情報のクリップボタン更新.
     * @param mapList クリップリスト(全件)
     */
    private void checkChannelClipStatus(final List<Map<String, String>> mapList) {
        DTVTLogger.start();
        DtvContentsChannelFragment dtvContentsChannelFragment = getChannelFragment();
        if (dtvContentsChannelFragment != null) {
            List<ContentsData> contentsDataList = dtvContentsChannelFragment.getContentsData();
            ContentsData contentsData;
            if (contentsDataList != null) {
                for (int i = 0; i < contentsDataList.size(); i++) {
                    contentsData = contentsDataList.get(i);
                    contentsDataList.get(i).setClipStatus(ClipUtils.setClipStatusContentsData(contentsData, mapList));
                }
                dtvContentsChannelFragment.setContentsData(contentsDataList);
                dtvContentsChannelFragment.setNotifyDataChanged();
            }
        }
        DTVTLogger.end();
    }

    /**
     * エラートーストを表示する.
     * @param errorType エラータイプ
     */
    private void showErrorToast(final ContentDetailUtils.ErrorType errorType) {
        DTVTLogger.start();
        showProgressBar(false);
        ErrorState errorState = null;
        String errorMessage = null;
        switch (errorType) {
            case rentalChannelListGet:
                errorState = mContentsDetailDataProvider.getError(ContentsDetailDataProvider.ErrorType.rentalChList);
                break;
            case rentalVoidListGet:
                errorState = mContentsDetailDataProvider.getError(ContentsDetailDataProvider.ErrorType.rentalVodList);
                break;
            case channelListGet:
                errorState = mScaledDownProgramListDataProvider.getChannelError();
                break;
            case roleListGet:
                errorState = mContentsDetailDataProvider.getError(ContentsDetailDataProvider.ErrorType.roleList);
                break;
            case unableToUseRemoteController:
                errorMessage = getString(R.string.contents_detail_episode_dialog_start_stb_dtv_toast);
                break;
            case stbViewingNg:
                errorMessage = getString(R.string.remote_controller_stb_viewing_ng_toast);
                break;
            case contentDetailGet:
            case recommendDetailGet:
            default:
                break;
        }
        errorMessage = errorState != null ? errorState.getErrorMessage() : errorMessage;
        showGetDataFailedToast(errorMessage);
        DTVTLogger.end();
    }

    /**
     * コンテンツ詳細データ取得.
     * @param channelInfo チャンネル情報
     */
    private void getChannelDetailData(final ChannelInfo channelInfo) {
        DTVTLogger.start();
        DtvContentsChannelFragment channelFragment = getChannelFragment();
        if (channelFragment != null) {
            channelFragment.setLoadInit();
            if (channelInfo != null) {
                channelFragment.setChannelDataChanged(channelInfo, getApplicationContext());
                getChannelDetailByPageNo();
            }
        }
        DTVTLogger.end();
    }

    /**
     * コンテンツの視聴可否判定を行う.
     * @param viewIngType 視聴可否種別
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    private void checkWatchContents(final ContentUtils.ViewIngType viewIngType) {
        switch (viewIngType) {
            case ENABLE_WATCH_LIMIT_THIRTY:
            case ENABLE_WATCH_LIMIT_THIRTY_001:
                //期限まで30日以内表示内容設定
                if (mEndDate == 0L) {
                    mEndDate = mDetailFullData.getPublish_end_date();
                    displayLimitDate();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 短い方の視聴可能期限を表示する.
     */
    private void displayLimitDate() {
        DTVTLogger.start();
        DtvContentsDetailFragment detailFragment = getDetailFragment();
        if (mEndDate != 0L && mEndDate < mVodEndDate && (mVodEndDateText == null || mVodEndDateText.isEmpty())) {
            String date = DateUtils.formatEpochToDateString(mEndDate);
            String untilDate = StringUtils.getConnectStrings(date, getString(R.string.common_until));
            DTVTLogger.debug("display limit date:---" + untilDate);
            OtherContentsDetailData detailData = detailFragment.getOtherContentsDetailData();
            if (detailData != null) {
                detailData.setChannelDate(untilDate);
                detailFragment.setOtherContentsDetailData(detailData);
            }
        } else {
            DTVTLogger.debug("display limit date:---" + mVodEndDateText);
            OtherContentsDetailData detailData = detailFragment.getOtherContentsDetailData();
            if (detailData != null) {
                detailData.setChannelDate(mVodEndDateText);
                detailFragment.setOtherContentsDetailData(detailData);
            }
        }
        DTVTLogger.end();
    }

    /**
     * コンテンツ種別ごとに視聴可否リクエストを投げる.
     * @param viewIngType 視聴可否種別種別
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    private void getViewingTypeRequest(final ContentUtils.ViewIngType viewIngType) {
        DTVTLogger.start();
        //未ペアリング時は視聴可否判定をしない
//        if (!StbConnectionManager.shared().getConnectionStatus().equals(StbConnectionManager.ConnectionStatus.NONE_PAIRING)) {
            //DBに保存されているUserInfoから契約情報を確認する
            String contractInfo = UserInfoUtils.getUserContractInfo(SharedPreferencesUtils.getSharedPreferencesUserInfo(ContentDetailActivity.this));
            DTVTLogger.debug("contractInfo: " + contractInfo);
            switch (viewIngType) {
                case PREMIUM_CHECK_START:
                    DTVTLogger.debug("premium channel check start");
                    mContentsDetailDataProvider.getChListData();
                    break;
                case SUBSCRIPTION_CHECK_START:
                    DTVTLogger.debug("subscription vod channel check start");
                    mContentsDetailDataProvider.getVodListData();
                    break;
                default:
                    responseResultCheck(viewIngType, mContentsType);
                    break;
            }
//        } else {
//            //未ペアリング時はログイン導線を出すため、すべて視聴可とする
//            mViewIngType = ContentUtils.ViewIngType.ENABLE_WATCH;
//            responseResultCheck(viewIngType, mContentsType);
//        }
        DTVTLogger.debug("get viewing type = " + viewIngType);
        DTVTLogger.end();
    }

    /**
     * データ取得がすべて終了していたらIndicatorを非表示にする(対象はContentsDetailDataProviderのみ).
     * @param viewIngType 視聴可否判定結果
     * @param contentsType コンテンツ種別
     */
    private void responseResultCheck(final ContentUtils.ViewIngType viewIngType, final ContentUtils.ContentsType contentsType) {
        if (mContentsDetailDataProvider == null
                || (!mContentsDetailDataProvider.isInContentsDetailRequest()
                && !mContentsDetailDataProvider.isInRentalChListRequest()
                && !mContentsDetailDataProvider.isInRentalVodListRequest()
                && !mContentsDetailDataProvider.isInRoleListRequest())) {
            //ログアウト状態ならそのまま表示する
            UserState userState = UserInfoUtils.getUserState(ContentDetailActivity.this);
//            StbConnectionManager.ConnectionStatus connectionStatus = StbConnectionManager.shared().getConnectionStatus();
//            String contractStatus = UserInfoUtils.getUserContractInfo(SharedPreferencesUtils.getSharedPreferencesUserInfo(this));
//            ContentUtils.ContentsDetailUserType detailUserType = ContentUtils.getContentDetailUserType(userState, connectionStatus, contractStatus);
//            if (contentsType != null) {
//                shapeViewType(detailUserType, contentsType, viewIngType);
//            }
            displayThumbnail(contentsType);
            getDetailFragment().changeVisibilityRecordingReservationIcon(viewIngType, contentsType);
            getDetailFragment().noticeRefresh();
            showProgressBar(false);
        }
    }

    /**
     * コンテンツ種別ごとのサムネイル部分の表示.
     * @param contentsType コンテンツ種別
     */
    private void displayThumbnail(final ContentUtils.ContentsType contentsType) {
        switch (contentsType) {
            case HIKARI_TV_OTT:
                checkOttPlayer();
                break;
            default:
                checkOttPlayer();
                break;
        }
    }

    /**
     * 放送番組フランス取得.
     * @return currentFragment
     */
    private DtvContentsBroadcastFragment getBroadcastFragment() {
        if (tabType != ContentDetailUtils.TabType.TV_CH_BROADCAST) {
            return null;
        }
        Fragment currentFragment = getFragmentFactory().createFragment(2, tabType);
        mDtvContentsBroadcastFragment = (DtvContentsBroadcastFragment) currentFragment;
        return mDtvContentsBroadcastFragment;
    }

    /**
     * 再生エリアを設定する.
     * @param view ビュー
     */
    private void setPlayerLayoutParams(final View view) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = getWidthDensity() / ContentDetailUtils.SCREEN_RATIO_WIDTH_16 * ContentDetailUtils.SCREEN_RATIO_HEIGHT_9;
        layoutParams.width = getWidthDensity();
        view.setLayoutParams(layoutParams);
    }

    /**
     * スクリーンのサイズを設定.
     */
    private void setScreenSize() {
        boolean isLandscape = getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initDisplayMetrics();
        mWidth = getWidthDensity();
        initDisplayMetrics();
        mHeight = getHeightDensity();
        mScreenNavHeight = getScreenHeight();
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * スクリーンのHeightを取得.
     * @return Height
     */
    private int getScreenHeight() {
        initDisplayMetrics();
        return getHeightDensity() + getNavigationBarHeight();
    }

    /**
     * ナビゲーションバーの高さを取得.
     * @return 高さ.
     */
    private int getNavigationBarHeight() {
        if (!isNavigationBarShow()) {
            return 0;
        }
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * ナビゲーションバーが表示されているか.
     * @return true:表示されている false:表示されていない
     */
    private boolean isNavigationBarShow() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y;
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                if (mDtvContentsOttPlayerFragment != null) {
                    mDtvContentsOttPlayerFragment.tapBackKey();
                }
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 再生中のくるくる処理.
     * @param isShow true 表示　false 非表示
     */
    private void showPlayIcon(final boolean isShow) {
        if (mPlayIcon == null) {
            mPlayIcon = new ImageView(getApplicationContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.contents_detail_player_media_controller_size),
                    (int) getResources().getDimension(R.dimen.contents_detail_player_media_controller_size)
            );
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mPlayIcon.setLayoutParams(layoutParams);
            mPlayIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mPlayIcon.setImageResource(R.mipmap.mediacontrol_icon_tap_play_arrow2);
        }
        if (isShow) {
            mThumbnailRelativeLayout.removeView(mPlayIcon);
            mThumbnailRelativeLayout.addView(mPlayIcon);
            mPlayIcon.setVisibility(View.VISIBLE);
        } else {
            mThumbnailRelativeLayout.removeView(mPlayIcon);
            mPlayIcon.setVisibility(View.GONE);
        }
    }
}
