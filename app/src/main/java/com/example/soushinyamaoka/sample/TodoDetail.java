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
    private EditText edit_Time;
    private EditText edit_Memo;
    private EditText edit_Box;
    private TextView text_Todo;
    private TextView text_Date;
    private TextView text_Time;
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
    int boxId;
    int allTodo;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boxDBAdapter = new BoxDBAdapter(this);

        Intent intent = getIntent();
        databaseId = intent.getIntExtra( "databaseId", 0);
        spinnerPosition = intent.getIntExtra("spinnerPosition", -1);
        boxId = intent.getIntExtra("boxName",0);//Main画面で押された位置から取得
        allTodo = intent.getIntExtra("allTodo",-1);//「全て」の画面からきている場合はこれが「0」になる
        boxDBAdapter.openBoxDB();

        if (boxId == 1){//完了済みのタスクの場合
            setContentView(R.layout.complete_todo_detail);
            text_Todo = findViewById(R.id.new_edit_Todo);
            text_Date = findViewById(R.id.new_edit_Date);
            text_Time = findViewById(R.id.new_edit_Time);
            text_Memo = findViewById(R.id.new_edit_Memo);
            text_Box = findViewById(R.id.new_edit_box);
        } else {//完了済み以外の時
            boxName = boxDBAdapter.changeToBoxName(boxId);
            setContentView(R.layout.activity_detail_todo);
            edit_Todo = findViewById(R.id.new_edit_Todo);
            edit_Date = findViewById(R.id.new_edit_Date);
            edit_Time = findViewById(R.id.new_edit_Time);
            edit_Memo = findViewById(R.id.new_edit_Memo);
            spinner_box = (Spinner) findViewById(R.id.new_box_spinner);
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
        if (boxId == 1){//完了済みのタスクの場合
            finish();
        } else {
            boxDBAdapter.openBoxDB();
            boxName = (String)spinner_box.getSelectedItem();
            boxDBAdapter.openBoxDB();
            boxId = boxDBAdapter.changeToBoxId(spinner_box.getSelectedItemPosition() + 2);//ListViewのずれ1と、完了済みを抜いてる分の１をプラス
            dbAdapter.openDB();
            dbAdapter.updateDB(databaseId,
                    edit_Todo.getText().toString(),
                    //edit_Box.getText().toString(),
                    (String)spinner_box.getSelectedItem(),
                    edit_Date.getText().toString(),
                    edit_Time.getText().toString(),
                    edit_Memo.getText().toString(),
                    boxId
                    );
            Intent data = new Intent(TodoDetail.this,ToDoActivity.class);
            if (allTodo == 0){//全てが選ばれていた場合はboxidを0に戻す
                boxId = 0;
            }
            data.putExtra("boxName",boxId);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public void showDetail(String boxName)  {
        String[] setTodo;
        String[] setDate;
        String[] setTime;
        String[] setMemo;
        Integer[] setId;

        dbAdapter.openDB();
        setTodo = dbAdapter.getTodo(databaseId);
        dbAdapter.openDB();
        setDate = dbAdapter.getDate(databaseId);
        dbAdapter.openDB();
        setTime = dbAdapter.getTime(databaseId);
        dbAdapter.openDB();
        setMemo = dbAdapter.getMemo(databaseId);

        if (boxId == 0){
            text_Todo.setText(setTodo[0]);
            text_Date.setText(setDate[0]);
            text_Date.setText(setTime[0]);
            text_Memo.setText(setMemo[0]);
            text_Box.setText(boxName);
        } else if (boxId == 1){//完了済みのタスクの場合
            text_Todo.setText(setTodo[0]);
            text_Date.setText(setDate[0]);
            text_Date.setText(setTime[0]);
            text_Memo.setText(setMemo[0]);
            text_Box.setText(boxName);

        } else {
            edit_Todo.setText(setTodo[0]);
            edit_Date.setText(setDate[0]);
            edit_Time.setText(setTime[0]);
            edit_Memo.setText(setMemo[0]);
            setSpinner();
            //edit_Date.setText(databaseId);
        }
    }

    public ArrayList<String> getSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        boxDBAdapter.openBoxDB();
        try {
            lvAdapter = boxDBAdapter.readBoxSpinnerDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lvAdapter;
    }

    public void setSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        boxDBAdapter.openBoxDB();
        lvAdapter = boxDBAdapter.readBoxSpinnerDB();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lvAdapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_box.setAdapter(adapter);
        spinner_box.setSelection(spinnerPosition+1);//完了済みを抜いている分をマイナスする
    }
}
















