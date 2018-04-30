package todo.example.soushinyamaoka.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by SoushinYamaoka on 2018/03/27.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "sampletodo11.db";//DB名
    private final static String DB_TABLE = "test11";//テーブル名
    private final static String DB_BOX_TABLE = "testbox11";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "id";
    //private static final String COL_ID = "id";
    private static final String COL_TODO = "todo";
    private static final String COL_BOX = "box";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_MEMO = "memo";
    private static final String COL_BOXID = "boxid";

    //データベースの生成
    //@Override
    //public void onCreate(SQLiteDatabase db){
    //    db.execSQL("create table if not exists " +
    //            DB_TABLE + "(id integer primary key autoincrement ,todo text,box text,date text,memo text)");//_id text primary key
    //}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ( " +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_TODO + " TEXT, " +
                        COL_BOX + " TEXT, " +
                        COL_DATE + " TEXT, " +
                        COL_TIME + " TEXT, " +
                        COL_MEMO + " TEXT," +
                        COL_BOXID + " INTEGER" +
                        ")";
        db.execSQL(createTable);

        String createBoxTable =
                "CREATE TABLE IF NOT EXISTS " + DB_BOX_TABLE + " ( " +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_BOX + " TEXT" +
                        ")";
        db.execSQL(createBoxTable);

        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put(COL_BOX, "完了済み" +
                "");//id=1
        //空欄でも書き込めるようになっているので要修正
        try {
            db.insert(DB_BOX_TABLE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }

        values.put(COL_BOX, "未分類");//id=2
        //空欄でも書き込めるようになっているので要修正
        try {
            db.insert(DB_BOX_TABLE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "SQLExcepption:" + e.toString());
        }
    }

    //データベースヘルパーのコンストラクタ
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION); //DB名、テーブル名、DBバージョンを定数として保持している
    }

    //データベースのアップグレード
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists "+DB_TABLE);
        onCreate(db);
        db.execSQL("drop table if exists "+DB_BOX_TABLE);
        onCreate(db);
    }
}
