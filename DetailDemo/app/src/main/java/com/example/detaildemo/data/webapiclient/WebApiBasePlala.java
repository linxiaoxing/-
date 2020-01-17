package com.example.detaildemo.data.webapiclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.detaildemo.BuildConfig;
import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.common.ErrorState;
import com.example.detaildemo.data.bean.OneTimeTokenData;
import com.example.detaildemo.data.webapiclient.accout.IDimDefines;
import com.example.detaildemo.data.webapiclient.token.ServiceTokenClient;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DateUtils;
import com.example.detaildemo.utils.NetWorkUtils;
import com.example.detaildemo.utils.SharedPreferencesUtils;
import com.example.detaildemo.utils.UserAgentUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * 通信処理.
 */
public class WebApiBasePlala{
    /**
     * エラー値.
     */
    protected ReturnCode mReturnCode;
    /**
     * コンテキスト.
     */
    private Context mContext;
    /**
     * コールバックのインスタンス.
     */
    private WebApiBasePlalaCallback mWebApiBasePlalaCallback = null;
    /**
     * 通信停止用コネクション蓄積.
     */
    private volatile static List<HttpURLConnection> mUrlConnections = null;
    /**
     * 全部止まった場合のフラグ.
     */
    private static boolean mIsStopAllConnections = false;
    /**
     * コネクション.
     */
    private HttpURLConnection mUrlConnection = null;
    /**
     * 結果を受け取るバッファ.
     */
    private String mAnswerBuffer = "";
    /**
     * ワンタイムトークン取得クラス.
     */
    private DaccountGetOtt mGetOtt = null;
    /**
     * クッキーマネージャー.
     */
    private CookieManager mCookieManager;
    /**
     * クッキー退避領域.
     */
    private List<HttpCookie> mCookies;
    /**
     * リクエスト種別・基本はPOST・子クラスで使われるのでプロテクテッド.
     */
    private static final String REQUEST_METHOD_POST = "POST";
    /**
     * リダイレクト処理用にGETも定義・子クラスで使われるのでプロテクテッド.
     */
    protected static final String REQUEST_METHOD_GET = "GET";
    /**
     * 文字種別 UTF-8.
     */
    private static final String UTF8_CHARACTER_SET = "UTF-8";

    //POSTでJSONを送信する為のパラメータ群
    /**
     * Content-Type.
     */
    private static final String CONTENT_TYPE_KEY_TEXT = "Content-Type";
    /**
     * application/json: charset=UTF-8.
     */
    private static final String CONTENT_TYPE_TEXT = "application/json; charset="
            + UTF8_CHARACTER_SET; //final同士なので、+での結合こそベスト

    /**
     * API通信用タスク.
     */
    private CommunicationTask mCommunicationTaskAPI = null;
    /**
     * API通信用フラグ(拡張情報付き).
     */
    private CommunicationTask mCommunicationTaskExtra = null;
    /**
     * ワンタイムトークンの取得コールバックからのタスク呼び出し用.
     */
    private CommunicationTask mCommunicationTaskOtt = null;
    /**
     * タイムアウト時間.
     */
    private int mTimeOut = 0;

    /**
     * 通信停止用フラグ.
     */
    private boolean mIsStop = false;
    /**
     * サービストークンのクッキーでのキー名.
     */
    private static final String SERVICE_TOKEN_KEY_NAME = "daccount_auth";
    /**
     * 日付形式判定用.
     */
    private static final String DATE_PATTERN = "yyyyMMdd";
    /**
     * リダイレクト用飛び先関連情報取得.
     */
    private static final String REDIRECT_JUMP_URL_GET = "Location";
    /**
     * SSL失効チェック時にコンテキストが存在しない場合の例外用テキスト.
     */
    private static final String NO_CONTEXT_ERROR = "No context";
    /**
     * リダイレクト時の飛び先URLが無い場合の例外用テキスト.
     */
    private static final String NO_REDIRECT_URL_ERROR = "No redirect url";
    //指定文字列パラメータ群
    //対外的なパラメータなので、現在は非使用の物にもpublicが必要になる。
    /**
     * フィルター用指定文字列・release.
     */
    public static final String FILTER_RELEASE = "release";
    /**
     * フィルター用指定文字列・testa.
     */
    protected static final String FILTER_TESTA = "testa";
    /**
     * フィルター用指定文字列・demo.
     */
    protected static final String FILTER_DEMO = "demo";
    /**
     * フィルター用指定文字列・ssvod.
     */
    public static final String FILTER_RELEASE_SSVOD = "release|ssvod";
    /**
     * フィルター用指定文字列・フィルターのパラメータ名.
     */
    protected static final String FILTER_PARAM = "filter";

    /**
     * タイプ用指定文字列・ｄCh.
     */
    protected static final String TYPE_D_CHANNEL = "dch";
    /**
     * タイプ用指定文字列・ひかりTV.
     */
    protected static final String TYPE_HIKARI_TV = "hikaritv";
    /**
     * タイプ用指定文字列・ひかりTV多chのOTT.
     */
    protected static final String TYPE_HIKARI_OTT = "hikariott";
    /**
     * タイプ用指定文字列・ひかりTVのVOD.
     */
    protected static final String TYPE_HIKARI_TV_VOD = "hikaritv_vod";
    /**
     * タイプ用指定文字列・dTVのVOD.
     */
    public static final String TYPE_DTV_VOD = "dtv_vod";
    /**
     * タイプ用指定文字列・ひかりTVのVODとdTVのVOD.
     */
    protected static final String TYPE_HIKARI_TV_AND_DTV_VOD = "hikaritv_and_dtv_vod";
    /**
     * タイプ用指定文字列・全て（指定なしは全てになる）.
     */
    protected static final String TYPE_ALL = "";

    /**
     * 日付指定文字列・現在時刻指定.
     */
    public static final String DATE_NOW = "now";

    /**
     * ソート用文字列・タイトルルビ昇順.
     */
    static final String SORT_TITLE_RUBY_ASC = "titleruby_asc";
    /**
     * ソート用文字列・配信開始日昇順.
     */
    static final String SORT_AVAIL_S_ASC = "avail_s_asc";
    /**
     * ソート用文字列・配信終了日降順.
     */
    static final String SORT_AVAIL_E_DESC = "avail_e_desc";
    /**
     * ソート用文字列・人気順（前日の視聴回数数降順）.
     */
    static final String SORT_PLAY_COUNT_DESC = "play_count_desc";

    /**
     * age_req(年齢設定値)の最小値.
     */
    protected static final int AGE_LOW_VALUE = 1;

