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

    private final static String DB_NAME = "samplebox4.db";//DB名
    private final static String DB_TABLE = "testbox4";//テーブル名
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

    int boxDataBaseId = 0;

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

    public void updateBoxDB(int boxDataBaseId, String boxName) {
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_TODO, boxName);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + boxDataBaseId, null);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    //データベースからの読み込み
    //public ArrayList<String> readBoxDB() {
    public String[] readBoxDB() {
        //listId = new ArrayList<Integer>();
        listBox = new ArrayList<>();
        int[] listId;
        //String[] stringBox = new String[];

        try {
            Cursor c = db.query(
                    DB_TABLE,
                    null,
                    "box !=?",
                    new String[]{"完了"},
                    null,
                    null,
                    ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBox.add(c.getString(1));
                //listId.add(c.getInt(0));
                //listBox.add(c.getString(1));
                c.moveToNext();
            }
            c.close();
            //stringBox = listBox.toArray(new String[0]);

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        String[] array = new String[listBox.size()];
        array = listBox.toArray(new String[listBox.size()]);
        return array;
    }

    //データベースからの読み込み
    public ArrayList<String> readBoxDB2() {
        listId = new ArrayList<Integer>();
        listBox = new ArrayList<>();

        try {
            Cursor c = db.query(DB_TABLE,
                    null,
                    "box !=?",
                    new String[]{"完了"},
                    null,
                    null,
                    ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listId.add(c.getInt(0));
                listBox.add(c.getString(1));
                c.moveToNext();
            }
            c.close();

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listBox;
    }

    public void deleteBoxDB(int boxDataBaseId){
        db.delete(DB_TABLE, "id = " + boxDataBaseId, null);
        db.close();
    }

    public int changeBoxId(long boxListViewId){
        ArrayList<Integer> idAdapter = new ArrayList<>();
        try {
            openBoxDB();
            idAdapter = getBoxDataBaseId();
            boxDataBaseId = idAdapter.get((int)boxListViewId);//削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boxDataBaseId;
    }

    public ArrayList<Integer> getBoxDataBaseId() {
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

    public String[] getBoxName(int boxDataBaseId) {
        listBox = new ArrayList<>();
        String[] setBoxName = new String[0];
        String[] cols = {"box"};
        try {
            Cursor c = db.query(DB_TABLE,
                                new String[]{COL_BOX},
                                "id = " + boxDataBaseId,
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
            setBoxName = listBox.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return setBoxName;
    }
}
