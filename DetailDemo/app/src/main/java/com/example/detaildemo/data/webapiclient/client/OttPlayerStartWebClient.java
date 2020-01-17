package com.example.detaildemo.data.webapiclient.client;


import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.UrlConstants;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.jsonparser.OttPlayerStartJsonParser;
import com.example.detaildemo.data.webapiclient.jsonparser.response.OttPlayerStartResponse;
import com.example.detaildemo.utils.OttContentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * OTT再生取得WebClient.
 */
public class OttPlayerStartWebClient extends WebApiBasePlala implements WebApiBasePlala.WebApiBasePlalaCallback {

    /** 通信禁止判定フラグ.*/
    private boolean mIsCancel = false;

    /** コールバック.*/
    public interface OttPlayerStarCallback {
        /**
         * 正常に終了した場合に呼ばれるコールバック.
         * @param ottPlayerStartResponse ott開始リスポンス情報
         */
        void onOttPlayerStartJsonParsed(OttPlayerStartResponse ottPlayerStartResponse);
    }
    /**
     * コールバックのインスタンス.
     */
    private OttPlayerStarCallback mOttPlayerStarCallback;

    /**
     * コンテキストを継承元のコンストラクタに送る.
     *
     * @param context コンテキスト
     */
    public OttPlayerStartWebClient(final Context context) {
        super(context);
    }

    /**
     * 通信成功時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onAnswer(final ReturnCode returnCode) {
        //JSONをパースして、データを返す
        if (mOttPlayerStarCallback != null) {
            new OttPlayerStartJsonParser(mOttPlayerStarCallback).executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, returnCode.bodyData);
        }
    }

    /**
     * 通信失敗時のコールバック.
     *
     * @param returnCode 戻り値構造体
     */
    @Override
    public void onError(final ReturnCode returnCode) {
        //エラーが発生したのでヌルを返す
        if (mOttPlayerStarCallback != null) {
            mOttPlayerStarCallback.onOttPlayerStartJsonParsed(null);
        }
    }

    /**
     * OTT再生開始IF.
     *
     * @param deviceId 端末を一意に識別する値
     * @param playType 再生種別 1 : ストリーミング再生 2 : ダウンロード再生
     * @param protocol 1 : MPEG-DASH 2 : HLS
     * @param quality  コンテンツの画質
     * @param availStatus  1.1 : 視聴確認用のリクエスト 1.3 : 通常視聴用のリクエスト
     * @param viewContinueFlg 0:新規視聴時 1:継続視聴時
     * @param contentList 再生情報のリスト
     * @param ottPlayerStarCallback コールバック
     * @return パラメータエラー等が発生した場合はfalse
     */
    public boolean getOttPlayerStartApi(final String deviceId, final int playType, final int protocol, final int quality, final String availStatus,
                                        final int viewContinueFlg, final List<HashMap<String, String>> contentList,
                                        final OttPlayerStarCallback ottPlayerStarCallback) {
        if (mIsCancel) {
            DTVTLogger.error("OttPlayerStartWebClient is stopping connection");
            return false;
        }

        if (ottPlayerStarCallback == null) {
            //コールバックがヌルならばfalse
            return false;
        }

        //パラメーターのチェック
        if (!checkNormalParameter(deviceId, playType, protocol, contentList)) {
            //パラメーターがおかしければ通信不能なので、falseで帰る
            return false;
        }

        //コールバックの設定
        mOttPlayerStarCallback = ottPlayerStarCallback;

        //送信用パラメータの作成
        String sendParameter = makeSendParameter(deviceId, playType, protocol, quality, availStatus, viewContinueFlg, contentList);

        //JSONの組み立てに失敗していれば、falseで帰る
        if (sendParameter.isEmpty()) {
            return false;
        }

        //Ott開始IFを呼び出す
        openUrlAddOtt( UrlConstants.WebApiUrl.OTT_PLAYER_START_GET_WEB_CLIENT, sendParameter, this, null);

        //現状失敗は無いのでtrue
        return true;
    }

    /**
     * 指定されたパラメータがおかしいかどうかのチェック.
     *
     * @param deviceId 端末を一意に識別する値
     * @param playType 再生種別 1 : ストリーミング再生 2 : ダウンロード再生
     * @param protocol 1 : MPEG-DASH 2 : HLS
     * @param contentList 再生情報のリスト
     * @return 値がおかしいならばfalse
     */
    private boolean checkNormalParameter(final String deviceId, final int playType, final int protocol, final List<HashMap<String, String>> contentList) {

        //deviceIdチェック
        if (TextUtils.isEmpty(deviceId)) {
            return false;
        }

        //playTypeチェック
        if (playType != OttContentUtils.OTT_PLAY_START_PLAY_TYPE_1 && protocol != OttContentUtils.OTT_PLAY_START_PLAY_TYPE_2) {
            return false;
        }

        //protocolチェック
        if (protocol != OttContentUtils.OTT_PLAY_START_PROTOCOL_1 && protocol != OttContentUtils.OTT_PLAY_START_PROTOCOL_2) {
            return false;
        }

        //再生情報チェック
        if (contentList == null || contentList.size() == 0) {
            return false;
        }

        //再生情報チェック
        boolean checkContentOK = true;
        for (int i = 0; i < contentList.size(); i++) {
            HashMap<String, String> hashMap = contentList.get(i);
            String kind = hashMap.get(OttContentUtils.OTT_PLAY_START_KIND);
            String serviceId = hashMap.get(OttContentUtils.OTT_PLAY_START_SERVICE_ID);
            String cid = hashMap.get(OttContentUtils.OTT_PLAY_START_CID);
            //kind nullチェック
            if (TextUtils.isEmpty(kind)) {
                checkContentOK = false;
                break;
            }
            //kind 値チェック
            if (!OttContentUtils.OTT_PLAY_START_KIND_MAIN.equals(kind) && !OttContentUtils.OTT_PLAY_START_KIND_SUB.equals(kind)) {
                checkContentOK = false;
                break;
            }
            if (TextUtils.isEmpty(serviceId)) {
                checkContentOK = false;
                break;
            }
            if (TextUtils.isEmpty(cid)) {
                checkContentOK = false;
                break;
            }
        }

        return checkContentOK;
    }

