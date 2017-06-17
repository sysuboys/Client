package com.example.a11962.touch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.a11962.touch.adapters.Friend;
import com.example.a11962.touch.adapters.FriendAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 11962 on 2017/5/20.
 */
public class FriendFragment extends Fragment {

    //好友列表的listview
    private ListView friendview;
    private FriendAdapter friendAdapter;
    private List<Friend> friList;
    private Toolbar friToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View friendLayout = inflater.inflate(R.layout.friends, container, false);

        friendview= (ListView) friendLayout.findViewById(R.id.friend_list);
        friList= getFriendList();
        friendAdapter=new FriendAdapter(friList,getActivity());
        friendview.setAdapter(friendAdapter);
        initClick();

        friToolbar = (Toolbar) friendLayout.findViewById(R.id.friendToolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(friToolbar);


        return friendLayout;
    }
    /*设置listview点击事件*/
    public void initClick() {
        friendview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity().getApplicationContext(),
                        friList.get(position).getName(), Toast.LENGTH_SHORT).show();
                //点击任一好友跳转到好友交流界面, 发送好友名字到对应Activity
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("friendName", friList.get(position).getName());
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }

        });
    }

    /*从文件中读取好友列表*/
    private List<Friend> getFriendList() {
        //从保存的好友信息中读取好友列表
        SharedPreferences preferences = this.getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        Set<String> friSet =
                new HashSet<String>(preferences.getStringSet("friends", new HashSet<String>()));
        List<Friend> friendList = new ArrayList<Friend>();
        if (friSet != null) {
            for (String s : friSet) {
                if (s != null) {
                    Friend fris = new Friend();
                    fris.setName(s);
                    friendList.add(fris);
                }
            }
        }

        return friendList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_FIRST_USER) {


        }
    }
}
