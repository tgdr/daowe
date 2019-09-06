package edu.buu.daowe.Util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class MyTimeUtils {


    public static String fun(int starttime_hour, int startime_min, int endtime_hour, int endtime_min) {
        Log.e("startime:" + starttime_hour + "" + startime_min, "endtime:" + endtime_hour + "" + endtime_min);
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = starttime_hour * 60 + startime_min;// 起始时间 17:20的分钟数
        final int end = endtime_hour * 60 + endtime_min;// 结束时间 19:00的分钟数

        long beginDate = 1568563200L;

        //   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Log.e("eeeeeeee"+new Date(beginDate).getMonth()+1,+(new Date(beginDate).getDay()+1)+"");



        if (minuteOfDay >= start && minuteOfDay <= end) {
            return "进行中";
        } else if (minuteOfDay <= start && minuteOfDay <= end) {
            return "未开始";
        } else {
            return "已结束";
        }
    }


    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Long getMonthEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }






}
