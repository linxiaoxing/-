package com.example.detaildemo.data.db.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.utils.StringUtils;

/**
 * 番組情報保存用DBHelper.
 */
public class DataBaseHelperChannel extends SQLiteOpenHelper{

    /**
     * DBVersion.
     */
    private static final int DATABASE_VERSION = 2;
    /**
     * drop table if exists.
     */
    private static final String DROP_TABLE_IF_EXISTS = "drop table if exists ";
    /**
     * 「TV番組表」テーブルの削除用SQL.
     */
    private static final String DROP_TV_SCHEDULE_TABLE_SQL = StringUtils.getConnectStrings(
            DROP_TABLE_IF_EXISTS, DataBaseConstants.TV_SCHEDULE_LIST_TABLE_NAME);
    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     * @param name DB名
     */
    public DataBaseHelperChannel(final Context context, final String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( DataBaseConstants.CREATE_TABLE_TV_SCHEDULE_SQL);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
        if (oldVersion < newVersion) {
            dropScheduleChannelList(sqLiteDatabase);
            createScheduleChannelList(sqLiteDatabase);
        }
    }

    /**
     * createScheduleChannelList.
     * @param sqLiteDatabase sqLiteDatabase
     */
    private void createScheduleChannelList(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataBaseConstants.CREATE_TABLE_TV_SCHEDULE_SQL);
    }

    /**
     * dropScheduleChannelList.
     * @param sqLiteDatabase sqLiteDatabase
     */
    private void dropScheduleChannelList(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DROP_TV_SCHEDULE_TABLE_SQL);
    }
}

