package com.example.detaildemo.data.provider;


import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.example.detaildemo.R;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.common.JsonConstants;
import com.example.detaildemo.data.bean.ClipKeyListRequest;
import com.example.detaildemo.data.bean.ClipKeyListResponse;
import com.example.detaildemo.data.bean.VodMetaFullData;
import com.example.detaildemo.data.bean.channel.ChannelList;
import com.example.detaildemo.data.manager.ClipKeyListDataManager;
import com.example.detaildemo.data.manager.DataBaseThread;
import com.example.detaildemo.data.manager.HomeDataManager;
import com.example.detaildemo.data.manager.ProgramDataManager;
import com.example.detaildemo.data.manager.RentalListDataManager;
import com.example.detaildemo.data.manager.RentalListInsertDataManager;
import com.example.detaildemo.data.manager.RoleListInsertDataManager;
import com.example.detaildemo.data.webapiclient.client.ContentsDetailGetWebClient;
import com.example.detaildemo.data.webapiclient.client.RemoteRecordingReservationWebClient;
import com.example.detaildemo.data.webapiclient.client.RentalChListWebClient;
import com.example.detaildemo.data.webapiclient.client.RentalVodListWebClient;
import com.example.detaildemo.data.webapiclient.client.RoleListWebClient;
import com.example.detaildemo.data.webapiclient.jsonparser.data.ActiveData;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RecordingReservationContentsDetailInfo;
import com.example.detaildemo.data.webapiclient.jsonparser.data.RoleListMetaData;
import com.example.detaildemo.data.webapiclient.jsonparser.response.ContentsDetailGetResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedChannelListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.PurchasedVodListResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.RemoteRecordingReservationResultResponse;
import com.example.detaildemo.data.webapiclient.jsonparser.response.RoleListResponse;
import com.example.detaildemo.utils.ClipUtils;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * コンテンツ詳細画面のDataProvider.
 */
