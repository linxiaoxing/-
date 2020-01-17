package com.example.detaildemo.data.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.db.dao.RoleListDao;
import com.example.detaildemo.data.db.helper.DataBaseHelper;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RoleListMetaData;
import com.example.detaildemo.utils.DateUtils;

import java.util.List;

/**
 * RoleListDataManager.
 */
public class RoleListInsertDataManager {

    /**
     * コンテキスト.
     */
    private final Context mContext;

    /**
     * コンストラクタ.
     *
     * @param context Activity
     */
    public RoleListInsertDataManager(final Context context) {
        mContext = context;
    }

    /**
     * ChannelAPIの解析結果をDBに格納する.
     * @param roleList ロールリスト
     */
    public void insertRoleList(final List<RoleListMetaData> roleList) {

        //取得データが空の場合は更新しないで、有効期限をクリアする
        if (roleList == null || roleList.size() < 1) {
            DateUtils.clearLastProgramDate(mContext, DateUtils.ROLELIST_LAST_UPDATE);
            return;
        }

        //各種オブジェクト作成
        DataBaseHelper channelListDataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(channelListDataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        synchronized (dataBaseManager) {
            try {
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();
                RoleListDao roleListDao = new RoleListDao(database);

                //DB保存前に前回取得したデータは全消去する
                roleListDao.delete();

                //HashMapの要素とキーを一行ずつ取り出し、DBに格納する
                for (int i = 0; i < roleList.size(); i++) {
                    RoleListMetaData roleData = roleList.get(i);
                    ContentValues values = new ContentValues();
                    values.put( JsonConstants.META_RESPONSE_CONTENTS_ID, roleData.getId());
                    values.put(JsonConstants.META_RESPONSE_CONTENTS_NAME, roleData.getName());
                    roleListDao.insert(values);
                }
                //データ保存日時を格納
                DateUtils dateUtils = new DateUtils(mContext);
                dateUtils.addLastProgramDate( DateUtils.ROLELIST_LAST_UPDATE);
            } catch (SQLiteException e) {
                DTVTLogger.debug("RoleListInsertDataManager::insertRoleList, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
    }
}
