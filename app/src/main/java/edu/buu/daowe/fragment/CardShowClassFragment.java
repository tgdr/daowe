package edu.buu.daowe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
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
        recyclerView = view.findViewById(R.id.recycler_view);


        //初始化数据集
        initData();
        //初始化recyclerView


    }

    private void initData() {
        final ArrayList timeId = new ArrayList();
        Log.e("TAG", timeId.toString());
        final ArrayList courseName = new ArrayList();
        final ArrayList buildingName = new ArrayList();
        final ArrayList roomName = new ArrayList();
        final ArrayList startTime = new ArrayList();
        final ArrayList endTime = new ArrayList();
        final ArrayList name = new ArrayList();

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

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                dataList = new ArrayList<>();
                                JSONArray dataarray;
                                //    Log.e("TAG",response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getInt("code") == 200) {

                                        dataarray = object.getJSONArray("data");
                                        for (int i = 0; i < dataarray.length(); i++) {
                                            timeId.add(dataarray.getJSONObject(i).getString("timeId"));
                                            courseName.add(dataarray.getJSONObject(i).getString("courseName"));
                                            buildingName.add(dataarray.getJSONObject(i).getString("buildingName"));
                                            courseName.add(dataarray.getJSONObject(i).getString("courseName"));
                                            roomName.add(dataarray.getJSONObject(i).getString("roomName"));
                                            startTime.add(dataarray.getJSONObject(i).getString("startTime"));
                                            endTime.add(dataarray.getJSONObject(i).getString("endTime"));
                                            roomName.add(dataarray.getJSONObject(i).getString("roomName"));
                                            name.add(dataarray.getJSONObject(i).getString("name"));
                                        }
                                        for (int i = 0; i < timeId.size(); i++) {
                                            dataList.add(new CardData(new SimpleDateFormat("yyyy年MM月dd日").format(new Date())
                                                    + "", name.get(i) + "", buildingName.get(i) + "-" + roomName.get(i), startTime.get(i) + "-" + endTime.get(i), timeId.get(i) + "", false, courseName.get(i) + ""));
                                        }
                                        RecyclerView.LayoutManager linearManager = new LinearLayoutManager(getActivity());
                                        mAdapter = new RecyclerViewAdapter(getActivity(), dataList);

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
