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

import edu.buu.daowe.R;
import edu.buu.daowe.http.BaseRequest;
import edu.buu.daowe.thread.VCodeSendCounter;
import okhttp3.Call;

public class Register_one_Fragment extends Fragment implements TextWatcher {
    boolean smssuccess = false;
    CheckBox cb;
    boolean srhf = false;
    EditText editsms, editphone;
    TextView tvsmscall;
    ImageButton img_btn;
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
        return inflater.inflate(R.layout.activity_main_register_step_one, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        mBtRegSubmit = view.findViewById(R.id.bt_register_submit);
        cb = view.findViewById(R.id.cb_protocol);
        tvsmscall = view.findViewById(R.id.tv_register_sms_call);
        editphone = view.findViewById(R.id.et_register_phonenum);
        editsms = view.findViewById(R.id.et_register_auth_code);
        editphone.addTextChangedListener(this);
        editsms.addTextChangedListener(this);
        img_btn = view.findViewById(R.id.ib_navigation_back);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        tvsmscall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View l_view) {
                VCodeSendCounter vCodeSendCounter = new VCodeSendCounter(tvsmscall, 60000, 1000);
                vCodeSendCounter.start();
                OkHttpUtils.get().url(BaseRequest.BASEURL + "sms/" + editphone.getText().toString()).build().execute(new StringCallback() {
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
                    Bundle data = new Bundle();
                    data.putString("vrcode", editsms.getText().toString());
                    data.putString("phone", editphone.getText().toString());
                    Register_two_Fragment two = new Register_two_Fragment();
                    two.setArguments(data);
                    transaction.replace(R.id.reg_frame, two);
                    transaction.commit();
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


        //登录按钮是否可用
        if (!TextUtils.isEmpty(phonenu) && !TextUtils.isEmpty(smsstatus)) {
            srhf = true;

        } else {
            mBtRegSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
            mBtRegSubmit.setClickable(false);
            srhf = false;
        }
    }
}
