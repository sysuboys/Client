package com.example.a11962.touch;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 11962 on 2017/5/26.
 */
public class SessionUtil {

    public static String SESSIONID = null;

    //持久化session id
    public static void setSESSIONID(Context contxt, String id) {
        SharedPreferences preferences = contxt.getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sessionid", id).commit();

    }
    public static String getSESSION(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("info", Context.MODE_PRIVATE);
        return preferences.getString("sessionid", null);
    }

}
