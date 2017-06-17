package com.example.a11962.touch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class TouchActivity extends AppCompatActivity {

    private Toolbar touchToolbar;
    private GridView gridView;
    private SimpleAdapter simpleAdapter;
    private int position;
    private  int[] icon = {R.mipmap.ic_action_emo_angry, R.mipmap.ic_action_emo_cool,
    R.mipmap.ic_action_emo_cry, R.mipmap.ic_action_emo_err, R.mipmap.ic_action_emo_evil,
    R.mipmap.ic_action_emo_kiss, R.mipmap.ic_action_emo_laugh, R.mipmap.ic_action_emo_shame,
    R.mipmap.ic_action_emo_tongue};
    private String[] iconName = {"angry", "cool", "cry", "err", "evil", "kiss", "laugh", "shame",
            "tongue"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        Intent intent = getIntent();
        String friName = intent.getStringExtra("friendName");

        touchToolbar = (Toolbar)findViewById(R.id.touchToolbar);
        if (friName != null) {
            //如果收到来自好友邀请，显示dialog
            touchToolbar.setTitle(friName);
        }

        setSupportActionBar(touchToolbar);

        gridView = (GridView)findViewById(R.id.touchGrid);
        initGrid();

        TouchThread touchThread = new TouchThread();
        touchThread.start();
    }
    //初始化网格视图
    public void initGrid() {
        List<Map<String, Object>> dataList = getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.touchImage, R.id.touchText};
        simpleAdapter = new SimpleAdapter(this, dataList, R.layout.touch_list, from, to);
        gridView.setAdapter(simpleAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
            }
        });
    }
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            dataList.add(map);
        }

        return dataList;
    }

    public class TouchThread extends Thread {

        private WebSocket mWebSocket = null;
        private int msgCount = 0; //消息发送次数
        private Timer mTimer;
        private String url ="ws://172.18.68.71:9310/";

        @Override
        public void run() {
            //新建client
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            //构造request对象
            Request request = new Request.Builder()
                    .url(url)
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

        //每秒发送一条消息
        private void startTask() {
            mTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mWebSocket == null) return;
                    msgCount++;
                    String posJson = "{'position':"+ position + "}";
                    boolean isSuccessed = mWebSocket.send(posJson);
                    //除了文本内容外，还可以将如图像，声音，视频等内容转为ByteString发送
                    //boolean send(ByteString bytes);
                }
            };
            mTimer.schedule(timerTask, 0, 1000);
        }
    }
}
