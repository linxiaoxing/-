package com.example.detaildemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.MenuDisplay;
import com.example.detaildemo.common.UserState;
import com.example.detaildemo.data.bean.ClipRequestData;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.manager.RebuildDatabaseTableManager;
import com.example.detaildemo.data.webapiclient.DaccountGetOtt;
import com.example.detaildemo.data.webapiclient.OttGetAuthSwitch;
import com.example.detaildemo.data.webapiclient.accout.DaccountControl;
import com.example.detaildemo.data.webapiclient.clip.ClipDeleteWebClient;
import com.example.detaildemo.data.webapiclient.clip.ClipRegistWebClient;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.SharedPreferencesUtils;
import com.example.detaildemo.utils.UserInfoUtils;
import com.example.detaildemo.view.CustomDialog;
import com.example.detaildemo.view.CustomDrawerLayout;
import com.example.detaildemo.view.DlnaDmsItem;

import java.util.LinkedList;

/**
 * クラス機能：
 * プロジェクトにて、すべての「Activity」のベースクラスである
 * 「Activity」全体にとって、共通の機能があれば、追加すること.
 */
@SuppressLint("Registered")
public class BaseActivity extends FragmentActivity implements View.OnClickListener,
        DaccountControl.DaccountControlCallBack,
        CustomDialog.DismissCallback,
        CustomDialog.ApiOKCallback,
        CustomDialog.ApiCancelCallback,
        CustomDialog.ApiListItemClickCallback,
        ClipRegistWebClient.ClipRegistJsonParserCallback,
        ClipDeleteWebClient.ClipDeleteJsonParserCallback {

    /** ヘッダーBaseレイアウト. */
    private LinearLayout mBaseLinearLayout = null;
    /**　ステータスレイアウト. */
    private LinearLayout mStatusLinearLayout = null;
    /**　タイトルレイアウト. */
    private RelativeLayout mHeaderTitleLayout = null;
    /** ヘッダーレイアウト. */
    private RelativeLayout mHeaderLayout = null;
    /** ヘッダータイトル. */
    protected TextView mTitleTextView = null;
    /** ヘッダーロゴ用ImageView. */
    private ImageView mTitleImageView = null;
    /** 番組表タイトル横矢印. */
    protected ImageView mTitleArrowImage = null;
    /** ヘッダーの戻るボタン. */
    private ImageView mHeaderBackIcon = null;
    /** ペアリングアイコン. */
    private ImageView mStbStatusIcon = null;
    /** メニューアイコン. */
    private ImageView mMenuImageViewForBase = null;
//    /** リモコンレイアウト. */
//    private RemoteControllerView mRemoteControllerView = null;
    /** Activityのコンテキスト. */
    private Context mContext = null;
    /** Activityのコンテキスト. */
    private Activity mActivity = null;
//    /** 中継アプリ連携クライアント. */
//    protected RemoteControlRelayClient mRemoteControlRelayClient = null;
    /** ダブルクリック防止用. */
    private long mLastClickTime = 0;
    /** クリップボタン. */
    private ImageView mClipButton = null;
    /** クリップリクエスト用データ. */
    private ClipRequestData mClipRequestData = null;
    /**
     * クリップ登録／解除実行中かを取得.
     * @return クリップ登録／解除実行中状態
     */
    public boolean isClipRunTime() {
        return mClipRunTime;
    }
    /** クリップ実行中フラグ.*/
    private boolean mClipRunTime = false;
    /** DialogQue. **/
    private final LinkedList<CustomDialog> mLinkedList = new LinkedList<>();
    /** タイムアウト時間. */
    protected static final int LOAD_PAGE_DELAY_TIME = 1000;
    /** ページング単位.*/
    protected static final int NUM_PER_PAGE = 50;
    /** webViewの読み込み完了値. */
    protected final static int PROGRESS_FINISH = 100;
    /** ダブルクリック抑止用 DELAY. */
    private static final int MIN_CLICK_DELAY_TIME = 500;
    /** スプラッシュ画面用のファイル設定ファイル用ダイアログ表示識別文字列. */
    protected final static String SHOW_SETTING_FILE_DIALOG = "SHOW_SETTING_FILE_DIALOG";
    /** スプラッシュ画面用のファイル設定ファイル用ダイアログ表示内容表示識別文字列. */
    protected final static String SHOW_SETTING_FILE_DIALOG_DATA
            = "SHOW_SETTING_FILE_DIALOG_DATA";
    /** STB選択画面でログアウトが発生してランチャーを再起動する際のパラメータ. */
    protected final static String STB_SELECT_ACTIVITY_RESTART
            = "STB_SELECT_ACTIVITY_RESTART";
    /** dアカウント設定アプリ登録処理. */
    private DaccountControl mDAccountControl = null;
    /** 初回dアカウント取得失敗時のダイアログを呼び出すハンドラー. */
    private Handler mFirstDaccountErrorHandler = null;
    /** 詳細画面起動元Classを保存. */
    private static String sSourceScreenClass = "";
    /**起動時チェックフラグ.*/
    private static boolean sIsLaunchChecked = false;
    /** ヘッダーに表示されているアイコンが×ボタンアイコンであることを判別するタグ(close). */
    private static final String HEADER_ICON_CLOSE = "close";
    /** requestPermissions()表示によるonPauseを判断するためのフラグ. */
    private boolean mShowPermissionDialogFlag = false;
    /** 表示中ダイアログ. */
    private CustomDialog mShowDialog = null;
//    /** 設定ファイル処理クラス. */
//    protected ProcessSettingFile mCheckSetting = null;
    /** 国内通信 MCC (440 Japan). */
    private static final int DOMESTIC_COMMUNICATION_MCC_1 = 440;
    /** 国内通信 MCC (441 Japan). */
    private static final int DOMESTIC_COMMUNICATION_MCC_2 = 441;
    /** dアカウント関連処理の必要有無判定. */
    private boolean mNecessaryDAccountRegistService = true;
    /** ワンタイムトークン取得クラス（dアカウントチェック用）. */
    private DaccountGetOtt mGetOtt = null;
    /** クリップ状態. */
    public static final String CLIP_ACTIVE_STATUS = "active";
    /** 未クリップ状態. */
    public static final String CLIP_OPACITY_STATUS = "opacity";
    /** クリップ状態. */
    public static final String CLIP_SCHEDULE_END_ACTIVE_STATUS = "schedule_end_active";
    /** 未クリップ状態. */
    public static final String CLIP_SCHEDULE_END_OPACITY_STATUS = "schedule_end_opacity";
    /** アダプタ内でのリスト識別用定数. */
    private final static int HOME_CONTENTS_DISTINCTION_ADAPTER = 10;
    /**
     * リモコン表示時の鍵交換処理フラグ.
     * ※ペアリングアイコンからのリモコン表示時のみ鍵交換を行う
     */
    private static boolean mKeyExchangeFlag = false;
    /** 終了後にタスク一覧からどこテレアプリを削除する場合に指定するフラグ. */
    public static final  String FORCE_FINISH = "FORCE_FINISH";
    /** DisplayMetrics.*/
    private DisplayMetrics mDisplayMetrics = null;
    /** Toast（連続して出さない為に保持）. */
    public Toast mToast = null;
//    /** 番組表情報取得DataProvider. */
//    private ScaledDownProgramListDataProvider mScaledDownProgramListDataProvider = null;
    /** 遷移先のクラス名. */
    private String mClassName = null;
    /** CustomDrawerLayout.*/
    private CustomDrawerLayout mDrawerLayout;
    /**メニューリストビュー.*/
    private ListView mGlobalMenuListView = null;
//    /** コンテンツ種別1のコンテンツ種別名のひかりTVタイプ.*/
//    private ContentUtils.HikariType mHikariType = ContentUtils.HikariType.H4D;
    /** 詳細画面STB連携タイトル名.*/
    private String mTitle = "";
    /** コンテンツ種別2のコンテンツ種別名.*/
    private ContentUtils.ContentsType mContentsType = ContentUtils.ContentsType.VOD;
    /** bg→fg区別.*/
    protected boolean mIsFromBgFlg = false;

    /**
     * 機能：onCreate.
     * Sub classにて、super.onCreate(savedInstanceState)をコールする必要がある
     *
     * @param savedInstanceState
     *                                              savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        DTVTLogger.start( );

        // 認証付きアプリのデバッグ時に有効化すると、デバッグのアタッチが行われるまでここで停止する。デバッグ時に有効化して利用。
        //android.os.Debug.waitForDebugger();

        super.onCreate( savedInstanceState );
        super.setContentView(R.layout.activity_base);
        mContext = this;
        initView();
    }

    /**
     * タイトルビユー初期化.
     */
    private void initView() {
        DTVTLogger.start();
        mBaseLinearLayout = findViewById(R.id.base_ll);
        mStatusLinearLayout = findViewById(R.id.header_status_linear);
        mHeaderTitleLayout = findViewById(R.id.header_title_relative_layout);
        mHeaderLayout = findViewById(R.id.base_title);
        mTitleTextView = findViewById(R.id.header_layout_text);
        mTitleImageView = findViewById(R.id.header_layout_title_image);
        mTitleArrowImage = findViewById(R.id.tv_program_list_main_layout_calendar_arrow);
        mHeaderBackIcon = findViewById(R.id.header_layout_back);
        mStbStatusIcon = findViewById(R.id.header_stb_status_icon);
        mMenuImageViewForBase = findViewById(R.id.header_layout_menu);
        mDrawerLayout = findViewById(R.id.baseCustomDrawerLayout);
        mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mGlobalMenuListView = findViewById(R.id.menu_list);
        changeGlobalMenuNewIcon(false);
        DTVTLogger.end();
    }

    @Override
    public void onClick(View view) {
        if (view != null && view.equals(mMenuImageViewForBase)) {
            if (HEADER_ICON_CLOSE.equals(mMenuImageViewForBase.getTag())) {
//                if (this instanceof EpisodeAllReadActivity) {
//                    finish();
//                } else {
                    contentsDetailCloseKey(view);
//                }
            } else {
                //ダブルクリックを防ぐ
                if (isFastClick()) {
                    displayGlobalMenu();
                }
            }
        } else if (view != null && view.equals(mHeaderBackIcon)) {
            contentsDetailBackKey(null);
        }
    }

    @Override
    public void onOKCallback(final boolean isOK) {
        if (mShowDialog != null) {
            if (mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.OPTIONAL_VERSION_UP
                    || mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.FORCED_VERSION_UP) {
                //toGooglePlay( UrlConstants.WebUrl.DTVT_GOOGLEPLAY_DOWNLOAD_URL);
                return;
            } else if ((mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.SETTING_FILE_ERROR_DIALOG)) {
                stopAllActivity();
                return;
            }
            onVersionUpDialogShow(ContentUtils.ILLEGAL_VALUE, CustomDialog.DialogTapType.OK);
        }
    }

    @Override
    public void allDismissCallback() {
        clearDialogContentText();
    }

    @Override
    public void otherDismissCallback() {
        if (mShowDialog != null) {
            if (mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.FORCED_VERSION_UP
                    || mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.SETTING_FILE_ERROR_DIALOG) {
                stopAllActivity();
                return;
            }
            onVersionUpDialogShow(-1, CustomDialog.DialogTapType.BACK_KEY);
        }
    }


    @Override
    public void onCancelCallback() {
        onVersionUpDialogShow(ContentUtils.ILLEGAL_VALUE, CustomDialog.DialogTapType.CANCEL);
    }

    @Override
    public void onApiListItemClickCallback(final int which) {
        if (mShowDialog != null) {
            onVersionUpDialogShow(which, CustomDialog.DialogTapType.LIST);
        }
    }

    /**
     * 機能　GooglePlayのAPPページへ.
     *
     * (設定ファイル制御からも呼ぶので、publicに変更)
     * @param downLoadUrl 当アプリはGooglePlayのダウンロードURL
     */
    public void toGooglePlay(final String downLoadUrl) {
        Uri uri = Uri.parse(downLoadUrl);
        Intent installIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(installIntent);
    }

    /**
     * getLinkListSize.
     * @return LinkListSize
     */
    private int getLinkListSize() {
        return mLinkedList.size();
    }

    /**
     * onVersionUpDialogShow.
     * @param which which
     * @param dialogTapType dialogTapType
     */
    protected void onVersionUpDialogShow(final int which, final CustomDialog.DialogTapType dialogTapType) {
        final CustomDialog.ShowDialogType showDialogType = mShowDialog.getErrorDialogType();
        if (getLinkListSize() > 0) {
            pollDialog();
            if (mShowDialog != null) {
                setVersionUpDialogCallBack(which, mShowDialog, showDialogType, dialogTapType);
            }
        } else {
            startNextProcess();
            switch (showDialogType) {
                case D_ACCOUNT_CHANGED:
                    onRestartApplication();
                    break;
                case D_ACCOUNT_REGISTRATION_HELP:
//                    Intent intent = new Intent(getApplicationContext(), DaccountSettingHelpActivity.class);
//                    startActivity(intent);
                    break;
                case SECURE_PLAYER_ERROR:
                    contentsDetailCloseKey(null);
                    break;
                case CONTENT_DETAIL_GET_ERROR:
                case OTT_CHANNEL_PROGRAM_GET_ERROR:
                    finish();
                    break;
                case LAUNCH_STB_START_DIALOG:
                    //showPairingDialog(dialogTapType);
                    break;
                case REMOTE_CONTROL_START_PAIRING_DIALOG:
                    //showPairingDialogOnRemoteControl(dialogTapType);
                    break;
                case DTV_EPISODE_LIST_ITEM_DIALOG:
                    //startListDialogDismissTask(which);
                    break;
                case COMMON_DIALOG:
                default:
                    startDialogDismissTask(new Pair<>(dialogTapType, CustomDialog.DialogTapType.NOT_TAP));
                    break;
            }
        }
    }

    /**
     * Activity内に独自の処理があればオーバライドする.
     * @param errorDialogTapPair errorDialogTapPair
     * errorDialogTapPair.first 最初のダイアログタップイベント
     * errorDialogTapPair.second 最後のダイアログタップイベント
     */
    protected void startDialogDismissTask(final Pair<CustomDialog.DialogTapType, CustomDialog.DialogTapType> errorDialogTapPair) {
        //NOP
    }

    /**
     * コンテンツ詳細のクローズボタン.
     *
     * @param view クローズボタンのビュー
     */
    protected void contentsDetailCloseKey(final View view) {
        //コンテンツ詳細画面をクローズする処理
        try {
            Class sourceClass = Class.forName(getSourceScreenClass());
            Intent intent = new Intent(this, sourceClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            setSourceScreenClass("");
//            TimerNoticeManager.shared().stopAll();
        } catch (ClassNotFoundException e) {
            DTVTLogger.debug(e);
        }
    }

    /**
     * コンテンツ詳細画面起動元のクラス名を取得する.
     *
     * @return クラス名
     */
    private String getSourceScreenClass() {
        return sSourceScreenClass;
    }

    /**
     * 戻るボタン.
     *
     * @param view 戻るボタンのビュー
     */
    protected void contentsDetailBackKey(final View view) {
//        if (this instanceof SearchTopActivity
//                || this instanceof TvProgramListActivity
//                || this instanceof RecordedListActivity
//                || this instanceof RankingTopActivity
//                || this instanceof VideoTopActivity
//                || this instanceof RecordReservationListActivity
//                || this instanceof SettingActivity) {
//            startHomeActivity();
//        } else {
            finish();
//        }
    }

    /**
     * 機能：ヘッダー色を変更する.
     *
     * @param isColorRed true:ヘッダー色=赤 false:ヘッダー色=黒
     */
    protected void setHeaderColor(final Boolean isColorRed) {
        if (null != mHeaderLayout) {
            if (isColorRed) {
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.header_background_color_red));
            } else {
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.header_background_color_black));
            }
        }
    }

    /**
     * 機能：Global menuアイコンを有効.
     *
     * @param isOn true: 表示  false: 非表示
     */
    protected void enableGlobalMenuIcon(final boolean isOn) {
        if (null != mMenuImageViewForBase) {
            if (isOn) {
                mMenuImageViewForBase.setVisibility(View.VISIBLE);
                mMenuImageViewForBase.setOnClickListener(this);
            } else {
                mMenuImageViewForBase.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 機能
     * ディスプレイインスタンスを初期化.
     */
    protected void initDisplayMetrics() {
        mDisplayMetrics = null;
    }

    /**
     * 機能
     * ディスプレイ幅取得.
     *
     * @return ディスプレイ幅
     */
    protected int getWidthDensity() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 機能
     * ディスプレイ高さ取得.
     *
     * @return ディスプレイ高さ
     */
    protected int getHeightDensity() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * 機能
     * ディスプレイインスタンス取得.
     *
     * @return ディスプレイインスタンス
     */
    private DisplayMetrics getDisplayMetrics() {
        if (mDisplayMetrics == null) {
            mDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        return mDisplayMetrics;
    }

    /**
     * 機能
     * 密度取得.
     *
     * @return 密度
     */
    protected float getDensity() {

        return getDisplayMetrics().density;
    }

    /**
     * ログアウトダイアログ.
     *
     * 各アクティビティで使われるようになったので、publicに変更
     */
    public void showLogoutDialog() {
        //特別なコールバックは使用しないのでヌル
        showLogoutDialog(null);
    }

    /**
     * ログアウトダイアログ.
     *
     * @param dissmissCallBack キャンセル後の処理
     */
    public void showLogoutDialog(CustomDialog.DismissCallback dissmissCallBack) {

        DTVTLogger.debug("showLogoutDialog");
        final CustomDialog logoutDialog = new CustomDialog(BaseActivity.this, CustomDialog.DialogType.CONFIRM);
        logoutDialog.setContent(this.getResources().getString( R.string.logout_message));

        //バックボタンをキャンセルボタンとして扱うように指示
        logoutDialog.setBackKeyAsCancel(true);

        //ダイアログ以外の部分を押した時に反応するのは禁止
        logoutDialog.setOnTouchOutside(false);

        logoutDialog.setOkCallBack(new CustomDialog.ApiOKCallback() {
            @SuppressWarnings("OverlyLongMethod")
            @Override
            public void onOKCallback(final boolean isOK) {
                //ログアウトのダイアログは閉じられたので、認証画面を再表示できるようにする
                OttGetAuthSwitch.INSTANCE.setNowAuth(true);

                DaccountControl.cacheClear(BaseActivity.this);
                reStartApplication();
            }

        });

        logoutDialog.setApiCancelCallback(new CustomDialog.ApiCancelCallback() {
            @Override
            public void onCancelCallback() {
                //ログアウトのダイアログは閉じられたので、認証画面を再表示する
                OttGetAuthSwitch.INSTANCE.setNowAuth(true);
                chkDaccountRegist();
            }
        });

        //コールバック指定の有無で処理を分ける
        if (dissmissCallBack == null) {
            //コールバックの指定が無かったので、通常の処理を行う
            logoutDialog.setDialogDismissCallback(new CustomDialog.DismissCallback() {
                @Override
                public void allDismissCallback() {
                    //ログアウトのダイアログは閉じられたので、認証画面を再表示できるようにする
                    OttGetAuthSwitch.INSTANCE.setNowAuth(true);

                    clearDialogContentText();

                    //キャンセルボタンが押されたかどうかのチェック
                    if (!logoutDialog.isButtonTap()) {
                        logoutDialog.setButtonTap(true);
                        //ダイアログが閉じた理由が電源ボタンやホームボタンなので、帰る
                        return;
                    } else {
                        OttGetAuthSwitch.INSTANCE.setNowAuth(true);
                        mDAccountControl = new DaccountControl();
                        mDAccountControl.registService(getApplicationContext(), BaseActivity.this);
                    }

                    pollDialog();
                }

                @Override
                public void otherDismissCallback() {
                    DTVTLogger.debug("otherDismissCallback");
                }
            });
        } else {
            //ログアウトのダイアログは閉じられたので、認証画面を再表示できるようにする
            OttGetAuthSwitch.INSTANCE.setNowAuth(true);

            //指定があったので、コールバックを設定する
            logoutDialog.setDialogDismissCallback(dissmissCallBack);
        }

        offerDialog(logoutDialog);
    }

    /**
     * onRestartApplication.
     */
    private void onRestartApplication() {
        SharedPreferencesUtils.setSharedPreferencesRestartFlag(getApplicationContext(), false);

        // dアカウントが変更しましたため、新しいお知らせの未読状態に関するFlagをリセットする
        SharedPreferencesUtils.removeNewlyNoticeFlag(mContext);

        //OKが押されたので、ホーム画面の表示
        reStartApplication();
    }

    /**
     * Dアカウントコントロール処理のゲッター.
     *
     * @return Dアカウント処理のクラスのインスタンス
     */
    public DaccountControl getDAccountControl() {
        return mDAccountControl;
    }

    /**
     * 有効なdアカウントが登録されているかどうかを見る.
     */
    private void chkDaccountRegist() {
        //dアカウント制御クラスのインスタンスの取得
        final DaccountControl daccountControl = getDAccountControl();

        //制御クラスが取得できて、かつビジーではないかを調べる
        if (daccountControl != null && (!daccountControl.isDAccountBusy())) {
            //dアカウント取得クラスを未取得ならば取得する
            if (mGetOtt == null) {
                mGetOtt = new DaccountGetOtt();
            }

            //認証画面の表示状況のインスタンスの取得。認証画面の重複表示を防止用
            final OttGetAuthSwitch ottGetAuthSwitch = OttGetAuthSwitch.INSTANCE;

            //ワンタイムトークンの取得を行う事で、dアカウントのチェックとする。dアカウントが無効ならば認証画面に遷移する
            mGetOtt.execDaccountGetOTT(getApplicationContext(),
                    ottGetAuthSwitch.isNowAuth(), new DaccountGetOtt.DaccountGetOttCallBack() {
                        @Override
                        public void getOttCallBack(int result, String id, String oneTimePassword) {
                            DTVTLogger.debug("ott" + oneTimePassword);
                            //dアカウントの制御のワンタイムトークン処理を呼び出す。
                            daccountControl.setResult(result);
                            daccountControlCallBack(false);
                        }
                    });
        }
    }

    /**
     * 次のダイアログの判定の為に、今のダイアログの文言をクリアする.
     */
    protected void clearDialogContentText() {
        if (mShowDialog != null) {
            mShowDialog.clearContentText();
        }
    }

    /**
     * ダイアログをキューに追加.
     *
     * (設定ファイル処理から呼ぶためにパブリック化)
     * @param dialog キュー表示するダイアログ
     */
    protected void offerDialog(final CustomDialog dialog) {
        DTVTLogger.start();

        //現在表示しているダイアログと新たに蓄積されるダイアログの本文を比較する
        String contentText = dialog.getContent();
        if (!TextUtils.isEmpty(contentText) && mShowDialog != null
                && mShowDialog.getContent() != null && mShowDialog.getContent().equals(contentText)) {
            //現在表示しているダイアログと本文が同じなので、蓄積せずに帰る
            return;
        }

        //蓄積しているキューの中に同じ文言の物があるかどうかをチェック
        for (CustomDialog customDialog : mLinkedList) {
            if (customDialog.getContent().equals(contentText)) {
                //本文が同じ物が見つかったので、蓄積せずに帰る
                return;
            }
        }

        mLinkedList.offer(dialog);
        pollDialog();
        DTVTLogger.end();
    }

    /**
     * キューにあるダイアログを順に表示.
     */
    protected void pollDialog() {
        DTVTLogger.start();
        DTVTLogger.debug("mLinkedList.size()=" + mLinkedList.size());
        if ((mShowDialog == null || !mShowDialog.isShowing()) && mLinkedList.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShowDialog = mLinkedList.poll();
                    if (mShowDialog != null) {
                        mShowDialog.showDialog();
                    }
                }
            });
        } else {
            if (mShowDialog != null) {
                startNextProcess();
                DTVTLogger.debug("show=" + mShowDialog.isShowing()
                        + ":mLinkedList.size()=" + mLinkedList.size());
            }
        }
        DTVTLogger.end();
    }

    /**
     * 次に行う処理があれば、オーバーライドしてください.
     */
    protected void startNextProcess() {
        DTVTLogger.start();
        DTVTLogger.end();
    }

    /**
     * アプリを終了させる.
     */
    public void stopAllActivity() {
        DTVTLogger.start();

        //Androidバージョンで終了処理を使い分ける
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //finishAndRemoveTaskAPIは、タスク一覧から消すが、現在のアクティビティしか終了しない。そこでdアカウント更新時と同じように、
            //タスク情報を消すオプションを付けてホーム画面に飛ぶ。これで画面がホーム一つに限定されるので、その後にfinishAndRemoveTaskを呼ぶ処理を行う
            forceFinishAndTaskRemove(getApplicationContext());
        } else {
            //4.4ではfinishAndRemoveTaskが使えないので、この場でアプリを終了させる
            finishAffinity();
        }

        DTVTLogger.end();
    }

    /**
     * タスク一覧にアプリ情報を残さずに終了する為の準備.
     *
     * @param context コンテキスト
     */
    public static void forceFinishAndTaskRemove(Context context) {
        DTVTLogger.start();

        //タスク情報を削除しつつホーム画面に飛ぶ設定・これで他のアクティビティは消えてホーム画面だけになる
        Intent intent = new Intent();
        intent.setClass(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        //ホーム画面起動後にfinishAndRemoveTaskを行わせるフラグのセット
        intent.putExtra(FORCE_FINISH, true);

        context.startActivity(intent);

        DTVTLogger.end();
    }

    /**
     * dアカウントの削除や変更後に、自アプリがフォアグラウンドならば、アプリの再起動を行う.
     */
    protected void reStartApplication() {
        DTVTLogger.start();
        //ホーム画面に戻る
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        DTVTLogger.end();
    }

    @Override
    public void daccountControlCallBack(boolean result) {

    }

    /**
     * dアカウント設定アプリ未インストールエラーダイアログ.
     */
    public void showDAccountApliNotFoundDialog() {
        DTVTLogger.debug("showDAccountApliNotFoundDialog");
        final CustomDialog notFoundDialog = new CustomDialog(
                BaseActivity.this, CustomDialog.DialogType.ERROR);
        notFoundDialog.setContent(this.getResources().getString(
                R.string.d_account_deleted_message));

        //バックボタンをキャンセルボタンとして扱うように指示
        notFoundDialog.setBackKeyAsCancel(true);

        //ダイアログ以外の部分を押した時に反応するのは禁止
        notFoundDialog.setOnTouchOutside(false);

        //メッセージを出さないキャッシュクリア
        DaccountControl.cacheClear(BaseActivity.this, null, false);

        //OKボタンが押されたら、ログアウト処理を行い、終了する
        notFoundDialog.setOkCallBack(new CustomDialog.ApiOKCallback() {
            @Override
            public void onOKCallback(boolean isOK) {
                DTVTLogger.debug("logout & restart");
                SharedPreferencesUtils.setSharedPreferencesRestartFlag(getApplicationContext(),
                        false);
                reStartApplication();
            }
        });

        offerDialog(notFoundDialog);
    }

    /**
     * ステータスバーの色変更.
     *
     * @param resId colorリソースID
     */
    protected void setStatusBarColor(final int resId) {
        //ステータスバーの色変更方法をLOLLIPOPを境界に変更する
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor(getString(resId)));
        } else {
            setTheme(R.style.ContentsDetailTheme);
        }
    }

    /**
     * 詳細画面起動元のクラス名を保存するstaticクラス.
     *
     * @param className コンテンツ詳細画面起動元のクラス名
     */
    private synchronized static void setSourceScreen(final String className) {
        BaseActivity.sSourceScreenClass = className;
    }

    /**
     * コンテンツ詳細画面起動元のクラス名を保持する.
     *
     * @param className クラス名
     */
    protected void setSourceScreenClass(final String className) {
        setSourceScreen(className);
    }

    /**
     * 機能：ヘッダーの戻るアイコン"<"有効.
     *
     * @param isOn true: 表示  false: 非表示
     */
    protected void enableHeaderBackIcon(final boolean isOn) {
        if (null != mHeaderBackIcon) {
            if (isOn) {
                mHeaderBackIcon.setVisibility( View.VISIBLE);
                mHeaderBackIcon.setOnClickListener(this);
            } else {
                mHeaderBackIcon.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 新着お知らせのメニューアイコンを切り替え
     * @param isNewly   true: 新着お知らせあり。false: 新着お知らせなし
     */
    protected void changeGlobalMenuNewIcon(final boolean isNewly) {
        if (null != mMenuImageViewForBase && !HEADER_ICON_CLOSE.equals(mMenuImageViewForBase.getTag())) {
            if (isNewly || SharedPreferencesUtils.getUnreadNewlyNotice(mContext)) {
                mMenuImageViewForBase.setImageResource(R.drawable.header_menu_news_selector);
            } else {
                mMenuImageViewForBase.setImageResource(R.drawable.header_menu_selector);
            }
        }
    }

    /**
     * 機能：×ボタンアイコンを表示する。
     */
    protected void changeGlobalMenuIconToCloseIcon() {
        if (null != mMenuImageViewForBase) {
            mMenuImageViewForBase.setImageResource(R.mipmap.header_material_icon_close);
            mMenuImageViewForBase.setTag(HEADER_ICON_CLOSE);
        }
    }

    /**
     * 機能
     * ダブルクリック防止.
     *
     * @return クリック状態
     */
    protected boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - mLastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        mLastClickTime = curClickTime;
        return flag;
    }


    /**
     * グローバルメニュー表示.
     */
    protected void displayGlobalMenu() {
        UserState param = UserInfoUtils.getUserState(this);
        DTVTLogger.debug("displayGlobalMenu userState=" + param);
        setUserState(param);
        displayMenu();
    }

    /**
     * ユーザ状態毎の表示.
     */
    private void displayMenu() {
        MenuDisplay.getInstance().display();
    }

    /**
     * ユーザ状態設定.
     *
     * @param userState ユーザ状態(ペアリング・契約・ログイン)
     */
    private void setUserState(final UserState userState) {
        synchronized (this) {
            MenuDisplay menu = MenuDisplay.getInstance();
            menu.setActivityAndListener(this, this, mDrawerLayout, mGlobalMenuListView);
            menu.changeUserState(userState);
        }
    }

    /**
     * BaseLayoutのBackgroundColor設定
     * @param resourceId リソースID
     */
    protected void setBaseLayoutBackgroundColor(@ColorRes final int resourceId) {
        mBaseLinearLayout.setBackgroundResource(resourceId);
    }

    /**
     * プログレスの表示設定.
     *
     * @param visible 表示値
     */
    public void setRemoteProgressVisible(final int visible) {
        //ヌルチェックを追加
        View view = findViewById(R.id.base_progress_rl);
        if (view != null) {
            view.setVisibility(visible);
            // コンテンツ詳細からのサービスアプリ連携時のクルクル表示中にクルクルのタップを有効にしてリモコンのボタンタップを禁止する
            view.setClickable(true);
        }
    }

    /**
     * クリップ登録/削除処理追加.
     *
     * @param data       クリップ処理用データ
     * @param clipButton クリップボタン
     */
    public void sendClipRequest(final ClipRequestData data, final ImageView clipButton) {

        if (data != null && clipButton != null && !isClipRunTime()) {
            DTVTLogger.debug(String.format("clip request: crid [%s] clip status [%s]", data.getCrid(), data.isClipStatus()));

            //クリップ多重実行に対応していないため実行中フラグで管理
            mClipRunTime = true;

            mClipButton = clipButton;
            mClipRequestData = data;

            boolean isParamCheck;

            //DREM-1882 期限切れコンテンツのクリップ対応により、期限切れコンテンツをクリップ登録しようとした場合にはエラーダイアログを表示する
            if (!data.isClipStatus() && data.isIsAfterLimitContents()) {
                String message = getString(R.string.str_clip_execution_after_limit_contents);
                showCommonControlErrorDialog(message, null, null, null, null);
                mClipRunTime = false;
                return;
            }

            //クリップ状態によりクリップ登録/削除実行
            if (data.isClipStatus()) {
                ClipDeleteWebClient deleteWebClient = new ClipDeleteWebClient(getApplicationContext());
                isParamCheck = deleteWebClient.getClipDeleteApi(data.getType(), data.getCrid(),
                        data.getTitleId(), this);
            } else {
                ClipRegistWebClient registWebClient = new ClipRegistWebClient(getApplicationContext());
                isParamCheck = registWebClient.getClipRegistApi(
                        data.getType(), data.getCrid(),
                        data.getServiceId(), data.getEventId(), data.getTitleId(), data.getTitle(),
                        data.getRValue(), data.getLinearStartDate(), data.getLinearEndDate(),
                        data.getIsNotify(), this);
            }

            if (!isParamCheck) {
                //パラメータチェックではじかれたら失敗表示とクリップ登録／解除実行中状態を解除
                showClipToast(R.string.clip_regist_error_message);
            } else {
                //クリップ登録／解除リクエストの結果応答時にコンテンツリストに反映するにクリップ登録／削除ステータスを設定.
                data.setClipStatus(!data.isClipStatus());
            }
        }
    }

    /**
     * クリップ処理でのトースト表示.
     *
     * @param msgId 各ステータスのメッセージID
     */
    public void showClipToast(final int msgId) {
        //指定された文字リソースを表示する
        if (mToast != null) {
            mToast.cancel();
        }
        if (!this.isFinishing()) {
            mToast = Toast.makeText(this, msgId, Toast.LENGTH_SHORT);
            mToast.show();
        } else {
            mToast = null;
        }

        //クリップ処理終了メッセージ後にフラグを実行中から終了に変更
        mClipRunTime = false;
    }

    /**
     * 機能 エラー制御ダイアログ.
     *
     * @param errorMessage      errorMessage
     * @param showDialogType    showDialogType
     * @param apiOKCallback     apiOKCallback
     * @param apiCancelCallback apiCancelCallback
     * @param dismissCallback   dismissCallback　バックキーによるダイアログクローズ
     */
    protected synchronized void showCommonControlErrorDialog(final String errorMessage, final CustomDialog.ShowDialogType showDialogType,
                                                             final CustomDialog.ApiOKCallback apiOKCallback,
                                                             final CustomDialog.ApiCancelCallback apiCancelCallback,
                                                             final CustomDialog.DismissCallback dismissCallback) {
        DTVTLogger.debug("showCommonControlErrorDialog:" + errorMessage);
        if (!isNeedToInsertQue(showDialogType)) {
            return;
        }
        CustomDialog errorDialog = setDialogParameter(showDialogType, apiOKCallback, apiCancelCallback, dismissCallback, null);
        errorDialog.setContent(errorMessage);
        //ダイアログをキューにためる処理
        offerDialog(errorDialog);
    }

    /**
     * 機能 ダイアログ制御チェック.
     * @param showDialogType ダイアログタイプ
     */
    private boolean isNeedToInsertQue(final CustomDialog.ShowDialogType showDialogType) {
        if (mShowDialog != null && mShowDialog.isShowing()) {
            //設定ファイルエラー/バージョンアップダイアログ以外の場合は蓄積せずに帰る
            if (showDialogType != CustomDialog.ShowDialogType.FORCED_VERSION_UP
                    && showDialogType != CustomDialog.ShowDialogType.OPTIONAL_VERSION_UP
                    && showDialogType != CustomDialog.ShowDialogType.DOWNLOAD_NOT_COMPLETED_DIALOG) {
                return false;
            }
            //VersionUpダイアログの場合、もし表示中であれば蓄積しない
            if (mShowDialog.getErrorDialogType() == showDialogType) {
                return false;
            }
        }
        return true;
    }

    /**
     * 機能 エラー制御ダイアログ.
     * @param showDialogType showDialogType
     * @param apiOKCallback apiOKCallback
     * @param apiCancelCallback apiCancelCallback
     * @param dismissCallback dismissCallback
     * @param apiListItemClickCallback apiListItemClickCallback
     */
    protected CustomDialog setDialogParameter(final CustomDialog.ShowDialogType showDialogType, final CustomDialog.ApiOKCallback apiOKCallback,
                                              final CustomDialog.ApiCancelCallback apiCancelCallback, final CustomDialog.DismissCallback dismissCallback,
                                              final CustomDialog.ApiListItemClickCallback apiListItemClickCallback) {
        CustomDialog customDialog;
        if (showDialogType != null) {
            switch (showDialogType) {
                case DTV_EPISODE_LIST_ITEM_DIALOG:
                    customDialog = new CustomDialog(BaseActivity.this, CustomDialog.DialogType.LIST);
                    break;
                case OPTIONAL_VERSION_UP:
                case UN_DOWNLOAD_CONFIRM_DIALOG:
                case LAUNCH_STB_START_DIALOG:
                case REMOTE_CONTROL_START_PAIRING_DIALOG:
                    customDialog = new CustomDialog(BaseActivity.this, CustomDialog.DialogType.CONFIRM);
                    break;
                default:
                    customDialog = new CustomDialog(BaseActivity.this, CustomDialog.DialogType.ERROR);
                    break;
            }
        } else {
            customDialog = new CustomDialog(BaseActivity.this, CustomDialog.DialogType.ERROR);
        }
        if (showDialogType == CustomDialog.ShowDialogType.LAUNCH_STB_START_DIALOG || showDialogType == CustomDialog.ShowDialogType.REMOTE_CONTROL_START_PAIRING_DIALOG) {
            customDialog.setConfirmText(R.string.contents_detail_pairing_button);
            customDialog.setCancelText(R.string.common_text_close);
        }
        //閉じたときに次のダイアログを呼ぶ処理
        customDialog.setOkCallBack((apiOKCallback == null) ? this : apiOKCallback);
        customDialog.setApiCancelCallback((apiCancelCallback == null) ? this : apiCancelCallback);
        customDialog.setDialogDismissCallback((dismissCallback == null) ? this : dismissCallback);
        customDialog.setApiListItemClickCallback((apiListItemClickCallback == null) ? this : apiListItemClickCallback);
        //ボタン以外タップ不可
        customDialog.setOnTouchOutside(false);
        customDialog.setErrorDialogType((showDialogType == null) ? CustomDialog.ShowDialogType.COMMON_DIALOG : showDialogType);
        return customDialog;
    }

    /**
     * versionUpダイアログ表示.
     * @param which which
     * @param showDialog showDialog
     * @param showDialogType showDialogType
     * @param dialogTapType 　最初のダイアログタップイベント
     */
    private void setVersionUpDialogCallBack(final int which, final CustomDialog showDialog,
                                            final CustomDialog.ShowDialogType showDialogType,
                                            final CustomDialog.DialogTapType dialogTapType) {
        showDialog.setOkCallBack(new CustomDialog.ApiOKCallback() {
            @Override
            public void onOKCallback(final boolean isOK) {
                if (mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.DOWNLOAD_NOT_COMPLETED_DIALOG) {
                    onVersionUpDialogShow(ContentUtils.ILLEGAL_VALUE, CustomDialog.DialogTapType.CANCEL);
                } else {
                    //toGooglePlay(UrlConstants.WebUrl.DTVT_GOOGLEPLAY_DOWNLOAD_URL);
                }
            }
        });
        showDialog.setApiCancelCallback(new CustomDialog.ApiCancelCallback() {
            @Override
            public void onCancelCallback() {
                switch (showDialogType) {
                    case D_ACCOUNT_CHANGED:
                        onRestartApplication();
                        break;
                    case D_ACCOUNT_REGISTRATION_HELP:
//                        Intent intent = new Intent(getApplicationContext(), DaccountSettingHelpActivity.class);
//                        startActivity(intent);
                        break;
                    case CONTENT_DETAIL_GET_ERROR:
                    case OTT_CHANNEL_PROGRAM_GET_ERROR:
                        finish();
                        break;
                    case SECURE_PLAYER_ERROR:
                        contentsDetailCloseKey(null);
                        break;
//                    case LAUNCH_STB_START_DIALOG:
//                        showPairingDialog(dialogTapType);
//                        break;
//                    case REMOTE_CONTROL_START_PAIRING_DIALOG:
//                        showPairingDialogOnRemoteControl(dialogTapType);
//                        break;
//                    case DTV_EPISODE_LIST_ITEM_DIALOG:
//                        startListDialogDismissTask(which);
//                        break;
                    case COMMON_DIALOG:
                    default:
                        startDialogDismissTask(new Pair<>(dialogTapType, CustomDialog.DialogTapType.CANCEL));
                        break;
                }
            }
        });
        showDialog.setDialogDismissCallback(new CustomDialog.DismissCallback() {
            @Override
            public void allDismissCallback() {
                clearDialogContentText();
                startNextProcess();
            }

            @Override
            public void otherDismissCallback() {
                if (mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.FORCED_VERSION_UP) {
                    stopAllActivity();
                    return;
                } else if (mShowDialog.getErrorDialogType() == CustomDialog.ShowDialogType.DOWNLOAD_NOT_COMPLETED_DIALOG) {
                    onVersionUpDialogShow(ContentUtils.ILLEGAL_VALUE, CustomDialog.DialogTapType.CANCEL);
                }
                switch (showDialogType) {
                    case D_ACCOUNT_CHANGED:
                        onRestartApplication();
                        break;
                    case D_ACCOUNT_REGISTRATION_HELP:
//                        Intent intent = new Intent(getApplicationContext(), DaccountSettingHelpActivity.class);
//                        startActivity(intent);
                        break;
                    case CONTENT_DETAIL_GET_ERROR:
                    case OTT_CHANNEL_PROGRAM_GET_ERROR:
                        finish();
                        break;
                    case SECURE_PLAYER_ERROR:
                        contentsDetailCloseKey(null);
                        break;
//                    case LAUNCH_STB_START_DIALOG:
//                        showPairingDialog(dialogTapType);
//                        break;
//                    case REMOTE_CONTROL_START_PAIRING_DIALOG:
//                        showPairingDialogOnRemoteControl(dialogTapType);
//                        break;
//                    case DTV_EPISODE_LIST_ITEM_DIALOG:
//                        startListDialogDismissTask(which);
//                        break;
                    case COMMON_DIALOG:
                    default:
                        startDialogDismissTask(new Pair<>(dialogTapType, CustomDialog.DialogTapType.BACK_KEY));
                        break;
                }
            }
        });
    }

    @Override
    public void onClipDeleteResult() {

    }

    @Override
    public void onClipDeleteFailure() {

    }

    @Override
    public void onClipRegistResult() {

    }

    @Override
    public void onClipRegistFailure() {

    }

    /**
     * ウイザード（多階層コンテンツ）画面に遷移する.
     *
     * @param contentsData コンテンツデータ
     */
    public void startChildContentListActivity(@NonNull final ContentsData contentsData) {
        Intent intent = new Intent(this, ChildContentListActivity.class);
        intent.putExtra(ChildContentListActivity.INTENT_KEY_CRID, contentsData.getCrid());
        intent.putExtra(ChildContentListActivity.INTENT_KEY_TITLE, contentsData.getTitle());
        intent.putExtra(ChildContentListActivity.INTENT_KEY_DISP_TYPE, contentsData.getDispType());
        intent.putExtra(ChildContentListActivity.INTENT_KEY_IS_RENTAL, contentsData.isRental());
        startActivity(intent);
    }


    /**
     * タイトル内容を設定.
     * ドコモテレビターミナル画像を出す場合は空文字を指定し、enableStbStatusIcon()より後に呼ぶ必要がある.
     *
     * @param text 設定する文字列
     */
    protected void setTitleText(final CharSequence text) {
        if (this instanceof HomeActivity) {
            if (mTitleImageView != null) {
                //ヘッダーに「ドコモテレビターミナル」画像を表示する対応
                mTitleTextView.setVisibility(View.GONE);
                mTitleImageView.setVisibility(View.VISIBLE);
                changeTitlePosition(mTitleImageView, mStatusLinearLayout);
            }
        } else if (mTitleTextView != null) {
            mTitleTextView.setVisibility(View.VISIBLE);
            mTitleImageView.setVisibility(View.GONE);
            mTitleTextView.setText(text);
            changeTitleLayout(mHeaderTitleLayout, mStatusLinearLayout);
        }
    }

    /**
     * 　タイトルの描画を監視する.
     *
     * @param mTitleImageView     タイトルレイアウト
     * @param mStatusLinearLayout 　ステータスアイコンレイアウト
     */
    private void changeTitlePosition(final ImageView mTitleImageView, final LinearLayout mStatusLinearLayout) {
        mStatusLinearLayout.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mStatusLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        displayTitlePosition(mTitleImageView, mStatusLinearLayout);
                        return true;
                    }
                });
    }

    /**
     * 　タイトルの描画を監視する.
     *
     * @param mHeaderTitleLayout  タイトルレイアウト
     * @param mStatusLinearLayout 　ステータスアイコンレイアウト
     */

    private void changeTitleLayout(final RelativeLayout mHeaderTitleLayout, final LinearLayout mStatusLinearLayout) {

        mStatusLinearLayout.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mStatusLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        displayTitleLayout(mHeaderTitleLayout, mStatusLinearLayout);
                        return true;
                    }
                });
    }

    /**
     * タイトルのレイアウトを変える.
     *
     * @param mHeaderTitleLayout 　タイトルレイアウト
     * @param statusIcon         　ステータスアイコンレイアウト
     */

    private void displayTitleLayout(final RelativeLayout mHeaderTitleLayout, final LinearLayout statusIcon) {
        if (statusIcon.getVisibility() == View.VISIBLE && mHeaderTitleLayout.getX() + mHeaderTitleLayout.
                getMeasuredWidth() > statusIcon.getX()) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.header_status_linear);
            mHeaderTitleLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * タイトルのレイアウトを変える.
     *
     * @param mTitleImageView 　タイトルレイアウト
     * @param statusIcon      　ステータスアイコンレイアウト
     */
    private void displayTitlePosition(final ImageView mTitleImageView, final LinearLayout statusIcon) {
        if (mTitleImageView.getX() + mTitleImageView.getMeasuredWidth() > statusIcon.getX()) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.header_status_linear);
            mTitleImageView.setLayoutParams(layoutParams);
        }
    }

    /**
     * アクション名設定.
     * @param titleName タイトル名
     */
    protected void setActionName(final String titleName) {
        mTitle = titleName;
    }

    /**
     * タイトル内容を取得.
     *
     * @return タイトル内容
     */
    protected CharSequence getTitleText() {
        if (mTitleTextView != null) {
            return mTitleTextView.getText();
        }
        return "";
    }

    /**
     * 機能：STB接続アイコンを有効.
     *
     * @param isOn true: 表示  false: 非表示
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    protected void enableStbStatusIcon(final boolean isOn) {
        if (mStbStatusIcon != null) {
            mStbStatusIcon.setVisibility(isOn ? View.VISIBLE : View.INVISIBLE);
            if (!isOn) {
                return;
            }
//            switch (StbConnectionManager.shared().getConnectionStatus()) {
//                case HOME_IN:
//                    setStbStatus(true);
//                    break;
//                case NONE_PAIRING:
//                case NONE_LOCAL_REGISTRATION:
//                case HOME_OUT:
//                case HOME_OUT_CONNECT:
//                default:
//                    setStbStatus(false);
//                    break;
//            }
        }
    }

    /**
     * 通信可能通知.
     * onResume処理及び止めた通信の再開処理を行う.
     *  各ActivityはこのメソッドをOverrideしてResume処理を行う.
     *  ※必ずBaseActivityのメソッドを呼び出してから各ActivityのResume処理を行う
     */
    protected void onStartCommunication() {
        DTVTLogger.start();
        registerDevListDlna();
        DlnaDmsItem dlnaDmsItem = SharedPreferencesUtils.getSharedPreferencesStbInfo(this);
        if (null == dlnaDmsItem) {
            return;
        }

        DTVTLogger.debug("RestartFlag check "
                + SharedPreferencesUtils.getSharedPreferencesRestartFlag(getApplicationContext()));

        // 再起動フラグがtrueならば、再起動メッセージを表示する
        if (SharedPreferencesUtils.getSharedPreferencesRestartFlag(getApplicationContext())) {
            reBuildAllTable();
        }

        //再起動フラグをOFFにする
        SharedPreferencesUtils.setSharedPreferencesRestartFlag(getApplicationContext(), false);

        DTVTLogger.debug("RestartFlag false "
                + SharedPreferencesUtils.getSharedPreferencesRestartFlag(getApplicationContext()));

        //ワンタイムトークンに通信可能を通知する
        setOttDisconnectionFlag(false);

        DTVTLogger.end();
    }

    /**
     * dアカ変更前の再起動ダイアログ表示前にテーブルの再構築を実行する.
     */
    private void reBuildAllTable() {
        DTVTLogger.start();
        RebuildDatabaseTableManager tableManager = new RebuildDatabaseTableManager(this);
        tableManager.allTableRebuild();
        restartMessageDialog();
        DTVTLogger.end();
    }

    /**
     * 再起動時のダイアログ・引数無しに対応するため、可変長引数とする.
     *
     * @param message 省略した場合はdアカウント用メッセージを表示。指定した場合は、常に先頭文字列のみ使用される
     */
    protected void restartMessageDialog(final String... message) {
        //呼び出し用のアクティビティの退避
        final Activity activity = this;

        //出力メッセージのデフォルトはdアカウント用
        String printMessage = getString(R.string.d_account_change_message);

        //引数がある場合はその先頭を使用する
        if (message != null && message.length > 0) {
            DTVTLogger.debug("restartMessageDialog:" + message[0]);
            printMessage = message[0];
        }

        //ダイアログを、OKボタンのコールバックありに設定する
        CustomDialog restartDialog = new CustomDialog(this, CustomDialog.DialogType.ERROR);
        //枠外を押した時の操作を無視するように設定する
        restartDialog.setOnTouchOutside(false);
        restartDialog.setContent(printMessage);
        //startAppDialog.setTitle(getString(R.string.dTV_content_service_start_dialog));
        showCommonControlErrorDialog(printMessage, CustomDialog.ShowDialogType.D_ACCOUNT_CHANGED, null,
                null, null);
    }

    /**
     * 機能：ActivityのSTB接続アイコン.
     */
    private void registerDevListDlna() {
        DTVTLogger.start();
//        if (this instanceof StbSelectActivity
//                || this instanceof LaunchActivity
//                || this instanceof StbConnectActivity
//                || this instanceof TutorialActivity
//                || this instanceof LaunchStbActivity
//                || this instanceof StbWifiSetActivity
//                || this instanceof RemoteSetActivity
//                || this instanceof RemoteSetIntroduceActivity
//                || this instanceof StbSelectErrorActivity
//                || this instanceof DaccountResettingActivity
//                || this instanceof PairingHelpActivity
//        ) {
//            DTVTLogger.end();
//            return;
//        }
//        if (StbConnectionManager.shared().getConnectionStatus() == StbConnectionManager.ConnectionStatus.NONE_PAIRING) {
//            return;
//        }
//        if (DlnaManager.shared().getContext() == null) {
//            DlnaManager.shared().setContext(getApplicationContext());
//        }
//        DlnaManager.shared().StartDmp();
        DTVTLogger.end();
    }

    /**
     * ワンタイムトークン取得完全停止.
     *
     * @param disconnectionFlag 通信を止めるならばtrue
     */
    private void setOttDisconnectionFlag(final boolean disconnectionFlag) {
        DTVTLogger.start();
        final DaccountGetOtt getOtt = new DaccountGetOtt();
        if (getOtt != null) {
            DTVTLogger.debug("setDisconnectionFlag = " + disconnectionFlag);
            //通信切断フラグを指定された値にセット
            getOtt.setDisconnectionFlag(disconnectionFlag);
        }
        DTVTLogger.end();
    }

    /**
     * データが取得失敗した時のトースト表示(メッセージ付き).
     *
     * @param message トースト表示するメッセージ
     */
    protected void showGetDataFailedToast(final String message) {
        if (TextUtils.isEmpty(message)) {
            //メッセージが空文字ならば、既存のメッセージ表示を呼び出す
            showGetDataFailedToast();
        } else {
            if (mToast != null) {
                mToast.cancel();
            }

            if (!this.isFinishing()) {
                //指定されたメッセージを表示する
                mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                mToast.show();
            } else {
                mToast = null;
            }
        }
    }

    /**
     * データが取得失敗した時のトースト表示.
     */
    protected void showGetDataFailedToast() {
        if (mToast != null) {
            mToast.cancel();
        }

        if (!this.isFinishing()) {
            mToast = Toast.makeText(this, R.string.common_get_data_failed_message, Toast.LENGTH_SHORT);
            mToast.show();
        } else {
            mToast = null;
        }
    }

    /**
     * タイトルの表示非表示を設定.
     *
     * @param visible 表示状態
     */
    protected void setTitleVisibility(final Boolean visible) {
        if (mHeaderLayout != null) {
            if (visible) {
                mHeaderLayout.setVisibility(View.VISIBLE);
            } else {
                mHeaderLayout.setVisibility(View.GONE);
            }
        }
    }
}
