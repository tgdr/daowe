package edu.buu.daowe;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author: lty
 * @time: 2019/7/6   ------   18:53
 */


public class DaoWeApplication extends Application {
    public SharedPreferences getSpf() {
        return spf;
    }

    public void setSpf(SharedPreferences spf) {
        this.spf = spf;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
        editor.commit();
    }

    SharedPreferences spf;
    SharedPreferences.Editor editor;
    public static long getLasttime() {
        return lasttime;
    }

    public static void setLasttime(long lasttime) {
        DaoWeApplication.lasttime = lasttime;
    }

    public static long lasttime;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
   private OkHttpClient okHttpClient;
    private static HandlerListener mListener;
    //对于okhttpclient使用单例模式
    public static void setOnHandlerListener(HandlerListener listener) {
        mListener = listener;
    }
    public  static HandlerListener getListener(){
        return mListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        spf  = PreferenceManager.getDefaultSharedPreferences(this);
        editor = spf.edit();
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public  interface HandlerListener {
        void heandleMessage(Message msg);
    }

    //设置可访问所有的https网站
    //HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
    //OkHttpClient okHttpClient = new OkHttpClient.Builder()
    //        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
    //         //其他配置
    //         .build();
    //OkHttpUtils.initClient(okHttpClient);
    //设置具体的证书
    //HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(证书的inputstream, null, null);
    //OkHttpClient okHttpClient = new OkHttpClient.Builder()
    //        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager))
    //         //其他配置
    //         .build();
    //OkHttpUtils.initClient(okHttpClient);
    //双向认证
    //HttpsUtils.getSslSocketFactory(
    //	证书的inputstream,
    //	本地证书的inputstream,
    //	本地证书的密码)
}
