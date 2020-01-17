package com.example.detaildemo.data.provider;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.ClipKeyListResponse;
import com.example.detaildemo.data.bean.ClipRequestData;
import com.example.detaildemo.data.bean.ScheduleInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfoList;
import com.example.detaildemo.data.bean.channel.ChannelList;
import com.example.detaildemo.data.db.DataBaseConstants;
import com.example.detaildemo.data.manager.ChannelInsertDataManager;
import com.example.detaildemo.data.manager.ClipKeyListDataManager;
import com.example.detaildemo.data.manager.DataBaseThread;
import com.example.detaildemo.data.manager.ProgramDataManager;
import com.example.detaildemo.data.manager.TvScheduleInsertDataManager;
import com.example.detaildemo.data.webapiclient.client.ChannelWebClient;
import com.example.detaildemo.data.webapiclient.client.TvScheduleWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.data.TvScheduleList;
import com.example.detaildemo.service.TvProgramIntentService;
import com.example.detaildemo.utils.ClipUtils;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DataBaseUtils;
import com.example.detaildemo.utils.DataConverter;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.NetWorkUtils;
import com.example.detaildemo.utils.ServiceUtils;
import com.example.detaildemo.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 縮小番組表用データプロバイダークラス
 * 機能：　縮小番組表画面で使うデータを提供するクラスである.
 */
