package com.example.detaildemo.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.provider.ThumbnailProvider;
import com.example.detaildemo.data.provider.data.OttPlayerContentData;
import com.example.detaildemo.data.webapiclient.download.ThumbnailDownloadTask;
import com.example.detaildemo.player.OttPlayerManager;
import com.example.detaildemo.utils.OttContentUtils;
import com.example.detaildemo.utils.UserAgentUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * プレイヤーレイアウト画面(全画面)に表示するチャンネルリストのアダプタ.
 */
public class PlayerChannelListRecyclerViewAdapter extends RecyclerView.Adapter<PlayerChannelListRecyclerViewAdapter.ViewHolder> {
    /**
     * Inflater.
     */
    private final LayoutInflater mInflater;
    /**
     * チャンネルリストデータ.
     */
    private List<ContentsData> mContentsDatas;
    /**
     * コンテキスト.
     */
    private final Context mContext;
    /**
     * サムネイル取得用プロバイダー.
     */
    private ThumbnailProvider mThumbnailProvider;
    /**
     * ダウンロード禁止判定フラグ.
     */
    private boolean mIsDownloadStop = false;

    /**
     * 再利用のビュー最大count.
     */
    private int mMaxItemCount = 0;

    /**
     * サービスIDユニーク.
     */
    private String mServiceIdUniq;

    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     */
    public PlayerChannelListRecyclerViewAdapter(final Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mThumbnailProvider = new ThumbnailProvider(mContext, ThumbnailDownloadTask.ImageSizeType.HOME_LIST);
    }

