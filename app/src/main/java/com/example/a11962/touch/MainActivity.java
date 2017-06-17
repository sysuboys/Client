package com.example.a11962.touch;



import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.a11962.touch.polling.BgService;
import com.example.a11962.touch.polling.PollingService;
import com.example.a11962.touch.polling.PollingUtils;
import com.example.a11962.touch.websocket.InviteService;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private ArrayList<Fragment> fragments;
    private BottomNavigationBar bottomNavigationBar;
    private Toolbar maintoolbar;

    private FriendFragment friendFragment;
    private DiaryFragment diaryFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //设置底部导航栏特性
        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.friend, "Friend"))
                .setActiveColor(R.color.primary_dark)
                .addItem(new BottomNavigationItem(R.drawable.diary, "Diary"))
                .setActiveColor(R.color.primary_dark)
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);



        //Start invite service
        startService(new Intent(this, InviteService.class));

    }

    /** * 设置默认的Fragment为friend*/
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (friendFragment == null)
            friendFragment = new FriendFragment();
        transaction.replace(R.id.content, friendFragment);
        transaction.commit();
    }
    //将所有的Fragment加入容器
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        if (friendFragment == null)
            friendFragment = new FriendFragment();
        if (diaryFragment == null)
            diaryFragment = new DiaryFragment();
        fragments.add(friendFragment);
        fragments.add(diaryFragment);
        return fragments;
    }

    //根据点击的position决定fragment的呈现
    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.content, fragment);
                } else {
                    ft.add(R.id.content, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    //如果切换了fragment，则移除原来的Fragment
    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //根据返回的requestCode，决定呈现好友或者日记页
        if (requestCode == 0) {
            bottomNavigationBar.selectTab(0);
        }
        if (requestCode == 1) {
            bottomNavigationBar.selectTab(1);
        }
    }
}
