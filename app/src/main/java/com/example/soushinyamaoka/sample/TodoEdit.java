package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class TodoEdit extends Activity {

    private EditText editTodo;
    private EditText editBox;
    private EditText editDate;
    private EditText editMemo;
    public ListView listView;
    private EditText editText;
    private Button editTodoButton;

    String setTextTodo;
    String setTextBox;
    String setTextDate;
    String setTextMemo;
    DBHelper db;
    BoxDBHelper boxdb;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;
    ArrayAdapter<String> adapter;

    long listviewId = 0;
    int databaseId = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit_todo);
        editTodo = findViewById(R.id.new_edit_Todo);
        editDate = findViewById(R.id.new_edit_Date);
        editMemo = findViewById(R.id.new_edit_Memo);
        editTodoButton = findViewById(R.id.edit_todo_button);
        db = new DBHelper(this);
        boxdb = new BoxDBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        //editText = findViewById(R.id.editText);
        //listView = findViewById(R.id.list_view_todo);
        final Spinner boxSpinner = (Spinner) findViewById(R.id.new_box_spinner);


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
        final EmptyTaskDialogFragment emptyTaskDialogFragment = new EmptyTaskDialogFragment();
        editTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextTodo = editTodo.getText().toString();//書き込まれた内容(getText)をstrに格納
                setTextBox = (String)boxSpinner.getSelectedItem();
                try {
                    if(setTextTodo.equals("")){
                        emptyTaskDialogFragment.show(getFragmentManager(), "empty");
                    } else if (setTextBox != null){
                        dbAdapter.openDB();
                        dbAdapter.writeDB(setTextTodo, setTextBox, setTextDate, setTextMemo);

                        boxDBAdapter.openBoxDB();
                        boxDBAdapter.writeBoxDB(setTextBox);

                        finish();
                    } else {
                        setTextBox = "今日";
                        dbAdapter.openDB();
                        dbAdapter.writeDB(setTextTodo, setTextBox, setTextDate, setTextMemo);

                        boxDBAdapter.openBoxDB();
                        boxDBAdapter.writeBoxDB(setTextBox);

                        finish();
                    }
                } catch (Exception e) {//エラーの場合
                    Log.e(TAG, "SQLExcepption:" + e.toString());
                }
            }
        });

        // Adapterの作成
        adapter = new ArrayAdapter<String>(this, R.layout.text_box_list, getSpinner());
        boxSpinner.setAdapter(adapter);
        boxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(TodoEdit.this, "選んでません", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
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

        editTodo.setText(setTodo[0]);
        //editBox.setText(setBox[0]);
        editDate.setText(setDate[0]);
        editMemo.setText(setMemo[0]);
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
















