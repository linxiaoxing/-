package com.example.detaildemo.data.webapiclient.client;


import android.content.Context;
import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.jsonparser.RealTimeRankingJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RealTimeRankingList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * リアルタイム再生数上位番組取得用WebClient.
 */
public class RealTimeRankingWebClient
        extends WebApiBasePlala implements WebApiBasePlala.WebApiBasePlalaCallback {

    /** 通信禁止判定フラグ.*/
    private boolean mIsCancel = false;
    /** コンテキスト. */
    private Context mContext;

    /**
     * コールバック.
     */
    public interface RealTimeRankingJsonParserCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         *
         * @param realTimeRankingLists JSONパース後のデータ
         */
        void onRealTimeRankingJsonParsed(List<RealTimeRankingList> realTimeRankingLists, ErrorState jsonParseError);
    }

    /**コールバックのインスタンス.*/
    private RealTimeRankingJsonParserCallback mRealTimeRankingJsonParserCallback;

    /**
     * コンテキストを継承元のコンストラクタに送る.
     *
     * @param context コンテキスト
     */
    public RealTimeRankingWebClient(final Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * チャンネル一覧取得.
     *
     * @param pagetLimit                   取得する最大件数(値は1以上)
     * @param type                         絞り込み（複数指定不可）hikariott：ひかりTV多chのOTT type指定なし：全て
     * @param ageReq                       年齢設定値 ※指定なしの場合、1
     * @param filter                       release、testa、demo ※指定なしの場合release
     * @param realTimeRankingJsonParserCallback コールバック
     * @return パラメータエラー等が発生した場合はfalse
     */
    public boolean getRealTimeRankingApi(final int pagetLimit, final String type,
                                         final int ageReq, final String filter,
                                         final RealTimeRankingJsonParserCallback realTimeRankingJsonParserCallback) {

        if (mIsCancel) {
            DTVTLogger.error("RealTimeRankingWebClient is stopping connection");
            return false;
        }

        //パラメーターのチェック
        if (!checkNormalParameter(pagetLimit, type, filter, realTimeRankingJsonParserCallback)) {
            //パラメーターがおかしければ通信不能なので、falseで帰る
            return false;
        }

        //コールバックの設定
        mRealTimeRankingJsonParserCallback = realTimeRankingJsonParserCallback;

        //送信用パラメータの作成
        String sendParameter = makeSendParameter(pagetLimit, type, ageReq, filter);

        //JSONの組み立てに失敗していれば、falseで帰る
        if (sendParameter.isEmpty()) {
            return false;
        }

        //リアルタイム再生数上位番組一覧を呼び出す
        openUrl( UrlConstants.WebApiUrl.REAL_TIME_RANKING_LIST, sendParameter, this);

        //現状失敗は無いのでtrue
        return true;
    }

    /**
     * 指定されたパラメータがおかしいかどうかのチェック.
     *
     * @param pagetLimit                   取得する最大件数(値は1以上)
     * @param type                         絞り込み（複数指定不可）hikariott：ひかりTV多chのOTT type指定なし：全て
     * @param filter                       release、testa、demo ※指定なしの場合release
     * @param realTimeRankingJsonParserCallback コールバック
     * @return 値がおかしいならばfalse
     */
    private boolean checkNormalParameter(final int pagetLimit, final String type, final String filter,
                                         final RealTimeRankingJsonParserCallback realTimeRankingJsonParserCallback) {

        // 各値が下限以下ならばfalse
        if (pagetLimit < 0) {
            return false;
        }
        //文字列がヌルならfalse
        if (type == null) {
            return false;
        }
        if (filter == null) {
            return false;
        }

        //フィルター用の固定値をひとまとめにする
        List<String> filterList = makeStringArry(FILTER_RELEASE, FILTER_TESTA, FILTER_DEMO);

        //指定された文字がひとまとめにした中に含まれるか確認
        if (filterList.indexOf(filter) == -1) {
            //空文字ならば有効なので、それ以外はfalse
            if (!filter.isEmpty()) {
                return false;
            }
        }

        //タイプ用の固定値をひとまとめにする
        List<String> typeList = makeStringArry(TYPE_HIKARI_OTT, TYPE_ALL);

        if (typeList.indexOf(type) == -1) {
            //含まれていないならばfalse
            return false;
        }

        if (realTimeRankingJsonParserCallback == null) {
            //コールバックがヌルならばfalse
            return false;
        }

        //何もエラーが無いのでtrue
        return true;
    }

    /**
     * 指定されたパラメータをJSONで組み立てて文字列にする.
     *
     * @param pagetLimit 取得する最大件数(値は1以上)
     * @param type       絞り込み（複数指定不可）hikariott：ひかりTV多chのOTT type指定なし：全て
     * @param ageReq     年齢制限の値 1から17を指定。範囲外の値は1or17に丸める ※指定なしの場合、1
     * @param filter     release、testa、demo ※指定なしの場合release
     * @return 組み立て後の文字列
     */
    private String makeSendParameter(final int pagetLimit, final String type, final int ageReq, final String filter) {
        JSONObject jsonObject = new JSONObject();
        String answerText;
        try {
            //ページャー部の作成
            if (pagetLimit > 0) {
                JSONObject jsonPagerObject = new JSONObject();
                jsonPagerObject.put( JsonConstants.META_RESPONSE_PAGER_LIMIT, pagetLimit);
                jsonObject.put(JsonConstants.META_RESPONSE_PAGER, jsonPagerObject);
            }

            int intAge = ageReq;
            //数字がゼロの場合は無指定と判断して1にする.また17より大きい場合は17に丸める.
            if (intAge < WebApiBasePlala.AGE_LOW_VALUE) {
                intAge = 1;
            } else if (intAge > WebApiBasePlala.AGE_HIGH_VALUE) {
                intAge = 17;
            }
            jsonObject.put(JsonConstants.META_RESPONSE_AGE_REQ, intAge);

            if (!filter.isEmpty()) {
                jsonObject.put(JsonConstants.META_RESPONSE_FILTER, filter);
            }

            answerText = jsonObject.toString();

        } catch (JSONException e) {
            //JSONの作成に失敗したので空文字とする
            answerText = "";
        }
        DTVTLogger.debugHttp(answerText);
        return answerText;
    }

    @Override
    public void onAnswer(ReturnCode returnCode) {
        //JSONをパースして、データを返す
        new RealTimeRankingJsonParser(mRealTimeRankingJsonParserCallback).executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, returnCode.bodyData);
    }

    @Override
    public void onError(ReturnCode returnCode) {
        //エラーが発生したのでヌルを返す
        mRealTimeRankingJsonParserCallback.onRealTimeRankingJsonParsed(null, null);
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
