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

public class TouchActivity extends AppCompatActivity {

    private Toolbar touchToolbar;
    private GridView gridView;
    private SimpleAdapter simpleAdapter;
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
        if (!friName.isEmpty()) {
            //如果收到来自好友邀请，显示dialog
            touchToolbar.setTitle(friName);
        }

        setSupportActionBar(touchToolbar);

        gridView = (GridView)findViewById(R.id.touchGrid);
        initGrid();
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
                sendPosition(i);
            }
        });
    }
    public void sendPosition(int pos) {

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
}
