package com.example.soushinyamaoka.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodoDetail extends Activity {

    private EditText editTodo;
    private TextView editDate;
    private TextView editTime;
    private EditText editMemo;
    private EditText editBox;
    private TextView textTodo;
    private TextView textDate;
    private TextView textTime;
    private TextView textMemo;
    private TextView textBox;
    private Spinner spinner_box;
    DBHelper db;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;

    long listviewId = 0;
    int todoId = 0;
    int spinnerPosition = 0;
    String boxName;
    int boxId;
    boolean allTodo = false;
    boolean todayTodo = false;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boxDBAdapter = new BoxDBAdapter(this);

        Intent intent = getIntent();
        todoId = intent.getIntExtra( "todoId", 0);
        spinnerPosition = intent.getIntExtra("spinnerPosition", -1);
        boxId = intent.getIntExtra("boxName",0);//Main画面で押された位置から取得
        if (boxId == 0) {//全てが選ばれていた場合
            allTodo = true;
        } else if (boxId == -1) {//今日が選ばれていた場合
            todayTodo = true;
        }

        boxDBAdapter.openBoxDB();

        if (boxId == 1){//完了済みのタスクの場合
            setContentView(R.layout.complete_todo_detail);
            textTodo = findViewById(R.id.new_edit_Todo);
            textDate = findViewById(R.id.new_edit_Date);
            textTime = findViewById(R.id.new_edit_Time);
            textMemo = findViewById(R.id.new_edit_Memo);
            textBox = findViewById(R.id.new_edit_box);
        } else {//完了済み以外の時
            boxName = boxDBAdapter.changeToBoxName(boxId);
            setContentView(R.layout.activity_detail_todo);
            editTodo = findViewById(R.id.new_edit_Todo);
            editDate = findViewById(R.id.new_edit_Date);
            editTime = findViewById(R.id.new_edit_Time);
            editMemo = findViewById(R.id.new_edit_Memo);
            spinner_box = (Spinner) findViewById(R.id.new_box_spinner);
        }

        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        //todoの詳細を表示
        //-------------------------
        showDetail(boxName);
        //-------------------------

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editTime.setText(hourOfDay + "時" + minute + "分");
            }
        };

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                editDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            }
        };
        Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog;
        final TimePickerDialog timePickerDialog;
        int year = calendar.get(Calendar.YEAR); // 年
        int monthOfYear = calendar.get(Calendar.MONTH); // 月
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 日
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);//時間
        int minute = calendar.get(Calendar.MINUTE);//分

        datePickerDialog = new DatePickerDialog(this, R.layout.date_picker,
                onDateSetListener, year, monthOfYear, dayOfMonth);
        editDate.setClickable(true);
        editDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        timePickerDialog = new TimePickerDialog(this, R.layout.time_picker,
                onTimeSetListener, hourOfDay, minute, true);
        editTime.setClickable(true);
        editTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ダイアログを表示する
                timePickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (boxId == 1){//完了済みのタスクの場合
            finish();
        } else {
            //boxDBAdapter.openBoxDB();
            //boxName = (String)spinner_box.getSelectedItem();
            boxDBAdapter.openBoxDB();
            int updateBoxId = boxDBAdapter.changeToBoxId(spinner_box.getSelectedItemPosition() + 2);//ListViewのずれ1と、完了済みを抜いてる分の１をプラス
            boxDBAdapter.openBoxDB();
            boxName = boxDBAdapter.changeToBoxName(updateBoxId);
            dbAdapter.openDB();
            dbAdapter.updateDB(todoId,
                    editTodo.getText().toString(),
                    //edit_Box.getText().toString(),
                    (String)spinner_box.getSelectedItem(),
                    editDate.getText().toString(),
                    editTime.getText().toString(),
                    editMemo.getText().toString(),
                    updateBoxId
                    );
            Intent data = new Intent(TodoDetail.this,ToDoActivity.class);
            if (allTodo){//全てが選ばれていた場合はboxidを0に戻す
                boxId = 0;
            } else if (todayTodo) {
                boxId = -1;
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
        setTodo = dbAdapter.getTodoData(todoId);
        dbAdapter.openDB();
        setDate = dbAdapter.getDateData(todoId);
        dbAdapter.openDB();
        setTime = dbAdapter.getTimeData(todoId);
        dbAdapter.openDB();
        setMemo = dbAdapter.getMemoData(todoId);

        if (boxId == 0){
            textTodo.setText(setTodo[0]);
            textDate.setText(setDate[0]);
            textTime.setText(setTime[0]);
            textMemo.setText(setMemo[0]);
            textBox.setText(boxName);
        } else if (boxId == 1){//完了済みのタスクの場合
            textTodo.setText(setTodo[0]);
            textDate.setText(setDate[0]);
            textTime.setText(setTime[0]);
            textMemo.setText(setMemo[0]);
            textBox.setText(boxName);

        } else {
            editTodo.setText(setTodo[0]);
            editDate.setText(setDate[0]);
            editTime.setText(setTime[0]);
            editMemo.setText(setMemo[0]);
            setSpinner();
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
        spinner_box.setSelection(spinnerPosition);//完了済みを抜いている分をマイナスする
    }
}
















