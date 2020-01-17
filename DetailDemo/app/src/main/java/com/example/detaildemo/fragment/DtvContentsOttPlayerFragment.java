package com.example.detaildemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.provider.ThumbnailProvider;
import com.example.detaildemo.data.provider.data.OttPlayerContentData;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.data.webapiclient.download.ThumbnailDownloadTask;
import com.example.detaildemo.player.OttPlayerManager;
import com.example.detaildemo.utils.OttContentUtils;
import com.example.detaildemo.utils.UserAgentUtils;
import com.example.detaildemo.view.player.OttFullScreenPlayerViewLayout;
import com.example.detaildemo.view.player.OttPlayerViewLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ott再生用Fragment.
 */
public class DtvContentsOttPlayerFragment extends Fragment implements
        OttFullScreenPlayerViewLayout.FullScreenPlayerViewListener {

    /** 親ビュー.*/
    private View mView = null;
    /** 全画面OTTプレイヤービュー. */
    private OttFullScreenPlayerViewLayout mOttFullScreenPlayerViewLayout;
    /** OTTプレイヤービュー. */
    private OttPlayerViewLayout mOttPlayerViewLayout;
    /**フラグメント見えるリスナー.*/
    private DtvContentsOttPlayerFragmentListener mDtvContentsOttPlayerFragmentListener;
    /** OTTプレイヤービューマネージャー. */
    private OttPlayerManager mOttPlayerManager;
    /** プレイヤー再生情報. */
    private OttPlayerStartData mOttPlayerStartData;
    /** コンテキスト. */
    private Context mContext = null;
    /**サムネイルプロバイダー.*/
    private ThumbnailProvider mThumbnailProvider;
    /** Activity.*/
    private Activity mActivity;
    /** チャンネルリスト.*/
    private List<ContentsData> mChannelList;
    /** サムネイル取得処理ストップフラグ .*/
    private boolean mIsDownloadStop = false;
    /** BuffCompleteフラグ .*/
    private boolean mIsBuffCompleteStop = false;
    /** 端末幅さ.*/
    private int mScreenWidth = 0;
    /** 端末高さ.*/
    private int mScreenHeight = 0;
    /** 端末高さ+nav.*/
    private int mScreenNavHeight = 0;
    /**サムネイルRelativeLayout.*/
    private RelativeLayout mThumbnailRelativeLayout;
    /**プレイヤーFrameLayout.*/
    private FrameLayout mFrameLayout;
    // TODO　削除する予定：仮実装 ---START
    /**Full Screen プレイヤービュー.*/
    private boolean mIsMainPlayer = true;
    // TODO　削除する予定：仮実装 ---END
    private boolean mIsInit = true;

    /**フラグメント見えるリスナー.*/
    public interface DtvContentsOttPlayerFragmentListener {
        /**
         * Fragment見えるのコールバック.
         * @param dtvContentsDetailFragment フラグメント
         */
        void onUserVisibleHint(DtvContentsOttPlayerFragment dtvContentsDetailFragment);
        /**
         * エラーコールバック.
         * @param what what
         * @param extra extra
         */
        void onOttErrorCallback(final int what, final int extra);
        /**
         * 横、縦チェンジコールバック.
         * @param isLandscape 横
         */
        void onOttScreenOrientationChangeCallBack(final boolean isLandscape);
    }

    /**
     * リスナー設定.
     * @param context context
     */
    public void setOttPlayerFragmentListener(final Context context) {
        this.mContext = context;
        this.mDtvContentsOttPlayerFragmentListener = (DtvContentsOttPlayerFragmentListener) context;
    }

    /**
     * コンテキスト設定.
     * @param context context
     */
    public void setContext(final Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return initView(container);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != mDtvContentsOttPlayerFragmentListener) {
            mDtvContentsOttPlayerFragmentListener.onUserVisibleHint(this);
        } else {
            startPlayer();
        }
    }

    @Override
    public Context getContext() {
        this.mActivity = getActivity();
        return mActivity;
    }

    /**
     * ビュー初期化.
     * @param container コンテナ
     * @return view
     */
    private View initView(final ViewGroup container) {
        DTVTLogger.start();
        if (null == mView) {
            mView = LayoutInflater.from(getContext()).inflate( R.layout.dtv_contents_detail_ott_player_fragment, container, false);
        }
        if (mIsMainPlayer) {
            mOttFullScreenPlayerViewLayout = mView.findViewById(R.id.dtv_contents_detail_ott_player_layout_ott_player_rl_full_screen);
            mOttFullScreenPlayerViewLayout.setVisibility(View.VISIBLE);
            mOttFullScreenPlayerViewLayout.setOrientationChangeListener(this);
            mOttFullScreenPlayerViewLayout.setScreenSize(mScreenWidth, mScreenHeight);
            mOttFullScreenPlayerViewLayout.setParentLayout(mThumbnailRelativeLayout);
            mOttFullScreenPlayerViewLayout.setFrameLayout(mFrameLayout);
            mOttFullScreenPlayerViewLayout.setScreenNavigationBarSize(mScreenWidth, mScreenNavHeight);
        } else {
            mOttPlayerViewLayout = mView.findViewById(R.id.dtv_contents_detail_ott_player_layout_ott_player_rl);
            mOttPlayerViewLayout.setVisibility(View.VISIBLE);
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOttFullScreenPlayerViewLayout != null) {
            mOttFullScreenPlayerViewLayout.enableThumbnailConnect();
        }
        enableThumbnailConnect();
    }

    @Override
    public void onPause() {
        if (mOttFullScreenPlayerViewLayout != null) {
            mOttFullScreenPlayerViewLayout.stopThumbnailConnect();
        }
        stopThumbnailConnect();
        super.onPause();
    }

    /**
     * ott再生情報を設定する.
     * @param ottPlayerStartData OTT再生情報
     */
    public void setOttPlayerData(final OttPlayerStartData ottPlayerStartData) {
        mOttPlayerStartData = ottPlayerStartData;
        if (mOttFullScreenPlayerViewLayout != null) {
            mOttFullScreenPlayerViewLayout.initMediaInfo(ottPlayerStartData);
        }
    }

    /**
     * ott再生データ取得.
     * @return ott再生情報
     */
    public OttPlayerStartData getOttPlayerData() {
        return mOttPlayerStartData;
    }

    /**
     * プレイヤービュー初期化.
     */
    public void startPlayer() {
        mIsBuffCompleteStop = false;
        SurfaceHolder holder;
        if (mIsMainPlayer) {
            mOttFullScreenPlayerViewLayout.setVisibility(View.VISIBLE);
            holder = mOttFullScreenPlayerViewLayout.initPlayerView();
        } else {
            mOttPlayerViewLayout.setVisibility(View.VISIBLE);
            holder = mOttPlayerViewLayout.initPlayerView();
        }
        start(holder);
    }

    /**
     * プレイヤービュー初期化.
     * @param holder holder
     */
    private void start(final SurfaceHolder holder) {
        OttPlayerContentData ottPlayerContentData = new OttPlayerContentData();
        ottPlayerContentData.setSystemId( OttContentUtils.OTT_PLAY_SYSTEM_ID);
        ottPlayerContentData.setPlayerUrl(mOttPlayerStartData.getPlayUrl());
        ottPlayerContentData.setWidevineLicenseAcquisitionUrl(mOttPlayerStartData.getLaUrl());
        ottPlayerContentData.setWidevineCustomData(mOttPlayerStartData.getCustomData());
        Map<String, String> headers = new HashMap<>();
        headers.put( DtvtConstants.USER_AGENT, UserAgentUtils.getCustomUserAgentForNeoPlayer());
        ottPlayerContentData.setHeaders(headers);
        if (mOttPlayerManager == null) {
            mOttPlayerManager = new OttPlayerManager(mContext);
            mOttPlayerManager.setSurfaceHolder(holder);
        }
        mOttPlayerManager.setOnPlayerStateListener(new OttPlayerManager.PlayerStateListener() {
            @Override
            public void onOttErrorCallBack(final int what, final int extra) {
                if (mOttPlayerManager != null) {
                    mOttPlayerManager.stop();
                }
                if (null != mDtvContentsOttPlayerFragmentListener) {
                    mDtvContentsOttPlayerFragmentListener.onOttErrorCallback(what, extra);
                }
            }

            @Override
            public void onOttPreparedCallBack(final MediaPlayer mediaPlayer) {
                if (mIsMainPlayer) {
                    if (mOttFullScreenPlayerViewLayout != null) {
                        mOttFullScreenPlayerViewLayout.showProgress(false);
                    }
                } else {
                    if (mOttPlayerViewLayout != null) {
                        mOttPlayerViewLayout.showProgress(false);
                    }
                }
            }

            @Override
            public void onOttBuffCompleteCallBack(final MediaPlayer mediaPlayer) {
                if (mIsBuffCompleteStop) {
                    return;
                }
                if (mIsMainPlayer) {
                    if (mOttFullScreenPlayerViewLayout != null && mIsInit) {
                        mOttFullScreenPlayerViewLayout.showProgress(false);
                        mIsInit = false;
                    }
                } else {
                    if (mOttPlayerViewLayout != null) {
                        mOttPlayerViewLayout.showProgress(false);
                    }
                }
            }
        });
        mOttPlayerManager.prepareToPlay(ottPlayerContentData);
        if (mIsMainPlayer) {
            mOttFullScreenPlayerViewLayout.setPlayerEvent();
        }
    }


    /**
     * 再生を停止.
     */
    public void stop() {
        if (mOttPlayerManager != null) {
            mOttPlayerManager.stop();
        }
    }

    /**
     * 再生を一時停止.
     */
    public void pause() {
        if (mOttPlayerManager != null) {
            mOttPlayerManager.pause();
        }
    }

    @Override
    public void onOttScreenOrientationChangeCallBack(boolean isLandscape) {
        if (mDtvContentsOttPlayerFragmentListener != null) {
            mDtvContentsOttPlayerFragmentListener.onOttScreenOrientationChangeCallBack(isLandscape);
        }
        if (isLandscape) {
            if (mOttFullScreenPlayerViewLayout != null) {
                mOttFullScreenPlayerViewLayout.setChannelListRecyclerViewData(mChannelList, mOttPlayerStartData.getServiceIdUniq());
            }
        }
        if (mOttFullScreenPlayerViewLayout != null) {
            mOttFullScreenPlayerViewLayout.setScreenSize(mScreenWidth, mScreenHeight);
        }
    }

    @Override
    public void onOttScreenSetLogoImageCallBack(String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url)) {
            if (mThumbnailProvider == null) {
                mThumbnailProvider = new ThumbnailProvider(mContext, ThumbnailDownloadTask.ImageSizeType.CONTENT_DETAIL);
            }
            if (!mIsDownloadStop) {
                mThumbnailProvider = new ThumbnailProvider(mContext, ThumbnailDownloadTask.ImageSizeType.CHANNEL);
                imageView.setTag(url);
                Bitmap bitmap = mThumbnailProvider.getThumbnailImage(imageView, url);
                if (bitmap != null) {
                    //サムネイル取得失敗時は取得失敗画像をセットする
                    imageView.setImageBitmap(bitmap);
                } else {
                    // 取得に失敗した場合：エラーロゴを表示（斜線あり）
                    imageView.setImageResource(R.mipmap.error_ch_mini);
                }
            }
        } else {
            // チャンネルロゴURLがない場合
            //　詰めてコンテンツタイトルを表示する
            imageView.setVisibility(View.GONE);
        }
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
     * @param height スクリーン高さ + NavigationBar
     */
    public void setScreenNavigationBarSize(final int height) {
        mScreenNavHeight = height;
    }

    /**
     * ペアレントビューを設定.
     * @param thumbnailRelativeLayout ペアレントビュー
     */
    public void setParentLayout(final RelativeLayout thumbnailRelativeLayout) {
        this.mThumbnailRelativeLayout = thumbnailRelativeLayout;
    }

    /**
     * FrameLayoutビューを設定.
     * @param frameLayout FrameLayoutビュー
     */
    public void setFrameLayout(final FrameLayout frameLayout) {
        this.mFrameLayout = frameLayout;
    }

    /**
     * 横画面視聴中にバックキーボタンをタップした際に、縦画面に戻る処理.
     */
    public void tapBackKey() {
        if (mOttFullScreenPlayerViewLayout != null) {
            mOttFullScreenPlayerViewLayout.tapBackKey();
        }
    }

    /**
     * サムネイル取得処理を止める.
     */
    private void stopThumbnailConnect() {
        DTVTLogger.start();
        mIsDownloadStop = true;
        if (mThumbnailProvider != null) {
            mThumbnailProvider.stopConnect();
        }
    }

    /**
     * 止めたサムネイル取得処理を再度取得可能な状態にする.
     */
    public void enableThumbnailConnect() {
        DTVTLogger.start();
        mIsDownloadStop = false;
        if (mThumbnailProvider != null) {
            mThumbnailProvider.enableConnect();
        }
    }

    // TODO　削除する予定：仮実装 ---START
    /**
     * BG → FG プレイヤービューをリスタートする
     */
    public void restartPlayerViewFromBg() {
        if (mOttFullScreenPlayerViewLayout != null) {
            mOttFullScreenPlayerViewLayout.restartPlayerViewFromBg();
        }
    }
    // TODO　削除する予定：仮実装 ---END

    public void setChannelListData(final List<ContentsData> mobileChannelList) {
        mChannelList = mobileChannelList;
    }

}
