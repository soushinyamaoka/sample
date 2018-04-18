package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoDetail extends Activity {

    private EditText edit_Todo;
    private EditText edit_Date;
    private EditText edit_Memo;
    private EditText edit_Box;
    private TextView text_Todo;
    private TextView text_Date;
    private TextView text_Memo;
    private TextView text_Box;
    private Spinner spinner_box;
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

        Intent intent = getIntent();
        databaseId = intent.getIntExtra( "databaseId", -1);
        spinnerPosition = intent.getIntExtra("spinnerPosition", -1);
        boxName = intent.getStringExtra("boxName");

        if (boxName.equals("完了")){
            setContentView(R.layout.complete_todo_detail);
            text_Todo = findViewById(R.id.new_edit_Todo);
            text_Date = findViewById(R.id.new_edit_Date);
            text_Memo = findViewById(R.id.new_edit_Memo);
            text_Box = findViewById(R.id.new_edit_box);
        } else {
            setContentView(R.layout.activity_detail_todo);
            edit_Todo = findViewById(R.id.new_edit_Todo);
            edit_Date = findViewById(R.id.new_edit_Date);
            edit_Memo = findViewById(R.id.new_edit_Memo);
            spinner_box = (Spinner) findViewById(R.id.spinner_box);
        }

        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        //todoの詳細を表示
        //-------------------------
        showDetail(boxName);
        //-------------------------
    }

    @Override
    public void onBackPressed(){
        if (boxName.equals("完了")){
            finish();
        } else{
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
    }

    public void showDetail(String boxName)  {
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

        if (boxName.equals("完了")){
            text_Todo.setText(setTodo[0]);
            text_Date.setText(setDate[0]);
            text_Memo.setText(setMemo[0]);
            text_Box.setText("完了");
        } else {
            edit_Todo.setText(setTodo[0]);
            edit_Date.setText(setDate[0]);
            edit_Memo.setText(setMemo[0]);
            setSpinner();
            //edit_Date.setText(databaseId);
        }
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
        boxDBAdapter.openBoxDB();
        lvAdapter = boxDBAdapter.readBoxDB2();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lvAdapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_box.setAdapter(adapter);
        spinner_box.setSelection(spinnerPosition);
    }
}
















