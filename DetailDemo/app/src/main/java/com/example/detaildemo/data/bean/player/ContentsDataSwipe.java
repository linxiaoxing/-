package com.example.detaildemo.data.bean.player;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * コンテンツデータ管理クラス(詳細画面スワイプ動作用).
 */
public class ContentsDataSwipe implements Serializable{

    private static final long serialVersionUID = -1L;

    /**メインタイトル.*/
    private String mTitle = null;
    /**サムネイルURL(リスト).*/
    private String mThumListURL = null;
    /**チャンネルアイコンURL.*/
    private String mTxtChannellogoURL;
    /**チャンネル名.*/
    private String mChannelName = null;
    /**サービスIDユニーク.*/
    private String mServiceIdUniq = null;

    /**
     * タイトル取得.
     * @return タイトル
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * タイトル設定.
     * @param title タイトル
     */
    public void setTitle(final String title) {
        this.mTitle = title;
    }

    /**
     * サムネイルURL(リスト)取得.
     * @return サムネイルURL(リスト)
     */
    public String getThumURL() {
        return mThumListURL;
    }

    /**
     * サムネイルURL(リスト)設定.
     * @param thumURL サムネイルURL(リスト)
     */
    public void setThumURL(final String thumURL) {
        this.mThumListURL = thumURL;
    }

    /**
     * チャンネルアイコンURL取得.
     * @return チャンネルアイコンURL
     */
    public String getTxtChannellogoURL() {
        return mTxtChannellogoURL;
    }

    /**
     *  チャンネルアイコンURL設定.
     * @param txtChannellogoURL チャンネルアイコンURL
     */
    public void setTxtChannellogoURL(String txtChannellogoURL) {
        this.mTxtChannellogoURL = txtChannellogoURL;
    }

    /**
     * チャンネル名取得.
     * @return チャンネル名
     */
    public String getChannelName() {
        return mChannelName;
    }

    /**
     * チャンネル名設定.
     * @param channelName チャンネル名
     */
    public void setChannelName(final String channelName) {
        this.mChannelName = channelName;
    }

    /**
     * サービスIDユニーク取得.
     * @return サービスIDユニーク
     */
    public String getServiceIdUniq() {
        return mServiceIdUniq;
    }

    /**
     * サービスIDユニーク設定.
     * @param mServiceIdUniq サービスIDユニーク
     */
    public void setServiceIdUniq(final String mServiceIdUniq) {
        this.mServiceIdUniq = mServiceIdUniq;
    }

