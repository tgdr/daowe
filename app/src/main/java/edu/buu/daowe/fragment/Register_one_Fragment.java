package edu.buu.daowe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import edu.buu.daowe.R;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;

public class Register_one_Fragment extends Fragment implements TextWatcher {
    EditText etstuid, etstuname;
    Button mBtRegSubmit;

    FragmentManager manager;
    FragmentTransaction transaction;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_register_step_two, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = getFragmentManager();
        transaction = manager.beginTransaction();


        etstuid = view.findViewById(R.id.et_register_username);
        etstuname = view.findViewById(R.id.et_register_stuname);
        etstuname.addTextChangedListener(this);
        mBtRegSubmit = view.findViewById(R.id.bt_register_submit);
        etstuid.addTextChangedListener(this);


        /**
         * 网络请求部分
         */

        mBtRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OkHttpUtils.get().url(BaseRequest.BASEURL + "auth/" + etstuid.getText().toString()
                        + "/").build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Bundle data = new Bundle();

                        Log.e("data", response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getInt("code") == 200) {
                                if (object.getString("data").equals(etstuname.getText().toString().trim())) {
                                    data.putString("stuid", etstuid.getText().toString());
                                    Register_two_Fragment two = new Register_two_Fragment();
                                    two.setArguments(data);
                                    transaction.replace(R.id.reg_frame, two);
                                    transaction.commit();
                                } else {
                                    Toast.makeText(getActivity(), "学号与姓名不匹配请检查！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "验证失败未定义的学号！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });



        /*
        mBtRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject dataob = new JSONObject();
                dataob.put("phoneNumber", data.getString("phone"));
                dataob.put("smsCode", data.getString("vrcode"));
                dataob.put("passwordHash", PassWordUtil.encode(etpass.getText().toString()));
                //    Log.e("decode", PassWordUtil.decode(PassWordUtil.encode(etpass.getText().toString())));
                dataob.put("id", etstuid.getText().toString());
                OkHttpUtils.postString().content(dataob.toJSONString()).mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .url(BaseRequest.BASEURL + "register").build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("注册失败！");
                        builder.setMessage("注册失败！发生错误！");
                        builder.setPositiveButton("好的我再试一次一下", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.setNegativeButton("算了直接登录吧！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("responseresponse", response);
                        try {
                            org.json.JSONObject res = new org.json.JSONObject(response);
                            if (res.getInt("code") == 404) {
                                Toast.makeText(getActivity(), "用户已经存在！请登录", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            } else if (res.getInt("code") == 200) {
                                Toast.makeText(getActivity(), "绑定手机号成功！" + id, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
*/

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
//登录按钮是否可用
        if (!TextUtils.isEmpty(etstuname.getText().toString()) && !TextUtils.isEmpty(etstuid.getText().toString())) {
            mBtRegSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtRegSubmit.setTextColor(getResources().getColor(R.color.white));
            mBtRegSubmit.setClickable(true);
        } else {
            mBtRegSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
            mBtRegSubmit.setClickable(false);
        }
    }
}
