package com.example.detaildemo.data.webapiclient;

import android.content.Context;
import android.os.Handler;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.bean.ClipKeyListRequest;
import com.example.detaildemo.data.bean.ClipKeyListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.ClipKeyListJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.JsonParserThread;
import com.example.detaildemo.utils.ContentUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ClipKeyListWebClient
        extends WebApiBasePlala implements WebApiBasePlala.WebApiBasePlalaCallback, JsonParserThread.JsonParser {
    /**
     * 通信禁止判定フラグ.
     */
    private boolean mIsCancel = false;
    /** コンテキスト. */
    private Context mContext;

    /**
     * コンテキストを継承元のコンストラクタに送る.
     *
     * @param context コンテキスト
     */
    public ClipKeyListWebClient(final Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * コールバック.
     */
    public interface TvClipKeyListJsonParserCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         *
         * エラー情報がうまく伝わらない場合があったので、エラー情報を追加
         * @param clipKeyListResponse JSONパース後のデータ
         * @param errorState エラー情報
         */
        void onTvClipKeyListJsonParsed(final ClipKeyListResponse clipKeyListResponse,
                                       final ErrorState errorState);
    }

    /**
     * コールバック.
     */
    public interface VodClipKeyListJsonParserCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         *
         * エラー情報がうまく伝わらない場合があったので、エラー情報を追加
         * @param clipKeyListResponse JSONパース後のデータ
         * @param errorState エラー情報
         */
        void onVodClipKeyListJsonParsed(ClipKeyListResponse clipKeyListResponse
                ,final ErrorState errorState);
    }

    /**
     * コールバックのインスタンス(TV).
     */
    private TvClipKeyListJsonParserCallback mTvClipKeyListJsonParserCallback;
    /**
     * コールバックのインスタンス(VOD).
     */
    private VodClipKeyListJsonParserCallback mVodClipKeyListJsonParserCallback;

    @Override
    public void onParserFinished(final Object parsedData) {
        if (mIsCancel) {
            return;
        }
        //パース後のデータを返す
        if (null != mTvClipKeyListJsonParserCallback) {
            mTvClipKeyListJsonParserCallback.onTvClipKeyListJsonParsed(
                    (ClipKeyListResponse) parsedData, getError());
        }
        //パース後のデータを返す
        if (null != mVodClipKeyListJsonParserCallback) {
            mVodClipKeyListJsonParserCallback.onVodClipKeyListJsonParsed(
                    (ClipKeyListResponse) parsedData, getError());
        }
    }

    @Override
    public Object parse(final String body) {
        ClipKeyListJsonParser clipKeyListJsonParser = new ClipKeyListJsonParser();
        ClipKeyListResponse pursedRespData;
        pursedRespData = clipKeyListJsonParser.clipKeyListSender(body);

        return pursedRespData;
    }

    /**
     * WebAPIのエラー情報を取得する.
     *
     * @return エラー情報クラス
     */
    public ErrorState getError() {
        DTVTLogger.start();
        DTVTLogger.debug("get error statue=" + mReturnCode.errorState.getErrorType());
        //エラーメッセージの取得を行う
        mReturnCode.errorState.addErrorMessage(mContext);
        DTVTLogger.debug("get error message=" + mReturnCode.errorState.getErrorMessage());
        DTVTLogger.end();
        //エラーコードを返す
        return mReturnCode.errorState;
    }

    /**
     * 通信成功時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onAnswer(final ReturnCode returnCode) {
        Handler handler = new Handler();
        try {
            JsonParserThread thread = new JsonParserThread(returnCode.bodyData, handler, this);
            thread.start();
        } catch (Exception e) {
            DTVTLogger.debug(e);
            onError(returnCode);
        }
    }

    /**
     * 通信失敗時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onError(final ReturnCode returnCode) {
        //エラーが発生したのでレスポンスデータにnullを設定してを返す
        if (null != mTvClipKeyListJsonParserCallback) {
            mTvClipKeyListJsonParserCallback.onTvClipKeyListJsonParsed(null,getError());
        }
        if (null != mVodClipKeyListJsonParserCallback) {
            mVodClipKeyListJsonParserCallback.onVodClipKeyListJsonParsed(null,getError());
        }
    }

    /**
     * クリップキー一覧取得.
     *
     * @param requestParam                     クリップキー一覧リクエスト
     * @param tvClipKeyListJsonParserCallback  コールバック
     * @param vodClipKeyListJsonParserCallback コールバック
     * @return パラメータ等に問題があった場合はfalse
     */
    public boolean getClipKeyListApi(final ClipKeyListRequest requestParam,
                                     final TvClipKeyListJsonParserCallback tvClipKeyListJsonParserCallback,
                                     final VodClipKeyListJsonParserCallback vodClipKeyListJsonParserCallback) {
        if (mIsCancel) {
            DTVTLogger.error("ClipKeyListWebClient is stopping connection");
            return false;
        }

        //パラメーターのチェック
        if (!checkNormalParameter(requestParam, tvClipKeyListJsonParserCallback, vodClipKeyListJsonParserCallback)) {
            //パラメーターがおかしければ通信不能なので、ヌルで帰る
            return false;
        }

        //コールバックの準備
        mTvClipKeyListJsonParserCallback = tvClipKeyListJsonParserCallback;
        mVodClipKeyListJsonParserCallback = vodClipKeyListJsonParserCallback;

        //送信用パラメータの作成
        String sendParameter = makeSendParameter(requestParam);

        //JSONの組み立てに失敗していれば、ヌルで帰る
        if (sendParameter.isEmpty()) {
            return false;
        }

        //クリップキー一覧を呼び出す
        openUrlAddOtt( UrlConstants.WebApiUrl.CLIP_KEY_LIST_WEB_CLIENT,
                sendParameter, this, null);

        //今のところ正常なので、trueで帰る
        return true;
    }

    /**
     * 指定されたパラメータがおかしいかどうかのチェック.
     *
     * @param requestParam                     リクエストパラメータ
     * @param tvClipKeyListJsonParserCallback  コールバック
     * @param vodClipKeyListJsonParserCallback コールバック
     * @return 値がおかしいならばfalse
     */
    private boolean checkNormalParameter(final ClipKeyListRequest requestParam,
                                         final TvClipKeyListJsonParserCallback tvClipKeyListJsonParserCallback,
                                         final VodClipKeyListJsonParserCallback vodClipKeyListJsonParserCallback) {
        if (mIsCancel) {
            DTVTLogger.error("ClipKeyListWebClient is stopping connection");
            return false;
        }

        // 必須項目に値が入っていなければエラー
        if (ClipKeyListRequest.DEFAULT_STRING.equals(requestParam.getType())) {
            return false;
        }

        //コールバックが含まれていないならばエラー
        if (tvClipKeyListJsonParserCallback == null
                && vodClipKeyListJsonParserCallback == null) {
            return false;
        }

        //何もエラーが無いのでtrue
        return true;
    }

    /**
     * 指定されたパラメータをJSONで組み立てて文字列にする.
     *
     * @param requestParam リクエストパラメータ
     * @return 組み立て後の文字列
     */
    private String makeSendParameter(final ClipKeyListRequest requestParam) {
        JSONObject jsonObject = new JSONObject();
        String answerText;
        try {
            jsonObject.put( JsonConstants.META_RESPONSE_TYPE, requestParam.getType());
            jsonObject.put(JsonConstants.META_RESPONSE_IS_FORCE, requestParam.getIsForce());
//            if (ContentUtils.isTT02(mContext)) {
                jsonObject.put(JsonConstants.META_RESPONSE_INCLUDE_BS4K, true);
//            }

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
