package com.example.detaildemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.bean.ScheduleInfo;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.provider.data.OttPlayerStartData;
import com.example.detaildemo.utils.comparator.CalendarComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * コンテンツ詳細Utilクラス.
 */
public class ContentDetailUtils {

    /** STR_FORMAT.*/
    private static final String STR_SPLIT_FORMAT = "\\|";
    /** STR_COMMA.*/
    private static final String STR_COMMA = ",";
    /**DTVパッケージ名.*/
    public static final String DTV_PACKAGE_NAME = "jp.co.nttdocomo.dtv";
    /**DTV schema url 変換.*/
    private static final String DTV_URL_FORMAT_CHANGE = "XXXX";
    /**DTV 新バージョン.*/
    private static final int DTV_VERSION_NEW = 60000;
    /**DTVバージョン.*/
    private static final int DTV_VERSION_STANDARD = 52301;
    /**dアニメストアパッケージ名.*/
    public static final String DANIMESTORE_PACKAGE_NAME = "com.nttdocomo.android.danimeapp";
    /**dアニメストアバージョン.*/
    private static final int DANIMESTORE_VERSION_STANDARD = 856;
    /**dTVチャンネルパッケージ名.*/
    private static final String DTVCHANNEL_PACKAGE_NAME = "com.nttdocomo.dch";
    /**dTVチャンネルバージョン.*/
    private static final int DTVCHANNEL_VERSION_STANDARD = 15;
    /**DAZNパッケージ名.*/
    private static final String DAZN_PACKAGE_NAME = "com.dazn";
    /**DAZNバージョン.*/
    private static final int DAZN_VERSION_STANDARD = 129825;
    /**dTVチャンネルカテゴリー放送.*/
    public static final String DTV_CHANNEL_CATEGORY_BROADCAST = "01";
    /**dTVチャンネルカテゴリー見逃し.*/
    public static final String DTV_CHANNEL_CATEGORY_MISSED = "02";
    /**dTVチャンネルカテゴリー関連.*/
    public static final String DTV_CHANNEL_CATEGORY_RELATION = "03";
    /**レスポンス(1).*/
    private static final String METARESPONSE1 = "1";
    /**レスポンス(2).*/
    private static final String METARESPONSE2 = "2";
    /**レスポンス(3).*/
    private static final String METARESPONSE3 = "3";
    /** 作品IDの長さ.*/
    private static final int CONTENTS_ID_VALID_LENGTH = 8;
    /**予約済みタイプ(4).*/
    private static final String RESERVED4_TYPE4 = "4";
    /**予約済みタイプ(7).*/
    private static final String RESERVED4_TYPE7 = "7";
    /**予約済みタイプ(8).*/
    private static final String RESERVED4_TYPE8 = "8";
    /** 16進数から10進数への変換時の指定値. */
    private static final int SOURCE_HEXADECIMAL = 16;
    /** サービスIDをひかりTV用のチャンネル番号に変換する際の倍率. */
    private static final int CONVERT_SEARVICE_ID_TO_CHANNEL_NUMBER = 10;
    /** リモコンキーを地デジ・BS用のチャンネル番号に変換する際の倍率. */
    private static final int CONVERT_REMOCON_KEY_TO_CHANNEL_NUMBER = 10000;
    /** チャンネルNoを地デジ・BS用のチャンネル番号に変換する際の倍率.*/
    private static final int CONVERT_CHANNEL_NO_TO_CHANNEL_NUMBER = 10;
    /** 値渡すキー. */
    public static final String RECORD_LIST_KEY = "recordListKey";
    /** プレイヤー前回のポジション.*/
    public static final String PLAY_START_POSITION = "playStartPosition";
    /** 前回リモートコントローラービュー表示フラグ.*/
    public static final String REMOTE_CONTROLLER_VIEW_VISIBILITY = "visibility";
    /** first visible item position.*/
    public static final String EPISODE_TAB_FIRST_VISIBLE_ITEM_POSITION = "firstVisible";
    /** topPosition.*/
    public static final String EPISODE_TAB_TOP_POSITION = "topPosition";
    /** dataCount.*/
    public static final String EPISODE_TAB_DATA_COUNT = "dataCount";
    /** dataHashMap.*/
    public static final String EPISODE_TAB_DATA_HASH_MAP = "dataHashMap";
    /** 再生停止フラグ.*/
    public static final String IS_PLAYER_PAUSED = "isPlayerPaused";
    /** 前回ViewPagerのタブ位置.*/
    public static final String VIEWPAGER_INDEX = "viewPagerIndex";
    /**コンテンツ詳細予約済みID.*/
    public static final String CONTENTS_DETAIL_RESERVEDID = "1";
    /** bvflg(1).*/
    public static final String BVFLG_FLAG_ONE = "1";
    /** bvflg(0).*/
    public static final String BVFLG_FLAG_ZERO = "0";
    /** アスペクト比(16:9)の16.*/
    public static final int SCREEN_RATIO_WIDTH_16 = 16;
    /** アスペクト比(16:9)の9.*/
    public static final int SCREEN_RATIO_HEIGHT_9 = 9;
    /** ひかり放送中光コンテンツ再生失敗時にリトライを行うエラーコードの開始値.*/
    public static final int RETRY_ERROR_START = 2000;
    /**モバイル視聴不可.*/
    public static final String MOBILEVIEWINGFLG_FLAG_ZERO = "0";
    /** 番組詳細 or 作品情報タブ.*/
    public static final int CONTENTS_DETAIL_INFO_TAB_POSITION = 0;
    /** チャンネルタブ.*/
    public static final int CONTENTS_DETAIL_CHANNEL_EPISODE_TAB_POSITION = 1;
    /** 放送番組タブ.*/
    public static final int CONTENTS_DETAIL_CHANNEL_BROADCAST_TAB_POSITION = 2;
    /** 画面すべてのクリップボタンを更新.*/
    public static final int CLIP_BUTTON_ALL_UPDATE = 0;
    /** チャンネルリストのクリップボタンをのみを更新.*/
    public static final int CLIP_BUTTON_CHANNEL_UPDATE = 1;
    /**コンテンツ詳細のみ.*/
    public final static int CONTENTS_DETAIL_ONLY = 0;
    /**プレイヤーのみ.*/
    public final static int PLAYER_ONLY = 1;
    /**プレイヤーとコンテンツ詳細.*/
    public final static int PLAYER_AND_CONTENTS_DETAIL = 2;
    /** サムネイルにかけるシャドウのアルファ値.*/
    public static final float THUMBNAIL_SHADOW_ALPHA = 0.7f;
    /** サムネイルの初期アルファ値.*/
    public static final float THUMBNAIL_INITIAL_ALPHA = 1.0f;
    /** 多チャンネル放送.*/
    private static final int PLATFORM_TYPE_H4D = 1;
    /** 地デジ.*/
    private static final int PLATFORM_TYPE_TTB = 2;
    /** BS.*/
    private static final int PLATFORM_TYPE_BS = 3;
    /** BS4K.*/
    private static final int PLATFORM_TYPE_BS4K = 4;
    /** エピソードタイトル.*/
    public static final String EPISODE_TITLE = "episode_title";
    /** エピソード情報.*/
    public static final String EPISODE_MESSAGE = "episode_message";
    /** 他サービス.*/
    public static final String EPISODE_OTHERSERVICE = "other_service";
    /** dTVフラグ.*/
    public static final String EPISODE_DTV_FLAG = "dtv_flag";
    /** サービスID.*/
    public static final String EPISODE_SERVICE_ID = "service_id";
    /** isSynopIsAllShow(1).*/
    private static final String IS_SYNOP_IS_ALL_SHOW_ONE = "1";
    /** isSynopIsAllShow(0).*/
    private static final String IS_SYNOP_IS_ALL_SHOW_ZERO = "0";

