package com.example.detaildemo.data.provider.stop;


import android.os.AsyncTask;

import com.example.detaildemo.adapter.ContentsAdapter;
import com.example.detaildemo.common.DTVTLogger;

/**
 * アダプタで行っている通信を止める.
 */
public class StopContentsAdapterConnect extends AsyncTask<ContentsAdapter, Void, Void>{
    /**
     * コンストラクタ.
     */
    public StopContentsAdapterConnect() {
        DTVTLogger.start();
    }

    @Override
    protected Void doInBackground(final ContentsAdapter... adapters) {
        DTVTLogger.start();
        //通信を行っている処理を止める
        if (adapters != null) {
            for (ContentsAdapter contentsAdapter : adapters) {
                if (contentsAdapter != null) {
                    contentsAdapter.stopConnect();
                }
            }
        }
        return null;
    }
}

