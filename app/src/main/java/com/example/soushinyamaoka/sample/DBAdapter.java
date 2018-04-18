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

public class DBAdapter extends AppCompatActivity {
    private final static String DB_TABLE = "test8";//テーブル名
    private static final String COL_ID = "id";
    private static final String COL_TODO = "todo";
    private static final String COL_BOX = "box";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_MEMO = "memo";
    private static final String COL_BOXID = "boxid";
    private static final String[] cols = {COL_ID, COL_TODO, COL_BOX, COL_DATE, COL_MEMO};
    private static final String ORDER_BY = "id desc";

    private SQLiteDatabase db = null;           // SQLiteDatabase
    private DBHelper dbHelper;// DBHepler
    protected Context context;
    ArrayList<Integer> listId;
    ArrayList<String> listTodo;
    ArrayList<String> listBox;
    ArrayList<String> listTime;
    ArrayList<String> listDate;
    ArrayList<String> listMemo;
    ArrayList<Integer> listBoxId;

    int databaseId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    public void openDB() {
        db = dbHelper.getWritableDatabase();        // DBの読み書き
    }

    public void writeDB(String todo, String box, String date,
                        String time, String memo, int boxId) {
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_TODO, todo);
        values.put(COL_BOX, box);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_MEMO, memo);
        values.put(COL_BOXID, boxId);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.insert(DB_TABLE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    public void updateDB(int todoId, String todo, String box, String date,
                         String time, String memo, int boxId) {
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_TODO, todo);
        values.put(COL_BOX, box);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_MEMO, memo);
        values.put(COL_BOXID, boxId);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + todoId, null);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    public void backDB(int todoId, String box) {
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_BOX, box);
        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + todoId, null);
            db.close();
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
                    "boxId !=?",
                    new String[]{String.valueOf(0)},
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

    public ArrayList<String> readDividedBoxDB(int boxId) throws Exception {
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        String[] cols = {"todo"};

        try {
            Cursor c = db.query(DB_TABLE,
                    cols,
                    "boxId !=" + boxId,
                    null,
                    null,
                    null,
                    ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                //listId.add(c.getInt(0));
                listTodo.add(c.getString(0));
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
        int boxId = 0;

        try {
            Cursor c = db.query(true,
                                DB_TABLE,
                                cols,
                                "boxId !=" + boxId,
                                null,
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

    public void deleteDB(int boxId){
        db.delete(DB_TABLE, "boxId = " + boxId, null);
        db.close();
    }

    public String[] getTodo(int databaseId) {
        listTodo = new ArrayList<>();
        String[] setTodo = new String[0];

        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_TODO},
                                "id = " + databaseId,
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

    public String[] getBox(int databaseId) {
        listBox = new ArrayList<>();
        String[] setBox = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_BOX},
                                "id = " + databaseId,
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

    public String[] getDate(int databaseId) {
        listDate = new ArrayList<>();
        String[] setDate = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_DATE},
                                "id = " + databaseId,
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

    public String[] getTime(int databaseId) {
        listTime = new ArrayList<>();
        String[] setTime = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                    new String[]{COL_TIME},
                    "id = " + databaseId,
                    null,
                    null,
                    null,
                    ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listTime.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
            setTime = listTime.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return setTime;
    }

    public String[] getMemo(int databaseId) {
        listMemo = new ArrayList<>();
        String[] setMemo = new String[0];
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_MEMO},
                                "id = " + databaseId,
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
        int boxId = 0;
        try {
            Cursor c = db.query(DB_TABLE,
                                cols,
                                "boxId !=" + boxId,
                                null,
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

    public int changeDividedId(long listviewId, String boxName) {
        int databaseId = 0;
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        String[] cols = {"id"};
        int boxId = 0;
        Integer[] array = new Integer[(int) listviewId];
        if (boxName.equals("全て")){
            try {
                Cursor c = db.query(DB_TABLE,
                        cols,
                        "boxId !=" + boxId,
                        null,
                        null,
                        null,
                        ORDER_BY);
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    listId.add(c.getInt(0));
                    c.moveToNext();
                    array = listId.toArray(new Integer[0]);
                }
                c.close();
                databaseId = array[(int) listviewId];
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
        } else{
            try {
                Cursor c = db.query(DB_TABLE,
                        cols,
                        "boxId =" + boxId,
                        null,
                        null,
                        null,
                        ORDER_BY);
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    //listId.add(c.getInt(0));
                    listId.add(c.getInt(0));
                    c.moveToNext();
                    array = listId.toArray(new Integer[0]);
                }
                c.close();
                databaseId = array[(int) listviewId];
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
        }
        return databaseId;
    }

    public int changeId(long listviewId){
        ArrayList<Integer> idAdapter = new ArrayList<>();
        try {
            openDB();
            idAdapter = getDataBaseId();
            databaseId = idAdapter.get((int)listviewId);//削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseId;
    }
}
