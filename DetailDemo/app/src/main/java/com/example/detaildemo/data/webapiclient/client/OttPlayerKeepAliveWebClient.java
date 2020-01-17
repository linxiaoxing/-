package com.example.detaildemo.data.webapiclient.client;


import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.jsonparser.OttPlayerKeepAliveJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerKeepAliveResponse;
import com.example.detaildemo.utils.OttContentUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * OTT再生KeepAlive WebClient.
 */
public class OttPlayerKeepAliveWebClient extends WebApiBasePlala implements WebApiBasePlala.WebApiBasePlalaCallback {

    /** 通信禁止判定フラグ.*/
    private boolean mIsCancel = false;

    /** コールバック.*/
    public interface OttPlayerKeepAliveCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         * @param ottPlayerKeepAliveResponse OTT再生KeepAliveのIFリスポンス情報
         */
        void onOttPlayerKeepAliveParsed(OttPlayerKeepAliveResponse ottPlayerKeepAliveResponse);
    }
    /** コールバックのインスタンス.*/
    private OttPlayerKeepAliveCallback mOttPlayerKeepAliveCallback;

    /**
     * コンテキストを継承元のコンストラクタに送る.
     * @param context コンテキスト
     */
    public OttPlayerKeepAliveWebClient(final Context context) {
        super(context);
    }

    /**
     * 通信成功時のコールバック.
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onAnswer(final ReturnCode returnCode) {
        //JSONをパースして、データを返す
        if (mOttPlayerKeepAliveCallback != null) {
            new OttPlayerKeepAliveJsonParser(mOttPlayerKeepAliveCallback).executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, returnCode.bodyData);
        }
    }

    /**
     * 通信失敗時のコールバック.
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onError(final ReturnCode returnCode) {
        //エラーが発生したのでヌルを返す
        if (mOttPlayerKeepAliveCallback != null) {
            mOttPlayerKeepAliveCallback.onOttPlayerKeepAliveParsed(null);
        }
    }

    /**
     * OTT再生KeepAliveのIF.
     *
     * @param playToken プレートークン
     * @param ottPlayerKeepAliveCallback コールバック
     * @return パラメータエラー等が発生した場合はfalse
     */
    public boolean getOttPlayerKeepAliveApi(final String playToken, final OttPlayerKeepAliveCallback ottPlayerKeepAliveCallback) {
        if (mIsCancel) {
            DTVTLogger.error("OttPlayerKeepAliveWebClient is stopping connection");
            return false;
        }

        if (ottPlayerKeepAliveCallback == null) {
            //コールバックがヌルならばfalse
            return false;
        }

        //パラメーターのチェック
        if (!checkNormalParameter(playToken)) {
            //パラメーターがおかしければ通信不能なので、falseで帰る
            return false;
        }

        //コールバックの設定
        mOttPlayerKeepAliveCallback = ottPlayerKeepAliveCallback;

        //送信用パラメータの作成
        String sendParameter = makeSendParameter(playToken);

        //JSONの組み立てに失敗していれば、falseで帰る
        if (sendParameter.isEmpty()) {
            return false;
        }

        //KeepAliveを呼び出す
        openUrlAddOtt( UrlConstants.WebApiUrl.OTT_PLAYER_KEEP_ALIVE_GET_WEB_CLIENT, sendParameter, this, null);

        //現状失敗は無いのでtrue
        return true;
    }

    /**
     * 指定されたパラメータがおかしいかどうかのチェック.
     *
     * @param playToken 端末を一意に識別する値
     * @return 値がおかしいならばfalse
     */
    private boolean checkNormalParameter(final String playToken) {
        return !TextUtils.isEmpty(playToken);
    }

    /**
     * 指定されたパラメータをJSONで組み立てて文字列にする.
     * @param playToken 端末を一意に識別する値
     * @return 組み立て後の文字列
     */
    private String makeSendParameter(final String playToken) {
        JSONObject jsonObject = new JSONObject();
        String answerText;
        try {
            jsonObject.put( OttContentUtils.OTT_PLAY_START_PLAY_TOKEN, playToken);
            answerText = jsonObject.toString();
        } catch (JSONException e) {
            //JSONの作成に失敗したので空文字とする
            answerText = "";
        }

        DTVTLogger.debugHttp(answerText);
        return answerText;
    }

    /**
     * 通信を止める.
     */
    public void stopConnection() {
        DTVTLogger.start();
        mIsCancel = true;
        stopAllConnections();
    }

    /**
     * 通信可能状態にする.
     */
    public void enableConnection() {
        DTVTLogger.start();
        mIsCancel = false;
    }

}