    /**
     * 指定されたパラメータをJSONで組み立てて文字列にする.
     *
     * @param deviceId 端末を一意に識別する値
     * @param playType 再生種別 1 : ストリーミング再生 2 : ダウンロード再生
     * @param protocol 1 : MPEG-DASH 2 : HLS
     * @param quality  コンテンツの画質
     * @param availStatus  1.1 : 視聴確認用のリクエスト 1.3 : 通常視聴用のリクエスト
     * @param viewContinueFlg 0:新規視聴時 1:継続視聴時
     * @param contentList 再生情報のリスト
     * @return 組み立て後の文字列
     */
    private String makeSendParameter(final String deviceId, final int playType, final int protocol, final int quality, final String availStatus,
                                     final int viewContinueFlg, final List<HashMap<String, String>> contentList) {
        JSONObject jsonObject = new JSONObject();
        String answerText;
        try {
            jsonObject.put(OttContentUtils.OTT_PLAY_START_DEVICE_ID, deviceId);
            jsonObject.put(OttContentUtils.OTT_PLAY_START_PLAY_TYPE, playType);
            jsonObject.put(OttContentUtils.OTT_PLAY_START_PROTOCOL, protocol);
            //quality
            List<Integer> qualityList = makeIntArry(OttContentUtils.OTT_PLAY_START_QUALITY_1, OttContentUtils.OTT_PLAY_START_QUALITY_2,
                    OttContentUtils.OTT_PLAY_START_QUALITY_3, OttContentUtils.OTT_PLAY_START_QUALITY_4, OttContentUtils.OTT_PLAY_START_QUALITY_5,
                    OttContentUtils.OTT_PLAY_START_QUALITY_6, OttContentUtils.OTT_PLAY_START_QUALITY_7);
            //指定された数字がひとまとめにした中に含まれるか確認
            if (qualityList.indexOf(quality) != -1) {
                jsonObject.put(OttContentUtils.OTT_PLAY_START_QUALITY, quality);
            }
            //availStatus
            List<String> availStatusList = makeStringArry(OttContentUtils.OTT_PLAY_START_AVAIL_STATUS_TEST,
                    OttContentUtils.OTT_PLAY_START_AVAIL_STATUS_RELEASE);
            //指定された数字がひとまとめにした中に含まれるか確認
            if (availStatusList.indexOf(availStatus) != -1) {
                jsonObject.put(OttContentUtils.OTT_PLAY_START_AVAIL_STATUS, availStatus);
            }
            //viewContinueFlg
            List<Integer> viewContinueFlgList = makeIntArry(OttContentUtils.OTT_PLAY_START_VIEW_CONTINUE_FLG_0,
                    OttContentUtils.OTT_PLAY_START_VIEW_CONTINUE_FLG_1);
            //指定された数字がひとまとめにした中に含まれるか確認
            if (viewContinueFlgList.indexOf(viewContinueFlg) != -1) {
                jsonObject.put(OttContentUtils.OTT_PLAY_START_VIEW_CONTINUE_FLG, viewContinueFlg);
            }
            JSONArray jsonArray = new JSONArray();
            //コンテンツ識別子配列をJsonArrayにする
            for (HashMap<String, String> hashMap : contentList) {
                String kind = hashMap.get(OttContentUtils.OTT_PLAY_START_KIND);
                String cid = hashMap.get(OttContentUtils.OTT_PLAY_START_CID);
                String serviceId = hashMap.get(OttContentUtils.OTT_PLAY_START_SERVICE_ID);
                String lid = hashMap.get(OttContentUtils.OTT_PLAY_START_LID);
                String crid = hashMap.get(OttContentUtils.OTT_PLAY_START_CRID);
                JSONObject jo = new JSONObject();
                jo.put(OttContentUtils.OTT_PLAY_START_KIND, kind);
                jo.put(OttContentUtils.OTT_PLAY_START_SERVICE_ID, serviceId);
                jo.put(OttContentUtils.OTT_PLAY_START_CID, cid);
                if (!TextUtils.isEmpty(lid)) {
                    jo.put(OttContentUtils.OTT_PLAY_START_LID, lid);
                }
                if (!TextUtils.isEmpty(crid)) {
                    jo.put(OttContentUtils.OTT_PLAY_START_CRID, crid);
                }
                jsonArray.put(jo);
            }
            //配列をリストと言う名前で追加
            jsonObject.put(OttContentUtils.OTT_PLAY_START_CONTENT_LIST, jsonArray);
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
