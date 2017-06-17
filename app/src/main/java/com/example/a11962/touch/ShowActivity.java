package com.example.a11962.touch;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
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
    private Toolbar showToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        showToolbar = (Toolbar)findViewById(R.id.showToolbar);
        webview = (WebView)findViewById(R.id.showWeb);
        setSupportActionBar(showToolbar);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");

        loadDiary();
    }
    //显示日记内容
    public void loadDiary() {
        try {
            String content = "";
            File myDiaryPath = new File(getFilesDir().getAbsolutePath()+File.separator+"myDiary" );
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
