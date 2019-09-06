package edu.buu.daowe.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.callback.BosProgressCallback;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.baidubce.services.bos.model.PutObjectRequest;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.BosUtils;
import edu.buu.daowe.Util.MyTimeUtils;
import edu.buu.daowe.activity.CameraholidayActivity;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @author: lty
 * @time: 2019/9/3   ------   12:35
 */
public class ApplicateFragment extends Fragment implements View.OnFocusChangeListener, TextWatcher {
    EditText edit_inputreason;
    TextView tvtime, tv_hintzp;
    boolean timepickflag = false;
    boolean imgflag = false;
    String filename;
    Button btn_submit;
    ImageView imgupload;
    private Calendar calendar;
    Spinner startsp, endsp, typeidsp;
    DaoWeApplication app;
    int kcidlength = -1;
    int startidselectposition = -1;
    byte[] picdata;
    ProgressDialog psdialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        picdata = bundle.getByteArray("bitmapdata");
//        Bitmap bitmap = bundle.getParcelableArray("bitmap");
        if (imgupload != null) {
            final Bitmap bm = BitmapFactory.decodeByteArray(picdata, 0, picdata.length);
            imgupload.setImageBitmap(bm);
            imgflag = true;
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();
        tv_hintzp = getView().findViewById(R.id.tv_hintzp);
        imgupload = getView().findViewById(R.id.img_upload);
        btn_submit = getView().findViewById(R.id.bt_submit);
        imgupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CameraholidayActivity.class), 0x520);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_inputreason.getText().toString().length() == 0 || edit_inputreason.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请输入请假原因！", Toast.LENGTH_SHORT).show();
                } else if (timepickflag == false) {
                    Toast.makeText(getActivity(), "请选择请假时间", Toast.LENGTH_SHORT).show();
                } else if (endsp.getSelectedItemPosition() < startsp.getSelectedItemPosition()) {
                    endsp.setSelection(kcidlength);
                    Toast.makeText(getActivity(), "你使用了不明力量，想要触发这个bug，被我发现了已为你更正", Toast.LENGTH_SHORT).show();
                } else if (kcidlength <= 0) {
                    Toast.makeText(getActivity(), "这天没有课不能请假", Toast.LENGTH_SHORT).show();
                } else {
                    if (typeidsp.getSelectedItemPosition() == 1) {
                        if (picdata != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ObjectMetadata meta = new ObjectMetadata();

                                    // 自定义元数据
                                    meta.setContentType("image/jpeg");
                                    BosClient client = new BosClient(BosUtils.initBosClientConfiguration());    //创建BOSClient实例
                                    // 获取数据流
                                    // 以数据流形式上传Object
                                    ObjectMetadata objectMetadata = new ObjectMetadata();
                                    objectMetadata.setContentType("image/jpeg");
                                    filename = app.getStuid() + "-" + System.currentTimeMillis() + ".jpg";
                                    PutObjectRequest request = new PutObjectRequest(BaseRequest.LEAVEREQ, filename,
                                            new ByteArrayInputStream(picdata), objectMetadata);
                                    request.setObjectMetadata(objectMetadata);
                                    request.setProgressCallback(new BosProgressCallback<PutObjectRequest>() {
                                        @Override
                                        public void onProgress(final PutObjectRequest request, final long currentSize, final long totalSize) {
                                            super.onProgress(request, currentSize, totalSize);
                                            if (currentSize == totalSize) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //   Log.e("dddddddd","ddddddd");
                                                        Toast.makeText(getActivity(), "请求上传成功", Toast.LENGTH_SHORT).show();
                                                        psdialog.dismiss();
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                JSONObject reqdata = null;
                                                                //Log.e("tagtagtag","failed");
                                                                int typeid = -1;
                                                                if (typeidsp.getSelectedItemPosition() == 0) {
                                                                    typeid = 4;
                                                                } else {
                                                                    typeid = 3;
                                                                }
                                                                try {
                                                                    reqdata = new JSONObject();
                                                                    reqdata.put("leaveReason", typeid);
                                                                    reqdata.put("studentId", app.getStuid());
                                                                    reqdata.put("leaveDate", tvtime.getText());
                                                                    reqdata.put("imageUrl", "https://doways-leavereq.cdn.bcebos.com/" + filename);
                                                                    reqdata.put("leaveContent", edit_inputreason.getText());
                                                                    reqdata.put("startNumber", Integer.parseInt(startsp.getSelectedItem().toString()));
                                                                    reqdata.put("endNumber", Integer.parseInt(endsp.getSelectedItem().toString()));

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                Log.e("datadata", reqdata.toString());
                                                                OkHttpUtils.postString().content(reqdata.toString())
                                                                        .addHeader("Authorization", "Bearer " + app.getToken())
                                                                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                                        .url(BaseRequest.BASEURL + "users/leave/application").build().execute(new StringCallback() {
                                                                    @Override
                                                                    public void onError(Call call, Exception e, int id) {
                                                                        Log.e("tagtagtag", e.toString());
                                                                    }

                                                                    @Override
                                                                    public void onResponse(String response, int id) {
                                                                        Log.e("tagtagtag", response);
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                    }
                                                });

                                            } else if (currentSize == 2048) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        psdialog = new ProgressDialog(getActivity());
                                                        psdialog.setTitle("处理请求中");
                                                        psdialog.setMessage("正在上传假条和请假信息，请稍后");


                                                    }
                                                });
                                            }

                                            Log.e("size" + currentSize, "total" + totalSize);


                                        }
                                    });
                                    final String e22Tag = client.putObject(request).getETag();

