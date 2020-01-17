package com.example.detaildemo.activity;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.detaildemo.R;
import com.example.detaildemo.adapter.ContentsAdapter;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.data.bean.ClipRequestData;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.provider.ChildContentDataProvider;
import com.example.detaildemo.data.provider.stop.StopChildContentDataConnect;
import com.example.detaildemo.data.provider.stop.StopContentsAdapterConnect;
import com.example.detaildemo.data.webapiclient.jsonparser.data.ActiveData;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.NetWorkUtils;
import com.example.detaildemo.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 子コンテンツ表示専用アクティビティ.
 */
public class ChildContentListActivity extends BaseActivity implements

        AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener,
        AbsListView.OnTouchListener,
        ChildContentDataProvider.DataCallback {
    /** 子コンテンツ一覧遷移時リクエストコード.*/
    private static final int REQUEST_CODE_CHILD_CONTENT_LIST = 1000;
    /** 戻る際にアクティビティを終了するコード.*/
    private static final int RESULT_CODE_FINISH_ACTIVITY = REQUEST_CODE_CHILD_CONTENT_LIST + 1;
    // region variable
    // view
    /** ランキングリストを表示するリスト.*/
    private ListView mListView;
    /** ProgressBar.*/
    private RelativeLayout mRelativeLayout = null;
    /** リスト0件メッセージ.*/
    private TextView mNoDataMessage;
    /** 検索プログレスバー.*/
    private View mLoadMoreView;
    /** data.*/
    public static final String INTENT_KEY_CRID = "crid",
            INTENT_KEY_TITLE = "title",
            INTENT_KEY_DISP_TYPE = "dispType",
            INTENT_KEY_IS_RENTAL = "isRental";
    /** ChildContentDataProvider. */
    private ChildContentDataProvider mChildContentDataProvider;
    /** リスト表示用アダプタ.*/
    private ContentsAdapter mContentsAdapter;
    /** コンテンツデータ一覧のリスト.*/
    private List<ContentsData> mContentsList;
    /** クリップリクエスト用データ. */
    private ClipRequestData mClipRequestData = null;
    /** コンテンツ詳細表示フラグ.*/
    private boolean mContentsDetailDisplay = false;
    /** mCrid.*/
    private String mCrid;
    /** タイプ.*/
    private String mDispType;
    /** 購入情報取得フラグ.*/
    private boolean mIsRental = false;
    /** データの追加読み込み状態の識別フラグ.*/
    private boolean mIsCommunicating = false;
    /** スクロール位置の記録.*/
    private int mFirstVisibleItem = 0;
    /** 最後のスクロール方向が上ならばtrue.*/
    private boolean mLastScrollUp = false;
    /** 指を置いたY座標.*/
    private float mStartY = 0;
    /** ロード終了.*/
    private boolean mIsEndPage = false;
    // endregion variable

    // region Activity LifeCycle
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.clear();
        }
        setContentView( R.layout.child_content_main_layout);

        Intent intent = getIntent();

        mCrid = intent.getStringExtra(INTENT_KEY_CRID);
        String title = intent.getStringExtra(INTENT_KEY_TITLE);
        mDispType = intent.getStringExtra(INTENT_KEY_DISP_TYPE);
        mIsRental = intent.getBooleanExtra(INTENT_KEY_IS_RENTAL, false);

        DTVTLogger.debug("mCrid = " + mCrid + ", title = " + title);
        setTitleText(title);
        setHeaderColor(false);
        enableGlobalMenuIcon(true);
        changeGlobalMenuIconToCloseIcon();
        setStatusBarColor(R.color.contents_header_background);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DTVTLogger.start();
        enableStbStatusIcon(true);
        //コンテンツ詳細から戻ってきたときのみクリップ状態をチェックする
        if (mContentsDetailDisplay) {
            mContentsDetailDisplay = false;
            if (null != mContentsAdapter) {
                List<ContentsData> list = mChildContentDataProvider.checkClipStatus(mContentsList);
                mContentsAdapter.setListData(list);
                mContentsAdapter.notifyDataSetChanged();
            }
        }
        DTVTLogger.end();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DTVTLogger.start();
        //通信を止める
        StopChildContentDataConnect stopChildContentDataConnect = new StopChildContentDataConnect();
        stopChildContentDataConnect.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, mChildContentDataProvider);
        StopContentsAdapterConnect stopContentsAdapterConnect = new StopContentsAdapterConnect();
        stopContentsAdapterConnect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mContentsAdapter);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHILD_CONTENT_LIST
                && resultCode == RESULT_CODE_FINISH_ACTIVITY) {
            mContentsDetailDisplay = false;
            setResult(RESULT_CODE_FINISH_ACTIVITY);
            finish();
        }
    }
    // endregion Activity LifeCycle

    @Override
    public void onStartCommunication() {
        super.onStartCommunication();
        DTVTLogger.start();

        //データプロパイダあれば通信を許可し、無ければ作成
        if (mChildContentDataProvider == null) {
            mChildContentDataProvider = new ChildContentDataProvider(this);
        }
        mChildContentDataProvider.enableConnect();

        //アダプタがあれば更新を行い、無ければデータの取得を行う
        if (mContentsAdapter != null) {
            mContentsAdapter.enableConnect();
            if (mContentsAdapter.getCount() == 0) {
                //初回取得中に通信が停止された場合、アダプタは存在するがデータは0件という状態になるため、
                //その場合にはデータの再取得を行う.
                showProgressBar(true);
                mChildContentDataProvider.getChildContentList(mCrid, 1, mDispType, mIsRental);
            } else {
                mContentsAdapter.notifyDataSetChanged();
            }
        } else {
            showProgressBar(true);
            mChildContentDataProvider.getChildContentList(mCrid, 1, mDispType, mIsRental);
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        //指を動かした方向を検知する
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //指を降ろしたので、位置を記録
                mStartY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                //指を離したので、位置を記録
                float mEndY = motionEvent.getY();
                mLastScrollUp = false;
                //スクロール方向の判定
                if (mStartY < mEndY) {
                    //終了時のY座標の方が大きいので、上スクロール
                    mLastScrollUp = true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        ContentsData contentsData = mContentsList.get(position);
        if (ContentUtils.isChildContentList(contentsData)) {
            if (isFastClick()) {
                Intent intent = new Intent(this, ChildContentListActivity.class);
                intent.putExtra(ChildContentListActivity.INTENT_KEY_CRID, contentsData.getCrid());
                intent.putExtra(ChildContentListActivity.INTENT_KEY_TITLE, contentsData.getTitle());
                intent.putExtra(ChildContentListActivity.INTENT_KEY_DISP_TYPE, contentsData.getDispType());
                intent.putExtra(ChildContentListActivity.INTENT_KEY_IS_RENTAL, mIsRental);
                intent.putExtra( DtvtConstants.SOURCE_SCREEN, getComponentName().getClassName());
                startActivityForResult(intent, REQUEST_CODE_CHILD_CONTENT_LIST);
            }
        } else {
            Intent intent = new Intent(this, ContentDetailActivity.class);
            intent.putExtra(DtvtConstants.SOURCE_SCREEN, getComponentName().getClassName());
            intent.putExtra(ContentUtils.PLALA_INFO_BUNDLE_KEY, contentsData.getContentsId());
            //コンテンツ詳細表示フラグを有効にする
            mContentsDetailDisplay = true;
            startActivity(intent);
        }
    }

    @Override
    public void onScroll(final AbsListView absListView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        synchronized (this) {
            if (null == mContentsAdapter) {
                return;
            }
            //現在のスクロール位置の記録
            mFirstVisibleItem = firstVisibleItem;
        }
    }

    @Override
    public void onScrollStateChanged(final AbsListView absListView, final int scrollState) {
        if (mIsEndPage) {
            return;
        }
        synchronized (this) {
            if (null == mContentsAdapter) {
                return;
            }
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && absListView.getLastVisiblePosition() == mContentsAdapter.getCount() - 1) {
                DTVTLogger.warning("mIsCommunicating = " + mIsCommunicating);
                if (mIsCommunicating) {
                    return;
                }
                DTVTLogger.warning("mFirstVisibleItem = " + mFirstVisibleItem + ", mLastScrollUp = " + mLastScrollUp);
                //スクロール位置がリストの先頭で上スクロールだった場合は、更新をせずに帰る
                if (mFirstVisibleItem == 0 && mLastScrollUp) {
                    return;
                }

                displayMoreData(true);
                setCommunicatingStatus(true);
                mNoDataMessage.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChildContentDataProvider.getChildContentList(mCrid, mChildContentDataProvider.getPagerOffset(), mDispType, mIsRental);
                    }
                }, LOAD_PAGE_DELAY_TIME);
            }
        }
    }

    @Override
    public void childContentListCallback(@Nullable final List<ContentsData> contentsDataList, final List<ActiveData> activeDatas) {
        // クリップ状態の判定Status変更済みコンテンツデータリスト
        final List<ContentsData> clipStatusContentsDataList = mChildContentDataProvider.checkClipStatus(contentsDataList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressBar(false);
                displayChildContentList(clipStatusContentsDataList);
            }
        });
    }

    @Override
    public void onClipRegistResult() {
        DTVTLogger.start();
        //コンテンツリストに登録ステータスを反映する.
        setContentsListClipStatus(mContentsList);
        super.onClipRegistResult();
        DTVTLogger.end();
    }

    @Override
    public void onClipDeleteResult() {
        DTVTLogger.start();
        //コンテンツリストに削除ステータスを反映する.
        setContentsListClipStatus(mContentsList);
        super.onClipDeleteResult();
        DTVTLogger.end();
    }

    /**
     * 取得結果の設定・表示.
     *
     * @param contentsDataList 取得したコンテンツデータリスト
     */
    private void displayChildContentList(final List<ContentsData> contentsDataList) {
        if (null == contentsDataList) {
            displayMoreData(false);

            //エラーメッセージを取得する
            ErrorState errorState = mChildContentDataProvider.getError();
            if (errorState != null) {
                String message = errorState.getApiErrorMessage(getApplicationContext());
                //有無で処理を分ける
                if (!TextUtils.isEmpty(message)) {
                    showDialogToClose(this, message);
                    return;
                }
            }
            showDialogToClose(this);
            return;
        }

        for (ContentsData item: contentsDataList) {
            mContentsList.add(item);
        }

        resetCommunication();
        mContentsAdapter.notifyDataSetChanged();
        DTVTLogger.end();
        if (mContentsList.size() == 0) {
            mNoDataMessage.setVisibility(View.VISIBLE);
        } else {
            mNoDataMessage.setVisibility(View.GONE);
        }
        if (mContentsList.size() >= mChildContentDataProvider.getTotal()) {
            mIsEndPage = true;
        }
    }

    /**
     * プロセスバーを表示する.
     *
     * @param showProgressBar プロセスバーを表示するかどうか
     */
    private void showProgressBar(final boolean showProgressBar) {
        mListView = findViewById(R.id.child_content_list);
        mRelativeLayout = findViewById(R.id.child_content_progress);
        if (showProgressBar) {
            //オフライン時は表示しない
            if (!NetWorkUtils.isOnline(this)) {
                return;
            }
            mListView.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mRelativeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 画面初期設定.
     */
    private void initView() {
        Intent intent = getIntent();
        //ヘッダーの設定
        String sourceClass = intent.getStringExtra(DtvtConstants.SOURCE_SCREEN);
        if (sourceClass == null || sourceClass.isEmpty()) {
            enableHeaderBackIcon(false);
        } else {
            enableHeaderBackIcon(true);
        }
        findViewById(R.id.header_stb_status_icon).setOnClickListener(mRemoteControllerOnClickListener);
        if (mContentsList == null) {
            mContentsList = new ArrayList<>();
        }
        mListView = findViewById(R.id.child_content_list);
        mListView.setOnItemClickListener(this);

        mRelativeLayout = findViewById(R.id.child_content_progress);

        mListView.setOnScrollListener(this);

        //スクロールの上下方向検知用のリスナーを設定
        mListView.setOnTouchListener(this);
        mContentsAdapter = new ContentsAdapter(this, mContentsList,
                ContentsAdapter.ActivityTypeItem.TYPE_VIDEO_CONTENT_LIST);
        mListView.setAdapter(mContentsAdapter);
        mLoadMoreView = View.inflate(this, R.layout.search_load_more, null);
        mNoDataMessage = findViewById(R.id.child_content_list_no_items);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.header_layout_menu:
                setResult(RESULT_CODE_FINISH_ACTIVITY);
                finish();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    /**
     * 再読み込み実施フラグ設定.
     *
     * @param bool 読み込み表示フラグ
     */
    private void setCommunicatingStatus(final boolean bool) {
        synchronized (this) {
            mIsCommunicating = bool;
        }
    }
    /**
     * 再読み込み時の処理.
     */
    private void resetCommunication() {
        displayMoreData(false);
        setCommunicatingStatus(false);
    }

    /**
     * 読み込み表示を行う.
     *
     * @param bool 読み込み表示フラグ
     */
    private void displayMoreData(final boolean bool) {
        if (null != mListView) {
            if (bool) {
                mListView.addFooterView(mLoadMoreView);
                //スクロール位置を最下段にすることで、追加した更新フッターを画面内に入れる
                mListView.setSelection(mListView.getMaxScrollAmount());
            } else {
                mListView.removeFooterView(mLoadMoreView);
            }
        }
    }

    /**
     * 各一覧画面のクリップ登録／解除リクエストの結果応答時にコンテンツリストに登録／削除ステータスを反映する.
     * @param contentsList 各一覧画面のコンテンツリスト
     */
    protected void setContentsListClipStatus(List<ContentsData> contentsList) {
        ClipRequestData clipRequestData = getClipRequestData(); //クリップ登録/削除リクエスト時のクリップ情報
        if (null != contentsList && null != clipRequestData) {
            for (ContentsData info : contentsList) {
                if (clipRequestData.equals(info.getRequestData())) {
                    //コンテンツリストに登録／削除ステータスを反映する.
                    info.setClipStatus(clipRequestData.isClipStatus());
                    DTVTLogger.debug(String.format("set clip result: crid [%s] clip status [%s]", info.getCrid(), info.isClipStatus()));
                    break;
                }
            }
        }
    }

    /**
     * クリップ要求時のリクエストデータを返却する.
     * @return クリップ登録/解除要求時のリクエストデータ
     */
    protected ClipRequestData getClipRequestData() {
        return mClipRequestData;
    }

    /**
     * データが取得失敗した時のダイアログ表示.
     * @param context コンテキスト
     */
    protected void showDialogToClose(final Context context) {
        //文字列リソースを取得して、メッセージ指定側に処理を移譲
        showDialogToClose(this, getApplicationContext().getString(
                R.string.common_get_data_failed_message));
    }

    /**
     * データが取得失敗した時のダイアログ表示（メッセージ指定）.
     *
     * @param context コンテキスト
     * @param message メッセージ
     */
    protected void showDialogToClose(final Context context, final String message) {
        CustomDialog closeDialog = new CustomDialog(context, CustomDialog.DialogType.ERROR);
        closeDialog.setContent(message);
        closeDialog.setOkCallBack(new CustomDialog.ApiOKCallback() {
            @Override
            public void onOKCallback(final boolean isOK) {
                contentsDetailBackKey(null);
            }
        });
        //戻るボタン等でダイアログが閉じられた時もOKと同じ挙動
        closeDialog.setDialogDismissCallback(new CustomDialog.DismissCallback() {
            @Override
            public void allDismissCallback() {
                //NOP
            }

            @Override
            public void otherDismissCallback() {
                contentsDetailBackKey(null);
            }
        });
        closeDialog.setCancelable(false);
        closeDialog.showDialog();
    }

    /**
     * リモコンUI画面用 onClickListener.
     */
    protected final View.OnClickListener mRemoteControllerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
//            switch (v.getId()) {
//                case R.id.header_stb_status_icon:
//                    if (StbConnectionManager.shared().getConnectionStatus() == StbConnectionManager.ConnectionStatus.HOME_IN) {
//                        DTVTLogger.debug("Start RemoteControl");
//                        if (findViewById(R.id.base_progress_rl).getVisibility() == View.VISIBLE) {
//                            DTVTLogger.debug("Return RemoteControl");
//                            return;
//                        }
//                        if (v.getContext() instanceof ContentDetailActivity) {
//                            if (((ContentDetailActivity) v.getContext()).getControllerVisible()) {
//                                // コンテンツ詳細画面でコントローラのヘッダーを表示する場合
//                                createRemoteControllerView(true);
//                            } else {
//                                // コンテンツ詳細画面でコントローラのヘッダーを表示しない場合
//                                createRemoteControllerView(false);
//                            }
//                        } else {
//                            // コンテンツ詳細画面以外の場合
//                            createRemoteControllerView(false);
//                            // 鍵交換処理が必要
//                            setKeyExchangeFlag(true);
//                        }
//                        getRemoteControllerView().startRemoteUI(true);
//                    }
//                    break;
//                default:
//                    DTVTLogger.debug("Close Controller");
//                    getRemoteControllerView().closeRemoteControllerUI();
//                    break;
//            }
        }
    };
}
