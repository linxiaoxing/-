package com.example.detaildemo.data.provider.data;


import java.io.Serializable;
import java.util.List;

/**
 * ユーザ情報永続化用構造体.
 */
public class SerializablePreferencesData implements Serializable{

    /**
     * Serializableバージョン.
     */
    private static final long serialVersionUID = -2672265264244417610L;
    /**
     * ユーザ情報リスト.
     */
    private List<UserInfoList> mUserInfoList;

    /**
     * ユーザ情報リスト取得.
     *
     * @return ユーザ情報リスト
     */
    public List<UserInfoList> getUserInfoList() {
        return mUserInfoList;
    }

    /**
     * ユーザ情報リスト保存.
     *
     * @param mUserInfoList ユーザ情報リスト
     */
    public void setUserInfoList(final List<UserInfoList> mUserInfoList) {
        this.mUserInfoList = mUserInfoList;
    }
}

