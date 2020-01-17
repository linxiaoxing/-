package com.example.detaildemo.data.provider.stop;


import android.os.AsyncTask;

import com.example.detaildemo.adapter.DtvContentsBroadcastAdapter;
import com.example.detaildemo.common.DTVTLogger;

/**
 * 放送番組タブ通信止め.
 */
public class StopBroadcastAdapterConnect extends AsyncTask<DtvContentsBroadcastAdapter, Void, Void>{
    /**
     * コンストラクタ.
     */
    public StopBroadcastAdapterConnect() {
        DTVTLogger.start();
    }

    @Override
    protected Void doInBackground(final DtvContentsBroadcastAdapter... adapters) {
        DTVTLogger.start();
        //通信を行っている処理を止める
        if (adapters != null) {
            for (DtvContentsBroadcastAdapter dtvContentsBroadcastAdapter : adapters) {
                if (dtvContentsBroadcastAdapter != null) {
                    dtvContentsBroadcastAdapter.stopConnect();
                }
            }
        }
        return null;
    }
}
