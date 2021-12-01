package com.example.appweather.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public  static final String API_ID = "3fbb62a8024142917b9a91b892b8f53f";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss EEEE yyyy");
        String rs = sdf.format(date);
        return rs;
    }



    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String rs = sdf.format(date);
        return rs;
    }
}
