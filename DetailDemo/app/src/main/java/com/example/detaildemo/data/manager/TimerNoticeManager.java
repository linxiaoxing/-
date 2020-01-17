package com.example.detaildemo.data.manager;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.data.bean.ScheduleInfo;
import com.example.detaildemo.data.bean.channel.ChannelInfo;
import com.example.detaildemo.data.provider.data.ScheduleComparator;
import com.example.detaildemo.data.webapiclient.jsonparser.data.TimerNoticeInfo;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 放送中コンテンツ切り替えマネージャー.
 */
public class TimerNoticeManager {

    /**
     * 放送中コンテンツ切り替え通知リスナー.
     */
    public interface ContentsChangeListener {
        /**
         * 放送中コンテンツ切り替え通知コールバック.
         * @param timerNoticeInfo 画面に渡す情報
         */
        void onContentsChangeCallBack(TimerNoticeInfo timerNoticeInfo);
        /**
         * 本日の一番最後の放送中コンテンツ終了通知コールバック.
         * @param timerNoticeInfo 画面に渡す情報
         */
        void onContentsCompleteCallBack(TimerNoticeInfo timerNoticeInfo);
    }

    /**
     * shared.
     * @return Instance
     */
    public static TimerNoticeManager shared() {
        return sInstance;
    }

    /** singleTon. */
    private static final TimerNoticeManager sInstance = new TimerNoticeManager();
    /** TimerNoticeManager. */
    private TimerNoticeManager() {
    }
    /** コンテキスト. */
    private Application mContext;
    /** 接続リスナー. */
    private ContentsChangeListener mContentsChangeListener = null;
    /** タイマー管理. */
    private List<MyTimerCountDown> mCountDownTimers;
    /**
     * コンテキスト設定.
     * @param context コンテキスト
     */
    public void setContext(final Application context) {
        TimerNoticeManager.shared().mContext = context;
    }

    /**
     * タイマーのリスナー設定.
     * @param listener リスナー
     */
    public void setConnectionListener(final ContentsChangeListener listener) {
        TimerNoticeManager.shared().mContentsChangeListener = listener;
    }

    /**
     * タイマー管理開始.
     * @param channelInfoList チャンネル情報
     */
    public void onBind(final List<ChannelInfo> channelInfoList) {
        DTVTLogger.start();
        if (TimerNoticeManager.shared().mCountDownTimers == null) {
            TimerNoticeManager.shared().mCountDownTimers = new ArrayList<>();
        }
        filterDate(channelInfoList, false);
        DTVTLogger.end();
    }

    /**
     * 番組表情報を更新.
     * 当日の番組すべて放送終了、翌日の番組表情報を追加.
     * @param channelInfoList チャンネル情報
     */
    public void updateChannelInfo(final List<ChannelInfo> channelInfoList) {
        DTVTLogger.start();
        filterDate(channelInfoList, true);
        DTVTLogger.end();
    }

    /**
     * タイマーをクリア.
     */
    public void stopAll() {
        DTVTLogger.start();
        if (TimerNoticeManager.shared().mCountDownTimers != null) {
            List<MyTimerCountDown> timers = TimerNoticeManager.shared().mCountDownTimers;
            for (int i = 0; i < timers.size(); i++) {
                timers.get(i).stop();
            }
            TimerNoticeManager.shared().mCountDownTimers.clear();
            TimerNoticeManager.shared().mCountDownTimers = null;
        }
        DTVTLogger.end();
    }

    /**
     * タイマーを一時停止.
     */
    public void onPause() {
        if (TimerNoticeManager.shared().mCountDownTimers != null) {
            List<MyTimerCountDown> timers = TimerNoticeManager.shared().mCountDownTimers;
            for (int i = 0; i < timers.size(); i++) {
                timers.get(i).stop();
            }
        }
    }

    /**
     * タイマーを再開.
     */
    public void onRestart() {
        if (TimerNoticeManager.shared().mCountDownTimers != null) {
            List<MyTimerCountDown> timers = TimerNoticeManager.shared().mCountDownTimers;
            for (int i = 0; i < timers.size(); i++) {
                MyTimerCountDown myTimerCountDown = timers.get(i);
                List<Date> list = myTimerCountDown.getDateList();
                if (list != null && list.size() > 0) {
                    myTimerCountDown.run();
                } else {
                    notifyContentCompleted(myTimerCountDown.getTimerNoticeInfo());
                }
            }
        } else {
            notifyContentCompleted(null);
        }
    }

