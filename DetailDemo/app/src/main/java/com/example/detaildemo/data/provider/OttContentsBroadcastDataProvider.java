package com.example.detaildemo.data.provider;

import android.content.Context;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfoList;
import com.example.detaildemo.data.webapiclient.WebApiBasePlala;
import com.example.detaildemo.data.webapiclient.client.RealTimeRankingWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RealTimeRankingList;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RoleListMetaData;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedChannelListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedVodListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.RemoteRecordingReservationResultResponse;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.UserInfoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * NOW ON AIRタブのOttコンテンツプロバイダー.
 */
public class OttContentsBroadcastDataProvider implements
        RealTimeRankingWebClient.RealTimeRankingJsonParserCallback,
        ScaledDownProgramListDataProvider.ApiDataProviderCallback,
        ContentsDetailDataProvider.ApiDataProviderCallback {

    /** コンテキスト. */
    private Context mContext;
    /** RealTimeRankingWebClient.*/
    private RealTimeRankingWebClient mRealTimeRankingWebClient = null;
    /** ScaledDownProgramListDataProvider.*/
    private ScaledDownProgramListDataProvider mScaledDownProgramListDataProvider = null;
    /** コンテンツ詳細データプロバイダー.*/
    private ContentsDetailDataProvider mContentsDetailDataProvider = null;
    /** Ott再生用コールバック. */
    private OttContentsBroadcastDataProviderCallback mOttContentsBroadcastDataProviderCallback;
    /** OTTコンテンツデータ.*/
    private List<ContentsData> mContentsData = null;
    /**チャンネルリストListクラス.*/
    private ArrayList<ChannelInfo> mChannelList = null;
    /**リアルタイム再生数上位番組"全て"を設定する.*/
    private static final int REAL_TIME_RANKING_DISPLAY_TYPE = 0;

    /**
     * コンストラクタ.
     */
    public OttContentsBroadcastDataProvider(final Context context) {
        this.mContext = context;
        this.mOttContentsBroadcastDataProviderCallback = (OttContentsBroadcastDataProviderCallback) context;
    }

    /**
     * Ott再生用コールバック.
     */
    public interface OttContentsBroadcastDataProviderCallback {
        /**
         * RealTimeRankingJsonParserCallback.onRealTimeRankingJsonParsedが終了したら、 コールバック.
         * @param contentsData  リアタイムランキングデータ
         * @param isLoadSuccess データ取得に成功したのか？true:成功　false:失敗
         */
        void onOttContentsBroadcastDataListCallBack(List<ContentsData> contentsData, boolean isLoadSuccess);

    }

    @Override
    public void onRealTimeRankingJsonParsed(final List<RealTimeRankingList> realTimeRankingLists, ErrorState jsonParseError) {
        DTVTLogger.start();
        if (realTimeRankingLists != null && realTimeRankingLists.size() > 0) {
            List<Map<String, String>> map = realTimeRankingLists.get(0).getRealTimeRankingList();
            setContentsData(setBroadcastContentData(map));
            getOttChannelList();
        } else {
            mOttContentsBroadcastDataProviderCallback.onOttContentsBroadcastDataListCallBack(null, false);
        }
        DTVTLogger.end();
    }

    @Override
    public void channelListCallback(final ArrayList<ChannelInfo> channels) {
        if (channels != null && !channels.isEmpty()) {
            mChannelList = channels;
            setContentsData(setChannelInfo(mContentsData));
            mOttContentsBroadcastDataProviderCallback.onOttContentsBroadcastDataListCallBack(mContentsData, true);
        } else {
            mOttContentsBroadcastDataProviderCallback.onOttContentsBroadcastDataListCallBack(null, false);
        }
    }

    @Override
    public void channelInfoCallback(final ChannelInfoList channelsInfo, final String[] serviceIdUniq) {
        //NOP
    }

    @Override
    public void clipKeyResult() {
        //NOP
    }

    @Override
    public void onContentsDetailInfoCallback(VodMetaFullData contentsDetailInfo, boolean clipStatus) {
        //NOP
    }

    @Override
    public void onRentalVodListCallback(final PurchasedVodListResponse response) {
        //NOP
    }

    @Override
    public void recordingReservationResult(RemoteRecordingReservationResultResponse response) {
        //NOP
    }

    @Override
    public void onRoleListCallback(ArrayList<RoleListMetaData> roleListInfo){
        //NOP
    }

    @Override
    public void onRentalChListCallback(final PurchasedChannelListResponse response) {
        DTVTLogger.start();
        if (response == null || response.getChannelListData() == null) {
            return;
        }
        for (int i = 0; i < mContentsData.size(); i++) {
            if (!ContentUtils.ViewIngType.PREMIUM_CHECK_START.equals(mContentsData.get(i).getViewIngType())) {
                continue;
            }
            ChannelInfo channelInfo = new ChannelInfo();
            channelInfo.setServiceIdUniq(mContentsData.get(i).getServiceIdUniq());
            channelInfo.setPurchaseId(mContentsData.get(i).getPurchaseId());
            channelInfo.setSubPurchaseId(mContentsData.get(i).getSubPurchaseId());
            channelInfo.setChannelPackPurchaseId(mContentsData.get(i).getChannelPackPurchaseId());
            channelInfo.setChannelPackSubPurchaseId(mContentsData.get(i).getChannelPackSubPurchaseId());
            long endDate = ContentUtils.getRentalChannelValidEndDate(response, channelInfo);
            ContentUtils.ViewIngType viewIngType = ContentUtils.getRentalChannelViewingTypeOfNowOnAir(mContentsData.get(i), endDate);
            mContentsData.get(i).setViewIngType(viewIngType);
        }
        mOttContentsBroadcastDataProviderCallback.onOttContentsBroadcastDataListCallBack(mContentsData, true);
        DTVTLogger.end();
    }

    /**
     * 放送番組データ設定.
     * @param contentsDataList 放送番組データ
     */
    public void setContentsData(final List<ContentsData> contentsDataList) {
        this.mContentsData = contentsDataList;
    }

    /**
     * リアルタイム再生数上位番組取得.
     */
    public void getRealTimeRankingData() {
        DTVTLogger.start();
        UserInfoDataProvider userInfoDataProvider = new UserInfoDataProvider(mContext);
        int ageReq = userInfoDataProvider.getUserAge();
        //通信クラスにデータ取得要求を出す
        mRealTimeRankingWebClient = new RealTimeRankingWebClient(mContext);
        mRealTimeRankingWebClient.getRealTimeRankingApi(0,
                JsonConstants.REAL_TIME_RANKING_DISPLAY_TYPE[REAL_TIME_RANKING_DISPLAY_TYPE],
                ageReq, WebApiBasePlala.FILTER_RELEASE, this);
        DTVTLogger.end();
    }

    /**
     * 全OTTチャンネルを取得する.
     */
    private void getOttChannelList() {
        if (mScaledDownProgramListDataProvider == null) {
            mScaledDownProgramListDataProvider = new ScaledDownProgramListDataProvider(mContext, this);
        }
        mScaledDownProgramListDataProvider.setAreaCode( UserInfoUtils.getAreaCode(mContext));
        mScaledDownProgramListDataProvider.getChannelList(0, 0, "", JsonConstants.CH_SERVICE_TYPE_INDEX_OTT);
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
            contentInfo.setAvailStartDate( DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_AVAIL_START_DATE)));
            contentInfo.setAvailEndDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_AVAIL_END_DATE)));
            contentInfo.setVodStartDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_VOD_START_DATE)));
            contentInfo.setVodEndDate(DateUtils.getSecondEpochTime(mapList.get(i).get(JsonConstants.META_RESPONSE_VOD_END_DATE)));
            contentsDataList.add(contentInfo);
        }
        return contentsDataList;
    }

    /**
     * チャンネル情報紐づけ.
     * @param contentsData 放送番組データ(チャンネル情報なし)
     * @return 放送番組データ(チャンネル情報あり)
     */
    private List<ContentsData> setChannelInfo(final List<ContentsData> contentsData) {
        if (contentsData == null) {
            return null;
        }
        List<ContentsData> contentDataList = new ArrayList<>();
        if (mChannelList == null || mChannelList.size() <= 0) {
            return contentDataList;
        }
        boolean isNeedGetRentalChannel = false;

        for (int i = 0; i < contentsData.size(); i++) {
            String serviceIdUniq = contentsData.get(i).getServiceIdUniq();
            if (!TextUtils.isEmpty(serviceIdUniq)) {
                for (int j = 0; j < mChannelList.size(); j++) {
                    if (serviceIdUniq.equals(mChannelList.get(j).getServiceIdUniq())) {
                        contentsData.get(i).setChannelName(mChannelList.get(j).getTitle());
                        contentsData.get(i).setChannelType(mChannelList.get(j).getChannelType());
                        ContentUtils.ViewIngType viewIngType = ContentUtils.getViewIngTypeOfNowOnAir(contentsData.get(i));
                        contentsData.get(i).setViewIngType(viewIngType);
                        contentsData.get(i).setPurchaseId(mChannelList.get(j).getPurchaseId());
                        contentsData.get(i).setSubPurchaseId(mChannelList.get(j).getSubPurchaseId());
                        contentsData.get(i).setChannelPackPurchaseId(mChannelList.get(j).getChannelPackPurchaseId());
                        contentsData.get(i).setChannelPackSubPurchaseId(mChannelList.get(j).getChannelPackSubPurchaseId());
                        contentsData.get(i).setChannelThumListURL(mChannelList.get(j).getThumbnail());
                        if (ContentUtils.ViewIngType.PREMIUM_CHECK_START.equals(viewIngType)) {
                            isNeedGetRentalChannel = true;
                        }
                        if (TextUtils.isEmpty(contentsData.get(i).getThumURL())) {
                            contentsData.get(i).setThumURL(mChannelList.get(j).getThumbnail());
                        }
                        contentDataList.add(contentsData.get(i));
                        break;
                    }
                }
            }
        }
        if (isNeedGetRentalChannel) {
            if (mContentsDetailDataProvider == null) {
                mContentsDetailDataProvider = new ContentsDetailDataProvider(mContext, this);
            }
            mContentsDetailDataProvider.getChListData();
        }
        return contentDataList;
    }

    /**
     * 通信を止める.
     */
    public void stopConnect() {
        DTVTLogger.start();
        if (mRealTimeRankingWebClient != null) {
            mRealTimeRankingWebClient.stopConnection();
        }
        if (mScaledDownProgramListDataProvider != null) {
            mScaledDownProgramListDataProvider.stopConnect();
        }
        if (mContentsDetailDataProvider != null) {
            mContentsDetailDataProvider.stopConnect();
        }
    }

    /**
     * 通信を許可する.
     */
    public void enableConnect() {
        DTVTLogger.start();
        if (mRealTimeRankingWebClient != null) {
            mRealTimeRankingWebClient.enableConnection();
        }
        if (mScaledDownProgramListDataProvider != null) {
            mScaledDownProgramListDataProvider.enableConnect();
        }
        if (mContentsDetailDataProvider != null) {
            mContentsDetailDataProvider.enableConnect();
        }
    }
}
