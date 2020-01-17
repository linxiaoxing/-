package com.example.detaildemo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.detaildemo.R;

public class DlnaUtils {

    /**ダウンロード通知チャンネルID.*/
    private static final String DOWNLOAD_NOTIFICATION_ID = "downloadProgress";

    /**
     * 通知表示.
     * @param title ダウンロード中番組タイトル
     * @param text テキスト
     * @param context コンテキスト
     * @return 通知
     */
    public static Notification getNotification(final String title, final String text, final Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setSmallIcon( R.mipmap.icon_normal_tv);
        builder.setLargeIcon( BitmapFactory.decodeResource(context.getResources(), R.mipmap.icd_app_tvterminal));
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(DOWNLOAD_NOTIFICATION_ID,
                    context.getString(R.string.record_download_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(mChannel);
            builder.setChannelId(DOWNLOAD_NOTIFICATION_ID);
        }
        return builder.build();
    }

}
