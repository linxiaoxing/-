package com.example.detaildemo.data.webapiclient.client;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.jsonparser.ChildContentListJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.response.ChildContentListGetResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 子コンテンツリスト取得WebClient.
 */
public class ChildContentListGetWebClient extends WebApiBasePlala implements
        WebApiBasePlala.WebApiBasePlalaCallback {

    // declaration
    /** コールバック. */
    public interface JsonParserCallback {
        /**
         * コールバック.
         * errorの場合はnullを返却する
         * @param response response
         */
        void onJsonParsed(@Nullable ChildContentListGetResponse response);
    }

    // region variable
    /**コンテクスト.*/
    private Context mContext = null;
    /**JsonParserCallback.*/
    private JsonParserCallback mJsonParserCallback;
    /** 通信禁止判定フラグ. */
    private boolean mIsCancel = false;
    /** RequestCount.*/
    private int mRequestCount;
    /** RequestCountセット.*/
    public void setRequestCount(final int count) {
        mRequestCount = count;
    }
    /**
     * コンテキストを継承元のコンストラクタに送る.
     *
     * @param context コンテキスト
     */
    public ChildContentListGetWebClient(final Context context) {
        super(context);
        mContext = context;
    }

    /**
     * 通信成功時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onAnswer(final ReturnCode returnCode) {
        new ChildContentListJsonParser(mContext, mJsonParserCallback).executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, returnCode.bodyData);
    }

    /**
     * 通信失敗時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onError(final ReturnCode returnCode) {
        if (mJsonParserCallback != null) {
            mJsonParserCallback.onJsonParsed(null);
        }
    }

    /**
     * APIリクエスト.
     * @param ageReq 年齢情報
     * @param callback callback
     * @param crid コンテンツ識別子
     * @param filter フィルター用指定文字列・release.
     * @param offset 取得位置(1～)
     * @return 成功true
     */
    public boolean requestChildContentListGetApi(final String crid, final int offset, final String filter,
                                                 final int ageReq, final JsonParserCallback callback) {
        mJsonParserCallback = callback;

        final String receivedParameter = makeSendParameter(crid, offset, filter, ageReq);
        if (receivedParameter.isEmpty()) {
            return false;
        }

        openUrl( UrlConstants.WebApiUrl.CHILD_CONTENTS_GET_WEB_CLIENT, receivedParameter, this);
        return true;
    }

    /**
     * SendParameter整合.
     * @param crid コンテンツ識別子
     * @param offset 取得位置(1～)
     * @param filter フィルター用指定文字列・release.
     * @param ageReq 年齢情報
     * @return 整合されたparameter
     */
    private String makeSendParameter(final String crid, final int offset, final String filter, final int ageReq) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject();
            int intAge = ageReq;
            //数字がゼロの場合は無指定と判断して1にする.また17より大きい場合は17に丸める.
            if (ageReq < WebApiBasePlala.AGE_LOW_VALUE) {
                intAge = WebApiBasePlala.AGE_LOW_VALUE;
            } else if (ageReq > WebApiBasePlala.AGE_HIGH_VALUE) {
                intAge = WebApiBasePlala.AGE_HIGH_VALUE;
            }
            jsonObject.put( JsonConstants.META_RESPONSE_AGE_REQ, intAge);

            jsonObject.put(JsonConstants.META_RESPONSE_CRID, crid);
            jsonObject.put(JsonConstants.META_RESPONSE_FILTER, filter);

            JSONObject jsonPagerObject = new JSONObject();
            jsonPagerObject.put(JsonConstants.META_RESPONSE_OFFSET, offset);
            if (mRequestCount > 0) {
                jsonPagerObject.put(JsonConstants.META_RESPONSE_PAGER_LIMIT, mRequestCount);
                mRequestCount = 0;
            } else {
                jsonPagerObject.put(JsonConstants.META_RESPONSE_PAGER_LIMIT, DtvtConstants.REQUEST_LIMIT_50);
            }
            jsonObject.put(JsonConstants.META_RESPONSE_PAGER, jsonPagerObject);

            result = jsonObject.toString().replace("\\", "");

        } catch (JSONException e) {
            DTVTLogger.warning(e.getMessage());
        }
        DTVTLogger.debugHttp(result);
        return result;
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
