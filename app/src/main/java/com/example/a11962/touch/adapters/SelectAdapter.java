package com.example.a11962.touch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.a11962.touch.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 11962 on 2017/6/6.
 */
public class SelectAdapter extends BaseAdapter {
    private Context context;
    private List<String> selectList;
    //用于记录每个RadioButton的状态，并保证只可选一个
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();

    public  SelectAdapter(Context context, List<String> selectList) {
        this.context = context;
        this.selectList = selectList;
    }

    @Override
    public int getCount() {
        return selectList.size();
    }

    @Override
    public Object getItem(int position) {
        return selectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 页面
        ViewHolder holder;
        String bean = selectList.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.select_list, null);
            holder = new ViewHolder();
//			holder.rb_state = (RadioButton) convertView
//					.findViewById(R.id.rb_light);
            holder.diaryName = (TextView) convertView
                    .findViewById(R.id.select_diary_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.diaryName.setText(bean);
        final RadioButton radio=(RadioButton) convertView.findViewById(R.id.select_btn);
        holder.rdBtn = radio;
        holder.rdBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // 重置，确保最多只有一项被选中
                for (String key : states.keySet()) {
                    states.put(key, false);

                }
                states.put(String.valueOf(position), radio.isChecked());
                SelectAdapter.this.notifyDataSetChanged();
            }
        });

        boolean res = false;
        if (states.get(String.valueOf(position)) == null
                || states.get(String.valueOf(position)) == false) {
            res = false;
            states.put(String.valueOf(position), false);
        } else
            res = true;

        holder.rdBtn.setChecked(res);
        return convertView;

    }

    class ViewHolder {
        TextView diaryName;
        RadioButton rdBtn;
    }





}
