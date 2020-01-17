package com.example.detaildemo.data.provider.data;


/**
 * 端末情報クラス.
 * 　　機能： 端末情報クラスである
 */
public class MobileDeviceInfo {
    /** 5G機種フラグ.*/
    private boolean mIs5GSupported = true;
    /** 2画面対応フラグ.*/
    private boolean mIsDualScreen = false;

    /**
     * 5G機種フラグ取得.
     */
    public boolean is5GSupported() {
        return mIs5GSupported;
    }

    /**
     * 5G機種フラグ設定.
     * @param is5GSupported is5GSupported
     */
    public void setIs5GSupported(boolean is5GSupported) {
        this.mIs5GSupported = is5GSupported;
    }

    /**
     * 2画面対応フラグ取得.
     */
    public boolean isDualScreen() {
        return mIsDualScreen;
    }

    /**
     * 2画面対応フラグ設定.
     * @param isDualScreen isDualScreen
     */
    public void setIsDualScreen(boolean isDualScreen) {
        this.mIsDualScreen = isDualScreen;
    }
}
