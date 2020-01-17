package com.example.detaildemo.data.provider.data;


import java.util.HashMap;
import java.util.Map;

/**
 * Ott再生用データ.
 */
public class OttPlayerContentData {
    /** 再生用URL.*/
    private String mPlayerUrl;
    /** 再生用licenseUrl.*/
    private String mPlayerLicenseUrl;
    /** ヘッダー.*/
    private Map<String, String> mHeaders = new HashMap<>();

    /** playReadyLicenseAcquisitionUrl.*/
    private String mPlayReadyLicenseAcquisitionUrl;
    /** playReadyCustomData.*/
    private String mPlayReadyCustomData;
    /** playReadyHttpHeaders.*/
    private String mPlayReadyHttpHeaders;

    /** widevineLicenseAcquisitionUrl.*/
    private String mWidevineLicenseAcquisitionUrl;
    /** widevineCustomData.*/
    private String mWidevineCustomData;
    /** widevineCustomData.*/
    private String mWidevineHttpHeaders;

    /** pluginInitUrl.*/
    private String mPluginInitUrl;
    /** tunneledPlaybackEnabled.*/
    private boolean mTunneledPlaybackEnabled;

    /** systemId.*/
    private String mSystemId;

    /**
     * URL取得.
     * @return 再生用URL
     */
    public String getPlayerUrl() {
        return mPlayerUrl;
    }

    /**
     * playerUrl設定.
     * @param playerUrl playerUrl
     */
    public void setPlayerUrl(final String playerUrl) {
        this.mPlayerUrl = playerUrl;
    }

    /**
     * licenseUrl取得.
     * @return 再生用licenseUrl
     */
    public String getPlayerLicenseUrl() {
        return mPlayerLicenseUrl;
    }

    /**
     * LicenseUrl設定.
     * @param playerLicenseUrl LicenseUrl
     */
    public void setPlayerLicenseUrl(final String playerLicenseUrl) {
        this.mPlayerLicenseUrl = playerLicenseUrl;
    }

    /**
     * ヘッダー取得.
     * @return 再生用ヘッダー
     */
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * ヘッダー設定.
     * @param headers ヘッダー
     */
    public void setHeaders(final Map<String, String> headers) {
        this.mHeaders = headers;
    }

    /**
     * mPlayReadyLicenseAcquisitionUrl取得.
     * @return mPlayReadyLicenseAcquisitionUrl
     */
    public String getPlayReadyLicenseAcquisitionUrl() {
        return mPlayReadyLicenseAcquisitionUrl;
    }

    /**
     * playReadyLicenseAcquisitionUrl設定.
     * @param playReadyLicenseAcquisitionUrl playReadyLicenseAcquisitionUrl
     */
    public void setPlayReadyLicenseAcquisitionUrl(final String playReadyLicenseAcquisitionUrl) {
        this.mPlayReadyLicenseAcquisitionUrl = playReadyLicenseAcquisitionUrl;
    }

    /**
     * playReadyCustomData取得.
     * @return mPlayReadyCustomData
     */
    public String getPlayReadyCustomData() {
        return mPlayReadyCustomData;
    }

    /**
     * playReadyCustomData設定.
     * @param playReadyCustomData playReadyCustomData
     */
    public void setPlayReadyCustomData(final String playReadyCustomData) {
        this.mPlayReadyCustomData = playReadyCustomData;
    }

    /**
     * playReadyHttpHeaders取得.
     * @return 再生用playReadyHttpHeaders
     */
    public String getPlayReadyHttpHeaders() {
        return mPlayReadyHttpHeaders;
    }

    /**
     * playReadyHttpHeaders.
     * @param playReadyHttpHeaders playReadyHttpHeaders
     */
    public void setPlayReadyHttpHeaders(final String playReadyHttpHeaders) {
        this.mPlayReadyHttpHeaders = playReadyHttpHeaders;
    }

    /**
     * widevineLicenseAcquisitionUrl取得.
     * @return widevineLicenseAcquisitionUrl
     */
    public String getWidevineLicenseAcquisitionUrl() {
        return mWidevineLicenseAcquisitionUrl;
    }

    /**
     * widevineLicenseAcquisitionUrl設定.
     * @param widevineLicenseAcquisitionUrl widevineLicenseAcquisitionUrl
     */
    public void setWidevineLicenseAcquisitionUrl(final String widevineLicenseAcquisitionUrl) {
        this.mWidevineLicenseAcquisitionUrl = widevineLicenseAcquisitionUrl;
    }

    /**
     * widevineCustomData取得.
     * @return mWidevineCustomData
     */
    public String getWidevineCustomData() {
        return mWidevineCustomData;
    }

    /**
     * widevineCustomData設定.
     * @param widevineCustomData widevineCustomData
     */
    public void setWidevineCustomData(final String widevineCustomData) {
        this.mWidevineCustomData = widevineCustomData;
    }

    /**
     * widevineHttpHeaders取得.
     * @return mWidevineHttpHeaders
     */
    public String getWidevineHttpHeaders() {
        return mWidevineHttpHeaders;
    }

    /**
     * widevineHttpHeaders設定.
     * @param widevineHttpHeaders widevineHttpHeaders
     */
    public void setWidevineHttpHeaders(final String widevineHttpHeaders) {
        this.mWidevineHttpHeaders = widevineHttpHeaders;
    }

    /**
     * pluginInitUrl取得.
     * @return mPluginInitUrl
     */
    public String getmPluginInitUrl() {
        return mPluginInitUrl;
    }

    /**
     * pluginInitUrl設定.
     * @param pluginInitUrl pluginInitUrl
     */
    public void setPluginInitUrl(final String pluginInitUrl) {
        this.mPluginInitUrl = pluginInitUrl;
    }

    /**
     * tunneledPlaybackEnabled取得.
     * @return mTunneledPlaybackEnabled
     */
    public boolean ismTunneledPlaybackEnabled() {
        return mTunneledPlaybackEnabled;
    }

    /**
     * tunneledPlaybackEnabled設定.
     * @param tunneledPlaybackEnabled tunneledPlaybackEnabled
     */
    public void setTunneledPlaybackEnabled(final boolean tunneledPlaybackEnabled) {
        this.mTunneledPlaybackEnabled = tunneledPlaybackEnabled;
    }

    /**
     * systemId取得.
     * @return mSystemId
     */
    public String getSystemId() {
        return mSystemId;
    }

    /**
     * systemId設定.
     * @param systemId systemId
     */
    public void setSystemId(final String systemId) {
        this.mSystemId = systemId;
    }
}

