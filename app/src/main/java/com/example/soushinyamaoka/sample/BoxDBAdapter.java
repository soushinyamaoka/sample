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

    private final static String DB_TABLE = "testbox11";//テーブル名
    private static final String COL_ID = "id";
    private static final String COL_TODO = "todo";
    private static final String COL_BOX = "box";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_MEMO = "memo";
    private static final String BOX_ID = "boxid";
    private static final String ORDER_BY = "id desc";

    private SQLiteDatabase db = null;           // SQLiteDatabase
    private DBHelper dbHelper;// DBHepler
    protected Context context;
    ArrayList<Integer> listBoxId;
    ArrayList<String> listBox;

    int boxDataBaseId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public BoxDBAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    public void openBoxDB() {
        db = dbHelper.getWritableDatabase();        // DBの読み書き
    }

    public void writeBoxDB(String box) {
        openBoxDB();
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_BOX, box);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.insert(DB_TABLE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    public void updateBoxDB(int boxId, String boxName) {
        openBoxDB();
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_BOX, boxName);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + boxId, null);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    public void deleteBoxDB(int boxId){
        openBoxDB();
        db.delete(DB_TABLE, "id = " + boxId, null);
        db.close();
    }

    //Mainで呼びだされるカテゴリ一覧
    public String[] readBoxDB() {
        openBoxDB();
        listBox = new ArrayList<>();
        String[] cols = {COL_BOX};
        //String[] where = {"完了","今日"};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id != 1 AND id != 2",//id.1の完了済みと、id.2の未分類は表示させない
                    null,
                    null,
                    null,
                    null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBox.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        String[] array;
        array = listBox.toArray(new String[listBox.size()]);
        return array;
    }

    //データベースからの読み込み
    public ArrayList<String> readBoxSpinnerDB() {
        openBoxDB();
        listBox = new ArrayList<>();
        String[] cols = {COL_BOX};
        try {
            Cursor c = db.query(DB_TABLE,
                                cols,
                    "id != 1",//id.1の完了済みは出さないようにしている
                    null,
                    null,
                    null,
                    null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBox.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listBox;
    }

    //list上のBoxのidをDB上のBoxのidに変換
    //スピナーから選択した時と、Mainから選択した時では値が1異なっている
    //スピナーでは未分類が表示されているが、Mainには表示されていないため
    public Integer getBoxId(int listViewId) {
        openBoxDB();
        listBoxId = new ArrayList<>();
        String[] cols = {COL_ID};
        Integer setBoxId;
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id != 1",//id.1の完了済みは表示させない
                    null,
                    null,
                    null,
                    null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBoxId.add(c.getInt(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        Integer[] array;
        array = listBoxId.toArray(new Integer[listBoxId.size()]);
        setBoxId = array[listViewId];
        return setBoxId;
    }

    public String changeToBoxName(int boxId){
        openBoxDB();
        listBox = new ArrayList<>();
        String[] cols = {COL_BOX};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,//cols
                    "id = " + boxId,//id.1の完了済みと、id.2の今日は除外
                    null,
                    null,
                    null,
                    null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                //listId.add(c.getInt(0));
                listBox.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        String[] setBoxName = listBox.toArray(new String[listBox.size()]);
        return setBoxName[0];
    }

    public String getBoxName(int boxId) {
        openBoxDB();
        listBox = new ArrayList<>();
        String[] cols = {COL_BOX};
        try {
            Cursor c = db.query(DB_TABLE,
                                cols,
                                "id = " + boxId,
                                null,
                                null,
                                null,
                                null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBox.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        String[] setBoxName = listBox.toArray(new String[0]);
        return setBoxName[0];
    }

    public Integer getSpinnerPosition(int boxId){
        openBoxDB();
        Integer setSpinnerPosition = 0;
        listBoxId = new ArrayList<>();
        String[] cols = {COL_ID};
        //String[] where = {"完了","今日"};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id != 1",//id.1の完了済みは表示させない
                    null,
                    null,
                    null,
                    null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                listBoxId.add(c.getInt(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        Integer[] array = listBoxId.toArray(new Integer[listBoxId.size()]);
        for (int i=0; i<=listBoxId.size(); i++){
            if (array[i] == boxId){
                setSpinnerPosition = array[i];
                break;
            }
        }
        return setSpinnerPosition - 2;
    }
}
