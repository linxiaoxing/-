package com.example.detaildemo.data.provider;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.detaildemo.data.manager.DownLoadListDataManager;
import com.example.detaildemo.service.download.DownloadListener;
import com.example.detaildemo.service.download.DownloadListener.DownLoadError;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.data.manager.DataBaseThread;
import com.example.detaildemo.service.DownloadService;
import com.example.detaildemo.service.download.DownloadData;
import com.example.detaildemo.service.download.DownloadParam;
import com.example.detaildemo.service.download.DownloadServiceListener;
import com.example.detaildemo.service.download.DownloaderBase;
import com.example.detaildemo.service.download.DtcpDownloadParam;
import com.example.detaildemo.utils.SharedPreferencesUtils;
import com.example.detaildemo.utils.StringUtils;
import com.example.detaildemo.utils.UserInfoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ダウンロードデータプロバイダー.
 * Activityからこのクラスを利用する
 */
public class DownloadDataProvider implements ServiceConnection, DownloadServiceListener, DataBaseThread.DataBaseOperation {

    /**Binder.*/
    private DownloadService.Binder mBinder;
    /**Activity.*/
    private Activity mActivity;
    /**ダウンロードデータ.*/
    private DownloadData mDownloadData;
    /**アイテムID.*/
    private String mItemId;
    /**ダウンロード進捗.*/
    private int mPercent;
    /**DlDataProvider機能有効か.*/
    private boolean mIsRegistered;
    /**ダウンロードデータプロバイダー.*/
    private static DownloadDataProvider sDownloadDataProvider = new DownloadDataProvider();
    /**操作ID.*/
    private static final int DOWNLOAD_STATUS_SELECT = 1;
    /**持ち出しのダウンロード情報格納.*/
    private static final int DOWNLOAD_INSERT = 2;
    /**持ち出しのダウンロード情報更新.*/
    private static final int DOWNLOAD_UPDATE = 3;
    /**DOWNLOAD_TOTALSIZE_SELECT.*/
    private static final int DOWNLOAD_TOTALSIZE_SELECT = 4;
    /**持ち出しのダウンロード情報全削除.*/
    private static final int DOWNLOAD_DELETE_ALL = 5;

    /**コンストラクタ.*/
    private DownloadDataProvider() {

    }
    /**
     * コンストラクタ.
     *
     * @param activity アクティビティ
     * @return dlDataProvider
     */
    public static DownloadDataProvider getInstance(final Activity activity) {
        if (null == activity) {
            DTVTLogger.error("DownloadDataProvider.DownloadDataProvider, null activity");
            return null;
        }
        if (null == sDownloadDataProvider) {
            sDownloadDataProvider = new DownloadDataProvider();
        }
        sDownloadDataProvider.mActivity = activity;
        return sDownloadDataProvider;
    }

    /**
     * releaseInstance.
     */
    public static void releaseInstance() {
        if (null == sDownloadDataProvider) {
            return;
        }
        sDownloadDataProvider = null;
    }

    /**
     * DlDataProvider機能有効かを取得.
     * @return true or false
     */
    public boolean getIsRegistered() {
        return mIsRegistered;
    }

    /**
     * ダウンロードデータプロバイダー.
     * @param activity activity
     */
    public DownloadDataProvider(final Activity activity) {
        this.mActivity = activity;
    }

    /**
     * DlDataProvider機能を有効.
     * @param act Activity
     * @param doStartService サービス起動要否
     */
    public void beginProvider(final Activity act, final boolean doStartService) {
        if (null == act) {
            start();
            return;
        }
        mActivity = act;
        Intent intent = new Intent(mActivity, DownloadService.class);
        mIsRegistered = mActivity.bindService(intent, this, Context.BIND_AUTO_CREATE);
        if (doStartService) {
            startService();
        }
    }

