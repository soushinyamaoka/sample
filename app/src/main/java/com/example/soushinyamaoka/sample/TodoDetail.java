package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class TodoDetail extends Activity {

    private TextView text_Todo;
    private TextView text_Box;
    private TextView text_Date;
    private TextView text_Memo;

    String editTodo;
    String editBox;
    String editDate;
    String editMemo;
    DBHelper db;
    DBAdapter dbAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.todo_detail);
        text_Todo = findViewById(R.id.text_Todo);
        text_Box = findViewById(R.id.text_Box);
        text_Date = findViewById(R.id.text_Date);
        text_Memo = findViewById(R.id.text_Memo);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーの文字列を取得する
        long todoId = intent.getLongExtra( "todoId", -1);

        showDetail(todoId);

        text_Todo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ... 処理 ...
                editTodo = text_Todo.getText().toString();
                try {
                    dbAdapter.openDB();
                    dbAdapter.writeDB(editTodo,null,null,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    public void showDetail(long todoId){
        int colsCount = dbAdapter.getColsCount();
        String todoDetail[] = new String[colsCount];
        dbAdapter.openDB();
        try {
            todoDetail = dbAdapter.readDetailDB(todoId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TextView.setText
        text_Todo.setText(todoDetail[0]);
        text_Box.setText(todoDetail[1]);
        text_Date.setText(todoDetail[2]);
        text_Memo.setText(todoDetail[3]);
    }
}
















