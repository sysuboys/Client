package com.example.a11962.touch;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
    private FloatingActionButton toTouch;
    private String title;
    private List<String> list;
    private Toolbar toolbar;
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        friendName = getIntent().getStringExtra("friend");

        selectList = (ListView)findViewById(R.id.select_list);
        toTouch = (FloatingActionButton)findViewById(R.id.toTouch);

        selectAdapter = new SelectAdapter(this, getDiaryList());
        selectList.setAdapter(selectAdapter);
        selectList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        toolbar = (Toolbar)findViewById(R.id.selectToolbar);
        setSupportActionBar(toolbar);

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
                Toast.makeText(SelectActivity.this, title, Toast.LENGTH_SHORT).show();
                new PreThread().start();
            }
        });
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
        private String url ="ws://172.18.69.141:8080/invite";
        @Override
        public void run() {
            //新建client
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
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
                    startTask();
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    //打印一些内容
                    System.out.println("client onMessage");
                    System.out.println("message:" + text);
                    try {
                        JSONObject resJson = new JSONObject(text);
                        if (resJson.getBoolean("success")) {
                            mWebSocket.close(0, "succes to close by client");
                            Intent intent = new Intent(SelectActivity.this, TouchActivity.class);
                            startActivityForResult(intent, 0);
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

        //每秒发送一条消息
        private void startTask() {
            mTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                String sendJson = "{\"invitee\":\"" + friendName + "\", \"title\":" + title + "}";

                @Override
                public void run() {
                    System.out.println(sendJson);
                    if (mWebSocket == null) return;
                    boolean isSuccessed = mWebSocket.send(sendJson);
                    //除了文本内容外，还可以将如图像，声音，视频等内容转为ByteString发送
                    //boolean send(ByteString bytes);
                }
            };
            mTimer.schedule(timerTask, 0, 1000);
        }
    };
}
