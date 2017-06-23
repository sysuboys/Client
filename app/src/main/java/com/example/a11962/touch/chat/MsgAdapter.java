package com.example.a11962.touch.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.bl.BubbleLayout;
import com.example.a11962.touch.ChatActivity;
import com.example.a11962.touch.R;

import java.util.List;

/**
 * Created by 11962 on 2017/5/21.
 */
public class MsgAdapter extends ArrayAdapter<Msg>
{

    private int resourceId;

    public MsgAdapter(Context context, int resource, List<Msg> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (BubbleLayout) view
                    .findViewById(R.id.left_layout);
            viewHolder.rightLayout = (BubbleLayout) view
                    .findViewById(R.id.right_layout);
            viewHolder.leftTextView = (TextView) view
                    .findViewById(R.id.left_msg);
            viewHolder.rightTextView = (TextView) view
                    .findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (msg.getType() == Msg.TYPE_RECEIVE) {   //如果是接收信息
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftTextView.setText(msg.getContent());
        } else {  // 如果是发送信息
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightTextView.setText(msg.getContent());
        }
        return view;
    }

    private class ViewHolder {

        BubbleLayout leftLayout;
        BubbleLayout rightLayout;

        TextView leftTextView;
        TextView rightTextView;

    }
}