//                        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).addHeader("Content-Type", "application/json")
//                                .url(BaseRequest.BASEURL + "tools/" + app.getStuid() + "/face").build().execute(new StringCallback() {
//                            @Override
//                            public void onError(Call call, Exception e, int id) {
//                                // Log.e(TAG,e.toString());
//                            }
//
//                            @Override
//                            public void onResponse(String response, int id) {
//
//                            }
//                        });
                                }
                            }).start();
                        } else {
                            Toast.makeText(getActivity(), "您还没有上传图片！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject reqdata = null;
                                int typeid = -1;
                                if (typeidsp.getSelectedItemPosition() == 0) {
                                    typeid = 4;
                                } else {
                                    typeid = 3;
                                }
                                try {
                                    reqdata = new JSONObject();

                                    reqdata.put("leaveReason", typeid);
                                    reqdata.put("studentId", app.getStuid());
                                    reqdata.put("leaveDate", tvtime.getText());
                                    reqdata.put("leaveContent", edit_inputreason.getText());
                                    reqdata.put("startNumber", Integer.parseInt(startsp.getSelectedItem().toString()));
                                    reqdata.put("endNumber", Integer.parseInt(endsp.getSelectedItem().toString()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.e("datadata", reqdata.toString());
                                OkHttpUtils.postString().content(reqdata.toString())
                                        .addHeader("Authorization", "Bearer " + app.getToken())
                                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                        .url(BaseRequest.BASEURL + "users/leave/application").build().execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        //     Log.e("tagtagtag",e.toString());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        try {
                                            JSONObject getdata = new JSONObject(response);
                                            if (getdata.getInt("code") == 200) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getActivity(), "请假请求发出成功，请等待老师审批！", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getActivity(), "您之前发出的请假请求已经包含本次所选课程了！", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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
            }
        });
        startsp = getView().findViewById(R.id.startsp);
        endsp = getView().findViewById(R.id.endsp);
        typeidsp = getView().findViewById(R.id.typeid);
        edit_inputreason = getView().findViewById(R.id.et_reason);
        tvtime = getView().findViewById(R.id.tv_select_time);
        typeidsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    tv_hintzp.setVisibility(View.GONE);
                    imgupload.setVisibility(View.GONE);
                } else {
                    tv_hintzp.setVisibility(View.VISIBLE);
                    imgupload.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                DatePickerDialog dpdialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {

                                // 更新EditText控件日期 小于10加0
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                final String userselecttime = format.format(calendar.getTime());
                                tvtime.setText(userselecttime);
                                timepickflag = true;


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OkHttpUtils.get().addHeader("Authorization", " Bearer " + app.getToken()).url(
                                                BaseRequest.BASEURL + "users/" + app.getStuid() + "/" + userselecttime + "/classes/id"
                                        ).build().execute(new StringCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {

                                            }

                                            @Override
                                            public void onResponse(String response, int id) {
                                                Log.e("resresres", response + "");
                                                try {
                                                    JSONObject responsedata = new JSONObject(response);
                                                    if (responsedata.getInt("code") == 200) {
                                                        JSONArray arrdata = responsedata.getJSONArray("data");
                                                        if (arrdata.length() == 0) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getActivity(), "这天没有课程安排哦～", Toast.LENGTH_SHORT).show();
                                                                    kcidlength = -1;
                                                                }
                                                            });

                                                        } else {
                                                            List haskcid = new ArrayList();
                                                            kcidlength = arrdata.length() - 1;
                                                            try {
                                                                for (int i = 0; i < arrdata.length(); i++) {
                                                                    haskcid.add(Integer.parseInt(arrdata.getString(i)));
                                                                }
                                                            } catch (NumberFormatException e) {
                                                                e.printStackTrace();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, haskcid);
                                                            startsp.setAdapter(_Adapter);
                                                            endsp.setAdapter(_Adapter);
                                                            endsp.setSelection(_Adapter.getCount() - 1);
                                                            startsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    startidselectposition = position;
                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });
                                                            endsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    if (position < startidselectposition) {
                                                                        Toast.makeText(getActivity(), "请注意结束请假节数应大于等于开始请假节数", Toast.LENGTH_SHORT).show();
                                                                        endsp.setSelection(kcidlength);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });
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
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


                dpdialog.getDatePicker().setMinDate(System.currentTimeMillis());  //设置日期最大值
                dpdialog.getDatePicker().setMaxDate(MyTimeUtils.getMonthEndTime(System.currentTimeMillis())); //设置日期最小值


                dpdialog.setCancelable(false);
                dpdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        tvtime.setText("选择请假日期");
                        timepickflag = false;
                    }
                });


