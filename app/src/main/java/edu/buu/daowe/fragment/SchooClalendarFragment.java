package edu.buu.daowe.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import edu.buu.daowe.R;
import edu.buu.daowe.Util.MyTimeUtils;
import edu.buu.daowe.activity.memo.EditActivity;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;

public class SchooClalendarFragment extends BaseFragment implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener, CalendarView.OnCalendarLongClickListener,
        View.OnClickListener {
    int[] color = {0xFF40db25, 0xff00CED1, 0xff00BFFF, 0xff1E90FF, 0xff4169E1, 0xff0000CD, 0xff8A2BE2, 0xff9400D3};
    TextView mTextMonthDay;
    Map<String, Calendar> map;
    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    int year, month;
    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
    //GroupRecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_school_ca;
    }


    @Override
    protected void initView() {

        setStatusBarDarkMode();
        mTextMonthDay = getView().findViewById(R.id.tv_month_day);
        mTextYear = getView().findViewById(R.id.tv_year);
        mTextLunar = getView().findViewById(R.id.tv_lunar);
        mRelativeTool = getView().findViewById(R.id.rl_tool);
        mCalendarView = getView().findViewById(R.id.calendarView);
        mTextCurrentDay = getView().findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.INVISIBLE);
                mTextYear.setVisibility(View.INVISIBLE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        getView().findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
                //mCalendarView.addSchemeDate(getSchemeCalendar(2019, 6, 1, 0xFF40db25, "假"));
//                int year = 2019;
//                int month = 6;
//                Map<String, Calendar> map = new HashMap<>();
//                map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
//                        getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
//                map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
//                        getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
//                map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
//                        getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
//                map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
//                        getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
//                mCalendarView.addSchemeDate(map);
            }
        });
        mCalendarLayout = getView().findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, true);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    @Override
    protected void initData() {
        year = mCalendarView.getCurYear();
        month = mCalendarView.getCurMonth() + 1;
        map = new HashMap<>();

        OkHttpUtils.get().addHeader("Authorization", " Bearer " + app.getToken()).url(BaseRequest.BASEURL + "users/" + app.getStuid() + "/termtime/2019/to/2020/1"
        ).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("cacacaca", e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("dadadadadada", response);
                JSONArray datalist;
                datalist = new JSONArray();
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("code") == 200) {
                        datalist = data.getJSONArray("data");
                        for (int i = 0; i < datalist.length(); i++) {

                            int weekNumber = datalist.getJSONObject(i).getInt("weekNumber");
                            LocalDateTime startTime = MyTimeUtils.getDateTimeOfTimestamp(datalist.getJSONObject(i).getLong("startTime"));
                            LocalDateTime endTime = MyTimeUtils.getDateTimeOfTimestamp(datalist.getJSONObject(i).getLong("endTime"));
                            Log.e("dddddddd", startTime.getDayOfYear() + " end" + endTime.getDayOfYear());
                            int starttime = startTime.getDayOfYear();
                            int endtime = endTime.getDayOfYear();
                            int colorindex = 0;
                            while (starttime < endtime - 1) {

                                map.put(getSchemeCalendar(year, startTime.getMonthValue(), startTime.getDayOfMonth(), color[colorindex], "第" + weekNumber + "周").toString(),
                                        getSchemeCalendar(year, startTime.getMonthValue(), startTime.getDayOfMonth(), color[colorindex], "第" + weekNumber + "周"));
                                starttime++;
                                startTime = startTime.plusDays(1);
                                colorindex++;


                            }
                            map.put(getSchemeCalendar(year, 10, 1, 0xFFbc13f0, "国庆").toString(),
                                    getSchemeCalendar(year, 10, 1, 0xFFbc13f0, "国庆"));

                            //    startTime.getMonthValue();

                            //     Log.e("startmonth:"+(dt.getMonthValue()),"startday:"+(dt.getDayOfMonth()));
                            //    Log.e("endmonth:"+(enddate.getMonth()),"endday:"+(enddate.getDay()+1));
                            Log.e("weeknum", weekNumber + "");
                            //Log.e("s",endTime+"");
//                                                           while (startday<endday && startmonth<endmonth){
//                                                               map.put(getSchemeCalendar(year, startmonth, startday, 0xFF40db25, "第"+weekNumber+"周").toString(),
//                                                                       getSchemeCalendar(year, startmonth, startday, 0xFF40db25, "第"+weekNumber+"周"));
//                                                           Log.e("startmonth",startmonth+"");
//                                                               Log.e("startday",startday+"");
//                                                               startday++;
//                                                           }
                            mCalendarView.setSchemeDate(map);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


//        map.put(getSchemeCalendar(year, month, 4, 0xFF40db25, "第一周").toString(),
//                getSchemeCalendar(year, month, 4, 0xFF40db25, "第一周"));
//        map.put(getSchemeCalendar(year, month, 5, 0xFF40db25, "第一周").toString(),
//                getSchemeCalendar(year, month, 5, 0xFF40db25, "第一周"));
//        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
//                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
//        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
//                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
//        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
//                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
//        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
//                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
//        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
//                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
//        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
//                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
//        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
//                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
//        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多").toString(),
//                getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用



    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_flyme:
//                CustomActivity.show(this);
//                break;
//            case R.id.ll_simple:
//                SimpleActivity.show(this);
//                break;
//            case R.id.ll_colorful:
//                ColorfulActivity.show(this);
//                break;
//            case R.id.ll_index:
//                IndexActivity.show(this);
//                break;
//        }
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {
        Toast.makeText(getActivity(), String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        Intent memointent = new Intent(getActivity(), EditActivity.class);
        memointent.putExtra("memoyear", calendar.getYear());
        memointent.putExtra("memomonth", calendar.getMonth());
        memointent.putExtra("memoday", calendar.getDay());
        memointent.putExtra("flag", 0);
        memointent.putExtra("login_user", app.getStuid());
        startActivity(memointent);
        // Toast.makeText(getActivity(), "长按添加备忘录\n" , Toast.LENGTH_SHORT).show();

    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();


        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }


}
