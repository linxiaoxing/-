package com.example.detaildemo.data.provider.data;


import java.io.Serializable;

/**
 * 契約情報構造体.
 */
public class AccountList implements Serializable{

    /**
     * バージョン.
     */
    private static final long serialVersionUID = 5253124266346397317L;
    /**
     * 契約状態.
     */
    private String mContractStatus;
    /**
     * dTVチャンネル視聴年齢値.
     */
    private String mDchAgeReq;
    /**
     * ひかりTVfordocomo視聴年齢値.
     */
    private String mH4dAgeReq;
    /**
     * エリアコード.
     */
    private String mAreaCode;
    /**
     * STB名.
     */
    private String mStbName;

    /**
     * 初期化用コンストラクタ.
     */
    public AccountList() {
        this.mContractStatus = "";
        this.mDchAgeReq = "";
        this.mH4dAgeReq = "";
        this.mAreaCode = "";
        this.mStbName = null;
    }

    /**
     * 契約状態をゲットする.
     * @return 契約状態.
     */
    public String getContractStatus() {
        return mContractStatus;
    }
    /**
     * 契約状態を設定するする.
     * @param contractStatus 契約状態.
     */
    public void setContractStatus(final String contractStatus) {
        if (contractStatus != null) {
            this.mContractStatus = contractStatus;
        }
    }

    /**
     * 視聴年齢値ゲットする.
     * @return dTVチャンネル視聴年齢値
     */
    public String getDchAgeReq() {
        return mDchAgeReq;
    }
    /**
     * 視聴年齢値設定する.
     * @param dchAgeReq dTVチャンネル視聴年齢値
     */
    public void setDchAgeReq(final String dchAgeReq) {
        if (dchAgeReq != null) {
            this.mDchAgeReq = dchAgeReq;
        }
    }

    /**
     * 視聴年齢値ゲットする.
     * @return ひかりTVfordocomo視聴年齢値
     */
    public String getH4dAgeReq() {
        return mH4dAgeReq;
    }

    /**
     * 視聴年齢値設定する.
     * @param h4dAgeReq ひかりTVfordocomo視聴年齢値
     */
    public void setH4dAgeReq(final String h4dAgeReq) {
        if (h4dAgeReq != null) {
            this.mH4dAgeReq = h4dAgeReq;
        }
    }

    /**
     * エリアコードを取得する.
     * @return エリアコード
     */
    public String getAreaCode() { return mAreaCode; }

    /**
     * エリアコードを設定する.
     * @param areaCode エリアコード
     */
    public void setAreaCode(final String areaCode) {
        if (areaCode != null) {
            this.mAreaCode = areaCode;
        }
    }

    /**
     * STB名を取得
     * @return STB名
     */
    public String getStbName() {
        return mStbName;
    }

    /**
     * STB名を設定
     * @param stbName : STB名
     */
    public void setStbName(String stbName) {
        this.mStbName = stbName;
    }
}
