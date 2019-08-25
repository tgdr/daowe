package edu.buu.daowe.defui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {
    protected SurfaceHolder sh;
    private String TestTag = "TestLog";
    private int mWidth = 0, mHeight = 0;
    private static int phoneWidth = 0;
    private static int phoneHeight = 0;

    public SVDraw(Context context, AttributeSet attrs) {
        super(context, attrs);

        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT); // 设置透明
        setZOrderOnTop(true); // 置顶

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();


        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        phoneWidth = dm.widthPixels;
        phoneHeight = dm.heightPixels;
    }

    /*
    ...其他的一些函数
    */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    public synchronized void drawRect() {
        Canvas canvas = sh.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT);

        // 红色提示矩形框画笔设置
        Paint redPaint = new Paint();
        redPaint.setAntiAlias(true);
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(4f);
        redPaint.setAlpha(100);

        // 绿色人物提示框画笔设置
        Paint greenPaint = new Paint();
        greenPaint.setAntiAlias(true);
        greenPaint.setColor(0xFFA4C739);
        greenPaint.setStrokeWidth(3.5f);
        greenPaint.setStyle(Paint.Style.STROKE);
        if (mWidth == 0 || mHeight == 0 || mWidth == -1 || mHeight == -1) {
            canvas.translate(getWidth() / 2, getHeight() / 2 - 200);
        } else {
            // 将画布原点移至画布中央（方便画布选点）
            canvas.translate(mWidth / 2, mHeight / 2 - 200);
        }


        // 绘制外围红色边框
        canvas.drawRect(new Rect(-360, -480, 360, 480), redPaint);

        // 头部
        RectF rectf_head = new RectF(-225, -375, 225, 15);
        canvas.drawArc(rectf_head, 200, 140, false, greenPaint);

        // 下颌
        RectF rectf_mouse = new RectF(-165, 120, 165, 322);
        canvas.drawArc(rectf_mouse, 20, 140, false, greenPaint);


        // 衬衣
        canvas.drawLine(-141, 315, -300, 458, greenPaint);
        canvas.drawLine(141, 315, 300, 458, greenPaint);

        sh.unlockCanvasAndPost(canvas);
    }

    public void clearDraw() {
        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.BLUE);
        sh.unlockCanvasAndPost(canvas);
    }
}
