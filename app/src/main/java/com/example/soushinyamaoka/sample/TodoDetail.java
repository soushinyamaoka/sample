package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class TodoDetail extends Activity {

    private EditText edit_Todo;
    private EditText edit_Date;
    private EditText edit_Memo;
    Spinner spinner_box;
    DBHelper db;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;

    long listviewId = 0;
    int databaseId = 0;
    int spinnerPosition = 0;
    String boxName;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail_todo);
        edit_Todo = findViewById(R.id.new_edit_Todo);
        edit_Date = findViewById(R.id.new_edit_Date);
        edit_Memo = findViewById(R.id.new_edit_Memo);
        spinner_box = (Spinner) findViewById(R.id.spinner_box);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        //intentからデータベースIDを取得する
        databaseId = intent.getIntExtra( "databaseId", -1);
        //現在のスピナーの位置を取得
        spinnerPosition = intent.getIntExtra("spinnerPosition", -1);
        //現在のカテゴリ名を取得
        boxName = intent.getStringExtra("boxName");

        //todoの詳細を表示
        //-------------------------
        showDetail();
        //-------------------------

        setSpinner();
        /*
        spinner_button.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
        long id) {
        String setBox =  parent.getSelectedItem().toString();
        edit_Box.setText(setBox);
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        dbAdapter.openDB();
        String[] setBox = dbAdapter.getBox(databaseId);
        edit_Box.setText(setBox[0]);
        }
        });
        */
    }

    @Override
    public void onBackPressed(){

        dbAdapter.openDB();
        dbAdapter.updateDB(databaseId,
                            edit_Todo.getText().toString(),
                            //edit_Box.getText().toString(),
                            (String)spinner_box.getSelectedItem(),
                            edit_Date.getText().toString(),
                            edit_Memo.getText().toString());
        Intent data = new Intent(TodoDetail.this,ToDoActivity.class);
        data.putExtra("boxName",boxName);
        setResult(RESULT_OK, data);
        finish();
    }

    public void showDetail()  {
        String[] setTodo;
        String[] setDate;
        String[] setMemo;
        Integer[] setId;

        dbAdapter.openDB();
        setTodo = dbAdapter.getTodo(databaseId);
        dbAdapter.openDB();
        setDate = dbAdapter.getDate(databaseId);
        dbAdapter.openDB();
        setMemo = dbAdapter.getMemo(databaseId);

        edit_Todo.setText(setTodo[0]);
        edit_Date.setText(setDate[0]);
        edit_Memo.setText(setMemo[0]);
        //edit_Date.setText(databaseId);
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

    public void setSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        //dbAdapter.openDB();
        //lvAdapter = dbAdapter.readDBBox();
        boxDBAdapter.openBoxDB();
        lvAdapter = boxDBAdapter.readBoxDB2();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lvAdapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_box.setAdapter(adapter);
        spinner_box.setSelection(spinnerPosition);
    }
}
















