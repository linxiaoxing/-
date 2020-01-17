package com.example.detaildemo.data.webapiclient;


import com.example.detaildemo.activity.BaseActivity;
import com.example.detaildemo.common.DTVTLogger;

/**
 * dアカウント認証画面の表示制御.
 */
public enum OttGetAuthSwitch {
    /**
     * シングルトン制御.
     */
    INSTANCE;
    /**
     * シングルトン制御field.
     */
    private String mField;

    /**
     * シングルトン制御用文字列.
     */
    private static final String SINGLETON_TEXT = "SINGLETON_TEXT";

    /**
     * dアカウントの認証画面を出すならばtrue
     */
    private boolean mNowAuth;

    /**
     * ダイアログを出すためのアクティビティ
     */
    private BaseActivity mActivity;

    /**
     * dアカウント認証キャンセル後のダイアログを、パーミッションチェックのダイアログよりも優先させるフラグ.
     */
    private boolean mSkipPermission = false;

    /**
     * コンストラクタ
     */
    OttGetAuthSwitch() {
        //最初は認証画面を出す
        mNowAuth = true;
        //アクティビティ初期化
        mActivity = null;
    }

    /**
     * クラスのインスタンス取得.
     *
     * @return インスタンス
     */
    @SuppressWarnings("SameReturnValue")
    public static OttGetAuthSwitch getInstance() {
        //enumならばこれでシングルトン制御が可能
        if (INSTANCE.mField == null) {
            INSTANCE.mField = SINGLETON_TEXT;
        }

        return INSTANCE;
    }

    /**
     * 認証画面を出すか否かの取得.
     *
     * @return 認証画面を出すならばtrue
     */
    public boolean isNowAuth() {
        if (mNowAuth) {
            //認証画面を出すのは最初のdアカウント操作だけで良いので、falseを変数に入れてからtrueを返す
            mNowAuth = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 認証画面表示スイッチのセット.
     *
     * @param nowAuth 認証画面表示スイッチの内容
     */
    public void setNowAuth(boolean nowAuth) {
        //処理は移譲
        setNowAuth(nowAuth, null);
    }

    /**
     * 認証画面表示スイッチのセット.
     *
     * @param nowAuth  認証画面表示スイッチの内容
     * @param activity アクティビティ・ヌルならば更新しない
     */
    public void setNowAuth(boolean nowAuth, BaseActivity activity) {
        //認証画面表示スイッチのセット
        mNowAuth = nowAuth;

        if (activity != null) {
            //アクティビティの退避
            mActivity = activity;
        }
    }

    /**
     * ログアウトのダイアログを表示
     */
    public void showLogoutDialog() {
        //ベースアクティビティを保持していれば、ログアウトダイアログを出す
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.showLogoutDialog();
                }
            });
        }
    }

    /**
     * dアカウント設定アプリ未インストール時のダイアログを呼び出す
     */
    public void showDAccountApliNotFoundDialog() {
        //ベースアクティビティを保持していれば、dアカウント設定アプリ未インストールダイアログを出す
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.showDAccountApliNotFoundDialog();
                }
            });
        }
    }

    /**
     * アクティビティのゲッター
     * @return 取得済みアクティビティ
     */
    public BaseActivity getActivity() {
        return mActivity;
    }

    /**
     * パーミッションチェックスキップフラグのゲッター.
     *
     * @return パーミッションチェックスキップフラグの内容
     */
    public boolean isSkipPermission() {
        DTVTLogger.start("get = " + mSkipPermission);
        return mSkipPermission;
    }

    /**
     * パーミッションチェックスキップフラグのセッター.
     *
     * @param skipPermission パーミッションチェックスキップフラグに入れたい内容
     */
    public void setSkipPermission(boolean skipPermission) {
        DTVTLogger.start("set = " + skipPermission);
        mSkipPermission = skipPermission;
        DTVTLogger.end();
    }

}

