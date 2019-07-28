package edu.buu.daowe.activity;

import android.app.Activity;
import android.app.DownloadManager;
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
    String nowtime  = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        dirisExist(Environment.getExternalStorageDirectory()+"/daowe_collection");


        mfilepath = Environment.getExternalStorageDirectory()+"/daowe_collection/"+nowtime +".png";
        btn = findViewById(R.id.btn_collection);
        img = findViewById(R.id.mycameraview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photouri = FileProvider.getUriForFile(CameraCollectionActivity.this,"edu.buu.daowe.fileauthorities",new File(mfilepath));
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予临时权限别忘了
              //  it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // it.setDataAndType(photouri, "image/*");

                it.putExtra(MediaStore.EXTRA_OUTPUT,photouri);
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
                    fis = new FileInputStream(mfilepath);
                    Bitmap mimg = BitmapFactory.decodeStream(fis);
                    img.setImageBitmap(mimg);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraCollectionActivity.this )
                                         .setMessage("签到时间为"+nowtime+"\n").setTitle("签到成功");
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
