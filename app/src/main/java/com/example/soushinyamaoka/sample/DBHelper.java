package com.example.soushinyamaoka.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SoushinYamaoka on 2018/03/27.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "sampletodo3.db";//DB名
    private final static String DB_TABLE = "test3";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "id";
    //private static final String COL_ID = "id";
    private static final String COL_TODO = "todo";
    private static final String COL_BOX = "box";
    private static final String COL_DATE = "date";
    private static final String COL_MEMO = "memo";

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
                        COL_MEMO + " TEXT" +
                        ")";
        db.execSQL(createTable);
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
    }
}
