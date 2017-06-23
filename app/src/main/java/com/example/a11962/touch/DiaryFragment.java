package com.example.a11962.touch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.a11962.touch.adapters.DiaryAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 11962 on 2017/5/20.
 */
public class DiaryFragment extends Fragment {

    private FloatingActionButton toEdit;
    private Toolbar diaryToolbar;
    private ListView diaryView;
    private DiaryAdapter diaryAdapter;
    private List<String> diaryList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View diaryLayout = inflater.inflate(R.layout.diary, container, false);

        diaryView = (ListView)diaryLayout.findViewById(R.id.diary_list);
        toEdit= (FloatingActionButton) diaryLayout.findViewById(R.id.toEdit);
        toEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击任一好友跳转到好友交流界面, 发送好友名字到对应Activity
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });

        diaryToolbar = (Toolbar) diaryLayout.findViewById(R.id.friendToolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(diaryToolbar);
        initList();
        initClick();
        return diaryLayout;
    }
    /*初始化*/
    public void initClick() {
        diaryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //点击日记显示日记内容
                Intent intent = new Intent(getActivity(), ShowActivity.class);
                intent.putExtra("title", diaryList.get(position));
                intent.putExtra("suffix", "myDiary");
                intent.putExtra("from", "MainActivity");
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }

        });
    }
    /*初始化日记列表视图*/
    public void initList() {

        diaryList = getDiaryList();
        diaryAdapter = new DiaryAdapter(getDiaryList(), getActivity());
        diaryView.setAdapter(diaryAdapter);

    }
    /*得到日记列表项*/
    public List<String> getDiaryList() {
        List<String> list = new ArrayList<>();

        try {
            //文件目录：内部存储文件路径/myDiary
            File myDiaryPath = new File(getActivity().getFilesDir().getAbsolutePath()+File.separator+"myDiary" );
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
}