    /**
     * コンテンツデータ管理クラスリスト取得.
     * @return コンテンツデータ管理クラスリスト
     */
    public static ArrayList<ContentsDataSwipe> getCreatedContentsData() {
        ArrayList<ContentsDataSwipe> arrayList = new ArrayList<>();
        ContentsDataSwipe contentsDataSwipe1 = new ContentsDataSwipe();
        contentsDataSwipe1.setTitle("あさイチ");
        contentsDataSwipe1.setChannelName("ＮＨＫ総合１・東京");
        contentsDataSwipe1.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201024/201024_201912160815.webp");
        contentsDataSwipe1.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0400.webp");
        contentsDataSwipe1.setServiceIdUniq("ttb_0400");
        arrayList.add(contentsDataSwipe1);

        ContentsDataSwipe contentsDataSwipe2 = new ContentsDataSwipe();
        contentsDataSwipe2.setTitle("ふしぎエンドレス");
        contentsDataSwipe2.setChannelName("ＮＨＫＥテレ１東京");
        contentsDataSwipe2.setThumURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0408.webp");
        contentsDataSwipe2.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0408.webp");
        contentsDataSwipe2.setServiceIdUniq("ttb_0408");
        arrayList.add(contentsDataSwipe2);

        ContentsDataSwipe contentsDataSwipe3 = new ContentsDataSwipe();
        contentsDataSwipe3.setTitle("スッキリ");
        contentsDataSwipe3.setChannelName("日テレ１");
        contentsDataSwipe3.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201040/201040_201912160800.webp");
        contentsDataSwipe3.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0410.webp");
        contentsDataSwipe3.setServiceIdUniq("ttb_0410");
        arrayList.add(contentsDataSwipe3);

        ContentsDataSwipe contentsDataSwipe4 = new ContentsDataSwipe();
        contentsDataSwipe4.setTitle("羽鳥慎一モーニングショー");
        contentsDataSwipe4.setChannelName("テレビ朝日");
        contentsDataSwipe4.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201064/201064_201912160800.webp");
        contentsDataSwipe4.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0428.webp");
        contentsDataSwipe4.setServiceIdUniq("ttb_0428");
        arrayList.add(contentsDataSwipe4);

        ContentsDataSwipe contentsDataSwipe5 = new ContentsDataSwipe();
        contentsDataSwipe5.setTitle("グッとラック！");
        contentsDataSwipe5.setChannelName("ＴＢＳ１");
        contentsDataSwipe5.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201048/201048_201912160800.webp");
        contentsDataSwipe5.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0418.webp");
        contentsDataSwipe5.setServiceIdUniq("ttb_0418");
        arrayList.add(contentsDataSwipe5);

        ContentsDataSwipe contentsDataSwipe6 = new ContentsDataSwipe();
        contentsDataSwipe6.setTitle("なないろ日和！");
        contentsDataSwipe6.setChannelName("テレビ東京1");
        contentsDataSwipe6.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201072/201072_201912160926.webp");
        contentsDataSwipe6.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0430.webp");
        contentsDataSwipe6.setServiceIdUniq("ttb_0430");
        arrayList.add(contentsDataSwipe6);

        ContentsDataSwipe contentsDataSwipe7 = new ContentsDataSwipe();
        contentsDataSwipe7.setTitle("ノンストップ！");
        contentsDataSwipe7.setChannelName("フジテレビ");
        contentsDataSwipe7.setThumURL("https://img.hikaritv-docomo.jp/ttb_thumbnail/448-252/201912/201056/201056_201912160950.webp");
        contentsDataSwipe7.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/0420.webp");
        contentsDataSwipe7.setServiceIdUniq("ttb_0420");
        arrayList.add(contentsDataSwipe7);

        ContentsDataSwipe contentsDataSwipe8 = new ContentsDataSwipe();
        contentsDataSwipe8.setTitle("キャシーのｂｉｇ Ｃ いま私にできること　シーズン２");
        contentsDataSwipe8.setChannelName("ＴＯＫＹＯ　ＭＸ１");
        contentsDataSwipe8.setThumURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/5c38.webp");
        contentsDataSwipe8.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ttb_ch_thumb/list/448-252/5c38.webp");
        contentsDataSwipe8.setServiceIdUniq("ttb_5c38");
        arrayList.add(contentsDataSwipe8);

        ContentsDataSwipe contentsDataSwipe9 = new ContentsDataSwipe();
        contentsDataSwipe9.setTitle("まるごと！北海道 ＃８ 十勝エリア");
        contentsDataSwipe9.setChannelName("ひかりＴＶチャンネル１");
        contentsDataSwipe9.setThumURL("https://img.hikaritv-docomo.jp/ch_thumb/list/448-252/101.webp");
        contentsDataSwipe9.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/ch_thumb/list/448-252/101.webp");
        contentsDataSwipe9.setServiceIdUniq("044d");
        arrayList.add(contentsDataSwipe9);

        ContentsDataSwipe contentsDataSwipe10 = new ContentsDataSwipe();
        contentsDataSwipe10.setTitle("BS1スペシャル");
        contentsDataSwipe10.setChannelName("ＮＨＫＢＳ１");
        contentsDataSwipe10.setThumURL("https://img.hikaritv-docomo.jp/bs_ch_thumb/list/448-252/0065.webp");
        contentsDataSwipe10.setTxtChannellogoURL("https://img.hikaritv-docomo.jp/bs_ch_thumb/list/448-252/0065.webp");
        contentsDataSwipe10.setServiceIdUniq("bs_0065");
        arrayList.add(contentsDataSwipe10);
        return arrayList;
    }
}
