package com.example.detaildemo.service;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.detaildemo.R;
import com.example.detaildemo.data.provider.DownloadDataProvider;
import com.example.detaildemo.service.download.DownloadData;
import com.example.detaildemo.service.download.DownloadListener;
import com.example.detaildemo.service.download.DownloadParam;
import com.example.detaildemo.service.download.DownloadServiceListener;
import com.example.detaildemo.service.download.DownloaderBase;
import com.example.detaildemo.utils.DlnaUtils;
import com.example.detaildemo.utils.GoogleAnalyticsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ダウンロードサービスクラス.
 */
public class DownloadService extends Service implements DownloadListener{
    /**ダウンロードサービスクラスリスナー.*/
    private DownloadServiceListener mDownloadServiceListener;
    /**DownloaderBase.*/
    private DownloaderBase mDownloaderBase;
    /**ダウンロードサービスID.*/
    private static final int DOWNLOAD_SERVICE_ID = 1;
    /**ダウンロード失敗通知ID.*/
    private static int sDownloadFailId = DOWNLOAD_SERVICE_ID + 1;
    /**タイトル.*/
    private String mTitle;
    /**ダウンロードデータキュー.*/
    private static List<DownloadData> sDownloadDataQue = new ArrayList<>();
    /**UI更新あるか.*/
    private boolean mIsUiRunning = false;

    //broadcast type
    /**プログレース.*/
    public static final String DOWNLOAD_ON_PROGRESS = "onProgress";
    /**ダウンロード成功.*/
    public static final String DOWNLOAD_ON_SUCCESS = "onSuccess";
    /**ダウンロード失敗.*/
    public static final String DOWNLOAD_ON_FAIL = "onFail";
    /**メモリ不足.*/
    public static final String DOWNLOAD_ON_LOW_STORAGE_SPACE = "onLowStorageSpace";
    /**ダウンロードタスク全キャンセル.*/
    public static final String DOWNLOAD_ON_CANCEL_ALL = "OnCancelAll";
    /**ダウンロードタスクキャンセル.*/
    public static final String DOWNLOAD_ON_CANCEL = "onCancel";
    /**ダウンロード開始.*/
    public static final String DOWNLOAD_ON_START = "onStart";
    /**ダウンロードデータプロバイダー使用可.*/
    public static final String DOWNLOAD_DL_DATA_PROVIDER_AVAILABLE = "onDlDataProviderAvailable";
    /**ダウンロードデータプロバイダー使用不可.*/
    public static final String DOWNLOAD_DL_DATA_PROVIDER_UNAVAILABLE = "onDDataProviderUnavailable";

    //broadcast param type
    /**broadcast param type　int.*/
    public static final String DOWNLOAD_PARAM_INT = "paramInt";
    /**broadcast param type  string.*/
    public static final String DOWNLOAD_PARAM_STRING = "paramString";

    /**
     * ダウンロードデータキュー設定.
     * @param downloadDataQue ダウンロードデータキュー
     */
    public synchronized static void setDlDataQue(final List<DownloadData> downloadDataQue) {
        sDownloadDataQue.addAll(downloadDataQue);
    }
    /**ダウンロードデータキュークリア.*/
    public synchronized static void setDlDataQueClear() {
        if (null != sDownloadDataQue) {
            sDownloadDataQue.clear();
        }
    }
    /**ダウンロードデータキューから先頭アイテム取り除く.*/
    public synchronized static void setDlDataQueRemove0() {
        if (null != sDownloadDataQue) {
            sDownloadDataQue.remove(0);
        }
    }

    /**
     * ダウンロードデータキュー.
     * @return ダウンロードデータキュー
     */
    public synchronized static  List<DownloadData> getDlDataQue() {
        return sDownloadDataQue;
    }

    /**
     * ダウンロードサービス稼働中か.
     * @return true or false
     */
    public static synchronized boolean isDownloadServiceRunning() {
        return (null != sDownloadDataQue) && 0 < sDownloadDataQue.size();
    }

    /**
     * ダウンロードリスナー設定.
     * @param dlServiceListener ダウンロードリスナー
     */
    public void setDownloadServiceListener(final DownloadServiceListener dlServiceListener) {
        mDownloadServiceListener = dlServiceListener;
    }

    /**
     * Create.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * ダウンロード開始.
     */
    public void start() {
        if (null != mDownloaderBase) {
            mDownloaderBase.start();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(DOWNLOAD_SERVICE_ID, DlnaUtils.getNotification(getTitle(),
                    getResources().getString( R.string.record_download_notification), this));
        }
    }

