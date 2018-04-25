package com.example.soushinyamaoka.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class TodoEdit extends Activity {

    private EditText editTodo;
    private EditText editBox;
    private TextView editDate;
    private TextView editTime;
    private EditText editMemo;
    public ListView listView;
    private EditText editText;
    private Button editTodoButton;

    String setTextTodo;
    String setTextBox;
    String setTextDate;
    String setTextTime;
    String setTextMemo;
    DBHelper db;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;
    ArrayAdapter<String> adapter;

    int todoId;
    int boxId;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit_todo);
        editTodo = findViewById(R.id.new_edit_Todo);
        editDate = findViewById(R.id.new_edit_Date);
        editTime = findViewById(R.id.new_edit_Time);
        editMemo = findViewById(R.id.new_edit_Memo);
        editTodoButton = findViewById(R.id.edit_todo_button);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        final Spinner spinner_box = (Spinner) findViewById(R.id.new_box_spinner);

        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーの文字列を取得する
        todoId = intent.getIntExtra( "todoId", -1);

        //todoの詳細を表示
        //-------------------------
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
                boxId = boxDBAdapter.getBoxId(spinner_box.getSelectedItemPosition());
                setTextTodo = editTodo.getText().toString();
                setTextBox = boxDBAdapter.changeToBoxName(boxId);
                setTextDate = editDate.getText().toString();
                setTextTime = editTime.getText().toString();
                setTextMemo = editMemo.getText().toString();
                try {
                    if(setTextTodo.equals("")){
                        emptyTaskDialogFragment.show(getFragmentManager(), "empty");
                    } else if (setTextBox == null){
                        setTextBox = "未分類";
                        boxId = 2;
                        dbAdapter.writeDB(setTextTodo, setTextBox, setTextDate, setTextTime, setTextMemo, boxId);

                        finish();
                    } else {
                        dbAdapter.writeDB(setTextTodo, setTextBox, setTextDate, setTextTime, setTextMemo, boxId);

                        finish();
                    }
                } catch (Exception e) {//エラーの場合
                    Log.e(TAG, "SQLExcepption:" + e.toString());
                }
            }
        });

        // Adapterの作成
        adapter = new ArrayAdapter<String>(this, R.layout.text_box_list, getSpinner());
        spinner_box.setAdapter(adapter);

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
        Date time = calendar.getTime();
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

    public String getNowDate(){
        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatterDate = new SimpleDateFormat("yyyy年M月d日");

        // フォーマット
        String nowText = formatterDate.format(now);
        return nowText;
    }

    public String getNowTime(){
        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatterTime = new SimpleDateFormat("HH時mm分");

        // フォーマット
        String nowText = formatterTime.format(now);
        return nowText;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void showDetail()  {
        //String[] setTodo = dbAdapter.getTodoData(todoId);
        //String[] setDate = dbAdapter.getDateData(todoId);
        //String[] setMemo = dbAdapter.getMemoData(todoId);

        //editTodo.setText(setTodo[0]);
        editDate.setText(getNowDate());
        editTime.setText(getNowTime());
        //editMemo.setText(setMemo[0]);
    }

    public ArrayList<String> getSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        try {
            lvAdapter = boxDBAdapter.readBoxSpinnerDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lvAdapter;
    }
}
















