package edu.buu.daowe.Util;

import android.util.Log;

import java.util.Calendar;

public class MyTimeUtils {


    public static String fun(int starttime_hour, int startime_min, int endtime_hour, int endtime_min) {
        Log.e("startime:" + starttime_hour + "" + startime_min, "endtime:" + endtime_hour + "" + endtime_min);
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = starttime_hour * 60 + startime_min;// 起始时间 17:20的分钟数
        final int end = endtime_hour * 60 + endtime_min;// 结束时间 19:00的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            return "进行中";
        } else if (minuteOfDay <= start && minuteOfDay <= end) {
            return "未开始";
        } else {
            return "已结束";
        }
    }


}
