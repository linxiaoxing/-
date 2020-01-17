package com.example.detaildemo.common;


import android.content.Context;
import android.text.TextUtils;

import com.example.detaildemo.R;
import com.example.detaildemo.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * WebAPIエラー情報クラス.
 * <p>
 * アクティビティ側で表示する際に使用する、ネットワーク情報のエラーやAPIのエラーの情報を格納します
 */
public class ErrorState {

    /**
     * 通信時エラー情報.
     */
    private DtvtConstants.ErrorType mErrorType;

    /**
     * エラーメッセージ.
     */
    private String mErrorMessage;

    /**
     * トースト用エラーメッセージ.
     */
    private String mToastErrorMessage;

    /**
     * エラーコード.
     */
    private String mErrorCode;

    /**
     * タイムアウトならばtrue.
     */
    private boolean mIsTimeout = false;

    /**
     * 空白文字.
     */
    private static final String SPACE_STRING = " ";

    /**
     * 正常終了コード.
     */
    private static final String NORMAL_RESULT_TEXT = "0";

    /**
     * XMLの結果コードの名前.
     */
    private static final String XML_RESULT_STRING = "id";

    /**
     * XMLの結果コードのキーの名前.
     */
    private static final String XML_RESULT_KEY_NAME = "Result";

    /**
     * エラーコード.
     */
    private static final String NETWORK_ERROR_CODE = "NW-";

    /**
     * コンストラクタ.
     */
    public ErrorState() {
        //エラー情報を初期化
        mErrorType = DtvtConstants.ErrorType.SUCCESS;
        mErrorMessage = "";
        mErrorCode = "";
        mIsTimeout = false;
    }

    /**
     * エラータイプを設定する.
     * @param errorType  エラータイプ
     */
    public void setErrorType(final DtvtConstants.ErrorType errorType) {
        DTVTLogger.start("ErrorType = " + errorType);
        this.mErrorType = errorType;
        DTVTLogger.end();
    }

    /**
     * エラータイプを取得する.
     * @return エラータイプ
     */
    public DtvtConstants.ErrorType getErrorType() {
        return mErrorType;
    }

    /**
     * 送られてきた文字列を格納する。JSONだった場合はエラー番号判定を行ってエラー番号だけ格納する.
     *
     * @param errorCode HTTPステータス文字列やJSONのエラー情報
     * @return 結果がエラーならばtrue、正常ならばfalse
     */
    public boolean setErrorCode(final String errorCode) {
        //送られてきた文字列がJSONかどうかを見る為に、変換を行う
        JSONObject json;
        try {
            json = new JSONObject(errorCode);
        } catch (JSONException e) {
            //JSONではなかったので、そのまま使用する
            this.mErrorCode = errorCode;
            return false;
        }

        //ステータスの確認
        try {
            String status = json.getString(JsonConstants.META_RESPONSE_STATUS);
            if (status.equals(JsonConstants.META_RESPONSE_STATUS_OK)) {
                //ステータスはOKだったので、正常で帰る
                return false;

            }
        } catch (JSONException e) {
            DTVTLogger.debug("no error num");
            //エラー番号は無いので、何もしない
        }

        //最終結果
        boolean answer = false;

        //エラー番号を持っているかどうかを見る
        try {
            //エラー番号が取得できたので、それを採用する
            this.mErrorCode = json.getString(JsonConstants.META_RESPONSE_NG_ERROR_NO);
            answer = true;
        } catch (JSONException e) {
            DTVTLogger.debug("no error num");
            //エラー番号は無いので、何もしない
        }

        return answer;
    }

    /**
     *エラーコード取得.
     * @return エラーコード
     */
    public String getErrorCode() {
        return mErrorCode;
    }

    /**
     * タイムアウトか否かをセット.
     *
     * @param timeout タイムアウトならばtrueを指定する
     */
    public void setIsTimeout(final boolean timeout) {
        mIsTimeout = timeout;
    }

    /**
     * エラーメッセージを設定する.
     * @param errorMessage エラーメッセージ
     */
    public void setErrorMessage(final String errorMessage) {
        this.mErrorMessage = errorMessage;
    }

