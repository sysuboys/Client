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

import com.example.a11962.touch.ChatUtils;
import com.example.a11962.touch.MainActivity;
import com.example.a11962.touch.R;
import com.example.a11962.touch.SelectActivity;
import com.example.a11962.touch.SessionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by 11962 on 2017/6/24.
 */
public class AddService extends Service {
    private Notification mNotification;
    private NotificationManager mManager;
    public static WebSocket mWebSocket = null;
    private int msgCount = 0; //消息发送次数
    private Timer mTimer;
    private String url ="ws://172.18.69.141:8080/isAdded";
    private Context addService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        addService = this;


        new SocketThread().start();
        return START_STICKY; //START_STICKY means our service should not be killed after our main activity has quitted.
    }
    class SocketThread extends Thread {
        @Override
        public void run() {
            initWsClient(url);
        }
    }




    //弹出Notification
    private void showNotification(String inviter) {
        //Navigator to the new activity when click the notification title
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("inviter", inviter);
        i.putExtra("from", "AddService");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new NotificationCompat
                .Builder(addService)
                .setTicker("邀请你")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(inviter)
                .setContentText("添加你为好友")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
        mNotification.contentIntent = pendingIntent;
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.notify(0, mNotification);

    }

    private void initWsClient(String wsUrl) {
        //新建client
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        //构造request对象
        //String s = SessionUtil.getSESSION(this);
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
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                //打印一些内容
                try {
                    System.out.println("client onMessage");
                    System.out.println("message:" + text);
                    JSONObject inviteRes = new JSONObject(text);
                    String inviter = inviteRes.getString("inviter");
                    showNotification(inviter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
