package com.example.soushinyamaoka.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SoushinYamaoka on 2018/03/27.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "test6.db";//DB名
    private final static String DB_TABLE = "test6";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "_id";

    //データベースの生成
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table if not exists " +
                DB_TABLE + "(id integer primary key autoincrement ,info text)");//_id text primary key
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
