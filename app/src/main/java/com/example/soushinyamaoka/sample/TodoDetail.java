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

        try {
            showDetail(todoId);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public void showDetail(long todoId) throws Exception {
        dbAdapter.openDB();
        text_Todo.setText((CharSequence) dbAdapter.getTodo(todoId));
    }
}
















