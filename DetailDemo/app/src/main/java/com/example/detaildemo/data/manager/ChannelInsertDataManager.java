package com.example.detaildemo.data.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.channel.ChannelList;
import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.data.db.dao.ChannelListDao;
import com.example.detaildemo.data.db.helper.DataBaseHelper;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.utils.DataBaseUtils;
import com.example.detaildemo.utils.DateUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * チャンネルリストデータ管理.
 */
public class ChannelInsertDataManager {

    /**
     * コンテキスト.
     */
    private final Context mContext;

    /**
     * コンストラクタ.
     *
     * @param context Activity
     */
    public ChannelInsertDataManager(final Context context) {
        mContext = context;
    }

    /**
     * チャンネル一覧の情報をDBに格納する.
     * @param channelList  チャンネルリスト情報
     */
    public void insertChannelInsertList(final ChannelList channelList) {

        DTVTLogger.start();

        //取得データが空の場合は更新しないで、有効期限をクリアする
        if (channelList == null || channelList.getChannelList() == null || channelList.getChannelList().size() == 0) {
            DateUtils.clearLastProgramDate(mContext, DateUtils.CHANNEL_LAST_UPDATE);
            return;
        }

        DataBaseHelper channelListDataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(channelListDataBaseHelper);
        DataBaseManager databaseManager = DataBaseManager.getInstance();

        synchronized (databaseManager) {
            //各種オブジェクト作成
            SQLiteDatabase database = databaseManager.openDatabase();
            database.acquireReference();
            ChannelListDao channelListDao = new ChannelListDao(database);
            List<Map<String, String>> hashMaps = new ArrayList<>(channelList.getChannelList());
            try {
                DTVTLogger.debug("bulk insert start");
                database.beginTransaction();
                //DB保存前に前回取得したデータは全消去する
                //日付とチャンネルを管理し、それらが一致するデータだけを消す事.またキャッシュ期限もその単位で管理する
                channelListDao.delete();

                //HashMapの要素とキーを一行ずつ取り出し、DBに格納する
                for (int i = 0; i < hashMaps.size(); i++) {
                    Iterator entries = hashMaps.get(i).entrySet().iterator();

                    ContentValues values = new ContentValues();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String keyName = (String) entry.getKey();
                        String valName = (String) entry.getValue();
                        if (JsonConstants.META_RESPONSE_AVAIL_START_DATE.equals(keyName)) {
                            values.put( DataBaseConstants.UPDATE_DATE, !TextUtils.isEmpty(valName) ? valName.substring(0, 10) : "");
                        }
                        values.put( DataBaseUtils.fourKFlgConversion(keyName), valName);
                    }
                    channelListDao.insert(values);
                }
                //データ保存日時を格納
                DateUtils dateUtils = new DateUtils(mContext);
                dateUtils.addLastProgramDate( DateUtils.CHANNEL_LAST_UPDATE);
                database.setTransactionSuccessful();
            } catch (SQLiteException e) {
                DTVTLogger.debug("ChannelInsertDataManager::insertChannelInsertList, e.cause=" + e.getCause());
            } finally {
                database.endTransaction();
                DataBaseManager.getInstance().closeDatabase();
                DTVTLogger.debug("bulk insert end");
            }
        }
        DTVTLogger.end();
    }
}