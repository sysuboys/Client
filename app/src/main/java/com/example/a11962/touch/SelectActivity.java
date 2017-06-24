package com.example.a11962.touch;

import android.content.Intent;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.a11962.touch.adapters.SelectAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SelectActivity extends AppCompatActivity {

    private SelectAdapter selectAdapter;
    private ListView selectList; //选择的视图
    private FloatingActionButton toTouch;//确认按钮
    private String title;
    //日记list
    private List<String> list;
    private Toolbar selectToolbar;
    private String friendName;
    //用于标识是哪个页面传过来的
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);


        selectList = (ListView)findViewById(R.id.select_list);
        toTouch = (FloatingActionButton)findViewById(R.id.toTouch);

        selectAdapter = new SelectAdapter(this, getDiaryList());
        //设置单选按钮
        selectList.setAdapter(selectAdapter);
        selectList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        selectToolbar = (Toolbar)findViewById(R.id.selectToolbar);
        Intent intent = getIntent();
        //得到传递过来的friend name
        friendName = intent.getStringExtra("friendName");
        from = intent.getStringExtra("from");

        setSupportActionBar(selectToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 根据RadioButton的选择情况确定用户名
                for (int i = 0, j = selectList.getCount(); i < j; i++) {
                    View child = selectList.getChildAt(i);
                    RadioButton rdoBtn = (RadioButton) child
                            .findViewById(R.id.select_btn);
                    if (rdoBtn.isChecked()) {
                        title = list.get(i);
                        break;
                    }
                }
                //根据消息的来源选择对应的url
                if (from.equals("ChatActivity")) {
                    new PreThread("ws://172.18.69.141:8080/invite").start();
                } else {
                    new PreThread("ws://172.18.69.141:8080/ready").start();
                }

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //返回上一级
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //得到日记选择列表
    public List<String> getDiaryList() {
        list = new ArrayList<>();

        try {
            //文件目录：内部存储文件路径/myDiary
            File myDiaryPath = new File(getFilesDir().getAbsolutePath()+File.separator+"myDiary" );
            if (!myDiaryPath.exists()) {
                myDiaryPath.mkdir();
            }
            for (File files : myDiaryPath.listFiles()) {
                list.add(files.getName().substring(0, files.getName().lastIndexOf('.')));
            }
        } catch (Exception e) {

        }
        return list;

    }

    public class PreThread extends Thread {

        private WebSocket mWebSocket = null;
        private Timer mTimer;
        private String url;
        PreThread(String url) {
            this.url = url;
        }
        @Override
        public void run() {
            //新建client
            if (mWebSocket != null) {
                mWebSocket.close(0000, "open a new web socket");
                mTimer.cancel();
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            String s = SessionUtil.SESSIONID;
            //构造request对象
            Request request = new Request.Builder()
                    .addHeader("Cookie", SessionUtil.SESSIONID)
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
                    ChatUtils.myTitle = title+".html";
                    ChatUtils.friend = friendName;
                    String sendJson =
                            "{\"invitee\":\"" + friendName + "\", \"title\":\"" + title + ".html\"}";
                    System.out.println(sendJson);
                    if (mWebSocket == null) return;
                    boolean isSuccessed = mWebSocket.send(sendJson);
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    //打印一些内容
                    System.out.println("client onMessage");
                    System.out.println("message:" + text);
                    try {
                        JSONObject resJson = new JSONObject(text);
                        if (resJson.getBoolean("success")) {
                            if (from.equals("ChatActivity")) {
                                ChatUtils.friTitle = resJson.getString("title");
                            }
                            Intent intent = new Intent(SelectActivity.this, TouchActivity.class);
                            startActivityForResult(intent, 0);
                            mWebSocket.close(0000, "success to close by client");

                        } else {
                            String error = resJson.getString("error");
                            Looper.prepare();
                            Toast.makeText(SelectActivity.this, error, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            mWebSocket.close(1000, "close by user");
                        }
                    } catch (JSONException e) {

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
/*
        //每秒发送一条消息
        private void startTask() {
            mTimer = new Timer();
            TimerTask timerTask = new TimerTask() {


                @Override
                public void run() {
                    String sendJson =
                            "{\"invitee\":\"" + friendName + "\", \"title\":\"" + title + ".html\"}";
                    System.out.println(sendJson);
                    if (mWebSocket == null) return;
                    boolean isSuccessed = mWebSocket.send(sendJson);
                    //除了文本内容外，还可以将如图像，声音，视频等内容转为ByteString发送
                    //boolean send(ByteString bytes);
                }
            };
            mTimer.schedule(timerTask, 0, 1000);
        }
        */
    };

}
