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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.provider.ThumbnailProvider;
import com.example.detaildemo.data.provider.UserInfoDataProvider;
import com.example.detaildemo.data.provider.data.MobileDeviceInfo;
import com.example.detaildemo.data.provider.data.OttPlayerContentData;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.data.webapiclient.download.ThumbnailDownloadTask;
import com.example.detaildemo.fragment.DtvContentsBroadcastFragment;
import com.example.detaildemo.player.OttPlayerManager;
import com.example.detaildemo.utils.ContentDetailUtils;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.OttContentUtils;
import com.example.detaildemo.utils.SharedPreferencesUtils;
import com.example.detaildemo.utils.StringUtils;
import com.example.detaildemo.utils.UserAgentUtils;
import com.example.detaildemo.utils.UserInfoUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DtvContentsBroadcastAdapter.
 */
public class DtvContentsBroadcastAdapter extends RecyclerView.Adapter<DtvContentsBroadcastAdapter.DtvContentsBroadcastHolder> {

    /** Inflater.*/
    private final LayoutInflater mInflater;
    /** コンテンツリスト.*/
    private final List<ContentsData> mContentList;
    /** コンテキスト.*/
    private final Context mContext;
    /** サムネイルデータプロバイダー.*/
    private final ThumbnailProvider mThumbnailProvider;
    /** BroadcastItemClickCallback.*/
    private BroadcastItemClickCallback mBroadcastItemClickCallback;
    /**ダウンロード禁止判定フラグ.*/
    private boolean mIsDownloadStop = false;
    /**DRMコンテンツ再生中フラグ.*/
    private boolean mIsDrmContentPlaying = false;
    /**端末情報*/
    private MobileDeviceInfo mDeviceInfo;
    /**ユーザ契約情報*/
    private String contractInfo;
    /** 再利用のビュー最大count.*/
    private int mMaxItemCount = 0;

    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     */
    public DtvContentsBroadcastAdapter(final Context context, final List<ContentsData> contentsDataList) {
        mInflater = LayoutInflater.from(context);
        this.mContentList = contentsDataList;
        this.mContext = context;
        mThumbnailProvider = new ThumbnailProvider(context, ThumbnailDownloadTask.ImageSizeType.BROADCAST);
        //端末情報
        this.mDeviceInfo = SharedPreferencesUtils.getSharedPreferencesSettingFileMobileCategory(mContext);
        //ユーザ契約情報
        this.contractInfo = UserInfoUtils.getUserContractInfo(
                SharedPreferencesUtils.getSharedPreferencesUserInfo(mContext));
    }

    @NonNull
    @Override
    public DtvContentsBroadcastHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = mInflater.inflate(R.layout.dtv_contents_broadcast_recyclerview_item, viewGroup, false);
        view.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        int width = view.getWidth();

