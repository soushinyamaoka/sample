package com.example.soushinyamaoka.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BoxDBAdapter extends AppCompatActivity {

    private final static String DB_NAME = "samplebox3.db";//DB名
    private final static String DB_TABLE = "testbox3";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "id";
    private static final String COL_TODO = "todo";
    private static final String COL_BOX = "box";
    private static final String COL_DATE = "date";
    private static final String COL_MEMO = "memo";
    private static final String[] cols = {COL_ID, COL_TODO, COL_BOX, COL_DATE, COL_MEMO};
    private static final String ORDER_BY = "id desc";
    private static final String delete = "削除";

    private SQLiteDatabase db = null;           // SQLiteDatabase
    private BoxDBHelper boxDBHelper;// DBHepler
    protected Context context;
    ArrayList<Integer> listId;
    ArrayList<String> listTodo;
    ArrayList<String> listBox;
    ArrayList<String> listDate;
    ArrayList<String> listMemo;

    int datebaseId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.list_view_todo);
        //listViewTodo = findViewById(R.id.list_view_todo);
    }

    public BoxDBAdapter(Context context) {
        this.context = context;
        boxDBHelper = new BoxDBHelper(this.context);
    }

    public void openBoxDB() {
        db = boxDBHelper.getWritableDatabase();        // DBの読み書き
    }

    public void writeBoxDB(String box) {
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_BOX, box);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.insert(DB_TABLE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    public void updateBoxDB(int todoId, String todo, String box, String date, String memo) {
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_TODO, todo);
        values.put(COL_BOX, box);
        values.put(COL_DATE, date);
        values.put(COL_MEMO, memo);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + todoId, null);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    //データベースからの読み込み
    public ArrayList<String> readBoxDB() throws Exception {
        listId = new ArrayList<Integer>();
        listBox = new ArrayList<>();
        String[] cols = {"box"};

        try {
            Cursor c = db.query(DB_TABLE,
                    cols,
                    "box !=?",
                    new String[]{"完了"},
                    null,
                    null,
                    ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listTodo.add(c.getString(0));
                c.moveToNext();
            }
            c.close();

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listBox;
    }
}
