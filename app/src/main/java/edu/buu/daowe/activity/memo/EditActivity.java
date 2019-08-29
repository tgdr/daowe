package edu.buu.daowe.activity.memo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.buu.daowe.Bean.NoteBean;
import edu.buu.daowe.DB.NoteDao;
import edu.buu.daowe.R;

public class EditActivity extends AppCompatActivity {
    private EditText et_new_title;
    private EditText et_new_content;
    private TextView tv_time;
    private Spinner spinner;
    private NoteDao noteDao;
    private NoteBean note;
    private int myID;
    private String myTitle;
    private String myContent;
    private String myCreate_time;
    private String myUpdate_time;
    private String mySelect_time;
    private String myType;
    private Calendar calendar;
    private String login_user;
    private int flag;//区分是新建还是修改
    int extrayear = -1;
    int extramonth = -1;
    int extraday = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent myit = getIntent();
        extrayear = myit.getIntExtra("memoyear", 1970);
        extramonth = myit.getIntExtra("memomonth", 1);
        extraday = myit.getIntExtra("memoday", 1);
        init();
        getNowTime();
        if (extrayear != -1 && extramonth != -1 && extraday != -1) {
            tv_time.setText(extrayear + "-" + extramonth + "-" + extraday + " 00:00");
        }
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();
            }
        });

    }

    private void selectTime() {

        calendar = Calendar.getInstance();
        DatePickerDialog dpdialog = new DatePickerDialog(EditActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int month, int day) {
                        // TODO Auto-generated method stub
                        // 更新EditText控件日期 小于10加0
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        final TimePickerDialog tpdialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                tv_time.setText(format.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
        dpdialog.show();
        dpdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tpdialog.show();
            }
        });

    }

    private void init() {
        et_new_title = (EditText) findViewById(R.id.et_new_title);
        if (extrayear != 1970 && extramonth != 1 && extraday != 1) {
            et_new_title.setText(extrayear + "年" + extramonth + "月" + extraday + "日");
        }
        et_new_content = (EditText) findViewById(R.id.et_new_content);
        tv_time = (TextView) findViewById(R.id.tv_remindtime);
        spinner = (Spinner) findViewById(R.id.type_select);

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);//0新建，1编辑
        login_user = intent.getStringExtra("login_user");

        if (flag == 0) {//0新建
            setTitle("新建笔记");
            myCreate_time = getNowTime();
            myUpdate_time = getNowTime();

        } else if (flag == 1) {//1编辑
            Bundle bundle = intent.getBundleExtra("data");
            note = (NoteBean) bundle.getSerializable("note");
            myID = note.getId();
            myTitle = note.getTitle();
            myContent = note.getContent();
            myCreate_time = note.getCreateTime();
            myUpdate_time = note.getUpdateTime();
            mySelect_time = note.getRemindTime();
            login_user = note.getOwner();
            myType = note.getType();
            setTitle("编辑笔记");
            for (int i = 0; i < 5; i++) {
                if (spinner.getItemAtPosition(i).toString().equals(myType)) {
                    spinner.setSelection(i);
                }
            }
            et_new_title.setText(note.getTitle());
            et_new_content.setText(note.getContent());
            tv_time.setText(mySelect_time);

        }
    }

    private String getNowTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_save://保存笔记
                saveNoteDate();
                break;
            case R.id.action_new_giveup://放弃保存
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNoteDate() {
        String noteremindTime = tv_time.getText().toString();
        if (noteremindTime.equals("点击设置完成时间")) {
            Toast.makeText(EditActivity.this, "请设置备忘事件的完成时间", Toast.LENGTH_SHORT).show();
            return;
        }
        String noteTitle = et_new_title.getText().toString();
        if (noteTitle.length() > 14) {
            Toast.makeText(EditActivity.this, "标题长度应在15字以下", Toast.LENGTH_SHORT).show();
            return;
        } else if (noteTitle.isEmpty()) {
            Toast.makeText(EditActivity.this, "标题内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String noteContent = et_new_content.getText().toString();
        if (noteContent.isEmpty()) {
            Toast.makeText(EditActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String notecreateTime = myCreate_time;
        String noteupdateTime = getNowTime();
        int noteID = myID;
        noteDao = new NoteDao(this);
        NoteBean note = new NoteBean();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setCreateTime(notecreateTime);
        note.setUpdateTime(noteupdateTime);
        note.setMark(0);
        note.setRemindTime(noteremindTime);
        note.setType(spinner.getSelectedItem().toString());
        note.setOwner(login_user);
        if (flag == 0) {//新建笔记
            noteDao.insertNote(note);
        } else if (flag == 1) {//修改笔记
            note.setId(noteID);
            noteDao.updateNote(note);
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