    /**
     * age_req(年齢設定値)の最大値.
     */
    protected static final int AGE_HIGH_VALUE = 17;

    /**
     * リスト作成文字列.
     */
    protected static final String LIST_STRING = "list";

    /**
     * h4d_iptv：多チャンネル.
     */
    public static final String CLIP_TYPE_H4D_IPTV = "h4d_iptv";

    /**
     * h4d_vod：ビデオ.
     */
    public static final String CLIP_TYPE_H4D_VOD = "h4d_vod";

    /**
     * dch：dTVチャンネル.
     */
    public static final String CLIP_TYPE_DCH = "dch";

    /**
     * dtv_vod：dTV.
     */
    public static final String CLIP_TYPE_DTV_VOD = "dtv_vod";

    /**
     * h4d_ttb_crid:TTB.
     */
    public static final String CLIP_TYPE_H4D_TTB_CRID = "h4d_ttb_crid";

    /**
     * h4d_bs_crid:BS.
     */
    public static final String CLIP_TYPE_H4D_BS_CRID = "h4d_bs_crid";

    /**
     * h4d_bs4k_crid:BS4K.
     */
    public static final String CLIP_TYPE_H4D_BS4K_CRID = "h4d_bs4k_crid";

    /**
     * ワンタイムトークン設定のキー名.
     */
    private static final String ONE_TIME_TOKEN_KEY = "x-service-token";

    /**
     * ワンタイムトークン設定の取得時のコンテントタイプ指定.
     */
    private static final String ONE_TIME_TOKEN_GET_CONTENT_TYPE =
            "application/x-www-form-urlencoded";

    /**
     * UserAgent.
     */
    private static final String USER_AGENT_KEY = "User-Agent";

    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     */
    public WebApiBasePlala(final Context context) {
        //コンテキストの退避
        mContext = context.getApplicationContext();

        //コネクション蓄積が存在しなければ作成する
        if (mUrlConnections == null) {
            mUrlConnections = new ArrayList<>();
        }

        //戻り値の準備
        mReturnCode = new ReturnCode();
    }

    /**
     * ワンタイムトークン取得APIの通信を開始する.
     * (使用方法が特異の為、専用とする)
     *
     * @param receivedParameters      API呼び出し用パラメータ
     * @param webApiBasePlalaCallback コールバック
     */
    public void openOneTimeTokenGetUrl(final String receivedParameters,
                                final WebApiBasePlalaCallback webApiBasePlalaCallback) {
        DTVTLogger.start();

        CommunicationTask communicationTask = new CommunicationTask(receivedParameters);

        //コールバックの準備
        mWebApiBasePlalaCallback = webApiBasePlalaCallback;

        //結果格納構造体の作成
        ReturnCode returnCode = new ReturnCode();

        //通信本体の開始
        communicationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, returnCode);

