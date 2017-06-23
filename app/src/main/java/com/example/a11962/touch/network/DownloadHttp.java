package com.example.a11962.touch.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.a11962.touch.ChatUtils;
import com.example.a11962.touch.SessionUtil;
import com.example.a11962.touch.ShowActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 11962 on 2017/6/21.
 * match成功后下载文件
 */
public class DownloadHttp extends Thread {
    private OkHttpClient mOkHttpClient;
    private String downloadUrl;
    private Context context;
    public DownloadHttp(String url, Context context) {
        downloadUrl = url;
        this.context = context;
    }


    @Override
    public void run() {
        mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Cookie", SessionUtil.SESSIONID)
                .url(downloadUrl)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                //文件目录：内部存储文件路径/myDiary
                File friDiaryPath =
                        new File(context.getFilesDir().getAbsolutePath()+File.separator+ ChatUtils.friend);
                //String disposition = response.header("Content-Disposition");
                //disposition = new String(disposition.getBytes("iso8859-1"), "gb2312");
                //ChatUtils.friTitle = disposition.substring(disposition.lastIndexOf('=')+2, disposition.length()-1);

                if (!friDiaryPath.exists()) {
                    friDiaryPath.mkdir();
                }
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(friDiaryPath, ChatUtils.friTitle);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        /*int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.arg1 = progress;
                        mHandler.sendMessage(msg);*/
                    }
                    fos.flush();

                    saveRecord();
                    Intent intent = new Intent(context, ShowActivity.class);
                    intent.putExtra("title",
                            ChatUtils.friTitle.substring(0, ChatUtils.friTitle.lastIndexOf(".")));
                    intent.putExtra("suffix", ChatUtils.friend);
                    intent.putExtra("from", "TouchActivity");
                    context.startActivity(intent);
                    Log.d("h_bl", "文件下载成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("h_bl", "文件下载失败");
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }


        });
    }
    public void saveRecord() {
        SharedPreferences record =
                context.getSharedPreferences(ChatUtils.friend, context.MODE_PRIVATE);
        SharedPreferences.Editor rEditor = record.edit();
        String titles = record.getString("diaries", "");
        String list = record.getString("type", "");
        if (ChatUtils.isIvited) {
            titles += ChatUtils.friTitle.substring(0, ChatUtils.friTitle.lastIndexOf('.')) +":"
                    +ChatUtils.myTitle.substring(0, ChatUtils.myTitle.lastIndexOf('.'))+":";
            list += "0:1:";
        } else {
            titles +=ChatUtils.myTitle.substring(0, ChatUtils.myTitle.lastIndexOf('.'))+":"
                    + ChatUtils.friTitle.substring(0, ChatUtils.friTitle.lastIndexOf('.'))+":";
            list += "1:0:";
        }
        rEditor.putString("diaries", titles).putString("type", list).commit();
    }

}
