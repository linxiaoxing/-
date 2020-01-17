package com.example.detaildemo.data.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.data.db.dao.DownLoadListDao;
import com.example.detaildemo.data.db.helper.DataBaseHelperDownload;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.service.download.DownloadData;

import java.util.List;
import java.util.Map;

/**
 * DownLoadListDataManager.
 */
public class DownLoadListDataManager {
    /**
     * コンテキスト.
     */
    private final Context mContext;
    /**
     * ダウンロードOK.
     */
    public final static String DOWNLOAD_OK = "OK";

    /**
     * コンストラクタ.
     *
     * @param context Activity
     */
    public DownLoadListDataManager(final Context context) {
        mContext = context;
    }

    /**
     * 持ち出しのダウンロード情報をDBに格納する.
     * @param downloadData ダウンロード情報
     */
    public void insertDownload(final DownloadData downloadData) {

        try {
            //各種オブジェクト作成
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downloadListDao = new DownLoadListDao(database);

            //DB保存前に前回取得したデータは全消去する
            downloadListDao.deleteByItemId(downloadData.getItemId(), downloadData.getSaveFile(), false);

            //HashMapの要素とキーを一行ずつ取り出し、DBに格納する
            ContentValues values = new ContentValues();
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_ITEM_ID, downloadData.getItemId());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_URL, downloadData.getUrl());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_TITLE, downloadData.getTitle());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_SIZE, downloadData.getTotalSize());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_DURATION, downloadData.getDuration());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_RESOLUTION, downloadData.getResolution());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_BITRATE, downloadData.getBitrate());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_UPNP_ICON, downloadData.getUpnpIcon());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_URL, downloadData.getSaveFile());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_TYPE, downloadData.getVideoType());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_CHANNELNAME, downloadData.getChannelName());
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_DATE, downloadData.getDate());
            downloadListDao.insert(values);
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::insertDownload, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
    }

    /**
     * 持ち出しのダウンロード情報をDBから削除する.
     *
     */
    public void deleteDownloadAllContents() {

        try {
            //各種オブジェクト作成
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();

            //タイミングによっては、既にクローズ済みと言う例外が返ってくるので回避
            if (database.isOpen()) {
                database.acquireReference();
                DownLoadListDao downloadListDao = new DownLoadListDao(database);

                downloadListDao.delete();
            }
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::deleteDownloadAllContents, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
    }

    /**
     * 持ち出しのダウンロード情報を削除する.
     * @param itemId  アイテムID.
     * @param path  パス.
     * @param completed  ダウンロード完了フラグ.
     */
    public void deleteDownloadContentByItemId(final String itemId, final String path, final boolean completed) {

        try {
            //各種オブジェクト作成
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downloadListDao = new DownLoadListDao(database);

            downloadListDao.deleteByItemId(itemId, path, completed);
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::deleteDownloadContentByItemId, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
    }

    /**
     * 持ち出し未完了ダウンロード情報を削除する.
     *
     * @return 削除した件数.
     */
    public int deleteDownloadContentNotCompleted() {
        try {
            //各種オブジェクト作成
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downloadListDao = new DownLoadListDao(database);

            return downloadListDao.deleteNotCompleted();
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::deleteDownloadContentByStatus, e.cause=" + e.getCause());
            return 0;
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
    }

    /**
     * 持ち出しのダウンロード情報をDBに更新する.
     * @param itemId アイテムID.
     */
    public void updateDownloadByItemId(final String itemId) {

        try {
            //各種オブジェクト作成
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downloadListDao = new DownLoadListDao(database);

            //HashMapの要素とキーを一行ずつ取り出し、DBに格納する
            ContentValues values = new ContentValues();
            values.put(DataBaseConstants.DOWNLOAD_LIST_COLUM_DOWNLOAD_STATUS, DOWNLOAD_OK);
            downloadListDao.updateByItemId(values, itemId);
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::updateDownloadByItemId, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
    }

    /**
     * 録画一覧データを返却する.
     *
     * @return list ダウンロードしたデータ状態.
     */
    public List<Map<String, String>> selectDownLoadListVideoData() {
        List<Map<String, String>> list = null;
        try {
            //ホーム画面に必要な列を列挙する
            String[] columns = {DataBaseConstants.DOWNLOAD_LIST_COLUM_DOWNLOAD_STATUS, DataBaseConstants.DOWNLOAD_LIST_COLUM_ITEM_ID,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_URL, DataBaseConstants.DOWNLOAD_LIST_COLUM_TITLE};

            //Daoクラス使用準備
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downLoadListDao = new DownLoadListDao(database);

            //持ち出し画面用データ取得
            list = downLoadListDao.findDownLoadList(columns);
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::selectDownLoadListVideoData, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
        return list;
    }

    /**
     * 録画一覧データを返却する.
     *
     * @return list ダウンロードしたデータ状態.
     */
    public List<Map<String, String>> selectDownLoadList() {
        List<Map<String, String>> list = null;
        try {
            //ホーム画面に必要な列を列挙する
            String[] columns = {DataBaseConstants.DOWNLOAD_LIST_COLUM_ITEM_ID, DataBaseConstants.DOWNLOAD_LIST_COLUM_URL,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_DIDL, DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_HOST,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_PORT, DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_URL,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_TYPE, DataBaseConstants.DOWNLOAD_LIST_COLUM_DOWNLOAD_SIZE,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_CHANNELNAME, DataBaseConstants.DOWNLOAD_LIST_COLUM_DATE,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_DOWNLOAD_STATUS, DataBaseConstants.DOWNLOAD_LIST_COLUM_SIZE,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_DURATION, DataBaseConstants.DOWNLOAD_LIST_COLUM_RESOLUTION,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_UPNP_ICON, DataBaseConstants.DOWNLOAD_LIST_COLUM_BITRATE,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_IS_SUPPORTED_BYTE_SEEK, DataBaseConstants.DOWNLOAD_LIST_COLUM_IS_SUPPORTED_TIME_SEEK,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_IS_AVAILABLE_CONNECTION_STALLING, DataBaseConstants.DOWNLOAD_LIST_COLUM_IS_LIVE_MODE,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_IS_REMOTE, DataBaseConstants.DOWNLOAD_LIST_COLUM_TITLE,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_CONTENTFORMAT};

            //Daoクラス使用準備
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downLoadListDao = new DownLoadListDao(database);

            //持ち出し画面用データ取得
            list = downLoadListDao.findAllDownloadList(columns);
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::selectDownLoadList, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
        return list;
    }

    /**
     * 該当コンテンツIDのデータを返却する.
     *
     * @return list ダウンロードした該当コンテンツIDのデータ
     */
    public List<Map<String, String>> selectDownLoadListById(final String itemId) {
        List<Map<String, String>> list = null;
        try {
            String[] columns = {DataBaseConstants.DOWNLOAD_LIST_COLUM_ITEM_ID,
                    DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_URL};

            //Daoクラス使用準備
            DataBaseHelperDownload downLoadListDBHelper = new DataBaseHelperDownload(mContext);
            DataBaseManager.clearDownloadInfo();
            DataBaseManager.initializeInstance(downLoadListDBHelper);
            SQLiteDatabase database = DataBaseManager.getDownloadInstance().openDownloadDatabase();
            database.acquireReference();
            DownLoadListDao downLoadListDao = new DownLoadListDao(database);

            //持ち出し画面用データ取得
            list = downLoadListDao.findByItemId(columns, itemId);
        } catch (SQLiteException e) {
            DTVTLogger.debug("DownLoadListDataManager::selectDownLoadListById, e.cause=" + e.getCause());
        } finally {
            DataBaseManager.getDownloadInstance().closeDownloadDatabase();
        }
        return list;
    }
}