    /** エラータイプ.*/
    public enum ErrorType {
        /** コンテンツ詳細取得.*/
        contentDetailGet,
        /** スタッフリスト取得.*/
        roleListGet,
        /** レンタルチャンネル取得.*/
        rentalChannelListGet,
        /** レンタルVod取得.*/
        rentalVoidListGet,
        /** チャンネルリスト取得.*/
        channelListGet,
        /** 番組データ取得.*/
        tvScheduleListGet,
        /** あらすじ取得.*/
        recommendDetailGet,
        /** リモコン使用不可.*/
        unableToUseRemoteController,
        /** STB視聴不可（pure dTVのみ発生）.*/
        stbViewingNg,
    }

    /**
     * タブ表示出しわけ.
     */
    public enum TabType {
        /**作品情報のみ.*/
        VOD,
        /**番組詳細のみ.*/
        TV_ONLY,
        /**番組詳細＆チャンネル.*/
        TV_CH,
        /**作品情報＆エピソード.*/
        VOD_EPISODE,
        /**番組詳細＆チャンネル&放送番組.*/
        TV_CH_BROADCAST
    }

    /** コンテンツタイプ(Google Analytics用).*/
    public enum ContentTypeForGoogleAnalytics {
        /** テレビ.*/
        TV,
        /** ビデオ.*/
        VOD,
        /** その他.*/
        OTHER
    }

