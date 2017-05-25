package com.example.a11962.touch.polling;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.a11962.touch.R;
import com.example.a11962.touch.TouchActivity;

/**
 * Created by 11962 on 2017/5/25.
 */
public class PollingService extends Service {

    public static final String ACTION = "com.ryantang.service.PollingService";

    private Notification mNotification;
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final Context pollingService = this;

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotification = new NotificationCompat
                .Builder(pollingService)
                .setTicker("Ticker")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText("Text")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        new PollingThread().start();
        return START_STICKY;
    }


    //弹出Notification
    private void showNotification(String name) {
        //Navigator to the new activity when click the notification title
        Intent i = new Intent(this, TouchActivity.class);
        i.putExtra("friendName", name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentIntent = pendingIntent;
        mManager.notify(0, mNotification);
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */
    int count = 0;
    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");
            count ++;
            //当计数能被5整除时弹出通知
            if (count % 2 == 0) {
                showNotification("Jack");
                System.out.println("New message!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

}
