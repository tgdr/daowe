package edu.buu.daowe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.callback.BosProgressCallback;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.baidubce.services.bos.model.PutObjectRequest;
import com.shizhefei.guide.GuideHelper;
import com.shizhefei.guide.GuideHelper.TipData;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.buu.daowe.Bean.NoteBean;
import edu.buu.daowe.DB.NoteDao;
import edu.buu.daowe.DB.UserDao;
import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.BosUtils;
import edu.buu.daowe.Util.BottomNavigationViewHelper;
import edu.buu.daowe.dialogue.ModifyPhotoBottomDialog;
import edu.buu.daowe.fragment.CardShowClassFragment;
import edu.buu.daowe.fragment.CheckInFragment;
import edu.buu.daowe.fragment.MemoFragment;
import edu.buu.daowe.fragment.SchooClalendarFragment;
import edu.buu.daowe.fragment.Three_Fragment;
import edu.buu.daowe.fragment.UserCenter_Fragment;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmanager;
    AppBarLayout mybar;
    private static final int TIME_INTERVAL = 2000;
    private NoteDao noteDao;
    private int nav_selected;
    long mBackPressed;
    private Menu menuNav;

    TextView tvusername, tvsign;
    ImageView img, imguserphoto;
    DaoWeApplication app;
    JSONObject userinfo;
    NavigationView navigationView;
    private List<NoteBean> noteList;
    public static String TAG = "MainActivity";
    InputStream inputStream, myinputstream = null;
    private BottomNavigationView mBottomNavigationView;
    private UserDao userdao;
    private int lastIndex;
    List<Fragment> mFragments;
    Toolbar toolbar;
    BottomNavigationItemView bottom_menu_kcb, bottom_menu_xl, bottom_menu_qz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (DaoWeApplication) getApplication();
        mybar = findViewById(R.id.mybar);
        fragmanager = getSupportFragmentManager();


        userdao = new UserDao(this);

        //   transaction.replace(R.id.main_frame,new CheckInFragment()).commit();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mybar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        noteDao = new NoteDao(this);
        bottom_menu_kcb = findViewById(R.id.menu_classtable);
        bottom_menu_xl = findViewById(R.id.menu_calendar);
        bottom_menu_qz = findViewById(R.id.menu_message);

        initBottomNavigation();

        initData();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        tvusername = navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        tvsign = navigationView.getHeaderView(0).findViewById(R.id.nav_sign);
        imguserphoto = navigationView.getHeaderView(0).findViewById(R.id.userimg);
        imguserphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ModifyPhotoBottomDialog editNameDialog = new ModifyPhotoBottomDialog();
                editNameDialog.show(fm, "fragment_bottom_dialog");
            }
        });
        Log.e(TAG, app.getUsername() + "   " + app.getToken());

        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).url(BaseRequest.BASEURL + "users/" + app.getUsername()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, response);
                try {
                    userinfo = new JSONObject(response);
                    if (userinfo.getInt("code") == 200) {
                        userinfo = userinfo.getJSONObject("data");
                        app.setStuid(userinfo.getString("id"));
                        tvsign.setText(userinfo.getString("introduction"));
                        tvusername.setText(userinfo.getString("name"));
                        setCount();
                        //https://i.loli.net/2019/08/18/VwGi1O8pnN5xljA.jpg   userinfo.getString("avatar")
                        OkHttpUtils.get().url(userinfo.getString("avatar")).build().execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(Bitmap response, int id) {
                                imguserphoto.setImageBitmap(response);
                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        img = navigationView.getHeaderView(0).findViewById(R.id.pic_header);
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                img.setImageResource(R.mipmap.nav_header2);

                return true;
            }

        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        shouguide();
    }

    private void initData() {

        //setSupportActionBar(mToolbar);
        mFragments = new ArrayList<>();
//添加全部备忘录的fragment
        synchronized (mFragments) {    //同步锁：同步监听对象/ 同步监听器 /互斥锁


            Bundle bundleall = new Bundle();
            bundleall.putInt("mark", 3);
            MemoFragment alldatafg = new MemoFragment();
            alldatafg.setArguments(bundleall);
            mFragments.add(alldatafg);

            //添加已完成的备忘录
            Bundle bundlefinished = new Bundle();
            bundlefinished.putInt("mark", 1);
            MemoFragment finishdatafg = new MemoFragment();
            finishdatafg.setArguments(bundlefinished);
            mFragments.add(finishdatafg);

            //添加未完成的备忘录
            Bundle bundleunfinished = new Bundle();
            bundleunfinished.putInt("mark", 0);
            MemoFragment unfinishdatafg = new MemoFragment();
            unfinishdatafg.setArguments(bundleunfinished);
            mFragments.add(unfinishdatafg);
        }


        mFragments.add(new Three_Fragment());
        mFragments.add(new SchooClalendarFragment());

        mFragments.add(new CardShowClassFragment());
        mFragments.add(new UserCenter_Fragment());


        mFragments.add(new CheckInFragment());


        mFragments.add(new CheckInFragment());



        // mFragments.add(new CancellationFragment());
        // mFragments.add(new AccountFragment());
        // 初始化展示MessageFragment
        mybar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        setFragmentPosition(5);
    }

    private void initBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bv_bottomNavigation);
        // 解决当item大于三个时，非平均布局问题
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_message:
                        mybar.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        setFragmentPosition(3);
                        setTitle("朋友圈");
                        break;
                    case R.id.menu_calendar:
                        mybar.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        setFragmentPosition(4);
                        setTitle("校历");
                        break;
                    case R.id.menu_classtable:
                        mybar.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        setFragmentPosition(5);
                        setTitle("今日课表");
                        break;
                    case R.id.menu_me:
                        mybar.setVisibility(View.GONE);
                        toolbar.setVisibility(View.GONE);
                        setFragmentPosition(6);
                        setTitle("个人中心");
                        break;
                    default:
                        break;
                }
                // 这里注意返回true,否则点击失效
                return true;
            }
        });
    }


    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Fragment lastFragment = mFragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.main_frame, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                System.exit(0);
                return;
            } else {
                Toast.makeText(getBaseContext(), "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return false;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_checkin) {
            // Handle the camera action
//            transaction = fragmanager.beginTransaction();
//        transaction.replace(R.id.main_frame,new CheckInFragment()).commit();
            mybar.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            Intent it = new Intent(MainActivity.this, CameraSignInActivity.class);
            startActivity(it);

        } else if (id == R.id.nav_cancellation) {
//            transaction = fragmanager.beginTransaction();
//            transaction.replace(R.id.main_frame,new CancellationFragment()).commit();
            mybar.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            Intent it = new Intent(MainActivity.this, CameraholidayActivity.class);
            startActivity(it);


        } else if (id == R.id.nav_logout) {
            mybar.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.delete().addHeader("Authorization", "Bearer " + app.getToken()).url(BaseRequest.BASEURL + "auth/logout").build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            app.getEditor().putString("AUTOLOGIN", "false").commit();
                            //  Log.e(TAG,response);
                            finish();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });

                }
            }).start();


        } else if (id == R.id.nav_all) {
            nav_selected = 3;
            refreshNoteList(app.getStuid(), nav_selected);

            setFragmentPosition(0);


            // setFragmentPosition(5);
            setTitle("备忘录——全部");

        } else if (id == R.id.nav_finish) {
            nav_selected = 1;
            refreshNoteList(app.getStuid(), nav_selected);
            setFragmentPosition(1);
            setTitle("备忘录——已完成");

        } else if (id == R.id.nav_unfinish) {
            nav_selected = 0;
            refreshNoteList(app.getStuid(), nav_selected);

            setFragmentPosition(2);
            setTitle("备忘录——未完成");

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 刷新ui
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {

            Bitmap bitmap = (Bitmap) message.obj;
            imguserphoto.setImageBitmap(bitmap);


        }
    };

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            final String key = app.getUsername();
            inputStream = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 创建ObjectMetadata类的实例
                    ObjectMetadata meta = new ObjectMetadata();

                    // 自定义元数据
                    meta.setContentType("image/jpeg");
                    BosClient client = new BosClient(BosUtils.initBosClientConfiguration());    //创建BOSClient实例
                    // 获取数据流

                    try {
                        inputStream = getContentResolver().openInputStream(data.getData());
                        myinputstream = getContentResolver().openInputStream(data.getData());
                        // 以数据流形式上传Object
                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setContentType("image/jpeg");
                        PutObjectRequest request = new PutObjectRequest("doways-avatar", key + ".jpg", inputStream, objectMetadata);
                        request.setObjectMetadata(objectMetadata);

                        request.setProgressCallback(new BosProgressCallback<PutObjectRequest>() {
                            @Override
                            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                                Log.e(currentSize + "", totalSize + "");
                                if (currentSize == totalSize) {
                                    Bitmap bitmap = BitmapFactory.decodeStream(myinputstream);
                                    //利用消息的方式把数据传送给handler
                                    Message msg = handler.obtainMessage();
                                    msg.obj = bitmap;
                                    handler.sendMessage(msg);
                                    try {
                                        myinputstream.close();
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        final JSONObject object = new JSONObject();
                                        object.put("avatar", BaseRequest.BASEBOS + app.getUsername() + ".jpg");
                                        Log.e(TAG, object.toString());
                                        OkHttpUtils.patch().addHeader("Authorization", "Bearer " + app.getToken())
                                                .requestBody(new RequestBody() {
                                                    @Override
                                                    public MediaType contentType() {
                                                        return MediaType.parse("application/json; charset=utf-8");
                                                    }

                                                    @Override
                                                    public void writeTo(BufferedSink sink) throws IOException {
                                                        sink.outputStream().write(object.toString().getBytes());
                                                    }

                                                })
                                                .url(BaseRequest.BASEURL + "users/" + app.getStuid() + "/avatar").build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e(TAG, e.toString());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        Log.e(TAG, response.toString());
                                                    }
                                                });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        String eTag = client.putObject(request).getETag();
//            PutObjectResponse putObjectResponseFromInputStream = client.putObject("doways-avatar", key+".jpg", inputStream,meta);
//            System.out.println(putObjectResponseFromInputStream.getETag());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {

        }



    }

    //设置抽屉菜单是否完成备忘录的数量
    private void setCount() {
        Cursor cursor0 = noteDao.getAllData(app.getStuid());
        noteList = new ArrayList<>();
        if (cursor0 != null) {
            while (cursor0.moveToNext()) {
                NoteBean bean = new NoteBean();
                bean.setId(cursor0.getInt(cursor0.getColumnIndex("note_id")));
                bean.setTitle(cursor0.getString(cursor0.getColumnIndex("note_tittle")));
                bean.setContent(cursor0.getString(cursor0.getColumnIndex("note_content")));
                bean.setType(cursor0.getString(cursor0.getColumnIndex("note_type")));
                bean.setMark(cursor0.getInt(cursor0.getColumnIndex("note_mark")));
                bean.setCreateTime(cursor0.getString(cursor0.getColumnIndex("createTime")));
                bean.setUpdateTime(cursor0.getString(cursor0.getColumnIndex("updateTime")));
                bean.setOwner(cursor0.getString(cursor0.getColumnIndex("note_owner")));
                noteList.add(bean);
            }
        }
        cursor0.close();
        Cursor cursor = userdao.query(app.getStuid() + "");
        if (cursor.moveToNext()) {
            //Toast.makeText(getApplicationContext(),"该用户已被注册，请重新输入",Toast.LENGTH_LONG).show();
            // userName.requestFocus();
        } else {
            userdao.insertUser(app.getStuid());
            cursor.close();

        }
        menuNav = navigationView.getMenu();
        int unfinishNum = noteDao.countType(app.getStuid(), 0);//未完成备忘录
        int finishNum = noteDao.countType(app.getStuid(), 1);//已完成备忘录
        int allNum = finishNum + unfinishNum;//所有备忘录
        MenuItem nav_all = menuNav.findItem(R.id.nav_all);
        MenuItem nav_finish = menuNav.findItem(R.id.nav_finish);
        MenuItem nav_unfinish = menuNav.findItem(R.id.nav_unfinish);

        String all_before = "所有备忘录";
        String finish_before = "已完成备忘录";
        String unfinish_before = "未完成备忘录";

        nav_all.setTitle(setSpanTittle(all_before, allNum));
        nav_finish.setTitle(setSpanTittle(finish_before, finishNum));
        nav_unfinish.setTitle(setSpanTittle(unfinish_before, unfinishNum));

    }

    //设置抽屉菜单是否完成备忘录数量的文字样式
    private SpannableString setSpanTittle(String tittle, int num) {
        String tittle2 = tittle + "      " + num + "  ";
        SpannableString sColored = new SpannableString(tittle2);
        sColored.setSpan(new BackgroundColorSpan(Color.GRAY), tittle2.length() - (num + "").length() - 4, tittle2.length(), 0);
        sColored.setSpan(new ForegroundColorSpan(Color.WHITE), tittle2.length() - (num + "").length() - 4, tittle2.length(), 0);
        return sColored;
    }

    //刷新数据库数据，其实对notelist单一更新即可，不必重新获取，但是偷懒了
    private void refreshNoteList(String login_user, int mark) {//mark--0=查询未完成，1=查询已完成，>1=查询所有
        noteList = noteDao.queryNotesAll(login_user, mark);
        //  mNoteListAdapter.setmNotes(noteList);
        // mNoteListAdapter.notifyDataSetChanged();
        setCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.getStuid() != null && !app.getStuid().equals("")) {
            refreshNoteList(app.getStuid(), 0);
            refreshNoteList(app.getStuid(), 1);
            refreshNoteList(app.getStuid(), 2);
            setCount();
        }

    }


    public void shouguide() {
        final GuideHelper guideHelper = new GuideHelper(MainActivity.this);

        TipData tipData1 = new TipData(R.mipmap.guide_kcb, Gravity.LEFT | Gravity.TOP, (View) bottom_menu_kcb);
        tipData1.setLocation(bottom_menu_kcb.getWidth() + 15, bottom_menu_kcb.getHeight());
        TipData tipData2 = new TipData(R.mipmap.guide_xl, Gravity.CENTER | Gravity.TOP, (View) bottom_menu_xl);
        tipData2.setLocation(bottom_menu_kcb.getWidth() + 60, bottom_menu_xl.getHeight());
        TipData tipData3 = new TipData(R.mipmap.guide_pyq, Gravity.CENTER | Gravity.TOP, (View) bottom_menu_qz);
        // tipData3.setLocation(bottom_menu_kcb.getWidth()+80, bottom_menu_qz.getHeight());

        guideHelper.addPage(tipData1);
        guideHelper.addPage(tipData2);
        guideHelper.addPage(tipData3);


        tipData1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideHelper.nextPage();
            }
        });
        //guideHelper.addPage(tipData1);

        guideHelper.show(false);

    }

}