    /**
     * サービス起動する.
     */
    private void startService() {
        if (DownloadService.isDownloadServiceRunning()) {
            return;
        }
        if (null == mActivity) {
            return;
        }
        Intent intent = new Intent(mActivity, DownloadService.class);
        mActivity.startService(intent);
    }

    /**
     * DlDataProvider機能を無効.
     */
    public void endProvider() {
        if (null == mActivity) {
            return;
        }
        try {
            mActivity.unbindService(this);
        } catch (Exception e) {
            DTVTLogger.debug(e);
        }
    }

    /**
     * ダウンロードパラメータ設定.
     *
     * @param param パラメータ
     */
    public void setDownloadParam(final DownloadParam param) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            ds.setDlParam(param);
        }
    }

    /**
     * DownloadService取得.
     * @return DownloadService
     */
    public DownloadService getDownloadService() {
        if (null == mBinder) {
            return null;
        }
        return mBinder.getDownloadService();
    }

    /**
     * ダウンロード進捗取得.
     * @return ダウンロード進捗
     */
    public int getPercent() {
        return mPercent;
    }

    /**
     * ダウンロード開始.
     */
    public void start() {
        mPercent = 0;
        DownloadService ds = getDownloadService();
        if (null != ds) {
            ds.start();
        }
    }

    /**
     * ダウンロード停止.
     */
    public void stopService() {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            ds.stop();
            ds.stopService();
        }
    }

    /**
     * ダウンロードエラー発生の時、コールされる.
     * @return エラータイプ
     */
    public DownLoadError isError() {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            return ds.isError();
        }
        return DownLoadError.DLError_NoError;
    }

    /**
     * ダウンロードキャンセル.
     */
    public void cancel() {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            ds.cancel();
        }
    }

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        mBinder = (DownloadService.Binder) iBinder;
        DownloadService ds = getDownloadService();
        if (null != ds) {
            ds.setDownloadServiceListener(this);
        }
        sendBroadcast(DownloadService.DOWNLOAD_DL_DATA_PROVIDER_AVAILABLE);
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
        mBinder = null;
        sendBroadcast(DownloadService.DOWNLOAD_DL_DATA_PROVIDER_UNAVAILABLE);
    }

    @Override
    public void onStart(final long totalFileByteSize) {
        sendBroadcast(DownloadService.DOWNLOAD_ON_START, DownloadService.DOWNLOAD_PARAM_INT, totalFileByteSize);
    }

    @Override
    public void onProgress(final int percent) {
        mPercent = percent;
        sendBroadcast(DownloadService.DOWNLOAD_ON_PROGRESS, DownloadService.DOWNLOAD_PARAM_INT, percent);
    }

    @Override
    public void onFail(final DownLoadError error, final String savePath, final String errorCode) {
        int paramInt = error.ordinal();
        sendBroadcast(DownloadService.DOWNLOAD_ON_FAIL, DownloadService.DOWNLOAD_PARAM_STRING, savePath, DownloadService.DOWNLOAD_PARAM_INT, paramInt);
        DownloadService ds = getDownloadService();
        if (null == ds) {
            return;
        }
        if (!ds.isUiRunning()) {
            cancelDownLoadStatus(savePath, false);
            setNextDownLoad();
        }
    }

    /**
     * ダウンロード成功,キャンセル,メモリ不足ブロードキャストsend.
     * @param broad broad
     * @param paramName paramName
     * @param param param
     */
    private void sendBroadcast(final String broad, final String paramName, final String param) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            Intent intent = new Intent();
            intent.setAction(broad);
            intent.putExtra(paramName, param);
            LocalBroadcastManager.getInstance(ds).sendBroadcast(intent);
        }
    }

    /**
     * ダウンロード失敗時のブロードキャストsend.
     * @param broad broad
     * @param paramName paramName
     * @param param param
     * @param paramName2 paramName2
     * @param intParam intParam
     */
    private void sendBroadcast(final String broad, final String paramName, final String param, final String paramName2, final int intParam) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            Intent intent = new Intent();
            intent.setAction(broad);
            intent.putExtra(paramName, param);
            intent.putExtra(paramName2, intParam);
            LocalBroadcastManager.getInstance(ds).sendBroadcast(intent);
        }
    }


    /**
     * ブロードキャストsend.
     * @param broad broad
     */
    private void sendBroadcast(final String broad) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            Intent intent = new Intent();
            intent.setAction(broad);
            LocalBroadcastManager.getInstance(ds).sendBroadcast(intent);
        }
    }

    /**
     *ダウンロード開始、ダウンロード中ブロードキャストsend.
     * @param broad broad
     * @param paramName paramName
     * @param param param
     */
    private void sendBroadcast(final String broad, final String paramName, final int param) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            Intent intent = new Intent();
            intent.setAction(broad);
            intent.putExtra(paramName, param);
            LocalBroadcastManager.getInstance(ds).sendBroadcast(intent);
        }
    }

    /**
     *ダウンロード開始、ダウンロード中ブロードキャストsend.
     * @param broad broad
     * @param paramName paramName
     * @param param param
     */
    private void sendBroadcast(final String broad, final String paramName, final long param) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            Intent intent = new Intent();
            intent.setAction(broad);
            intent.putExtra(paramName, param);
            LocalBroadcastManager.getInstance(ds).sendBroadcast(intent);
        }
    }

    /**separator.*/
    private static final String sSeparator = File.separator + "";

    @Override
    public void onSuccess(final String fullPath) {
        if (!TextUtils.isEmpty(fullPath)) {
            if (fullPath.contains(sSeparator)) {
                String[] paths = fullPath.split(sSeparator);
                String[] ids = fullPath.split(sSeparator);
                mItemId = ids[paths.length - 1];
                if (!TextUtils.isEmpty(mItemId)) {
                    updateDownloadStatusToDb();
                }
            }
        }
        sendBroadcast(DownloadService.DOWNLOAD_ON_SUCCESS, DownloadService.DOWNLOAD_PARAM_STRING, fullPath);
        DownloadService ds = getDownloadService();
        if (null == ds) {
            return;
        }
        if (!ds.isUiRunning()) {
            setNextDownLoad();
        }
    }

    /**
     * 次のコンテンツをダウンロード.
     */
    private void setNextDownLoad() {
        if (DownloadService.getDlDataQue() != null && DownloadService.getDlDataQue().size() > 0) {
            DownloadService.setDlDataQueRemove0();
            DTVTLogger.debug(">>>>>>>>>>>>>>>>>> dl ok 3");
            if (0 == DownloadService.getDlDataQue().size()) {
                mIsRegistered = false;
                stopService();
                return;
            }
            try {
                Thread.sleep(1000);
                DTVTLogger.debug(">>>>>>>>>>>>>>>>>> new dl");
                setDownloadParam(getDownLoadParam());
                start();
            } catch (Exception e) {
                DTVTLogger.debug(e);
            }
        } else {
            stopService();
        }
    }

    /**
     * ダウンロードに必要なパラメータ取得.
     * @return パラメータ
     */
    private DownloadParam getDownLoadParam() {
        DownloadParam downloadParam = null;
        if (DownloadService.getDlDataQue() != null && DownloadService.getDlDataQue().size() > 0) {
            DownloadData item = DownloadService.getDlDataQue().get(0);
            Context context = null;
            if (getDownloadService() != null) {
                context = getDownloadService().getApplicationContext();
            }
            downloadParam = new DtcpDownloadParam();
            DtcpDownloadParam dtcpDownloadParam = (DtcpDownloadParam) downloadParam;
            dtcpDownloadParam.setContext(context);
            dtcpDownloadParam.setSavePath(item.getSaveFile());
            dtcpDownloadParam.setSaveFileName(item.getItemId());
            dtcpDownloadParam.setTitle(item.getTitle());
            dtcpDownloadParam.setDtcp1host(item.getHost());
            dtcpDownloadParam.setDtcp1port(Integer.parseInt(item.getPort()));
            dtcpDownloadParam.setUrl(item.getUrl());
            dtcpDownloadParam.setCleartextSize(Integer.parseInt(item.getTotalSize()));
            dtcpDownloadParam.setItemId(item.getItemId());
            dtcpDownloadParam.setPercentToNotify(Integer.parseInt(item.getPercentToNotify()));
            dtcpDownloadParam.setXmlToDownLoad(item.getXmlToDownLoad());
        }
        return downloadParam;
    }

    @Override
    public void onCancel(final String filePath) {
        sendBroadcast(DownloadService.DOWNLOAD_ON_CANCEL, DownloadService.DOWNLOAD_PARAM_STRING, filePath);
        DownloadService ds = getDownloadService();
        if (null == ds) {
            return;
        }
        if (!ds.isUiRunning()) {
            cancelDownLoadStatus(filePath, false);
            setNextDownLoad();
        }
    }

    @Override
    public void onLowStorageSpace(final String fullPath) {
        sendBroadcast(DownloadService.DOWNLOAD_ON_LOW_STORAGE_SPACE, DownloadService.DOWNLOAD_PARAM_STRING, fullPath);
        DownloadService ds = getDownloadService();
        if (null == ds) {
            return;
        }
        if (!ds.isUiRunning()) {
            cancelDownLoadStatus(fullPath, false);
            setNextDownLoad();
        }
    }

    /**
     * ダウンロードステータス取得.
     */
    public void getDownLoadStatus() {
        dbOperationByThread(DOWNLOAD_STATUS_SELECT);
    }

    @Override
    public void onDbOperationFinished(final boolean isSuccessful, final List<Map<String, String>> resultSet, final int operationId) {
        if (isSuccessful) {
            switch (operationId) {
                case DOWNLOAD_STATUS_SELECT:
                    List<DownloadData> statusList = new ArrayList<>();
                    for (int i = 0; i < resultSet.size(); i++) {
                        Map<String, String> hashMap = resultSet.get(i);
                        String downloadStatus = hashMap.get( DataBaseConstants.DOWNLOAD_LIST_COLUM_DOWNLOAD_STATUS);
                        String itemId = hashMap.get(DataBaseConstants.DOWNLOAD_LIST_COLUM_ITEM_ID);
                        String saveURL = hashMap.get(DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_URL);
                        String fileName = hashMap.get(DataBaseConstants.DOWNLOAD_LIST_COLUM_TITLE);
                        DownloadData mDownloadData = new DownloadData();
                        mDownloadData.setItemId(itemId);
                        mDownloadData.setSaveFile(saveURL);
                        mDownloadData.setTitle(fileName);
                        mDownloadData.setDownLoadStatus(downloadStatus);
                        statusList.add(mDownloadData);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<Map<String, String>> dbOperation(final DataBaseThread dataBaseThread, final int operationId) {
        List<Map<String, String>> resultSet = null;
        DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(mActivity);
        switch (operationId) {
            case DOWNLOAD_STATUS_SELECT:
                resultSet = downLoadListDataManager.selectDownLoadListVideoData();
                break;
            case DOWNLOAD_INSERT:
                if (mDownloadData != null) {
                    downLoadListDataManager.insertDownload(mDownloadData);
                }
                break;
            case DOWNLOAD_UPDATE:
                if (mDownloadData != null) {
                    downLoadListDataManager.updateDownloadByItemId(mItemId);
                }
                break;
            case DOWNLOAD_DELETE_ALL:
                downLoadListDataManager.deleteDownloadAllContents();
                break;
            default:
                break;
        }
        return resultSet;
    }

    /**
     * ダウンロードキャンセル.
     * @param path パスー
     */
    public void cancelDownLoadStatus(final String path, final boolean completed) {
        if (null == path || path.isEmpty()) {
            return;
        }
        if (!TextUtils.isEmpty(path) && path.contains(sSeparator)) {
            String[] paths = path.split(sSeparator);
            String[] ids = path.split(sSeparator);
            String itemId = ids[paths.length - 1];
            if (!TextUtils.isEmpty(itemId)) {
                List<String> pathList = getPathListById(itemId, path, completed);
                if (pathList != null && pathList.size() > 0) {
                    for (String savePath : pathList) {
                        DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(mActivity);
                        downLoadListDataManager.deleteDownloadContentByItemId(itemId, savePath, completed);
                        //ディスクからコンテンツを削除する
                        File file = new File( StringUtils.getConnectStrings(savePath, sSeparator, itemId));
                        if (file.exists()) {
                            File[] files = file.listFiles();
                            if (null != files) {
                                for (File file1 : files) {
                                    if (null != file1) {
                                        if (!file1.delete()) {
                                            DTVTLogger.debug("delete cacel file fail path:" + path);
                                        }
                                    }
                                }
                            }
                            if (file.exists()) {
                                if (!file.delete()) {
                                    DTVTLogger.debug("delete cacel directory fail path:" + path);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 該当コンテンツIDのパスリストを取得.
     * @param itemId コンテンツID
     * @param path パス
     * @param completed ダウンロード完了フラグ
     * @return pathList パスリスト
     */
    private List<String> getPathListById(final String itemId, final String path, final boolean completed) {
        List<String> pathList = new ArrayList<>();
        DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(mActivity);
        if (completed) {
            List<Map<String, String>> resultList = downLoadListDataManager.selectDownLoadListById(itemId);
            if (resultList != null && resultList.size() > 0) {
                for (Map<String, String> hashMap : resultList) {
                    pathList.add(hashMap.get(DataBaseConstants.DOWNLOAD_LIST_COLUM_SAVE_URL));
                }
            }
        } else {
            pathList.add(path.substring(0, path.lastIndexOf(sSeparator)));
        }
        return pathList;
    }

    /**
     * ダウンロードしたデータ状態取得.
     * @return ダウンロードしたデータ状態.
     */
    public List<Map<String, String>> getDownloadListData() {
        DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(mActivity);
        return downLoadListDataManager.selectDownLoadList();
    }
    /**
     * ダウンロードしたコンテンツを全削除.
     *
     * @param context コンテキスト
     */
    public static void deleteAllDownLoadContents(final Context context) {
        DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(context);
        downLoadListDataManager.deleteDownloadAllContents();
        List<String> pathList = DownloaderBase.getDownloadPathList(context);
        for (String path : pathList) {
            deleteAllFiles(new File(path));
        }
    }

    /**
     * ダウンロード未完了コンテンツを全削除.
     */
    public int deleteDownloadContentNotCompleted() {
        DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(mActivity);
        return downLoadListDataManager.deleteDownloadContentNotCompleted();
    }

    /**
     * delete download file.
     * @param root ルート
     */
    private static void deleteAllFiles(final File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                    try {
                        if (!f.delete()) {
                            DTVTLogger.debug("delete download file fail ");
                        }
                    } catch (Exception e) {
                        DTVTLogger.debug(e);
                    }
                } else {
                    if (f.exists()) {
                        deleteAllFiles(f);
                        try {
                            if (!f.delete()) {
                                DTVTLogger.debug("delete download file fail ");
                            }
                        } catch (Exception e) {
                            DTVTLogger.debug(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * DLデータ設定.
     *
     * @param downloadData DLデータ
     */
    public void setDownloadData(final DownloadData downloadData) {
        this.mDownloadData = downloadData;
        String itemId = downloadData.getItemId();
        if (!TextUtils.isEmpty(itemId) && !itemId.startsWith(DownloaderBase.sDlPrefix)) {
            itemId = DownloaderBase.getFileNameById(itemId);
        }
        cancelDownLoadStatus(downloadData.getSaveFile() + File.separator + itemId, false);
        dbOperationByThread(DOWNLOAD_INSERT);
    }

    /**
     * DBにダウンロードステータス更新.
     */
    private void updateDownloadStatusToDb() {
        DTVTLogger.start();
        if (mActivity != null) {
            DTVTLogger.debug("writing db");
            DownLoadListDataManager downLoadListDataManager = new DownLoadListDataManager(mActivity);
            downLoadListDataManager.updateDownloadByItemId(mItemId);
        }
        DTVTLogger.end();
    }

    /**
     * キュー設定.
     * @param downloadData ダウンロードするデータ.
     */
    public void setQue(final List<DownloadData> downloadData) {
        if (DownloadService.getDlDataQue() != null && DownloadService.getDlDataQue().size() > 0) {
            DownloadService.setDlDataQueClear();
        }
        DownloadService.setDlDataQue(downloadData);
    }

    /**
     * ThreadによるDB操作.
     *
     * @param operationId 操作ID
     */
    private void dbOperationByThread(final int operationId) {
        Handler handler = new Handler();
        DataBaseThread t = new DataBaseThread(handler, this, operationId);
        t.start();
    }

    /**
     * Ui runningを設定.
     * @param yn true or false
     */
    public void setUiRunning(final boolean yn) {
        DownloadService ds = getDownloadService();
        if (null != ds) {
            ds.setUiRunning(yn);
        }
    }

    /**
     * DlDataからfullpathを戻す.
     * @param data data
     * @return fullpath fullpath
     */
    private static String getCurrentDlFullPath(final DownloadData data) {
        if (null == data) {
            return null;
        }
        StringBuilder path = new StringBuilder();
        path.append(data.getSaveFile());
        path.append(File.separator);
        path.append(data.getItemId());
        return path.toString();
    }

    /**
     * 機能：すべてのDLをキャンセル.
     */
    public static synchronized void cancelAll() {
        if (null == sDownloadDataProvider) {
            return;
        }
        DownloadService ds = sDownloadDataProvider.getDownloadService();
        if (null != ds) {
            if (ds.isUiRunning()) {
                sDownloadDataProvider.sendBroadcast(DownloadService.DOWNLOAD_ON_CANCEL_ALL);
            } else {
                List<DownloadData> downloadDataQue = DownloadService.getDlDataQue();
                for (DownloadData d : downloadDataQue) {
                    String path = getCurrentDlFullPath(d);
                    if (null != path) {
                        sDownloadDataProvider.cancelDownLoadStatus(path, false);
                    }
                }
                for (int i = downloadDataQue.size() - 1; i > -1; --i) {
                    downloadDataQue.remove(i);
                }
                ds.cancel();
            }
        }
    }

    /**
     * ダウンロード中なのか.
     * @return true or false
     */
    public synchronized boolean isDownloading() {
        DownloadService ds = getDownloadService();
        return null != ds && ds.isDownloading();
    }

    /**
     * 機能：すべてのDLをキャンセルし、ダウンロードしたコンテンツを削除する.
     * @param context コンテキスト
     * @param checkContractInfo 契約情報確認要否
     */
    public synchronized static void clearAllDownloadContents(final Context context, final boolean checkContractInfo) {
        if (checkContractInfo) {
            String oldContractInfo = SharedPreferencesUtils.getSharedPreferencesContractInfo(context);
            String newContractInfo = UserInfoUtils.getUserContractInfo(SharedPreferencesUtils.getSharedPreferencesUserInfo(context));
            if (!newContractInfo.equals(oldContractInfo)) {
                SharedPreferencesUtils.setSharedPreferencesContractInfo(context, newContractInfo);
                if (!UserInfoUtils.CONTRACT_INFO_NONE.equals(newContractInfo)) {
                    return;
                }
            } else {
                return;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                cancelAll();
                deleteAllDownLoadContents(context);
            }
        }).start();
    }
}
