package com.example.detaildemo.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.detaildemo.R;
import com.example.detaildemo.adapter.ContentsAdapter;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.common.UserState;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.bean.channel.ChannelList;
import com.example.detaildemo.data.webapiclient.jsonparser.data.ActiveData;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedChannelListResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ContentUtils {
    /** コンテンツ一覧数.*/
    public final static int HOME_CONTENTS_LIST_COUNT = 11;
    /** ヘッダのmargin.*/
    public final static int HOME_CONTENTS_LIST_START_INDEX = 1;
    /** UIの上下表示順(NOW ON AIR - モバイル専用).*/
    public final static int HOME_CONTENTS_SORT_CHANNEL_MOBILE = HOME_CONTENTS_LIST_START_INDEX;
    /** UIの上下表示順(NOW ON AIR).*/
    public final static int HOME_CONTENTS_SORT_CHANNEL = HOME_CONTENTS_LIST_START_INDEX + 1;
    /** UIの上下表示順(おすすめ番組).*/
    public final static int HOME_CONTENTS_SORT_RECOMMEND_PROGRAM = HOME_CONTENTS_LIST_START_INDEX + 2;
    /** UIの上下表示順(おすすめビデオ).*/
    public final static int HOME_CONTENTS_SORT_RECOMMEND_VOD = HOME_CONTENTS_LIST_START_INDEX + 3;
    /** UIの上下表示順(今日のテレビランキング).*/
    public final static int HOME_CONTENTS_SORT_TODAY = HOME_CONTENTS_LIST_START_INDEX + 4;
    /** UIの上下表示順(ビデオランキング).*/
    public final static int HOME_CONTENTS_SORT_VIDEO = HOME_CONTENTS_LIST_START_INDEX + 5;
    /** UIの上下表示順(視聴中ビデオ).*/
    public final static int HOME_CONTENTS_SORT_WATCHING_VIDEO = HOME_CONTENTS_LIST_START_INDEX + 6;
    /** UIの上下表示順(クリップ[テレビ]).*/
    public final static int HOME_CONTENTS_SORT_TV_CLIP = HOME_CONTENTS_LIST_START_INDEX + 7;
    /** UIの上下表示順(クリップ[ビデオ]).*/
    public final static int HOME_CONTENTS_SORT_VOD_CLIP = HOME_CONTENTS_LIST_START_INDEX + 8;
    /** UIの上下表示順(プレミアム).*/
    public final static int HOME_CONTENTS_SORT_PREMIUM = HOME_CONTENTS_LIST_START_INDEX + 9;
    /** UIの上下表示順(レンタル).*/
    public final static int HOME_CONTENTS_SORT_RENTAL = HOME_CONTENTS_LIST_START_INDEX + 10;
    /** エラー情報の取得用に追加（ジャンル一覧）.*/
    public final static int HOME_CONTENTS_LIST_PER_GENRE = HOME_CONTENTS_LIST_START_INDEX + 11;
    /** エラー情報の取得用に追加（デイリーランク）.*/
    public final static int HOME_CONTENTS_DAILY_RANK_LIST = HOME_CONTENTS_LIST_START_INDEX + 12;
    /** エラー情報の取得用に追加（番組表）.*/
    public final static int HOME_CONTENTS_TV_SCHEDULE = HOME_CONTENTS_LIST_START_INDEX + 13;
    /** レコメンドのCategoryId 01. **/
    public static final String RECOMMEND_CATEGORY_ID_ONE = "01";
    /** レコメンドのCategoryId 02. **/
    public static final String RECOMMEND_CATEGORY_ID_TWO = "02";
    /** レコメンドのCategoryId 03. **/
    public static final String RECOMMEND_CATEGORY_ID_THREE = "03";
    /** レコメンドのCategoryId 04. **/
    public static final String RECOMMEND_CATEGORY_ID_FOUR = "04";
    /** レコメンドのCategoryId 05. **/
    private static final String RECOMMEND_CATEGORY_ID_FIVE = "05";
    /** レコメンドのCategoryId 06. **/
    private static final String RECOMMEND_CATEGORY_ID_SIX = "06";
    /** レコメンドのCategoryId 07. **/
    private static final String RECOMMEND_CATEGORY_ID_SEVEN = "07";
    /** レコメンドのCategoryId 08. **/
    private static final String RECOMMEND_CATEGORY_ID_EIGHT = "08";
    /** レコメンドのCategoryId 10. **/
    private static final String RECOMMEND_CATEGORY_ID_TEN = "10";
    /** レコメンドのCategoryId 11.*/
    public static final String RECOMMEND_CATEGORY_ID_ELEVEN = "11";
    /**DTVコンテンツサービスID.*/
    public static final int DTV_CONTENTS_SERVICE_ID = 15;
    /**DアニメコンテンツサービスID.*/
    public static final int D_ANIMATION_CONTENTS_SERVICE_ID = 17;
    /**DTVチャンネルコンテンツサービスID.*/
    public static final int DTV_CHANNEL_CONTENTS_SERVICE_ID = 43;
    /**DAZNコンテンツサービスID.*/
    public static final int DAZN_CONTENTS_SERVICE_ID = 35;
    /**DTVひかりコンテンツサービスID.*/
    public static final int DTV_HIKARI_CONTENTS_SERVICE_ID = 44;
    /**レコメンド情報キー.*/
    public static final String RECOMMEND_INFO_BUNDLE_KEY = "recommendInfoKey";
    /**検索情報キー.*/
    public static final String SEARCH_INFO_BUNDLE_KEY = "searchInfoKey";
    /**ぷらら情報キー.*/
    public static final String PLALA_INFO_BUNDLE_KEY = "plalaInfoKey";
    /**STB初期設定扉.*/
    public static final String LAUNCH_STB_FROM = "launch_stb_from";
    /**コンテンツ詳細画面からの遷移.*/
    public static final int LAUNCH_STB_CONTENT_DETAIL = 0;
    /**設定画面からの遷移.*/
    public static final int LAUNCH_STB_SETTING = 1;
    /**ホーム画面からの遷移.*/
    public static final int LAUNCH_STB_HOME = 2;
    /**初期リモート視聴設定.*/
    public static final String LAUNCH_REMOTE_SETTING = "launch_remote_setting";
    /** disp_type(tv_program).*/
    public static final String TV_PROGRAM = "tv_program";
    /** disp_type(video_program).*/
    public static final String VIDEO_PROGRAM = "video_program";
    /** disp_type(video_package).*/
    public static final String VIDEO_PACKAGE = "video_package";
    /** disp_type(video_series).*/
    public static final String VIDEO_SERIES = "video_series";
    /** disp_type(subscription_package).*/
    public static final String SUBSCRIPTION_PACKAGE = "subscription_package";
    /** disp_type(series_svod).*/
    public static final String SERIES_SVOD = "series_svod";
    /** tv_service(1).*/
    public static final String TV_SERVICE_FLAG_HIKARI = "1";
    /** tv_service(2).*/
    public static final String TV_SERVICE_FLAG_DCH_IN_HIKARI = "2";
    /** tv_service(3).*/
    public static final String TV_SERVICE_FLAG_TTB = "3";
    /** tv_service(4).*/
    public static final String TV_SERVICE_FLAG_BS = "4";
    /** contents_type(0).*/
    public static final String CONTENT_TYPE_FLAG_ZERO = "0";
    /** contents_type(1).*/
    public static final String CONTENT_TYPE_FLAG_ONE = "1";
    /** contents_type(2).*/
    public static final String CONTENT_TYPE_FLAG_TWO = "2";
    /** contents_type(3).*/
    public static final String CONTENT_TYPE_FLAG_THREE = "3";
    /** dtv(1).*/
    public static final String DTV_FLAG_ONE = "1";
    /** dtv(0).*/
    public static final String DTV_FLAG_ZERO = "0";
    /** チャンネルタイプ kihon_ch.*/
    private static final String CH_TYPE_KIHON = "kihon_ch";
    /** チャンネルタイプ basic_ch.*/
    private static final String CH_TYPE_BASIC = "basic_ch";
    /** チャンネルタイプ trial_free.*/
    private static final String CH_TYPE_TRIAL = "trial_free";
    /** チャンネルタイプ premium_ch.*/
    private static final String CH_TYPE_PREMIUM = "premium_ch";
    /** BVフラグ 1.*/
    private static final String IS_BV_FLAG = "1";
    /** disp_type(WIZARD).*/
    public static final String WIZARD = "wizard";
    // region Google アナリティクス カスタム ディメンション
    /** 契約種別.*/
    public static final int CUSTOMDIMENSION_CONTRACT = 1;
    /** ペアリング.*/
    public static final int CUSTOMDIMENSION_PAIRING = 2;
    /** ログイン.*/
    public static final int CUSTOMDIMENSION_LOGIN = 3;
    /** サービス.*/
    public static final int CUSTOMDIMENSION_SERVICE = 4;
    /** コンテンツ種別1.*/
    public static final int CUSTOMDIMENSION_CONTENTSTYPE1 = 5;
    /** コンテンツ種別2.*/
    public static final int CUSTOMDIMENSION_CONTENTSTYPE2 = 6;
    /** コンテンツ名.*/
    public static final int CUSTOMDIMENSION_CONTENTNAME = 7;
    /** 検索キーワード.*/
    public static final int CUSTOMDIMENSION_KEYWORD = 8;
    /** 視聴デバイス.*/
    public static final int CUSTOMDIMENSION_DEVICE = 9;
    /** ジャンル.*/
    public static final int CUSTOMDIMENSION_GENRE = 10;
    /**リモート視聴設定.*/
    public static final int CUSTOMDIMENSION_REMOTE = 11;
    /**ペアリング操作.*/
    public static final int CUSTOMDIMENSION_PARING_OPERATION = 12;
    /**cookie id session_dA.*/
    public static final int CUSTOMDIMENSION_DCM_ANALYTICS = 13;
    /**アカウント登録機器.*/
    public static final int CUSTOMDIMENSION_ACCOUNT_REGISTER_STB = 18;
    /** tv_service(1).*/
    public static final String CH_TYPE_TV_SERVICE_H4D = "1";
    /** tv_service(2).*/
    public static final String CH_TYPE_TV_SERVICE_D_CH = "2";
    /** tv_service(3).*/
    public static final String CH_TYPE_TV_SERVICE_TTB = "3";
    /** tv_service(4) BS.*/
    public static final String CH_TYPE_TV_SERVICE_BS = "4";
    /** スペース.*/
    public static final String STR_SPACE = " ";
    /** 空白.*/
    public static final String STR_BLANK = "";
    /** ドット.*/
    public static final String STR_DOT = ".";
    /** コロン.*/
    public static final String STR_COLON = ":";
    /** ドル.*/
    public static final String STR_DOLLAR = "$";
    /** サイズ.*/
    private static final int SCHEDULE_FORMAT_SIZE = 10;
    /** 予約されたサービス 多チャン.*/
    private static final String PLATFORM_STB_IPSP = "ipsp";
    /** 予約されたサービス 地デジ.*/
    private static final String PLATFORM_STB_TTB = "ttb";
    /** 予約されたサービス BS.*/
    private static final String PLATFORM_STB_BS = "bs";
    /** 予約されたサービス BS4K.*/
    public static final String PLATFORM_STB_BS4K = "abs";
    /** 予約されたサービス 多チャン.*/
    private static final int PLATFORM_REMOTE_H4D = 1;
    /** 予約されたサービス 地デジ.*/
    private static final int PLATFORM_REMOTE_TTB = 2;
    /** 予約されたサービス BS.*/
    private static final int PLATFORM_REMOTE_BS = 3;
    /** 予約されたサービス BS4K.*/
    public static final int PLATFORM_REMOTE_BS4K = 4;
    /** スターブサーバー.*/
    public static final String LOCAL_SIGNED_ON = "local_signed_on";
    /** パラメータ名「copy」DTCP:CopyNever.*/
    public final static String LABEL_STATUS_COPY_COPYNEVER = "DTCP:CopyNever";
    /** パラメータ名「copy」DTCP:CopyNever.*/
    public final static String LABEL_STATUS_COPY_COPYFREE = "DTCP:CopyFree";
    /** パラメータ名「copy」DTCP:CopyNever.*/
    public final static String LABEL_STATUS_COPY_COPYONCE = "DTCP:CopyOnce";
    /** パラメータ名「copy」DTCP:CopyNever.*/
    public final static String LABEL_STATUS_COPY_COPYNOMORE = "DTCP:CopyNoMore";
    /** r_value PG-12.*/
    public final static String R_VALUE_PG_12 = "PG-12";
    /** r_value R-12.*/
    public final static String R_VALUE_R_12 = "R-12";
    /** r_value R-15.*/
    public final static String R_VALUE_R_15 = "R-15";
    /** r_value R-18.*/
    public final static String R_VALUE_R_18 = "R-18";
    /** r_value R-20.*/
    public final static String R_VALUE_R_20 = "R-20";
    /** マイチャンネル登録位置の上限値.*/
    public static final int MY_CHANNEL_MAX_INDEX = 45;
    /** チャンネルのアダルトタイプ(adult).*/
    public static final String MY_CHANNEL_ADULT_TYPE_ADULT = "adult";
    /** チャンネルのパレンタル設定値(G).*/
    public static final String MY_CHANNEL_R_VALUE_G = "G";
    /** minus one.*/
    public static final int ILLEGAL_VALUE = -1;
    /** 4kFlg 1.*/
    public static final String FLG_4K_ONE = "1";
    /** ottFlg 1.*/
    public static final String FLG_OTT_ONE = "1";
    /** ottDrmflg 1.*/
    public static final String FLG_OTT_DRM_ONE = "1";
    /** ottFlg 1.*/
    public static final int FLG_OTT_VALUE = 1;
    /** STB3号機.*/
    public static final String STB_3 = "TT02";
    /** STB未設定.*/
    public static final String STB_NONE = "未設定";
    /** ブロードキャストコンテンツチェンジアクション. */
    public static final String CONTENTS_CHANGE_ACTION = "CONTENTS_CHANGE_ACTION";
    /** ブロードキャスト該当チャンネルのコンテンツすべて放送終了アクション. */
    public static final String CHANNEL_COMPLETED_ACTION = "CONTENTS_CHANGE_ACTION";
    /** ブロードキャストコンテンツ切り替える場合の通知情報のキー. */
    public static final String NOTICE_INFO = "NOTICE_INFO";
    /** 休止. */
    public static final String OFF_THE_AIR = "休止";
    /** 1リクエストで取得可能なCHが10chまでとなるため. */
    public static final int MAX_CHANNEL_ONE_REQUEST = 10;
    /** 配信期限(avail_end_date/vod_end_date)の判定基準. */
    private static final int AVAILABLE_BASE_DAY = 31;

    /**
     * コンテンツタイプ.
     */
    public enum ContentsType {
        /**テレビ.*/
        TV,
        /**ビデオ.*/
        VOD,
        /**地デジ.*/
        DIGITAL_TERRESTRIAL_BROADCASTING,
        /**BS.*/
        BROADCASTING_SATELLITE,
        /**ひかりTV(番組).*/
        HIKARI_TV,
        /**ひかりTV(番組)放送1時間以内.*/
        HIKARI_TV_WITHIN_TWO_HOUR,
        /**ひかりTV(Now On Air).*/
        HIKARI_TV_NOW_ON_AIR,
        /**ひかりTV(Ott).*/
        HIKARI_TV_OTT,
        /**ひかり内dTVCh(番組).*/
        HIKARI_IN_DCH_TV,
        /**ひかり内dTVCh(番組)(Now On Air).*/
        HIKARI_IN_DCH_TV_NOW_ON_AIR,
        /**ひかり内dTVCh(番組)放送1時間以内.*/
        HIKARI_IN_DCH_TV_WITHIN_TWO_HOUR,
        /**ひかりTV(VOD).*/
        HIKARI_TV_VOD,
        /**ひかり内dTVCh.(検レコメタで判定).*/
        HIKARI_IN_DCH,
        /**ひかり内dTV.*/
        HIKARI_IN_DTV,
        /**ひかり内dTVCh(見逃し).*/
        HIKARI_IN_DCH_MISS,
        /**ひかり内dTVCh(関連).*/
        HIKARI_IN_DCH_RELATION,
        /**ひかり(録画).*/
        HIKARI_RECORDED,
        /**ひかりTV内dch_見逃し(３２以上).*/
        DCHANNEL_VOD_OVER_31,
        /**ひかりTV内dch_見逃し(３1以内).*/
        DCHANNEL_VOD_31,
        /**レンタル.*/
        RENTAL,
        /**プレミアム.*/
        PREMIUM,
        /**PureDTV.*/
        PURE_DTV,
        /**PureDTVCh.*/
        PURE_DTV_CHANNEL,
        /**PureDTVCh(見逃し).*/
        PURE_DTV_CHANNEL_MISS,
        /**PureDTVCh(関連番組).*/
        PURE_DTV_CHANNEL_RELATION,
        /**dアニメストア.*/
        D_ANIME_STORE,
        /**DAZN.*/
        DAZN,
        /**その他.*/
        OTHER
    }

    /**
     * チャンネルタイプ.
     */
    public enum ChannelServiceType {
        /**ひかりTV for docomo.*/
        H4D,
        /**dtvチャンネル.*/
        DTV_CH,
        /**地デジ.*/
        TTB,
        /**BS.*/
        BS,
        BS4K,
        /**UNKOWN.*/
        UNKOWN
    }

    /**
     * 視聴可否種別.
     */
    public enum ViewIngType {
        /**視聴可能.*/
        ENABLE_WATCH,
        /**視聴可能.dCHのみ契約.*/
        ENABLE_WATCH_001,
        /**視聴可能(期限30日以内なので視聴可能期限表示).*/
        ENABLE_WATCH_LIMIT_THIRTY,
        /**視聴可能(期限30日以内なので視聴可能期限表示).dCHのみ契約.*/
        ENABLE_WATCH_LIMIT_THIRTY_001,
        /**視聴可能(期限30日以内なので視聴可能期限表示) ※複数期限があり、期限が一番長いのを基準にする場合.*/
        ENABLE_WATCH_LIMIT_THIRTY_LONGEST,
        /**視聴可能(期限30日超、視聴期限非表示).*/
        ENABLE_WATCH_LIMIT_THIRTY_OVER,
        /**視聴不可(再生導線非表示).*/
        DISABLE_WATCH_AND_PLAY,
        /**視聴不可(再生導線非表示).dCHのみ契約.*/
        DISABLE_WATCH_AND_PLAY_001,
        /**視聴不可契約導線表示.*/
        DISABLE_WATCH_AGREEMENT_DISPLAY,
        /**視聴不可契約導線表示(購入CH判定時).*/
        DISABLE_CHANNEL_WATCH_AGREEMENT_DISPLAY,
        /**視聴不可契約導線表示(購入VOD判定時).*/
        DISABLE_VOD_WATCH_AGREEMENT_DISPLAY,
        /**視聴不可.*/
        DISABLE_WATCH,
        /**購入済みCH判定開始.*/
        PREMIUM_CHECK_START,
        /**購入済みVOD判定開始.*/
        SUBSCRIPTION_CHECK_START,
        /**BSプレミアムチャンネル.*/
        BS_PREMIUM_CH,
        /**視聴可否判定外ステータス ※サーバレスポンスが正常ならこの状態にはならない想定.*/
        NONE_STATUS
    }

    /**
     * コンテンツ種別(ひかり).
     */
    public enum HikariType {
        /**ひかりTV for docomo.*/
        H4D,
        /**ひかりTV内dTVチャンネル.*/
        HIKARITV_IN_DTV_CH,
        /**ひかりTV内dTV.*/
        HIKARITV_IN_DTV,
    }


    /**
     * サービスID(dTV関連)に応じた他サービスであるかどうかを返す.
     *
     * @param id サービスID
     * @return true：他サービス false：ひかり
     */
    public static boolean isOtherService(final int id) {
        switch (id) {
            case DTV_CONTENTS_SERVICE_ID:
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
            case D_ANIMATION_CONTENTS_SERVICE_ID:
            case DAZN_CONTENTS_SERVICE_ID:
                return true;
            default:
                return false;
        }
    }

    /**
     * コンテント表示タイプ.
     */
    public enum DisplayType {
        /**サムネイル.*/
        THUMNAIL,
        /**「契約が必要です」.*/
        CONTRACT,
        /**プレイヤー.*/
        PLAYER
    }

    /**
     * 番組、VODの判断(レコメンド).
     *
     * @param serviceId disp_type
     * @param categoryId tv_service
     * @return VOD、TV、その他
     */
    @SuppressWarnings("OverlyComplexMethod")
    public static ContentsType getContentsTypeByRecommend(final int serviceId, final String categoryId) {
        ContentsType cType = ContentsType.OTHER;
        switch (serviceId) {
            //dTV
            case DTV_CONTENTS_SERVICE_ID:
                if (RECOMMEND_CATEGORY_ID_ONE.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_TWO.equals(categoryId)) {
                    cType = ContentsType.VOD;
                }
                break;
            //dアニメストア
            case D_ANIMATION_CONTENTS_SERVICE_ID:
                if (RECOMMEND_CATEGORY_ID_ONE.equals(categoryId)) {
                    cType = ContentsType.VOD;
                }
                break;
            //DAZN
            case DAZN_CONTENTS_SERVICE_ID:
                if (RECOMMEND_CATEGORY_ID_TWO.equals(categoryId)) {
                    cType = ContentsType.TV;
                }
                break;
            //dTVチャンネル
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
                if (RECOMMEND_CATEGORY_ID_ONE.equals(categoryId)) {
                    cType = ContentsType.TV;
                } else if (RECOMMEND_CATEGORY_ID_TWO.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_THREE.equals(categoryId)) {
                    cType = ContentsType.VOD;
                }
                break;
            //ひかりTV for docomo
            case DTV_HIKARI_CONTENTS_SERVICE_ID:
                if (RECOMMEND_CATEGORY_ID_ONE.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_TWO.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_THREE.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_FOUR.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_ELEVEN.equals(categoryId)) {
                    cType = ContentsType.TV;
                } else if (RECOMMEND_CATEGORY_ID_FIVE.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_SIX.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_EIGHT.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_TEN.equals(categoryId)) {
                    cType = ContentsType.VOD;
                }
                break;
            default:
                break;
        }
        return cType;
    }

    /**
     * サービスID(dTV関連)に応じたサービス名を返す.
     *
     * @param id サービスID
     * @return サービス名
     */
    public static int getContentsServiceName(final int id) {
        switch (id) {
            case DTV_CONTENTS_SERVICE_ID:
                return R.mipmap.label_service_dtv;
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
                return R.mipmap.label_service_dch;
            case D_ANIMATION_CONTENTS_SERVICE_ID:
                return R.mipmap.label_service_danime;
            case DAZN_CONTENTS_SERVICE_ID:
                return R.mipmap.label_service_dazn;
        }
        return R.mipmap.label_service_hikari;
    }

    /**
     * TVコンテンツ(チャンネル名表示)種別の場合 true.
     *
     * @param contentsType コンテンツ種別
     * @return 判定結果
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    public static boolean isChanelNameDisplay(final ContentsType contentsType) {
        if (contentsType == null) {
            // コンテンツ取得に失敗していれば判定不可
            return false;
        }
        switch (contentsType) {
            case TV:
            case HIKARI_TV:
            case HIKARI_TV_WITHIN_TWO_HOUR:
            case HIKARI_TV_NOW_ON_AIR:
            case HIKARI_IN_DCH_TV:
            case HIKARI_IN_DCH_TV_NOW_ON_AIR:
            case HIKARI_IN_DCH_TV_WITHIN_TWO_HOUR:
            case HIKARI_IN_DCH:
            case HIKARI_IN_DCH_MISS:
            case HIKARI_IN_DCH_RELATION:
            case HIKARI_RECORDED:
            case DCHANNEL_VOD_OVER_31:
            case DCHANNEL_VOD_31:
                return true;
            default:
                return false;
        }
    }

    /**
     * コンテンツ配信期限の表示 DREM-2011.
     * コンテンツ配信前表示(対向ぷららサーバ) DREM-2047
     *
     * 表示用のテキストをセットする
     * @param context         コンテクスト
     * @param textView        テキストビュー
     * @param listContentInfo 行データー
     */
    @SuppressWarnings({"EnumSwitchStatementWhichMissesCases", "OverlyComplexMethod", "OverlyLongMethod"})
    public static void setPeriodText(final Context context, final TextView textView, final ContentsData listContentInfo) {
        final String dispType = listContentInfo.getDispType();
        final String tvService = listContentInfo.getTvService();
        final String contentsType = listContentInfo.getContentsType();
        final String estFlg = listContentInfo.getEstFlg();
        final String chsVod = listContentInfo.getChsVod();
        final long availStartDate = listContentInfo.getAvailStartDate();
        final long availEndDate = listContentInfo.getAvailEndDate();
        final long vodStartDate = listContentInfo.getVodStartDate();
        final long vodEndDate = listContentInfo.getVodEndDate();

        final ContentsType periodContentsType = getContentsTypeByPlala(
                dispType, tvService, contentsType, availStartDate, availEndDate, vodStartDate, vodEndDate,
                estFlg, chsVod);

        String viewingPeriod = "";
        switch (periodContentsType) {
            case VOD:// VOD
                if (DateUtils.isBefore(availStartDate)) { //から
                    viewingPeriod = DateUtils.getContentsDateString(context, availStartDate, true);
                } else { //まで、見逃し
                    //VOD(m/d（曜日）まで)
                    viewingPeriod = DateUtils.getContentsDetailVodDate(context, availEndDate);
                }
                break;
            case DCHANNEL_VOD_31: // ひかりTV内dch_見逃し(３1日以内).
                //見逃しには 〇〇まで表示のみ
                //見逃し(m/d（曜日）まで)
                viewingPeriod = DateUtils.getContentsDetailVodDate(context, vodEndDate);
                break;
            case TV:// TV
                //番組(m/d（曜日）h:ii - h:ii)
                long start = 0;
                String startDate = listContentInfo.getPublishStartDate();
                if (!TextUtils.isEmpty(startDate) && DataBaseUtils.isNumber(startDate)) {
                    start = Long.parseLong(listContentInfo.getPublishStartDate());
                }
                viewingPeriod = DateUtils.getContentsDateString(start);
                break;
            case DCHANNEL_VOD_OVER_31: // ひかりTV内dch_見逃し(３２日以上).
            default:
                break;
        }

        // 表示
        switch (periodContentsType) {
            case TV:
            case VOD:
            case DCHANNEL_VOD_31:
                if (!TextUtils.isEmpty(viewingPeriod)) {
                    textView.setText(viewingPeriod);
                    textView.setVisibility( View.VISIBLE);
                }
            case DCHANNEL_VOD_OVER_31:// ひかりTV内dch_見逃し(３２日以上)の場合日付表示しません.
            case OTHER:
            default:
                break;
        }
    }

    // これ聞く->週間番組ランキングがOhterになっている
    /**
     * 番組、VODの判定.
     *
     * @param dispType disp_type
     * @param tvService tv_service
     * @param contentsType contents_type
     * @param availStartDate 配信開始日時(avail_start_date)
     * @param availEndDate 配信終了日時(avail_end_date)
     * @param vodStartDate VOD配信日時(vod_start_date)
     * @param vodEndDate VOD配信日時(vod_end_date)
     * @param estflg ESTフラグ
     * @param chsvod CHSVOD(chsvod)
     * @return VOD、TV、DCHANNEL_VOD_OVER_31、DCHANNEL_VOD_31、その他
     */
    @SuppressWarnings("OverlyComplexMethod")
    public static ContentsType getContentsTypeByPlala(final String dispType, final String tvService,
                                                      final String contentsType, final long availStartDate, final long availEndDate,
                                                      final long vodStartDate, final long vodEndDate, final String estflg,
                                                      final String chsvod) {
        ContentsType cType = ContentsType.OTHER;
        //VODコンテンツかつ当日日付から配信開始日又は配信終了日が31日以内のものは〇〇から or 〇〇まで表示をする
        //VODとして扱うもの→ひかりTV_VOD、ひかりTV内dTV、レンタル、プレミアムビデオ(2018/7/25現在)
        //※現状は disp_type のみで判定できるため disp_type を使用
        if (VIDEO_PROGRAM.equals(dispType)
                || VIDEO_SERIES.equals(dispType)
                || VIDEO_PACKAGE.equals(dispType)
                || SUBSCRIPTION_PACKAGE.equals(dispType)
                || SERIES_SVOD.equals(dispType)) {
            //ひかりTV_VOD、ひかりTV内dTV
            cType = DateUtils.getContentsTypeByAvailEndDate(availStartDate, availEndDate);
        } else if (TV_PROGRAM.equals(dispType)) {
            if (TV_SERVICE_FLAG_HIKARI.equals(tvService) || TV_SERVICE_FLAG_TTB.equals(tvService) || TV_SERVICE_FLAG_BS.equals(tvService)) {
                //ひかりTV_番組
                cType = ContentsType.TV;
            } else if (TV_SERVICE_FLAG_DCH_IN_HIKARI.equals(tvService)) {
                if (CONTENT_TYPE_FLAG_THREE.equals(contentsType)) {
                    // ひかりTV内ｄTVチャンネル関連VODの場合、vod_start/end_dateをパラメータとする
                    cType = DateUtils.getContentsTypeByAvailEndDate(vodStartDate, vodEndDate);
                } else if (CONTENT_TYPE_FLAG_ONE.equals(contentsType)
                        || CONTENT_TYPE_FLAG_TWO.equals(contentsType)) {
                    Calendar cal = Calendar.getInstance();
                    Date nowDate = cal.getTime();
                    cal.setTimeInMillis(vodStartDate * 1000);
                    Date startDate = cal.getTime();
                    if (startDate.compareTo(nowDate) != 1) {
                        //ひかりTV内dTVチャンネル_見逃し
                        if (DateUtils.isOver31Day(vodEndDate)) {
                            //ひかりTV内dTVチャンネル_見逃し(32日以上)
                            cType = ContentsType.DCHANNEL_VOD_OVER_31;
                        } else if (DateUtils.isIn31Day(vodEndDate)) {
                            //ひかりTV内dTVチャンネル_見逃し(31日以内)
                            cType = ContentsType.DCHANNEL_VOD_31;
                        }
                    } else {
                        //ひかりTV内dTVチャンネル_番組
                        cType = ContentsType.TV;
                    }
                } else {
                    //ひかりTV内dTVチャンネル_番組
                    cType = ContentsType.TV;
                }
            }
        }
        return cType;
    }

    /**
     * 地デジまたはBS番組.
     * @param contentsData ContentsData
     * @return true
     */
    public static boolean isBsOrTtbProgramOther(final ContentsData contentsData) {
        if (DataBaseUtils.isNumber(contentsData.getServiceId())) {
            return DTV_HIKARI_CONTENTS_SERVICE_ID == Integer.parseInt(contentsData.getServiceId())
                    && (RECOMMEND_CATEGORY_ID_ONE.equals(contentsData.getCategoryId())
                    || RECOMMEND_CATEGORY_ID_TWO.equals(contentsData.getCategoryId()));
        }
        return false;
    }

    /**
     * 地デジまたはBS番組.
     * @param tvService tvService
     * @return true
     */
    public static boolean isBsOrTtbProgramPlala(final String tvService) {
        return CH_TYPE_TV_SERVICE_TTB.equals(tvService)
                || CH_TYPE_TV_SERVICE_BS.equals(tvService);
    }

    /**
     * チャンネル名表示を別メソッド化.
     * @param context           コンテキスト
     * @param hyphenTextView 「-」表示ビュー
     * @param channelTextView チャンネル名表示ビュー
     * @param listContentInfo 行データ
     * @param type 機能タイプ
     */
    public static ContentsType setChannelNameOrMissedText(final Context context,
                                                          final TextView hyphenTextView,
                                                          final TextView channelTextView,
                                                          final ContentsData listContentInfo,
                                                          final ContentsAdapter.ActivityTypeItem type) {
        final String dispType = listContentInfo.getDispType();
        final String tvService = listContentInfo.getTvService();
        final String contentsType = listContentInfo.getContentsType();
        final String estFlg = listContentInfo.getEstFlg();
        final String chsVod = listContentInfo.getChsVod();
        final long availStartDate = listContentInfo.getAvailStartDate();
        final long availEndDate = listContentInfo.getAvailEndDate();
        final long vodStartDate = listContentInfo.getVodStartDate();
        final long vodEndDate = listContentInfo.getVodEndDate();

        final ContentsType periodContentsType;
        if (type == ContentsAdapter.ActivityTypeItem.TYPE_RECOMMEND_LIST || type == ContentsAdapter.ActivityTypeItem.TYPE_SEARCH_LIST) {
            periodContentsType = getContentsTypeByRecommend(Integer.parseInt(listContentInfo.getServiceId()), listContentInfo.getCategoryId());
        } else {
            periodContentsType = getContentsTypeByPlala(dispType, tvService, contentsType, availStartDate, availEndDate, vodStartDate, vodEndDate,
                    estFlg, chsVod);
        }

        String viewingChannelName = "";
        if (periodContentsType == ContentsType.DCHANNEL_VOD_OVER_31 || periodContentsType == ContentsType.DCHANNEL_VOD_31) {
            //「見逃し」
            viewingChannelName = context.getString(R.string.contents_detail_hikari_d_channel_miss_viewing);

        } else if (periodContentsType == ContentsType.TV) {
            viewingChannelName = listContentInfo.getChannelName();

        }

        // 表示
        if (periodContentsType == ContentsType.TV || periodContentsType == ContentsType.VOD
                || periodContentsType == ContentsType.DCHANNEL_VOD_OVER_31 || periodContentsType == ContentsType.DCHANNEL_VOD_31) {
            if (!TextUtils.isEmpty(viewingChannelName)) {
                //ひかりTV内dch_見逃し(３２以上)の場合、「見逃し」のみ表示する
                if (periodContentsType == ContentsType.DCHANNEL_VOD_OVER_31) {
                    hyphenTextView.setVisibility(View.GONE);
                } else {
                    hyphenTextView.setVisibility(View.VISIBLE);
                }
                channelTextView.setVisibility(View.VISIBLE);
                SpannableString spannableString = new SpannableString(viewingChannelName);
                int subStart = 0;
                int subEnd = 0;
                if (viewingChannelName.contains(context.getString(R.string.contents_detail_hikari_d_channel_miss_viewing))) {
                    subStart = viewingChannelName.indexOf(context.getString(R.string.contents_detail_hikari_d_channel_miss_viewing));
                    subEnd = spannableString.length();

                    //「見逃し」は黄色文字で表示する
                    spannableString.setSpan(new ForegroundColorSpan( ContextCompat.getColor(context, R.color.contents_detail_video_miss_color)),
                            subStart, subEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                channelTextView.setText(spannableString);
            }
        }
        //録画予約一覧画面にチャンネル名を表示する
        if (type == ContentsAdapter.ActivityTypeItem.TYPE_RECORDING_RESERVATION_LIST && !TextUtils.isEmpty(listContentInfo.getChannelName())) {
            hyphenTextView.setVisibility(View.VISIBLE);
            channelTextView.setVisibility(View.VISIBLE);
            channelTextView.setText(listContentInfo.getChannelName());
        }
        return periodContentsType;
    }

    /**
     * 多階層コンテンツであるか判定する.
     * @param contentsData コンテンツデータ
     * @return 多階層
     */
    public static boolean isChildContentList(@Nullable final ContentsData contentsData) {
        return null != contentsData && contentsData.hasChildContentList();
    }


    /**
     * コンテンツ種別判定(ひかり).
     *
     * @param metaFullData コンテンツ詳細データ(ひかり)
     * @return ContentsType
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public static ContentsType getHikariContentsType(final VodMetaFullData metaFullData) {
        //配信種別
        String dispType = metaFullData.getDisp_type();
        //TVサービス
        String tvService = metaFullData.getmTv_service();
        //コンテンツ種別
        String contentsType = metaFullData.getmContent_type();
        //DTVフラグ
        String dTvFlag = metaFullData.getDtv();
        //Vod配信日時
        long vodStartDate = metaFullData.getmVod_start_date();
        //現在時刻
        long current = DateUtils.getNowTimeFormatEpoch();
        //Now On Air フラグ
        boolean isNowOnAir = isNowOnAir(metaFullData.getPublish_start_date(), metaFullData.getPublish_end_date());

        if (dispType == null) {
            return ContentsType.OTHER;
        } else {
            switch (dispType) {
                case VIDEO_PROGRAM:
                case VIDEO_SERIES:
                    //dispType=video_program || video_series
                    if (dTvFlag == null) {
                        //dTvFlag=0 || 未設定 -> ひかりTV_VOD
                        return ContentsType.HIKARI_TV_VOD;
                    } else {
                        switch (dTvFlag) {
                            case DTV_FLAG_ONE:
                                //dTvFlag=1 -> ひかりTV内dTV
                                return ContentsType.HIKARI_IN_DTV;
                            default:
                                //dTvFlag=0 || 未設定 -> ひかりTV_VOD
                                return ContentsType.HIKARI_TV_VOD;
                        }
                    }
                case TV_PROGRAM:
                    //dispType=tv_program
                    if (tvService == null) {
                        //tvService=other
                        return ContentsType.OTHER;
                    } else {
                        switch (tvService) {
                            case TV_SERVICE_FLAG_HIKARI:
                            case TV_SERVICE_FLAG_TTB:
                            case TV_SERVICE_FLAG_BS:
                                //TODO:OTT再生させるため、仮実装start
                                if (metaFullData.getOttFlg() == 1) {
                                    return ContentsType.HIKARI_TV_OTT;
                                }
                                //TODO:OTT再生させるため、仮実装end
                                //tv_service=1、3、4 -> ひかりTV_番組
                                if (isNowOnAir) {
                                    return ContentsType.HIKARI_TV_NOW_ON_AIR;
                                } else if (DateUtils.isWithInTwoHour(metaFullData.getPublish_start_date())) {
                                    //配信開始が現在時刻の2時間以内のひかりTV
                                    return ContentsType.HIKARI_TV_WITHIN_TWO_HOUR;
                                } else {
                                    return ContentsType.HIKARI_TV;
                                }
                            case TV_SERVICE_FLAG_DCH_IN_HIKARI:
                                //tv_service=2
                                if (contentsType == null) {
                                    //contentsType=other -> ひかりTV_番組
                                    if (isNowOnAir) {
                                        return ContentsType.HIKARI_IN_DCH_TV_NOW_ON_AIR;
                                    } else if (DateUtils.isWithInTwoHour(metaFullData.getPublish_start_date())) {
                                        //配信開始が現在時刻の2時間以内のひかりTV
                                        return ContentsType.HIKARI_IN_DCH_TV_WITHIN_TWO_HOUR;
                                    } else {
                                        return ContentsType.HIKARI_IN_DCH_TV;
                                    }
                                } else {
                                    switch (contentsType) {
                                        case CONTENT_TYPE_FLAG_ONE:
                                        case CONTENT_TYPE_FLAG_TWO:
                                            if (vodStartDate <= current) {
                                                //VOD配信日時(vod_start_date) <= 現在時刻 -> ひかりTV内dTVチャンネル_見逃し
                                                return ContentsType.HIKARI_IN_DCH_MISS;
                                            } else if (DateUtils.isWithInTwoHour(metaFullData.getPublish_start_date())) {
                                                //現在時刻 + 2h > 放送開始日時(metaFullData.getPublish_start_date()) > 現在時刻 -> ひかりTV内dTVチャンネル_番組放送開始2時間以内
                                                return ContentsType.HIKARI_IN_DCH_TV_WITHIN_TWO_HOUR;
                                            } else {
                                                //VOD配信日時(vod_start_date) > 現在時刻 -> ひかりTV内dTVチャンネル_番組
                                                return ContentsType.HIKARI_IN_DCH_TV;
                                            }
                                        case CONTENT_TYPE_FLAG_THREE:
                                            //contentsType=3 -> ひかりTV内dTVチャンネル_関連VOD
                                            return ContentsType.HIKARI_IN_DCH_RELATION;
                                        default:
                                            //contentsType=other -> ひかりTV_番組
                                            if (isNowOnAir) {
                                                return ContentsType.HIKARI_IN_DCH_TV_NOW_ON_AIR;
                                            } else if (DateUtils.isWithInTwoHour(metaFullData.getPublish_start_date())) {
                                                //配信開始が現在時刻の2時間以内のひかりTV
                                                return ContentsType.HIKARI_IN_DCH_TV_WITHIN_TWO_HOUR;
                                            } else {
                                                return ContentsType.HIKARI_IN_DCH_TV;
                                            }
                                    }
                                }
                            default:
                                //tvService = other
                                return ContentsType.OTHER;
                        }
                    }
                default:
                    //dispType = other
                    return ContentsType.OTHER;
            }
        }
    }

    /**
     * NowOnAir判定.
     *
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @return 現在放送しているかどうか
     */
    public static boolean isNowOnAir(final String startTime, final String endTime) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            return false;
        }
        Date startDate;
        Date endDate;
        Date nowDate;
        String start = startTime.trim().replaceAll(STR_SPACE, STR_BLANK);
        String end = endTime.trim().replaceAll(STR_SPACE, STR_BLANK);
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.DATE_YYYY_MM_DDHHMMSS, Locale.JAPAN);
        Calendar c = Calendar.getInstance();
        try {
            startDate = format.parse(start);
            endDate = format.parse(end);
            nowDate = c.getTime();
        } catch (ParseException e) {
            DTVTLogger.debug(e);
            return false;
        }
        return (nowDate.getTime() >= startDate.getTime() && nowDate.getTime() <= endDate.getTime());
    }

    /**
     * NowOnAir判定.
     *
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @return 現在放送しているかどうか
     */
    public static boolean isNowOnAir(final long startTime, final long endTime) {
        //現在時刻
        long current = DateUtils.getNowTimeFormatEpoch();
        return (startTime <= current && current < endTime);
    }

    /**
     * 購入済みチャンネルのActiveListから最も未来のvalid_end_dateを取得する.
     *
     * @param response     購入済みチャンネルレスポンス
     * @param channelInfo  チャンネルデータ
     * @return valid_end_date
     */
    public static long getRentalChannelValidEndDate(final PurchasedChannelListResponse response, final ChannelInfo channelInfo) {
        ChannelList channelList = response.getChannelListData();
        Map<String, String> chList = null;
        if (channelList != null && channelList.getChannelList() != null) {
            chList = checkChServiceIdListSame(channelList.getChannelList(), channelInfo);
        }

        //最長のvalid_end_dateを格納する
        long vodLimitDate = 0;
        //購入済みチャンネル一覧取得IF「metadata_list」のチャンネルメタレスポンス「service_id」を番組に紐づくチャンネルの「service_id」と比較
        //一致した場合
        if (chList != null) {
            //対象チャンネルのpuid、sub_puid、CHPACK-puid、CHPACK-sub_puidと購入済みチャンネル一覧取得
            String puId = channelInfo.getPurchaseId();
            String subPuId = channelInfo.getSubPurchaseId();
            String chPackPuId = channelInfo.getChannelPackPurchaseId();
            String chPackSubPuId = channelInfo.getChannelPackSubPurchaseId();
            ArrayList<ActiveData> activeDataList = response.getChActiveData();
            long nowDate = DateUtils.getNowTimeFormatEpoch();
            //複数のvalidEndDateから期限が最も未来のものを抽出する()
            for (ActiveData activeData : activeDataList) {
                String licenseId = activeData.getLicenseId();
                //IF「active_list」の「license_id」と比較して一致した場合
                if (licenseId.equals(puId) || licenseId.equals(subPuId) || licenseId.equals(chPackPuId) || licenseId.equals(chPackSubPuId)) {
                    //一致した「active_list」の「valid_end_date」> 現在時刻の場合（一件でも条件を満たせば視聴可能）
                    long validEndDate = activeData.getValidEndDate();
                    if (validEndDate > nowDate) {
                        //有効期限が最も未来の日付を取得
                        if (vodLimitDate < validEndDate) {
                            vodLimitDate = validEndDate;
                        }
                    }
                }
            }
        }
        return vodLimitDate;
    }

    /**
     * 購入済みCH一覧のservice_id_uniqと対象のCHのservice_id_uniqが一致するか確認.
     *
     * @param chList 購入済みCHリスト
     * @param channelInfo チャンネルデータ
     * @return true:一致 false:不一致
     */
    private static Map<String, String> checkChServiceIdListSame(final List<Map<String, String>> chList, final ChannelInfo channelInfo) {

        //CHのservice_id一覧を取得
        for (Map<String, String> hashMap : chList) {
            String serviceIdUniq = hashMap.get( JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ);
            if (!TextUtils.isEmpty(serviceIdUniq)) {
                if (serviceIdUniq.equals(channelInfo.getServiceIdUniq())) {
                    //service_idが一致
                    return hashMap;
                }
            }
        }
        return null;
    }

    /**
     * 購入済みチャンネル視聴可否判定.
     *
     * @param contentsData  contentsData
     * @param validEndDate  valid_end_date
     * @return 購入済みチャンネル視聴可否ステータス
     */
    public static ViewIngType getRentalChannelViewingTypeOfNowOnAir(
            final ContentsData contentsData, final long validEndDate) {

        long nowDate = DateUtils.getNowTimeFormatEpoch();

        //getRentalChannelValidEndDateでvalidEndDateを取得した場合、次の条件に一致しないものは validEndDate = 0 で返却されるため、視聴可否判定にvalidEndDateを使用する
        //購入済みチャンネル一覧取得IF「metadata_list」のチャンネルメタレスポンス「service_id」を番組に紐づくチャンネルの「service_id」と比較
        //対象チャンネルのpuid、sub_puid、CHPACK-puid、sub_puidと購入済みチャンネル一覧取得IF「active_list」の「license_id」と比較
        if (validEndDate > 0) {
            long publishStartDate = StringUtils.changeString2Long(contentsData.getPublishStartDate());
            long publishEndDate = StringUtils.changeString2Long(contentsData.getPublishEndDate());
            //メタレスポンス「publish_start_date」 <= 現在時刻 < 「publish_end_date」
            if (publishStartDate <= nowDate && nowDate < publishEndDate) {
                //視聴可能
                return ViewIngType.ENABLE_WATCH;
            } else {
                //視聴不可(放送時間外のため再生導線を非表示)
                return ViewIngType.DISABLE_WATCH_AND_PLAY;
            }
            //対象チャンネルのpuid、sub_puid、CHPACK-puid、sub_puidと購入済みチャンネル一覧取得IF「active_list」の「license_id」と比較して一致しなかった場合
        } else {
            //すべて不一致の場合は視聴不可(契約導線を表示する)
            DTVTLogger.debug("Unviewable(CH purchased info mismatch)");
            return ViewIngType.DISABLE_CHANNEL_WATCH_AGREEMENT_DISPLAY;
        }
    }

    /**
     * ログインしていたｄアカウントとSTBの紐づける機種判定.
     * @param context context
     * @return 3号機とのペアリング状態
     */
    public static boolean isTT02(final Context context) {
        if (context == null || SharedPreferencesUtils.getSharedKeyPreferencesStbInfo(context) == null) {
            return false;
        }
        UserState userState = UserInfoUtils.getUserState(context);
        if (UserState.LOGIN_NG.equals(userState)) {
            return false;
        }
        return STB_3.equals(SharedPreferencesUtils.getSharedKeyPreferencesStbInfo(context));
    }

    /**
     * 契約導線表示状態を取得.
     *
     * @param viewIngType 視聴可否種別
     * @return 契約導線表示フラグ
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    public static boolean isContractWireDisplay(@NonNull final ViewIngType viewIngType) {
        switch (viewIngType) {
            case DISABLE_WATCH_AGREEMENT_DISPLAY:
            case DISABLE_CHANNEL_WATCH_AGREEMENT_DISPLAY:
            case DISABLE_VOD_WATCH_AGREEMENT_DISPLAY:
                return true;
            default:
                return false;
        }
    }

    /**
     * コンテンツ詳細「NO ON AIR」タブ視聴可否ステータス.
     * 仕様が複雑なため、確認を簡単にするため、仕様の文章と連動する記載とする.
     *
     * @param contentsData Vodメタデータ
     * @return 視聴可否判定結果
     */
    public static ViewIngType getViewIngTypeOfNowOnAir(final ContentsData contentsData) {
        if (contentsData == null) return ViewIngType.NONE_STATUS;
        String chType = contentsData.getChannelType();
        String tvService = contentsData.getTvService();
        long publishStartDate = StringUtils.changeString2Long(contentsData.getPublishStartDate());
        long publishEndDate = StringUtils.changeString2Long(contentsData.getPublishEndDate());
        long nowDate = DateUtils.getNowTimeFormatEpoch();
        //メタレスポンス「tv_service」が「1」
        if (TV_SERVICE_FLAG_HIKARI.equals(tvService)) {
            //チャンネルメタレスポンス「ch_type」が「kihon_ch」、「basic_ch」、「trial_free」
            if (CH_TYPE_KIHON.equals(chType) || CH_TYPE_BASIC.equals(chType) ||
                    CH_TYPE_TRIAL.equals(chType)) {
                //メタレスポンス「publish_start_date」 <= 現在時刻 < 「publish_end_date」
                if (publishStartDate <= nowDate && nowDate < publishEndDate) {
                    //視聴可能
                    return ViewIngType.ENABLE_WATCH;
                    //メタレスポンス「publish_start_date」 > 現在時刻 または 現在時刻 >= 「publish_end_date」
                } else {
                    //視聴不可(放送時間外のため再生導線を非表示)
                    return ViewIngType.DISABLE_WATCH_AND_PLAY;
                }
                //チャンネルメタレスポンス「ch_type」が「premium_ch」
            } else if (CH_TYPE_PREMIUM.equals(chType)) {
                //購入済み判定開始
                return ViewIngType.PREMIUM_CHECK_START;
            } else {
                //視聴可否判定外
                return ViewIngType.NONE_STATUS;
            }
        } else {
            return ViewIngType.NONE_STATUS;
        }
    }

    /**
     * 過去コンテンツを判定
     * @param endTime OTTコンテンツ終了時間
     * @return true  過去コンテンツ
     *         false 過去コンテンツではない
     */
    public static boolean isPastContent(final long endTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DATE_PATTERN, Locale.JAPAN);
        String endTimeString = DateUtils.formatEpochToString(endTime);
        try {
            Date endDate = simpleDateFormat.parse(endTimeString);
            Calendar now = Calendar.getInstance();
            if (now.getTimeInMillis() > endDate.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            DTVTLogger.debug(e);
        }
        return false;
    }

    /**
     * カスタムディメンション情報設定.
     *
     *  @param loginStatus ログイン
     *  @param serviceName サービス
     *  @param contentsType1 コンテンツ種別1
     *  @param contentsType2 コンテンツ種別2
     *  @param contentsName コンテンツ名
     *  @return カスタムディメンション情報
     */
    public static SparseArray<String> getCustomDimensions(final String loginStatus, final String serviceName,
                                                          final String contentsType1, final String contentsType2, final String contentsName) {
        SparseArray<String> customDimensions = new SparseArray<>();
        if (loginStatus != null) {
            customDimensions.put(ContentUtils.CUSTOMDIMENSION_LOGIN, loginStatus);
        }
        customDimensions.put(ContentUtils.CUSTOMDIMENSION_SERVICE, serviceName);
        customDimensions.put(ContentUtils.CUSTOMDIMENSION_CONTENTSTYPE1, contentsType1);
        customDimensions.put(ContentUtils.CUSTOMDIMENSION_CONTENTSTYPE2, contentsType2);
        customDimensions.put(ContentUtils.CUSTOMDIMENSION_CONTENTNAME, contentsName);
        return customDimensions;
    }

    /**
     * 他サービススクリーム名取得.
     *
     * @param context コンテキスト
     * @param id サービスID
     * @return 他サービススクリーム名
     */
    public static String getOtherServiceScreenName(final Context context, final int id) {
        String screenName = null;
        switch (id) {
            case DTV_CONTENTS_SERVICE_ID:
                screenName = context.getString(R.string.google_analytics_screen_name_content_detail_other_dtv);
                break;
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
                screenName = context.getString(R.string.google_analytics_screen_name_content_detail_other_dtv_ch);
                break;
            case D_ANIMATION_CONTENTS_SERVICE_ID:
                screenName = context.getString(R.string.google_analytics_screen_name_content_detail_other_danime);
                break;
            case DAZN_CONTENTS_SERVICE_ID:
                screenName = context.getString(R.string.google_analytics_screen_name_content_detail_other_dazn);
                break;
            default:
                break;
        }
        return screenName;
    }

    /**
     * ひかりのコンテンツ種別1名取得.
     *
     * @param context コンテキスト
     * @param hikariType ひかりタイプ
     * @return ひかりのコンテンツ種別1名
     */
    public static String getContentsType1(final Context context, final HikariType hikariType) {
        String contentsType1 = null;
        switch (hikariType) {
            case H4D:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_h4d);
                break;
            case HIKARITV_IN_DTV:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_hikari_dtv);
                break;
            case HIKARITV_IN_DTV_CH:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_hikari_dtv_ch);
                break;
            default:
                break;
        }
        return contentsType1;
    }

    /**
     * 他サービスのコンテンツ種別1名取得.
     *
     * @param context コンテキスト
     * @param id サービスID
     * @return 他サービスのコンテンツ種別1名
     */
    public static String getContentsType1(final Context context, final int id) {
        String contentsType1 = null;
        switch (id) {
            case DTV_CONTENTS_SERVICE_ID:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_pure_dtv);
                break;
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_pure_dtv_ch);
                break;
            case D_ANIMATION_CONTENTS_SERVICE_ID:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_pure_danime);
                break;
            case DAZN_CONTENTS_SERVICE_ID:
                contentsType1 = context.getString(R.string.google_analytics_custom_dimension_contents_type1_pure_dazn);
            default:
                break;
        }
        return contentsType1;
    }

    /**
     * 他サービスのサービス名取得.
     *
     * @param context コンテキスト
     * @param id サービスID
     * @return 他サービスのサービス名
     */
    public static String getServiceName(final Context context, final int id) {
        String serviceName = null;
        switch (id) {
            case DTV_CONTENTS_SERVICE_ID:
                serviceName = context.getString(R.string.google_analytics_custom_dimension_service_dtv);
                break;
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
                serviceName = context.getString(R.string.google_analytics_custom_dimension_service_dtv_ch);
                break;
            case D_ANIMATION_CONTENTS_SERVICE_ID:
                serviceName = context.getString(R.string.google_analytics_custom_dimension_service_danime);
                break;
            case DAZN_CONTENTS_SERVICE_ID:
                serviceName = context.getString(R.string.google_analytics_custom_dimension_service_dazn);
                break;
            default:
                break;
        }
        return serviceName;
    }


    /**
     * 視聴可否のみを返却する.
     *
     * @param contentsType コンテンツ種別
     * @return 視聴可否
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    public static boolean isHikariTvProgram(@NonNull final ContentsType contentsType) {
        switch (contentsType) {
            case HIKARI_TV:
            case HIKARI_TV_WITHIN_TWO_HOUR:
            case HIKARI_TV_NOW_ON_AIR:
                return true;
            default:
                return false;
        }
    }


    /**
     * 視聴可否のみを返却する.
     *
     * @param viewIngType 視聴可否種別
     * @return 視聴可否
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    public static boolean isEnableDisplay(@NonNull final ViewIngType viewIngType) {
        switch (viewIngType) {
            case ENABLE_WATCH:
            case ENABLE_WATCH_001:
            case ENABLE_WATCH_LIMIT_THIRTY:
            case ENABLE_WATCH_LIMIT_THIRTY_001:
            case ENABLE_WATCH_LIMIT_THIRTY_LONGEST:
            case ENABLE_WATCH_LIMIT_THIRTY_OVER:
            case DISABLE_WATCH_AND_PLAY:
                return true;
            default:
                return false;
        }
    }

    /**
     * コンテンツ種別から録画ボタン活性表示フラグを取得.
     *
     * @param contentsType コンテンツ種別
     * @return 録画ボタン活性表示フラグ
     */
    @SuppressWarnings("EnumSwitchStatementWhichMissesCases")
    public static boolean isRecordButtonDisplay(@NonNull final ContentsType contentsType) {
        switch (contentsType) {
            case HIKARI_TV:
            case HIKARI_IN_DCH_TV:
                return true;
            default:
                return false;
        }
    }

    /**
     * 購入済みVodデータのActiveListから最も未来のvalid_end_dateを取得する.
     *
     * @param metaFullData Vodメタデータ
     * @param activeList Activeリスト
     * @param isEndDate isEndDate
     * @return isEndDate true:valid_end_date false:ライセンスID
     */
    public static String getRentalVodValidInfo(final VodMetaFullData metaFullData, final List<ActiveData> activeList, final boolean isEndDate) {
        //購入済みVOD取得からの戻り(視聴可否判定)
        String[] liinfArray = metaFullData.getmLiinf_array();
        String puid = metaFullData.getPuid();

        //最長のvalid_end_dateを格納する
        long vodLimitDate = 0;
        String licenseId = "";
        //現在Epoch秒
        long nowDate = DateUtils.getNowTimeFormatEpoch();
        if (!TextUtils.isEmpty(puid) && activeList != null) {
            for (ActiveData activeData : activeList) {
                String license_id = activeData.getLicenseId();
                if (puid.equals(license_id)) {
                    long validEndDate = activeData.getValidEndDate();
                    if (validEndDate > nowDate) {
                        vodLimitDate = validEndDate;
                        licenseId = license_id;
                    }
                    break;
                }
            }
        }

        if (liinfArray != null && activeList != null) {
            for (String liinf : liinfArray) {
                //liinfを"|"区切りで分解する
                if (liinf == null) {
                    continue;
                }
                String[] column = liinf.split( Pattern.quote("|"), 0);
                for (ActiveData activeData : activeList) {
                    String license_id = activeData.getLicenseId();
                    if (license_id != null) {
                        //対象VODのpuid、liinf_arrayのライセンスID（パイプ区切り）と購入済みＶＯＤ一覧取得IF「active_list」の「license_id」と比較して一致した場合
                        if (column.length > 2 && license_id.equals(column[0])) {
                            //２カラム目 <= sysdate <= ３カラム目 であれば視聴可能。
                            if (DateUtils.getEpochTime(column[1]) <= nowDate && nowDate <= DateUtils.getEpochTime(column[2])) {
                                long validEndDate = activeData.getValidEndDate();
                                //一致した「active_list」の「valid_end_date」> 現在時刻の場合（一件でも条件を満たせば視聴可能）
                                if (validEndDate > nowDate) {
                                    if (vodLimitDate < validEndDate) {
                                        vodLimitDate = validEndDate;
                                        licenseId = license_id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (isEndDate) {
            return String.valueOf(vodLimitDate);
        } else {
            return licenseId;
        }
    }

    /**
     * 番組日付をチェック.
     *
     *  @param startTime 開始時刻
     *  @param endTime 終了時刻
     *  @return true:正　false:不正
     */
    public static boolean checkScheduleDate(final String startTime, final String endTime) {
        //NULL
        if (startTime == null || endTime == null) {
            return false;
        }
        //空文字
        if (startTime.length() == 0 || endTime.length() == 0) {
            return false;
        }
        //時分秒
        if (startTime.trim().length() <= SCHEDULE_FORMAT_SIZE || endTime.trim().length() <= SCHEDULE_FORMAT_SIZE) {
            return false;
        }
        String start = startTime.trim().replaceAll(STR_SPACE, STR_BLANK);
        String end = endTime.trim().replaceAll(STR_SPACE, STR_BLANK);
        Date startDate;
        Date endDate;
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_YYYY_MM_DDHHMMSS, Locale.JAPAN);
        try {
            startDate = sdf.parse(start);
            endDate = sdf.parse(end);
        } catch (ParseException e) {
            return false;
        }
        if (startDate == null || endDate == null) {
            return false;
        }
        return endDate.getTime() > startDate.getTime();
    }

    /**
     * 過去の番組であるかどうか.
     *
     *  @param endTime 終了時刻
     *  @return 過去の番組
     */
    public static boolean isLastDate(final String endTime) {
        Date endDate = new Date();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_YYYY_MM_DDHHMMSS, Locale.JAPAN);
        Calendar c = Calendar.getInstance();
        try {
            endDate = sdf.parse(endTime);
            now = c.getTime();
        } catch (ParseException e) {
            DTVTLogger.debug(e);
        }
        return endDate.getTime() < now.getTime();
    }

    /**
     * チャンネルタイプよりtv_service取得.
     *  @param channelServiceType チャンネルタイプ
     *  @return tv_service
     */
    public static String getTvService(final ChannelServiceType channelServiceType) {
        if (channelServiceType == null) {
            return null;
        }
        String result = null;
        switch (channelServiceType) {
            case H4D:
                result = CH_TYPE_TV_SERVICE_H4D;
                break;
            case DTV_CH:
                result = CH_TYPE_TV_SERVICE_D_CH;
                break;
            case TTB:
                result = CH_TYPE_TV_SERVICE_TTB;
                break;
            case BS:
            case BS4K:
                result = CH_TYPE_TV_SERVICE_BS;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 視聴可否ステータスを取得.
     *
     * @param contractInfo 　契約情報
     * @param metaFullData Vodメタデータ
     * @param channelInfo  Channelメタデータ
     * @return 視聴可否ステータス
     */
    public static ContentUtils.ViewIngType getViewingType(final String contractInfo,
                                                          final VodMetaFullData metaFullData,
                                                          final ChannelInfo channelInfo) {
        if (metaFullData != null) {
            if (contractInfo == null || contractInfo.isEmpty() || UserInfoUtils.CONTRACT_INFO_NONE.equals(contractInfo)) {
                //契約情報が未設定、または"none"の場合は視聴不可(契約導線を表示)
                DTVTLogger.debug("Unviewable(Not contract)");
                return ContentUtils.ViewIngType.DISABLE_WATCH_AGREEMENT_DISPLAY;
            } else if (UserInfoUtils.CONTRACT_INFO_DTV.equals(contractInfo)) {
                return ContentUtils.contractInfoOne(metaFullData);
            } else if (UserInfoUtils.CONTRACT_INFO_H4D.equals(contractInfo)) {
                return ContentUtils.contractInfoTwo(metaFullData, channelInfo);
            } else {
                return ContentUtils.ViewIngType.NONE_STATUS;
            }
        } else {
            return ContentUtils.ViewIngType.NONE_STATUS;
        }
    }

    /**
     * 視聴可否判定、契約情報が"001"の場合.
     *
     * @param metaFullData Vodメタデータ
     * @return 視聴可否判定結果
     */
    @SuppressWarnings("OverlyComplexMethod")
    private static ViewIngType contractInfoOne(final VodMetaFullData metaFullData) {
        DTVTLogger.debug("disp_type: " + metaFullData.getDisp_type());
        //メタレスポンス「disp_type」が「tv_program」
        if (TV_PROGRAM.equals(metaFullData.getDisp_type())) {
            DTVTLogger.debug("tv_service: " + metaFullData.getmTv_service());
            String tvService = metaFullData.getmTv_service();
            //メタレスポンス「tv_service」が「2」
            if (TV_SERVICE_FLAG_DCH_IN_HIKARI.equals(tvService)) {
                long publishStartDate = metaFullData.getPublish_start_date();
                long publishEndDate = metaFullData.getPublish_end_date();
                long nowDate = DateUtils.getNowTimeFormatEpoch();
                //メタレスポンス「publish_start_date」 <= 現在時刻 < 「publish_end_date」
                if (publishStartDate <= nowDate && nowDate < publishEndDate) {
                    //「publish_end_date」が現在日時から1か月以内
                    if (DateUtils.isLimitThirtyDay(publishEndDate)) {
                        //視聴可能期限(「publish_end_dateまで」)を表示
                        return ViewIngType.ENABLE_WATCH_LIMIT_THIRTY_001;
                    } else {
                        //視聴可能期限は非表示
                        return ViewIngType.ENABLE_WATCH_001;
                    }
                    //メタレスポンス「publish_start_date」 >= 現在時刻
                } else if (publishStartDate >= nowDate) {
                    //視聴不可(放送時間外のため再生導線を非表示)
                    DTVTLogger.debug("Unviewable(Hide playing method because outside broadcasting time)");
                    return ViewIngType.DISABLE_WATCH_AND_PLAY_001;
                    //メタレスポンス「publish_end_date」 <= 現在時刻
                } else if (publishEndDate <= nowDate) {
                    long vodStartDate = metaFullData.getmVod_start_date();
                    long vodEndDate = metaFullData.getmVod_end_date();
                    //「vod_start_date」が未設定
                    if (vodStartDate == 0) {
                        //視聴不可
                        return ViewIngType.DISABLE_WATCH;
                        //「vod_start_date」 <= 現在時刻 < 「vod_end_date」
                    } else if (vodStartDate <= nowDate && nowDate < vodEndDate) {
                        //「vod_end_date」が現在時刻から1か月以内の場合視聴可能期限(「vod_end_dateまで」)を表示
                        if (DateUtils.isLimitThirtyDay(vodEndDate)) {
                            return ViewIngType.ENABLE_WATCH_LIMIT_THIRTY_001;
                        } else {
                            return ViewIngType.ENABLE_WATCH_001;
                        }
                        //「vod_end_date」 <= 現在時刻
                    } else if (vodEndDate <= nowDate) {
                        //視聴不可
                        return ViewIngType.DISABLE_WATCH;
                    } else {
                        //視聴可否判定外ステータス
                        return ViewIngType.DISABLE_WATCH;
                    }
                } else {
                    //視聴可否判定外ステータス
                    return ViewIngType.NONE_STATUS;
                }
                //メタレスポンス「tv_service」が「1、3、4」または未設定
            } else if (tvService == null || tvService.equals(TV_SERVICE_FLAG_HIKARI)
                    || tvService.equals(TV_SERVICE_FLAG_TTB) || tvService.equals(TV_SERVICE_FLAG_BS)) {
                //視聴不可(視聴導線を非表示)
                return ViewIngType.DISABLE_WATCH;
            } else {
                //視聴可否判定外ステータス
                return ViewIngType.NONE_STATUS;
            }
        } else {
            //視聴可否判定外ステータス
            return ViewIngType.NONE_STATUS;
        }
    }

    /**
     * 視聴可否判定、契約情報が"002"の場合.
     * 仕様が複雑なため、確認を簡単にするため、仕様の文章と連動する記載とする.
     *
     * @param metaFullData Vodメタデータ
     * @param channelInfo チャンネル情報
     * @return 視聴可否判定結果
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    private static ViewIngType  contractInfoTwo(final VodMetaFullData metaFullData, final ChannelInfo channelInfo) {

        String dispType = metaFullData.getDisp_type();

        String channelServiceIdUniq = null;
        String chType = null;
        //tv_programの場合、チャンネル情報がnullなら判定不可
        if (TV_PROGRAM.equals(dispType) && channelInfo == null) {
            return ViewIngType.NONE_STATUS;
        } else if (channelInfo != null) {
            channelServiceIdUniq = channelInfo.getServiceIdUniq();
            chType = channelInfo.getChannelType();
        }

        String metaServiceIdUniq = metaFullData.getServiceIdUniq();
        String tvService = metaFullData.getmTv_service();
        long publishStartDate = metaFullData.getPublish_start_date();
        long publishEndDate = metaFullData.getPublish_end_date();
        long availStartDate = metaFullData.getAvail_start_date();
        long availEndDate = metaFullData.getAvail_end_date();
        long nowDate = DateUtils.getNowTimeFormatEpoch();
        //メタレスポンス「disp_type」が「tv_program」
        switch (dispType) {
            case TV_PROGRAM:
                //メタレスポンス「tv_service」が「1」
                if (TV_SERVICE_FLAG_HIKARI.equals(tvService)) {
                    //メタレスポンスの「service_id_uniq」とCH一覧取得IFで取得したチャンネルの「service_id_uniq」で番組に紐づくチャンネルを特定する
                    if (!TextUtils.isEmpty(metaServiceIdUniq) && metaServiceIdUniq.equals(channelServiceIdUniq)) {
                        //チャンネルメタレスポンス「ch_type」が「kihon_ch」、「basic_ch」、「trial_free」
                        if (CH_TYPE_KIHON.equals(chType)
                                || CH_TYPE_BASIC.equals(chType)
                                || CH_TYPE_TRIAL.equals(chType)) {
                            //メタレスポンス「publish_start_date」 <= 現在時刻 < 「publish_end_date」
                            if (publishStartDate <= nowDate
                                    && nowDate < publishEndDate) {
                                //視聴可能
                                return ViewIngType.ENABLE_WATCH;
                                //メタレスポンス「publish_start_date」 > 現在時刻 または 現在時刻 >= 「publish_end_date」
                            } else {
                                //視聴不可(放送時間外のため再生導線を非表示)
                                return ViewIngType.DISABLE_WATCH_AND_PLAY;
                            }
                            //チャンネルメタレスポンス「ch_type」が「premium_ch」
                        } else if (CH_TYPE_PREMIUM.equals(chType)) {
                            //購入済み判定開始
                            return ViewIngType.PREMIUM_CHECK_START;
                        } else {
                            //視聴可否判定外
                            return ViewIngType.NONE_STATUS;
                        }
                    } else {
                        //取得したチャンネル情報が不正の場合
                        return ViewIngType.NONE_STATUS;
                    }
                    //メタレスポンス「tv_service」が「2」
                } else if (TV_SERVICE_FLAG_DCH_IN_HIKARI.equals(tvService)) {
                    //メタレスポンス「publish_start_date」 <= 現在時刻 < 「publish_end_date」
                    if (publishStartDate <= nowDate && nowDate < publishEndDate) {
                        //視聴可能期限は非表示
                        return ViewIngType.ENABLE_WATCH;
                        //メタレスポンス「publish_start_date」 >= 現在時刻
                    } else if (publishStartDate >= nowDate) {
                        //視聴不可(放送時間外のため再生導線を非表示)
                        return ViewIngType.DISABLE_WATCH_AND_PLAY;
                        //メタレスポンス「publish_end_date」 <= 現在時刻
                    } else if (publishEndDate <= nowDate) {
                        long vodStartDate = (metaFullData.getmVod_start_date());
                        long vodEndDate = (metaFullData.getmVod_end_date());
                        //「vod_start_date」が未設定
                        if (vodStartDate == 0) {
                            //視聴不可
                            return ViewIngType.DISABLE_WATCH;
                            //「vod_start_date」 <= 現在時刻 < 「vod_end_date」
                        } else if (vodStartDate <= nowDate && nowDate < vodEndDate) {
                            //「vod_end_date」が現在時刻から1か月以内の場合視聴可能期限(「vod_end_dateまで」)を表示
                            if (DateUtils.isLimitThirtyDay(vodEndDate)) {
                                return ViewIngType.ENABLE_WATCH_LIMIT_THIRTY;
                                //視聴可能
                            } else {
                                return ViewIngType.ENABLE_WATCH;
                            }
                            //「vod_end_date」 <= 現在時刻
                        } else if (vodEndDate <= nowDate) {
                            //視聴不可
                            return ViewIngType.DISABLE_WATCH;
                        }
                    } else {
                        //視聴可否範囲外
                        return ViewIngType.NONE_STATUS;
                    }
                    //メタレスポンス「tv_service」が「3」
                } else if (TV_SERVICE_FLAG_TTB.equals(tvService)) {
                    //メタレスポンスの「service_id_uniq」とCH一覧取得IFで取得したチャンネルの「service_id_uniq」で番組に紐づくチャンネルを特定する
                    if (!TextUtils.isEmpty(metaServiceIdUniq) && metaServiceIdUniq.equals(channelServiceIdUniq)) {
                        //チャンネルメタレスポンス「ch_type」が「kihon_ch」
                        if (publishStartDate <= nowDate
                                && nowDate < publishEndDate) {
                            //視聴可能
                            return ViewIngType.ENABLE_WATCH;
                            //メタレスポンス「publish_start_date」 > 現在時刻 または 現在時刻 >= 「publish_end_date」
                        } else {
                            return ViewIngType.DISABLE_WATCH_AND_PLAY;
                        }
                    } else {
                        //取得したチャンネル情報が不正の場合
                        return ViewIngType.NONE_STATUS;
                    }
                    //メタレスポンス「tv_service」が「4」
                } else if (TV_SERVICE_FLAG_BS.equals(tvService)) {
                    //チャンネルメタレスポンス「ch_type」が「premium_ch」
                    if (CH_TYPE_PREMIUM.equals(chType)) {
                        //サムネイル領域文言 「本チャンネル（有料）はテレビで視聴できます。\nチャンネルの契約状態はテレビでご確認ください。」
                        return ViewIngType.BS_PREMIUM_CH;
                    } else {
                        //メタレスポンスの「service_id_uniq」とCH一覧取得IFで取得したチャンネルの「service_id_uniq」で番組に紐づくチャンネルを特定する
                        if (!TextUtils.isEmpty(metaServiceIdUniq) && metaServiceIdUniq.equals(channelServiceIdUniq)) {
                            if (publishStartDate <= nowDate
                                    && nowDate < publishEndDate) {
                                //視聴可能
                                return ViewIngType.ENABLE_WATCH;
                                //メタレスポンス「publish_start_date」 > 現在時刻 または 現在時刻 >= 「publish_end_date」
                            } else {
                                //視聴不可(放送時間外のため再生導線を非表示)
                                return ViewIngType.DISABLE_WATCH_AND_PLAY;
                            }
                        } else {
                            //取得したチャンネル情報が不正の場合
                            return ViewIngType.NONE_STATUS;
                        }
                    }
                } else {
                    //視聴可否範囲外
                    return ViewIngType.NONE_STATUS;
                }
            case VIDEO_PROGRAM:
            case VIDEO_SERIES:
                //"dtv"の値を確認する
                String dTv = metaFullData.getDtv();
                DTVTLogger.debug("dtv: " + dTv);
                if (dTv != null && DTV_FLAG_ONE.equals(dTv)) {
                    //メタレスポンス「availStartDate」 <= 現在時刻 < 「availEndDate」の場合
                    if (availStartDate <= nowDate && nowDate < availEndDate) {
                        //「availStartDate」が現在日時から1か月以内
                        if (DateUtils.isLimitThirtyDay(availEndDate)) {
                            //視聴可能期限(「availEndDateまで」)を表示
                            return ViewIngType.ENABLE_WATCH_LIMIT_THIRTY;
                        } else {
                            //「availEndDate」が現在日時から1か月以上先
                            //視聴可能期限は非表示
                            return ViewIngType.ENABLE_WATCH;
                        }
                        //メタレスポンス「availStartDate」 > 現在時刻 または 現在時刻 >= 「availEndDate」の場合
                    } else if (availStartDate > nowDate || nowDate >= availEndDate) {
                        //視聴不可
                        return ViewIngType.DISABLE_WATCH;
                    }
                    //メタレスポンス「dtv」が「0」か未設定の場合
                } else if (dTv == null || dTv.equals(DTV_FLAG_ZERO)) {
                    String bvFlg = metaFullData.getBvflg();
                    //「bvflg」が「1」
                    DTVTLogger.debug("bvflg: " + bvFlg);
                    if (IS_BV_FLAG.equals(bvFlg)) {
                        //メタレスポンス「availStartDate」 <= 現在時刻 < 「availEndDate」
                        if (availStartDate <= nowDate && nowDate < availEndDate) {
                            //「publish_end_date」が現在日時から1か月以内
                            if (DateUtils.isLimitThirtyDay(availEndDate)) {
                                //視聴可能期限(「availEndDateまで」)を表示
                                return ViewIngType.ENABLE_WATCH_LIMIT_THIRTY;
                                //「availEndDate」が現在日時から1か月以上先
                            } else {
                                //視聴可能期限は非表示
                                return ViewIngType.ENABLE_WATCH;
                            }
                        }
                    } else {
                        //bvflgが1ではないので視聴不可
                        DTVTLogger.debug("Unviewable(bvflg != 1)");
                        return ViewIngType.SUBSCRIPTION_CHECK_START;
                    }
                } else {
                    return ViewIngType.NONE_STATUS;
                }
                //メタレスポンス「disp_type」が「subscription_package」
            case SUBSCRIPTION_PACKAGE:
            case SERIES_SVOD:
            case VIDEO_PACKAGE:
                return ViewIngType.SUBSCRIPTION_CHECK_START;
            default:
                return ViewIngType.NONE_STATUS;
        }
    }

    /**
     * チャンネルタイプ取得(検レコサーバ).
     *  @param serviceId サービスID
     *  @param categoryId カテゴリID
     *  @return カスタムディメンション情報
     */
    public static ChannelServiceType getChannelServiceTypeFromRecommend(final int serviceId, final String categoryId) {
        ChannelServiceType channelServiceType = ChannelServiceType.UNKOWN;
        switch (serviceId) {
            case DTV_HIKARI_CONTENTS_SERVICE_ID:
                if (RECOMMEND_CATEGORY_ID_ONE.equals(categoryId)) {
                    channelServiceType = ChannelServiceType.TTB;
                } else if (RECOMMEND_CATEGORY_ID_TWO.equals(categoryId)) {
                    channelServiceType = ChannelServiceType.BS;
                } else if (RECOMMEND_CATEGORY_ID_THREE.equals(categoryId) || RECOMMEND_CATEGORY_ID_EIGHT.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_TEN.equals(categoryId)) {
                    channelServiceType = ChannelServiceType.H4D;
                } else if (RECOMMEND_CATEGORY_ID_FOUR.equals(categoryId) || RECOMMEND_CATEGORY_ID_FIVE.equals(categoryId)
                        || RECOMMEND_CATEGORY_ID_SIX.equals(categoryId)) {
                    channelServiceType = ChannelServiceType.DTV_CH;
                } else if (RECOMMEND_CATEGORY_ID_ELEVEN.equals(categoryId)) {
                    channelServiceType = ChannelServiceType.BS4K;
                }
                break;
            case DTV_CHANNEL_CONTENTS_SERVICE_ID:
                channelServiceType = ChannelServiceType.DTV_CH;
                break;
            default:
                break;
        }
        return channelServiceType;
    }

}