//                dpdialog.setButton(DialogInterface.BUTTON_NEGATIVE, "我在想想", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == DialogInterface.BUTTON_NEGATIVE) {
//
//
//                        }
//                        else{
//                            // Do Stuff
//                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                            final String userselecttime = format.format(calendar.getTime());
//                            tvtime.setText(userselecttime+"");
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    OkHttpUtils.get().addHeader("Authorization", " Bearer " + app.getToken()).url(
//                                            BaseRequest.BASEURL + "users/"+app.getStuid()+"/"+userselecttime+"/classes/id"
//                                    ).build().execute(new StringCallback() {
//                                        @Override
//                                        public void onError(Call call, Exception e, int id) {
//
//                                        }
//
//                                        @Override
//                                        public void onResponse(String response, int id) {
//                                            Log.e("resresres",response+"");
//                                        }
//                                    });
//                                }
//                            }).start();
//                        }
//
//                    }
//                });


                dpdialog.show();

            }
        });
        edit_inputreason.setOnFocusChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_canceljq,null);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_reason) {
            if (hasFocus) {
                edit_inputreason.setActivated(true);

            }
        } else {
         edit_inputreason.setActivated(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(edit_inputreason.getText())) {
            btn_submit.setBackgroundResource(R.drawable.bg_login_submit);
            btn_submit.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn_submit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            btn_submit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }
}