    /**
     * RecyclerViewデータの設定.
     *
     * @param contentsDatas contentsDatas
     */
    public void setRecyclerViewData(final List<ContentsData> contentsDatas, final String serviceIdUniq) {
        mContentsDatas = contentsDatas;
        mServiceIdUniq = serviceIdUniq;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mContentsDatas == null) {
            return 0;
        }
        return  mContentsDatas.size();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        View view = mInflater.inflate( R.layout.tv_player_channel_list_layout_recyclerview_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        //viewHolder.thumbnailUrl = view.findViewById(R.id.tv_player_channel_list_recyclerview_item_iv);
        viewHolder.mSurfaceView = view.findViewById(R.id.tv_contents_recycler_view_player);
        viewHolder.channelListLogo = view.findViewById(R.id.tv_player_fullscreen_channel_list_logo);
        viewHolder.channelListTitle = view.findViewById(R.id.tv_player_fullscreen_channel_list_name);
        viewHolder.watchingItemAnimation = view.findViewById(R.id.tv_player_fullscreen_channel_list_animation);
        //viewHolder.watchingItemBackground = view.findViewById(R.id.tv_player_channel_list_recyclerview_item_iv_on_live_background);
        mMaxItemCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        DTVTLogger.start();
        //viewHolder.thumbnailUrl.setImageResource(R.mipmap.loading_list);
        if (mContentsDatas == null) {
            return;
        }
        final ContentsData contentsData = mContentsDatas.get(position);
//        if (!mIsDownloadStop) {
//            String thumbnail = contentsData.getThumURL();
//            if (!TextUtils.isEmpty(thumbnail)) {
//                viewHolder.thumbnailUrl.setTag(thumbnail);
//                mThumbnailProvider.setMaxQueueCount(mMaxItemCount);
//                Bitmap thumbnailImage = mThumbnailProvider.getThumbnailImage(viewHolder.thumbnailUrl, thumbnail);
//                if (thumbnailImage != null) {
//                    viewHolder.thumbnailUrl.setImageBitmap(thumbnailImage);
//                } else {
//                    // 無い場合はchロゴ
//                    if (!TextUtils.isEmpty(contentsData.getThumDetailURL())) {
//                        Bitmap bitmap = mThumbnailProvider.getThumbnailImage(viewHolder.thumbnailUrl, contentsData.getThumDetailURL());
//                        if (bitmap != null) {
//                            viewHolder.thumbnailUrl.setImageBitmap(bitmap);
//                        } else {
//                            // エラーサムネイル
//                            viewHolder.thumbnailUrl.setImageResource(R.mipmap.error_ch_mini);
//                        }
//                    } else {
//                        // エラーサムネイル
//                        viewHolder.thumbnailUrl.setImageResource(R.mipmap.error_ch_mini);
//                    }
//                }
//
//                if (!TextUtils.isEmpty(mServiceIdUniq) && mServiceIdUniq.equals(contentsData.getServiceIdUniq())) {
//                    // 視聴中アニメーションを表示する
//                    viewHolder.watchingItemAnimation.setVisibility(View.VISIBLE);
//                    viewHolder.watchingItemAnimation.setBackgroundResource(R.drawable.audio_bar_animation);
//                    AnimationDrawable frameAnimation = (AnimationDrawable) viewHolder.watchingItemAnimation.getBackground();
//                    frameAnimation.start();
//                    viewHolder.watchingItemBackground.setVisibility(View.VISIBLE);
//                } else {
//                    viewHolder.watchingItemBackground.setVisibility(View.GONE);
//                    viewHolder.watchingItemAnimation.setVisibility(View.GONE);
//                }
//            } else {
//                // 無い場合はchロゴ
//                if (!TextUtils.isEmpty(contentsData.getThumDetailURL())) {
//                    viewHolder.thumbnailUrl.setTag(contentsData.getThumDetailURL());
//                    Bitmap bitmap = mThumbnailProvider.getThumbnailImage(viewHolder.thumbnailUrl, contentsData.getThumDetailURL());
//                    if (bitmap != null) {
//                        viewHolder.thumbnailUrl.setImageBitmap(bitmap);
//                    } else {
//                        // エラーサムネイル
//                        viewHolder.thumbnailUrl.setImageResource(R.mipmap.error_ch_mini);
//                    }
//                } else {
//                    // エラーサムネイル
//                    viewHolder.thumbnailUrl.setImageResource(R.mipmap.error_ch_mini);
//                }
//            }
//        }
        //viewHolder.thumbnailUrl.setVisibility(View.GONE);
        viewHolder.mSurfaceView.setVisibility(View.VISIBLE);
        startPlay(viewHolder);

        if (!TextUtils.isEmpty(contentsData.getThumDetailURL())) {
            if (!mIsDownloadStop) {
                viewHolder.channelListLogo.setTag(contentsData.getThumDetailURL());
                Bitmap bitmap = mThumbnailProvider.getThumbnailImage(viewHolder.channelListLogo, contentsData.getThumDetailURL());
                if (bitmap != null) {
                    viewHolder.channelListLogo.setImageBitmap(bitmap);
                } else {
                    // 取得に失敗した場合：エラーロゴを表示（斜線あり）
                    viewHolder.channelListLogo.setImageResource(R.mipmap.error_ch_mini);
                }
            }
        } else {
            // チャンネルロゴURLがない場合
            //　詰めてコンテンツタイトルを表示する
            viewHolder.channelListLogo.setVisibility(View.GONE);
        }
        // TODO　削除する予定：詳細画面（縦画面）コンテンツのチャンネルリスト用の仮実装 -----START
        if (!TextUtils.isEmpty(mServiceIdUniq) && mServiceIdUniq.equals(contentsData.getServiceIdUniq())) {
            if (!TextUtils.isEmpty(contentsData.getChannelName())) {
                viewHolder.channelListTitle.setText(contentsData.getChannelName());
            }
            return;
        }
        // TODO　削除する予定：詳細画面（縦画面）コンテンツのチャンネルリスト用の仮実装 -----END

        String title = contentsData.getTitle();
        if (!TextUtils.isEmpty(title)) {
            viewHolder.channelListTitle.setText(title);
        }

        DTVTLogger.end();
    }