    /**
     * フィルター処理（番組の終了時間が現在時刻以前の場合対象外）.
     * @param channels 番組情報
     * @param isUpdate 番組表情報更新フラグ
     */
    private void filterDate(final List<ChannelInfo> channels, final boolean isUpdate) {
        if (channels != null) {
            final List<ChannelInfo> channelInfos = new ArrayList<>(channels);
            for (int i = 0; i < channelInfos.size(); i++) {
                String serviceIdUniq = channelInfos.get(i).getServiceIdUniq();
                if (TextUtils.isEmpty(serviceIdUniq)) {
                    continue;
                }
                ArrayList<ScheduleInfo> scheduleInfos = channelInfos.get(i).getSchedules();
                List<Date> dateList = null;
                List<TimerNoticeInfo> timerNoticeInfos = null;
                Date now = DateUtils.getNowTimeFormatDate();
                if (scheduleInfos != null && scheduleInfos.size() > 0) {
                    dateList = new ArrayList<>();
                    timerNoticeInfos = new ArrayList<>();
                    ArrayList<ScheduleInfo> scheduleList = new ArrayList<>(scheduleInfos);
                    for (int j = 0; j < scheduleList.size(); j++) {
                        ScheduleInfo mScheduleInfo = scheduleList.get(j);
                        String endDate = mScheduleInfo.getEndTime();
                        if (TextUtils.isEmpty(endDate)) {
                            continue;
                        }
                        Date date = DateUtils.stringToChangeDate(endDate);
                        if (date == null) {
                            continue;
                        }
                        if (date.getTime() <= now.getTime()) {
                            continue;
                        }
                        TimerNoticeInfo timerNoticeInfo = new TimerNoticeInfo();
                        timerNoticeInfo.setTitle(mScheduleInfo.getTitle());
                        timerNoticeInfo.setServiceIdUniq(mScheduleInfo.getServiceIdUniq());
                        timerNoticeInfo.setThumListURL(mScheduleInfo.getImageUrl());
                        timerNoticeInfo.setRValue(mScheduleInfo.getRValue());
                        timerNoticeInfo.setStartTime(mScheduleInfo.getStartTime());
                        timerNoticeInfo.setEndTime(mScheduleInfo.getEndTime());
                        timerNoticeInfo.setContentsId(mScheduleInfo.getContentsId());
                        timerNoticeInfos.add(timerNoticeInfo);
                        dateList.add(date);
                    }
                }
                if (dateList != null && dateList.size() > 0) {
                    Collections.sort(dateList, new ScheduleComparator());
                    boolean isExistFlg = false;
                    if (TimerNoticeManager.shared().mCountDownTimers != null) {
                        List<MyTimerCountDown> timerCountDowns = TimerNoticeManager.shared().mCountDownTimers;
                        for (int j = 0; j < timerCountDowns.size(); j++) {
                            MyTimerCountDown myTimerCountDown = timerCountDowns.get(j);
                            //該当チャンネル既に存在する場合
                            if (serviceIdUniq.equals(myTimerCountDown.getServiceIdUniq())) {
                                myTimerCountDown.setDateList(dateList);
                                myTimerCountDown.setTimerNoticeInfo(timerNoticeInfos);
                                if (isUpdate) {
                                    notifyContentChanged(timerNoticeInfos.get(0));
                                }
                                myTimerCountDown.run();
                                isExistFlg = true;
                                break;
                            }
                        }
                    }
                    //該当チャンネル存在しない場合
                    if (!isExistFlg && !isUpdate) {
                        MyTimerCountDown myTimerCountDown = new MyTimerCountDown();
                        myTimerCountDown.setDateList(dateList);
                        myTimerCountDown.setTimerNoticeInfo(timerNoticeInfos);
                        myTimerCountDown.setServiceIdUniq(serviceIdUniq);
                        myTimerCountDown.run();
                        TimerNoticeManager.shared().mCountDownTimers.add(myTimerCountDown);
                    }
                }
            }
        } else {
            DTVTLogger.debug("TimerNoticeManager checkEndDate channels is null");
        }
    }

    /**
     * 番組切り替え通知コールバック.
     * @param timerNoticeInfo 画面に渡す情報
     */
    private void notifyContentChanged(final TimerNoticeInfo timerNoticeInfo) {
        if (TimerNoticeManager.shared().mContentsChangeListener != null) {
            TimerNoticeManager.shared().mContentsChangeListener.onContentsChangeCallBack(timerNoticeInfo);
        } else {
            if (mContext != null) {
                Intent intent = new Intent();
                intent.setAction( ContentUtils.CONTENTS_CHANGE_ACTION);
                intent.putExtra(ContentUtils.NOTICE_INFO, timerNoticeInfo);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                DTVTLogger.debug("TimerNoticeManager notifyContentChanged mContext is null");
            }
        }
    }

