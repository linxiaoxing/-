package com.example.detaildemo.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detaildemo.R;
import com.example.detaildemo.activity.ContentDetailActivity;
import com.example.detaildemo.adapter.DtvContentsBroadcastAdapter;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.provider.stop.StopBroadcastAdapterConnect;
import com.example.detaildemo.data.webapiclient.jsonparser.data.TimerNoticeInfo;
import com.example.detaildemo.utils.ContentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 放送番組フラグメント.
 */
public class DtvContentsBroadcastFragment extends Fragment implements
        DtvContentsBroadcastAdapter.BroadcastItemClickCallback {
    /** 列数.*/
    public static final int SPAN_COUNT = 2;
    /** 間隔.*/
    private static final float SPAN_INTERVAL = 16;
    /** 放送番組親ビュー.*/
    private View mView;
    /** Activity.*/
    private Activity mActivity;
    /** データがない場合.*/
    private TextView mNoMessageTxt;
    /** ContentsBroadcastFragmentListenerリスナー.*/
    private ContentsBroadcastFragmentListener mContentsBroadcastFragmentListener;
    /** DtvContentsBroadcastAdapter.*/
    private DtvContentsBroadcastAdapter mDtvContentsBroadcastAdapter = null;
    /** OTTコンテンツデータ.*/
    private List<ContentsData> mContentsData = null;
    /** 放送番組RecyclerView.*/
    private RecyclerView mRecyclerView = null;
    /** GridDividerItemDecoration.*/
    private GridDividerItemDecoration mItemDecoration = null;
    /** 番組切替時に情報を受け取るBroadcastReceiver. */
    private OttBroadcastReceiver mContentChangeReceiver;
    /** 番組表情報終了フラグ. */
    boolean mIsChannelCompleted = false;

    @Override
    public void onBroadcastItemClick(final ContentsData contentsData) {
        ContentDetailActivity contentDetailActivity = (ContentDetailActivity) mActivity;
        if (ContentUtils.isChildContentList(contentsData)) {
            contentDetailActivity.startChildContentListActivity(contentsData);
        } else {
            Intent intent = new Intent();
            intent.setClass(mActivity, ContentDetailActivity.class);
            intent.putExtra(ContentUtils.PLALA_INFO_BUNDLE_KEY, contentsData.getContentsId());
            contentDetailActivity.startActivity(intent);
        }
    }
    /**
     * コールバックリスナー.
     */
    public interface ContentsBroadcastFragmentListener {
        /**
         * Fragment初期化されるコールバック.
         * @param isVisibleToUser isVisibleToUser
         * @param fragment fragment
         */
        void onUserVisibleHint(boolean isVisibleToUser, DtvContentsBroadcastFragment fragment);
    }

    /**
     * リスナー設定.
     * @param contentsBroadcastFragmentListener contentsBroadcastFragmentListener
     */
    public void setContentsDetailFragmentListener(final ContentsBroadcastFragmentListener contentsBroadcastFragmentListener) {
        this.mContentsBroadcastFragmentListener = contentsBroadcastFragmentListener;
    }


    @Override
    public Context getContext() {
        this.mActivity = getActivity();
        return mActivity;
    }

    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return initView(container);
    }

    @Override
    public void onDestroy() {
        if (mContentChangeReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity.getApplication()).unregisterReceiver(mContentChangeReceiver);
            mContentChangeReceiver = null;
        }
        super.onDestroy();
    }

    /**
     * コンテンツ詳細画面エピソードタブ表示されること.
     * @param container コンテナ
     * @return view ビュー
     */
    private View initView(final ViewGroup container) {
        DTVTLogger.start();
        if (null == mView) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.dtv_contents_broadcast_fragment, container, false);
        }

        mNoMessageTxt = mView.findViewById(R.id.dtv_contents_recycler_view_no_data);

        if (mContentsData == null) {
            mContentsData = new ArrayList<>();
        }
        // 放送番組RecyclerView
        if (mRecyclerView == null) {
            mRecyclerView = mView.findViewById(R.id.contents_detail_broadcast_recycler_view);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT, RecyclerView.VERTICAL, false);
        if (mItemDecoration == null) {
            mItemDecoration = new GridDividerItemDecoration(getContext(), SPAN_INTERVAL, SPAN_COUNT);
            mRecyclerView.addItemDecoration(mItemDecoration);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mDtvContentsBroadcastAdapter = new DtvContentsBroadcastAdapter(getContext(), mContentsData);
        mDtvContentsBroadcastAdapter.setBroadcastItemClickListener(this);
        mRecyclerView.setAdapter(mDtvContentsBroadcastAdapter);
        mRecyclerView.setItemAnimator(null);
        DTVTLogger.end();
        return mView;
    }
    /**
     * データ更新.
     */
    public void setNotifyDataChanged() {
        showProgress(false);
        loadComplete();
        mDtvContentsBroadcastAdapter.notifyDataSetChanged();
    }

    /**
     * 一覧更新して、フッタービューの非表示.
     */
    public void loadComplete() {
        if (mContentsData != null && mContentsData.size() > 0) {
            mNoMessageTxt.setVisibility(View.GONE);
        } else {
            mNoMessageTxt.setVisibility(View.VISIBLE);
            mNoMessageTxt.setText(mActivity.getResources().getString(R.string.common_empty_data_message));
        }
    }

    /**
     * 取得失敗の場合.
     */
    public void loadFailed() {
        showProgress(false);
        mNoMessageTxt.setVisibility(View.VISIBLE);
        mNoMessageTxt.setText(mActivity.getResources().getString(R.string.common_get_data_failed_message));
    }

    /**
     * ビュー初期化.
     */
    public void initLoad() {
        DTVTLogger.start();

        DTVTLogger.end();
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        DTVTLogger.start();
        super.setUserVisibleHint(isVisibleToUser);
        if (mContentsBroadcastFragmentListener != null) {
            mContentsBroadcastFragmentListener.onUserVisibleHint(isVisibleToUser, this);
        }
        DTVTLogger.end();
    }

    /**
     * BroadcastAdapterの通信を止める.
     */
    public void stopBroadcastAdapterCommunication() {
        DTVTLogger.start();
        StopBroadcastAdapterConnect stopBroadcastAdapterConnect = new StopBroadcastAdapterConnect();
        if (mDtvContentsBroadcastAdapter != null) {
            stopBroadcastAdapterConnect.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, mDtvContentsBroadcastAdapter);
        }
    }

    /**
     * BroadcastAdapterで止めた通信を再度可能な状態にする.
     */
    public void enableBroadcastAdapterCommunication() {
        DTVTLogger.start();
        if (mDtvContentsBroadcastAdapter != null) {
            mDtvContentsBroadcastAdapter.enableConnect();
        }
    }

    /**
     * 放送番組データ取得.
     * @return 放送番組データ
     */
    public List<ContentsData> getContentsData() {
        return mContentsData;
    }

    /**
     * 放送番組データ設定.
     * @param contentsDataList 放送番組データ
     */
    public void setContentsData(final List<ContentsData> contentsDataList) {
        this.mContentsData.clear();
        this.mContentsData.addAll(contentsDataList);
    }

    /**
     * エピソードデータ読み込み中アイコンの表示切替.
     * @param showProgress 表示するならばtrue
     */
    public void showProgress(final boolean showProgress) {
        //ヌルチェックの追加
        if (mView == null) {
            return;
        }
        View progressView = mView.findViewById(R.id.broadcast_progress_relative_layout);
        if (progressView == null) {
            return;
        }
        if (showProgress) {
            // 既にコンテンツデータがある場合は表示しない
            if (mContentsData != null && mContentsData.size() > 0) {
                return;
            }
            progressView.setVisibility(View.VISIBLE);
            if (mNoMessageTxt != null) {
                mNoMessageTxt.setVisibility(View.GONE);
            }
        } else {
            progressView.setVisibility(View.GONE);
        }
    }

    /**
     * 番組切替時にonReceiveに入るのBroadcastReceiver
     */
    private class OttBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final TimerNoticeInfo serviceIdUniq = intent.getParcelableExtra(ContentUtils.NOTICE_INFO);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ContentUtils.CONTENTS_CHANGE_ACTION.equals(intent.getAction())) {
                        if (mContentsData != null && mContentsData.size() > 0 && serviceIdUniq != null) {
                            for (int i = 0; i < mContentsData.size(); i++) {
                                if (mContentsData.get(i) == null) {
                                    continue;
                                }
                                if (!TextUtils.isEmpty(serviceIdUniq.getServiceIdUniq()) &&
                                        serviceIdUniq.getServiceIdUniq().equals(mContentsData.get(i).getServiceIdUniq())) {
                                    mContentsData.get(i).setTitle(serviceIdUniq.getTitle());
                                    mContentsData.get(i).setRValue(serviceIdUniq.getRValue());
                                    if (!TextUtils.isEmpty(serviceIdUniq.getThumListURL())) {
                                        mContentsData.get(i).setThumURL(serviceIdUniq.getThumListURL());
                                    } else {
                                        mContentsData.get(i).setThumURL(mContentsData.get(i).getChannelThumListURL());
                                    }
                                    break;
                                }
                                setNotifyDataChanged();
                            }
                        }
                    } else if (ContentUtils.CHANNEL_COMPLETED_ACTION.equals(intent.getAction())) {
                        mIsChannelCompleted = true;
                        //getProgram(mServiceIdUniqs);
                    }
                }
            });
        }
    };

}

