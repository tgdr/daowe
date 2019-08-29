package edu.buu.daowe.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 1U02UN on 2017/5/17.
 */

public class UserDao {
    Context context;
    userDBHelper dbHelper;

    public UserDao(Context context) {
        this.context = context;
        dbHelper = new userDBHelper(context, "user.db", null, 1);
    }

    public void insertUser(String username) {
//        获取数据库写的权限 一般都是更新
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "insert into user(username,password)values(?,?)";
        sqLiteDatabase.execSQL(sql, new String[]{username, "666"});
//        ContentValues values=new ContentValues();
//        values.put("username",username);
//        values.put("password",password);
//        sqLiteDatabase.insert("user",null,values);
//        sqLiteDatabase.close();
    }

    //    查询数据库方法,  使用数据库读数据库权限的时候，不能调用sqLiteDatabase.close();
    public Cursor query(String username) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String sql = "select*from user where username=?";
        return sqLiteDatabase.rawQuery(sql, new String[]{username});
    }

    public int updatePw(String username, String password, String newpassword) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newpassword);
        int i;
        i = sqLiteDatabase.update("user", cv, "username=? and password=?", new String[]{username, password});
        sqLiteDatabase.close();
        return i;
    }

}