    /**
     * スタッフ情報TextView作成.
     * @param context context
     * @return textView
     */
    public static TextView createStaffTextView(final Context context) {
        TextView textView = new TextView(context);
        textView.setGravity( Gravity.CENTER_VERTICAL);
        textView.setTextColor( ContextCompat.getColor(context, R.color.contents_detail_schedule_detail_sub_title));
        textView.setLineSpacing(context.getResources().getDimension(R.dimen.contents_detail_content_line_space), 1);
        return textView;
    }

    /**
     * スタッフ情報LayoutParams取得.
     * @param context context
     * @return LayoutParams
     */
    public static LinearLayout.LayoutParams createStaffTextViewParams(final Context context) {
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        contentParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.contents_detail_staff_margin_top), 0, 0);
        return contentParams;
    }

    /**
     * タブ名取得.
     * @param tabType タブタイプ
     * @param context context
     * @return 録画予約確認ダイアログ
     */
    public static String[] getTabNames(final TabType tabType, final Context context) {
        String[] mTabNames = null;
        switch (tabType) {
            case VOD:
                mTabNames = context.getResources().getStringArray(R.array.contents_detail_tab_other_service_vod);
                break;
            case TV_ONLY:
                mTabNames = context.getResources().getStringArray(R.array.contents_detail_tab_other_service_tv);
                break;
            case TV_CH:
                mTabNames = context.getResources().getStringArray(R.array.contents_detail_tabs_tv_ch);
                break;
            case VOD_EPISODE:
                mTabNames = context.getResources().getStringArray(R.array.contents_detail_tabs_vod_episode);
                break;
            case TV_CH_BROADCAST:
                mTabNames = context.getResources().getStringArray(R.array.contents_detail_tabs_tv_ch_broadcast);
                break;
            default:
                break;
        }
        return mTabNames;
    }

    /** OTTコンテンツ判定.
     *
     * @param detailFullData フルメタデータ.
     * @return true  OTTコンテンツ
     * false OTTコンテンツではない
     */
    public static boolean isOttContent(final VodMetaFullData detailFullData) {
        if (detailFullData != null) {
            boolean isOttDetailData = detailFullData.getOttFlg() == ContentUtils.FLG_OTT_VALUE;
            boolean isHikari = ContentUtils.TV_SERVICE_FLAG_HIKARI.equals(detailFullData.getmTv_service());
            return isOttDetailData && isHikari;
        }
        return false;
    }

    /**
     * スクリーン名マップを取得する.
     * @param contentType コンテンツタイプ
     * @param context コンテキスト
     * @param mIsH4dPlayer 放送視聴可否
     * @return スクリーン名マップ
     */
    public static HashMap<String, String> getScreenNameMap(final ContentTypeForGoogleAnalytics contentType, final Context context, final boolean mIsH4dPlayer) {
        HashMap<String, String> screenNameMap = new HashMap<>();
        screenNameMap.put(context.getString(R.string.contents_detail_tab_contents_info),
                context.getString(R.string.google_analytics_screen_name_content_detail_h4d_vod_program_detail));
        if (contentType == ContentTypeForGoogleAnalytics.VOD) {
            screenNameMap.put(context.getString(R.string.contents_detail_tab_program_detail), context.getString(
                    R.string.google_analytics_screen_name_content_detail_h4d_vod_program_detail));
        } else {
            if (mIsH4dPlayer) {
                screenNameMap.put(context.getString(R.string.contents_detail_tab_program_detail), context.getString(
                        R.string.google_analytics_screen_name_player_h4d_broadcast_program_detail));
            } else {
                screenNameMap.put(context.getString(R.string.contents_detail_tab_program_detail), context.getString(
                        R.string.google_analytics_screen_name_content_detail_h4d_broadcast_program_detail));
            }
        }
        if (mIsH4dPlayer) {
            screenNameMap.put(context.getString(R.string.contents_detail_tab_channel), context.getString(
                    R.string.google_analytics_screen_name_player_h4d_broadcast_channel));
        } else {
            screenNameMap.put(context.getString(R.string.contents_detail_tab_channel), context.getString(
                    R.string.google_analytics_screen_name_content_detail_h4d_broadcast_channel));
        }
        screenNameMap.put(context.getString(R.string.contents_detail_tab_episode),
                context.getString(R.string.google_analytics_screen_name_content_detail_h4d_vod_episode));
        return screenNameMap;
    }

    /**
     * ソートを行う.
     * @param channels チャンネル
     */
    public static void sort(final List<ChannelInfo> channels) {
        for (ChannelInfo channel : channels) {
            Collections.sort(channel.getSchedules(), new CalendarComparator());
        }
    }

    /**
     * チャンネルタブの番組表日付を取得.
     * @param channelDate channelDate
     * @param context context
     * @return 日付
     */
    public static String getDateForChannel(final String channelDate, final Context context) {
        String subTitle = null;
        if (channelDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_YYYY_MM_DDHHMMSS, Locale.JAPAN);
            try {
                Calendar calendar = Calendar.getInstance(Locale.JAPAN);
                calendar.setTime(sdf.parse(channelDate));
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                subTitle = (month + 1) + context.getString(R.string.home_contents_slash) + day
                        + context.getString(R.string.home_contents_front_bracket)
                        + DateUtils.STRING_DAY_OF_WEEK[week]
                        + context.getString(R.string.home_contents_back_bracket);
            } catch (ParseException e) {
                DTVTLogger.debug(e);
            }
        }
        return subTitle;
    }

    /**
     * チャンネル情報設定.
     * @param contentsData contentsData
     * @param scheduleInfo scheduleInfo
     */
    public static void setContentsData(final ContentsData contentsData, final ScheduleInfo scheduleInfo) {
        contentsData.setTitle(scheduleInfo.getTitle());
        contentsData.setContentsId(scheduleInfo.getCrId());
        contentsData.setRequestData(scheduleInfo.getClipRequestData());
        contentsData.setThumURL(scheduleInfo.getImageUrl());
        contentsData.setTime(DateUtils.getContentsDetailChannelHmm(scheduleInfo.getStartTime()));
        contentsData.setClipExec(scheduleInfo.isClipExec());
        contentsData.setDispType(scheduleInfo.getDispType());
        contentsData.setDtv(scheduleInfo.getDtv());
        contentsData.setTvService(scheduleInfo.getTvService());
        contentsData.setServiceId(scheduleInfo.getServiceId());
        contentsData.setEventId(scheduleInfo.getEventId());
        contentsData.setCrid(scheduleInfo.getCrId());
        contentsData.setTitleId(scheduleInfo.getTitleId());
    }

    /**
     * 録画予約失敗時エラーダイアログ表示.
     * @param channels チャンネルリスト.
     * @param channelId チャンネルId.
     * @return チャンネル情報
     */
    public static ChannelInfo setPureDchChannelName(final ArrayList<ChannelInfo> channels, final String channelId) {
        ChannelInfo mChannel = null;
        String tvService = ContentUtils.getTvService(ContentUtils.ChannelServiceType.DTV_CH);
        if (!TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(tvService)) {
            for (int i = 0; i < channels.size(); i++) {
                ChannelInfo channel = channels.get(i);
                if (channelId.equals(channel.getServiceId()) && tvService.equals(channel.getService())) {
                    mChannel = channel;
                    break;
                }
            }
        }
        return mChannel;
    }

    /**
     * 再生中のプログレスビュー作成.
     * @param context context
     * @return プログレスビュー
     */
    public static LinearLayout getProgressView(final Context context) {
        LinearLayout progressLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressLayout.setLayoutParams(layoutParams);
        progressLayout.setOrientation(LinearLayout.VERTICAL);
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (context.getResources().getDimension(R.dimen.contents_detail_player_progress_height_width)),
                (int) (context.getResources().getDimension(R.dimen.contents_detail_player_progress_height_width)));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        progressBar.setLayoutParams(params);
        progressLayout.addView(progressBar);
        TextView textView = new TextView(context);
        params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        textView.setLayoutParams(params);
        params.setMargins(0, (int) context.getResources().getDimension(R.dimen.contents_detail_player_progress_message_margin_top), 0, 0);
        textView.setText(context.getResources().getString(R.string.contents_detail_player_progress_message));
        textView.setTextColor(ContextCompat.getColor(context, R.color.basic_text_color_white));
        textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        progressLayout.addView(textView);
        return progressLayout;
    }

    /**
     * 再生用の情報をフィルター処理.
     * @param ottPlayerList ottPlayerList
     * @param serviceId serviceId
     * @param cid cid
     */
    public static OttPlayerStartData filterOttPlayerData(final List<OttPlayerStartData> ottPlayerList, final String serviceId, final String cid) {
        if (TextUtils.isEmpty(serviceId) && TextUtils.isEmpty(cid)) {
            return null;
        }
        if (ottPlayerList != null) {
            for (int i = 0; i < ottPlayerList.size(); i++) {
                OttPlayerStartData ottPlayerStartData = ottPlayerList.get(i);
                if (!OttContentUtils.OTT_PLAY_START_KIND_MAIN.equals(ottPlayerStartData.getKind())) {
                    continue;
                }
                if (!TextUtils.isEmpty(serviceId) && serviceId.equals(ottPlayerStartData.getContentId())
                        || !TextUtils.isEmpty(cid) && cid.equals(ottPlayerStartData.getContentId())) {
                    return ottPlayerStartData;
                }
            }
        }
        return null;
    }
}
