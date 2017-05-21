package com.example.a11962.touch.chat;

/**
 * Created by 11962 on 2017/5/21.
 * 定义用于实现日记记录的Msg类型
 */
public class Msg
{
    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;
    private String content;
    private int type;

    public Msg(String content, int type)
    {
        this.content = content;
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public int getType()
    {
        return type;
    }
}
