package com.example.detaildemo.data.manager;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.db.helper.DataBaseHelper;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.utils.DateUtils;

/**
 * データベーステーブル再構築用クラス.
 */
public class RebuildDatabaseTableManager {
    /** コンテキスト. */
    private Context mContext;

    /**
     * コンストラクタ.
     * @param context コンテキスト
     */
    public RebuildDatabaseTableManager(final Context context) {
        this.mContext = context;
    }

    /**
     * データベース(db_data)内の全テーブルの再構築.
     */
    @SuppressWarnings("OverlyLongMethod")
    public void allTableRebuild() {
        DTVTLogger.start();
        //各種オブジェクト作成
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
        DataBaseManager.initializeInstance(dataBaseHelper);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        synchronized (dataBaseManager) {
            try {
                SQLiteDatabase database = dataBaseManager.openDatabase();
                database.acquireReference();
                DateUtils.clearLastDate(mContext);

                //各種データベースの再構築
                DataBaseHelper.dropAllTable(database);
                DataBaseHelper.createAllTable(database);
            } catch (SQLException e) {
                DTVTLogger.debug("RebuildDatabaseTableManager::allTableRebuild, rebuild " + "table failed, cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
        DTVTLogger.end();
    }
}
