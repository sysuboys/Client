package com.example.a11962.touch.diaries;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a11962.touch.R;
import com.example.a11962.touch.friends.Friend;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 11962 on 2017/5/22.
 */

public class DiaryAdapter extends BaseAdapter{
    private List<String> diaryList;
    private LayoutInflater inflater;
    public DiaryAdapter() {}

    public DiaryAdapter(List<String> diaryList, Context context) {
        this.diaryList = diaryList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return diaryList==null?0:diaryList.size();
    }

    @Override
    public String getItem(int position) {
        return diaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view=inflater.inflate(R.layout.diary_list,null);
        String title=getItem(position);
        //在view视图中查找id为diaryItem的控件
        TextView tv_name= (TextView) view.findViewById(R.id.diaryItem);
        tv_name.setText(title);
        tv_name.setTag(position);
        return view;
    }

}