    /**
     * ダウンロードエラー発生の時、コールされる.
     * @return ダウンロードエラータイプ
     */
    public com.example.detaildemo.service.download.DownloadListener.DownLoadError isError() {
        if (null != mDownloaderBase) {
            return mDownloaderBase.isError();
        }
        return com.example.detaildemo.service.download.DownloadListener.DownLoadError.DLError_NoError;
    }

    /**
     * ダウンロードキャンセル.
     */
    public void cancel() {
        if (null != mDownloaderBase) {
            mDownloaderBase.cancel();
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new Binder();
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return true;
    }

    @Override
    public void onRebind(final Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        startService();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * ダウンロード開始.
     */
    @SuppressLint("NewApi")
    private void startService() {
        startForeground(DOWNLOAD_SERVICE_ID, DlnaUtils.getNotification(getTitle(),
                getResources().getString(R.string.record_download_notification), this));
    }

    /**
     * ダウンロード停止.
     */
    public void stopService() {
        stopForeground(true);
        stopSelf();
        boolean isUiRunning = isUiRunning();
        if (!isUiRunning) {
            DownloadDataProvider.releaseInstance();
        }
    }

    /**
     * UI更新あるか.
     * @return true or false
     */
    public boolean isUiRunning() {
        return mIsUiRunning;
    }

    /**
     * DownloadParam設定.
     * @param param param
     */
    public void setDlParam(final DownloadParam param) {
        mTitle = param.getTitle();
        ++sDownloadFailId;
        if (null == mDownloaderBase) {
//            mDownloaderBase = new DtcpDownloader(param, this);
        } else {
            mDownloaderBase.reset(param);
        }
    }

    /**
     * ダウンロード中コンテンツタイトル取得.
     * @return title タイトル
     */
    private String getTitle() {
        return mTitle;
    }

    /**
     * onDestroy処理.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(final long totalFileByteSize) {
        if (null != mDownloadServiceListener) {
            mDownloadServiceListener.onStart(totalFileByteSize);
        }
    }

    @Override
    public void onProgress(final int percent) {
        if (null != mDownloadServiceListener) {
            mDownloadServiceListener.onProgress(percent);
        }
    }

    @Override
    public void onFail(final com.example.detaildemo.service.download.DownloadListener.DownLoadError error, final String savePath, final String errorCode) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(sDownloadFailId,
                DlnaUtils.getNotification(getTitle(),
                        getResources().getString(R.string.record_download_error_message, errorCode),
                        this));
        String fixErrorCode = getString(R.string.error_prefix_type_download_error, errorCode);
        final StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
       // GoogleAnalyticsUtils.sendErrorReport( GoogleAnalyticsUtils.getClassNameAndMethodName(stackTraceElement), fixErrorCode);
        if (null != mDownloadServiceListener) {
            mDownloadServiceListener.onFail(error, savePath, errorCode);
        }
    }

    @Override
    public void onSuccess(final String fullPath) {
        if (null != mDownloadServiceListener) {
            mDownloadServiceListener.onSuccess(fullPath);
        }
    }

    @Override
    public void onCancel(final String filePath) {
        if (null != mDownloadServiceListener) {
            mDownloadServiceListener.onCancel(filePath);
        }
    }

    @Override
    public void onLowStorageSpace(final String fullPath) {
        if (null != mDownloadServiceListener) {
            mDownloadServiceListener.onLowStorageSpace(fullPath);
        }
    }

    /**
     * Binderクラス.
     */
    public class Binder extends android.os.Binder {

        /**
         * DownloadService取得.
         * @return  DownloadService
         */
        public DownloadService getDownloadService() {
            return DownloadService.this;
        }
    }

    /**
     * 機能：.
     *      １．Download Uiがなくなる場合、且サービスにqueueはない場合、必ずこれをコールする
     *      ２．Download Uiがない場合、Serviceは閉じる時、必ずこれをコールする
     */
    public void stop() {
        if (null == mDownloaderBase) {
            return;
        }
        mDownloaderBase.stop();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(DownloadService.DOWNLOAD_SERVICE_ID);
    }

    /**
     * Ui runningを設定.
     * @param yn true or false
     */
    public void setUiRunning(final boolean yn) {
        mIsUiRunning = yn;
    }

    /**
     * ダウンロード中か.
     * @return true or false
     */
    public synchronized boolean isDownloading() {
        return null != mDownloaderBase && mDownloaderBase.isDownloading();
    }
}
