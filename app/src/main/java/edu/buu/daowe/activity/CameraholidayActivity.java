package edu.buu.daowe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;

public class CameraholidayActivity extends Activity {
    ImageView mIvScan;
    DaoWeApplication app;
    public String TAG = "CameraholidayActivity";
    /**
     * 0:从上往下 1:从下往上
     */
    Animation mTop2Bottom, mBottom2Top;


    private ImageView btn;
    //   private ImageView iv;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.ShutterCallback shutter;
    private Camera.PictureCallback jepg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation);
        app = (DaoWeApplication) getApplication();
        initview();
    }

    private void initview() {
        mIvScan = findViewById(R.id.scan_line);

        btn = findViewById(R.id.camp);
//        mTop2Bottom = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
//                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
//                TranslateAnimation.RELATIVE_TO_PARENT, 0.7f);
//
//        mBottom2Top = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
//                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.7f,
//                TranslateAnimation.RELATIVE_TO_PARENT, 0f);


        mTop2Bottom = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.8f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f);

        mBottom2Top = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0.8f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f);


        mBottom2Top.setRepeatMode(Animation.RESTART);
        mBottom2Top.setInterpolator(new LinearInterpolator());
        mBottom2Top.setDuration(1500);
        mBottom2Top.setFillEnabled(true);//使其可以填充效果从而不回到原地
        mBottom2Top.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        mBottom2Top.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvScan.startAnimation(mTop2Bottom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTop2Bottom.setRepeatMode(Animation.RESTART);
        mTop2Bottom.setInterpolator(new LinearInterpolator());
        mTop2Bottom.setDuration(1500);
        mTop2Bottom.setFillEnabled(true);
        mTop2Bottom.setFillAfter(true);
        mTop2Bottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvScan.startAnimation(mBottom2Top);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIvScan.startAnimation(mTop2Bottom);


        surfaceView = findViewById(R.id.cancelcameraSV);
        surfaceHolder = surfaceView.getHolder();
        SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }

            //获取前置摄像头
            public Camera getCamera() {
                camera = null;
                Camera.CameraInfo info = new Camera.CameraInfo();
                int cnt = Camera.getNumberOfCameras();
                for (int i = 0; i < cnt; i++) {
                    Camera.getCameraInfo(i, info);

                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        try {
                            camera = Camera.open(i);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return camera;
            }


            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                camera = getCamera();
                try {
                    camera.setPreviewDisplay(holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPictureSize(320, 240);
                parameters.getFocusMode();
                parameters.setPictureFormat(PixelFormat.JPEG);
                camera.setParameters(parameters);
                //

                camera.startPreview();
            }
        };
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置显示图片
        jepg = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                camera.startPreview();

                final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                //  iv.setImageBitmap(bm);
                Toast.makeText(CameraholidayActivity.this, "拍照成功！", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraholidayActivity.this, "66666", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CameraholidayActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        //  bundle.putParcelable("bitmap", bm);
                        bundle.putByteArray("bitmapdata", data);

                        intent.putExtras(bundle);
                        setResult(0x520, intent);
                        finish();

                    }
                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //    Log.e(TAG, Base64Util.encode(data));
//                        ObjectMetadata meta = new ObjectMetadata();
//
//                        // 自定义元数据
//                        meta.setContentType("image/jpeg");
//                        BosClient client = new BosClient(BosUtils.initBosClientConfiguration());    //创建BOSClient实例
//                        // 获取数据流
//                        // 以数据流形式上传Object
//                        ObjectMetadata objectMetadata = new ObjectMetadata();
//                        objectMetadata.setContentType("image/jpeg");
//                        PutObjectRequest request = new PutObjectRequest(BaseRequest.LEAVEREQ, app.getStuid() + "-" + System.currentTimeMillis() + ".jpg",
//                                new ByteArrayInputStream(data), objectMetadata);
//                        request.setObjectMetadata(objectMetadata);
//                        final String eTag = client.putObject(request).getETag();
//
////                        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).addHeader("Content-Type", "application/json")
////                                .url(BaseRequest.BASEURL + "tools/" + app.getStuid() + "/face").build().execute(new StringCallback() {
////                            @Override
////                            public void onError(Call call, Exception e, int id) {
////                                // Log.e(TAG,e.toString());
////                            }
////
////                            @Override
////                            public void onResponse(String response, int id) {
////
////                            }
////                        });
//                    }
//                }).start();


            }
        };

        shutter = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                // Toast.makeText(getApplicationContext(), "成功拍照", Toast.LENGTH_SHORT).show();

            }
        };
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(shutter, null, jepg);
            }
        });
    }
}
