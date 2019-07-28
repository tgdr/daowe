package edu.buu.daowe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.buu.daowe.R;

public class CameraCollectionActivity extends Activity {

    public static String mfilepath;
    Button btn;
    ImageView img;
    private static  int reqcode= 0x001;
    Bundle getbunle;
    String nowtime  = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Intent receiveit =getIntent();
        getbunle =  receiveit.getBundleExtra("info");   //接受由CheckInFragment传递过来的bundle对象
        dirisExist(Environment.getExternalStorageDirectory()+"/daowe_collection");   //保存照片文件的路径为 sd卡/daowe_collection文件夹 如果没有就创建
        mfilepath = Environment.getExternalStorageDirectory()+"/daowe_collection/"+nowtime +".png"; //以当前打开activity的时间为基点 命名文件的名字
        btn = findViewById(R.id.btn_collection);
        img = findViewById(R.id.mycameraview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用系统Image_Capture行为
                Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //android 7.0以后不能直接用file:///的形式访问文件目录 要先申请读取存取权限 之后利用FileProvide进行 sd卡的文件操作
                Uri photouri = FileProvider.getUriForFile(CameraCollectionActivity.this,"edu.buu.daowe.fileauthorities",new File(mfilepath));
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                it.putExtra(MediaStore.EXTRA_OUTPUT,photouri); //用来获取系统拍照返回的文件
                startActivityForResult(it,reqcode);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FileInputStream fis=null;
        if(resultCode == RESULT_OK){
            if(requestCode == reqcode){
                try {
                    //系统拍摄的照片文件保存到sd卡设置的目录中  利用bitmap读取并显示到imgview上
                    fis = new FileInputStream(mfilepath);
                    Bitmap mimg = BitmapFactory.decodeStream(fis);
                    img.setImageBitmap(mimg);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraCollectionActivity.this )
                                         .setMessage("签到时间为"+nowtime+"\n"+"教室id为："+getbunle.getString("classid")).setTitle("签到成功");
                                 builder.show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static void dirisExist(String path) {
        File file = new File(path);
//判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }

}
}
