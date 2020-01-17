package com.example.detaildemo.data.webapiclient.jsonparser.response;


import com.example.detaildemo.data.provider.data.OttPlayerStartData;

import java.util.ArrayList;
import java.util.List;

/**
 * 視聴開始取得レスポンス.
 */
public class OttPlayerStartResponse {
    /** ステータス.*/
    private String mStatus;
    /** playToken.*/
    private String mPlayToken;
    /** 視聴開始情報.*/
    private List<OttPlayerStartData> mOttPlayerStartData;

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
     * トークン取得.
     * @return トークン
     */
    public String getPlayToken() {
        return mPlayToken;
    }

    /**
     * トークン設定.
     * @param playToken トークン
     */
    public void setPlayToken(final String playToken) {
        this.mPlayToken = playToken;
    }

    /**
     * 視聴開始情報.
     * @return 視聴開始情報
     */
    public List<OttPlayerStartData> getOttPlayerStartData() {
        return mOttPlayerStartData;
    }

    /**
     * 視聴開始情報設定.
     * @param ottPlayerStartData 視聴開始情報
     */
    public void setOttPlayerStartData(final List<OttPlayerStartData> ottPlayerStartData) {
        this.mOttPlayerStartData = ottPlayerStartData;
    }

    /**
     * コンストラクタ.
     */
    public OttPlayerStartResponse() {
        mOttPlayerStartData = new ArrayList<>();
    }
}
