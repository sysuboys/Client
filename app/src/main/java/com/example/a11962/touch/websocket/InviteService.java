package com.example.a11962.touch.websocket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.a11962.touch.R;
import com.example.a11962.touch.SessionUtil;
import com.example.a11962.touch.TouchActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by 11962 on 2017/5/26.
 */
public class InviteService extends Service {

    public static final String ACTION = "com.example.a11962.touch.websocket.InviteService";

    private Notification mNotification;
    private NotificationManager mManager;
    private WebSocket mWebSocket = null;
    private int msgCount = 0; //消息发送次数
    private Timer mTimer;
    private String url ="ws://172.18.69.141:8080/isInvited";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final Context inviteService = this;

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotification = new NotificationCompat
                .Builder(inviteService)
                .setTicker("Ticker")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText("Text")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
        new PollingThread().start();
        return START_STICKY; //START_STICKY means our service should not be killed after our main activity has quitted.
    }
    class PollingThread extends Thread {
        @Override
        public void run() {
            initWsClient(url);
        }
    }


    //每秒发送一条消息
    private void startTask() {
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mWebSocket == null) return;
                msgCount++;
                boolean isSuccessed = mWebSocket.send("msg" + msgCount + "-" + System.currentTimeMillis());
                if (msgCount == 5) {
                    mWebSocket.close(1000, "close by user");
                }
                //除了文本内容外，还可以将如图像，声音，视频等内容转为ByteString发送
                //boolean send(ByteString bytes);
            }
        };
        mTimer.schedule(timerTask, 0, 1000);
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

    private void initWsClient(String wsUrl) {
        //新建client
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        //构造request对象
        Request request = new Request.Builder()
                .addHeader("Cookie", SessionUtil.SESSIONID)
                .url(wsUrl)
                .build();

        //建立连接
        client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mWebSocket = webSocket;
                //打印一些内容
                System.out.println("client onOpen");
                System.out.println("client request header:" + response.request().headers());
                System.out.println("client response header:" + response.headers());
                System.out.println("client response:" + response);
                //开启消息定时发送
                startTask();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                //打印一些内容
                System.out.println("client onMessage");
                System.out.println("message:" + text);
                showNotification(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("client onClosing");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                //打印一些内容
                System.out.println("client onClosed");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                //出现异常会进入此回调
                System.out.println("client onFailure");
                System.out.println("throwable:" + t);
                System.out.println("response:" + response);
            }
        });
    }





}
