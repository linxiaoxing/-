package com.example.detaildemo.data.provider;


import android.content.Context;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.data.webapiclient.client.OttPlayerKeepAliveWebClient;
import com.example.detaildemo.data.webapiclient.client.OttPlayerStartWebClient;
import com.example.detaildemo.data.webapiclient.client.OttStopWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerKeepAliveResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerStartResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttStopResponse;
import com.example.detaildemo.utils.OttContentUtils;
import com.example.detaildemo.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Ottプレイヤーコントローラー.
 */
public class OttPlayerDataProvider implements OttPlayerStartWebClient.OttPlayerStarCallback,
        OttPlayerKeepAliveWebClient.OttPlayerKeepAliveCallback, OttStopWebClient.OttStopJsonParserCallback {

    /** コンテキスト. */
    private Context mContext;
    /** Ott再生用コールバック. */
    private OttPlayerProviderCallback mOttPlayerProviderCallback;
    /** KeepAlive用タイマー. */
    private Timer mTimer;
    /** KeepAlive用タイマー秒数. */
    private static final int KEEP_ALIVE_TIME_OUT = 60 * 1000;
    /** Ottプレイヤー再生停止WebClient. */
    private OttStopWebClient mOttStopWebClient = null;

    /**
     * Ott再生用コントラクター.
     * @param context コンテキスト
     */
    public OttPlayerDataProvider(final Context context) {
        this.mContext = context;
        if (context instanceof OttPlayerProviderCallback) {
            this.mOttPlayerProviderCallback = (OttPlayerProviderCallback) context;
        }
    }

    /**
     * Ott再生用コールバック.
     */
    public interface OttPlayerProviderCallback {
        /**
         * Ott開始IFコールバック.
         * @param ottPlayerStartResponse Ott開始リスポンス情報
         */
        void onStartPlayerCallback(OttPlayerStartResponse ottPlayerStartResponse);

        /**
         * Ott停止IFコールバック.
         * @param isSuccess 成功フラグ
         */
        void onStopPlayerCallback(boolean isSuccess);

        /**
         * Ott KeepAliveIFコールバック.
         * @param isAlive isAlive
         */
        void onKeepAliveCallback(boolean isAlive);
    }

    /**
     * Ottプレイヤー再生開始.
     * @param playType 再生種別
     * @param quality 画質
     * @param viewContinueFlg 0:新規視聴時 1:継続視聴時
     * @param contentList 再生情報のリスト
     */
    public void startPlay(final int playType, final int quality, final int viewContinueFlg, final List<HashMap<String, String>> contentList) {
        OttPlayerStartWebClient ottPlayerStartWebClient = new OttPlayerStartWebClient(mContext);
        boolean result = ottPlayerStartWebClient.getOttPlayerStartApi(getEncryptUuid(mContext), playType,
                OttContentUtils.OTT_PLAY_START_PROTOCOL_1, quality,
                OttContentUtils.OTT_PLAY_START_AVAIL_STATUS_DEFAULT, viewContinueFlg,  contentList, this);
        if (!result) {
            if (mOttPlayerProviderCallback != null) {
                mOttPlayerProviderCallback.onStartPlayerCallback(null);
            }
            DTVTLogger.debug("OttPlayerDataProvider startPlay getOttPlayerStartApi result is false");
        }
    }

    /**
     * OttプレイヤーkeepAlive.
     */
    private void keepPlayAlive() {
        OttPlayerKeepAliveWebClient ottPlayerKeepAliveWebClient = new OttPlayerKeepAliveWebClient(mContext);
        boolean result = ottPlayerKeepAliveWebClient.getOttPlayerKeepAliveApi(getPlayToken(mContext), this);
        if (!result) {
            DTVTLogger.debug("OttPlayerDataProvider keepPlayAlive getOttPlayerKeepAliveApi result is false");
        }
    }

    /**
     * Ottプレイヤー再生停止.
     */
    public void stopPlay() {
        stopTimer();
        if (!TextUtils.isEmpty(getPlayToken(mContext))) {
            mOttStopWebClient = new OttStopWebClient(mContext);
            boolean result = mOttStopWebClient.callOttStopApi(getPlayToken(mContext), this);
            if (!result) {
                if (mOttPlayerProviderCallback != null) {
                    mOttPlayerProviderCallback.onStopPlayerCallback(false);
                }
                DTVTLogger.debug("OttPlayerDataProvider stopPlay callOttStopApi result is false");
            }
        } else {
            if (mOttPlayerProviderCallback != null) {
                mOttPlayerProviderCallback.onStopPlayerCallback(true);
            }
        }
    }

    /**
     * 暗号化のUUID取得.
     * @param context コンテキスト
     * @return 暗号化後の文字列
     */
    private String getEncryptUuid(final Context context) {
        // 保存されたUUID取得
        String uuid = getUUID(context);
        if (null == uuid) {
            // UUID生成、保存
            uuid = UUID.randomUUID().toString();
            saveUUID(context, uuid);
        }
        // -を破棄
        uuid = uuid.replaceAll("-", "");

        // 32ビット毎に4つに分ける
        String uuid1 = uuid.substring(0, 8);
        String uuid2 = uuid.substring(8, 16);
        String uuid3 = uuid.substring(16, 24);
        String uuid4 = uuid.substring(24);

        // 16進数から2進数の文字列へ変更,
        // 1ビット左へシフト（桁あふれ分は末尾に埋めする）
        uuid1 = getBinaryFromHex(uuid1);
        uuid2 = getBinaryFromHex(uuid2);
        uuid3 = getBinaryFromHex(uuid3);
        uuid4 = getBinaryFromHex(uuid4);

        // 2進数から10進数の文字列へ変更
        // 10桁に満たない場合は先頭から0埋めする
        uuid1 = getDecFromBinary(uuid1);
        uuid2 = getDecFromBinary(uuid2);
        uuid3 = getDecFromBinary(uuid3);
        uuid4 = getDecFromBinary(uuid4);

        uuid = uuid1 + uuid2 + uuid3 + uuid4;

        return uuid;
    }

    /**
     * 16進数から2進数の文字列へ変更.
     * 1ビット左へシフト（桁あふれ分は末尾に埋めする）
     * @param hex 16進数文字列
     * @return 2進数文字列
     */
    private String getBinaryFromHex(final String hex) {
        // 16進数から2進数の文字列へ変更
        String binary = Long.toBinaryString(Long.parseLong(hex, 16));
        // 32桁に満たない場合は先頭から0埋めする
        while (binary.length() < 32) {
            binary = (new StringBuilder()).append("0")
                    .append(binary)
                    .toString();
        }
        // 1ビット左へシフト（桁あふれ分は末尾を埋めする）
        binary = binary.substring(1) + binary.substring(0, 1);

        return binary;
    }

    /**
     * 2進数から10進数の文字列へ変更.
     * 10桁に満たない場合は先頭から0埋めする
     * @param binary 2進数文字列
     * @return 10進数文字列
     */
    private String getDecFromBinary(final String binary) {
        // 2進数から10進数の文字列へ変更
        String dec = Long.toString(Long.parseLong(binary, 2));
        // 10桁に満たない場合は先頭から0埋めする
        while (dec.length() < 10) {
            dec = (new StringBuilder()).append("0")
                    .append(dec)
                    .toString();
        }
        return dec;
    }

    /**
     * UUID取得.
     * @param context コンテキスト
     * @return uuid
     */
    private String getUUID(final Context context) {
        return SharedPreferencesUtils.getUuid(context);
    }

    /**
     * UUID保存.
     * @param context コンテキスト
     * @param uuid uuid
     */
    private void saveUUID(final Context context, final String uuid) {
        // UUID生成、保存
        SharedPreferencesUtils.setUuid(context, uuid);
    }

    /**
     * プレイトークン保存.
     * @param context コンテキスト
     * @param playToken プレイトークン
     */
    private void savePlayToken(final Context context, final String playToken) {
        SharedPreferencesUtils.saveOttPlayToken(context, playToken);
    }

    /**
     * 保存したOTTリニアコンテンツ用プレイトークンの取得.
     * @param context コンテキスト
     * @return プレイトークン
     */
    private String getPlayToken(final Context context) {
        return SharedPreferencesUtils.getOttPlayToken(context);
    }

    /**
     * 保存したOTTリニアコンテンツ用プレイトークンのを削除.
     */
    private void deletePlayToken() {
        SharedPreferencesUtils.deleteOttPlayToken(mContext);
    }

    /**
     * keepAliveタイマー.
     */
    private void startKeepAliveTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        final TimerTask task = new TimerTask() {

            @Override
            public void run() {
                DTVTLogger.debug("startKeepAliveTimer");
                keepPlayAlive();
            }
        };
        mTimer.scheduleAtFixedRate(task, KEEP_ALIVE_TIME_OUT, KEEP_ALIVE_TIME_OUT);
    }

    /**
     * keepAliveタイマー停止.
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onOttPlayerStartJsonParsed(final OttPlayerStartResponse ottPlayerStartResponse) {
        if (ottPlayerStartResponse != null) {
            if (ottPlayerStartResponse.getPlayToken() != null) {
                startKeepAliveTimer();
                savePlayToken(mContext, ottPlayerStartResponse.getPlayToken());
            }
        }
        if (mOttPlayerProviderCallback != null) {
            // Data Test
            OttPlayerStartResponse mOttPlayerStartResponse = new OttPlayerStartResponse();
            List<OttPlayerStartData> ottPlayerStartDatas = new ArrayList<>();
            OttPlayerStartData ottPlayerStartData = new OttPlayerStartData();
            ottPlayerStartData.setPlayUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
            ottPlayerStartData.setContentId("0451");
            ottPlayerStartData.setKind(OttContentUtils.OTT_PLAY_START_KIND_MAIN);
            ottPlayerStartDatas.add(ottPlayerStartData);
            mOttPlayerStartResponse.setOttPlayerStartData(ottPlayerStartDatas);
            mOttPlayerProviderCallback.onStartPlayerCallback(mOttPlayerStartResponse);
        }
    }

    @Override
    public void onOttPlayerKeepAliveParsed(final OttPlayerKeepAliveResponse ottPlayerKeepAliveResponse) {
        if (mOttPlayerProviderCallback == null) {
            return;
        }
        //KeepAlive無効となる場合の判定基準（エラー番号）QA中　
        if (ottPlayerKeepAliveResponse != null && OttContentUtils.OTT_PLAY_STATUS_NG.equals(ottPlayerKeepAliveResponse.getStatus())) {
            mOttPlayerProviderCallback.onKeepAliveCallback(false);
            deletePlayToken();
            return;
        }
        mOttPlayerProviderCallback.onKeepAliveCallback(true);
    }

    @Override
    public void onOttStopJsonParsed(final OttStopResponse ottStopResponse) {
        boolean result = true;
        if (!DtvtConstants.ErrorType.SUCCESS.equals(mOttStopWebClient.getError().getErrorType())) {
            //ネットワークエラー以外はplayTokenを消す
            if (!DtvtConstants.ErrorType.NETWORK_ERROR.equals(mOttStopWebClient.getError().getErrorType())) {
                deletePlayToken();
            }
            result = false;
        }
        if (mOttPlayerProviderCallback != null) {
           // mOttPlayerProviderCallback.onStopPlayerCallback(result);
            mOttPlayerProviderCallback.onStopPlayerCallback(true);
        }
    }
}
