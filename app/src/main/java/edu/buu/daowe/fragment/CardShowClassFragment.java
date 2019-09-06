package edu.buu.daowe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.MyTimeUtils;
import edu.buu.daowe.adapter.CardData;
import edu.buu.daowe.adapter.RecyclerViewAdapter;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;

public class CardShowClassFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<CardData> dataList;
    DaoWeApplication app;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardview_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        app = (DaoWeApplication) getActivity().getApplication();
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);



        //初始化数据集
        initData();
        //初始化recyclerView


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_autoqj) {
            Map piliangdata = mAdapter.getPiliangdata();
            Set<Map.Entry<Integer, String>> entrySet = piliangdata.entrySet();

            Iterator<Map.Entry<Integer, String>> it = entrySet.iterator();

            while (it.hasNext()) {
                Map.Entry temp = it.next();
                String key = (String) temp.getKey() + "";
                String value = (String) temp.getValue() + "";
                //  Log.e(key, value);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        recyclerView = getView().findViewById(R.id.recycler_view);
        final ArrayList timeId = new ArrayList();
        Log.e("TAG", timeId.toString());
        final ArrayList courseName = new ArrayList();
        final ArrayList buildingName = new ArrayList();
        final ArrayList roomName = new ArrayList();
        final ArrayList startTime = new ArrayList();
        final ArrayList endTime = new ArrayList();
        final ArrayList name = new ArrayList();
        final ArrayList coursestatus = new ArrayList();
        final ArrayList major = new ArrayList();
        final ArrayList minor = new ArrayList();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken())
                                .url(BaseRequest.BASEURL + "users/" + app.getStuid() + "/course").build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //     Log.e("TAG","call"+e.toString());
                                // Log.e("ttttttttttttttt","nbbbnnnnnnnnnn");
                            }

                            @Override
                            public void onResponse(String response, int id) {

                                dataList = new ArrayList<>();
                                JSONArray dataarray;
                                //    Log.e("TAG",response);
                                ArrayList jcdatastarttemp = new ArrayList();
                                ArrayList jcdataendtemp = new ArrayList();
                                ArrayList jcdata = new ArrayList();
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getInt("code") == 200) {

                                        dataarray = object.getJSONArray("data");//Log.e("ttttttttttttttt","llllllllllllll");
                                        if (dataarray.length() == 0) {
                                            //  Log.e("TAG","66666666666666");

                                            getFragmentManager().beginTransaction().replace(R.id.classno, new CardShowClassNoCourse(), "nocourse").commit();
                                        }
                                        Log.e("time", dataarray.toString());

                                        for (int i = 0; i < dataarray.length(); i++) {
                                            String[] etime = dataarray.getJSONObject(i).getString("endTime").split(":");
                                            String[] stime = dataarray.getJSONObject(i).getString("startTime").split(":");
                                            jcdatastarttemp.add(stime);
                                            jcdataendtemp.add(etime);
                                            String status = MyTimeUtils.fun(Integer.valueOf(stime[0]), Integer.valueOf(stime[1]), Integer.valueOf(etime[0]), Integer.valueOf(etime[1]));
                                            timeId.add(dataarray.getJSONObject(i).getString("timeId"));
                                            courseName.add(dataarray.getJSONObject(i).getString("courseName"));
                                            buildingName.add(dataarray.getJSONObject(i).getString("buildingName"));
                                        //    courseName.add(dataarray.getJSONObject(i).getString("courseName"));
                                            roomName.add(dataarray.getJSONObject(i).getInt("floorsMajor") + "0" + dataarray.getJSONObject(i).getInt("roomMinor"));
                                            startTime.add(dataarray.getJSONObject(i).getString("startTime"));
                                            endTime.add(dataarray.getJSONObject(i).getString("endTime"));
                                            //    roomName.add(dataarray.getJSONObject(i).getString("roomName"));
                                            name.add(dataarray.getJSONObject(i).getString("name"));

                                            major.add(dataarray.getJSONObject(i).getInt("floorsMajor"));
                                            minor.add(dataarray.getJSONObject(i).getInt("roomMinor"));

                                            coursestatus.add(status);

                                        }
                                        jcdata.add(0, jcdatastarttemp);
                                        jcdata.add(1, jcdataendtemp);
                                        app.setJctime(jcdata);

                                        for (int i = 0; i < timeId.size(); i++) {
                                            //  Log.e("smsmsmsmsmsmsmsmsm",coursestatus.get(i)+"");
                                            dataList.add(new CardData(new SimpleDateFormat("yyyy年MM月dd日").format(new Date())
                                                    + "", name.get(i) + "", buildingName.get(i) + "-" + roomName.get(i),
                                                    startTime.get(i) + "-" + endTime.get(i), timeId.get(i) + "",
                                                    courseName.get(i) + "", coursestatus.get(i) + ""));
                                        }
                                        Log.e("tag", dataList.toString() + "");


                                        RecyclerView.LayoutManager linearManager = new LinearLayoutManager(getActivity());
                                        mAdapter = new RecyclerViewAdapter(getActivity(), dataList, major, minor, app);

                                        //设置Adapter
                                        recyclerView.setAdapter(mAdapter);
                                        //设置布局类型为线性布局
                                        recyclerView.setLayoutManager(linearManager);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();

    }


}