public class ScaledDownProgramListDataProvider extends ClipKeyListDataProvider implements
        ChannelWebClient.ChannelJsonParserCallback,
        TvScheduleWebClient.TvScheduleJsonParserCallback {
    /** データ取得結果コールバック. */
    private ApiDataProviderCallback mApiDataProviderCallback;
    /** コンテキスト. */
    private Context mContext;
    /** 番組データ. */
    private TvScheduleList mTvScheduleList = null;
    /** チャンネル番号で仕分けした番組データ. */
    private ChannelInfoList mChannelsInfoList = null;
    /** チャンネルタイプ(dチャンネル、ひかりTV多ch、全て). */
    private int mChannelServiceType = JsonConstants.CH_SERVICE_TYPE_INDEX_ALL;
    /** 取得要求日付. */
    private String mProgramSelectDate = null;
    //共通スレッド使う
    /** チャンネル更新(親クラスのDbThreadで"0","1","2"を使用しているため使用しない). */
    private static final int CHANNEL_UPDATE = 5;
    /** 番組更新(親クラスのDbThreadで"0","1","2"を使用しているため使用しない). */
    private static final int SCHEDULE_UPDATE = 6;
    /** チャンネル検索. */
    private static final int CHANNEL_SELECT = 3;
    /** 番組検索. */
    private static final int SCHEDULE_SELECT = 4;
    /** CH毎番組取得のフィルター. */
    private static final String PROGRAM_LIST_CHANNEL_PROGRAM_FILTER_RELEASE = "release";
    /** 通信エラー時ウェイト処理. */
    private static final long SLEEP_TIME = 100L;
    /** tvコンテンツのクリップキーリスト取得済み判定. */
    private boolean mTvClipKeyListResponse = false;
    /** vodコンテンツのクリップキーリスト取得済み判定. */
    private boolean mVodClipKeyListResponse = false;
    /** 通信禁止判定フラグ. */
    private boolean mIsStop = false;
    /** 番組リスト取得中フラグ. */
    private boolean mIsTvScheduleRequest = false;
    /** チャンネルリスト取得WebClient. */
    private ChannelWebClient mChannelWebClient = null;
    /** 番組リスト取得WebClient. */
    private TvScheduleWebClient mTvScheduleWebClient = null;
    /** TvScheduleWebClientのキューリスト. */
    private LinkedList<TvScheduleWebClient> mTvScheduleWebClientLinkedList = null;
    /** チャンネルリスト用エラー情報バッファ. */
    private ErrorState mChannelError = null;
    /** 番組リスト用エラー情報バッファ. */
    private ErrorState mTvScheduleError = null;
    /** エリアコード. */
    private String mAreaCode = "";

    /**
     * コンストラクタ.
     *
     * @param mContext TvProgramListActivity
     */
    public ScaledDownProgramListDataProvider(final Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.mApiDataProviderCallback = (ApiDataProviderCallback) mContext;
        mTvScheduleWebClientLinkedList = new LinkedList<>();
    }

    /**
     * コンストラクタ.
     *
     * @param mContext コンテスト
     * @param mApiDataProviderCallback リスナー
     */
    public ScaledDownProgramListDataProvider(final Context mContext, final ApiDataProviderCallback mApiDataProviderCallback) {
        super(mContext);
        this.mContext = mContext;
        this.mApiDataProviderCallback = mApiDataProviderCallback;
        mTvScheduleWebClientLinkedList = new LinkedList<>();
    }

    /**
     * チャンネル情報取得エラーのクラスを返すゲッター.
     *
     * @return チャンネル情報取得エラーのクラス
     */
    public ErrorState getChannelError() {
        return mChannelError;
    }

    /**
     * 番組情報取得エラーのクラスを返すゲッター.
     *
     * @return 番組情報取得エラーのクラス
     */
    public ErrorState getmTvScheduleError() {
        return mTvScheduleError;
    }


    @SuppressWarnings("OverlyLongMethod")
    @Override
    public void onDbOperationFinished(final boolean isSuccessful,
                                      final List<Map<String, String>> resultSet,
                                      final int operationId) {
        if (isSuccessful) {
            switch (operationId) {
                case CHANNEL_SELECT:
                    ArrayList<ChannelInfo> channels = new ArrayList<>();
                    for (int i = 0; i < resultSet.size(); i++) {
                        ArrayList<ScheduleInfo> mScheduleList;
                        Map<String, String> map = resultSet.get(i);
                        String adult = map.get(JsonConstants.META_RESPONSE_ADULT);
                        String chNo = map.get(JsonConstants.META_RESPONSE_CHNO);
                        String remoconKey = map.get(JsonConstants.META_RESPONSE_REMOCON_KEY);
                        String title = map.get(JsonConstants.META_RESPONSE_TITLE);
                        String thumb = map.get(JsonConstants.META_RESPONSE_THUMB_448);
                        String serviceId = map.get(JsonConstants.META_RESPONSE_SERVICE_ID);
                        String serviceIdUniq = map.get(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ);
                        String rValue = map.get(JsonConstants.META_RESPONSE_R_VALUE);
                        String dispType = map.get(JsonConstants.META_RESPONSE_DISP_TYPE);
                        String searchOk = map.get(JsonConstants.META_RESPONSE_SEARCH_OK);
                        String dtv = map.get(JsonConstants.META_RESPONSE_DTV);
                        String dtvType = map.get(JsonConstants.META_RESPONSE_DTV_TYPE);
                        String startDate = map.get(JsonConstants.META_RESPONSE_AVAIL_START_DATE);
                        String endDate = map.get(JsonConstants.META_RESPONSE_AVAIL_END_DATE);
                        String service = map.get(JsonConstants.META_RESPONSE_SERVICE);
                        String flg4K = map.get( DataBaseConstants.UNDER_BAR_FOUR_K_FLG);

                        ScheduleInfo mSchedule = new ScheduleInfo();
                        mSchedule.setStartTime(startDate);
                        mSchedule.setEndTime(endDate);
                        mSchedule.setImageUrl(thumb);
                        mSchedule.setTitle(title);
                        mSchedule.setChNo(chNo);
                        mSchedule.setContentType(map.get(JsonConstants.META_RESPONSE_CONTENT_TYPE));
                        mSchedule.setDtv(dtv);
                        mSchedule.setRValue(rValue);
                        mSchedule.setDispType(dispType);
                        mSchedule.setClipExec( ClipUtils.isCanClip(dispType, searchOk, dtv, dtvType));
                        mSchedule.setClipRequestData(setClipData(map));
                        mSchedule.setContentsId(map.get(JsonConstants.META_RESPONSE_CRID));
                        mSchedule.setTvService(map.get(JsonConstants.META_RESPONSE_TV_SERVICE));
                        mSchedule.setEventId(map.get(JsonConstants.META_RESPONSE_EVENT_ID));
                        mSchedule.setServiceId(map.get(JsonConstants.META_RESPONSE_SERVICE_ID));
                        mSchedule.setServiceIdUniq(map.get(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ));
                        mSchedule.setTitleId(map.get(JsonConstants.META_RESPONSE_TITLE_ID));
                        mSchedule.setCrId(map.get(JsonConstants.META_RESPONSE_CRID));

                        if (!TextUtils.isEmpty(chNo)) {
                            ChannelInfo channel = new ChannelInfo();
                            channel.setAdult(adult);
                            channel.setChannelNo(Integer.parseInt(chNo));
                            channel.setRemoconKey(remoconKey);
                            channel.setTitle(title);
                            channel.setThumbnail(thumb);
                            channel.setServiceId(serviceId);
                            channel.setService(service);
                            channel.setServiceIdUniq(serviceIdUniq);
                            channel.set4KFlg(flg4K);
                            channel.setPurchaseId(map.get(JsonConstants.META_RESPONSE_PUID));
                            channel.setSubPurchaseId(map.get(JsonConstants.META_RESPONSE_SUB_PUID));
                            channel.setChannelPackPurchaseId(map.get( StringUtils.getConnectStrings(
                                    JsonConstants.META_RESPONSE_CHPACK, JsonConstants.UNDER_LINE, JsonConstants.META_RESPONSE_PUID)));
                            channel.setChannelPackSubPurchaseId(map.get(StringUtils.getConnectStrings(
                                    JsonConstants.META_RESPONSE_CHPACK, JsonConstants.UNDER_LINE, JsonConstants.META_RESPONSE_SUB_PUID)));
                            channel.setChannelType(map.get(JsonConstants.META_RESPONSE_CH_TYPE));
                            channel.setOttflg(map.get(JsonConstants.META_RESPONSE_OTT_FLG));
                            channel.setOttDrmflg(map.get(JsonConstants.META_RESPONSE_OTT_DRMFLG));
                            channel.setFullHd(map.get(JsonConstants.META_RESPONSE_FULL_HD));
                            channel.setExternalOutput(map.get(JsonConstants.META_RESPONSE_EXTERNAL_OUTPUT));
                            mScheduleList = new ArrayList<>();
                            mScheduleList.add(mSchedule);
//                            channel.setSchedules(mScheduleList);
                            channels.add(channel);
                        }
                    }
                    sendChannelInfoArray(channels);
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("OverlyLongMethod")
    @Override
    public List<Map<String, String>> dbOperation(final DataBaseThread dataBaseThread, final int operationId) {
        super.dbOperation(dataBaseThread, operationId);
        List<Map<String, String>> resultSet = null;
        switch (operationId) {
            case CHANNEL_UPDATE://サーバーから取得したチャンネルデータをDBに保存する
                ChannelInsertDataManager channelInsertDataManager = new ChannelInsertDataManager(mContext);
                channelInsertDataManager.insertChannelInsertList(dataBaseThread.getChannelList());
                break;
            case SCHEDULE_UPDATE://サーバーから取得した番組データをDBに保存する
                TvScheduleInsertDataManager scheduleInsertDataManager = new TvScheduleInsertDataManager(mContext);
                ChannelInfoList channelInfoList = dataBaseThread.getChannelsInfoList();
                scheduleInsertDataManager.insertTvScheduleInsertList(channelInfoList, mProgramSelectDate);
                //番組表の保存と番組表描画を並行して実行するとフリーズするため、DBへのInsertが終了してから描画を開始する
                sendChannelInfoList(channelInfoList, new String[0]);
                break;
            case CHANNEL_SELECT://DBからチャンネルデータを取得して、画面に返却する
                ProgramDataManager channelDataManager = new ProgramDataManager(mContext);
                resultSet = channelDataManager.selectChannelListProgramData(mChannelServiceType);
                if (resultSet == null || resultSet.size() == 0) {
                    getChannelListFromApi(0, 0, "");
                }
                break;
            case SCHEDULE_SELECT://DBから番組データを取得して、画面に返却する
                ProgramDataManager scheduleDataManager = new ProgramDataManager(mContext);
                //DBから取得した複数チャンネルの情報を格納するリスト
                List<List<Map<String, String>>> resultSetList =
                        scheduleDataManager.selectTvScheduleListProgramData(dataBaseThread.getFromDB(), mProgramSelectDate);
                resultSet = new ArrayList<>();
                // 番組データがある場合はダミーで1件の結果セットを返す
                if (resultSetList != null && resultSetList.size() > 0) {
                    resultSet.add(new HashMap<String, String>() {{ put("", ""); }});
                }
                //非同期なので、タブ切替時にこの処理に入ってしまわないようにNullチェックを追加
                if (resultSetList != null) {
                    ChannelInfoList channelsInfo = new ChannelInfoList();
                    ClipKeyListDataManager keyListDataManager = new ClipKeyListDataManager(mContext);
                    List<Map<String, String>> clipKeyList = keyListDataManager.selectClipAllList();
                    for (List<Map<String, String>> channelInfos : resultSetList) {
                        if (channelInfos != null && channelInfos.size() > 0) {
                            ArrayList<ScheduleInfo> scheduleInfoList = new ArrayList<>();
                            for (int i = 0; i < channelInfos.size(); i++) { //番組データ取得して整形する
                                HashMap<String, String> hashMap = (HashMap<String, String>) channelInfos.get(i);
                                ScheduleInfo mSchedule = DataConverter.convertScheduleInfo(hashMap, clipKeyList);
                                scheduleInfoList.add(mSchedule);
                            }
                            //setScheduleInfoのやり方を踏襲.
                            ChannelInfo channel = new ChannelInfo();
                            channel.setServiceIdUniq(scheduleInfoList.get(0).getServiceIdUniq());
                            channel.setTitle(scheduleInfoList.get(0).getTitle());
                            channel.setSchedules(scheduleInfoList);
                            channelsInfo.addChannel(channel);
                        }
                    }
                    if (channelsInfo.getChannels().size() == 0 && NetWorkUtils.isOnline(mContext)) {
                        List<String> serviceIdUniqs = dataBaseThread.getFromDB();
                        String[] fromWebAPIServiceIdUniqs = new String[serviceIdUniqs.size()];
                        for (int i = 0; i < serviceIdUniqs.size(); i++) {
                            fromWebAPIServiceIdUniqs[i] = serviceIdUniqs.get(i);
                        }
                        String[] date = {mProgramSelectDate};
                        getScheduleListFromApi(fromWebAPIServiceIdUniqs, date);
                    } else {
                        sendChannelInfoList(channelsInfo, new String[0]);
                    }
                } else {
                    sendChannelInfoList(null, new String[0]);
                }
                break;
            default:
                break;
        }
        return resultSet;
    }

    @Override
    public void onChannelJsonParsed(final List<ChannelList> channelLists, final ErrorState jsonParseError) {
        ArrayList<ChannelInfo> channels = null;
        if (channelLists != null && channelLists.size() > 0 && jsonParseError == null) {
            ChannelList mChannelList = channelLists.get(0);
            List<Map<String, String>> channelList = mChannelList.getChannelList();

            channels = new ArrayList<>();
            setChannelData(channels, channelList);
            Handler handler = new Handler(mContext.getMainLooper()); //チャンネル情報更新
            try {
                DataBaseThread dataBaseThread = new DataBaseThread(handler, this, CHANNEL_UPDATE);
                dataBaseThread.setChannelList(mChannelList);
                dataBaseThread.start();
            } catch (IllegalThreadStateException e) {
                DTVTLogger.debug(e);
                channels = null;
            }
        } else {
            //JSONパースエラー更新
            mChannelWebClient.setJsonParseError(jsonParseError);
            //データが取得できなかったので、エラーを取得する
            mChannelError = mChannelWebClient.getError();
        }

        //WebApi上は必ずひかり・dCH共に取得するが、呼び出し元によってはどちらか一方のみ用いるのでフィルタ.
        //せいぜい250チャンネル程度であれば高負荷にならないのでmain スレッドで回す.
        ArrayList<ChannelInfo> dstChannels = null;
        if (channels != null) {
            boolean is4KFlg = false;
            boolean isOttFlag = false;
            dstChannels = new ArrayList<>();
            String service = null;
            switch (mChannelServiceType) {
                case JsonConstants.CH_SERVICE_TYPE_INDEX_HIKARI:
                    service = ProgramDataManager.CH_SERVICE_HIKARI;
                    break;
                case JsonConstants.CH_SERVICE_TYPE_INDEX_OTT:
                    service = ProgramDataManager.CH_SERVICE_HIKARI;
                    isOttFlag = true;
                    break;
                case JsonConstants.CH_SERVICE_TYPE_INDEX_DCH:
                    service = ProgramDataManager.CH_SERVICE_DCH;
                    break;
                case JsonConstants.CH_SERVICE_TYPE_INDEX_TTB:
                    service = ProgramDataManager.CH_SERVICE_TTB;
                    break;
                case JsonConstants.CH_SERVICE_TYPE_INDEX_BS:
                    service = ProgramDataManager.CH_SERVICE_BS;
                    break;
                case JsonConstants.CH_SERVICE_TYPE_INDEX_BS4K:
                    service = ProgramDataManager.CH_SERVICE_BS;
                    is4KFlg = true;
                    break;
                default:
                    break;
            }
            if (service == null) {
                // どちらも
                dstChannels = channels;
            } else {
                for (int i = 0; i < channels.size(); i++) {
                    if (service.equals(channels.get(i).getService())) {
                        if (ProgramDataManager.CH_SERVICE_BS.equals(service)) {
                            if (is4KFlg) {
                                if (!ContentUtils.FLG_4K_ONE.equals(channels.get(i).get4KFlg())) {
                                    continue;
                                }
                            } else {
                                if (ContentUtils.FLG_4K_ONE.equals(channels.get(i).get4KFlg())) {
                                    continue;
                                }
                            }
                        }  else if (ProgramDataManager.CH_SERVICE_HIKARI.equals(service)) {
                            if (isOttFlag) {
                                if (!ContentUtils.FLG_OTT_ONE.equals(channels.get(i).getOttflg())) {
                                    continue;
                                }
                            } else {
                                if (ContentUtils.FLG_OTT_ONE.equals(channels.get(i).getOttflg())) {
                                    continue;
                                }
                            }
                        }
                        dstChannels.add(channels.get(i));
                    }
                }
            }
        }
        sendChannelInfoArray(dstChannels);
    }

    @Override
    public void onTvScheduleJsonParsed(final List<TvScheduleList> tvScheduleList, final String[] serviceIdUniqs) {
        //WebClientキューが残っていたら実行する
        mIsTvScheduleRequest = false;
        pollTvScheduleWebClient();
        if (tvScheduleList != null) {
            DTVTLogger.start("onTvScheduleJsonParsed tvScheduleList size:" + tvScheduleList.size());
            //チャンネルデータ
            mTvScheduleList = tvScheduleList.get(0);

            if (mRequiredClipKeyList) {
                // クリップキーリストを取得
                mTvClipKeyListResponse = false;
                mVodClipKeyListResponse = false;
                getClipKeyList();
            } else {
                TvScheduleList scheduleList = mTvScheduleList;
                //扱いやすくするために一旦ArrayListに変換
                ArrayList<String> serviceIdUniqList = new ArrayList<>();
                Collections.addAll(serviceIdUniqList, serviceIdUniqs);
                List<Map<String, String>> mapList = scheduleList.geTvsList();

                //番組データが存在するチャンネル番号を削除
                for (int i = 0; i < mapList.size(); i++) {
                    serviceIdUniqList.remove(mapList.get(i).get(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ));
                }

                //番組データがないチャンネル用のダミーデータを設定する
                for (int i = 0; i < serviceIdUniqList.size(); i++) {
                    mapList.add(DataConverter.getDummyContentMap(mContext, serviceIdUniqList.get(i), false));
                }
                scheduleList.setTvsList(mapList);
                mTvScheduleList = scheduleList;
                if (null != mApiDataProviderCallback) {
                    setProgramListContentData(scheduleList);
                }
            }
        } else {
            DTVTLogger.start("onTvScheduleJsonParsed tvScheduleList size: null");
            //データが取得できなかったので、エラーを取得する
            if (mTvScheduleWebClient != null) {
                mTvScheduleError = mTvScheduleWebClient.getError();
            }

            //Data取得時に、DBから取得するチャンネル番号とWebAPIから取得するチャンネル番号を分けて
            //データを取っているため、ここで改めてDBからデータを取得は行わない.
            sendChannelInfoList(null, serviceIdUniqs);
        }
    }

    @Override
    public void onTvClipKeyResult(final ClipKeyListResponse clipKeyListResponse, final ErrorState errorState) {
        mTvScheduleError = errorState;
        super.onTvClipKeyResult(clipKeyListResponse, errorState);
        if (mVodClipKeyListResponse) {
            sendClipKeyResult();
            setProgramListContentData(mTvScheduleList);
        } else {
            mTvClipKeyListResponse = true;
        }
    }

    @Override
    public void onVodClipKeyResult(final ClipKeyListResponse clipKeyListResponse, final ErrorState errorState) {
        super.onVodClipKeyResult(clipKeyListResponse, errorState);
        if (mTvClipKeyListResponse) {
            sendClipKeyResult();
            setProgramListContentData(mTvScheduleList);
        } else {
            mVodClipKeyListResponse = true;
        }
    }

    /**
     * 番組表をチャンネルに入れる.
     *
     * @param hashMap 番組情報
     * @param channelsInfo チャンネル情報
     * @param clipKeyList クリップキーリスト
     */
    private void setScheduleInfo(final Map<String, String> hashMap, final ChannelInfoList channelsInfo, final List<Map<String, String>> clipKeyList) {
        ScheduleInfo mSchedule = DataConverter.convertScheduleInfo(hashMap, clipKeyList);
        String serviceIdUniq = mSchedule.getServiceIdUniq();
        if (!TextUtils.isEmpty(serviceIdUniq)) { //CH毎番組データ取得して、整形する
            List<ChannelInfo> oldChannelList = channelsInfo.getChannels();
            boolean isExist = false;
            if (oldChannelList.size() > 0) { //番組ID存在するのをチェックする
                for (int j = 0; j < oldChannelList.size(); j++) {
                    ChannelInfo oldChannel = oldChannelList.get(j);
                    if (serviceIdUniq.equals(oldChannel.getServiceIdUniq())) { //番組ID存在する場合
                        ArrayList<ScheduleInfo> oldSchedule = oldChannel.getSchedules();
                        oldSchedule.add(mSchedule);
                        isExist = true;
                        break;
                    }
                }
            }
            if (!isExist) { //番組ID存在しない場合
                ArrayList<ScheduleInfo> mScheduleList = new ArrayList<>();
                mScheduleList.add(mSchedule);
                ChannelInfo channel = new ChannelInfo();
                channel.setServiceIdUniq(serviceIdUniq);
                if (DataBaseUtils.isNumber(mSchedule.getChNo())) {
                    channel.setChannelNo(Integer.parseInt(mSchedule.getChNo()));
                }
//                channel.setTitle(mSchedule.getTitle());
                channel.setSchedules(mScheduleList);
                channelsInfo.addChannel(channel);
            }
        }
    }

    /**
     * 番組表用に番組のメタデータを整形.
     *
     * @param scheduleList 番組情報
     * @return 番組情報
     */
    private ChannelInfoList setProgramListContentData(final TvScheduleList scheduleList) {
        ChannelInfoList channelsInfo = null;

        if (scheduleList == null) {
            //解放処理の強化によりヌルの場合が発生したので、ヌルならば帰る
            sendChannelInfoList(null, new String[0]);
            return null;
        }

        List<Map<String, String>> channelProgramList = scheduleList.geTvsList();
        if (channelProgramList != null) {
            channelsInfo = new ChannelInfoList();
            ClipKeyListDataManager keyListDataManager = new ClipKeyListDataManager(mContext);
            List<Map<String, String>> clipKeyList = keyListDataManager.selectClipAllList();
            for (int i = 0; i < channelProgramList.size(); i++) {
                //CH毎番組データ取得して、整形する
                Map<String, String> hashMap = channelProgramList.get(i);
                setScheduleInfo(hashMap, channelsInfo, clipKeyList);
            }
            mChannelsInfoList = channelsInfo;
            Handler handler = new Handler(mContext.getMainLooper());
            //番組情報更新
            try {
                DataBaseThread t = new DataBaseThread(handler, this, SCHEDULE_UPDATE);
                t.setChannelsInfoList(channelsInfo);
                t.start();
            } catch (IllegalThreadStateException e) {
                DTVTLogger.debug(e);
            }
        } else {
            sendChannelInfoList(null, new String[0]);
        }
        return channelsInfo;
    }

    /**
     * クリップリクエストに必要なデータを作成する(番組表用).
     *
     * @param map 番組表データ
     * @return Clipリクエストに必要なデータ
     */
    public static ClipRequestData setClipData(final Map<String, String> map) {
        String dispType = map.get(JsonConstants.META_RESPONSE_DISP_TYPE);
        String contentsType = map.get(JsonConstants.META_RESPONSE_CONTENT_TYPE);
        String tvService = map.get(JsonConstants.META_RESPONSE_TV_SERVICE);
        ClipRequestData requestData = new ClipRequestData();
        requestData.setCrid(map.get(JsonConstants.META_RESPONSE_CRID));
        requestData.setServiceId(map.get(JsonConstants.META_RESPONSE_SERVICE_ID));
        requestData.setEventId(map.get(JsonConstants.META_RESPONSE_EVENT_ID));
        requestData.setTitleId(map.get(JsonConstants.META_RESPONSE_TITLE_ID));
        requestData.setTitle(map.get(JsonConstants.META_RESPONSE_TITLE));
        requestData.setRValue(map.get(JsonConstants.META_RESPONSE_R_VALUE));
        requestData.setLinearStartDate(String.valueOf( DateUtils.getSecondEpochTime(map.get(JsonConstants.META_RESPONSE_PUBLISH_START_DATE))));
        requestData.setLinearEndDate(String.valueOf(DateUtils.getSecondEpochTime(map.get(JsonConstants.META_RESPONSE_PUBLISH_END_DATE))));
        requestData.setSearchOk(map.get(JsonConstants.META_RESPONSE_SEARCH_OK));
        String bs4kFlg = !TextUtils.isEmpty(map.get(JsonConstants.META_RESPONSE_4KFLG)) ? map.get(JsonConstants.META_RESPONSE_4KFLG) : map.get(DataBaseConstants.UNDER_BAR_FOUR_K_FLG);
        requestData.setIsNotify(dispType, contentsType,
                DateUtils.getSecondEpochTime(map.get(JsonConstants.META_RESPONSE_VOD_START_DATE)),
                tvService, map.get(JsonConstants.META_RESPONSE_DTV), bs4kFlg);
        requestData.setDispType(dispType);
        requestData.setContentType(contentsType);
        requestData.setTvService(tvService);
        long vodStartDate = DateUtils.getSecondEpochTime(map.get(JsonConstants.META_RESPONSE_VOD_START_DATE));
        requestData.setVodStartDate(vodStartDate);
        return requestData;
    }

    /**
     * チャンネルデータの整形.
     *
     * @param channels    channels
     * @param channelList channelList
     */
    private void setChannelData(final ArrayList<ChannelInfo> channels,
                                final List<Map<String, String>> channelList) {
        for (int i = 0; i < channelList.size(); i++) {
            Map<String, String> hashMap = channelList.get(i);
            String adult =  hashMap.get(JsonConstants.META_RESPONSE_ADULT);
            String chNo = hashMap.get(JsonConstants.META_RESPONSE_CHNO);
            String remoconKey = hashMap.get(JsonConstants.META_RESPONSE_REMOCON_KEY);
            String title = hashMap.get(JsonConstants.META_RESPONSE_TITLE);
            String thumbnail = hashMap.get(JsonConstants.META_RESPONSE_THUMB_448);
            String serviceId = hashMap.get(JsonConstants.META_RESPONSE_SERVICE_ID);
            String serviceIdUniq = hashMap.get(JsonConstants.META_RESPONSE_SERVICE_ID_UNIQ);
            String chType = hashMap.get(JsonConstants.META_RESPONSE_CH_TYPE);
            String puId = hashMap.get(JsonConstants.META_RESPONSE_PUID);
            String subPuId = hashMap.get(JsonConstants.META_RESPONSE_SUB_PUID);
            String service = hashMap.get(JsonConstants.META_RESPONSE_SERVICE);
            String flg4k = hashMap.get(JsonConstants.META_RESPONSE_4KFLG);
            String ottFlg = hashMap.get(JsonConstants.META_RESPONSE_OTT_FLG);
            String ottDrmflg = hashMap.get(JsonConstants.META_RESPONSE_OTT_DRMFLG);
            String fullHd = hashMap.get(JsonConstants.META_RESPONSE_FULL_HD);
            String externalOutput = hashMap.get(JsonConstants.META_RESPONSE_EXTERNAL_OUTPUT);
            String chPackPuId = hashMap.get(JsonConstants.META_RESPONSE_CHPACK
                    + JsonConstants.UNDER_LINE + JsonConstants.META_RESPONSE_PUID);
            String chPackSubPuId = hashMap.get(JsonConstants.META_RESPONSE_CHPACK
                    + JsonConstants.UNDER_LINE + JsonConstants.META_RESPONSE_SUB_PUID);
            if (!TextUtils.isEmpty(chNo)) {
                ChannelInfo channel = new ChannelInfo();
                channel.setAdult(adult);
                channel.setTitle(title);
                channel.setChannelNo(Integer.parseInt(chNo));
                channel.setRemoconKey(remoconKey);
                channel.setThumbnail(thumbnail);
                channel.setServiceId(serviceId);
                channel.setServiceIdUniq(serviceIdUniq);
                channel.setChannelType(chType);
                channel.setPurchaseId(puId);
                channel.setSubPurchaseId(subPuId);
                channel.setChannelPackPurchaseId(chPackPuId);
                channel.setChannelPackSubPurchaseId(chPackSubPuId);
                channel.setService(service);
                channel.set4KFlg(flg4k);
                channel.setOttflg(ottFlg);
                channel.setFullHd(fullHd);
                channel.setOttDrmflg(ottDrmflg);
                channel.setExternalOutput(externalOutput);
                channels.add(channel);
            }
        }
    }

    /**
     * 後始末を行い、ガベージコレクションされやすくする.
     */
    public void clearData() {
        if (mTvScheduleList != null) {
            mTvScheduleList.clearData();
            mTvScheduleList = null;
        }

        if (mChannelsInfoList != null) {
            mChannelsInfoList.clearData();
            mChannelsInfoList = null;
        }
        if (mChannelWebClient != null) {
            mChannelWebClient = null;
        }
        if (mTvScheduleWebClient != null) {
            mTvScheduleWebClient = null;
        }
    }

    /**
     * 画面用データを返却するためのコールバック.
     */
    public interface ApiDataProviderCallback {

        /**
         * 縮小番組表多チャンネル情報を戻すコールバック.
         *
         * @param channelsInfo 画面に渡すチャンネル番組情報
         * @param serviceIdUniq サービスユニーク
         */
        void channelInfoCallback(ChannelInfoList channelsInfo, String[] serviceIdUniq);

        /**
         * チャンネルリストを戻す.
         *
         * @param channels 　画面に渡すチャンネル情報
         */
        void channelListCallback(ArrayList<ChannelInfo> channels);

        /**
         * クリップキー取得終了callback.
         */
        void clipKeyResult();
    }

    /**
     * エリアコード設定.
     * @param areaCode  エリアコード
     */
    public void setAreaCode(final String areaCode) {
        this.mAreaCode = areaCode;
    }

    /**
     * CH一覧取得.
     *
     * @param limit  レスポンスの最大件数
     * @param offset 取得位置(1～)
     * @param filter release、testa、demo ※指定なしの場合release
     * @param type   dch：dチャンネル, hikaritv：ひかりTVの多ch, 指定なしの場合：すべて
     */
    public void getChannelList(final int limit, final int offset, final String filter, final int type) {
        mChannelServiceType = type;

        DateUtils dateUtils = new DateUtils(mContext);
        String lastDate = dateUtils.getLastDate(DateUtils.CHANNEL_LAST_UPDATE);
//        if ((TextUtils.isEmpty(lastDate) || dateUtils.isBeforeProgramLimitDate(lastDate)) && NetWorkUtils.isOnline(mContext)) {
        if (false) {
            if (!mIsStop) {
                getChannelListFromApi(limit, offset, filter);
            } else {
                DTVTLogger.error("ScaledDownProgramListDataProvider is stopping connect");
            }
        } else {
            //データをDBから取得する
            Handler handler = new Handler(mContext.getMainLooper());
            //チャンネル情報更新
            try {
                DataBaseThread dataBaseThread = new DataBaseThread(handler, this, CHANNEL_SELECT);
                dataBaseThread.start();
            } catch (IllegalThreadStateException e) {
                DTVTLogger.debug(e);
            }
        }
    }

    /**
     * CH一覧取得.
     *
     * @param limit  レスポンスの最大件数
     * @param offset 取得位置(1～)
     * @param filter release、testa、demo ※指定なしの場合release
     */
    private void getChannelListFromApi(final int limit, final int offset, final String filter) {
        mChannelWebClient = new ChannelWebClient(mContext);
        mChannelWebClient.getChannelApi(limit, offset, filter, "", mAreaCode, this);
    }

    /**
     * 番組データ一覧取得.
     *
     * @param serviceIdUniqs  配列, 中身はString値
     * @param dateList 配列, 中身は整数値 YYYYMMDD
     */
    private void getScheduleListFromApi(final String[] serviceIdUniqs, final String[] dateList) {
        TvScheduleWebClient tvScheduleWebClient = new TvScheduleWebClient(mContext);
        tvScheduleWebClient.setChannelNoList(serviceIdUniqs, dateList, PROGRAM_LIST_CHANNEL_PROGRAM_FILTER_RELEASE);
        mTvScheduleWebClientLinkedList.offer(tvScheduleWebClient);
        pollTvScheduleWebClient();
    }

    /**
     * 通信/DBで、チャンネル毎番組取得.
     *
     * @param serviceIdUniq   配列, 中身はString値
     * @param dateList 配列, 中身は整数値 YYYYMMDD
     */
    public void getProgram(final String[] serviceIdUniq, final String[] dateList) {
        DateUtils dateUtils = new DateUtils(mContext);
        //dateListのサイズは1.
        mProgramSelectDate = dateList[0];
        //前回のデータ取得日時を取得
        String[] lastDate = dateUtils.getChLastDate(serviceIdUniq, mProgramSelectDate);
        //DBから取得するチャンネル情報とWebAPiから取得するチャンネル番号を分ける.
        List<String> fromWebAPI = new ArrayList<>();
        List<String> fromDB = new ArrayList<>();

        for (int i = 0; i < lastDate.length; i++) {
            if (dateUtils.isBeforeLimitChDate(lastDate[i])) {
                fromWebAPI.add(serviceIdUniq[i]);
            } else {
                fromDB.add(serviceIdUniq[i]);
            }
        }

        //データをDBから取得する
        if (fromDB.size() > 0) {
            Handler handler = new Handler(mContext.getMainLooper());
            //チャンネル情報更新
            try {
                DataBaseThread t = new DataBaseThread(handler, this, SCHEDULE_SELECT);
                t.setFromDB(fromDB);
                t.start();
            } catch (IllegalThreadStateException e) {
                DTVTLogger.debug(e);
            }
        }

        //データをWebAPIから取得する
        if (!mIsStop) {
            if (fromWebAPI.size() > 0) {
                String[] serviceIdUniqs = new String[fromWebAPI.size()];
                for (int i = 0; i < fromWebAPI.size(); i++) {
                    serviceIdUniqs[i] = fromWebAPI.get(i);
                }
                if (NetWorkUtils.isOnline(mContext)) {
                    //キューに仕込む分はローカル変数を作成する(中断処理が別系統となるため)
                    getScheduleListFromApi(serviceIdUniqs, dateList);
                } else {
                    sendChannelInfoList(null, serviceIdUniqs);
                }
            }
        } else {
            DTVTLogger.error("ScaledDownProgramListDataProvider is stopping connect");
        }
    }

    /**
     * キューに入れたWebClientを実行する.
     */
    private void pollTvScheduleWebClient() {
        if (mTvScheduleWebClientLinkedList != null && mTvScheduleWebClientLinkedList.size() > 0 && !mIsTvScheduleRequest) {
            mIsTvScheduleRequest = true;
            mTvScheduleWebClientLinkedList.poll().getTvScheduleApi(this, mAreaCode);
            DTVTLogger.debug("mTvScheduleWebClientLinkedList size = " + mTvScheduleWebClientLinkedList.size());
        }
    }

    /**
     * TvProgramIntentServiceを開始する.
     */
    public void startTvProgramIntentService() {
        //TvProgramIntentServiceが実行していない場合のみ開始する
        if (!ServiceUtils.isRunningService(mContext, TvProgramIntentService.class.getName())) {
            TvProgramIntentService.startTvProgramService(mContext);
        }
    }

    /**
     * TvProgramIntentServiceを終了する.
     */
    public void stopTvProgramIntentService() {
        //TvProgramIntentServiceが実行している場合のみ終了する
        if (ServiceUtils.isRunningService(mContext, TvProgramIntentService.class.getName())) {
            Intent intent = new Intent(mContext, TvProgramIntentService.class);
            mContext.stopService(intent);
        }
    }

    /**
     * 通信を止める.
     */
    public void stopConnect() {
        DTVTLogger.start();
        mIsStop = true;
        if (mChannelWebClient != null) {
            mChannelWebClient.stopConnection();
        }
        if (mTvScheduleWebClient != null) {
            mTvScheduleWebClient.stopConnection();
        }
        if (mTvScheduleWebClientLinkedList != null) {
            for (int i = 0; i < mTvScheduleWebClientLinkedList.size(); i++) {
                mTvScheduleWebClientLinkedList.poll().stopConnection();
            }
        }
    }

    /**
     * 通信を許可する.
     */
    public void enableConnect() {
        DTVTLogger.start();
        mIsStop = false;
        //通信再開時は番組リスト取得中フラグを初期化する
        mIsTvScheduleRequest = false;
        if (mChannelWebClient != null) {
            mChannelWebClient.enableConnection();
        }
        if (mTvScheduleWebClient != null) {
            mTvScheduleWebClient.enableConnection();
        }
        if (mTvScheduleWebClientLinkedList != null) {
            for (int i = 0; i < mTvScheduleWebClientLinkedList.size(); i++) {
                mTvScheduleWebClientLinkedList.poll().enableConnection();
            }
        }
    }

    /**
     * 複数チャンネルクラス送信.
     *
     * @param channelsInfo 複数チャンネルクラス
     * @param serviceIdUniq サービスユニーク
     */
    private void sendChannelInfoList(final ChannelInfoList channelsInfo, final String[] serviceIdUniq) {
        DTVTLogger.start();
        if (null != mApiDataProviderCallback && !mIsStop) {
            DTVTLogger.debug("null != mApiDataProviderCallback");
            mApiDataProviderCallback.channelInfoCallback(channelsInfo, serviceIdUniq);
        }
        DTVTLogger.end();
    }

    /**
     * チャンネルクラス送信.
     * @param channels チャンネルクラス
     */
    private void sendChannelInfoArray(final ArrayList<ChannelInfo> channels) {
        DTVTLogger.start();
        if (null != mApiDataProviderCallback && !mIsStop) {
            DTVTLogger.debug("null != mApiDataProviderCallback");

            if (channels == null) {
                //channelsがヌルの場合は通信エラーになっている。実際の通信の前に通信エラーとなった場合、アクティビティ側の初期処理が終了するよりも早く
                //結果が返る場合があり、動作に不整合が発生する場合があった。そこで、ウェイト時間を設ける事で対処する。アクティビティ側での対処では、
                //例外処理が膨れ上がった上に信頼性も低かったので、これが最もシンプルな解決方法となる。
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    //割り込みがあっても特に何も行わない
                }
            }

            mApiDataProviderCallback.channelListCallback(channels);
        }
        DTVTLogger.end();
    }

    /**
     * クリップキー取得終了イベント送信.
     */
    private void sendClipKeyResult() {
        DTVTLogger.start();
        if (null != mApiDataProviderCallback) {
            DTVTLogger.debug("get clip key result");
            mApiDataProviderCallback.clipKeyResult();
        }
        DTVTLogger.end();
    }

    /**
     * callbackキャンセル用.
     *
     * @param providerCallback callback(nullを設定)
     */
    public void setApiDataProviderCallback(final ApiDataProviderCallback providerCallback) {
        this.mApiDataProviderCallback = providerCallback;
    }
}