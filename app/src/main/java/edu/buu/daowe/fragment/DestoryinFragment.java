package edu.buu.daowe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.adapter.DestoryinAdapter;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;

public class DestoryinFragment extends Fragment {
    DaoWeApplication app;
    ArrayList endNumberList, startNumberList, leaveDateList, leaveReasonList;
    ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_destory, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();
        listView = getView().findViewById(R.id.mylist);
        endNumberList = new ArrayList();
        startNumberList = new ArrayList();
        leaveDateList = new ArrayList();
        leaveReasonList = new ArrayList();
        //  Log.e("dededededede","dededededededede");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("urlurl", BaseRequest.BASEURL + "users/"
                        + app.getStuid() + "/leave/");
                OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).url(BaseRequest.BASEURL + "users/"
                        + app.getStuid() + "/leave/").build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("erropr", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject getres = new JSONObject(response);
                            Log.e("tagtag", response);
                            if (getres.getInt("code") == 200) {
                                JSONArray datalist = getres.getJSONArray("data");
                                if (datalist.length() > 0) {
                                    for (int i = 0; i < datalist.length(); i++) {
                                        endNumberList.add(datalist.getJSONObject(i).getInt("endNumber"));
                                        startNumberList.add(datalist.getJSONObject(i).getInt("startNumber"));
                                        leaveDateList.add(datalist.getJSONObject(i).getString("leaveDate"));
                                        leaveReasonList.add(datalist.getJSONObject(i).getString("leaveReason"));
                                    }
                                    DestoryinAdapter myadapter = new DestoryinAdapter(leaveDateList, leaveReasonList, startNumberList, endNumberList, getActivity());
                                    listView.setAdapter(myadapter);


                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();


    }
}
