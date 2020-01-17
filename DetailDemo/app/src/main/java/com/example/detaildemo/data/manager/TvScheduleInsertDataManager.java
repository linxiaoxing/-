package com.example.detaildemo.data.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.ScheduleInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfoList;
import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.data.db.dao.TvScheduleListDao;
import com.example.detaildemo.data.db.helper.DataBaseHelper;
import com.example.detaildemo.data.db.helper.DataBaseHelperChannel;
import com.example.detaildemo.data.db.helper.DataBaseManager;
import com.example.detaildemo.data.webapiclient.jsonparser.data.TvScheduleList;
import com.example.detaildemo.utils.DataBaseUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 番組情報保存用DataManager.
 */
public class TvScheduleInsertDataManager {

    /**
     * コンテキスト.
     */
    private Context mContext = null;

    /**
     * コンストラクタ.
     *
     * @param context Activity
     */
    public TvScheduleInsertDataManager(final Context context) {
        mContext = context;
    }

    /**
     * TvScheduleAPIの解析結果をDBに格納する(Now On Air用).
     *
     * @param tvScheduleList チャンネル詳細情報
     */
    public void insertTvScheduleInsertList(final TvScheduleList tvScheduleList) {

        //取得データが空の場合は更新しないで、有効期限をクリアする
        if (tvScheduleList == null || tvScheduleList.geTvsList() == null
                || tvScheduleList.geTvsList().size() < 1 || tvScheduleList.geTvsList().get(0).isEmpty()) {
            DateUtils.clearLastProgramDate(mContext, DateUtils.TV_SCHEDULE_LAST_INSERT);
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
                TvScheduleListDao tvScheduleListDao = new TvScheduleListDao(database);
                @SuppressWarnings("unchecked")
                List<Map<String, String>> hashMaps = tvScheduleList.geTvsList();

                //DB保存前に前回取得したデータは全消去する
                tvScheduleListDao.delete();

                //HashMapの要素とキーを一行ずつ取り出し、DBに格納する
                for (int i = 0; i < hashMaps.size(); i++) {
                    Iterator entries = hashMaps.get(i).entrySet().iterator();
                    ContentValues values = new ContentValues();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String keyName = (String) entry.getKey();
                        String valName = (String) entry.getValue();
                        if (JsonConstants.META_RESPONSE_PUBLISH_START_DATE.equals(keyName)) {
                            values.put(DataBaseConstants.UPDATE_DATE, !TextUtils.isEmpty(valName) ? valName.substring(0, 10) : "");
                        }
                        values.put( DataBaseUtils.fourKFlgConversion(keyName), valName);
                    }
                    tvScheduleListDao.insert(values);
                }
                //データ保存日時を格納
                DateUtils dateUtils = new DateUtils(mContext);
                dateUtils.addLastDate(DateUtils.TV_SCHEDULE_LAST_INSERT);
            } catch (SQLiteException e) {
                DTVTLogger.debug("TvScheduleInsertDataManager::insertTvScheduleInsertList, e.cause=" + e.getCause());
            } finally {
                DataBaseManager.getInstance().closeDatabase();
            }
        }
    }

    /**
     * TvScheduleAPIの解析結果をDBに格納する(番組表用).
     *
     * @param channelInfoList チャンネル詳細情報
     * @param chInfoDate      チャンネル情報取得日付
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public synchronized void insertTvScheduleInsertList(final ChannelInfoList channelInfoList, final String chInfoDate) {
        DTVTLogger.start();
        if (channelInfoList.getChannels().size() == 0) {
            //保存するチャンネル情報が無いため保存せず戻る
            return;
        }
        String chInfoGetDate = StringUtils.getChDateInfo(chInfoDate);

        //チャンネル情報取得日のDBフォルダが作成されているか確認する
        String filesDir = mContext.getFilesDir().getAbsolutePath();
        String dbChannelDir = StringUtils.getConnectStrings(filesDir, "/../databases/channel");
        String dbDir = StringUtils.getConnectStrings(dbChannelDir, "/" + chInfoGetDate);
        File channelDir = new File(dbChannelDir);
        if (!channelDir.isDirectory()) {
            //取得日のフォルダが存在しないためフォルダを作成する.
            DTVTLogger.debug("channel Folder not exist, make folder.");
            boolean isChannelDir = channelDir.mkdir();
            if (!isChannelDir) {
                //フォルダの作成に失敗
                DTVTLogger.error("date Failed to create channel cache directory");
            }
        }
        File fileDir = new File(dbDir);
        if (!fileDir.isDirectory()) {
            //取得日のフォルダが存在しないためフォルダを作成する.
            DTVTLogger.debug("Folder not exist, make folder.");
            try {
                boolean isCreateDir = fileDir.mkdir();
                if (!isCreateDir) {
                    //フォルダの作成に失敗
                    DTVTLogger.error("Failed to create date cache directory");
                }
            } catch (SecurityException | NullPointerException e) {
                DTVTLogger.error(e.toString());
            }
        }

        List<ChannelInfo> channelInformation = channelInfoList.getChannels();
        synchronized (channelInformation) {
            for (ChannelInfo channelInfo : channelInformation) {
                ArrayList<ScheduleInfo> scheduleInformation = channelInfo.getSchedules();
                if (scheduleInformation == null) {
                    continue;
                }
                if (mContext != null && scheduleInformation.size() == 1) {
                    String title = scheduleInformation.get(0).getTitle();
                    if (mContext.getString( R.string.common_failed_data_message).equals(title)
                            || mContext.getString(R.string.common_empty_data_message).equals(title)) {
                        continue;
                    }
                }
                //DB名としてチャンネル番号を取得.
                String serviceIdUniq = channelInfo.getServiceIdUniq();
                DataBaseHelperChannel tvScheduleListDBHelper = new DataBaseHelperChannel(mContext, serviceIdUniq);
                DataBaseManager.clearChInfo();
                DataBaseManager.initializeInstance(tvScheduleListDBHelper);
                DataBaseManager databaseManager = DataBaseManager.getChInstance();
                synchronized (databaseManager) {
                    //各種オブジェクト作成
                    SQLiteDatabase database = databaseManager.openChDatabase();
                    database.acquireReference();
                    TvScheduleListDao tvScheduleListDao = new TvScheduleListDao(database);

                    //ContentValuesに変換してDBに保存する.
                    try {
                        DTVTLogger.debug("bulk insert start");
                        database.beginTransaction();
                        //イテレーターのループはここでは問題が発生する場合があった。FindBugs等の指摘があっても変更無用
                        for (int counter = 0; counter < scheduleInformation.size(); counter++) {
                            ScheduleInfo scheduleInfo = scheduleInformation.get(counter);
                            ContentValues values = convertScheduleInfoToContentValues(scheduleInfo);
                            tvScheduleListDao.insert(values);
                        }
                        database.setTransactionSuccessful();
                    } catch (SQLiteException e) {
                        DTVTLogger.debug("insertTvScheduleInsertList, e.cause=" + e.getCause());
                    } finally {
                        database.endTransaction();
                        DataBaseManager.getInstance().closeChDatabase();
                        DTVTLogger.debug("bulk insert end");
                    }
                }
                //保存したDBを所定の場所へ移動する( HOME/database/channel/yyyyMMdd/ )
                String channelFilePath = StringUtils.getConnectStrings(filesDir, "/../databases/", serviceIdUniq);
                File channelFile = new File(channelFilePath);
                File movedFile = new File(StringUtils.getConnectStrings(dbDir, "/", serviceIdUniq));
                if (channelFile.isFile()) {
                    try {
                        if (!channelFile.renameTo(movedFile)) {
                            DTVTLogger.error("Failed to move DB file");
                            //移動に失敗したため元ファイルを削除する
                            if (!channelFile.delete()) {
                                DTVTLogger.error("Failed to remove DB file");
                            }
                        }
                    } catch (SecurityException | NullPointerException e) {
                        DTVTLogger.error(e.toString());
                    }
                }

                //journalファイルも同じ場所へ移動させる.
                String journalFilePath = StringUtils.getConnectStrings(filesDir, "/../databases/", serviceIdUniq, "-journal");
                File journalFile = new File(journalFilePath);
                File movedJournalFile = new File(StringUtils.getConnectStrings(dbDir, "/", serviceIdUniq, "-journal"));
                if (journalFile.isFile()) {
                    try {
                        if (!journalFile.renameTo(movedJournalFile)) {
                            DTVTLogger.error("Failed to move journal file");
                            //移動に失敗したため元ファイルを削除する
                            if (!journalFile.delete()) {
                                DTVTLogger.error("Failed to remove journal file");
                            }
                        }
                    } catch (SecurityException | NullPointerException e) {
                        DTVTLogger.error(e.toString());
                    }
                }
            }
        }

        //古いDBファイルを削除するため、日付フォルダ一覧を取得
        File[] files = new File(dbChannelDir).listFiles();
        //前後一週間を超えるDBフォルダは無条件で削除する.
        if (files != null) {
            for (File folder : files) {
                if (folder.isDirectory()) {
                    Date folderDate;
                    try {
                        String folderName = StringUtils.getConnectStrings(folder.getName(), "000000");
                        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_YYYY_MM_DD_HH_MM_SS, Locale.JAPAN);
                        folderDate = sdf.parse(folderName);
                    } catch (ParseException e) {
                        DTVTLogger.error(e.toString());
                        continue;
                    }
                    Calendar folderCalendar = Calendar.getInstance();
                    folderCalendar.setTime(folderDate);

                    Date nowDate = new Date();
                    Calendar afterEightDay = Calendar.getInstance();
                    Calendar beforeEightDay = Calendar.getInstance();
                    afterEightDay.setTime(nowDate);
                    beforeEightDay.setTime(nowDate);
                    afterEightDay.add(Calendar.DAY_OF_MONTH, 8);
                    beforeEightDay.add(Calendar.DAY_OF_MONTH, -8);

                    if (folderCalendar.compareTo(afterEightDay) > 0
                            || folderCalendar.compareTo(beforeEightDay) < 0) {
                        //キャッシュ期限範囲外の日付のフォルダなので削除する.
                        recursiveDeleteFile(folder);
                    }
                }
            }
        }
        DTVTLogger.end();
    }

    /**
     * フォルダを中身ごと削除する.
     *
     * @param file 削除対象フォルダ
     */
    private static void recursiveDeleteFile(final File file) {
        // 存在しない場合は処理終了
        if (!file.exists()) {
            return;
        }
        // 対象がディレクトリの場合は再帰処理
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    recursiveDeleteFile(child);
                }
            }
        }
        // 対象がファイルもしくは配下が空のディレクトリの場合は削除する
        if (!file.delete()) {
            DTVTLogger.error("Failed to delete folder or file");
        }
    }

    /**
     * ContentValuesに変換する.
     *
     * @param scheduleInfo ScheduleInfo
     * @return ContentValues
     */
    private ContentValues convertScheduleInfoToContentValues(final ScheduleInfo scheduleInfo) {
        ContentValues values = new ContentValues();
        values.put( JsonConstants.META_RESPONSE_THUMB_448, scheduleInfo.getImageUrl());
        values.put(JsonConstants.META_RESPONSE_THUMB_640, scheduleInfo.getImageDetailUrl());
        values.put(JsonConstants.META_RESPONSE_TITLE, scheduleInfo.getTitle());
        values.put(JsonConstants.META_RESPONSE_PUBLISH_START_DATE, scheduleInfo.getStartTime());
        values.put(JsonConstants.META_RESPONSE_PUBLISH_END_DATE, scheduleInfo.getEndTime());
        values.put(JsonConstants.META_RESPONSE_CHNO, scheduleInfo.getChNo());
        values.put(JsonConstants.META_RESPONSE_DISP_TYPE, scheduleInfo.getDispType());
        values.put(JsonConstants.META_RESPONSE_SEARCH_OK, scheduleInfo.getSearchOk());
        values.put(JsonConstants.META_RESPONSE_CRID, scheduleInfo.getCrId());
        values.put(JsonConstants.META_RESPONSE_SERVICE_ID, scheduleInfo.getServiceId());
        values.put(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ, scheduleInfo.getServiceIdUniq());
        values.put(JsonConstants.META_RESPONSE_EVENT_ID, scheduleInfo.getEventId());
        values.put(JsonConstants.META_RESPONSE_TITLE_ID, scheduleInfo.getTitleId());
        values.put(JsonConstants.META_RESPONSE_R_VALUE, scheduleInfo.getRValue());
        values.put(JsonConstants.META_RESPONSE_CONTENT_TYPE, scheduleInfo.getContentType());
        values.put(JsonConstants.META_RESPONSE_DTV, scheduleInfo.getDtv());
        values.put(JsonConstants.META_RESPONSE_TV_SERVICE, scheduleInfo.getTvService());
        values.put(JsonConstants.META_RESPONSE_VOD_START_DATE, scheduleInfo.getVodStartDate());
        values.put(JsonConstants.META_RESPONSE_DTV_TYPE, scheduleInfo.getDtvType());
        values.put(JsonConstants.META_RESPONSE_SYNOP, scheduleInfo.getDetail());
        values.put( DataBaseConstants.UNDER_BAR_FOUR_K_FLG, scheduleInfo.get4KFlg());
        values.put(JsonConstants.META_RESPONSE_OTT_FLG, scheduleInfo.getOttFlg());
        values.put(JsonConstants.META_RESPONSE_OTT_MASK, scheduleInfo.getOttMask());
        return values;
    }
}