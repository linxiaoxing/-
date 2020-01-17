package com.example.detaildemo.data.webapiclient.jsonparser;

import android.content.Context;
import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UserState;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.webapiclient.client.ChildContentListGetWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.response.ChildContentListGetResponse;
import com.example.detaildemo.utils.UserInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * レンタル一覧（Jsonパーサー）.
 */
public class ChildContentListJsonParser extends AsyncTask<String, Object, Object>{
    /**コンテキスト.*/
    private Context mContext = null;
    /**子コンテンツ一覧取得ウェブクライアントJsonParserCallback.*/
    private ChildContentListGetWebClient.JsonParserCallback mJsonParserCallback = null;
    /**子コンテンツ一覧取得レスポンス.*/
    private ChildContentListGetResponse mChildContentListGetResponse = null;

    /**
     * コンストラクタ.
     * @param context コンテキスト
     * @param callback  JsonParserCallback
     */
    public ChildContentListJsonParser(final Context context, final ChildContentListGetWebClient.JsonParserCallback callback) {
        mContext = context;
        mJsonParserCallback = callback;
        mChildContentListGetResponse = new ChildContentListGetResponse();
    }

    @Override
    protected Object doInBackground(final String... strings) {
        String result = strings[0];
        if (result == null) {
            mChildContentListGetResponse.setStatus(JsonConstants.META_RESPONSE_STATUS_NG);
            return mChildContentListGetResponse;
        }

        try {
            JSONObject jsonObj = new JSONObject(result);
            mChildContentListGetResponse.setStatus(jsonObj.getString(JsonConstants.META_RESPONSE_STATUS));
            parseMetaList(jsonObj);
        } catch (JSONException e) {
            mChildContentListGetResponse.setStatus(JsonConstants.META_RESPONSE_STATUS_NG);
            DTVTLogger.debug(e);
        }
        return mChildContentListGetResponse;
    }

    @Override
    protected void onPostExecute(final Object s) {
        mJsonParserCallback.onJsonParsed(mChildContentListGetResponse);
    }

    /**
     * メタリストをパースする（user情報を見てclip状態も反映する）.
     * @param jsonObj APIレスポンス Jsonデータ
     */
    private void parseMetaList(final JSONObject jsonObj) {
        try {
            ArrayList<VodMetaFullData> vodMetaFullDataList = new ArrayList<VodMetaFullData>();
            if (!jsonObj.isNull(JsonConstants.META_RESPONSE_STATUS)) {
                mChildContentListGetResponse.setStatus(jsonObj.getString(JsonConstants.META_RESPONSE_STATUS));
            }

            if (!jsonObj.isNull(JsonConstants.META_RESPONSE_PAGER)) {
                JSONObject pager = jsonObj.getJSONObject(JsonConstants.META_RESPONSE_PAGER);
                int limit = 0, offset = 0, count, total;
                if (!pager.isNull(JsonConstants.META_RESPONSE_PAGER_LIMIT)) {
                    limit = pager.getInt(JsonConstants.META_RESPONSE_PAGER_LIMIT);
                }
                if (!pager.isNull(JsonConstants.META_RESPONSE_OFFSET)) {
                    offset = pager.getInt(JsonConstants.META_RESPONSE_OFFSET);
                }
                count = pager.getInt( JsonConstants.META_RESPONSE_COUNT);
                total = pager.getInt(JsonConstants.META_RESPONSE_TOTAL);
                mChildContentListGetResponse.setPager(limit, offset, count, total);
            }

            if (!jsonObj.isNull(JsonConstants.META_RESPONSE_LIST)) {
                JSONArray metaList = jsonObj.getJSONArray(JsonConstants.META_RESPONSE_LIST);
                UserState userState = UserInfoUtils.getUserState(mContext);
                for (int i = 0; i < metaList.length(); i++) {
                    VodMetaFullData vodMetaFullData = new VodMetaFullData();
                    vodMetaFullData.setData(userState, metaList.getJSONObject(i));
                    vodMetaFullDataList.add(vodMetaFullData);
                }
                mChildContentListGetResponse.setVodMetaFullData(vodMetaFullDataList);
            }

        } catch (JSONException e) {
            DTVTLogger.debug(e);
        }
    }
}