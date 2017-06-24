package com.example.a11962.touch;



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.a11962.touch.websocket.AddService;
import com.example.a11962.touch.websocket.InviteService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private ArrayList<Fragment> fragments;
    private BottomNavigationBar bottomNavigationBar;
    private Toolbar maintoolbar;

    private FriendFragment friendFragment;
    private DiaryFragment diaryFragment;
    private SearchView searchView;
    private String inviter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //设置底部导航栏特性
        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.friend, "Friend"))
                .setActiveColor(R.color.primary_dark)
                .addItem(new BottomNavigationItem(R.drawable.diary, "Diary"))
                .setActiveColor(R.color.primary_dark)
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);
        if (getIntent().getBooleanExtra("Edit", false)) {
            bottomNavigationBar.selectTab(1);
        }
        String from = getIntent().getStringExtra("from");
        if (from != null && from.equals("AddService")) {
            inviter = getIntent().getStringExtra("inviter");
            comfirm();

        }

        //Start invite service
        startService(new Intent(this, InviteService.class));
        startService(new Intent(this, AddService.class));

    }
    //收到来自好友邀请时，确认
    public void comfirm() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case Dialog.BUTTON_POSITIVE:
                        String comfirmJson =
                                "{\"inviter\":\"" + inviter + "\", \"accept\":" + true + "}";
                        if (AddService.mWebSocket.send(comfirmJson)) {
                            saveFri(inviter);
                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        String refuseJson =
                                "{\"inviter\":\"" + inviter + "\", \"accept\":" + false + "}";
                        AddService.mWebSocket.send(refuseJson);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("好友申请来自"+inviter);
        builder.setMessage("是否确认接受");
        builder.setPositiveButton("确认", dialogClickListener);
        builder.setNegativeButton("取消", dialogClickListener);
        builder.create().show();
    }
    //保存好友信息
    public void saveFri(String name) {
        SharedPreferences preferences = getSharedPreferences("info", Context.MODE_PRIVATE);
        Set<String> friSet =
                new HashSet<String>(preferences.getStringSet("friends", new HashSet<String>()));
        SharedPreferences.Editor edit = preferences.edit();
        friSet.add(name);
        edit.putStringSet("friends", friSet).commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.ab_search);
        searchView= (SearchView) MenuItemCompat.getActionView(menuItem);//加载searchview
        searchView.setSubmitButtonEnabled(true);
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new AddThread("ws://172.18.69.141:8080/add", query).start();
                //收起键盘和searchView
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setQueryHint("添加好友");
        return true;
    }

    /** * 设置默认的Fragment为friend*/
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (friendFragment == null)
            friendFragment = new FriendFragment();
        transaction.replace(R.id.content, friendFragment);
        transaction.commit();
    }
    //将所有的Fragment加入容器
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        if (friendFragment == null)
            friendFragment = new FriendFragment();
        if (diaryFragment == null)
            diaryFragment = new DiaryFragment();
        fragments.add(friendFragment);
        fragments.add(diaryFragment);
        return fragments;
    }

    //根据点击的position决定fragment的呈现
    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.content, fragment);
                } else {
                    ft.add(R.id.content, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    //如果切换了fragment，则移除原来的Fragment
    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //根据返回的requestCode，决定呈现好友或者日记页
        if (requestCode == 0) {
            bottomNavigationBar.selectTab(0);
        }
        if (requestCode == 1) {
            bottomNavigationBar.selectTab(1);
        }
    }
    //邀请好友的thread
    public class AddThread extends Thread {
        private WebSocket mWebSocket = null;
        private String url;
        private String name;
        AddThread(String url, String name) {
            this.url = url;
            this.name = name;
        }
        @Override
        public void run() {
            //新建client
            if (mWebSocket != null) {
                mWebSocket.close(0000, "open a new web socket");
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
                    //开启消息发送
                    //开启消息定时发送
                    String sendJson =
                            "{\"invitee\":\"" + name + "\"}";
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
                            //从保存的好友信息中读取好友列表
                            String newFri = resJson.getString("invitee");
                            saveFri(newFri);
                            //收起searchView
                        } else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, resJson.getString("error"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            mWebSocket.close(1000, "关闭连接");
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

    }
}