        DTVTLogger.end();
    }

    /**
     * データ受け渡しコールバック.
     */
    public interface WebApiBasePlalaCallback {
        /**
         * 正常終了時のコールバック.
         *
         * @param returnCode 値を返す構造体
         */
        void onAnswer(final ReturnCode returnCode);

        /**
         * 通信失敗時のコールバック.
         *
         * @param returnCode 値を返す構造体
         */
        void onError(final ReturnCode returnCode);
    }

    /**
     * サービストークンエラーコールバック.
     */
    interface ServiceTokenErrorCallback {
        /**
         * サービストークン取得失敗のコールバック.
         *
         * @param returnCode 値を返す構造体
         */
        void onTokenError(final ReturnCode returnCode);
    }

    /**
     * 戻り値用構造体.
     */
    static public class ReturnCode {
        /**
         * 通信時エラー情報.
         */
        final ErrorState errorState;
        /**
         * 本体データ.
         */
        public String bodyData;
        /**
         * 拡張データ.
         */
        Bundle extraData;

        /**
         * コンストラクタ.
         */
        ReturnCode() {
            errorState = new ErrorState();
            bodyData = "";
            extraData = null;
        }
    }

    /**
     * 通信本体のクラス.
     */
    public class CommunicationTask extends AsyncTask<Object, Object, ReturnCode>{
        /**
         * 実行するAPIの名前.
         */
        final String mSourceUrl;
        /**
         * 送るパラメータ.
         */
        String mSendParameter;
        /**
         * 拡張データ.
         */
        Bundle mExtraData = null;
        /**
         * ワンタイムトークンの取得の有無.
         */
        private boolean mIsUseOtt = false;
        /**
         * ワンタイムトークンの値.
         */
        private String mOneTimeToken = "";
        /**
         * ワンタイムトークン取得のスイッチ.
         */
        private boolean oneTimeTokenGetSwitch = false;
        /**
         * リクエストに利用するHTTPメソッド.
         */
        private String mRequestMethod = REQUEST_METHOD_POST;

        /**
         * ワンタイムトークンの値を設定する.
         *
         * @param oneTimeToken 設定したいワンタイムトークン
         */
        public void setOneTimeToken(final String oneTimeToken) {
            mOneTimeToken = oneTimeToken;
        }

        /**
         * コンストラクタ.
         *
         * @param sourceUrl          実行するAPIの名前
         * @param receivedParameters 送るパラメータ
         * @param requestMethod      HTTPリクエストメソッド
         */
        CommunicationTask(final String sourceUrl, final String receivedParameters, final String requestMethod) {
            mSourceUrl = sourceUrl;
            mSendParameter = receivedParameters;

            //拡張データとワンタイムトークンは使用しない
            mExtraData = null;
            mIsUseOtt = false;
            oneTimeTokenGetSwitch = false;
            mRequestMethod = requestMethod;
        }

        /**
         * コンストラクタ(拡張情報付き).
         *
         * @param sourceUrl          実行するAPIの名前
         * @param receivedParameters 送るパラメータ
         * @param extraDataSrc       受け渡す拡張情報
         * @param requestMethod      HTTPリクエストメソッド
         */
        CommunicationTask(final String sourceUrl, final String receivedParameters,
                          final Bundle extraDataSrc, final String requestMethod) {
            mSourceUrl = sourceUrl;
            mSendParameter = receivedParameters;

            //拡張データの確保
            mExtraData = extraDataSrc;

            //ワンタイムトークンは使用しない
            mIsUseOtt = false;
            oneTimeTokenGetSwitch = false;
            mRequestMethod = requestMethod;
        }

        /**
         * コンストラクタ（ワンタイムトークを使用する場合）.
         *
         * @param sourceUrl          実行するAPIの名前
         * @param receivedParameters 送るパラメータ
         * @param extraDataSrc       受け渡す拡張情報
         * @param isGetOtt           ワンタイムトークンの使用可否
         * @param requestMethod      HTTPリクエストメソッド
         */
        CommunicationTask(final String sourceUrl, final String receivedParameters,
                          final Bundle extraDataSrc, final boolean isGetOtt, final String requestMethod) {
            mSourceUrl = sourceUrl;
            mSendParameter = receivedParameters;

            if (extraDataSrc != null) {
                //拡張データの確保
                mExtraData = extraDataSrc;
            } else {
                mExtraData = null;
            }

            //ワンタイムトークンの使用可否
            mIsUseOtt = isGetOtt;
            oneTimeTokenGetSwitch = false;
            mRequestMethod = requestMethod;
        }

        /**
         * dアカウント認証要求・認可専用コンストラクタ.
         * このAPIは使用方法が全く違うため、専用の動作とする
         *
         * @param receivedParameters ワンタイムパスワードを含むパラメータ
         */
        CommunicationTask(final String receivedParameters) {
            //TODO
            mSourceUrl = "";
            mSendParameter = receivedParameters;

            //拡張データとワンタイムトークンは使用しない
            mExtraData = null;
            mIsUseOtt = false;

            //ワンタイムトークン取得時スイッチをONにする
            oneTimeTokenGetSwitch = true;
            mRequestMethod = REQUEST_METHOD_POST;
        }

        /**
         * 通信本体処理.
         *
         * @param strings 不使用
         * @return 不使用
         */
        @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
        @Override
        protected ReturnCode doInBackground(final Object... strings) {
            if (isCancelled() || mIsStop) {
                return null;
            }

            //圏外等の判定
            if (mContext == null || !NetWorkUtils.isOnline(mContext)) {
                //そもそも通信のできない状態なので、ネットワークエラーとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.NETWORK_ERROR);

                //以下の処理は行わずに帰る
                return mReturnCode;
            }

            //ワンタイムトークンを使用する処理で値が存在しないならば、トークンエラーとする
            if (mIsUseOtt && mOneTimeToken.isEmpty()) {
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.TOKEN_ERROR);

                //以下の処理は行わずに帰る
                return mReturnCode;
            }

            //クッキー管理の初期化 -- 所有请求之前声明
            //Android自身所带的HttpUrlConnection方法是默认不开启Cookie存储的
            mCookieManager = new CookieManager();
            //CookiePolicy.ACCEPT_ALL 接收所有Cookies.
            //CookiePolicy.ACCEPT_ORIGINAL_SERVER 只接收来自对应Server的cookies.
            //CookiePolicy.ACCEPT_NONE 不接收Cookies.
            mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(mCookieManager);

            //サービストークン取得は専用の処理に移行した。
            // その為、以降の処理からもサービストークン取得用の処理は取り除いた。
            if (oneTimeTokenGetSwitch) {
                DTVTLogger.debug("first Url=" + mSourceUrl);
                DTVTLogger.debug("first param=" + mSendParameter);
                gotoRedirect(mSourceUrl, mSendParameter);
                return mReturnCode;
            }

            //タイムアウト判定用
            boolean isTimeout = false;
            long startTime = System.currentTimeMillis();

            try {
                //指定された名前でURLを作成する
                URL url = new URL(mSourceUrl);

                //指定された名前で開く
                mUrlConnection = (HttpURLConnection) url.openConnection();

                //事前設定パラメータのセット
                setParameters(mUrlConnection);

                //コンテキストがあればSSL証明書失効チェックを行う
                if (mContext != null) {
                    DTVTLogger.debug(mSourceUrl);
                    //SSL証明書失効チェックライブラリの初期化を行う
//                    OcspUtil.init(mContext);
//
//                    //通信開始時にSSL証明書失効チェックを併せて行う
//                    OcspURLConnection ocspURLConnection = new OcspURLConnection(mUrlConnection);
//                    ocspURLConnection.connect();
                } else {
                    DTVTLogger.debug("SSL check error");
                    //将来はSSL専用になるので、コンテキストが無くて証明書チェックが行えないならばエラーとする
                    //SSLチェック初期化失敗の例外を投げる
                    throw new SSLPeerUnverifiedException(NO_CONTEXT_ERROR);
                }

                //パラメータを渡す
                if (mRequestMethod.equals(REQUEST_METHOD_POST)) {
                    setPostData(mUrlConnection);
                }
                //結果を読み込む
                int statusCode = mUrlConnection.getResponseCode();
                mAnswerBuffer = readConnectionBody(statusCode);

                //通信の切断と切断リストからの削除
                mUrlConnection.disconnect();
                removeConnections(mUrlConnection);
            } catch (ConnectException e) {
                DTVTLogger.warning("ConnectException");
                //通信エラー扱いとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.NETWORK_ERROR);

                //コネクト時のタイムアウトはこちらに来るので、判定する
                if (System.currentTimeMillis() - startTime
                        > DtvtConstants.SERVER_CONNECT_TIMEOUT) {
                    //エラーメッセージにタイムアウトを示唆する物があればタイムアウトとして扱う
                    isTimeout = true;
                    DTVTLogger.debug("timeout ConnectException:" + e.getMessage());
                } else {
                    isTimeout = false;
                    DTVTLogger.debug("normal ConnectException" + e.getMessage());
                }
                //タイムアウトエラーの設定
                mReturnCode.errorState.setIsTimeout(isTimeout);
            } catch (SSLHandshakeException e) {
                DTVTLogger.warning("SSLHandshakeException");
                //SSL証明書が失効している
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SSL_ERROR);
                DTVTLogger.debug(e);
            } catch (SSLPeerUnverifiedException e) {
                DTVTLogger.warning("SSLPeerUnverifiedException");
                //SSLチェックライブラリの初期化が行われていない
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SSL_ERROR);
                DTVTLogger.debug(e);
            } catch (SocketTimeoutException e) {
                //タイムアウトを明示して取得
                DTVTLogger.warning("SocketTimeoutException");
                DTVTLogger.debug("SocketTimeoutException normal ConnectException:"
                        + e.getMessage());
                //サーバーエラー扱いとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.SERVER_ERROR);
                //タイムアウト判定用
                mReturnCode.errorState.setIsTimeout(true);
                DTVTLogger.debug(e);
            } catch (IOException e) {
                DTVTLogger.warning("IOException");
                //サーバーエラー扱いとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.SERVER_ERROR);
                DTVTLogger.debug(e);
            } finally {
                //最後なので初期化
                mUrlConnection = null;
            }

            DTVTLogger.end();
            return mReturnCode;
        }

        /**
         * 通信終了後の処理.
         *
         * @param returnCode 結果格納構造体
         */
        @Override
        protected void onPostExecute(final ReturnCode returnCode) {
            DTVTLogger.start();
            if (returnCode == null || isCancelled()) {
                return;
            }

            //拡張情報があればそれも伝える
            if (mExtraData != null) {
                returnCode.extraData = mExtraData;
            }

            //呼び出し元に伝える情報を判断する
            switch (returnCode.errorState.getErrorType()) {
                //通信自体は成功している場合
                case SUCCESS:
                    if (mAnswerBuffer.isEmpty()) {
                        //結果の値が無いので、失敗を伝える
                        returnCode.errorState.setErrorType(DtvtConstants.ErrorType.HTTP_ERROR);
                        mWebApiBasePlalaCallback.onError(returnCode);
                    } else {
                        //通信に成功したので、値を伝える
                        // **FindBugs** Bad practice FindBugsは不使用なのでbodyDataは消せと警告するが、
                        // コールバック先では使用するため対応しない
                        returnCode.bodyData = mAnswerBuffer;

                        //エラーコードをセットする
                        if (returnCode.errorState.setErrorCode(mAnswerBuffer)) {
                            //エラーコードが存在したので、HTTPエラーとする
                            returnCode.errorState.setErrorType(
                                    DtvtConstants.ErrorType.HTTP_ERROR);
                        }

                        DTVTLogger.debug("onPostExecute answer = " + returnCode);
                        mWebApiBasePlalaCallback.onAnswer(returnCode);
                    }
                    break;

                //通信自体が失敗している場合
                case NETWORK_ERROR:
                case SERVER_ERROR:
                case TOKEN_ERROR:
                case SSL_ERROR:
                case HTTP_ERROR:
                    //その他のエラーなので、呼び出し元にはエラーを伝える
                    mWebApiBasePlalaCallback.onError(returnCode);
                    break;
            }

            //次のワンタイムトークンの取得を許可する
            if (mGetOtt != null) {
                mGetOtt.allowNext(mContext);
            }

            DTVTLogger.end();
        }

        /**
         * HTTPリクエスト用のパラメータを指定する.
         *
         * @param urlConnection コネクション
         * @throws ProtocolException プロトコルエクセプション
         */
        void setParameters(final HttpURLConnection urlConnection) throws ProtocolException {
            //送る文字列長の算出
            byte[] sendParameterByte = mSendParameter.getBytes( StandardCharsets.UTF_8);
            int sendParameterLength = sendParameterByte.length;

            //ワンタイムトークンに内容があれば、セットする
            if (mIsUseOtt && !mOneTimeToken.isEmpty()) {
                DTVTLogger.debug("set token = [" + mOneTimeToken + "]");
                //ワンタイムトークンをセット
                urlConnection.addRequestProperty(ONE_TIME_TOKEN_KEY, mOneTimeToken);
            }

            //コンテントタイプを指定する
            urlConnection.setRequestProperty(CONTENT_TYPE_KEY_TEXT, CONTENT_TYPE_TEXT);

            // UserAgentを設定
            urlConnection.setRequestProperty(USER_AGENT_KEY, UserAgentUtils.getCustomUserAgent());
            DTVTLogger.debug("Set UserAgent:" + UserAgentUtils.getCustomUserAgent());

            //ジャンルID、ロールIDはファイルDLのためGETメソッドリクエストする
            //POSTでJSONを送ることを宣言
            urlConnection.setRequestMethod(mRequestMethod);
            if (mRequestMethod.equals(REQUEST_METHOD_POST)) {
                urlConnection.setDoOutput(true);
            }
            urlConnection.setDoInput(true);
            urlConnection.setFixedLengthStreamingMode(sendParameterLength);

            //タイムアウト指定(0以下ならば、デフォルト値の30秒とする)
            if (mTimeOut > 0) {
                urlConnection.setConnectTimeout(mTimeOut);
                urlConnection.setReadTimeout(mTimeOut);
            } else {
                //接続タイムアウト
                urlConnection.setConnectTimeout(DtvtConstants.SERVER_CONNECT_TIMEOUT);
                //読み込みタイムアウト
                urlConnection.setReadTimeout(DtvtConstants.SERVER_READ_TIMEOUT);
            }
        }

        /**
         * パラメータをストリームに書き込む.
         *
         * @param urlConnection 書き込み対象のコネクション
         */
        void setPostData(final HttpURLConnection urlConnection) {
            if (urlConnection == null) {
                return;
            }
            // POSTデータ送信処理
            DataOutputStream dataOutputStream = null;
            try {
                dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                DTVTLogger.debug(String.format("RequestMethod[%s] mSendParameter[%s] size[%s]",
                        urlConnection.getRequestMethod(), mSendParameter, mSendParameter.length()));
                dataOutputStream.write(mSendParameter.getBytes(UTF8_CHARACTER_SET));
                dataOutputStream.flush();
            } catch (IOException e) {
                // POST送信エラー
                DTVTLogger.debug(e);
                //result="POST送信エラー";
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e1) {
                        DTVTLogger.debug(e1);
                    }
                }
            }
        }

        /**
         * サービストークン取得用のリダイレクト処理.
         *
         * @param newUrlString 飛び先URL
         * @param parameter    使用するパラメータ・使用しない場合はヌルか空文字
         */
        @SuppressWarnings("OverlyLongMethod")
        private void gotoRedirect(final String newUrlString, final String parameter) {
            DTVTLogger.start();

            mSendParameter = parameter;

            //タイムアウト判定用
            boolean isTimeout = false;
            long startTime = System.currentTimeMillis();

            HttpURLConnection urlConnection = null;
            try {
                //指定された名前でURLを作成する
                URL url = new URL(newUrlString);

                String buildTarget = BuildConfig.FLAVOR + BuildConfig.BUILD_TYPE;
                if (ContentUtils.LOCAL_SIGNED_ON.equals(buildTarget)) {
                    //指定された名前で開く
                    urlConnection = (HttpURLConnection) url.openConnection();
                } else {
                    //指定された名前で開く
                    urlConnection = (HttpsURLConnection) url.openConnection();
                }

                //事前設定パラメータのセット
                setParametersRedirect(urlConnection);

                //コンテキストがあればSSL証明書失効チェックを行う
                if (mContext != null) {
                    DTVTLogger.debug(mSourceUrl);
//                    //SSL証明書失効チェックライブラリの初期化を行う
//                    OcspUtil.init(mContext);
//
//                    //通信開始時にSSL証明書失効チェックを併せて行う
//                    OcspURLConnection ocspURLConnection = new OcspURLConnection(urlConnection);
//                    ocspURLConnection.connect();
                } else {
                    DTVTLogger.debug("SSL check error");
                    //コンテキストが無くて証明書チェックが行えないならば、SSLチェック初期化失敗の例外を投げる
                    throw new SSLPeerUnverifiedException(NO_CONTEXT_ERROR);
                }

                if (!TextUtils.isEmpty(mSendParameter)) {
                    //パラメータを送る
                    setPostDataRedirect(urlConnection);
                }

                //結果を読み込む
                int status = urlConnection.getResponseCode();

                //新たな飛び先を取得する
                String newUrl = urlConnection.getHeaderField(REDIRECT_JUMP_URL_GET);

                //newUrlがヌルならば、以後の続行は不能となる。通常は発生しない。
                if (newUrl == null) {
                    DTVTLogger.debug("newUrl = null");
                    CookieStore cookieStore = mCookieManager.getCookieStore();
                    mCookies = cookieStore.getCookies();
                    DTVTLogger.debug("cookies=" + mCookies);

                    throw new ConnectException(NO_REDIRECT_URL_ERROR);
                }

                DTVTLogger.debug("newUrl=" + newUrl);

                //リダイレクトのステータスを判定
                if (isRedirectCode(status)) {
                    if (newUrl.contains( ServiceTokenClient.getOkUrlString())
                            || newUrl.contains(ServiceTokenClient.getNgUrlString())) {
                        //リダイレクトで、ロケーションがOKかNGに指定したURLならば、サービストークン取得処理へ遷移
                        getServiceTokenAnswer(newUrl);
                        //コネクションを閉じる
                        urlConnection.disconnect();
                        removeConnections(urlConnection);
                    } else {
                        //クッキーの退避
                        CookieStore cookieStore = mCookieManager.getCookieStore();
                        mCookies = cookieStore.getCookies();

                        //コネクションを閉じる
                        urlConnection.disconnect();
                        removeConnections(urlConnection);

                        //リダイレクトかつ、OKとNGのURLではないならば、取得したURLで再度呼び出し
                        gotoRedirect(newUrl, "");
                    }
                }

                removeConnections(urlConnection);
            } catch (ConnectException e) {
                DTVTLogger.warning("ConnectException");
                //通信エラー扱いとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.NETWORK_ERROR);

                //コネクト時のタイムアウトはこちらに来るので、判定する
                if (System.currentTimeMillis() - startTime
                        > DtvtConstants.SERVER_CONNECT_TIMEOUT) {
                    //エラーメッセージにタイムアウトを示唆する物があればタイムアウトとして扱う
                    isTimeout = true;
                    DTVTLogger.debug("timeout ConnectException:" + e.getMessage());
                } else {
                    isTimeout = false;
                    DTVTLogger.debug("normal ConnectException" + e.getMessage());
                }
                //タイムアウトエラーの設定
                mReturnCode.errorState.setIsTimeout(isTimeout);
            } catch (SSLHandshakeException e) {
                DTVTLogger.warning("SSLHandshakeException");
                //SSL証明書が失効している
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SSL_ERROR);
                DTVTLogger.debug(e);
            } catch (SSLPeerUnverifiedException e) {
                DTVTLogger.warning("SSLPeerUnverifiedException");
                //SSLチェックライブラリの初期化が行われていない
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SSL_ERROR);
                DTVTLogger.debug(e);
            } catch (SocketTimeoutException e) {
                //タイムアウトを明示して取得
                DTVTLogger.warning("SocketTimeoutException");
                DTVTLogger.debug("SocketTimeoutException normal ConnectException:"
                        + e.getMessage());
                //サーバーエラー扱いとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.SERVER_ERROR);
                //タイムアウト判定用
                mReturnCode.errorState.setIsTimeout(true);
                DTVTLogger.debug(e);
            } catch (IOException e) {
                DTVTLogger.warning("IOException");
                //サーバーエラー扱いとする
                mReturnCode.errorState.setErrorType(
                        DtvtConstants.ErrorType.SERVER_ERROR);
                DTVTLogger.debug(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                //最後なので初期化
                urlConnection = null;
            }

            DTVTLogger.end();
        }

        /**
         * HTTPリクエスト用のパラメータを指定する.
         *
         * @param urlConnection コネクション
         * @throws ProtocolException プロトコルエクセプション
         */
        void setParametersRedirect(final HttpURLConnection urlConnection) throws ProtocolException {
            //コンテントタイプを指定する
            urlConnection.setRequestProperty(CONTENT_TYPE_KEY_TEXT,
                    ONE_TIME_TOKEN_GET_CONTENT_TYPE);
            //UserAgentを設定する
            urlConnection.setRequestProperty(USER_AGENT_KEY, UserAgentUtils.getCustomUserAgent());
            DTVTLogger.debug("Set UserAgent:" + UserAgentUtils.getCustomUserAgent());

            if (TextUtils.isEmpty(mSendParameter)) {
                //パラメータをgetで送る
                urlConnection.setRequestMethod(REQUEST_METHOD_GET);

            } else {
                //パラメータをpostで送る
                urlConnection.setRequestMethod(REQUEST_METHOD_POST);

                //送る文字列長の算出
                byte[] sendParameterByte = mSendParameter.getBytes(StandardCharsets.UTF_8);
                int sendParameterLength = sendParameterByte.length;

                urlConnection.setDoInput(true);
                urlConnection.setFixedLengthStreamingMode(sendParameterLength);
            }

            //接続タイムアウト
            urlConnection.setConnectTimeout(DtvtConstants.SERVER_CONNECT_TIMEOUT);
            //読み込みタイムアウト
            urlConnection.setReadTimeout(DtvtConstants.SERVER_READ_TIMEOUT);

            //自動リダイレクトを無効化する
            HttpsURLConnection.setDefaultAllowUserInteraction(false);
            urlConnection.setInstanceFollowRedirects(false);

            //クッキー情報の有無を検査
            if (mCookies != null) {
                //蓄積してあるクッキー情報を書き込む
                CookieStore cookieStore = mCookieManager.getCookieStore();
                for (int counter = 0; counter < mCookies.size(); counter++) {
                    DTVTLogger.debug("Cookie(" + counter + ")=" + mCookies.get(counter));
                    cookieStore.add(null, mCookies.get(counter));
                }

                //次に備えてクッキー情報は初期化する
                mCookies = null;
            }
        }

        /**
         * パラメータをストリームに書き込む.
         *
         * @param urlConnection 書き込み対象のコネクション
         */
        void setPostDataRedirect(final HttpURLConnection urlConnection) {
            if (urlConnection == null) {
                return;
            }

            if (mSendParameter == null) {
                return;
            }

            // POSTデータ送信処理
            DataOutputStream dataOutputStream = null;
            try {
                dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                DTVTLogger.debug(String.format("RequestMethod[%s] mSendParameter[%s] size[%s]",
                        urlConnection.getRequestMethod(), mSendParameter, mSendParameter.length()));
                dataOutputStream.write(mSendParameter.getBytes(UTF8_CHARACTER_SET));
                dataOutputStream.flush();
            } catch (IOException e) {
                // POST送信エラー
                DTVTLogger.debug(e);
                //result="POST送信エラー";
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e1) {
                        DTVTLogger.debug(e1);
                    }
                }
            }
        }

        /**
         * 返ってきたロケーションが最初にこちらで指定したURLだった場合の処理.
         *
         * @param location 取得したロケーション
         */
        private void getServiceTokenAnswer(final String location) {
            //クッキー情報のバッファ
            String serviceToken = "";
            long serviceTokenMaxAge = 0;

            //事前に判定しないと、なぜかfalse時の動作が不正だった
            boolean locationCheck = location.contains(ServiceTokenClient.getOkUrlString());

            //OK URLが含まれているかどうか
            if (locationCheck && mCookieManager != null) {
                //正常にサービストークンが取得できた場合はクッキーを取得
                CookieStore cookieStore = mCookieManager.getCookieStore();
                List<HttpCookie> cookies = cookieStore.getCookies();

                //取得したクッキーの数だけ回る
                for (HttpCookie cookie : cookies) {
                    //サービストークンを見つける
                    if (cookie.getName().equals(SERVICE_TOKEN_KEY_NAME)) {
                        OneTimeTokenData oneTimeTokenData = new OneTimeTokenData();

                        //見つけたので蓄積
                        serviceToken = cookie.getValue();
                        serviceTokenMaxAge = cookie.getMaxAge();

                        //取得した時間は生存秒数なので、ミリ秒の無効化予定時間を算出する
                        serviceTokenMaxAge = (serviceTokenMaxAge * 1000)
                                + DateUtils.getNowTimeFormatEpoch();

                        oneTimeTokenData.setOneTimeToken(serviceToken);
                        oneTimeTokenData.setOneTimeTokenGetTime(serviceTokenMaxAge);

                        //プリファレンスに書き込み
                        SharedPreferencesUtils.setOneTimeTokenData(mContext, oneTimeTokenData);

                        DTVTLogger.debug("Service Token Get");
                        break;
                    }
                }

                if (TextUtils.isEmpty(serviceToken)) {
                    //サービストークンは見つからなかったので、トークンエラーとする
                    mReturnCode.errorState.setErrorType( DtvtConstants.ErrorType.TOKEN_ERROR);
                } else {
                    //サービストークン取得では使用しないが、データを格納しなければエラー扱いになるので、トークンを入れておく
                    mAnswerBuffer = serviceToken;
                }
            } else {
                //トークン取得以前の問題なので、通信エラーとする
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.HTTP_ERROR);
            }
        }
    }

    /**
     * ボディ部の読み込みを行う.
     *
     * @param statusCode コネクションの際のステータス
     * @return 読み込んだボディ部
     */
    private String readConnectionBody(final int statusCode) {
        if (statusCode != HttpURLConnection.HTTP_OK) {
            //HTTP通信エラーとして元に返す
            mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.HTTP_ERROR);
            mReturnCode.errorState.setErrorCode(String.valueOf(statusCode));
            DTVTLogger.debug(String.format("statusCode[%s]", statusCode));
            return "";
        }

        //コネクトに成功したので、控えておく
        addUrlConnections();

        StringBuilder stringBuilder = null;
        InputStream stream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            //ボディー部の読み込み用ストリームを開く
            stream = mUrlConnection.getInputStream();
            stringBuilder = new StringBuilder();
            String lineBuffer;
            inputStreamReader = new InputStreamReader(stream, UTF8_CHARACTER_SET);
            bufferedReader = new BufferedReader(inputStreamReader);

            //内容が尽きるまで蓄積する
            while ((lineBuffer = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineBuffer);
                stringBuilder.append("\n");
            }

        } catch (UnsupportedEncodingException e) {
            //サーバーエラーとして元に返す
            mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SERVER_ERROR);
            DTVTLogger.debug(e);
        } catch (IOException e) {
            //全通信停止発行済みならば、正常な動作となる
            if (!mIsStopAllConnections) {
                //通信停止ではないので、通信エラー
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SERVER_ERROR);
                DTVTLogger.debug(e);
            }
        } finally {
            //ストリームを閉じる
            streamCloser(stream, inputStreamReader, bufferedReader);
        }

        //蓄積したボディー部を返す
        String bodyData = "";
        if (stringBuilder != null) {
            bodyData = stringBuilder.toString();
        }
        DTVTLogger.debug(String.format("bodyData[%s]", bodyData));
        return bodyData;
    }

    /**
     * コネクションを蓄積して、後で止められるようにする.
     */
    private void addUrlConnections() {
        //通信が終わり、ヌルが入れられる場合に備えたヌルチェック
        if (mUrlConnections == null) {
            //既に削除されていたので、再度確保を行う
            mUrlConnections = new ArrayList<>();
        }
        if (mUrlConnection != null) {
            //HTTPコネクションを追加する
            mUrlConnections.add(mUrlConnection);
        }
    }

    /**
     * 各ストリームを閉じる.
     *
     * @param stream            コネクションから取得したストリーム
     * @param inputStreamReader UTF-8を指定したストリーム
     * @param bufferedReader    バッファーストリーム
     */
    private void streamCloser(final InputStream stream,
                              final InputStreamReader inputStreamReader,
                              final BufferedReader bufferedReader) {

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                //クローズ失敗はサーバー
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SERVER_ERROR);
                DTVTLogger.debug(e);
            }
        }
        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                //クローズ失敗はサーバーエラー
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SERVER_ERROR);
                DTVTLogger.debug(e);
            }
        }
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                //クローズ失敗はサーバーエラー
                mReturnCode.errorState.setErrorType(DtvtConstants.ErrorType.SERVER_ERROR);
                DTVTLogger.debug(e);
            }
        }
    }

    /**
     * 切断済みコネクションを蓄積から削除する.
     *
     * @param connection 削除したいコネクション
     */
    static private void removeConnections(final HttpURLConnection connection) {
        if (mUrlConnections != null) {
            mUrlConnections.remove(connection);
        }
    }

    /**
     * ステータスが300番台かどうかのチェック.
     * (リダイレクト判定がレコメンドの認証にも必要になったので流用)
     *
     * @param status HTTPステータス
     * @return 300番台ならばtrue
     */
    public static boolean isRedirectCode(final int status) {
        switch (status) {
            case HttpURLConnection.HTTP_MULT_CHOICE:
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
            case HttpURLConnection.HTTP_NOT_MODIFIED:
            case HttpURLConnection.HTTP_USE_PROXY:
                return true;
        }

        return false;
    }

    /**
     * 全ての通信を遮断する.
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    protected synchronized void stopAllConnections() {
        DTVTLogger.start();
        if (mIsStopAllConnections) {
            return;
        }
        //各通信タスクにキャンセルを通知する
        mIsStop = true;
        if (mCommunicationTaskAPI != null) {
            mCommunicationTaskAPI.cancel(true);
        }
        if (mCommunicationTaskExtra != null) {
            mCommunicationTaskExtra.cancel(true);
        }
        if (mCommunicationTaskOtt != null) {
            mCommunicationTaskOtt.cancel(true);
        }

        if (mUrlConnections == null) {
            return;
        }

        //全ての通信を止めることを宣言する
        mIsStopAllConnections = true;

        //ConcurrentModificationException 抑制のため synchronized 化
        final List<HttpURLConnection> urlConnections = new ArrayList<>(mUrlConnections);
        synchronized (urlConnections) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < urlConnections.size(); i++) {
                        //全てのコネクションにdisconnectを送る
                        final HttpURLConnection stopConnection = urlConnections.get(i);
                        if (stopConnection != null) {
                            stopConnection.disconnect();
                        }
                    }
                    urlConnections.clear();
                }
            }).start();
        }
    }

    /**
     * 指定したAPIをワンタイムトークン付きで通信を開始する.
     *
     * @param sourceUrl               API呼び出し名
     * @param receivedParameters      API呼び出し用パラメータ
     * @param webApiBasePlalaCallback 結果のコールバック
     * @param extraDataSrc            拡張情報（使用しないときはヌルをセット）
     */
    protected void openUrlAddOtt(final String sourceUrl, final String receivedParameters,
                       final WebApiBasePlalaCallback webApiBasePlalaCallback,
                       final Bundle extraDataSrc) {
        //タスクを作成する
        mCommunicationTaskOtt = new CommunicationTask(sourceUrl, receivedParameters,
                extraDataSrc, true, getRequestMethod());

        //呼び出し元に戻るコールバックの準備
        mWebApiBasePlalaCallback = webApiBasePlalaCallback;

        //ワンタイムトークン専用のコールバックを作成する
        ServiceTokenErrorCallback serviceTokenErrorCallback = new ServiceTokenErrorCallback() {
            @Override
            public void onTokenError(final ReturnCode returnCode) {
                //ワンタイムトークン側のエラー情報を、WebApi側にも反映する
                mReturnCode = returnCode;

                //呼び出し元にエラーを伝える
                mWebApiBasePlalaCallback.onError(returnCode);
            }
        };

        //ワンタイムトークンの情報を取得する
        OneTimeTokenData oneTimeTokenData = SharedPreferencesUtils.getOneTimeTokenData(mContext);

        //ワンタイムトークンの期限切れ確認
        if (oneTimeTokenData.getOneTimeTokenGetTime()
                < DateUtils.getNowTimeFormatEpoch()) {
            //期限切れなので、ワンタイムパスワードの取得を起動
            getOneTimePassword(mContext, serviceTokenErrorCallback);
        } else {
            //有効なワンタイムトークンなので、そのまま使用して処理を呼び出す
            mCommunicationTaskOtt.setOneTimeToken(oneTimeTokenData.getOneTimeToken());
            ReturnCode returnCode = new ReturnCode();
            DTVTLogger.debug("******mCommunicationTaskOtt.execute at openUrlAddOtt");
            mCommunicationTaskOtt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, returnCode);
        }
    }

    /**
     * ワンタイムトークンを取得する為に、dアカウント設定アプリからワンタイムパスワードを取得する.
     *
     * @param context コンテキスト
     *@param serviceTokenErrorCallback   サービストークンエラーコールバック
     */
    private void getOneTimePassword(final Context context,
                                    final ServiceTokenErrorCallback serviceTokenErrorCallback) {
        DTVTLogger.debug("”execDaccountGetOTT” getOneTimePassword");
        //認証画面の表示状況のインスタンスの取得
        final OttGetAuthSwitch ottGetAuthSwitch = OttGetAuthSwitch.INSTANCE;

        //ワンタイムパスワードの取得(未認証時は認証画面への遷移を能動制御するように変更)
        mGetOtt = new DaccountGetOtt();
        mGetOtt.execDaccountGetOTT(context, ottGetAuthSwitch.isNowAuth(), new DaccountGetOtt.DaccountGetOttCallBack() {
            @Override
            public void getOttCallBack(final int result, final String id,
                                       final String oneTimePassword) {
                //ワンタイムパスワードが取得できたかどうかを見る
                if (result == IDimDefines.RESULT_USER_CANCEL) {
                    //キャンセルされたので、ログアウトダイアログを表示する
                    ottGetAuthSwitch.showLogoutDialog();
                    return;
                }

                //ワンタイムトークンが期限内ならば、そのまま使用する
                OneTimeTokenData tokenData = SharedPreferencesUtils.getOneTimeTokenData(mContext);

                //期限内ならば、そのまま使用する
                if (tokenData.getOneTimeTokenGetTime() > DateUtils.getNowTimeFormatEpoch()) {
                    //取得済みのトークンを使用する
                    mCommunicationTaskOtt.setOneTimeToken(tokenData.getOneTimeToken());

                    //結果格納構造体の作成
                    ReturnCode returnCode = new ReturnCode();

                    //ワンタイムトークンの取得結果を元にして、通信を開始する
                    DTVTLogger.debug("******mCommunicationTaskOtt.execute at getOttCallBack");
                    mCommunicationTaskOtt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, returnCode);
                    return;
                }

                //ワンタイムトークンの取得を行う
                getServiceToken(mContext, mCommunicationTaskOtt,
                        oneTimePassword, serviceTokenErrorCallback);
            }
        });
    }

    /**
     * ワンタイムトークンの取得を行う.
     *
     * @param context           　コンテキスト
     * @param communicationTask 通信処理クラス
     * @param oneTimePassword   ワンタイムパスワード
     * @param serviceTokenErrorCallback サービストークンエラーコールバック
     */
    private static void getServiceToken(final Context context,
                                        final CommunicationTask communicationTask,
                                        final String oneTimePassword,
                                        final ServiceTokenErrorCallback serviceTokenErrorCallback) {
        //ワンタイムトークンとその取得時間を取得する
        final ServiceTokenClient tokenClient = new ServiceTokenClient(context);

        boolean answer = tokenClient.getServiceTokenApi(oneTimePassword,
                new ServiceTokenClient.TokenGetCallback() {
                    @Override
                    public void onTokenGot(final boolean successFlag) {
                        //結果に値があるかを確認
                        if (successFlag) {
                            //値があったので、セット
                            communicationTask.setOneTimeToken(
                                    SharedPreferencesUtils.
                                            getOneTimeTokenData(context).getOneTimeToken());

                            ReturnCode returnCode = new ReturnCode();
                            //既に実行されたかどうかの判定
                            if (communicationTask.getStatus().equals(AsyncTask.Status.PENDING)) {
                                //実行されていないので、ワンタイムトークンの取得結果を元にして、通信を開始する
                                communicationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, returnCode);
                            } else {
                                //既に実行されていたので、処理をスキップ
                                //(2重起動はsuccessFlagがfalseの場合にしか確認していないので、実行されない筈ですが)
                                DTVTLogger.debug("communicationTask already exec");
                            }

                        } else {
                            //値が無いのでリセット
                            communicationTask.setOneTimeToken("");

                            //通信エラーとして処理を終了する
                            if (serviceTokenErrorCallback != null) {
                                DTVTLogger.debug("token get failed ");
                                //通信エラーのエラーコードを作成する
                                ReturnCode returnCode = new ReturnCode();

                                //SSLエラーはSSLエラーとして返す
                                if (tokenClient.getError().getErrorType()
                                        == DtvtConstants.ErrorType.SSL_ERROR) {
                                    //SSLエラーなのでSSLエラーとして返す
                                    returnCode.errorState.setErrorType(
                                            DtvtConstants.ErrorType.SSL_ERROR);
                                } else {
                                    //それ以外はトークンエラー
                                    returnCode.errorState.setErrorType(
                                            DtvtConstants.ErrorType.TOKEN_ERROR);
                                }
                                //呼び出し元にエラーを伝える
                                serviceTokenErrorCallback.onTokenError(returnCode);
                            }
                        }
                    }
                }
        );

        DTVTLogger.debug("getServiceToken answer = " + answer);

        if (!answer) {
            //dアカウントが設定されていない場合はこちらが動作する

            //結果格納構造体の作成
            ReturnCode returnCode = new ReturnCode();

            DTVTLogger.debug("communicationTask answer false = " + communicationTask);

            //ワンタイムトークンは取得できなかったので、そのまま通信を開始する
            //communicationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, returnCode);

            //ワンタイムトークンが必要なAPIでトークンの取得に失敗した場合は、エラーとする
            communicationTask.setOneTimeToken("");
            //トークンエラーとする
            returnCode.errorState.setErrorType(DtvtConstants.ErrorType.TOKEN_ERROR);
            //呼び出し元にエラーを伝える
            serviceTokenErrorCallback.onTokenError(returnCode);
        }
    }

    /**
     * リクエストメソッド取得.
     * POSTリクエストしない場合はオーバライドしてメソッド名を返却する事.
     *
     * @return メソッド名
     */
    protected String getRequestMethod() {
        return REQUEST_METHOD_POST;
    }

    /**
     * WebAPIのエラー情報を取得する.
     *
     * @return エラー情報クラス
     */
    public ErrorState getError() {
        DTVTLogger.start();
        DTVTLogger.debug("get error statue=" + mReturnCode.errorState.getErrorType());
        //エラーメッセージの取得を行う
        mReturnCode.errorState.addErrorMessage(mContext);
        DTVTLogger.debug("get error message=" + mReturnCode.errorState.getErrorMessage());
        DTVTLogger.end();
        //エラーコードを返す
        return mReturnCode.errorState;
    }

    /**
     * パラメータの比較用などの為に、与えられた文字列をひとまとめにする.
     *
     * @param strings ひとまとめにしたい文字列
     * @return ひとまとめになった文字列
     */
    protected List<String> makeStringArry(final String... strings) {
        return Arrays.asList(strings);
    }

    /**
     * 指定したAPIで通信を開始する.
     *
     * @param sourceUrl               API呼び出し名
     * @param receivedParameters      API呼び出し用パラメータ
     * @param webApiBasePlalaCallback コールバック
     */
    protected void openUrl(final String sourceUrl, final String receivedParameters,
                 final WebApiBasePlalaCallback webApiBasePlalaCallback) {

        mCommunicationTaskAPI = new CommunicationTask(sourceUrl, receivedParameters, getRequestMethod());

        //コールバックの準備
        mWebApiBasePlalaCallback = webApiBasePlalaCallback;

        //結果格納構造体の作成
        ReturnCode returnCode = new ReturnCode();

        //通信本体の開始
        mCommunicationTaskAPI.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, returnCode);
    }

    /**
     * JSON parseのエラーが発生された場合、エラーの情報を更新する
     * @param jsonParseError
     */
    public void setJsonParseError(ErrorState jsonParseError) {
        if (jsonParseError != null) {
            mReturnCode.errorState.setErrorType(jsonParseError.getErrorType());
            mReturnCode.errorState.setErrorCode(jsonParseError.getErrorCode());
            mReturnCode.errorState.setErrorMessage(jsonParseError.getErrorMessage());
        }
    }

    /**
     * 文字列の日付判定.
     *
     * @param dateString 日付(yyyyMMdd)であることが期待される文字列
     * @return 日付ならばtrue
     */
    protected boolean checkDateString(final String dateString) {
        //日付フォーマットの設定
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.JAPAN);
        dateFormat.setLenient(false);
        Date parsedDate;
        try {
            //日付変換
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            //例外が発生したならば、当然日付ではない
            return false;
        }

        //通常の問題では例外が発生するはずだが、念のため日付変換の前後で値を比べて、違っていた場合は不正な値とみなす。
        String temporaryDateString = dateFormat.format(parsedDate);
        return temporaryDateString.equals(dateString);
    }

    /**
     * パラメータの比較用などの為に、与えられた数字をひとまとめにする.
     *
     * @param ints ひとまとめにしたい数字
     * @return ひとまとめになった数字
     */
    protected List<Integer> makeIntArry(final Integer... ints) {
        return Arrays.asList(ints);
    }
}