package edu.buu.daowe.activity.memo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.buu.daowe.Bean.NoteBean;
import edu.buu.daowe.DB.NoteDao;
import edu.buu.daowe.R;


public class NoteActivity extends AppCompatActivity {
    private TextView tv_note_title;//笔记标题
    private TextView tv_note_content;//笔记内容
    private TextView tv_note_create_time;//笔记创建时间
    private TextView tv_note_remind_time;//笔记备忘时间
    private NoteBean note;//笔记对象
    private String myTitle;
    private String myContent;
    private String myCreate_time;
    private String myRemind_time;
    private NoteDao noteDao;
    private SwitchCompat switchbt;

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        init();
    }

    private void init() {


        noteDao = new NoteDao(this);


        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("数据加载中...");
        loadingDialog.setCanceledOnTouchOutside(false);

        tv_note_title = (TextView) findViewById(R.id.tv_note_title);//标题
        tv_note_title.setTextIsSelectable(true);
        tv_note_content = (TextView) findViewById(R.id.tv_note_content);//内容
        tv_note_create_time = (TextView) findViewById(R.id.tv_note_create_time);
        tv_note_remind_time = (TextView) findViewById(R.id.tv_note_remind_time);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        note = (NoteBean) bundle.getSerializable("note");

        myTitle = note.getTitle();
        myContent = note.getContent();
        myCreate_time = note.getCreateTime();
        myRemind_time = note.getRemindTime();

        tv_note_title.setText(myTitle);
        tv_note_content.setText(myContent);
        tv_note_create_time.setText(myCreate_time);
        tv_note_remind_time.setText(myRemind_time);
        setTitle("笔记详情");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        MenuItem switchButton = menu.findItem(R.id.action_note_mark);
        switchbt = (SwitchCompat) MenuItemCompat.getActionView(switchButton);
        switchbt.setChecked(note.getMark() == 0 ? false : true);
        switchbt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                note.setMark(b == true ? 1 : 0);
                noteDao.updateNote(note);
                Toast.makeText(getApplicationContext(), b == true ? "成功标记为已完成" : "成功标记为未完成", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_note_edit://编辑笔记
                Intent intent = new Intent(NoteActivity.this, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("data", bundle);
                intent.putExtra("flag", 1);//编辑笔记
                startActivity(intent);
                finish();
                break;
            case R.id.action_note_delete://删除笔记
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除笔记？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ret = noteDao.DeleteNote(note.getId());
                        if (ret > 0) {
                            Toast.makeText(NoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;
//            case R.id.action_note_mark:

        }
        return super.onOptionsItemSelected(item);
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
