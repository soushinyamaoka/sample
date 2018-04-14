package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

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
    BoxDBHelper boxdb;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;

    long listviewId = 0;
    int databaseId = 0;
    Spinner boxSpinner;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail_todo);
        text_Todo = findViewById(R.id.new_edit_Todo);
        text_Date = findViewById(R.id.new_edit_Date);
        text_Memo = findViewById(R.id.new_edit_Memo);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxSpinner = (Spinner) findViewById(R.id.new_box_spinner);
        //editText = findViewById(R.id.editText);
        //listView = findViewById(R.id.list_view_todo);

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
                    (String)boxSpinner.getSelectedItem(),
                            text_Date.getText().toString(),
                            text_Memo.getText().toString());
        finish();
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
        //boxSpinner.setSelection(setBox[0]);
        text_Date.setText(setDate[0]);
        text_Memo.setText(setMemo[0]);
        editText.setText(setTodo[0]);
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
