    /**
     * エラーメッセージを返す.
     *
     * こちらはエラーコードが混在することが無いので、基本的にトースト用となります。
     * @return エラーメッセージ
     */
    public String getErrorMessage() {
        //トースト用メッセージを返す
        return mToastErrorMessage;
    }


    /**
     * 現在のエラー情報に合わせたエラーメッセージを取得する.
     *
     * @param context コンテキスト
     */
    public void addErrorMessage(final Context context) {
        DTVTLogger.start();

        //コンテキストが無いとリソースにアクセスできないので、帰る
        if (context == null) {
            DTVTLogger.debug("addErrorMessage no context");
            return;
        }

        //エラーメッセージの選択
        //使用するメッセージは、ダイアログ用SSLメッセージ以外は既存の物を流用する
        //トースト用は現状同一メッセージとする
        switch (mErrorType) {
            case SUCCESS:
                //正常なのでメッセージは無し。
                DTVTLogger.debug("success ");
                break;
            case SSL_ERROR:
//                //SSLエラー用メッセージの取得
//                mErrorMessage = context.getString(R.string.network_ssl_error_message);
//
//                //トースト用
//                mToastErrorMessage = context.getString(
//                        R.string.network_ssl_error_message);
                break;
            case NETWORK_ERROR:
                //通信不能用のメッセージ ダイアログ用
                mErrorMessage = context.getString(
                        R.string.network_nw_error_message_dialog);

                //トースト用
                mToastErrorMessage = context.getString(
                        R.string.network_nw_error_message);
                break;
            case SERVER_ERROR:
                //ネットワークエラー(サーバエラー)
                mErrorMessage = context.getString( R.string.network_server_error_message);
                //トースト用
                mToastErrorMessage = context.getString(
                        R.string.network_server_error_message);
                break;
            case TOKEN_ERROR:
                //ネットワークエラー(トークン取得エラー)
                mErrorMessage = context.getString(R.string.network_token_error_message);
                //トースト用
                mToastErrorMessage = context.getString(
                        R.string.network_token_error_message);
                break;
            case HTTP_ERROR:
                //その他Lエラー用メッセージの取得
                mErrorMessage = context.getString(R.string.network_http_error_message);

                //トースト用
                mToastErrorMessage = context.getString(
                        R.string.network_http_error_message);
                break;
            case PARSE_ERROR:
                //JSONパースエラーのダイアログ用メッセージの取得
                mErrorMessage = context.getString(R.string.json_parse_error_message);

                //JSONパースエラーのトースト用メッセージの取得
                mToastErrorMessage = context.getString(R.string.json_parse_error_message);
                break;
            default:
                break;
        }

        DTVTLogger.end();
    }

    /**
     * エラーメッセージとして、エラーメッセージとエラーコードを組み合わせた物を返す.
     *
     * こちらはエラーコードが付加されるので、基本的にはダイアログ用となります。
     *
     * @param context 指定していればエラーコードは文字リソースから取得した括弧で囲まれる。なければそのまま出力
     * @return エラーメッセージ
     */
    public String getApiErrorMessage(final Context context) {
        //エラーメッセージが無ければ空文字で帰る
        if (TextUtils.isEmpty(mErrorMessage)) {
            return "";
        }

        String openString;
        String closeString = "";
        //コンテキストの指定があれば、囲む括弧の文字を取得
        if (context != null) {
            openString = context.getString(R.string.home_contents_front_bracket);
            closeString = context.getString(R.string.home_contents_back_bracket);
        } else {
            //文字列リソースが取得できないので、空白で分離
            openString = SPACE_STRING;
        }

        String answer;
        //エラーコードの有無の判定
        if (TextUtils.isEmpty(mErrorCode)) {
            //エラーコードは無いので、メッセージだけを返す
            answer = mErrorMessage;
        } else {
            //文字列を結合する
            answer = StringUtils.getConnectStrings(
                    mErrorMessage,
                    openString,
                    NETWORK_ERROR_CODE,
                    mErrorCode,
                    closeString);
        }

        //結果を返す
        return answer;
    }
}
