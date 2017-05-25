package com.example.a11962.touch;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a11962.touch.chat.Msg;
import com.example.a11962.touch.chat.MsgAdapter;

import java.util.ArrayList;
import java.util.List;

/*
* 显示和好友日记交流的界面
* */
public class ChatActivity extends AppCompatActivity {

    List<Msg> msgList = new ArrayList<>();
    private ListView listView;
    private MsgAdapter adapter;
    private FloatingActionButton toTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        //从intent中找到对应的好友名字
        String friName = intent.getStringExtra("friendName");
        toolbar.setTitle(friName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toTouch = (FloatingActionButton)findViewById(R.id.fab);
        touchClick();
        initMsg();
        initList();
    }
    //按钮点击事件，转到下一进行touch的界面
    public void touchClick() {
        toTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击任一好友跳转到好友交流界面, 发送好友名字到对应Activity
                Intent intent = new Intent(ChatActivity.this, TouchActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
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
        return super.onOptionsItemSelected(item);
    }

    /* 初始化RecycleView*/
    private void initList() {
        listView = (ListView) findViewById(R.id.list_view);

        adapter = new MsgAdapter(ChatActivity.this, R.layout.chat_left_right, msgList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ChatActivity.this,
                        msgList.get(i).getContent(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //得到初始化信息
    private void initMsg()
    {
        Msg msg1 = new Msg("hello sealong", Msg.TYPE_RECEIVE);
        msgList.add(msg1);
        Msg msg2 = new Msg("hello peipei", Msg.TYPE_SEND);
        msgList.add(msg2);
        Msg msg = new Msg("What are you doing", Msg.TYPE_RECEIVE);
        msgList.add(msg);
    }
}
