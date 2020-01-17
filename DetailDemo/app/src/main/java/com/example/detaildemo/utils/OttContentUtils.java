package com.example.detaildemo.utils;


/**
 * Ott再生用パラメータ定義.
 */
public class OttContentUtils {
    /** 再生種別 ストリーミング再生. */
    public static final int OTT_PLAY_START_PLAY_TYPE_1 = 1;
    /** 再生種別 ダウンロード再生. */
    public static final int OTT_PLAY_START_PLAY_TYPE_2 = 2;
    /** 再生protocol 1. */
    public static final int OTT_PLAY_START_PROTOCOL_1 = 1;
    /** 再生protocol 2. */
    public static final int OTT_PLAY_START_PROTOCOL_2 = 2;
    /** コンテンツの画質 default. */
    public static final int OTT_PLAY_START_QUALITY_DEFAULT = -1;
    /** コンテンツの画質 180p. */
    public static final int OTT_PLAY_START_QUALITY_1 = 1;
    /** コンテンツの画質 360p. */
    public static final int OTT_PLAY_START_QUALITY_2 = 2;
    /** コンテンツの画質 480p(SD). */
    public static final int OTT_PLAY_START_QUALITY_3 = 3;
    /** コンテンツの画質 720p(HD). */
    public static final int OTT_PLAY_START_QUALITY_4 = 4;
    /** コンテンツの画質 1080ｐ(FHD). */
    public static final int OTT_PLAY_START_QUALITY_5 = 5;
    /** コンテンツの画質 2160p(UHD 15Mbps). */
    public static final int OTT_PLAY_START_QUALITY_6 = 6;
    /** コンテンツの画質 2160p(UHD 25Mbps). */
    public static final int OTT_PLAY_START_QUALITY_7 = 7;
    /** 視聴リクエスト default. */
    public static final String OTT_PLAY_START_AVAIL_STATUS_DEFAULT = "";
    /** 視聴確認用のリクエスト. */
    public static final String OTT_PLAY_START_AVAIL_STATUS_TEST = "1.1";
    /** 通常視聴用のリクエスト. */
    public static final String OTT_PLAY_START_AVAIL_STATUS_RELEASE = "1.3";
    /** 視聴フラグdefault. */
    public static final int OTT_PLAY_START_VIEW_CONTINUE_FLG_DEFAULT = -1;
    /** 新規視聴時. */
    public static final int OTT_PLAY_START_VIEW_CONTINUE_FLG_0 = 0;
    /** 継続視聴時. */
    public static final int OTT_PLAY_START_VIEW_CONTINUE_FLG_1 = 1;
    /** kind main. */
    public static final String OTT_PLAY_START_KIND_MAIN = "main";
    /** kind sub. */
    public static final String OTT_PLAY_START_KIND_SUB = "sub";
    /** kind. */
    public static final String OTT_PLAY_START_KIND = "kind";
    /** contentId. */
    public static final String OTT_PLAY_START_CONTENT_ID = "contentId";
    /** playUrl. */
    public static final String OTT_PLAY_START_PLAY_URL = "playUrl";
    /** drmMode. */
    public static final String OTT_PLAY_START_DRM_MODE = "drmMode";
    /** laUrl. */
    public static final String OTT_PLAY_START_LA_URL = "laUrl";
    /** customData. */
    public static final String OTT_PLAY_START_CUSTOM_DATA = "customData";
    /** playList. */
    public static final String OTT_PLAY_START_PLAYLIST = "playList";
    /** playToken. */
    public static final String OTT_PLAY_START_PLAY_TOKEN = "playToken";
    /** serviceId. */
    public static final String OTT_PLAY_START_SERVICE_ID = "serviceId";
    /** cid. */
    public static final String OTT_PLAY_START_CID = "cid";
    /** lid. */
    public static final String OTT_PLAY_START_LID = "lid";
    /** crid. */
    public static final String OTT_PLAY_START_CRID = "crid";
    /** deviceId. */
    public static final String OTT_PLAY_START_DEVICE_ID = "deviceId";
    /** play_type. */
    public static final String OTT_PLAY_START_PLAY_TYPE = "play_type";
    /** protocol. */
    public static final String OTT_PLAY_START_PROTOCOL = "protocol";
    /** quality. */
    public static final String OTT_PLAY_START_QUALITY = "quality";
    /** avail_status. */
    public static final String OTT_PLAY_START_AVAIL_STATUS = "avail_status";
    /** status. */
    public static final String OTT_PLAY_STATUS = "status";
    /** view_continue_flg. */
    public static final String OTT_PLAY_START_VIEW_CONTINUE_FLG = "view_continue_flg";
    /** contentList. */
    public static final String OTT_PLAY_START_CONTENT_LIST = "contentList";
    /**レスポンス エラー番号.*/
    public static final String OTT_PLAYER_GET_RESPONSE_ERRORNO = "errorno";
    /** STATUS(OK). */
    public static final String OTT_PLAY_STATUS_OK = "OK";
    /** STATUS(NG). */
    public static final String OTT_PLAY_STATUS_NG = "NG";
    /** systemId. */
    public static final String OTT_PLAY_SYSTEM_ID = "widevine,playready";

}
