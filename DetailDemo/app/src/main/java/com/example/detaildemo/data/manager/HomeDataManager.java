package com.example.detaildemo.data.manager;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.data.db.dao.ChannelListDao;
import com.example.detaildemo.data.db.dao.DailyRankListDao;
import com.example.detaildemo.data.db.dao.RoleListDao;
import com.example.detaildemo.data.db.dao.TvScheduleListDao;
import com.example.detaildemo.data.db.dao.WatchListenVideoListDao;
import com.example.detaildemo.data.db.dao.WeeklyRankListDao;
import com.example.detaildemo.data.db.helper.DataBaseHelper;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Home画面用DBManager.
 */
public class HomeDataManager {

    /**
     * コンテキスト.
     */
    private final Context mContext;

    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     */
    public HomeDataManager(final Context context) {
        this.mContext = context;
    }

    /**
     * ホーム画面用視聴中ビデオデータを返却する.
     *
     * @return list 視聴中ビデオデータリスト
     */
    public List<Map<String, String>> selectWatchingVideoHomeData() {

        List<Map<String, String>> list = new ArrayList<>();

        //ホーム画面に必要な列とコンテンツ詳細へ連携する列を列挙する
        String[] columns = {JsonConstants.META_RESPONSE_THUMB_448,
                JsonConstants.META_RESPONSE_THUMB_640,
                JsonConstants.META_RESPONSE_DTV_THUMB_448,
                JsonConstants.META_RESPONSE_DTV_THUMB_640,
                JsonConstants.META_RESPONSE_DTV,
                JsonConstants.META_RESPONSE_AVAIL_START_DATE,
                JsonConstants.META_RESPONSE_AVAIL_END_DATE,
                JsonConstants.META_RESPONSE_VOD_START_DATE,
                JsonConstants.META_RESPONSE_VOD_END_DATE,
                JsonConstants.META_RESPONSE_TV_SERVICE,
                JsonConstants.META_RESPONSE_CRID,
                JsonConstants.META_RESPONSE_CONTENT_TYPE,
                JsonConstants.META_RESPONSE_TITLE,
                JsonConstants.META_RESPONSE_DISP_TYPE};

        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        synchronized (dataBaseManager) {
            try {
                //Daoクラス使用準備
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();

                //データ存在チェック
                if (!DataBaseUtils.isCachingRecord(database, DataBaseConstants.WATCH_LISTEN_VIDEO_TABLE_NAME)) {
                    DataBaseManager.getInstance().closeDatabase();
                    return list;
                }

                WatchListenVideoListDao watchListenVideoListDao = new WatchListenVideoListDao(database);

                //ホーム画面用データ取得
                list = watchListenVideoListDao.findById(columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("HomeDataManager::selectWatchingVideoHomeData, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        return list;
    }

    /**
     * ホーム画面用CH一覧データを返却する.
     *
     * @return list CH一覧データリスト
     */
    public List<Map<String, String>> selectChannelListHomeData() {

        List<Map<String, String>> list = new ArrayList<>();

        //ホーム画面に必要な列を列挙する
        String[] columns = {JsonConstants.META_RESPONSE_CHNO, JsonConstants.META_RESPONSE_THUMB_448,
                JsonConstants.META_RESPONSE_TITLE, JsonConstants.META_RESPONSE_AVAIL_START_DATE,
                JsonConstants.META_RESPONSE_AVAIL_END_DATE, JsonConstants.META_RESPONSE_DISP_TYPE,
                JsonConstants.META_RESPONSE_SERVICE_ID, JsonConstants.META_RESPONSE_CID,
                JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ, JsonConstants.META_RESPONSE_SERVICE,
                DataBaseConstants.UNDER_BAR_FOUR_K_FLG, JsonConstants.META_RESPONSE_OTT_FLG,
                JsonConstants.META_RESPONSE_OTT_DRMFLG, JsonConstants.META_RESPONSE_FULL_HD,
                JsonConstants.META_RESPONSE_EXTERNAL_OUTPUT};

        //Daoクラス使用準備
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();

        synchronized (dataBaseManager) {
            try {
                //Daoクラス使用準備
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();

                //データ存在チェック
                if (!DataBaseUtils.isCachingRecord(database, DataBaseConstants.CHANNEL_LIST_TABLE_NAME)) {
                    DataBaseManager.getInstance().closeDatabase();
                    return list;
                }

                ChannelListDao channelListDao = new ChannelListDao(database);

                //ホーム画面用データ取得
                list = channelListDao.findById(columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("HomeDataManager::selectChannelListHomeData, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        return list;
    }

    /**
     * ホーム画面用今日のランキング一覧データを返却する.
     *
     * @return list 今日のランキング一覧データリスト
     */
    public List<Map<String, String>> selectDailyRankListHomeData() {

        List<Map<String, String>> list = new ArrayList<>();

        //ホーム画面に必要な列を列挙する
        String[] columns = {JsonConstants.META_RESPONSE_THUMB_448, JsonConstants.META_RESPONSE_TITLE,
                JsonConstants.META_RESPONSE_DISP_TYPE, JsonConstants.META_RESPONSE_DUR,
                JsonConstants.META_RESPONSE_SEARCH_OK, JsonConstants.META_RESPONSE_CRID,
                JsonConstants.META_RESPONSE_SERVICE_ID, JsonConstants.META_RESPONSE_EVENT_ID,
                JsonConstants.META_RESPONSE_TITLE_ID, JsonConstants.META_RESPONSE_R_VALUE,
                JsonConstants.META_RESPONSE_PUBLISH_START_DATE, JsonConstants.META_RESPONSE_PUBLISH_END_DATE,
                JsonConstants.META_RESPONSE_DISP_TYPE, JsonConstants.META_RESPONSE_CONTENT_TYPE,
                JsonConstants.META_RESPONSE_DTV, JsonConstants.META_RESPONSE_TV_SERVICE,
                JsonConstants.META_RESPONSE_DTV_TYPE, JsonConstants.META_RESPONSE_CID,
                JsonConstants.META_RESPONSE_THUMB_640, JsonConstants.META_RESPONSE_CHNO,
                JsonConstants.META_RESPONSE_AVAIL_START_DATE, JsonConstants.META_RESPONSE_AVAIL_END_DATE,
                JsonConstants.META_RESPONSE_VOD_START_DATE, JsonConstants.META_RESPONSE_VOD_END_DATE,
                JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ};

        //Daoクラス使用準備
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();

        synchronized (dataBaseManager) {
            try {
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();

                //データ存在チェック
                if (!DataBaseUtils.isCachingRecord(database, DataBaseConstants.DAILYRANK_LIST_TABLE_NAME)) {
                    DataBaseManager.getInstance().closeDatabase();
                    return list;
                }

                DailyRankListDao dailyRankListDao = new DailyRankListDao(database);

                //ホーム画面用データ取得
                list = dailyRankListDao.findById(columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("HomeDataManager::selectDailyRankListHomeData, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        return list;
    }

    /**
     * ホーム画面用CH毎番組表データを返却する.
     *
     * @return list Now On Air データ
     */
    public synchronized List<Map<String, String>> selectTvScheduleListHomeData() {

        List<Map<String, String>> list = new ArrayList<>();

        //ホーム画面に必要な列を列挙する
        String[] columns = {JsonConstants.META_RESPONSE_THUMB_448, JsonConstants.META_RESPONSE_TITLE,
                JsonConstants.META_RESPONSE_PUBLISH_START_DATE, JsonConstants.META_RESPONSE_PUBLISH_END_DATE,
                JsonConstants.META_RESPONSE_DISP_TYPE, JsonConstants.META_RESPONSE_CONTENT_TYPE,
                JsonConstants.META_RESPONSE_CID, JsonConstants.META_RESPONSE_SERVICE_ID,
                JsonConstants.META_RESPONSE_THUMB_640, JsonConstants.META_RESPONSE_CHNO,
                JsonConstants.META_RESPONSE_CRID, JsonConstants.META_RESPONSE_TV_SERVICE,
                JsonConstants.META_RESPONSE_R_VALUE, JsonConstants.META_RESPONSE_RATING,
                JsonConstants.META_RESPONSE_SYNOP, JsonConstants.META_RESPONSE_EVENT_ID,
                JsonConstants.META_RESPONSE_SERVICE_ID, JsonConstants.META_RESPONSE_OTT_FLG,
                JsonConstants.META_RESPONSE_OTT_MASK};

        //Daoクラス使用準備
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        synchronized (dataBaseManager) {
            try {
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();

                //データ存在チェック
                if (!DataBaseUtils.isCachingRecord(database, DataBaseConstants.TV_SCHEDULE_LIST_TABLE_NAME)) {
                    DataBaseManager.getInstance().closeDatabase();
                    return list;
                }

                TvScheduleListDao tvScheduleListDao = new TvScheduleListDao(database);

                //ホーム画面用データ取得
                list = tvScheduleListDao.findById(columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("HomeDataManager::selectTvScheduleListHomeData, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        return list;
    }

    /**
     * ホーム画面用週間ランキングデータを返却する.
     *
     * @return list 週間ランキングデータ
     */
    public List<Map<String, String>> selectWeeklyRankListHomeData() {

        List<Map<String, String>> list = new ArrayList<>();

        //ホーム画面に必要な列を列挙する
        String[] columns = {JsonConstants.META_RESPONSE_THUMB_448, JsonConstants.META_RESPONSE_TITLE,
                JsonConstants.META_RESPONSE_SEARCH_OK, JsonConstants.META_RESPONSE_CRID,
                JsonConstants.META_RESPONSE_SERVICE_ID, JsonConstants.META_RESPONSE_EVENT_ID,
                JsonConstants.META_RESPONSE_TITLE_ID, JsonConstants.META_RESPONSE_R_VALUE,
                JsonConstants.META_RESPONSE_PUBLISH_START_DATE, JsonConstants.META_RESPONSE_PUBLISH_END_DATE,
                JsonConstants.META_RESPONSE_DISP_TYPE, JsonConstants.META_RESPONSE_CONTENT_TYPE,
                JsonConstants.META_RESPONSE_DTV, JsonConstants.META_RESPONSE_DTV_TYPE,
                JsonConstants.META_RESPONSE_TV_SERVICE, JsonConstants.META_RESPONSE_CID,
                JsonConstants.META_RESPONSE_THUMB_640, JsonConstants.META_RESPONSE_CHNO,
                JsonConstants.META_RESPONSE_DUR, JsonConstants.META_RESPONSE_AVAIL_START_DATE,
                JsonConstants.META_RESPONSE_AVAIL_END_DATE, JsonConstants.META_RESPONSE_VOD_START_DATE,
                JsonConstants.META_RESPONSE_VOD_END_DATE, JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ
        };

        //Daoクラス使用準備
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        synchronized (dataBaseManager) {

            try {
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();

                //データ存在チェック
                if (!DataBaseUtils.isCachingRecord(database, DataBaseConstants.WEEKLYRANK_LIST_TABLE_NAME)) {
                    DataBaseManager.getInstance().closeDatabase();
                    return list;
                }

                WeeklyRankListDao weeklyRankListDao = new WeeklyRankListDao(database);

                //ホーム画面用データ取得
                list = weeklyRankListDao.findById(columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("HomeDataManager::selectWeeklyRankListHomeData, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        return list;
    }

    /**
     * ロールリストデータを返却する.
     *
     * @return list ロールリスト
     */
    public List<Map<String, String>> selectRoleListData() {

        List<Map<String, String>> list = new ArrayList<>();

        //ホーム画面に必要な列を列挙する
        String[] columns = {JsonConstants.META_RESPONSE_CONTENTS_ID, JsonConstants.META_RESPONSE_CONTENTS_NAME};

        //Daoクラス使用準備
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        synchronized (dataBaseManager) {
            try {
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();

                //データ存在チェック
                if (!DataBaseUtils.isCachingRecord(database, DataBaseConstants.ROLE_LIST_TABLE_NAME)) {
                    DataBaseManager.getInstance().closeDatabase();
                    return list;
                }

                RoleListDao roleListDao = new RoleListDao(database);

                //ホーム画面用データ取得
                list = roleListDao.findById(columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("HomeDataManager::selectRoleListData, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        return list;
    }
}