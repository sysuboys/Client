package com.example.a11962.touch;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by 11962 on 2017/5/20.
 */
public class DiaryFragment extends Fragment {

    private FloatingActionButton toEdit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View diaryLayout = inflater.inflate(R.layout.diary, container, false);

        toEdit= (FloatingActionButton) diaryLayout.findViewById(R.id.toEdit);
        toEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击任一好友跳转到好友交流界面, 发送好友名字到对应Activity
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });
        return diaryLayout;
    }
}
