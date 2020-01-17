package com.example.detaildemo.data.webapiclient.client;


import android.content.Context;
import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.jsonparser.RoleListJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.response.RoleListResponse;

/**
 * ロール一覧WebClient.
 */
public class RoleListWebClient
        extends WebApiBasePlala implements WebApiBasePlala.WebApiBasePlalaCallback {

    /**
     * 通信禁止判定フラグ.
     */
    private boolean mIsCancel = false;

    /**
     * コールバック.
     */
    public interface RoleListJsonParserCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         *
         * @param roleListResponse JSONパース後のデータ
         */
        void onRoleListJsonParsed(RoleListResponse roleListResponse);
    }

    /**
     * コールバックのインスタンス.
     */
    private RoleListJsonParserCallback mRoleListJsonParserCallback;

    /**
     * コンテキストを継承元のコンストラクタに送る.
     *
     * @param context コンテキスト
     */
    public RoleListWebClient(final Context context) {
        super(context);
    }

    @Override
    public void onAnswer(final ReturnCode returnCode) {
        if (mRoleListJsonParserCallback != null) {
            //JSONをパースして、データを返す
            new RoleListJsonParser(mRoleListJsonParserCallback)
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
        if (mRoleListJsonParserCallback != null) {
            //エラーが発生したのでヌルを返す
            mRoleListJsonParserCallback.onRoleListJsonParsed(null);
        }
    }

    /**
     * ジャンル一覧の取得.
     *
     * @param roleListJsonParserCallback コールバック
     * @return パラメータエラー等が発生した場合はfalse
     */
    public boolean getRoleListApi(final RoleListJsonParserCallback roleListJsonParserCallback) {
        DTVTLogger.start();

        if (mIsCancel) {
            DTVTLogger.error("RoleListWebClient is stopping connection");
            return false;
        }

        //パラメーターのチェック
        if (!checkNormalParameter(roleListJsonParserCallback)) {
            //パラメーターがおかしければ通信不能なので、falseで帰る
            DTVTLogger.end_ret(String.valueOf(false));
            return false;
        }

        //コールバックのセット
        mRoleListJsonParserCallback = roleListJsonParserCallback;

        //ロール一覧ファイルを読み込む
        DTVTLogger.debug("Get lole list file");
        openUrl( UrlConstants.WebApiUrl.ROLE_LIST_FILE, "", this);

        DTVTLogger.end();
        //今のところ失敗していないので、trueを返す
        return true;
    }

    /**
     * 指定されたパラメータがおかしいかどうかのチェック.
     *
     * @param roleListJsonParserCallback コールバック
     * @return 値がおかしいならばfalse
     */
    private boolean checkNormalParameter(final RoleListJsonParserCallback
                                                 roleListJsonParserCallback) {
        //コールバックが指定されていないならばfalse
        if (roleListJsonParserCallback == null) {
            return false;
        }

        //何もエラーが無いのでtrue
        return true;
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

    @Override
    protected String getRequestMethod() {
        return WebApiBasePlala.REQUEST_METHOD_GET;
    }
}
