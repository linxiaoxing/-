package com.example.detaildemo.data.webapiclient.jsonparser.data;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * リアルタイム再生数上位番組リスト
 */
public class RealTimeRankingList {
    /**リアルタイム再生数上位番組マップ.*/
    private HashMap<String, String> mRealTimeRankingMap = new HashMap<>();

    /** リアルタイム再生数上位番組リスト情報. */
    private List<Map<String, String>> mRealTimeRankingList = null;

    /**
     * リアルタイム再生数上位番組マップ取得.
     * @return リアルタイム再生数上位番組マップ
     */
    public HashMap getRealTimeRankingMap() {
        return mRealTimeRankingMap;
    }

    /**
     * リアルタイム再生数上位番組マップ設定.
     * @param realTimeRankingMap  リアルタイム再生数上位番組マップ
     */
    public void setRealTimeRankingMap(final HashMap<String, String> realTimeRankingMap) {
        this.mRealTimeRankingMap = realTimeRankingMap;
    }

    /**
     * リアルタイム再生数上位番組リスト情報取得.
     * @return リアルタイム再生数上位番組リスト
     */
    public List<Map<String, String>> getRealTimeRankingList() {
        return mRealTimeRankingList;
    }

    /**
     * チャンネルリスト情報設定.
     * @param realTimeRankingList  リアルタイム再生数上位番組リスト
     */
    public void setRealTimeRankingList(final List realTimeRankingList) {
        this.mRealTimeRankingList = realTimeRankingList;
    }
}
