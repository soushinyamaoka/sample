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
    private final static String DB_NAME = "sampletodo1.db";//DB名
    private final static String DB_TABLE = "test1";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "id";
    private static final String COL_TODO = "todo";
    private static final String COL_BOX = "box";
    private static final String COL_DATE = "date";
    private static final String COL_MEMO = "memo";
    private static final String[] cols = {COL_ID, COL_TODO, COL_BOX, COL_DATE, COL_MEMO};

    private SQLiteDatabase db = null;           // SQLiteDatabase
    private DBHelper dbHelper;           // DBHepler
    protected Context context;
    public ListView listView;
    ArrayList<Integer> listId;
    ArrayList<String> listTodo;
    ArrayList<String> listBox;
    ArrayList<String> listDate;
    ArrayList<String> listMemo;

    int datebaseId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        listView = findViewById(R.id.list_view);
    }

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    public void openDB() {
        db = dbHelper.getWritableDatabase();        // DBの読み書き
    }

    public void writeDB(String todo, String box, String date, String memo) throws Exception{
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_TODO, todo);
        values.put(COL_BOX, box);
        values.put(COL_DATE, date);
        values.put(COL_MEMO, memo);

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
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();

        try {
            Cursor c = db.query(DB_TABLE, cols, null, null, null, null, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listId.add(c.getInt(0));
                listTodo.add(c.getString(1));
                c.moveToNext();
            }
            c.close();

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listTodo;
    }

    public String[] getTodo(long todoId) throws Exception {
        listTodo = new ArrayList<>();
        int todoid;
        String[] array = new String[0];
        String todo = null;
        String box = null;
        String date = null;
        String memo = null;
        listId = new ArrayList<>();
        listBox = new ArrayList<>();
        listTodo = new ArrayList<>();
        listDate = new ArrayList<>();
        listMemo = new ArrayList<>();
        try {
            Cursor c = db.query(DB_TABLE, new String[]{COL_TODO}, "id = " + todoId, null, null, null, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                //todoInfo[i] = c.getString(i);
                //listId.add(c.getInt(0));
                listTodo.add(c.getString(0));
                //listBox.add(c.getString(2));
                //listDate.add(c.getString(3));
                //listMemo.add(c.getString(4));
                //todoid = c.getInt(0);
                //todo = c.getString(1);
                //box = c.getString(2);
                //date = c.getString(3);
                //memo = c.getString(4);

                c.moveToNext();
            }
            c.close();
            array=(String[])listTodo.toArray(new String[0]);

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return array;
    }

    public int getColsCount(){
        return cols.length;
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

    public int getDataBaseId(long listviewId){
        ArrayList<Integer> idAdapter = new ArrayList<>();
        try {
            openDB();
            datebaseId = idAdapter.get((int)listviewId);//削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datebaseId;
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

