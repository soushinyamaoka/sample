package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

public class TodoDetail extends Activity {

    private EditText edit_Todo;
    private EditText edit_Date;
    private EditText edit_Memo;
    Spinner edit_boxSpinner;
    DBHelper db;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;

    long listviewId = 0;
    int databaseId = 0;
    int spinnerPosition;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail_todo);
        edit_Todo = findViewById(R.id.new_edit_Todo);
        edit_Date = findViewById(R.id.new_edit_Date);
        edit_Memo = findViewById(R.id.new_edit_Memo);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        edit_boxSpinner = (Spinner) findViewById(R.id.new_box_spinner);

        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーの文字列を取得する
        listviewId = intent.getLongExtra( "todoId", -1);
        databaseId = dbAdapter.changeId(listviewId);

        spinnerPosition = intent.getIntExtra("spinnerPosition",0);

        //todoの詳細を表示
        //-------------------------
        showDetail();
        //-------------------------
    }

    @Override
    public void onBackPressed(){

        dbAdapter.openDB();
        dbAdapter.updateDB(databaseId,
                            edit_Todo.getText().toString(),
                    (String)edit_boxSpinner.getSelectedItem(),
                            edit_Date.getText().toString(),
                            edit_Memo.getText().toString());
        finish();
        super.onBackPressed();
    }

    public void showDetail()  {
        String[] setTodo;
        String[] setDate;
        String[] setMemo;

        dbAdapter.openDB();
        setTodo = dbAdapter.getTodo(databaseId);
        dbAdapter.openDB();
        setDate = dbAdapter.getDate(databaseId);
        dbAdapter.openDB();
        setMemo = dbAdapter.getMemo(databaseId);
        dbAdapter.openDB();
        ArrayList<String> lvAdapter = new ArrayList<>();
        lvAdapter = dbAdapter.readDBBox();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lvAdapter);

        edit_Todo.setText(setTodo[0]);
        edit_boxSpinner.setAdapter(adapter);
        edit_boxSpinner.setSelection(spinnerPosition);
        edit_Date.setText(setDate[0]);
        edit_Memo.setText(setMemo[0]);
    }

    public ArrayList<String> getSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        boxDBAdapter.openBoxDB();
        try {
            lvAdapter = boxDBAdapter.readBoxDB2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lvAdapter;
    }
}
