public class ContentsDetailDataProvider extends ClipKeyListDataProvider implements
        ContentsDetailGetWebClient.ContentsDetailJsonParserCallback,
        RoleListWebClient.RoleListJsonParserCallback,
        DataBaseThread.DataBaseOperation,
        RemoteRecordingReservationWebClient.RemoteRecordingReservationJsonParserCallback,
        RentalVodListWebClient.RentalVodListJsonParserCallback,
        RentalChListWebClient.RentalChListJsonParserCallback {

    /**declaration.*/
    public enum ErrorType {
        /**コンテンツ詳細取得.*/
        contentsDetailGet,
        /**ロール一覧取得.*/
        roleList,
        /**レンタルチャンネル一覧.*/
        rentalChList,
        /**レンタルVod一覧.*/
        rentalVodList,
    }

    // callback
    /**
     * 画面用データを返却するためのコールバック.
     */
    public interface ApiDataProviderCallback {
        /**
         * コンテンツ情報取得.
         *
         * @param contentsDetailInfo 画面に渡すチャンネル番組情報
         * @param clipStatus クリープステータス
         */
        void onContentsDetailInfoCallback(VodMetaFullData contentsDetailInfo, boolean clipStatus);

        /**
         * ロールリスト情報取得.
         *
         * @param roleListInfo 画面に渡すチャンネルロールリスト情報
         */
        void onRoleListCallback(ArrayList<RoleListMetaData> roleListInfo);

        /**
         * リモート録画予約実行結果を返す.
         *
         * @param response 実行結果
         */
        void recordingReservationResult(RemoteRecordingReservationResultResponse response);

        /**
         * 購入済みVOD一覧を返す.
         *
         * @param response 購入済みVOD一覧
         */
        void onRentalVodListCallback(PurchasedVodListResponse response);

        /**
         * 購入済みCH一覧を返す.
         *
         * @param response 購入済みCH一覧
         */
        void onRentalChListCallback(final PurchasedChannelListResponse response);
    }

    /** コンテンツ詳細情報取得中. */
    private boolean mIsInContentsDetailRequest = false;
    /** ロールリスト取得中. */
    private boolean mIsInRoleListRequest = false;
    /** レンタルVodリスト取得中. */
    private boolean mIsInRentalVodListRequest = false;
    /** レンタルChリスト取得中. */
    private boolean mIsInRentalChListRequest = false;
    // region variable
    /** ApiDataProviderCallbackのインスタンス. */
    private ApiDataProviderCallback mApiDataProviderCallback = null;
    /** コンテキスト. */
    private Context mContext = null;
    /** 購入済みVODリスト情報を保持. */
    private PurchasedVodListResponse mPurchasedVodListResponse = null;
    /** 購入済みチャンネルリスト情報を保持. */
    private PurchasedChannelListResponse mPurchasedChannelListResponse = null;
    /** 購入済みチャンネルのactive_listの情報を保持. */
    private List<Map<String, String>> mPurchasedChActiveList = null;
    /** ロールリスト情報を保持. */
    private ArrayList<RoleListMetaData> mRoleListInfo = null;
    /** クリップキーレスポンス保持. */
    private ClipKeyListResponse mClipKeyListResponse = null;

    /** チャンネル検索(親クラスのDbThreadで"0","1","2"を使用しているため使用しない). */
    private static final int CHANNEL_SELECT = 9;
    /** ロールリスト更新. */
    private static final int ROLELIST_UPDATE = 3;
    /** ロールリスト検索. */
    private static final int ROLELIST_SELECT = 4;
    /** 購入済みチャンネルリスト更新. */
    private static final int RENTAL_VOD_UPDATE = 5;
    /** 購入済みチャンネルリスト取得. */
    private static final int RENTAL_VOD_SELECT = 6;
    /** 購入済みチャンネルリスト更新. */
    private static final int RENTAL_CHANNEL_UPDATE = 7;
    /** 購入済みチャンネルリスト取得. */
    private static final int RENTAL_CHANNEL_SELECT = 8;
    /** VodMetaFullData. */
    private VodMetaFullData mVodMetaFullData = null;
    /** コンテンツ詳細取得WebClient. */
    private ContentsDetailGetWebClient mDetailGetWebClient = null;
    /** ロールリスト取得WebClient. */
    private RoleListWebClient mRoleListWebClient = null;
    /** レンタルチャンネル一覧取得WebClient. */
    private RentalChListWebClient mRentalChListWebClient = null;
    /** レンタルVOD一覧取得WebClient. */
    private RentalVodListWebClient mRentalVodListWebClient = null;
    /** 通信禁止判定フラグ. */
    private boolean isStop = false;
    /** tvコンテンツのクリップキーリスト取得済み判定. */
    private boolean mTvClipKeyListResponse = false;
    /** vodコンテンツのクリップキーリスト取得済み判定. */
    private boolean mVodClipKeyListResponse = false;

    // endregion
    /**
     * コンストラクタ.
     *
     * @param context TvProgramListActivity
     */
    public ContentsDetailDataProvider(final Context context) {
        super(context);
        this.mContext = context;
        this.mApiDataProviderCallback = (ApiDataProviderCallback) context;
    }

    /**
     * コンストラクタ.
     *
     * @param context TvProgramListActivity
     */
    public ContentsDetailDataProvider(final Context context, final OttContentsBroadcastDataProvider ottContentsBroadcastDataProvider) {
        super(context);
        this.mContext = context;
        this.mApiDataProviderCallback = (ApiDataProviderCallback) context;
    }

    // region super class

    @Override
    public void onContentsDetailJsonParsed(final ContentsDetailGetResponse contentsDetailLists, final ErrorState jsonParseError) {
        if (contentsDetailLists != null && jsonParseError == null) {
            if (contentsDetailLists.getStatus().equals(mContext.getString( R.string.contents_detail_response_ok))) {
                ArrayList<VodMetaFullData> detailListInfo = contentsDetailLists.getVodMetaFullData();
                if (detailListInfo != null && detailListInfo.size() > 0) {
                    mVodMetaFullData = detailListInfo.get(0);
                    mVodMetaFullData.setContentsType(ContentUtils.getHikariContentsType(mVodMetaFullData));
                    if (!mRequiredClipKeyList) {
                        executeContentsDetailInfoCallback(
                                mVodMetaFullData, getContentsDetailClipStatus(detailListInfo.get(0)));
                    } else {
                        requestGetClipKeyList(mVodMetaFullData);
                    }
                } else {
                    //結果がOKでも0件ならば、エラー扱いとする
                    executeContentsDetailInfoCallback(null, false);
                }
            } else {
                //status = "NG"の場合
                executeContentsDetailInfoCallback(null, false);
            }
        } else {
            mDetailGetWebClient.setJsonParseError(jsonParseError);
            executeContentsDetailInfoCallback(null, false);
        }
    }

    /**
     * コンテンツ詳細データcallback実行用.
     * @param contentsDetailInfo コンテンツ詳細情報
     * @param clipStatus　クリップ状態
     */
    private void executeContentsDetailInfoCallback(final VodMetaFullData contentsDetailInfo, boolean clipStatus) {
        mIsInContentsDetailRequest = false;
        if (mApiDataProviderCallback != null) {
            mApiDataProviderCallback.onContentsDetailInfoCallback(contentsDetailInfo, clipStatus);
        }
    }

    @Override
    public void onTvClipKeyResult(final ClipKeyListResponse clipKeyListResponse
            ,final ErrorState errorState) {
        super.onTvClipKeyResult(clipKeyListResponse,errorState);
        DTVTLogger.start();
        if (mVodClipKeyListResponse) {
            addClipStatus();
        } else {
            mTvClipKeyListResponse = true;
            mClipKeyListResponse = clipKeyListResponse;
        }
        DTVTLogger.end();
    }

    @Override
    public void onVodClipKeyResult(final ClipKeyListResponse clipKeyListResponse
            ,final ErrorState errorState) {
        super.onVodClipKeyResult(clipKeyListResponse,errorState);
        DTVTLogger.start();
        if (mTvClipKeyListResponse) {
            addClipStatus();
        } else {
            mVodClipKeyListResponse = true;
            mClipKeyListResponse = clipKeyListResponse;
        }
        DTVTLogger.end();
    }

    /**
     * クリップ状態取得後にコンテンツ詳細情報を送信.
     *
     */
    private void addClipStatus() {
        DTVTLogger.start();
        //クリップ状態取得
        boolean isClipStatus;
        ClipKeyListDataManager manager = new ClipKeyListDataManager(mContext);
        List<Map<String, String>> mapList = manager.selectClipAllList();
        isClipStatus = ClipUtils.setClipStatusVodMetaData(mVodMetaFullData, mapList);
        executeContentsDetailInfoCallback(
                mVodMetaFullData, isClipStatus);
        DTVTLogger.end();
    }

    @Override
    public void onRentalVodListJsonParsed(final PurchasedVodListResponse purchasedVodListResponse, final ErrorState jsonParseError ) {
        mPurchasedVodListResponse = purchasedVodListResponse;
        if (mPurchasedVodListResponse != null && jsonParseError == null) {
            Handler handler = new Handler(); //チャンネル情報更新
            try {
                DataBaseThread dataBaseThread = new DataBaseThread(handler, this, RENTAL_VOD_UPDATE);
                dataBaseThread.start();
            } catch (RuntimeException e) {
                DTVTLogger.debug(e);
            }
            executeRentalVodListCallback(mPurchasedVodListResponse);
        } else {
            mRentalVodListWebClient.setJsonParseError(jsonParseError);
            executeRentalVodListCallback(null);
        }
    }

    /**
     * 購入済みVOD一覧返却用メソッド.
     * @param response 購入済みVod一覧
     */
    private void executeRentalVodListCallback(final PurchasedVodListResponse response) {
        mIsInRentalVodListRequest = false;
        if (mApiDataProviderCallback != null) {
            mApiDataProviderCallback.onRentalVodListCallback(response);
        }
    }

    @Override
    public void onRentalChListJsonParsed(final PurchasedChannelListResponse purchasedChannelListResponse, final ErrorState jsonParseError) {
        mPurchasedChannelListResponse = purchasedChannelListResponse;
        if (mPurchasedChannelListResponse != null && jsonParseError == null) {
            ArrayList<ActiveData> actionData = mPurchasedChannelListResponse.getChActiveData();
            List<Map<String, String>> channelList = mPurchasedChannelListResponse.getChannelListData().getChannelList();
            if (actionData != null && actionData.size() > 0 && channelList != null && !channelList.isEmpty()) {
                Handler handler = new Handler(); //チャンネル情報更新
                try {
                    DataBaseThread dataBaseThread = new DataBaseThread(handler, this, RENTAL_CHANNEL_UPDATE);
                    dataBaseThread.start();
                } catch (RuntimeException e) {
                    DTVTLogger.debug(e);
                }
            }
            executeRentalChListCallback(purchasedChannelListResponse);
        } else {
            mRentalChListWebClient.setJsonParseError(jsonParseError);
            executeRentalChListCallback(null);
        }
    }

    /**
     * 購入済みCh一覧返却用メソッド.
     * @param response 購入済みCh一覧
     */
    private void executeRentalChListCallback(final PurchasedChannelListResponse response) {
        mIsInRentalChListRequest = false;
        if (mApiDataProviderCallback != null) {
            mApiDataProviderCallback.onRentalChListCallback(response);
        }
    }

    @Override
    public void onRoleListJsonParsed(final RoleListResponse roleListResponse) {
        if (roleListResponse != null) {
            mRoleListInfo = roleListResponse.getRoleList();
            if (mRoleListInfo != null) {
                Handler handler = new Handler(); //チャンネル情報更新
                try {
                    DataBaseThread dataBaseThread = new DataBaseThread(handler, this, ROLELIST_UPDATE);
                    dataBaseThread.start();
                } catch (RuntimeException e) {
                    DTVTLogger.debug(e);
                }
            }
            executeRoleListCallback(mRoleListInfo);
        } else {
            executeRoleListCallback(null);
        }
    }

    /**
     * ロールリスト返却用メソッド.
     * @param roleListInfo ロールリスト
     */
    private void executeRoleListCallback(final ArrayList<RoleListMetaData> roleListInfo) {
        mIsInRoleListRequest = false;
        if (mApiDataProviderCallback != null) {
            mApiDataProviderCallback.onRoleListCallback(roleListInfo);
        }
    }

    @SuppressWarnings("OverlyLongMethod")
    @Override
    public void onDbOperationFinished(final boolean isSuccessful, final List<Map<String, String>> resultSet, final int operationId) {
        if (isSuccessful) {
            switch (operationId) {
                case ROLELIST_SELECT:
                    ArrayList<RoleListMetaData> roleListData = new ArrayList<>();
                    for (int i = 0; i < resultSet.size(); i++) {
                        Map<String, String> hashMap = resultSet.get(i);
                        String id = hashMap.get( JsonConstants.META_RESPONSE_CONTENTS_ID);
                        String name = hashMap.get(JsonConstants.META_RESPONSE_CONTENTS_NAME);
                        RoleListMetaData roleData = new RoleListMetaData();
                        roleData.setId(id);
                        roleData.setName(name);
                        roleListData.add(roleData);
                    }
                    executeRoleListCallback(roleListData);
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
    @Override
    public List<Map<String, String>> dbOperation(final DataBaseThread dataBaseThread, final int operationId) {
        super.dbOperation(dataBaseThread, operationId);
        List<Map<String, String>> resultSet = null;
        switch (operationId) {
            case ROLELIST_UPDATE: //サーバーから取得したロールリストデータをDBに保存する
                RoleListInsertDataManager roleListInsertDataManager = new RoleListInsertDataManager(mContext);
                roleListInsertDataManager.insertRoleList(mRoleListInfo);
                break;
            case ROLELIST_SELECT: //DBからロールリストデータを取得して、画面に返却する
                HomeDataManager homeDataManager = new HomeDataManager(mContext);
                resultSet = homeDataManager.selectRoleListData();
                break;
            case CHANNEL_SELECT: //DBからチャンネルデータを取得して、画面に返却する
                ProgramDataManager channelDataManager = new ProgramDataManager(mContext);
                resultSet = channelDataManager.selectChannelListProgramData(JsonConstants.CH_SERVICE_TYPE_INDEX_ALL);
                break;
            case RENTAL_VOD_UPDATE: //サーバーから取得した購入済みVODデータをDBに保存する
                RentalListInsertDataManager rentalListInsertDataManager = new RentalListInsertDataManager(mContext);
                rentalListInsertDataManager.insertRentalListInsertList(mPurchasedVodListResponse);
                break;
            case RENTAL_VOD_SELECT: //DBから購入済みVODデータを取得して返却する
                RentalListDataManager rentalListDataManager = new RentalListDataManager(mContext);
                List<Map<String, String>> purchasedVodActiveList = rentalListDataManager.selectRentalActiveListData();
                PurchasedVodListResponse purchasedVodListData = new PurchasedVodListResponse();
                if (purchasedVodActiveList == null || purchasedVodActiveList.size() < 1) {
                    //DBから取得したデータがない場合は初期化したデータを渡す
                    executeRentalVodListCallback(purchasedVodListData);
                } else {
                    ArrayList<ActiveData> activeDatas = new ArrayList<>();
                    for (int i = 0; i < purchasedVodActiveList.size(); i++) {
                        Map<String, String> hashMap = purchasedVodActiveList.get(i);
                        String active_list_license_id = hashMap.get(JsonConstants.META_RESPONSE_ACTIVE_LIST
                                + JsonConstants.UNDER_LINE + JsonConstants.META_RESPONSE_LICENSE_ID);
                        String active_list_valid_end_date = hashMap.get(JsonConstants.META_RESPONSE_ACTIVE_LIST
                                + JsonConstants.UNDER_LINE + JsonConstants.META_RESPONSE_VAILD_END_DATE);
                        ActiveData activeDate = new ActiveData();
                        activeDate.setLicenseId(active_list_license_id);
                        activeDate.setValidEndDate(Long.parseLong(active_list_valid_end_date));
                        activeDatas.add(activeDate);
                    }
                    purchasedVodListData.setVodActiveData(activeDatas);
                    if (null != mApiDataProviderCallback) {
                        executeRentalVodListCallback(purchasedVodListData);
                    }
                }
                break;
            case RENTAL_CHANNEL_UPDATE: //サーバーから取得した購入済みCHデータをDBに保存する
                RentalListInsertDataManager rentalChListInsertDataManager = new RentalListInsertDataManager(mContext);
                rentalChListInsertDataManager.insertChRentalListInsertList(mPurchasedChannelListResponse);
                break;
            case RENTAL_CHANNEL_SELECT: //DBから購入済みCHデータを取得して返却する
                RentalListDataManager rentalChListDataManager = new RentalListDataManager(mContext);
                resultSet = rentalChListDataManager.selectRentalChListData();
                List<Map<String, String>> purchasedChActiveList = rentalChListDataManager.selectRentalChActiveListData();
                PurchasedChannelListResponse purchasedChannelListResponse = new PurchasedChannelListResponse();
                if (purchasedChActiveList == null || purchasedChActiveList.size() < 1 || resultSet == null || resultSet.size() < 1) {
                    //active_list又はmetadata_listが取得できない時は必ず購入済み判定がfalseになるため初期化したデータを渡す
                    executeRentalChListCallback(purchasedChannelListResponse);
                } else {
                    ChannelList channelList = new ChannelList();
                    List<HashMap<String, String>> list = new ArrayList<>();

                    for (int i = 0; i < resultSet.size(); i++) {
                        Map<String, String> hashMap = resultSet.get(i);
                        HashMap<String, String> vcListMap = new HashMap<>();
                        for (String para : JsonConstants.METADATA_LIST_PARA) {
                            vcListMap.put(para, hashMap.get(para));
                        }
                        list.add(vcListMap);
                    }
                    channelList.setChannelList(list);
                    purchasedChannelListResponse.setChannelListData(channelList);

                    ArrayList<ActiveData> activeChDatas = new ArrayList<>();
                    for (int i = 0; i < purchasedChActiveList.size(); i++) {
                        Map<String, String> hashMap = purchasedChActiveList.get(i);

                        String active_list_license_id = hashMap.get(JsonConstants.META_RESPONSE_ACTIVE_LIST
                                + JsonConstants.UNDER_LINE + JsonConstants.META_RESPONSE_LICENSE_ID);
                        String active_list_valid_end_date = hashMap.get(JsonConstants.META_RESPONSE_ACTIVE_LIST
                                + JsonConstants.UNDER_LINE + JsonConstants.META_RESPONSE_VAILD_END_DATE);
                        ActiveData activeDate = new ActiveData();
                        activeDate.setLicenseId(active_list_license_id);
                        activeDate.setValidEndDate(Long.parseLong(active_list_valid_end_date));

                        activeChDatas.add(activeDate);
                    }
                    purchasedChannelListResponse.setChActiveData(activeChDatas);
                    if (null != mApiDataProviderCallback) {
                        executeRentalChListCallback(purchasedChannelListResponse);
                    }
                }
                break;
            default:
                break;
        }
        return resultSet;
    }

    @Override
    public void onRemoteRecordingReservationJsonParsed(final RemoteRecordingReservationResultResponse response) {
        mApiDataProviderCallback.recordingReservationResult(response);
    }
    // endregion

    // region public method
    /**
     * コンテンツ詳細取得.
     *
     * @param crid   取得したい情報のコンテンツ識別ID(crid)の配列
     * @param filter release、testa、demo ※指定なしの場合release
     * @param ageReq dch：dチャンネル, hikaritv：ひかりTVの多ch, 指定なしの場合：すべて
     * @param areaCode エリアコード
     */
    public void getContentsDetailData(final String[] crid, final String filter, final int ageReq, final String areaCode) {
        mIsInContentsDetailRequest = true;
        if (!isStop) {
            mDetailGetWebClient = new ContentsDetailGetWebClient(mContext);
            mDetailGetWebClient.getContentsDetailApi(crid, filter, ageReq, areaCode, this);
        } else {
            DTVTLogger.error("ContentsDetailDataProvider is stopping connect");
        }
    }

    /**
     * 購入済みVOD一覧取得.
     */
    public void getVodListData() {
        mIsInRentalVodListRequest = true;
        DateUtils dateUtils = new DateUtils(mContext);
        String lastDate = dateUtils.getLastDate( DateUtils.RENTAL_VOD_LAST_UPDATE);
        if (!TextUtils.isEmpty(lastDate) && !dateUtils.isBeforeLimitDate(lastDate)) {
            //データをDBから取得する
            Handler handler = new Handler(); //チャンネル情報更新
            try {
                DataBaseThread dataBaseThread = new DataBaseThread(handler, this, RENTAL_VOD_SELECT);
                dataBaseThread.start();
            } catch (RuntimeException e) {
                DTVTLogger.debug(e);
            }
        } else {
            if (!isStop) {
                mRentalVodListWebClient = new RentalVodListWebClient(mContext);
                mRentalVodListWebClient.getRentalVodListApi(this);
            } else {
                DTVTLogger.error("ContentsDetailDataProvider is stopping connect");
            }
        }
    }

    /**
     * 購入済みCH一覧取得.
     */
    public void getChListData() {
        mIsInRentalChListRequest = true;
        DateUtils dateUtils = new DateUtils(mContext);
        String lastDate = dateUtils.getLastDate(DateUtils.RENTAL_CHANNEL_LAST_UPDATE);
        if (!TextUtils.isEmpty(lastDate) && !dateUtils.isBeforeLimitDate(lastDate)) {
            //データをDBから取得する
            Handler handler = new Handler(); //チャンネル情報更新
            try {
                DataBaseThread dataBaseThread = new DataBaseThread(handler, this, RENTAL_CHANNEL_SELECT);
                dataBaseThread.start();
            } catch (RuntimeException e) {
                DTVTLogger.debug(e);
            }
        } else {
            if (!isStop) {
                mRentalChListWebClient = new RentalChListWebClient(mContext);
                mRentalChListWebClient.getRentalChListApi(this);
            } else {
                DTVTLogger.error("ContentsDetailDataProvider is stopping connect");
            }
        }
    }

    /**
     * ロールリスト取得.
     */
    public void getRoleListData() {
        mIsInRoleListRequest = true;
        DateUtils dateUtils = new DateUtils(mContext);
        String lastDate = dateUtils.getLastDate(DateUtils.ROLELIST_LAST_UPDATE);
        if (!TextUtils.isEmpty(lastDate) && !dateUtils.isBeforeProgramLimitDate(lastDate)) {
            //データをDBから取得する
            Handler handler = new Handler(); //チャンネル情報更新
            try {
                DataBaseThread dataBaseThread = new DataBaseThread(handler, this, ROLELIST_SELECT);
                dataBaseThread.start();
            } catch (RuntimeException e) {
                DTVTLogger.debug(e);
            }
        } else {
            if (!isStop) {
                mRoleListWebClient = new RoleListWebClient(mContext);
                mRoleListWebClient.getRoleListApi(this);
            } else {
                DTVTLogger.error("ContentsDetailDataProvider is stopping connect");
            }
        }
    }

    /**
     * 録画予約一覧取得.
     *
     * @param info 録画予約コンテンツ詳細
     * @return パラメータエラー等でAPIの実行が行えなかった場合はfalse
     */
    public boolean requestRecordingReservation(final RecordingReservationContentsDetailInfo info) {
        RemoteRecordingReservationWebClient client =
                new RemoteRecordingReservationWebClient(mContext);
        return client.getRemoteRecordingReservationApi(info, this);
    }

    /**
     * 通信を止める.
     */
    public void stopConnect() {
        DTVTLogger.start();
        isStop = true;
        if (mDetailGetWebClient != null) {
            mDetailGetWebClient.stopConnection();
        }
        if (mRoleListWebClient != null) {
            mRoleListWebClient.stopConnection();
        }
        if (mRentalChListWebClient != null) {
            mRentalChListWebClient.stopConnection();
        }
        if (mRentalVodListWebClient != null) {
            mRentalVodListWebClient.stopConnection();
        }
        stopConnection();
    }

    /**
     * 通信を許可する.
     */
    public void enableConnect() {
        DTVTLogger.start();
        isStop = false;
        if (mDetailGetWebClient != null) {
            mDetailGetWebClient.enableConnection();
        }
        if (mRoleListWebClient != null) {
            mRoleListWebClient.enableConnection();
        }
        if (mRentalChListWebClient != null) {
            mRentalChListWebClient.enableConnection();
        }
        if (mRentalVodListWebClient != null) {
            mRentalVodListWebClient.enableConnection();
        }
    }

    /**
     * WebClientで発生したエラーを返す.
     * @param type WebClientType(コールしたWebClientType)
     *
     * @return エラー情報
     */
    public ErrorState getError(final ErrorType type) {
        switch (type) {
            case contentsDetailGet:
                return mDetailGetWebClient.getError();
            case roleList:
                return mRoleListWebClient.getError();
            case rentalChList:
                return mRentalChListWebClient.getError();
            case rentalVodList:
                return mRentalVodListWebClient.getError();
            default:
                break;
        }
        return null;
    }
    // endregion

    // region private method
    /**
     * コンテンツ詳細情報のメタデータを元にクリップ状態を取得.
     *
     * @param metaFullData クリップ状態
     * @return コンテンツ詳細データ
     */
    private boolean getContentsDetailClipStatus(final VodMetaFullData metaFullData) {
        return getClipStatus(metaFullData.getDisp_type(),
                metaFullData.getmContent_type(),
                metaFullData.getDtv(),
                metaFullData.getCrid(),
                metaFullData.getmService_id(),
                metaFullData.getmEvent_id(),
                metaFullData.getTitle_id(),
                metaFullData.getmTv_service(),
                metaFullData.getmVod_start_date());
    }

    /**
     * コンテンツ詳細情報を元にクリップキー一覧の取得を要求.
     *
     * @param metaFullData コンテンツ詳細データ
     */
    private void requestGetClipKeyList(final VodMetaFullData metaFullData) {
        DTVTLogger.start();
        mTvClipKeyListResponse = false;
        mVodClipKeyListResponse = false;
        //番組でも見逃し等VODデータが入ってい来ることがあるので、全件取得する
        getClipKeyList(new ClipKeyListRequest(ClipKeyListRequest.RequestParamType.TV));
        getClipKeyList(new ClipKeyListRequest(ClipKeyListRequest.RequestParamType.VOD));
        DTVTLogger.end();
    }

    /**
     * コンテンツ詳細リクエストステータスフラグ返却.
     * @return リクエストステータス
     */
    public boolean isInContentsDetailRequest() {
        return mIsInContentsDetailRequest;
    }

    /**
     * ロールリストリクエストステータスフラグ返却.
     * @return リクエストステータス
     */
    public boolean isInRoleListRequest() {
        return mIsInRoleListRequest;
    }

    /**
     * レンタルVodリストリクエストステータスフラグ返却.
     * @return リクエストステータス
     */
    public boolean isInRentalVodListRequest() {
        return mIsInRentalVodListRequest;
    }

    /**
     * レンタルChリストリクエストステータスフラグ返却.
     * @return リクエストステータス
     */
    public boolean isInRentalChListRequest() {
        return mIsInRentalChListRequest;
    }
    // endregion
}

