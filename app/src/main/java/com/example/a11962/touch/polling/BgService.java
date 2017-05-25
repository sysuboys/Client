package com.example.a11962.touch.polling;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.a11962.touch.R;

/**
 * Created by 11962 on 2017/5/24.
 */
public class BgService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final Context bgService = this;

        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    Notification notification = new NotificationCompat
                            .Builder(bgService)
                            .setTicker("Ticker")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Title")
                            .setContentText("Text")
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);

                    try {
                        this.sleep(5000);
                    } catch (InterruptedException e) {
                        Log.d("Interrupted", e.getMessage());
                    }
                }
            }
        };

        thread.start();

        return START_STICKY; //START_STICKY means our service should not be killed after our main activity has quitted.
    }
}
