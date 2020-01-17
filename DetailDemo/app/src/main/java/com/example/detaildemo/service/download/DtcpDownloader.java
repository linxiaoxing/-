package com.example.detaildemo.service.download;


import android.content.Context;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.utils.DlnaUtils;
import com.example.detaildemo.utils.NewEnvironmentUtil;

import java.io.File;
//
///**
// * tcpダウンローダークラス.
// */
//public class DtcpDownloader extends DownloaderBase implements DlnaManager.DownloadStatusListener {
//
//    /**
//     * Dtcpダウンローダー.
//     *
//     * @param param ダウンロードパラメータ
//     * @param downloadListener リスナー
//     */
//    DtcpDownloader(final DownloadParam param, final DownloadListener downloadListener) {
//        super(param, downloadListener);
//    }
//
//    @Override
//    protected long calculateTotalBytes() {
//        long ret = 0;
//        DtcpDownloadParam param;
//        try {
//            param = (DtcpDownloadParam) getDownloadParam();
//            ret = param.getCleartextSize();
//        } catch (Exception e) {
//            DTVTLogger.debug(e);
//        }
//        return ret;
//    }
//
//    @Override
//    protected void download() {
//        DTVTLogger.debug("dtcp download begin, files");
//        printDlPathFiles();
//        //mFinishedBytes=0;
//        DtcpDownloadParam param = (DtcpDownloadParam) getDownloadParam();
//        if (!param.isParamValid()) {
//            onFail(DownloadListener.DownLoadError.DLError_ParamError, DlnaUtils.DL_ERROR_CODE_STARTED_PARAM_ERROR);
//            return;
//        }
//
//        Context context = param.getContext();
//        if (null == context) {
//            onFail(DownloadListener.DownLoadError.DLError_Other, DlnaUtils.DL_ERROR_CODE_STARTED_OTHER);
//            return;
//        }
//
//        DlnaManager.shared().mDownloadStatusListener = this;
//        if (DlnaManager.shared().initDownload()) {
//            String homeParent = getParentDir(param.getSavePath());
//            int ret = NewEnvironmentUtil.copyDeviceKeyFromOtherCMWork(context, homeParent, EnvironmentUtil.ACTIVATE_DATA_HOME.DMP);
//            if (1 != ret && 3 != ret) {
//                onFail(DownloadListener.DownLoadError.DLError_CopyKeyFileFailed, DlnaUtils.DL_ERROR_CODE_STARTED_COPYKEY_FILE_FAILED);
//                DlnaManager.shared().setCanceledStatus(false);
//                return;
//            }
//            if (!DlnaManager.shared().getCanceled()) {
//                DlnaManager.shared().downloadStart(param);
//            } else {
//                DlnaManager.shared().setCanceledStatus(false);
//                onDownloadStatusCallBack(DlnaManager.DownLoadStatus.DOWNLOADER_STATUS_CANCELLED, null);
//            }
//        } else {
//            onFail(DownloadListener.DownLoadError.DLError_Other, DlnaUtils.DL_ERROR_CODE_STARTED_OTHER);
//        }
//    }
//
//    /**
//     * getParentDir.
//     * @param dir dir
//     * @return Parent path
//     */
//    private String getParentDir(final String dir) {
//        File f = new File(dir);
//        if (!f.exists()) {
//            boolean r = f.mkdirs();
//            if (!r) {
//                return "";
//            }
//        }
//        return f.getParent();
//    }
//
//    @Override
//    protected boolean isStorageSpaceLow() {
//        DownloadParam param =  getDownloadParam();
//        if (null == param) {
//            return true;
//        }
//        DtcpDownloadParam dp = (DtcpDownloadParam) param;
//        String path = dp.getSavePath();
//        if (null == path || path.isEmpty()) {
//            return true;
//        }
//        File f = new File(path);
//        if (!f.exists()) {
//            if (!f.mkdirs()) {
//                return true;
//            }
//        }
//        long usableSpace = (f.getUsableSpace() / 1024) / 1024;  //-->MB
//        long safeSpace = getInnerStorageSafeSpaceMB();
//        long dlSize = (dp.getCleartextSize() / 1024) / 1024; //-->MB
//        return (usableSpace - dlSize) < safeSpace;
//    }
//
//    @Override
//    public void onDownloadStatusCallBack(final DlnaManager.DownLoadStatus status, final String errorCode) {
//        switch (status) {
//            case DOWNLOADER_STATUS_COMPLETED:
//                onSuccess();
//                onStopIt();
//                break;
//            case DOWNLOADER_STATUS_ERROR_OCCURED:
//                onFail(DownloadListener.DownLoadError.DLError_Download, errorCode);
//                onStopIt();
//                break;
//            case DOWNLOADER_STATUS_CANCELLED:
//                onCancel();
//                onStopIt();
//                break;
//            case DOWNLOADER_STATUS_UNKNOWN:
//                break;
//            case DOWNLOADER_STATUS_MOVING:
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onDownloadProgressCallBack(final int progress) {
//        onProgress(progress);
//    }
//
//    /**
//     *ダウンロードパスーを出力する.
//     */
//    private void printDlPathFiles() {
//        DtcpDownloadParam dp = (DtcpDownloadParam) getDownloadParam();
//        if (null != dp) {
//            String path = dp.getSavePath();
//            if (null != path) {
//                File f = new File(path);
//                File[] files = f.listFiles();
//                if (null != files) {
//                    for (File f2 : files) {
//                        if (null != f2) {
//                            DTVTLogger.debug(f2.getAbsolutePath() + ", mSize=" + f2.length());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * ダウンロード停止.
//     */
//    private void onStopIt() {
//        DTVTLogger.debug("dtcp download end, files");
//        printDlPathFiles();
//    }
//
//    @Override
//    public void stop() {
//        DlnaManager.shared().DownloadStop();
//    }
//}
