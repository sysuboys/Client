package com.example.a11962.touch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hdl.mricheditor.bean.CamaraRequestCode;
import com.hdl.mricheditor.view.MRichEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class EditorActivity extends AppCompatActivity {

    private MRichEditor richEditor;
    private Toolbar editToolbar;
    private EditText diaryText;
    private boolean lastSave = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        richEditor = (MRichEditor)findViewById(R.id.mre_editor);
        editToolbar = (Toolbar)findViewById(R.id.edittoolbar);
        diaryText = (EditText)findViewById(R.id.diaryTitle);
        setSupportActionBar(editToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editToolbar.setOnMenuItemClickListener(onMenuItemClick);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    //返回键按下
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //返回上一级
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /*Toolbar最右边item的点击事件*/
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_settings:
                    msg += "保存成功";
                    save();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(EditorActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
    /**
     * 需要重写这个方法选择图片、拍照
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode == CamaraRequestCode.CAMARA_GET_IMG) {
            richEditor.insertImg(data.getData());
        } else if (requestCode == CamaraRequestCode.CAMARA_TAKE_PHOTO) {
            richEditor.insertImg(data);
        }
    }

    /*日记编写完成，保存到本地文件*/
    public void save() {
        String title = diaryText.getText().toString().trim();
        String filename = title+".html";
        //标题不可为空
        if (title == "" || title.isEmpty()) {
            Toast.makeText(this, "标题不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        richEditor.setHtmlTitle(title);
        String htmlStr = richEditor.createHtmlStr();

        try {
            //文件目录：内部存储文件路径/myDiary
            File myDiaryPath = new File(getFilesDir().getAbsolutePath()+File.separator+"myDiary" );
            if (!myDiaryPath.exists()) {
                myDiaryPath.mkdir();
            }
            //不能包含一样标题的文件
            for (File files : myDiaryPath.listFiles()) {
                if (files.getName().equals(filename) && !lastSave) {
                    Toast.makeText(this, "你已经有一样标题的日记了", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            lastSave = true;
            File myDiary = new File(myDiaryPath, filename);
            FileOutputStream outputStream = new FileOutputStream(myDiary);
            outputStream.write(htmlStr.getBytes());
            outputStream.close();
/*
            //检查文件内容
            String content = "";
            InputStreamReader reader = new InputStreamReader(new FileInputStream(myDiary));
            BufferedReader bufferReader = new BufferedReader(reader);
            String parts = null;
            while ((parts = bufferReader.readLine()) != null)
                content += parts;
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
            reader.close();
            */
        } catch (Exception e) {

        }
        //保存到文章标题到文件title.txt
        try {
            File titleFile = new File(getFilesDir(), "title.txt");
            FileOutputStream titleOutput = new FileOutputStream(titleFile, true);
            titleOutput.write(title.getBytes());
            titleOutput.write("\n".getBytes());
            titleOutput.close();
        } catch (Exception e) {

        }


    }




}
