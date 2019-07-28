package edu.buu.daowe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import okhttp3.Call;

/**
 * @author: lty
 * @time: 2019/7/6   ------   19:37
 */
public class CancellationFragment extends Fragment {
        DaoWeApplication app;
    TextView tv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cancellation,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = view.findViewById(R.id.tveeeee);
        OkHttpUtils.get().url("http://suggest.taobao.com/sug?code=utf-8&q=3900X").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                tv.setText("网络错误");
            }

            @Override
            public void onResponse(String response, int id) {
                    tv.setText(response);
            }
        });




    }
}
