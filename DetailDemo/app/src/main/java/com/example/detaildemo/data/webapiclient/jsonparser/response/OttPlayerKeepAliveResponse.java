package com.example.detaildemo.data.webapiclient.jsonparser.response;


/**
 * 視聴開始取得レスポンス.
 */
public class OttPlayerKeepAliveResponse {
    /** ステータス.*/
    private String mStatus;
    /** エラー番号.*/
    private String mErrorno;

    /**
     * ステータス取得.
     * @return ステータス
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * ステータス設定.
     * @param status ステータス
     */
    public void setStatus(final String status) {
        mStatus = status;
    }

    /**
     * エラー番号取得.
     * @return エラー番号
     */
    public String getErrorNo() {
        return mErrorno;
    }

    /**
     * エラー番号設定.
     * @param errorNo エラー番号
     */
    public void setErrorNo(final String errorNo) {
        this.mErrorno = errorNo;
    }
}