    /**
     * 本日の番組すべて終了通知コールバック.
     * @param timerNoticeInfo 画面に渡す情報
     */
    private void notifyContentCompleted(final TimerNoticeInfo timerNoticeInfo) {
        if (TimerNoticeManager.shared().mContentsChangeListener != null) {
            TimerNoticeManager.shared().mContentsChangeListener.onContentsCompleteCallBack(timerNoticeInfo);
        } else {
            if (mContext != null) {
                Intent intent = new Intent();
                intent.setAction(ContentUtils.CHANNEL_COMPLETED_ACTION);
                intent.putExtra(ContentUtils.NOTICE_INFO, timerNoticeInfo);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                DTVTLogger.debug("TimerNoticeManager notifyContentCompleted mContext is null");
            }
        }
    }

    /**
     * カウントダウン.
     */
    private class MyTimerCountDown {
        /** サービスIDユニーク. */
        private String mServiceIdUniq;
        /** 番組切り替える時に、画面に渡す情報. */
        private TimerNoticeInfo mTimerNoticeInfo;
        /** タイマー管理日付リスト. */
        private List<Date> mDateList;
        /** 画面に渡す情報リスト. */
        private List<TimerNoticeInfo> mTimerNoticeInfos;
        /** タイマー. */
        private Timer mTimer;

        /**
         * コントラクター.
         */
        MyTimerCountDown() {
            mDateList = new ArrayList<>();
            mTimerNoticeInfos = new ArrayList<>();
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
         * @param serviceIdUniq サービスIDユニーク
         */
        public void setServiceIdUniq(final String serviceIdUniq) {
            this.mServiceIdUniq = serviceIdUniq;
            if (mTimerNoticeInfo == null) {
                mTimerNoticeInfo = new TimerNoticeInfo();
                mTimerNoticeInfo.setServiceIdUniq(serviceIdUniq);
            }
        }

        /**
         * 画面に渡す情報取得.
         * @return サービスIDユニーク
         */
        TimerNoticeInfo getTimerNoticeInfo() {
            return mTimerNoticeInfo;
        }

        /**
         * 画面に渡す情報設定.
         * @param serviceIdUniq サービスIDユニーク
         */
        void setTimerNoticeInfo(final TimerNoticeInfo serviceIdUniq) {
            this.mTimerNoticeInfo = serviceIdUniq;
        }

        /**
         * 番組終了時間リスト設定.
         * @param dateList 番組終了時間リスト
         */
        void setDateList(final List<Date> dateList) {
            this.mDateList = dateList;
        }

        /**
         * 番組終了時間リスト取得.
         * @return リスト
         */
        List<Date> getDateList() {
            return mDateList;
        }

        /**
         * 画面に通知情報設定.
         * @param timerNoticeInfo 画面に通知情報
         */
        void setTimerNoticeInfo(final List<TimerNoticeInfo> timerNoticeInfo) {
            this.mTimerNoticeInfos = timerNoticeInfo;
        }

        /**
         * タイマー実行.
         */
        void run() {
            if (mDateList.size() > 0) {
                Date date = mDateList.get(0);
                startTimer(date);
            } else {
                notifyContentCompleted(mTimerNoticeInfo);
                stop();
            }
        }

        /**
         * タイマー開始.
         * @param endDate 番組終了時間
         */
        private void startTimer(final Date endDate) {
            DTVTLogger.start();
            Date now = DateUtils.getNowTimeFormatDate();
            //現在時刻より＜の場合
            if (endDate.getTime() <= now.getTime()) {
                notifyChangeCheck();
                return;
            }
            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    notifyChangeCheck();
                }
            };
            mTimer.schedule(task, endDate);
            DTVTLogger.end();
        }

        /**
         * 通知チェック.
         */
        private void notifyChangeCheck() {
            DTVTLogger.start();
            if (mDateList.size() == 0) {
                notifyContentCompleted(mTimerNoticeInfo);
                stop();
                return;
            }
            try {
                mDateList.remove(0);
                mTimerNoticeInfos.remove(0);
                if (mDateList.size() > 0 && mTimerNoticeInfos.size() > 0) {
                    setTimerNoticeInfo(mTimerNoticeInfos.get(0));
                    //日付チェック
                    Date now = DateUtils.getNowTimeFormatDate();
                    if (mDateList.get(0).getTime() <= now.getTime()) {
                        notifyChangeCheck();
                        return;
                    }
                    notifyContentChanged(mTimerNoticeInfo);
                    MyTimerCountDown.this.run();
                } else {
                    notifyContentCompleted(mTimerNoticeInfo);
                    stop();
                }
            } catch (IndexOutOfBoundsException | UnsupportedOperationException e) {
                DTVTLogger.debug("notifyChangeCheck Exception :" + e);
            }
            DTVTLogger.end();
        }

        /**
         * タイマー停止.
         */
        void stop() {
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    }
}
