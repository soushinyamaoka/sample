package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by syama on 2018/02/03.
 */

public class MainActivity extends Activity {

    private final static String DB_NAME = "test5.db";//DB名
    private final static String DB_TABLE = "test5";//テーブル名
    private final static int    DB_VERSION = 1;   //バージョン
    private static final String COL_ID = "_id";

    private EditText editText;
    private Button editButton;
    public ListView listView;
    DBAdapter dbAdapter = new DBAdapter(this);
    DialogFragment newFragment = new SampleDialogFragment();
    DBHelper db = new DBHelper(this);

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        editButton = findViewById(R.id.editButton);
        listView = findViewById(R.id.listView);

        showlist();

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
                        showlist();
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
                ArrayList<Integer> deleteListID = deleteList();//DB上のid
                int deleteID = deleteListID.get((int) id);//削除対象のリストと同じ位置にある、DB上のidを取得
                dbAdapter.deleteDB(deleteID);
                showlist();
                try {
                    //showlist();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    //    public  void showList2(){
    //    SQLiteDatabase db = (new DBHelper(this)).getWritableDatabase();
    //    @SuppressLint("Recycle") Cursor cursor = db.query(DB_TABLE, null, null, null, null, null, null);
    //    db.close();
    //}
    public void showlist(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list, (List<String>) lvAdapter);
        // ListViewにAdapterを関連付ける
        listView.setAdapter(adapter);
    }

    public ArrayList<Integer> deleteList(){
        ArrayList<Integer> idAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            idAdapter = dbAdapter.deletereadDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idAdapter;
    }
}