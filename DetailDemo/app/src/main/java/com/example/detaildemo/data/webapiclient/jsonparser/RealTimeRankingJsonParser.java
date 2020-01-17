package com.example.detaildemo.data.webapiclient.jsonparser;


import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.webapiclient.client.RealTimeRankingWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RealTimeRankingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * リアルタイム再生数上位番組取得用JsonのParser.
 */
public class RealTimeRankingJsonParser extends AsyncTask<Object, Object, Object>{

    /**
     * Parse結果を返すコールバック.
     */
    private final RealTimeRankingWebClient.RealTimeRankingJsonParserCallback mRealTimeRankingJsonParserCallback;
    /**
     * オブジェクトクラスの定義.
     */
    private RealTimeRankingList mRealTimeRankingList;
    /**
     * pagerのparse用パラメータ.
     */
    private static final String[] PAGER_PARAMETERS = {JsonConstants.META_RESPONSE_PAGER_LIMIT,
            JsonConstants.META_RESPONSE_TYPE, JsonConstants.META_RESPONSE_AGE_REQ,
            JsonConstants.META_RESPONSE_FILTER};

    /**
     * JSONパースエラー
     */
    private ErrorState mJsonParseError = null;

    /**
     * コンストラクタ.
     *
     * @param realTimeRankingJsonParserCallback コールバック
     */
    public RealTimeRankingJsonParser(final RealTimeRankingWebClient.RealTimeRankingJsonParserCallback realTimeRankingJsonParserCallback) {
        this.mRealTimeRankingJsonParserCallback = realTimeRankingJsonParserCallback;
    }

    /**
     * リアルタイム再生数上位番組一覧Jsonデータを解析する.
     *
     * @param jsonStr 元のJSONデータ
     * @return リスト化データ
     */
    private List<RealTimeRankingList> channelOttListSender(final String jsonStr) {

        DTVTLogger.debugHttp(jsonStr);
        mRealTimeRankingList = new RealTimeRankingList();

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            sendStatus(jsonObj);
            sendVcList(jsonObj);
            return Collections.singletonList(mRealTimeRankingList);
        } catch (JSONException e) {
            mJsonParseError = new ErrorState();
            mJsonParseError.setErrorType( DtvtConstants.ErrorType.PARSE_ERROR);
            DTVTLogger.debug(e);
        }
        return null;
    }

    /**
     * statusの値をMapでオブジェクトクラスに渡す.
     *
     * @param jsonObj 元のJSONデータ
     */
    private void sendStatus(final JSONObject jsonObj) {
        try {
            // statusの値を取得し、Mapに格納
            HashMap<String, String> map = new HashMap<>();
            if (!jsonObj.isNull(JsonConstants.META_RESPONSE_STATUS)) {
                String status = jsonObj.getString(JsonConstants.META_RESPONSE_STATUS);
                map.put(JsonConstants.META_RESPONSE_STATUS, status);
            }

            if (!jsonObj.isNull(JsonConstants.META_RESPONSE_PAGER)) {
                JSONObject pager = jsonObj.getJSONObject(JsonConstants.META_RESPONSE_PAGER);

                for (String pagerParameter : PAGER_PARAMETERS) {
                    if (!pager.isNull(pagerParameter)) {
                        String para = pager.getString(pagerParameter);
                        map.put(pagerParameter, para);
                    }
                }
            }
            mRealTimeRankingList.setRealTimeRankingMap(map);

        }  catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }

    /**
     * コンテンツのList<HashMap>をオブジェクトクラスに格納.
     *
     * @param jsonObj 元のJSONデータ
     */
    private void sendVcList(final JSONObject jsonObj) {
        try {
            if (!jsonObj.isNull(JsonConstants.META_RESPONSE_LIST)) {
                // コンテンツリストのList<HashMap>を用意
                List<HashMap<String, String>> vcList = new ArrayList<>();

                // コンテンツリストをJSONArrayにパースする
                JSONArray jsonArr = jsonObj.getJSONArray(JsonConstants.META_RESPONSE_LIST);

                // リストの数だけまわす
                for (int i = 0; i < jsonArr.length(); i++) {
                    // 最初にHashMapを生成＆初期化
                    HashMap<String, String> vcListMap = new HashMap<>();

                    // i番目のJSONArrayをJSONObjectに変換する
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    for (String listBuffer : JsonConstants.LIST_PARA) {
                        if (!jsonObject.isNull(listBuffer)) {
                            if (listBuffer.equals(JsonConstants.META_RESPONSE_PUINF)) {
                                JSONObject puinfObj = jsonObject.getJSONObject(listBuffer);
                                for (String puinfBuffer : JsonConstants.PUINF_PARA) {
                                    String para = puinfObj.getString(puinfBuffer);
                                    vcListMap.put(JsonConstants.META_RESPONSE_PUINF + JsonConstants.UNDER_LINE + puinfBuffer, para);
                                }
                            } else {
                                String para = jsonObject.getString(listBuffer);
                                vcListMap.put(listBuffer, para);
                            }
                        }
                    }
                    // i番目のMapをListにadd
                    vcList.add(vcListMap);
                }
                // リスト数ぶんの格納が終わったらオブジェクトクラスにList<HashMap>でset
                mRealTimeRankingList.setRealTimeRankingList(vcList);
            }
        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }

    @Override
    protected void onPostExecute(Object object) {
        mRealTimeRankingJsonParserCallback.onRealTimeRankingJsonParsed((List<RealTimeRankingList>) object, mJsonParseError);
    }

    @Override
    protected Object doInBackground(Object... objects) {
        String result = (String) objects[0];
        return channelOttListSender(result);
    }
}