                        View thumbnailArea = view.findViewById(R.id.recycler_view_item_relative_layout);
                        ViewGroup.LayoutParams layoutParams = thumbnailArea.getLayoutParams();
                        layoutParams.width = width;
                        layoutParams.height = width / ContentDetailUtils.SCREEN_RATIO_WIDTH_16 * ContentDetailUtils.SCREEN_RATIO_HEIGHT_9;
                        thumbnailArea.setLayoutParams(layoutParams);
                        return true;
                    }
                });
        mMaxItemCount++;
        return new DtvContentsBroadcastHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DtvContentsBroadcastHolder viewHolder, final  int i) {
        //初期化
        viewHolder.imageView.setVisibility(View.GONE);
        viewHolder.imageView.setAlpha(ContentDetailUtils.THUMBNAIL_INITIAL_ALPHA );
        viewHolder.mSurfaceView.setVisibility(View.GONE);
        viewHolder.watchingItemAnimation.setVisibility(View.GONE);
        viewHolder.contractText.setVisibility(View.GONE);
        viewHolder.newline.setVisibility(View.GONE);
        viewHolder.linearLayout.setClickable(true);

        final ContentsData contentsData = mContentList.get(i);

        //年齢制限フラグ
        UserInfoDataProvider userInfoDataProvider = new UserInfoDataProvider(mContext);
        boolean isParental = StringUtils.isParental(userInfoDataProvider.getUserAge(), contentsData.getRValue());
        //TODO TEST
        if (i == 2) {
            isParental = true;
        }
        if (i == 3) {
            contentsData.setViewIngType(ContentUtils.ViewIngType.DISABLE_CHANNEL_WATCH_AGREEMENT_DISPLAY);
        }
        if (isParental) {
            //年齢制御対象の場合
            //あらすじを「＊＊＊」でマスクする
            //タップしても、コンテンツ詳細に遷移しない
            viewHolder.titleName.setText(StringUtils.returnAsterisk(mContext));
            viewHolder.linearLayout.setClickable(false);
        } else {
            //年齢制御対象ではない場合
            viewHolder.titleName.setText(contentsData.getTitle());

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mBroadcastItemClickCallback.onBroadcastItemClick(contentsData);
                }
            });
        }

        viewHolder.titleName.post(new Runnable() {
            @Override
            public void run() {
                //タイトルが1行に収まる場合はセルサイズは変えずにチャンネル名を上寄せとする
                if (viewHolder.titleName.getLineCount() <= 1) {
                    //コンテンツの間のスペースを一致するため
                    viewHolder.newline.setVisibility(View.VISIBLE);
                }
                String channelName = contentsData.getChannelName();
                viewHolder.channelName.setText(channelName);
            }
        });

        //サムネイルかプレイヤーかを表示する
        if (!mIsDownloadStop) {
            ContentUtils.DisplayType displayType = getContentDisplayType(contentsData, isParental);
            if (displayType == ContentUtils.DisplayType.PLAYER) {
                //TODO:再生関連はDREM-8828で対応予定
                viewHolder.mSurfaceView.setVisibility(View.VISIBLE);
                startPlay(viewHolder);
            } else {
                if (displayType == ContentUtils.DisplayType.CONTRACT) {
                    viewHolder.contractText.setVisibility(View.VISIBLE);
                    viewHolder.contractText.setText(mContext.getResources().getString(R.string.contents_detail_contract_request));
                    //タップ不可
                    viewHolder.linearLayout.setClickable(false);
                    //サムネイル画像にシャドウをかける
                    viewHolder.imageView.setAlpha(ContentDetailUtils.THUMBNAIL_SHADOW_ALPHA);
                }
                String thumbnail = contentsData.getThumURL();
                if (TextUtils.isEmpty(thumbnail)) {
                    thumbnail = contentsData.getThumURL();
                }

                // サムネイル表示
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.imageView.setTag(thumbnail);
                mThumbnailProvider.setMaxQueueCount(mMaxItemCount);
                Bitmap bitmap = mThumbnailProvider.getThumbnailImage(viewHolder.imageView, thumbnail);
                if (bitmap != null) {
                    viewHolder.imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * プレイヤービュー初期化.
     * TODO:再生関連はDREM-8828で対応予定
     * @param viewHolder holder
     */
    private void startPlay(final DtvContentsBroadcastHolder viewHolder) {
        OttPlayerContentData ottPlayerContentData = new OttPlayerContentData();
        ottPlayerContentData.setSystemId(OttContentUtils.OTT_PLAY_SYSTEM_ID);
        ottPlayerContentData.setPlayerUrl("https://www.radiantmediaplayer.com/media/bbb-360p.mp4");
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
            }

            @Override
            public void onOttBuffCompleteCallBack(final MediaPlayer mediaPlayer) {

            }
        });
        viewHolder.mOttPlayerManager.setSurfaceHolder(viewHolder.mSurfaceView.getHolder());
        viewHolder.mOttPlayerManager.prepareToPlay(ottPlayerContentData);
    }

    /**
     * コンテント表示タイプを取得する.
     * @param contentsData コンテント情報
     * @param isParental 年齢制御フラグ
     * @return  コンテント表示タイプ
     */
    private ContentUtils.DisplayType getContentDisplayType(ContentsData contentsData, boolean isParental) {
        if (ContentUtils.isContractWireDisplay(contentsData.getViewIngType())) {
            //※要購入の場合、「契約が必要です」を表示する
            return ContentUtils.DisplayType.CONTRACT;
        }

        //5G端末はH4d契約者のみ無音自動再生する
        // if (!mDeviceInfo.is5GSupported()) {
        if (mDeviceInfo.is5GSupported()) {
            //5G端末じゃない場合
            return ContentUtils.DisplayType.THUMNAIL;
        }

//        if (!UserInfoUtils.CONTRACT_INFO_H4D.equals(contractInfo)) {
//            //H4d契約者じゃない場合
//            return ContentUtils.DisplayType.THUMNAIL;
//        }

        if (contentsData.getTitle().contains(ContentUtils.OFF_THE_AIR)) {
            //タイトルに「休止」が含まれていた場合サムネイルを表示する
            return ContentUtils.DisplayType.THUMNAIL;
        }

        if (isParental) {
            //年齢制御
            return ContentUtils.DisplayType.THUMNAIL;
        }

        // TODO:プレイヤーでDRMコンテンツ再生中にNOW ON AIRタブ内で別のDRMコンテンツがあった場合はサムネイルを表示する
        if (ContentUtils.FLG_OTT_DRM_ONE.equals(contentsData.getOttDrmflg())) {
            //再生中のDRMコンテンツがあった場合
            if (mIsDrmContentPlaying) {
                return ContentUtils.DisplayType.THUMNAIL;
            } else {
                mIsDrmContentPlaying = true;
            }
        }

        return ContentUtils.DisplayType.PLAYER;
    }

    /**
     * setBroadcastItemClickListener.
     * @param broadcastItemClickListener broadcastItemClickListener
     */
    public void setBroadcastItemClickListener(final DtvContentsBroadcastFragment broadcastItemClickListener) {
        this.mBroadcastItemClickCallback = broadcastItemClickListener;
    }

    /**
     * サムネイルタップコールバック.
     */
    public interface BroadcastItemClickCallback {
        /**
         * サムネイルタップコールバック.
         * @param contentsData contentsData
         */
        void onBroadcastItemClick(ContentsData contentsData);
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    /**
     * DtvContentsBroadcastHolder.
     */
    public class DtvContentsBroadcastHolder extends RecyclerView.ViewHolder {
        /**サムネイル.*/
        ImageView imageView;
        /**タイトル名.*/
        TextView titleName;
        /**チャンネル名.*/
        TextView channelName;
        /** レイアウト.*/
        LinearLayout linearLayout;
        /** レイアウト.*/
        RelativeLayout relativeLayout;
        //TODO:再生関連はDREM-8828で対応予定 start
        /** NEOプレイヤー.*/
        SurfaceView mSurfaceView;
        /** 視聴中アニメーション.*/
        ImageView watchingItemAnimation;
        //TODO:再生関連はDREM-8828で対応予定 end
        /** 「契約が必要です」.*/
        TextView contractText;
        //TODO:再生関連はDREM-8828で対応予定 start
        /** OTTプレイヤービューマネージャー. */
        OttPlayerManager mOttPlayerManager;
        /** プレイヤー再生情報. */
        OttPlayerStartData mOttPlayerStartData;
        //TODO:再生関連はDREM-8828で対応予定 end
        /** 改行コードを表示.*///
        TextView newline;

        /**
         * 各コンテンツを表示するViewHolder.
         *
         * @param itemView コンテンツView
         */
        public DtvContentsBroadcastHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.dtv_contents_recycler_view_thumbnail);
            titleName = itemView.findViewById(R.id.dtv_contents_recycler_view_title_name);
            channelName = itemView.findViewById( R.id.dtv_contents_recycler_view_channel_name);
            linearLayout = itemView.findViewById(R.id.recycler_view_item_linear_layout);
            relativeLayout = itemView.findViewById(R.id.recycler_view_item_relative_layout);
            mSurfaceView = itemView.findViewById(R.id.dtv_contents_recycler_view_player);
            watchingItemAnimation = itemView.findViewById(R.id.tv_player_channel_list_animation);
            contractText = itemView.findViewById(R.id.contract_text);
            newline = itemView.findViewById(R.id.dtv_contents_recycler_view_new_line);
        }
    }
    /**
     * 通信を止める.
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
     * 通信を許可する.
     */
    public void enableConnect() {
        DTVTLogger.start();
        mIsDownloadStop = false;
        if (mThumbnailProvider != null) {
            mThumbnailProvider.enableConnect();
        }
    }
}
