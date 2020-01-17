package com.example.detaildemo.data.webapiclient.client;


import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttStopJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttStopResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * OTT再生終了呼び出しWebClient.
 */
public class OttStopWebClient
        extends WebApiBasePlala implements WebApiBasePlala.WebApiBasePlalaCallback {

    /**
     * 通信禁止判定フラグ.
     */
    private boolean mIsCancel;

    /**
     * コールバック.
     */
    public interface OttStopJsonParserCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         *
         * @param ottStopResponse JSONパース後のデータ
         */
        void onOttStopJsonParsed(
                OttStopResponse ottStopResponse);
    }

    /**
     * コールバックのインスタンス.
     */
    private OttStopJsonParserCallback ottStopJsonParserCallback;

    /**
     * コンテキストを継承元のコンストラクタに送る.
     *
     * @param context コンテキスト
     */
    public OttStopWebClient(final Context context) {
        super(context);
    }

    @Override
    public void onAnswer(final ReturnCode returnCode) {
        if (ottStopJsonParserCallback != null) {
            //JSONをパースして、データを返す
            new OttStopJsonParser(ottStopJsonParserCallback)
                    .executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, returnCode.bodyData);
        }
    }

    /**
     * 通信失敗時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onError(final ReturnCode returnCode) {
        if (ottStopJsonParserCallback != null) {
            //エラーが発生したのでヌルを返す
            ottStopJsonParserCallback.onOttStopJsonParsed(null);
        }
    }

    /**
     * OTT再生終了呼び出し取得.
     *
     * @param playToken プレイトークン
     * @param ottStopJsonParserCallback コールバック
     * @return パラメータエラー等が発生した場合はfalse
     */
    public boolean callOttStopApi(final String playToken,
                                  final OttStopJsonParserCallback
                                          ottStopJsonParserCallback) {
        if (mIsCancel) {
            DTVTLogger.error("OttStopWebClient is stopping connection");
            return false;
        }

        //パラメーターのチェック
        if (!checkNormalParameter(playToken, ottStopJsonParserCallback)) {
            //パラメーターがおかしければ通信不能なので、falseで帰る
            return false;
        }

        //コールバックのセット
        this.ottStopJsonParserCallback = ottStopJsonParserCallback;

        //送信用パラメータの作成
        String sendParameter = makeSendParameter(playToken);

        //JSONの組み立てに失敗していれば、falseで帰る
        if (sendParameter.isEmpty()) {
            return false;
        }

        //OTT動画の視聴終了を知らせるために視聴終了APIを呼び出す
        openUrlAddOtt( UrlConstants.WebApiUrl.OTT_PLAYER_STOP_GET_WEB_CLIENT,
                sendParameter, this, null);

        //今のところ失敗していないので、trueを返す
        return true;
    }

    /**
     * 指定されたパラメータをJSONで組み立てて文字列にする.
     *
     * @param playToken プレイトークン
     * @return 組み立て後の文字列
     */
    private String makeSendParameter(final String playToken) {
        JSONObject jsonObject = new JSONObject();
        String answerText;
        try {
            //リクエストパラメータの作成
            jsonObject.put( JsonConstants.OTT_PLAY_TOKEN, playToken);

            answerText = jsonObject.toString();

        } catch (JSONException e) {
            //JSONの作成に失敗したので空文字とする
            answerText = "";
        }

        DTVTLogger.debugHttp(answerText);
        return answerText;
    }

    /**
     * 指定されたパラメータがおかしいかどうかのチェック.
     * @param playToken プレイトークン
     * @param ottStopJsonParserCallback コールバック
     * @return 値がおかしいならばfalse
     */
    private boolean checkNormalParameter(final String playToken,
                                         final OttStopJsonParserCallback ottStopJsonParserCallback) {
        //何もエラーが無い場合はtrue
        return !(TextUtils.isEmpty(playToken) || ottStopJsonParserCallback == null);
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

