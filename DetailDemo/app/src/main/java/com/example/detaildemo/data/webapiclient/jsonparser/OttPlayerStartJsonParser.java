package com.example.detaildemo.data.webapiclient.jsonparser;


import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.data.webapiclient.client.OttPlayerStartWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerStartResponse;
import com.example.detaildemo.utils.OttContentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * コンテンツ詳細JsonParser.
 */
public class OttPlayerStartJsonParser extends AsyncTask<Object, Object, Object>{
    /**コールバック.*/
    private final OttPlayerStartWebClient.OttPlayerStarCallback mOttPlayerStarCallback;
    /**オブジェクトクラスの定義　.*/
    private OttPlayerStartResponse mOttPlayerStartResponse;

    /**
     * コンストラクタ.
     * @param ottPlayerStarCallback コールバック
     */
    public OttPlayerStartJsonParser(final OttPlayerStartWebClient.OttPlayerStarCallback ottPlayerStarCallback) {
        mOttPlayerStarCallback = ottPlayerStarCallback;
        mOttPlayerStartResponse = new OttPlayerStartResponse();
    }

    @Override
    protected void onPostExecute(final Object s) {
        mOttPlayerStarCallback.onOttPlayerStartJsonParsed(mOttPlayerStartResponse);
    }

    @Override
    protected Object doInBackground(final Object... strings) {
        String result = (String) strings[0];
        return ottPlayerStartSender(result);
    }

    /**
     * Jsonデータを解析する.
     * @param jsonStr Jsonデータ
     * @return レスポンスデータ
     */
    private OttPlayerStartResponse ottPlayerStartSender(final String jsonStr) {

        DTVTLogger.debugHttp(jsonStr);
        try {
            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                sendStatus(jsonObj);
                sendPlayToken(jsonObj);
                sendOttPlayerStartResponse(jsonObj);
                return mOttPlayerStartResponse;
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
        return null;
    }

    /**
     * statusの値をレスポンスデータに格納.
     * @param jsonObj Jsonデータ
     */
    private void sendStatus(final JSONObject jsonObj) {
        try {
            // statusの値を取得しセットする
            if (!jsonObj.isNull(OttContentUtils.OTT_PLAY_STATUS)) {
                String status = jsonObj.getString(OttContentUtils.OTT_PLAY_STATUS);
                if (mOttPlayerStartResponse != null) {
                    mOttPlayerStartResponse.setStatus(status);
                }
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }

    /**
     * playTokenの値をレスポンスデータに格納.
     * @param jsonObj Jsonデータ
     */
    private void sendPlayToken(final JSONObject jsonObj) {
        try {
            // playTokenの値を取得しセットする
            if (!jsonObj.isNull(OttContentUtils.OTT_PLAY_START_PLAY_TOKEN)) {
                String playToken = jsonObj.getString(OttContentUtils.OTT_PLAY_START_PLAY_TOKEN);
                if (mOttPlayerStartResponse != null) {
                    mOttPlayerStartResponse.setPlayToken(playToken);
                }
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }

    /**
     * レスポンスデータオブジェクトに格納.
     * @param jsonObj Jsonデータ
     */
    private void sendOttPlayerStartResponse(final JSONObject jsonObj) {
        try {
            List<OttPlayerStartData> ottPlayerDataList = new ArrayList<>();
            if (!jsonObj.isNull( OttContentUtils.OTT_PLAY_START_PLAYLIST)) {
                // レスポンスデータをJSONArrayにパースする
                JSONArray lists = jsonObj.getJSONArray(OttContentUtils.OTT_PLAY_START_PLAYLIST);
                if (lists.length() == 0) {
                    return;
                }
                for (int i = 0; i < lists.length(); i++) {
                    OttPlayerStartData ottPlayerStartData = new OttPlayerStartData();
                    //データを個別に転送する
                    ottPlayerStartData.setData(lists.getJSONObject(i));
                    //データを追加
                    ottPlayerDataList.add(ottPlayerStartData);
                }
                if (mOttPlayerStartResponse != null) {
                    mOttPlayerStartResponse.setOttPlayerStartData(ottPlayerDataList);
                }
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }
}
