package com.example.a11962.touch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a11962.touch.R;

import java.util.List;

/**
 * Created by 11962 on 2017/5/21.
 * 初始化好友列表的Adapter
 */
public class FriendAdapter extends BaseAdapter {
    private List<Friend> friList;
    private LayoutInflater inflater;
    public FriendAdapter() {}

    public FriendAdapter(List<Friend> friList, Context context) {
        this.friList = friList;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return friList==null?0:friList.size();
    }

    @Override
    public Friend getItem(int position) {
        return friList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view=inflater.inflate(R.layout.friend_list,null);
        Friend friend=getItem(position);
        //在view视图中查找id为image_photo的控件
        TextView tv_name= (TextView) view.findViewById(R.id.name);
        tv_name.setText(friend.getName());
        tv_name.setTag(position);
        return view;
    }

}
