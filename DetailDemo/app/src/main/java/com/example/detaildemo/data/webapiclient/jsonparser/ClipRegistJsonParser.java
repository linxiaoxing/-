package com.example.detaildemo.data.webapiclient.jsonparser;


import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.webapiclient.clip.ClipRegistWebClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * クリップ登録JsonParser.
 */
public class ClipRegistJsonParser extends AsyncTask<Object, Object, String>{

    /** callback.*/
    private final ClipRegistWebClient.ClipRegistJsonParserCallback mClipJsonParserCallback;

    /**クリップ登録/削除成功判定用.*/
    private static final String CLIP_RESULT_STATUS_OK = "OK";

    /**
     * CH一覧Jsonデータを解析する.
     *
     * @param jsonStr 元のJSONデータ
     * @return 正常時レスポンスデータ
     */
    private String getClipStatus(final String jsonStr) {

        DTVTLogger.debugHttp(jsonStr);
        String status = "";

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj != null) {
                if (!jsonObj.isNull( JsonConstants.META_RESPONSE_STATUS)) {
                    status = jsonObj.getString(JsonConstants.META_RESPONSE_STATUS);
                }

                return status;
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
        return null;
    }

    /**
     * コンストラクタ.
     *
     * @param mClipJsonParserCallback コールバック
     */
    public ClipRegistJsonParser(final ClipRegistWebClient.ClipRegistJsonParserCallback mClipJsonParserCallback) {
        this.mClipJsonParserCallback = mClipJsonParserCallback;
    }

    @Override
    protected void onPostExecute(final String status) {
        if (status != null && status.equals(CLIP_RESULT_STATUS_OK)) {
            //成功時のcallback
            mClipJsonParserCallback.onClipRegistResult();
        } else {
            //失敗時のcallback
            mClipJsonParserCallback.onClipRegistFailure();
        }
    }

    @Override
    protected String doInBackground(final Object... strings) {
        String result = (String) strings[0];
        return getClipStatus(result);
    }
}
