package edu.buu.daowe.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import edu.buu.daowe.R;
import edu.buu.daowe.Util.PassWordUtil;
import edu.buu.daowe.http.BaseRequest;
import edu.buu.daowe.thread.VCodeSendCounter;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class Register_two_Fragment extends Fragment implements TextWatcher {


    Bundle data;
    OkHttpClient okHttpClient;
    CookieJarImpl cookieJar;
    boolean smssuccess = false;
    CheckBox cb;
    boolean srhf = false;
    EditText editsms, editphone, etpass;
    TextView tvsmscall;
    ImageButton img_btn;
    Button mBtRegSubmit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_register_step_one, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = getArguments();
        etpass = view.findViewById(R.id.et_register_pwd_input);
        mBtRegSubmit = view.findViewById(R.id.bt_register_submit);
        cb = view.findViewById(R.id.cb_protocol);
        tvsmscall = view.findViewById(R.id.tv_register_sms_call);
        editphone = view.findViewById(R.id.et_register_phonenum);
        editsms = view.findViewById(R.id.et_register_auth_code);
        editphone.addTextChangedListener(this);
        editsms.addTextChangedListener(this);
        etpass.addTextChangedListener(this);
        img_btn = view.findViewById(R.id.ib_navigation_back);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        cookieJar = new CookieJarImpl(new PersistentCookieStore(getActivity()));
//        ClearableCookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getActivity()));
        okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        tvsmscall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View l_view) {
                VCodeSendCounter vCodeSendCounter = new VCodeSendCounter(tvsmscall, 60000, 1000);
                vCodeSendCounter.start();

                OkHttpUtils.initClient(okHttpClient).get().url(BaseRequest.BASEURL + "sms/" + editphone.getText().toString()).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        smssuccess = false;
                        Toast.makeText(getActivity(), "发送失败请您检查网络状态", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        smssuccess = true;
                        Toast.makeText(getActivity(), "验证码发送成功请查收", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        mBtRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (smssuccess == true) {
                    // Toast.makeText(getActivity(),"手机验证通过请输入您的详细信息",Toast.LENGTH_SHORT).show();
                    JSONObject dataob = null;
                    try {
                        dataob = new JSONObject();
                        dataob.put("phoneNumber", editphone.getText().toString());
                        dataob.put("smsCode", editsms.getText().toString());
                        dataob.put("passwordHash", PassWordUtil.encode(PassWordUtil.encode(etpass.getText().toString())));
                        //    Log.e("decode", PassWordUtil.decode(PassWordUtil.encode(etpass.getText().toString())));
                        dataob.put("id", data.getString("stuid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    OkHttpUtils.getInstance().postString().content(dataob.toString()).mediaType(MediaType.parse("application/json; charset=utf-8"))
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
                                    Toast.makeText(getActivity(), "用户注册成功！", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "您还没有通过验证码认证，请验证后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true && srhf == true) {
                    mBtRegSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                    mBtRegSubmit.setTextColor(getResources().getColor(R.color.white));
                    mBtRegSubmit.setClickable(true);
                } else {
                    mBtRegSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    mBtRegSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                    mBtRegSubmit.setClickable(false);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String smsstatus = editsms.getText().toString().trim();
        String phonenu = editphone.getText().toString().trim();
        String password = etpass.getText().toString().trim();

        //登录按钮是否可用
        if (!TextUtils.isEmpty(phonenu) && !TextUtils.isEmpty(smsstatus) && !TextUtils.isEmpty(password)) {
            srhf = true;

        } else {
            mBtRegSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
            mBtRegSubmit.setClickable(false);
            srhf = false;
        }
    }
}

