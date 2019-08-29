package edu.buu.daowe.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 1U02UN on 2017/5/17.
 */

public class noteDBHelper extends SQLiteOpenHelper {
    public noteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists note_data(" +
                "note_id integer primary key autoincrement," +
                "note_tittle varchar," +
                "note_content varchar," +
                "note_type varchar," +
                "note_mark varchar," +
                "createTime varchar," +
                "updateTime varchar," +
                "remindTime varchar," +
                "note_owner varchar)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
