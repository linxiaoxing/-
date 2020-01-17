package com.example.detaildemo.data.manager;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.provider.data.OttPlayerContentData;

import java.io.IOException;

///**
// * Ott再生マネージャー.
// */
//public class OttPlayerManager implements OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener,
//        OnInfoListener, OnTimedTextListener, OnQoEAlertListener, OnLicenseAcquiredListener,
//        OnErrorListener, OnBufferingUpdateListener, SurfaceHolder.Callback {
//
//    /** メディアプレイヤー.*/
//    private MediaPlayer mMediaPlayer;
//    /** コンテキスト.*/
//    private Context mContext;
//    /** プレイヤーステータス.*/
//    private PlayerStateListener mPlayerStateListener;
//    /** プレイヤーステータス.*/
//    private OttPlayerContentData mOttPlayerContentData;
//
//    /**再生状態.*/
//    public enum PlayerBackStatus {
//        /**未知.*/
//        UNKNOWN,
//        /**アクティベーション.*/
//        PLAYING,
//        /**一時停止.*/
//        PAUSED,
//        /**失敗.*/
//        FAILED,
//        /**停止.*/
//        STOPPED
//    }
//
//    /**
//     * 再生監視リスナー.
//     */
//    public interface PlayerStateListener {
//        /**
//         * エラーコールバック.
//         * @param what エラーコード
//         * @param extra extraCode
//         */
//        void onOttErrorCallBack(final int what, final int extra);
//        /**
//         * エラーコールバック.
//         * @param mediaPlayer mediaPlayer
//         */
//        void onOttPreparedCallBack(final MediaPlayer mediaPlayer);
//        /**
//         * エラーコールバック.
//         * @param mediaPlayer mediaPlayer
//         */
//        void onOttBuffCompleteCallBack(final MediaPlayer mediaPlayer);
//    }
//
//    /**
//     * 再生用ビュー通知リスナー.
//     */
//    public interface OnPlayerPreparedListener {
//        /**
//         * 再生用ビューコールバック.
//         */
//        void onPreparedCallBack();
//    }
//
//    /**再生ローディング状態.*/
//    public enum PlayeLoadingStatus {
//        /**未知.*/
//        UNKNOWN,
//        /**アクティベーション.*/
//        PREPARE,
//        /**再生可能.*/
//        PLAYABLE
//    }
//
//    /**
//     * コントラクター.
//     * @param context コンテキスト
//     */
//    public OttPlayerManager(final Context context) {
//        this.mContext = context;
//    }
//
//    /**
//     * プレイヤーステータスリスナー設定.
//     * @param playerStateListener playerStateListener
//     */
//    public void setOnPlayerStateListener(final PlayerStateListener playerStateListener) {
//        mPlayerStateListener = playerStateListener;
//    }
//
//    /**
//     * surfaceHolder設定.
//     * @param surfaceHolder surfaceHolder
//     */
//    public void setSurfaceHolder(final SurfaceHolder surfaceHolder) {
//        surfaceHolder.addCallback(this);
//    }
//
//    /**
//     * 再生用パラメータ設定.
//     * @param ottPlayerContentData 再生用パラメータ
//     */
//    public void prepareToPlay(final OttPlayerContentData ottPlayerContentData) {
//        mOttPlayerContentData = ottPlayerContentData;
//    }
//
//    /**
//     * プレイヤー情報設定.
//     * @param surfaceHolder surfaceHolder
//     */
//    private void initMediaPlayer(final SurfaceHolder surfaceHolder) {
//        if (isInteractive()) {
//            if (mMediaPlayer == null) {
//                mMediaPlayer = new MediaPlayer();
//            } else {
//                mMediaPlayer.reset();
//            }
//            mMediaPlayer.setOnPreparedListener(this);
//            mMediaPlayer.setOnVideoSizeChangedListener(this);
//            mMediaPlayer.setOnCompletionListener(this);
//            mMediaPlayer.setOnErrorListener(this);
//            mMediaPlayer.setOnBufferingUpdateListener(this);
//            mMediaPlayer.setOnInfoListener(this);
//            mMediaPlayer.setOnTimedTextListener(this);
//            mMediaPlayer.setOnQoEAlertListener(this);
//            mMediaPlayer.setOnLicenseAcquiredListener(this);
//            try {
//                mMediaPlayer.setDataSource(mContext, Uri.parse(mOttPlayerContentData.getPlayerUrl()), mOttPlayerContentData.getHeaders());
//            } catch (IOException | IllegalArgumentException | IllegalStateException e) {
//                DTVTLogger.debug("ott player setDataSource failed " + e);
//                if (mPlayerStateListener != null) {
//                    mPlayerStateListener.onOttErrorCallBack(0, 0);
//                }
//                return;
//            }
//            setLegacyDrmConfigurations(mMediaPlayer, mOttPlayerContentData);
//            mMediaPlayer.setDisplay(surfaceHolder);
//            mMediaPlayer.setScreenOnWhilePlaying(true);
//            PlayerConfiguration conf = new PlayerConfiguration();
//            mMediaPlayer.prepareAsync(conf);
//        } else {
//            DTVTLogger.debug("initPlayer is failed because not interactive");
//            if (mPlayerStateListener != null) {
//                mPlayerStateListener.onOttErrorCallBack(0, 0);
//            }
//        }
//    }
//
//    /**
//     * デバイスがインタラクティブ状態であるかどうか.
//     * @return デバイスがインタラクティブな状態にある場合、trueを返します
//     */
//    private boolean isInteractive() {
//        if (mContext == null) {
//            return false;
//        }
//        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        return pm.isInteractive();
//    }
//
//    /**
//     * メディア属性（format）設定.
//     * @param player メディアプレイヤー
//     * @param ottPlayerContentData 再生用パラメータ
//     */
//    private void setLegacyDrmConfigurations(final MediaPlayer player, final OttPlayerContentData ottPlayerContentData) {
//        //playRead
//        player.setDrmConfig("LICACQ_SERVER_URL", ottPlayerContentData.getPlayReadyLicenseAcquisitionUrl());
//        if (!TextUtils.isEmpty(ottPlayerContentData.getPlayReadyLicenseAcquisitionUrl())) {
//            player.setDrmConfig("LICACQ_HTTP_HEADER", ottPlayerContentData.getPlayReadyLicenseAcquisitionUrl());
//        } else {
//            player.setDrmConfig("LICACQ_HTTP_HEADER", "Content-Type: text/xml; charset=utf-8\r\n"
//                    + "SOAPAction: \"http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense\"\r\n");
//        }
//        player.setDrmConfig("LICACQ_CUSTOM_DATA", ottPlayerContentData.getPlayReadyCustomData());
//        //widevine
//        player.setDrmConfig("WV_LICACQ_SERVER_URL", ottPlayerContentData.getWidevineLicenseAcquisitionUrl());
//        player.setDrmConfig("WV_LICACQ_CUSTOM_DATA", ottPlayerContentData.getWidevineCustomData());
//        if (!TextUtils.isEmpty(ottPlayerContentData.getWidevineHttpHeaders())) {
//            player.setDrmConfig("WV_LICACQ_HTTP_HEADER", ottPlayerContentData.getWidevineHttpHeaders());
//        }
//        player.setDrmConfig("SUPPORTED_MEDIA_DRM_SCHEME", ottPlayerContentData.getSystemId());
//    }
//
//    /**
//     * 再生開始.
//     */
//    public void play() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
//    }
//
//    /**
//     * 再生中判定.
//     */
//    public boolean isPlaying() {
//        return mMediaPlayer != null && mMediaPlayer.isPlaying();
//    }
//
//    /**
//     * 再生一時停止.
//     */
//    public void pause() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.pause();
//        }
//    }
//
//    /**
//     * 再生停止.
//     */
//    public void stop() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }
//
//    /**
//     * プレイヤーリセット.
//     */
//    public void release() {
//        setOnPlayerStateListener(null);
//    }
//
//    @Override
//    public void surfaceCreated(final SurfaceHolder holder) {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.setDisplay(holder);
//        } else {
//            initMediaPlayer(holder);
//        }
//    }
//
//    @Override
//    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
//        DTVTLogger.debug("OttPlayerManager surfaceChanged:" + holder);
//    }
//
//    @Override
//    public void surfaceDestroyed(final SurfaceHolder holder) {
//        DTVTLogger.debug("OttPlayerManager surfaceDestroyed:" + holder);
//        stop();
//        release();
//    }
//
//    @Override
//    public void onBufferingUpdate(final MediaPlayer mediaPlayer, final int i) {
//        DTVTLogger.debug("OttPlayerManager onBufferingUpdate:" + i);
//        if (isPlaying() && i == 100) {
//            if (mPlayerStateListener != null) {
//                mPlayerStateListener.onOttBuffCompleteCallBack(mediaPlayer);
//            }
//        }
//    }
//
//    @Override
//    public void onCompletion(final MediaPlayer mediaPlayer) {
//        DTVTLogger.debug("OttPlayerManager onCompletion:" + mediaPlayer);
//    }
//
//    @Override
//    public boolean onError(final MediaPlayer mediaPlayer, final int what, final int extra) {
//        DTVTLogger.debug("OttPlayerManager onError:" + " what: " + what + " extra: " + extra);
//        if (mPlayerStateListener != null) {
//            mPlayerStateListener.onOttErrorCallBack(what, extra);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onInfo(final MediaPlayer mediaPlayer, final int i, final int i1) {
//        return false;
//    }
//
//    @Override
//    public boolean onInfo(final MediaPlayer mediaPlayer, final int i, final int i1, final Object o) {
//        return false;
//    }
//
//    @Override
//    public void onLicenseAcquired(final int i, final HttpResponse httpResponse) {
//
//    }
//
//    @Override
//    public void onPrepared(final MediaPlayer mediaPlayer) {
//        DTVTLogger.debug("OttPlayerManager onPrepared");
//        if (mPlayerStateListener != null) {
//            mPlayerStateListener.onOttPreparedCallBack(mediaPlayer);
//        }
//        play();
//    }
//
//    @Override
//    public void onQoEAlert(final MediaPlayer mediaPlayer, final String s) {
//
//    }
//
//    @Override
//    public void onTimedText(final MediaPlayer mediaPlayer, final TimedText timedText) {
//
//    }
//
//    @Override
//    public void onVideoSizeChanged(final MediaPlayer mediaPlayer, final int i, final int i1) {
//
//    }
//}