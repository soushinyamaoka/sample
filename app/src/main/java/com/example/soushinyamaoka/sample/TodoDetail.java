package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class TodoDetail extends Activity {

    private EditText edit_Todo;
    private EditText edit_Date;
    private EditText edit_Memo;
    private TextView edit_Box;
    Spinner spinner_button;
    DBHelper db;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;

    long listviewId = 0;
    int databaseId = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail_todo);
        edit_Todo = findViewById(R.id.new_edit_Todo);
        edit_Box = findViewById(R.id.new_edit_box);
        edit_Date = findViewById(R.id.new_edit_Date);
        edit_Memo = findViewById(R.id.new_edit_Memo);
        spinner_button = (Spinner) findViewById(R.id.spinner_button);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentからIDを取得する
        listviewId = intent.getLongExtra( "todoId", -1);
        databaseId = dbAdapter.changeId(listviewId);

        //todoの詳細を表示
        //-------------------------
        showDetail();
        //-------------------------

        setSpinner();
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
    }

    @Override
    public void onBackPressed(){

        dbAdapter.openDB();
        dbAdapter.updateDB(databaseId,
                            edit_Todo.getText().toString(),
                            edit_Box.getText().toString(),
                            edit_Date.getText().toString(),
                            edit_Memo.getText().toString());
        Intent data = new Intent(TodoDetail.this,ToDoActivity.class);
        data.putExtra("boxName",edit_Box.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    public void showDetail()  {
        String[] setTodo;
        String[] setBox;
        String[] setDate;
        String[] setMemo;

        dbAdapter.openDB();
        setTodo = dbAdapter.getTodo(databaseId);
        dbAdapter.openDB();
        setBox = dbAdapter.getBox(databaseId);
        dbAdapter.openDB();
        setDate = dbAdapter.getDate(databaseId);
        dbAdapter.openDB();
        setMemo = dbAdapter.getMemo(databaseId);

        edit_Todo.setText(setTodo[0]);
        edit_Box.setText(setBox[0]);
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

    public void setSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        //dbAdapter.openDB();
        //lvAdapter = dbAdapter.readDBBox();
        boxDBAdapter.openBoxDB();
        lvAdapter = boxDBAdapter.readBoxDB2();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lvAdapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_button.setAdapter(adapter);
    }
}
















