package edu.buu.daowe.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.buu.daowe.R;
import edu.buu.daowe.activity.memo.EditActivity;

/**
 * @author: lty
 * @time: 2019/9/3   ------   12:35
 */
public class ApplicateFragment extends Fragment implements View.OnFocusChangeListener {
    EditText edit_inputreason;
    TextView tvtime;
    private Calendar calendar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edit_inputreason = getView().findViewById(R.id.et_reason);
        tvtime = getView().findViewById(R.id.tv_select_time);
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

                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            //    dpdialog.getDatePicker().setMaxDate();  //设置日期最大值
             //   dpdialog.getDatePicker().setMinDate(); //设置日期最小值


//                final TimePickerDialog tpdialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
//                        calendar.set(Calendar.HOUR, i);
//                        calendar.set(Calendar.MINUTE, i1);
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                        tvtime.setText(format.format(calendar.getTime()));
//                    }
//                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
//                dpdialog.show();
//                dpdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        tpdialog.show();
//                    }
//                });


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
}
