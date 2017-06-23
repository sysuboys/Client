package com.example.a11962.touch.network;

import com.example.a11962.touch.SessionUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 11962 on 2017/5/27.
 */
public class HttpUtils {
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /*根据url进行服务器访问登录，得到返回字符串，保存session*/
    public String loginAttempt(String url, String name, String pwd) throws IOException {
        //请求字符串转化为json
        RequestBody body = new FormBody.Builder().
                add("username", name).
                add("password", pwd).build();

        Request request = new Request.Builder().url(url).post(body).build();

        //得到响应
        Response response = client.newCall(request).execute();
        System.out.println(response.toString());
        List<String> cookies = response.headers().values("Set-Cookie");
        if (cookies != null) {
            String session = cookies.get(0);
            //SessionUtil.SESSIONID = session.substring(0, session.indexOf(";"));
        }
        return response.body().string();
    }
    /*根据url进行服务器访问登录，得到返回字符串，保存session*/
    public String uploadAttempt(String url, String title, String content) throws IOException {
        //请求字符串转化为json
        RequestBody body = new FormBody.Builder().add("title", title).add("body", content).build();
        Request request = new Request.Builder().url(url).
                addHeader("Cookie", null).post(body).build();

        //得到响应
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

}
