package com.example.detaildemo.service.download;


/**
 * ダウンロードリスナーインタフェース.
 */
public interface DownloadListener {

    /**
     * ダウンロードエラー定義.
     */
    enum DownLoadError {
        /**サーバーエラー.*/
        DLError_NetLost,
        /**DmsLost.*/
        DLError_DmsLost,
        /**DmsError.*/
        DLError_DmsError,
        /**メモリ不足.*/
        DLError_StorageNoSpace,
        /**Download.*/
        DLError_Download,
        /**パラメータエラー.*/
        DLError_ParamError,
        /**Unactivated.*/
        DLError_Unactivated,
        /**CopyKeyFileFailed.*/
        DLError_CopyKeyFileFailed,
        /**Other.*/
        DLError_Other,
        /**NoError.*/
        DLError_NoError,
    }

    /**
     * ダウンロード開始の時、コールされる.
     * @param totalFileByteSize totalFileByteSize
     */
    void onStart(long totalFileByteSize);

    /**
     * ダウンロード進捗通知.
     *  @param percent ダウンロード進捗
     */
    void onProgress(int percent);

    /**
     * ダウンロードエラー発生の時、コールされる.
     * @param error  error
     * @param savePath savePath, savePathは提供できない場合、""に設定
     * @param errorCode エラーコード
     */
    void onFail(DownLoadError error, final String savePath, final String errorCode);

    /**
     * ダウンロード完了の時、コールされる.
     * @param fullPath  fullPath
     */
    void onSuccess(final String fullPath);

    /**
     * ダウンロードキャンセルの時、コールされる.
     * @param fullPath  fullPath
     */
    void onCancel(final String fullPath);

    /**
     * ダウンロード容量不足の場合、コールされる.
     * @param fullPath fullPath
     */
    void onLowStorageSpace(final String fullPath);
}
