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

    private final static String DB_TABLE = "testbox10";//テーブル名
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
        values.put(COL_BOX, boxName);

        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + boxDataBaseId, null);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    public void deleteBoxDB(int boxDataBaseId){
        db.delete(DB_TABLE, "id = " + boxDataBaseId, null);
        db.close();
    }

    //Mainで呼びだされるカテゴリ一覧
    public String[] readBoxDB() {
        listBox = new ArrayList<>();
        String[] cols = {COL_BOX};
        //String[] where = {"完了","今日"};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id != 1 AND id != 2",//id.1の完了済みと、id.2の今日は表示させない
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
        listBoxId = new ArrayList<Integer>();
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
                //listId.add(c.getInt(0));
                listBox.add(c.getString(0));
                c.moveToNext();
            }
            c.close();

        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listBox;
    }

    //データベースからの読み込み
    public Integer getBoxId(int listViewId) {
        listBoxId = new ArrayList<>();
        String[] cols = {COL_ID};
        Integer setBoxId;
        //String[] where = {"完了","今日"};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id != 1 AND id != 2",//id.1の完了済みと、id.2の今日は表示させない
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

    public String changeToBoxName(int boxId){
        String setBoxName = null;
        listBox = new ArrayList<>();
        String[] cols = {COL_BOX};
        String str = String.valueOf(boxId);
        String[] selectionArgs = {str};
        String[] where = {"完了","今日"};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,//cols
                    "id =" + boxId,//"id =" + boxId,
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
        String[] array;
        array = listBox.toArray(new String[listBox.size()]);
        setBoxName = array[0];
        return setBoxName;
    }

    public int changeToBoxId(int boxId){
        int setBoxId;
        listBoxId = new ArrayList<>();
        String[] cols = {COL_ID};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id =" + boxId,
                    null,
                    null,
                    null,
                    ORDER_BY);
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
        setBoxId = array[0];
        return setBoxId;
    }

    public ArrayList<Integer> getBoxDataBaseId() {
        ArrayList<Integer> listID = new ArrayList<>();
        String[] cols = {"id"};
        try {
            Cursor c = db.query(DB_TABLE,
                    cols,
                    null,
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

    public Integer getSpinnerPosition(int boxId){
        Integer setSpinnerPosition = 0;
        listBoxId = new ArrayList<>();
        String[] cols = {COL_ID};
        //String[] where = {"完了","今日"};
        try {
            Cursor c = db.query(
                    DB_TABLE,
                    cols,
                    "id != 1 AND id != 2",//id.1の完了済みと、id.2の今日は表示させない
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
        for (int i=0; i<=listBoxId.size(); i++){
            if (array[i] == boxId){
                setSpinnerPosition = array[i];
                break;
            }
        }
        return setSpinnerPosition-3;
    }
}
