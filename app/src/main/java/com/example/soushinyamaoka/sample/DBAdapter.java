package com.example.soushinyamaoka.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DBAdapter extends AppCompatActivity {
    private final static String DB_NAME = "test6.db";//DB名
    private final static String DB_TABLE = "test6";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "_id";

    private SQLiteDatabase db = null;           // SQLiteDatabase
    private DBHelper dbHelper;           // DBHepler
    protected Context context;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        listView = findViewById(R.id.list_view);
    }

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    public void openDB() {
        db = dbHelper.getWritableDatabase();        // DBの読み書き
    }

    public void writeDB(String info) throws Exception{
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        //values.put("id", "0");
        values.put("info", info);
        //空欄でも書き込めるようになっているので要修正
            try {
                db.insert(DB_TABLE, null, values);
            } catch (Exception e) {
                Log.e(TAG, "SQLExcepption:" + e.toString());
            }
            //int colNum = db.update(DB_TABLE, values, null, null);
            //if(colNum == 0)db.insert(DB_TABLE, "", values);
            //上記はINSERT INTO DB_TABLE (id, 0) VALUES("info", info);と同じ処理をしている
    }

    //データベースからの読み込み
    public ArrayList<String> readDB() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Integer> listID = new ArrayList<>();
        String[] cols = {"info","id"};
        try {
            Cursor c = db.query(DB_TABLE, cols, null, null, null, null, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                list.add(c.getString(0));
                listID.add(c.getInt(1));
                c.moveToNext();
            }
            c.close();

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return list;
    }

    public void deleteDB(int id){
        db.delete(DB_TABLE, "id = " + id, null);
        db.close();
    }

    //データベースからの読み込み
    public ArrayList<Integer> deletereadDB() throws Exception {
        ArrayList<Integer> listID = new ArrayList<>();
        String[] cols = {"id"};
        try {
            Cursor c = db.query(DB_TABLE, cols, null, null, null, null, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listID.add(c.getInt(0));
                c.moveToNext();
            }
            c.close();

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listID;
    }

}
//    private static class DBHelper extends SQLiteOpenHelper {

        //データベースヘルパーのコンストラクタ
//        public DBHelper(Context context) {
//            super(context, DB_NAME, null, DB_VERSION); //DB名、テーブル名、DBバージョンを定数として保持している
//        }

        //データベースの生成
//        @Override
//        public void onCreate(SQLiteDatabase db){
//            db.execSQL("create table if not exists " +
//                    DB_TABLE + "(id integer primary key autoincrement ,info text)");//_id text primary key
//        }

        //データベースのアップグレード
//        @Override
//        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
//            db.execSQL("drop table if exists "+DB_TABLE);
//            onCreate(db);
//        }
//    }

