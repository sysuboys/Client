package com.example.a11962.touch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private String friName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        //从intent中找到对应的好友名字
        friName = intent.getStringExtra("friendName");
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
                Intent intent = new Intent(ChatActivity.this, SelectActivity.class);
                intent.putExtra("friendName", friName);
                intent.putExtra("from", "ChatActivity");
                startActivityForResult(intent, 1);
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
                //点击日记显示日记内容
                Intent intent = new Intent(ChatActivity.this, ShowActivity.class);
                intent.putExtra("title", msgList.get(i).getContent());
                if (msgList.get(i).getType() == Msg.TYPE_RECEIVE)
                    intent.putExtra("suffix", friName);
                else
                    intent.putExtra("suffix", "myDiary");
                intent.putExtra("from", "ChatActivity");
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });
    }
    //得到初始化信息
    private void initMsg()
    {
        SharedPreferences record = getSharedPreferences(friName, MODE_PRIVATE);
        String type = record.getString("type", null);
        String list = record.getString("diaries", null);
        if (type == null || list == null)
            return;
        String[] types = type.split(":");
        String[] lists = list.split(":");
        for (int i = 0; i < types.length; i++) {
            if (!lists[i].isEmpty()) {
                if (types[i].equals("0")) {
                    msgList.add(new Msg(lists[i], Msg.TYPE_RECEIVE));
                } else {
                    msgList.add(new Msg(lists[i], Msg.TYPE_SEND));
                }
            }
        }
    }
}