    /**
     * プレイヤービュー初期化.
     * TODO:再生関連はDREM-8828で対応予定
     * @param viewHolder holder
     */
    private void startPlay(final ViewHolder viewHolder) {
        OttPlayerContentData ottPlayerContentData = new OttPlayerContentData();
        ottPlayerContentData.setSystemId( OttContentUtils.OTT_PLAY_SYSTEM_ID);
        ottPlayerContentData.setPlayerUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        ottPlayerContentData.setWidevineLicenseAcquisitionUrl("");
        ottPlayerContentData.setWidevineCustomData("");

        Map<String, String> headers = new HashMap<>();
        headers.put( DtvtConstants.USER_AGENT, UserAgentUtils.getCustomUserAgentForNeoPlayer());
        ottPlayerContentData.setHeaders(headers);
        viewHolder.mOttPlayerManager = new OttPlayerManager(mContext);
        viewHolder.mOttPlayerManager.setOnPlayerStateListener(new OttPlayerManager.PlayerStateListener() {
            @Override
            public void onOttErrorCallBack(final int what, final int extra) {
                //視聴中アニメーションを停止する
                viewHolder.watchingItemAnimation.setVisibility(View.GONE);
                viewHolder.watchingItemAnimation.setBackgroundResource(R.drawable.audio_bar_animation);
                AnimationDrawable frameAnimation = (AnimationDrawable) viewHolder.watchingItemAnimation.getBackground();
                frameAnimation.stop();
            }

            @Override
            public void onOttPreparedCallBack(final MediaPlayer mediaPlayer) {
                //繰り返し再生禁止
                mediaPlayer.setLooping(false);
                //無音再生
                mediaPlayer.setVolume(0f, 0f);
                viewHolder.watchingItemAnimation.setVisibility(View.VISIBLE);
                viewHolder.watchingItemAnimation.setBackgroundResource(R.drawable.audio_bar_animation);
                AnimationDrawable frameAnimation = (AnimationDrawable) viewHolder.watchingItemAnimation.getBackground();
                frameAnimation.start();
                //viewHolder.watchingItemBackground.setVisibility(View.GONE);
            }

            @Override
            public void onOttBuffCompleteCallBack(final MediaPlayer mediaPlayer) {
                //視聴中アニメーションを停止する
                viewHolder.watchingItemAnimation.setVisibility(View.GONE);
                viewHolder.watchingItemAnimation.setBackgroundResource(R.drawable.audio_bar_animation);
                AnimationDrawable frameAnimation = (AnimationDrawable) viewHolder.watchingItemAnimation.getBackground();
                frameAnimation.stop();
            }
        });
        viewHolder.mOttPlayerManager.setSurfaceHolder(viewHolder.mSurfaceView.getHolder());
        viewHolder.mOttPlayerManager.prepareToPlay(ottPlayerContentData);
    }

    /**
     * コンテンツビューを初期化.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * サムネイル.
         */
        ImageView thumbnailUrl;

        /**
         * チャンネルロゴ.
         */
        ImageView channelListLogo;

        /**
         * チャンネルタイトル.
         */
        TextView channelListTitle;

        /**
         * 視聴中アニメーション.
         */
        ImageView watchingItemAnimation;

        /**
         * 視聴中コンテンツ背景.
         */
        ImageView watchingItemBackground;

        /**
         * OTTプレイヤービューマネージャー.
         */
        OttPlayerManager mOttPlayerManager;

        /**
         * NEOプレイヤー.
         */
        SurfaceView mSurfaceView;

        /**
         * 各コンテンツを表示するViewHolder.
         *
         * @param itemView コンテンツView
         */
        ViewHolder(final View itemView) {
            super(itemView);
        }
    }

    /**
     * サムネイル取得処理を止める.
     */
    public void stopConnect() {
        DTVTLogger.start();
        mIsDownloadStop = true;
        if (mThumbnailProvider != null) {
            mThumbnailProvider.stopConnect();
            mThumbnailProvider.removeAllMemoryCache();
        }
    }

    /**
     * 止めたサムネイル取得処理を再度取得可能な状態にする.
     */
    public void enableConnect() {
        DTVTLogger.start();
        mIsDownloadStop = false;
        if (mThumbnailProvider != null) {
            mThumbnailProvider.enableConnect();
        }
    }
}
