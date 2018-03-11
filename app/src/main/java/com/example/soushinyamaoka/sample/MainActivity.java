package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by syama on 2018/02/03.
 */

public class MainActivity extends Activity{

    private final static String DB_NAME = "test.db";//DB名
    private final static String DB_TABLE = "test";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン

    private EditText editText;
    private Button editButton;
    private Button showButton;
    public TextView textview;
    public ListView listView;

    private SQLiteDatabase db;    //データベースオブジェクト

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        editButton = findViewById(R.id.editButton);
        showButton = findViewById(R.id.showButton);
        textview = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);

        //データベースオブジェクトの取得
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String str = editText.getText().toString();//書き込まれた内容(getText)をstrに格納
                    writeDB(str);//writeDBメソッドを呼び出し、strを引数として渡す
                }catch(Exception e){//エラーの場合
                    textview.setText("書き込み失敗しました");
                }
           }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    listAdapter();
                }catch(Exception e){
                    textview.setText("書き込み失敗しました");
                }
            }
        });
    }

    //データベースへの書き込み
    private void writeDB(String info) throws Exception{
        ContentValues values = new ContentValues();//値を格納するためのvaluesを宣言
        values.put("id", "0");
        values.put("info", info);
        int colNum = db.update(DB_TABLE, values, null, null);
        if(colNum == 0)db.insert(DB_TABLE, "", values);
        //上記はINSERT INTO DB_TABLE (id, 0) VALUES("info", info);と同じ処理をしている
    }

    //データベースからの読み込み
    private String[] readDB() throws Exception{
        String[] cols = {"info"};
        Cursor c = db.query(DB_TABLE, cols,//query()メソッドを使用してデータ検索をする
                "id='0'", null, null, null,null);
        //query()の第一引数にはテーブル名、第二引数には取得対象のカラム名を配列の形で指定する。
        //第三引数以降は取得条件(like演算子など)やgroup by句、order by句を指定する。今回はorder by句のみ指定して、あとはnull
        //検索した結果はcに格納される
        if(c.getCount() == 0)throw new Exception();//全件取得する場合は、moveToFirst()メソッドを使用
        //ArrayList<String> str = new ArrayList<String>();
        String[] str = new String[c.getCount()];
        c.moveToFirst();                           //確実にcursor内のデータ参照先を先頭に持ってくる。
        for(int i=0; i<c.getCount(); i++){
            str[i] = c.getString(i);
            c.moveToNext();
        }
        c.close();
        return str;
    }

    private void listAdapter() throws Exception {
        String[] str = readDB();
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, str);

        listView.setAdapter(arrayAdapter);//strをeditTextで表示
    }

    //データベースヘルパーの定義
    private static class DBHelper extends SQLiteOpenHelper {
        //データベースヘルパーのコンストラクタ
        public DBHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION); //DB名、テーブル名、DBバージョンを定数として保持している
        }

        //データベースの生成
        //@Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("create table if not exists " +
                    DB_TABLE + "(id text primary key,info text)");
        }

        //データベースのアップグレード
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,
                              int newVersion){
            db.execSQL("drop table if exists "+DB_TABLE);
            onCreate(db);
        }
    }
}
