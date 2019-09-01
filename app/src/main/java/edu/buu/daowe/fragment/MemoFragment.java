package edu.buu.daowe.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zhy.http.okhttp.utils.L;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.buu.daowe.Bean.NoteBean;
import edu.buu.daowe.DB.NoteDao;
import edu.buu.daowe.DB.UserDao;
import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.SpacesItemDecoration;
import edu.buu.daowe.activity.memo.EditActivity;
import edu.buu.daowe.activity.memo.NoteActivity;
import edu.buu.daowe.adapter.NoteListAdapter;

public class MemoFragment extends Fragment
        implements PopupMenu.OnMenuItemClickListener {

    private UserDao userDao;
    private NoteDao noteDao;
    private RecyclerView rv_list_main;
    private NoteListAdapter mNoteListAdapter;
    private List<NoteBean> noteList;
    private String login_user;
    // private TextView utv;

    private NavigationView navigationView;
    private Menu menuNav;
    private CircleImageView iv_user;
    private CircleImageView userPic;
    private Bitmap head;// 头像Bitmap
    private Bitmap loadhead;
    int markdata = -1;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    DaoWeApplication app;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_memo_main, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();
        if (getArguments() != null) {
            Log.e(getArguments().toString(), getArguments().toString());
            markdata = getArguments().getInt("mark");
            Log.e("mymark", markdata + "");
        }

        login_user = app.getStuid();

        noteDao = new NoteDao(getActivity());
        userDao = new UserDao(getActivity());
        //  utv= (TextView) findViewById(R.id.tv_loginuser);

        initData();
        initView();
        registerForContextMenu(rv_list_main);
    }


    //获取当前用户头像
    private Drawable getUserDrawable() {
        //  loadhead = BitmapFactory.decodeFile(path + login_user + "head.jpg");// 从SD卡中找头像，转换成Bitmap
        if (loadhead != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(loadhead);// 转换成drawable
            return drawable;
        } else {
            return getActivity().getDrawable(R.mipmap.ic_logo);
        }
    }


    /******************************************/
    //刷新数据库数据，其实对notelist单一更新即可，不必重新获取，但是偷懒了
    private void refreshNoteList(int mark) {//mark--0=查询未完成，1=查询已完成，>1=查询所有

        noteList = noteDao.queryNotesAll(login_user, mark);
        mNoteListAdapter.setmNotes(noteList);
        mNoteListAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = mNoteListAdapter.getPosition();
        } catch (Exception e) {

            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case Menu.FIRST + 1://查看该笔记
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", noteList.get(position));
                intent.putExtra("data", bundle);
                startActivity(intent);
                break;

            case Menu.FIRST + 2://编辑该笔记
                Intent intent2 = new Intent(getActivity(), EditActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("note", noteList.get(position));
                intent2.putExtra("data", bundle2);
                intent2.putExtra("flag", 1);//编辑笔记
                startActivity(intent2);
                break;

            case Menu.FIRST + 3://删除该笔记
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示");
                builder.setMessage("确定删除笔记？");
                builder.setCancelable(false);
                final int finalPosition = position;
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ret = noteDao.DeleteNote(noteList.get(finalPosition).getId());
                        if (ret > 0) {
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                            refreshNoteList(2);
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;

            case Menu.FIRST + 4://标记为已完成
                NoteBean bean = noteList.get(position);
                if (bean.getMark() == 1) {
                    Toast.makeText(getActivity(), "它早就被完成了啊", Toast.LENGTH_SHORT).show();
                } else {
                    bean.setMark(1);
                    noteDao.updateNote(bean);
                    //noteList.get(position).setMark(1);
                    refreshNoteList(2);
                    mNoteListAdapter.notifyItemRangeChanged(position, position);
                }
                break;


            case Menu.FIRST + 5://标记为未完成
                NoteBean bean2 = noteList.get(position);
                if (bean2.getMark() == 0) {
                    Toast.makeText(getActivity(), "它本来就没完成啊", Toast.LENGTH_SHORT).show();
                } else {
                    bean2.setMark(0);
                    noteDao.updateNote(bean2);
                    //noteList.get(position).setMark(0);
                    refreshNoteList(0);
                    mNoteListAdapter.notifyItemRangeChanged(position, position);
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    //初始化数据库数据
    private void initData() {
        Cursor cursor = noteDao.getAllData(login_user);
        noteList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                NoteBean bean = new NoteBean();
                bean.setId(cursor.getInt(cursor.getColumnIndex("note_id")));
                bean.setTitle(cursor.getString(cursor.getColumnIndex("note_tittle")));
                bean.setContent(cursor.getString(cursor.getColumnIndex("note_content")));
                bean.setType(cursor.getString(cursor.getColumnIndex("note_type")));
                bean.setMark(cursor.getInt(cursor.getColumnIndex("note_mark")));
                bean.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                bean.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
                bean.setOwner(cursor.getString(cursor.getColumnIndex("note_owner")));
                noteList.add(bean);
            }
        }
        cursor.close();

    }

    //初始化控件
    private void initView() {

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("flag", 0);
                intent.putExtra("login_user", login_user);
                startActivity(intent);
            }
        });

//        //抽屉式菜单
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
//        utv = (TextView)headerLayout.findViewById(R.id.tv_loginuser);
//        iv_user= (CircleImageView) headerLayout.findViewById(R.id.iv_user);
//        utv.setText(login_user);
//        iv_user.setImageDrawable(getUserDrawable());


        //设置RecyclerView的属性
        rv_list_main = (RecyclerView) getView().findViewById(R.id.rv_list_main);
        rv_list_main.addItemDecoration(new SpacesItemDecoration(0));//设置item间距
        rv_list_main.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//竖向列表
        rv_list_main.setLayoutManager(layoutManager);

        mNoteListAdapter = new NoteListAdapter();
        mNoteListAdapter.setmNotes(noteList);
        refreshNoteList(markdata);
        rv_list_main.setAdapter(mNoteListAdapter);



        //RecyclerViewItem单击事件
        mNoteListAdapter.setOnItemClickListener(new NoteListAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, NoteBean note) {
                //Toast.makeText(MainActivity.this,""+note.getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
    }

    //头像更换弹出式菜单，选择拍照和使用图库
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
          /*  case R.id.gallery:
//                Toast.makeText(this, "图库", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                break;
            case R.id.camera:
//                Toast.makeText(this, "相机", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                break;*/

            default:
                break;
        }
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //     Log.e("smsmsmsmsmsmsmsmsmsmsmsm", "666" + hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (isVisibleToUser) {

            //相当于Fragment的onResume，为true时，Fragment已经可见 
            //     Log.e("tagtag", "tttttttttttt");

            refreshNoteList(0);
            getFragmentManager().beginTransaction().remove(this).add(new MemoFragment(), "s").commit();
        } else {
            //相当于Fragment的onPause，为false时，Fragment不可见

            //   Log.e("tagtag", "ooooooooooo");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Log.e("tagtag","ggggggg");

        // refreshNoteList(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        //  Log.e("tagtag","startstart");
        if (mNoteListAdapter != null) {
            //   Log.e("update","updatedataupdatedata");
            noteList = new NoteDao(getActivity()).queryNotesAll(login_user, markdata);
            mNoteListAdapter.setmNotes(noteList);
            mNoteListAdapter.notifyDataSetChanged();
        }


    }
}
