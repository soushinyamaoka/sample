package todo.example.soushinyamaoka.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class DBAdapter extends AppCompatActivity {
    private final static String DB_TABLE = "test11";//テーブル名
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
        openDB();
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
        openDB();
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

    //完了済みtodoを元に戻す際に使用「未分類」に戻される
    public void playBackDB(int todoId, String boxName, int boxId) {
        openDB();
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_BOX, boxName);
        values.put(COL_BOXID, boxId);
        //空欄でも書き込めるようになっているので要修正
        try {
            db.update(DB_TABLE, values, "id = " + todoId, null);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    //データベースからの読み込み
    public ArrayList<String> readDB() {
        openDB();
        //listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        String[] cols = {"todo"};
        try {
            Cursor c = db.query(DB_TABLE,
                    cols,
                    "boxId != 1",//boxid.1=「完了済み」以外を表示
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

    public ArrayList<String> readTodayDB(String nowDate){
        openDB();
        listTodo = new ArrayList<>();
        listDate = new ArrayList<>();
        ArrayList<String> todayTodo = new ArrayList<>();
        //List<String> todayTodo;
        String[] cols = {"todo","date"};
        try {
            Cursor c = db.query(DB_TABLE,
                    cols,
                    "boxId != 1",//現在以前の日付けのデータを抽出
                    null,
                    null,
                    null,
                    ORDER_BY);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                //listId.add(c.getInt(0));
                listTodo.add(c.getString(0));
                listDate.add(c.getString(1));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        for (int i=0; i<listDate.size(); i++){
            String[] arrayDate = new String[listDate.size()];
            String[] arrayTodo = new String[listTodo.size()];
            arrayDate[i] = listDate.get(i);
            arrayTodo[i] = listTodo.get(i);
            if (!arrayDate[i].isEmpty()){
                if (arrayDate[i].compareTo(nowDate)<=0){//nowDateが大きければ+1/同じなら0/小さければ
                    todayTodo.addAll(Arrays.asList(arrayTodo[i]));
                }
            }
        }
        return todayTodo;
    }

    public ArrayList<String> readDividedBoxDB(int boxId) {
        openDB();
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        String[] cols = {"todo"};
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
                listTodo.add(c.getString(0));
                c.moveToNext();
            }
            c.close();
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        return listTodo;
    }

    public void deleteDB(int boxId){
        openDB();
        db.delete(DB_TABLE, "boxId = " + boxId, null);
        db.close();
    }

    public String getTodoData(int todoId) {
        openDB();
        listTodo = new ArrayList<>();
        String setTodo;
        String[] array = new String[0];
        String[] cols = {"todo"};
        try {
            Cursor c = db.query(DB_TABLE,
                                cols,
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
            array = listTodo.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        setTodo = array[0];
        return setTodo;
    }

    public String getBoxData(int todoId) {
        openDB();
        listBox = new ArrayList<>();
        String setBox;
        String[] array = new String[0];
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
            array = listBox.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        setBox = array[0];
        return setBox;
    }

    public String getDateData(int todoId) {
        openDB();
        listDate = new ArrayList<>();
        String[] array = new String[0];
        String setDate;
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
            array = listDate.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        setDate = array[0];
        return setDate;
    }

    public String getTimeData(int todoId) {
        openDB();
        listTime = new ArrayList<>();
        String[] array = new String[0];
        String setTime;
        try {
            Cursor c = db.query(DB_TABLE,
                    new String[]{COL_TIME},
                    "id = " + todoId,
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
            array = listTime.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        setTime = array[0];
        return setTime;
    }

    public String getMemoData(int todoId) {
        openDB();
        listMemo = new ArrayList<>();
        String[] array = new String[0];
        String setMemo;
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
            array = listMemo.toArray(new String[0]);
        }catch(SQLException e) {
            Log.e(TAG, "SQLExcepption:"+e.toString());
        }
        setMemo = array[0];
        return setMemo;
    }

    public Integer getBoxIdData(int todoId) {
        openDB();
        listBoxId = new ArrayList<>();
        Integer[] boxId;
        try {
            Cursor c = db.query(DB_TABLE,
                    new String[]{COL_BOXID},
                    "id = " + todoId,
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
        boxId = listBoxId.toArray(new Integer[0]);
        return boxId[0];
    }

    //タップしたTODOからDB上でのidを取得※全てのときのみ？
    public Integer getTodoId(String nowDate, int boxId ,long listViewId) {
        openDB();
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        Integer setTodoId;

        if (boxId == -1) {//今日が選択されているとき
            String[] cols = {COL_ID,COL_DATE};
            try {
                Cursor c = db.query(DB_TABLE,
                        cols,
                        "boxId != 1",//現在以前の日付けのデータを抽出
                        null,
                        null,
                        null,
                        ORDER_BY);
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    //listId.add(c.getInt(0));
                    listId.add(c.getInt(0));
                    c.moveToNext();
                }
                c.close();
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
            ArrayList subListId = new ArrayList();
            for (int i=0; i<listDate.size(); i++){
                String[] arrayDate = new String[listDate.size()];
                Integer[] arrayId = new Integer[listId.size()];
                arrayDate[i] = listDate.get(i);
                arrayId[i] = listId.get(i);
                if (!arrayDate[i].isEmpty()){
                    if (arrayDate[i] != null){
                        if (arrayDate[i].compareTo(nowDate)<=0){//nowDateが大きければ+1/同じなら0/小さければ
                            subListId.addAll(Arrays.asList(arrayId[i]));
                        }
                    }
                }
            }
            listId = new ArrayList<>();
            listId = subListId;//まとめてlistIdでreturnしているので、最後に代入しなおす
        } else if (boxId == 0){//boxId==0で全てを選択しているとき
            String[] cols = {COL_ID};
            try {
                Cursor c = db.query(DB_TABLE,
                        cols,
                        "boxId !=1",//id.1完了済み以外全て
                        null,
                        null,
                        null,
                        ORDER_BY);
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    listId.add(c.getInt(0));
                    c.moveToNext();
                }
                c.close();
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
        } else {//特定のカテゴリor完了済みが選択されているとき
            String[] cols = {COL_ID};
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
                }
                c.close();
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
        }
        Integer[] array;
        array = listId.toArray(new Integer[listId.size()]);
        setTodoId = array[(int) listViewId];
        return setTodoId;
    }

    public Integer[] getTodoIdAsArray(String nowDate, int boxId) {
        openDB();
        listId = new ArrayList<Integer>();
        listTodo = new ArrayList<>();
        Integer[] setTodoId;

        if (boxId == -1) {//今日が選択されているとき
            String[] cols = {COL_ID,COL_DATE};
            try {
                Cursor c = db.query(DB_TABLE,
                        cols,
                        "boxId != 1",//現在以前の日付けのデータを抽出
                        null,
                        null,
                        null,
                        ORDER_BY);
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    //listId.add(c.getInt(0));
                    listId.add(c.getInt(0));
                    c.moveToNext();
                }
                c.close();
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
            ArrayList subListId = new ArrayList();
            for (int i=0; i<listDate.size(); i++){
                String[] arrayDate = new String[listDate.size()];
                Integer[] arrayId = new Integer[listId.size()];
                arrayDate[i] = listDate.get(i);
                arrayId[i] = listId.get(i);
                if (!arrayDate[i].isEmpty()){
                    if (arrayDate[i] != null){
                        if (arrayDate[i].compareTo(nowDate)<=0){//nowDateが大きければ+1/同じなら0/小さければ
                            subListId.addAll(Arrays.asList(arrayId[i]));
                        }
                    }
                }
            }
            listId = new ArrayList<>();
            listId = subListId;//まとめてlistIdでreturnしているので、最後に代入しなおす
        } else if (boxId == 0){//boxId==0で全てを選択しているとき
            String[] cols = {COL_ID};
            try {
                Cursor c = db.query(DB_TABLE,
                        cols,
                        "boxId !=1",//id.1完了済み以外全て
                        null,
                        null,
                        null,
                        ORDER_BY);
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    listId.add(c.getInt(0));
                    c.moveToNext();
                }
                c.close();
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
        } else {//特定のカテゴリor完了済みが選択されているとき
            String[] cols = {COL_ID};
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
                }
                c.close();
            }catch(SQLException e) {
                Log.e(TAG, "SQLExcepption:"+e.toString());
            }
        }
        setTodoId = listId.toArray(new Integer[listId.size()]);
        return setTodoId;
    }
}
