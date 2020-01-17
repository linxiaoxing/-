package com.example.detaildemo.data.webapiclient.jsonparser.response;

import java.io.Serializable;

/**
 * OTT視聴終了：正常時レスポンスデータ.
 */
public class OttStopResponse implements Serializable{
    private static final long serialVersionUID = 8048714539214936603L;

    /**ステータス.*/
    private String mStatus; // status
    /**ステータス.*/
    private static final String OTT_STOP_FIXED_STATUS = "";
    /**
     * ステータス初期値取得.
     * @return ステータス
     */
    public String getStatus() {
        return mStatus;
    }
    /**
     * ステータス設定.
     * @param  status ステータス
     */
    public void setStatus(final String status) {
        mStatus = status;
    }

    /**
     * コンストラクタ.
     */
    public OttStopResponse() {
        mStatus = OTT_STOP_FIXED_STATUS;
    }
}