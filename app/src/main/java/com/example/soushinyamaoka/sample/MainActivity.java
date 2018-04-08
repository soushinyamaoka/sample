package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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
    String editTodo;
    String editBox;
    String editDate;
    String editMemo;

    int datebaseId = 0;

    private EditText edit_Text;
    private Button editButton;
    public ListView listView;
    DBAdapter dbAdapter;
    DialogFragment emptyTaskDialogFragment;
    DBHelper db;
    ArrayAdapter<String> adapter;
    ArrayList<String> lvAdapter;
    DeleteDialogFragment deleteDialogFragment;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        edit_Text = findViewById(R.id.edit_Text);
        editButton = findViewById(R.id.edit_Button);
        listView = findViewById(R.id.list_view);
        dbAdapter = new DBAdapter(this);
        emptyTaskDialogFragment = new EmptyTaskDialogFragment();
        db = new DBHelper(this);

        showList(this);

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    editTodo = edit_Text.getText().toString();//書き込まれた内容(getText)をstrに格納
                    if(editTodo.equals("")){
                        emptyTaskDialogFragment.show(getFragmentManager(), "empty");
                    }
                    else {
                        dbAdapter.openDB();
                        dbAdapter.writeDB(editTodo, editBox, editDate, editMemo);//writeDBメソッドを呼び出し、strを引数として渡す
                        dbAdapter.readDB();
                        showList(getApplicationContext());
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
                                           int position, long listviewId) {
                deleteDialog(listviewId);
                showList(getApplicationContext());

                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(MainActivity.this, TodoDetail.class);
                // intentへ添え字付で値を保持させる
                intent.putExtra( "todoId", id );
                startActivity(intent);
                showList(getApplicationContext());
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void showList(Context context){
        lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(context, R.layout.list, (List<String>) lvAdapter);
        listView.setAdapter(adapter);
    }

    public void deleteList(Context context, long listviewId){
        dbAdapter = new DBAdapter(context);
        try {
            dbAdapter.openDB();
            datebaseId = dbAdapter.changeId(listviewId);//List上のIDをDB上のIDに変換
            dbAdapter.openDB();
            dbAdapter.deleteDB(datebaseId);//DB上の値をDB上のidで削除。
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDialog(long listviewId){
        deleteDialogFragment = new DeleteDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("deleteId", listviewId);
        deleteDialogFragment.setArguments(bundle);
        deleteDialogFragment.show(getFragmentManager(), "delete");
    }
}