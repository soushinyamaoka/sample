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
    private final static String DB_NAME = "sampletodo4.db";//DB名
    private final static String DB_TABLE = "test4";//テーブル名
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
    private DBHelper dbHelper;// DBHepler
    protected Context context;
    public ListView listViewTodo;
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
    }

    public void updateDB(int todoId, String todo, String box, String date, String memo) {
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
    public ArrayList<String> readDB() throws Exception {
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();

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

    public ArrayList<String> readDividedBoxDB(String boxName) throws Exception {
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        String[] cols = {"todo"};

        try {
            Cursor c = db.query(DB_TABLE,
                    cols,
                    "box =?",
                    new String[]{boxName},
                    null,
                    null,
                    ORDER_BY);
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

    public ArrayList<String> readDBBox(){
        ArrayList<String> boxList = new ArrayList<>();
        String[] cols = {"box"};

        try {
            Cursor c = db.query(true,
                                DB_TABLE,
                                cols,
                      "box !=?",
                                new String[]{"", "完了"},
                                null,
                                null,
                                ORDER_BY,
                                null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                //listId.add(c.getInt(0));
                boxList.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return boxList;
    }

    public void deleteDB(int id){
        db.delete(DB_TABLE, "id = " + id, null);
        db.close();
    }

    public String[] getTodo(long todoId) {
        listTodo = new ArrayList<>();
        String[] setTodo = new String[0];

        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_TODO},
                                "id = " + todoId,
                                null,
                                null,
                                null,
                                ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listTodo.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
            setTodo = listTodo.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return setTodo;
    }

    public String[] getBox(long todoId) {
        listBox = new ArrayList<>();
        String[] setBox = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_BOX},
                                "id = " + todoId,
                                null,
                                null,
                                null,
                                ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBox.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
            setBox = listBox.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return setBox;
    }

    public String[] getDate(long todoId) {
        listDate = new ArrayList<>();
        String[] setDate = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_DATE},
                                "id = " + todoId,
                                null,
                                null,
                                null,
                                ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listDate.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
            setDate = listDate.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return setDate;
    }

    public String[] getMemo(long todoId) {
        listMemo = new ArrayList<>();
        String[] setMemo = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_MEMO},
                                "id = " + todoId,
                                null,
                                null,
                                null,
                                ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listMemo.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
            setMemo = listMemo.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return setMemo;
    }

    //データベースからの読み込み
    public ArrayList<Integer> getDataBaseId() {
        ArrayList<Integer> listID = new ArrayList<>();
        String[] cols = {"id"};
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
                listID.add(c.getInt(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listID;
    }

    public int changeId(long listviewId){
        ArrayList<Integer> idAdapter = new ArrayList<>();
        try {
            openDB();
            idAdapter = getDataBaseId();
            datebaseId = idAdapter.get((int)listviewId);//削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datebaseId;
    }
}
