package com.example.a11962.touch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a11962.touch.friends.Friend;
import com.example.a11962.touch.friends.FriendAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11962 on 2017/5/20.
 */
public class FriendFragment extends Fragment {

    //好友列表的listview
    private ListView friendview;
    private FriendAdapter friendAdapter;
    private List<Friend> friList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View friendLayout = inflater.inflate(R.layout.friends, container, false);
        friendview= (ListView) friendLayout.findViewById(R.id.friend_list);
        friList= getFriendList();
        friendAdapter=new FriendAdapter(friList,getActivity());
        friendview.setAdapter(friendAdapter);
        initClick();
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
        //TODO 从好友文件中读取List
        List<Friend> friList=new ArrayList<>();
        for(int i=0;i<10;i++){
            Friend stu=new Friend();
            stu.setName("name"+i);
            friList.add(stu);
        }
        return friList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_FIRST_USER) {


        }
    }
}
