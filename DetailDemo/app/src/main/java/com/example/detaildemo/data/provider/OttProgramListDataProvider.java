package com.example.detaildemo.data.provider;

import android.content.Context;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfoList;
import com.example.detaildemo.data.bean.channel.ChannelList;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.client.RealTimeRankingWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RealTimeRankingList;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DataBaseUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.SharedPreferencesUtils;
import com.example.detaildemo.utils.StringUtils;
import com.example.detaildemo.utils.UserInfoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OTTコンテンツデータプロバイダー.
 */
public class OttProgramListDataProvider implements ScaledDownProgramListDataProvider.ApiDataProviderCallback,
        RealTimeRankingWebClient.RealTimeRankingJsonParserCallback {

    /**
     * リアルタイム再生数上位番組"全て"を設定する.
     */
    private static final int REAL_TIME_RANKING_DISPLAY_TYPE = 0;
    /**
     * 表示するコンテンツの最大件数.
     */
    private static final int MAX_CONTENTS_COUNT_FOR_SHOW = 10;
    /**
     * コンテキスト.
     */
    private Context mContext;
    /**
     * ScaledDownProgramListDataProvider.
     */
    private ScaledDownProgramListDataProvider mScaledDownProgramListDataProvider = null;
    /**
     * リアルタイム再生数上位番組取得WebClient.
     */
    private RealTimeRankingWebClient mRealTimeRankingWebClient = null;
    /**
     * OTTチャンネルコールバック.
     */
    private OttChannelProgramCallback mOttChannelProgramCallback;
    /**
     * 番組切替時のみエラーダイアログを表示する判定.
     */
    private boolean mIsNeedShowError = false;
    /**
     * 日付けString.
     */
    private String mDateString;
    /**
     * コンストラクタ.
     *
     * @param mContext TvProgramListActivity
     */
    public OttProgramListDataProvider(final Context mContext) {
        this.mContext = mContext;
        this.mOttChannelProgramCallback = (OttChannelProgramCallback) mContext;
    }

    @Override
    public void channelInfoCallback(final ChannelInfoList channelsInfo, final String[] serviceIdUniq) {
        DTVTLogger.start();
        if (null != mOttChannelProgramCallback) {
            DTVTLogger.debug("null != mApiDataProviderCallback");
            mOttChannelProgramCallback.onOttChannelProgramCallback(channelsInfo, serviceIdUniq, mIsNeedShowError);
        }
        DTVTLogger.end();
    }

    @Override
    public void channelListCallback(final ArrayList<ChannelInfo> channels) {
        if (channels != null && !channels.isEmpty()) {
            getOttProgramByChannelInfo(channels);
        } else {
            mOttChannelProgramCallback.onOttChannelProgramCallback(null, null, mIsNeedShowError);
        }
    }

    @Override
    public void onRealTimeRankingJsonParsed(final List<RealTimeRankingList> realTimeRankingLists, ErrorState jsonParseError) {
        DTVTLogger.start();

        //ヌルならば、エラー判定を行う
        if (realTimeRankingLists != null && realTimeRankingLists.size() > 0) {
            //NOW ON AIR - モバイル専用をHomeActivityに送る
            List<Map<String, String>> map = realTimeRankingLists.get(0).getRealTimeRankingList();
            List<ContentsData> contentsData = setBroadcastContentData(map);
//            sendRealTimeRankingListData(setChannelName(contentsData, mChannelList, true));
            sendRealTimeRankingListData(contentsData);
        }
        // Test Data
        List<ContentsData> contentsDataTest = setBroadcastContentDataTest();
        sendRealTimeRankingListData(contentsDataTest);
        DTVTLogger.end();
    }

    /**
     * NOW ON AIR - モバイル専用をActivityに送る.
     *
     * @param list 番組リスト
     */
    private void sendRealTimeRankingListData(final List<ContentsData> list) {
        //契約ありの場合のみ「NOW ON AIR」を表示する(番組表表示のためデータをDB保存する必要があるためActivityへのデータ送信抑制のみ実施)
//        if (UserInfoUtils.isContract(mContext)) {
            //List<ContentsData> matchDataList = getContentsDataListMatchAgeInfo(list, true);
            mOttChannelProgramCallback.mobileTvScheduleListCallback(list);
//        }
    }

    /**
     * 年齢情報に該当し、且つ画面表示に必要件数分の情報を戻す.
     *
     * @param list 番組リスト
     * @param isNowOnAirOnlyMobileData NOW ON AIRモバイル専用データかどうかのフラグ
     * @return 条件に該当する番組リスト
     */
    private List<ContentsData> getContentsDataListMatchAgeInfo(final List<ContentsData> list, final boolean isNowOnAirOnlyMobileData) {
        List<ContentsData> baseList = new ArrayList<>();
        int userAge = UserInfoUtils.getUserAgeInfoWrapper( SharedPreferencesUtils.getSharedPreferencesUserInfo(mContext));

        for (ContentsData data : list) {
            if (!StringUtils.isParental(userAge, data.getRValue())) {
                int position = -1;
                for (int i = 0; i < baseList.size(); i++) {
                    // 該当チャンネルのNOW ON AIRコンテンツ重複チェック.
                    String serviceIdUniq = data.getServiceIdUniq();
                    if (!TextUtils.isEmpty(serviceIdUniq) && serviceIdUniq.equals(baseList.get(i).getServiceIdUniq())) {
                        position = i;
                    }
                }
                if (position != -1) {
                    baseList.set(position, data);
                } else {
                    baseList.add(data);
                }
            }
            // Now ON Air モバイル専用の場合、全カウント表示
            if (!isNowOnAirOnlyMobileData) {
//                if (baseList.size() >= MAX_CONTENTS_COUNT_FOR_SHOW) {
//                    break;
//                }
            }
        }

        return baseList;
    }

    /**
     * 放送番組データ整形.
     * @param mapList 整形前放送番組データmapList
     * @return 整形後の放送番組データ
     */
    private List<ContentsData> setBroadcastContentData(final List<Map<String, String>> mapList) {
        List<ContentsData> contentsDataList = new ArrayList<>();
        ContentsData contentInfo;
        for (int i = 0; i < mapList.size(); i++) {
            contentInfo = new ContentsData();
            contentInfo.setTime(mapList.get(i).get(JsonConstants.META_RESPONSE_DISPLAY_START_DATE));
            contentInfo.setTitle(mapList.get(i).get(JsonConstants.META_RESPONSE_TITLE));
            contentInfo.setDtv(mapList.get(i).get(JsonConstants.META_RESPONSE_DTV));
            if (ContentUtils.DTV_FLAG_ONE.equals(contentInfo.getDtv())) {
                contentInfo.setThumURL(mapList.get(i).get(JsonConstants.META_RESPONSE_DTV_THUMB_448));
                contentInfo.setThumDetailURL(mapList.get(i).get(JsonConstants.META_RESPONSE_DTV_THUMB_640));
            } else {
                contentInfo.setThumURL(mapList.get(i).get(JsonConstants.META_RESPONSE_THUMB_448));
                contentInfo.setThumDetailURL(mapList.get(i).get(JsonConstants.META_RESPONSE_THUMB_640));
            }
            contentInfo.setPublishStartDate(mapList.get(i).get(JsonConstants.META_RESPONSE_PUBLISH_START_DATE));
            contentInfo.setPublishEndDate(mapList.get(i).get(JsonConstants.META_RESPONSE_PUBLISH_END_DATE));
            contentInfo.setServiceId(mapList.get(i).get(JsonConstants.META_RESPONSE_SERVICE_ID));
            contentInfo.setServiceIdUniq(mapList.get(i).get(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ));
            contentInfo.setTvService(mapList.get(i).get(JsonConstants.META_RESPONSE_TV_SERVICE));
            contentInfo.setChannelNo(mapList.get(i).get(JsonConstants.META_RESPONSE_CHNO));
            contentInfo.setRValue(mapList.get(i).get(JsonConstants.META_RESPONSE_R_VALUE));
            contentInfo.setDispType(mapList.get(i).get(JsonConstants.META_RESPONSE_DISP_TYPE));
            contentInfo.setContentsType(mapList.get(i).get(JsonConstants.META_RESPONSE_CONTENT_TYPE));
            contentInfo.setContentsId(mapList.get(i).get(JsonConstants.META_RESPONSE_CRID));
            contentInfo.setCrid(mapList.get(i).get(JsonConstants.META_RESPONSE_CRID));
            contentInfo.setRatStar(mapList.get(i).get(JsonConstants.META_RESPONSE_RATING));
            contentInfo.setSynop(mapList.get(i).get(JsonConstants.META_RESPONSE_SYNOP));
            contentInfo.setEventId(mapList.get(i).get(JsonConstants.META_RESPONSE_EVENT_ID));
            contentInfo.setAvailStartDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_AVAIL_START_DATE)));
            contentInfo.setAvailEndDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_AVAIL_END_DATE)));
            contentInfo.setVodStartDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_VOD_START_DATE)));
            contentInfo.setVodEndDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_VOD_END_DATE)));
            contentsDataList.add(contentInfo);
        }
        return contentsDataList;
    }

    /**
     * 放送番組データ整形.
     * @return 整形後の放送番組データ
     */
    private List<ContentsData> setBroadcastContentDataTest() {
        List<ContentsData> contentsDataList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            ContentsData contentInfo = new ContentsData();
            contentInfo.setTime("2020/01/20 9:00");
            contentInfo.setTitle("なないろ日和！");
            contentInfo.setDtv("1");
            if (ContentUtils.DTV_FLAG_ONE.equals(contentInfo.getDtv())) {
                contentInfo.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201064/201064_201912160800.webp");
                contentInfo.setThumDetailURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0428.webp");
            } else {
                contentInfo.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201064/201064_201912160800.webp");
                contentInfo.setThumDetailURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0428.webp");
            }
            contentInfo.setPublishStartDate("2020/01/20");
            contentInfo.setPublishEndDate("2020/02/20");
            contentInfo.setServiceId("428");
            if (i == 2) {
                contentInfo.setServiceIdUniq("ttb_0408");

            } else {
                contentInfo.setServiceIdUniq("ttb_0428");
                contentInfo.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201024/201024_201912160815.webp");
                contentInfo.setThumDetailURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0400.webp");
            }
            contentInfo.setTvService("");
            contentInfo.setChannelNo("428");
            contentInfo.setRValue("");
            contentInfo.setDispType("");
            contentInfo.setContentsType("");
            contentInfo.setContentsId("");
            contentInfo.setCrid("");
            contentInfo.setRatStar("");
            contentInfo.setSynop("");
            contentInfo.setEventId("");
            contentsDataList.add(contentInfo);
        }
        return contentsDataList;
    }


    /**
     * コンテンツのServiceIDとServiceIDが一致するチャンネル名を追加する.
     *
     * @param contentDataList コンテンツリスト
     * @param channelList  チャンネルリスト
     * @param isPlala true:ぷららサーバ  false:レコメンドサーバ
     * @return チャンネル名付いているコンテンツリスト
     */
    private synchronized List<ContentsData> setChannelName(final List<ContentsData> contentDataList, final ChannelList channelList, final boolean isPlala) {
        if (contentDataList == null) {
            return null;
        }
        List<ContentsData> tmpContentDataList = new ArrayList<>(contentDataList);
        if (channelList == null || tmpContentDataList.isEmpty()) {
            return tmpContentDataList;
        }
        List<Map<String, String>> list = new ArrayList<>(channelList.getChannelList());
        for (int i = 0; i < tmpContentDataList.size(); i++) {
            if (!isPlala) {
                String channelId = tmpContentDataList.get(i).getChannelId();
                String serviceId = tmpContentDataList.get(i).getServiceId();
                String categoryId = tmpContentDataList.get(i).getCategoryId();
                if (DataBaseUtils.isNumber(serviceId)) {
                    int serviceID = Integer.parseInt(serviceId);
                    ContentUtils.ChannelServiceType serviceType = ContentUtils.getChannelServiceTypeFromRecommend(serviceID, categoryId);
                    String tvService = ContentUtils.getTvService(serviceType);
                    if (!TextUtils.isEmpty(tvService) && !TextUtils.isEmpty(channelId)) {
                        for (int j = 0; j < list.size(); j++) {
                            Map<String, String> hashMap = list.get(j);
                            if (tvService.equals(hashMap.get(JsonConstants.META_RESPONSE_SERVICE))) {
                                if (serviceType == ContentUtils.ChannelServiceType.BS4K) {
                                    //BS4Kコンテンツの場合
                                    if (channelId.equals(hashMap.get(JsonConstants.META_RESPONSE_SERVICE_ID)) && ContentUtils.FLG_4K_ONE.equals(hashMap.get(JsonConstants.META_RESPONSE_4KFLG))) {
                                        tmpContentDataList.get(i).setChannelName(hashMap.get(JsonConstants.META_RESPONSE_TITLE));
                                    }
                                } else if (channelId.equals(hashMap.get(JsonConstants.META_RESPONSE_SERVICE_ID))) {
                                    tmpContentDataList.get(i).setChannelName(hashMap.get(JsonConstants.META_RESPONSE_TITLE));
                                }
                            }
                        }
                    }
                }
            } else {
                String serviceIdUniq = tmpContentDataList.get(i).getServiceIdUniq();
                if (!TextUtils.isEmpty(serviceIdUniq)) {
                    for (int j = 0; j < list.size(); j++) {
                        Map<String, String> hashMap = list.get(j);
                        if (serviceIdUniq.equals(hashMap.get(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ))) {
                            tmpContentDataList.get(i).setChannelName(hashMap.get(JsonConstants.META_RESPONSE_TITLE));
                        }
                    }
                }
            }
        }
        return tmpContentDataList;
    }

    /**
     * 1日分のOtt全チャンネル番組を取得処理.
     * @param serviceIdUniqList serviceIdUniqList
     */
    private void getOttChannelListOneDay(final List<String> serviceIdUniqList) {
        DTVTLogger.start();
        if (!serviceIdUniqList.isEmpty()) {
            int requestCount = serviceIdUniqList.size() / ContentUtils.MAX_CHANNEL_ONE_REQUEST;
            for (int i = 1; i <= requestCount; i++) {
                getOttProgram(serviceIdUniqList.subList((i - 1) * ContentUtils.MAX_CHANNEL_ONE_REQUEST,
                        i * ContentUtils.MAX_CHANNEL_ONE_REQUEST - 1));
            }
            if (serviceIdUniqList.size() % ContentUtils.MAX_CHANNEL_ONE_REQUEST > 0) {
                getOttProgram(serviceIdUniqList.subList(requestCount * ContentUtils.MAX_CHANNEL_ONE_REQUEST,
                        serviceIdUniqList.size() - 1));
            }
        }
        DTVTLogger.end();
    }

    @Override
    public void clipKeyResult() {
        //NOP
    }

    /**
     * 全チャンネルを取得する.
     * @param dateString dateString
     * @param channelInfos channelInfos
     * @param isNeedShowError isNeedShowError
     */
    public void getAllChannel(final String dateString, final ArrayList<ChannelInfo> channelInfos, final boolean isNeedShowError) {
        this.mIsNeedShowError = isNeedShowError;
        this.mDateString = dateString;
        if (channelInfos != null && !channelInfos.isEmpty()) {
            getOttProgramByChannelInfo(channelInfos);
        } else {
            if (mScaledDownProgramListDataProvider == null) {
                mScaledDownProgramListDataProvider = new ScaledDownProgramListDataProvider(mContext, this);
            }
            mScaledDownProgramListDataProvider.setAreaCode(UserInfoUtils.getAreaCode(mContext));
            mScaledDownProgramListDataProvider.getChannelList(0, 0, "", JsonConstants.CH_SERVICE_TYPE_INDEX_ALL);
        }
    }

    /**
     * getOttProgramByChannelInfo.
     * @param channelInfos channelInfos
     */
    private void getOttProgramByChannelInfo(final ArrayList<ChannelInfo> channelInfos) {
        //1リクエストで取得可能なCHが10chまでとなるため
        List<String> serviceIdUniqList = new ArrayList<>();
        for (ChannelInfo item : channelInfos) {
            if (ContentUtils.FLG_OTT_ONE.equals(item.getOttflg())) {
                serviceIdUniqList.add(item.getServiceIdUniq());
            }
        }
        if (serviceIdUniqList.size() > 0) {
            getOttChannelListOneDay(serviceIdUniqList);
        } else {
            mOttChannelProgramCallback.onOttChannelProgramCallback(null, null, mIsNeedShowError);
        }
    }


    /**
     * 1日分のOtt全チャンネル番組を取得処理メソッド.
     * @param serviceIdUniqList サービスIDユニークリスト
     */
    private void getOttProgram(final List<String> serviceIdUniqList) {
        String programDay = DateUtils.convertToProgramDay(mDateString);
        if (mScaledDownProgramListDataProvider == null) {
            mScaledDownProgramListDataProvider = new ScaledDownProgramListDataProvider(mContext, this);
        }
        mScaledDownProgramListDataProvider.setAreaCode( UserInfoUtils.getAreaCode(mContext));
        mScaledDownProgramListDataProvider.getProgram(serviceIdUniqList.toArray(new String[serviceIdUniqList.size()]), new String[]{programDay});
    }

    /**
     * 「NOW ON AIR - モバイル専用」情報取得.
     */
    public void getTvScheduleMobileListData() {
        DTVTLogger.start();
//        if (!mIsStop) {
//            UserInfoDataProvider userInfoDataProvider = new UserInfoDataProvider(mContext);
//            int ageReq = userInfoDataProvider.getUserAge();
            int ageReq = 20;
            //通信クラスにデータ取得要求を出す
            mRealTimeRankingWebClient = new RealTimeRankingWebClient(mContext);
            mRealTimeRankingWebClient.getRealTimeRankingApi(0,
                    JsonConstants.REAL_TIME_RANKING_DISPLAY_TYPE[REAL_TIME_RANKING_DISPLAY_TYPE],
                    ageReq, WebApiBasePlala.FILTER_RELEASE, this);
//        } else {
//            DTVTLogger.error("RealTimeRankingWebClient is stopping connect");
//        }
        DTVTLogger.end();
    }

    /**
     * １日分のOTTチャンネルリストコールバック.
     */
    public interface OttChannelProgramCallback {
        /**
         * １日分のOTTチャンネルリストコールバック.
         *
         * @param channelsInfo  画面に渡すチャンネル番組情報
         * @param serviceIdUniq サービスユニーク
         * @param isUpdate 番組切替時のみエラーダイアログを表示する判定
         */
        void onOttChannelProgramCallback(ChannelInfoList channelsInfo, String[] serviceIdUniq, boolean isUpdate);
        /**
         * チャンネル一覧（モバイル専用）用コールバック.
         *
         * @param mobileChannelList チャンネルリスト
         */
        void mobileTvScheduleListCallback(List<ContentsData> mobileChannelList);
    }
}