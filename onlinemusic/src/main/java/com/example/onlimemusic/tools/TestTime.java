package com.example.onlimemusic.tools;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class TestTime {
    public static void main(String[] args) {
        //把时间格式化
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //格式化成字符串
        String time = sf.format(new Date());

        System.out.println("当前的时间" + time);

    }
}
