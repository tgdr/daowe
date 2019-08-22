package edu.buu.daowe.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.callback.BosProgressCallback;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.baidubce.services.bos.model.PutObjectRequest;
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

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.BosUtils;
import edu.buu.daowe.Util.BottomNavigationViewHelper;
import edu.buu.daowe.dialogue.ModifyPhotoBottomDialog;
import edu.buu.daowe.fragment.CancellationFragment;
import edu.buu.daowe.fragment.CardShowClassFragment;
import edu.buu.daowe.fragment.CheckInFragment;
import edu.buu.daowe.fragment.Four_Fragment;
import edu.buu.daowe.fragment.Three_Fragment;
import edu.buu.daowe.fragment.Two_Fragment;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmanager;
    FragmentTransaction transaction;
    TextView tvusername, tvsign;
    ImageView img, imguserphoto;
    DaoWeApplication app;
    JSONObject userinfo;
    public static String TAG = "MainActivity";
    InputStream inputStream, myinputstream = null;
    private BottomNavigationView mBottomNavigationView;

    private int lastIndex;
    List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (DaoWeApplication) getApplication();
        fragmanager = getSupportFragmentManager();
        transaction = fragmanager.beginTransaction();
        //   transaction.replace(R.id.main_frame,new CheckInFragment()).commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        NavigationView navigationView = findViewById(R.id.nav_view);
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
    }

    private void initData() {
        //setSupportActionBar(mToolbar);
        mFragments = new ArrayList<>();
        mFragments.add(new CardShowClassFragment());
        mFragments.add(new Two_Fragment());
        mFragments.add(new Three_Fragment());
        mFragments.add(new Four_Fragment());

        mFragments.add(new CheckInFragment());
        mFragments.add(new CancellationFragment());
        // mFragments.add(new AccountFragment());
        // 初始化展示MessageFragment
        setFragmentPosition(0);
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
                        setFragmentPosition(0);
                        break;
                    case R.id.menu_contacts:
                        setFragmentPosition(1);
                        break;
                    case R.id.menu_discover:
                        setFragmentPosition(2);
                        break;
                    case R.id.menu_me:
                        setFragmentPosition(3);
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_checkin) {
            // Handle the camera action
//            transaction = fragmanager.beginTransaction();
//        transaction.replace(R.id.main_frame,new CheckInFragment()).commit();

            setFragmentPosition(4);

        } else if (id == R.id.nav_cancellation) {
//            transaction = fragmanager.beginTransaction();
//            transaction.replace(R.id.main_frame,new CancellationFragment()).commit();
            setFragmentPosition(5);

        } else if (id == R.id.nav_slideshow) {
            Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(picture, 6);

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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


    }
}
