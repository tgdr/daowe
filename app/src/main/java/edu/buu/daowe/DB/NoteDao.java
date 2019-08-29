package edu.buu.daowe.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.buu.daowe.Bean.NoteBean;

/**
 * Created by 1U02UN on 2017/5/17.
 */

public class NoteDao {
    Context context;
    noteDBHelper dbHelper;

    public NoteDao(Context context) {
        this.context = context;
        dbHelper = new noteDBHelper(context, "note.db", null, 1);
    }

    public void insertNote(NoteBean bean) {

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note_tittle", bean.getTitle());
        cv.put("note_content", bean.getContent());
        cv.put("note_type", bean.getType());
        cv.put("note_mark", bean.getMark());
        cv.put("createTime", bean.getCreateTime());
        cv.put("updateTime", bean.getUpdateTime());
        cv.put("remindTime", bean.getRemindTime());
        cv.put("note_owner", bean.getOwner());
        sqLiteDatabase.insert("note_data", null, cv);
    }

    public int DeleteNote(int id) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int ret = 0;
        ret = sqLiteDatabase.delete("note_data", "note_id=?", new String[]{id + ""});
        return ret;
    }

    public Cursor getAllData(String note_owner) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "select * from note_data where note_owner=?";
        return sqLiteDatabase.rawQuery(sql, new String[]{note_owner});
    }

    public void updateNote(NoteBean note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note_tittle", note.getTitle());
        cv.put("note_content", note.getContent());
        cv.put("note_type", note.getType());
        cv.put("note_mark", note.getMark());
        cv.put("updateTime", note.getUpdateTime());
        cv.put("remindTime", note.getRemindTime());
        db.update("note_data", cv, "note_id=?", new String[]{note.getId() + ""});
        db.close();
    }


    public List<NoteBean> queryNotesAll(String login_user, int mark) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<NoteBean> noteList = new ArrayList<>();
        NoteBean note;
        String sql;
        Cursor cursor = null;
        if (mark == 0) {//未完成备忘录
            sql = "select * from note_data where note_owner=? and note_mark=0 order by note_id desc";
        } else if (mark == 1) {//已完成备忘录
            sql = "select * from note_data where note_owner=? and note_mark=1 order by note_id desc";
        } else {//所有备忘录
            sql = "select * from note_data where note_owner=? order by note_id desc";
        }
        cursor = db.rawQuery(sql, new String[]{login_user});
        while (cursor.moveToNext()) {
            note = new NoteBean();
            note.setId(cursor.getInt(cursor.getColumnIndex("note_id")));
            note.setTitle(cursor.getString(cursor.getColumnIndex("note_tittle")));
            note.setContent(cursor.getString(cursor.getColumnIndex("note_content")));
            note.setType(cursor.getString(cursor.getColumnIndex("note_type")));
            note.setMark(cursor.getInt(cursor.getColumnIndex("note_mark")));
            note.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
            note.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
            note.setRemindTime(cursor.getString(cursor.getColumnIndex("remindTime")));
            noteList.add(note);
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return noteList;
    }

    public int countType(String login_user, int mark) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select count(*) from note_data where note_owner=? and note_mark=?";
        Cursor cursor = db.rawQuery(sql, new String[]{login_user, mark + ""});
        int i = 0;
        while (cursor.moveToNext()) {
            i = cursor.getInt(0);
        }
        return i;
    }


}
