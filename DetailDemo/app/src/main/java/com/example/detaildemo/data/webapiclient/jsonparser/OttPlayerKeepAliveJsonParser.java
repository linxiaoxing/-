package com.example.detaildemo.data.webapiclient.jsonparser;


import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.webapiclient.client.OttPlayerKeepAliveWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerKeepAliveResponse;
import com.example.detaildemo.utils.OttContentUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * コンテンツ詳細JsonParser.
 */
public class OttPlayerKeepAliveJsonParser extends AsyncTask<Object, Object, Object>{
    /**コールバック.*/
    private OttPlayerKeepAliveWebClient.OttPlayerKeepAliveCallback mOttPlayerKeepAliveCallback;
    /**オブジェクトクラスの定義　.*/
    private OttPlayerKeepAliveResponse mOttPlayerKeepAliveResponse;

    /**
     * コンストラクタ.
     * @param ottPlayerKeepAliveCallback コールバック
     */
    public OttPlayerKeepAliveJsonParser(final OttPlayerKeepAliveWebClient.OttPlayerKeepAliveCallback ottPlayerKeepAliveCallback) {
        mOttPlayerKeepAliveCallback = ottPlayerKeepAliveCallback;
        mOttPlayerKeepAliveResponse = new OttPlayerKeepAliveResponse();
    }

    @Override
    protected void onPostExecute(final Object s) {
        mOttPlayerKeepAliveCallback.onOttPlayerKeepAliveParsed(mOttPlayerKeepAliveResponse);
    }

    @Override
    protected Object doInBackground(final Object... strings) {
        String result = (String) strings[0];
        return ottPlayerKeepAliveSender(result);
    }

    /**
     * Jsonデータを解析する.
     * @param jsonStr Jsonデータ
     * @return レスポンスデータ
     */
    private OttPlayerKeepAliveResponse ottPlayerKeepAliveSender(final String jsonStr) {

        DTVTLogger.debugHttp(jsonStr);
        try {
            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                sendStatusAndError(jsonObj);
                return mOttPlayerKeepAliveResponse;
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
        return null;
    }

    /**
     * status、errorNoの値をレスポンスデータに格納.
     * @param jsonObj Jsonデータ
     */
    private void sendStatusAndError(final JSONObject jsonObj) {
        try {
            // statusの値を取得しセットする
            if (!jsonObj.isNull( OttContentUtils.OTT_PLAY_STATUS)) {
                String status = jsonObj.getString(OttContentUtils.OTT_PLAY_STATUS);
                if (mOttPlayerKeepAliveResponse != null) {
                    mOttPlayerKeepAliveResponse.setStatus(status);
                }
            }
            // errornoの値を取得しセットする
            if (!jsonObj.isNull(OttContentUtils.OTT_PLAYER_GET_RESPONSE_ERRORNO)) {
                String errorNo = jsonObj.getString(OttContentUtils.OTT_PLAYER_GET_RESPONSE_ERRORNO);
                if (mOttPlayerKeepAliveResponse != null) {
                    mOttPlayerKeepAliveResponse.setErrorNo(errorNo);
                }
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }
}
