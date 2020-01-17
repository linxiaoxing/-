package com.example.detaildemo.data.webapiclient.jsonparser.response;


import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.webapiclient.client.OttStopWebClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 視聴終了機能用JsonParserクラス.
 */
public class OttStopJsonParser extends AsyncTask<Object, Object, Object>{
    /**callback.*/
    private final OttStopWebClient.OttStopJsonParserCallback
            OttStopJsonParserCallback;
    /**オブジェクトクラスの定義　.*/
    private OttStopResponse ottStopResponse;

    /**
     * コンストラクタ.
     * <p>
     * @param OttStopJsonParserCallback callback
     */
    public OttStopJsonParser(final OttStopWebClient.OttStopJsonParserCallback OttStopJsonParserCallback) {
        this.OttStopJsonParserCallback = OttStopJsonParserCallback;
        ottStopResponse = new OttStopResponse();
    }

    @Override
    protected void onPostExecute(final Object s) {
        OttStopJsonParserCallback.onOttStopJsonParsed(ottStopResponse);
    }
    @Override
    protected Object doInBackground(final Object... strings) {
        String result = (String) strings[0];
        return  OttStopResponseSender(result);
    }

    /**
     * 視聴終了機能Jsonデータを解析する.
     *
     * @param jsonStr 視聴終了機能Jsonデータ
     * @return 視聴終了機能取得：正常時レスポンスデータ
     */
    private OttStopResponse OttStopResponseSender(final String jsonStr) {

        DTVTLogger.debugHttp(jsonStr);
        ottStopResponse = new OttStopResponse();

        try {
            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                sendStatus(jsonObj);
                return ottStopResponse;
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
        return null;
    }

    /**
     * statusの値を視聴終了機能取得：正常時レスポンスデータオブジェクトに格納.
     *
     * @param jsonObj APIレスポンス Jsonデータ
     */
    private void sendStatus(final JSONObject jsonObj) {
        try {
            // statusの値を取得しセットする
            if (!jsonObj.isNull( JsonConstants.META_RESPONSE_STATUS)) {
                String status = jsonObj.getString(JsonConstants.META_RESPONSE_STATUS);
                if (ottStopResponse != null) {
                    ottStopResponse.setStatus(status);
                }
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }
}
