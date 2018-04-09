package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TodoDetail extends Activity {

    private EditText text_Todo;
    private EditText text_Box;
    private EditText text_Date;
    private EditText text_Memo;
    private Button custom_Todo;
    private Button custom_Box;
    private Button custom_Date;
    private Button custom_Memo;
    public ListView listView;

    private EditText editText;

    String editTodo;
    String editBox;
    String editDate;
    String editMemo;
    DBHelper db;
    DBAdapter dbAdapter;

    long listviewId = 0;
    int databaseId = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.todo_detail);
        text_Todo = findViewById(R.id.text_Todo);
        text_Box = findViewById(R.id.text_Box);
        text_Date = findViewById(R.id.text_Date);
        text_Memo = findViewById(R.id.text_Memo);
        custom_Todo = findViewById(R.id.custom_todo_button);
        custom_Box = findViewById(R.id.custom_box_button);
        custom_Date = findViewById(R.id.custom_date_button);
        custom_Memo = findViewById(R.id.custom_memo_button);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        //editText = findViewById(R.id.editText);
        listView = findViewById(R.id.list_view);


        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーの文字列を取得する
        listviewId = intent.getLongExtra( "todoId", -1);
        databaseId = dbAdapter.changeId(listviewId);

        //todoの詳細を表示
        //-------------------------
        dbAdapter.openDB();
        try {
            showDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-------------------------
    }

    @Override
    public void onBackPressed(){
        dbAdapter.openDB();
        dbAdapter.updateDB(databaseId,
                            text_Todo.getText().toString(),
                            text_Box.getText().toString(),
                            text_Date.getText().toString(),
                            text_Memo.getText().toString());

        //------------------------------
        //Intent data = new Intent();
        //Bundle bundle = new Bundle();
        //setResult(RESULT_OK, data);

        // finish() で終わらせて
        // Intent data を送る
        finish();
        //------------------------------

        super.onBackPressed();
    }

    public void showDetail()  {
        String[] setTodo;
        String[] setBox;
        String[] setDate;
        String[] setMemo;

        setTodo = dbAdapter.getTodo(databaseId);
        setBox = dbAdapter.getBox(databaseId);
        setDate = dbAdapter.getDate(databaseId);
        setMemo = dbAdapter.getMemo(databaseId);

        text_Todo.setText(setTodo[0]);
        text_Box.setText(setBox[0]);
        text_Date.setText(setDate[0]);
        text_Memo.setText(setMemo[0]);
        editText.setText(setTodo[0]);
    }
}
















