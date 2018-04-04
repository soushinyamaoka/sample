package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static android.content.ContentValues.TAG;

/**
 * Created by syama on 2018/02/03.
 */

public class MainActivity2 extends Activity {

    private EditText editText;
    private Button editButton;
    public ListView listView;
    String[] cols = {"info"};
    DBAdapter2 dbAdapter = new DBAdapter2(this);
    DialogFragment newFragment = new EmptyTaskDialogFragment();
    private SQLiteDatabase db = null;           // SQLiteDatabase
    private final static String DB_TABLE = "test5";//テーブル名

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        editButton = findViewById(R.id.editButton);
        listView = findViewById(R.id.listView);

        dbAdapter.openDB();
        try {
            dbAdapter.readDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String str = editText.getText().toString();//書き込まれた内容(getText)をstrに格納
                    if(str.equals("")){
                        newFragment.show(getFragmentManager(), "test1");
                    }
                    else {
                        dbAdapter.openDB();
                        dbAdapter.writeDB(str);//writeDBメソッドを呼び出し、strを引数として渡す
                        dbAdapter.readDB();
                        showlist2();
                    }
                } catch (Exception e) {//エラーの場合
                    Log.e(TAG, "SQLExcepption:" + e.toString());
                }
            }
        });

        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,View view,
                                                   int position, long id) {
                        dbAdapter.openDB();
                        dbAdapter.deleteDB(id);
                        try {
                            showlist2();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
    }

    public void showlist2(){
        dbAdapter.openDB();
        try {
            //Cursor cursor = dbAdapter.readDB();
            String[] cols = {"info"};
            Cursor cursor = db.query(DB_TABLE, cols, null, null, null, null, null);
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,//第一引数：自オブジェクト
                    R.layout.list,//第2引数：レイアウト設定
                    cursor,//カーソルオブジェクト
                    cols,//	UIにバインドするデータを表す列名のリスト
                    new int[] {R.id.text1},//fromをセットするリソースID
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            );
            listView.setAdapter(adapter);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}