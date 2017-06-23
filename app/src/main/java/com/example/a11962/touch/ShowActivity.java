package com.example.a11962.touch;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ShowActivity extends AppCompatActivity {

    private String title = "";
    private WebView webview;
    private String dirPath;
    private String suffix;
    private Toolbar showToolbar;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        showToolbar = (Toolbar)findViewById(R.id.showToolbar);
        webview = (WebView)findViewById(R.id.showWeb);
        setSupportActionBar(showToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        suffix = intent.getStringExtra("suffix");
        from = intent.getStringExtra("from");

        loadDiary();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //返回上一级
            case android.R.id.home:
                if (from.equals("TouchActivity")) {
                    Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();

                } else {
                    finish();
                }
                break;
        }
        return true;
    }
    //返回键按下
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //显示日记内容
    public void loadDiary() {
        try {
            String content = "";
            File myDiaryPath = new File(getFilesDir().getAbsolutePath()+File.separator+suffix);
            File myDiary = new File(myDiaryPath, title+".html");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(myDiary));
            BufferedReader bufferReader = new BufferedReader(reader);
            String parts = null;
            while ((parts = bufferReader.readLine()) != null)
                content += parts;
            reader.close();

            webview.loadData(content, "text/html; charset=UTF-8", null);
        } catch(Exception e) {

        }


    }
}
