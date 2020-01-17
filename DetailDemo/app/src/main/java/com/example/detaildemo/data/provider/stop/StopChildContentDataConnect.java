package com.example.detaildemo.data.provider.stop;

import android.os.AsyncTask;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.provider.ChildContentDataProvider;

/**
 * ウィザード画面のデータ取得の通信を止める.
 *
 */
public class StopChildContentDataConnect extends AsyncTask<ChildContentDataProvider, Void, Void>{
    /**
     * コンストラクタ.
     */
    public StopChildContentDataConnect() {
        DTVTLogger.start();
    }

    @Override
    protected Void doInBackground(final ChildContentDataProvider... dataProviders) {
        DTVTLogger.start();
        //通信を行っている処理を止める
        if (dataProviders != null) {
            for (ChildContentDataProvider childContentDataProvider : dataProviders) {
                if (childContentDataProvider != null) {
                    childContentDataProvider.stopConnect();
                }
            }
        }
        return null;
    }
}