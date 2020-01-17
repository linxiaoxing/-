package com.example.detaildemo.service.download;


import android.content.Context;

/**
 * このクラスから継承して、DownloaderBaseおよびSubClass用パラメータクラウスである.
 */
public abstract class DownloadParam {
    /**コンテキスト.*/
    private Context mContext;
    /**保存パスー.*/
    private String mSavePath;
    /**保存ファイル名.*/
    private String mSaveFileName;
    /**通知の頻度.*/
    private int mPercentToNotify;
    /**タイトル.*/
    private String mTitle;

    /**
     * 通知の頻度設定.
     * @param mPercentToNotify 通知の頻度
     */
    public void setPercentToNotify(final int mPercentToNotify) {
        this.mPercentToNotify = mPercentToNotify;
    }

    /**
     * 通知の頻度取得.
     * @return 通知の頻度
     */
    public int getPercentToNotity() {
        return mPercentToNotify;
    }

    /**
     * Getter.
     * @return mContext
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Setter.
     * @param context context
     */
    public void setContext(final Context context) {
        this.mContext = context;
    }

    /**
     * Getter.
     * @return mSavePath
     */
    public String getSavePath() {
        return mSavePath;
    }

    /**
     * Setter.
     * @param savePath savePath
     */
    public void setSavePath(final String savePath) {
        this.mSavePath = savePath;
    }

    /**
     * Getter.
     * @return mTitle
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Setter.
     * @param title title
     */
    public void setTitle(final String title) {
        this.mTitle = title;
    }

    /**
     * Getter.
     * @return mSaveFileName
     */
    public String getSaveFileName() {
        return mSaveFileName;
    }

    /**
     * Setter.
     * @param saveFileName saveFileName
     */
    public void setSaveFileName(final String saveFileName) {
        this.mSaveFileName = saveFileName;
    }

    /**
     * 機能：Sub Classで実現し、パラメーター有効かを戻す.
     * @return true or false
     */
    boolean isParamValid() {
        return !(null == mContext || null == mSavePath || 1 > mSavePath.length() || null == mSaveFileName || 1 > mSaveFileName.length());
    }

    /**
     * Sub classesで実現し、DLサイズを戻す.
     * @return dl size
     */
    public abstract long getTotalSizeToDl();
}
