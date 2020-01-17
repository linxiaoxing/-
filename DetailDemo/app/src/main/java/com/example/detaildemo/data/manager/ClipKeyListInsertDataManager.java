package com.example.detaildemo.data.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.ClipKeyListResponse;
import com.example.detaildemo.data.db.dao.ClipKeyListDao;
import com.example.detaildemo.data.db.helper.DataBaseHelper;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.utils.DataBaseUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * クリップキー用InsertDataManager.
 */
public class ClipKeyListInsertDataManager {

    /**
     * コンテキスト.
     */
    private final Context mContext;

    /**
     * コンストラクタ.
     *
     * @param context Activity
     */
    public ClipKeyListInsertDataManager(final Context context) {
        mContext = context;
    }

    /**
     * TvClipAPIの解析結果をDBに格納する.
     *
     * @param type                テーブル種別
     * @param clipKeyListResponse クリップキー一覧 レスポンスデータ
     */
    public synchronized void insertClipKeyListInsert(final ClipKeyListDao.TableTypeEnum type, final ClipKeyListResponse clipKeyListResponse) {
        DTVTLogger.start();

        DataBaseHelper clipKeyListDataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(clipKeyListDataBaseHelper);
        DataBaseManager databaseManager = DataBaseManager.getInstance();
        synchronized (databaseManager) {
            //各種オブジェクト作成
            SQLiteDatabase database = databaseManager.openDatabase();
            database.acquireReference();

            ClipKeyListDao clipKeyListDao = new ClipKeyListDao(database);
            @SuppressWarnings("unchecked")
            List<HashMap<String, String>> hashMaps = clipKeyListResponse.getCkList();

            //DB保存前に前回取得したデータを消去する
            try {
                clipKeyListDao.delete(type);
            } catch (RuntimeException e) {
                DTVTLogger.debug("ClipKeyListInsertDataManager::insertClipKeyListInsert, e.cause=" + e.getCause());
            }

            try {
                DTVTLogger.debug("bulk insert start");
                //DREM-2967の対応
                database.beginTransaction();
                //HashMapの要素とキーを一行ずつ取り出し、DBに格納する
                for (int i = 0; i < hashMaps.size(); i++) {
                    Iterator entries = hashMaps.get(i).entrySet().iterator();
                    ContentValues values = new ContentValues();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String keyName = (String) entry.getKey();
                        String valName = (String) entry.getValue();
                        values.put( DataBaseUtils.fourKFlgConversion(keyName), valName);
                    }
                    clipKeyListDao.insert(type, values);
                }
                database.setTransactionSuccessful();
            } catch (SQLiteException e) {
                DTVTLogger.debug("ClipKeyListInsertDataManager::insertClipKeyListInsert, e.cause=" + e.getCause());
            } finally {
                database.endTransaction();
                DataBaseManager.getInstance().closeDatabase();
                DTVTLogger.debug("bulk insert end");
            }
        }
        DTVTLogger.end();
    }

    /**
     * クリップ登録成功後の行データ挿入.
     *
     * @param tableType テーブル種別(TV/VOD)
     * @param crId      コンテンツ識別子
     * @param serviceId サービスID
     * @param eventId   イベントID
     * @param titleId   タイトルID
     */
    public void insertRowSqlStart(
            final ClipKeyListDao.TableTypeEnum tableType, final String crId, final String serviceId,
            final String eventId, final String titleId) {
        DTVTLogger.start();

        DataBaseHelper clipKeyListDataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(clipKeyListDataBaseHelper);
        DataBaseManager databaseManager = DataBaseManager.getInstance();
        synchronized (databaseManager) {
            try {
                SQLiteDatabase database = databaseManager.openDatabase();
                database.acquireReference();
                ClipKeyListDao clipKeyListDao = new ClipKeyListDao(database);

                //コンテンツデータ作成
                ContentValues values = new ContentValues();
                values.put(JsonConstants.META_RESPONSE_CRID, crId);
                values.put(JsonConstants.META_RESPONSE_SERVICE_ID, serviceId);
                values.put(JsonConstants.META_RESPONSE_EVENT_ID, eventId);
                values.put(JsonConstants.META_RESPONSE_TITLE_ID, titleId);
                clipKeyListDao.insert(tableType, values);
            } catch (SQLiteException e) {
                DTVTLogger.debug("ClipKeyListInsertDataManager::insertRowSqlStart, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        DTVTLogger.end();
    }

    /**
     * 条件に一致する行を削除する.
     *
     * @param crId      コンテンツ識別子
     * @param serviceId サービスID
     * @param eventId イベントID
     * @param titleId   タイトルID
     */
    public void deleteRowSqlStart(final String crId, final String serviceId,
                                  final String eventId, final String titleId) {
        DTVTLogger.start();

        DataBaseHelper clipKeyListDataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(clipKeyListDataBaseHelper);
        DataBaseManager databaseManager = DataBaseManager.getInstance();
        synchronized (databaseManager) {
            try {
                SQLiteDatabase database = databaseManager.openDatabase();
                database.acquireReference();
                ClipKeyListDao clipKeyListDao = new ClipKeyListDao(database);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append( JsonConstants.META_RESPONSE_CRID);
                stringBuilder.append("=?");
                String[] columns;
                if (!TextUtils.isEmpty(titleId)) {
                    stringBuilder.append(" OR ");
                    stringBuilder.append(JsonConstants.META_RESPONSE_TITLE_ID);
                    stringBuilder.append("=?");
                    columns = new String[]{crId, titleId};
                } else if (!TextUtils.isEmpty(serviceId) && !TextUtils.isEmpty(eventId)) {
                    stringBuilder.append(" OR ");
                    stringBuilder.append(JsonConstants.META_RESPONSE_SERVICE_ID);
                    stringBuilder.append("=?");
                    stringBuilder.append(" AND ");
                    stringBuilder.append(JsonConstants.META_RESPONSE_EVENT_ID);
                    stringBuilder.append("=?");
                    columns = new String[]{crId, serviceId, eventId};
                } else {
                    columns = new String[]{crId};
                }
                clipKeyListDao.deleteRowData(ClipKeyListDao.TableTypeEnum.TV, stringBuilder.toString(), columns);
                clipKeyListDao.deleteRowData(ClipKeyListDao.TableTypeEnum.VOD, stringBuilder.toString(), columns);
            } catch (SQLiteException e) {
                DTVTLogger.debug("ClipKeyListInsertDataManager::deleteRowSqlStart, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        DTVTLogger.end();
    }
}
