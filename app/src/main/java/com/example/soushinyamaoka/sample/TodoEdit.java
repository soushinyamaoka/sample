package com.example.soushinyamaoka.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class TodoEdit extends Activity {

    private EditText editTodo;
    private TextView editDate;
    private TextView editTime;
    private EditText editMemo;
    private Button editTodoButton;
    private Spinner spinner_box;
    private CheckBox reminderButton;

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
        spinner_box = (Spinner) findViewById(R.id.new_box_spinner);
        reminderButton = findViewById(R.id.reminder_button);
        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーの文字列を取得する
        todoId = intent.getIntExtra( "todoId", -1);

        //todoの詳細を表示
        showDetail();

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
                    if(setTextTodo.equals("")){//todoが空欄だった時の処理
                        emptyTaskDialogFragment.show(getFragmentManager(), "empty");
                    } else if (setTextBox == null){//分類がされていない時の処理
                        setTextBox = "未分類";
                        boxId = 2;
                        dbAdapter.writeDB(setTextTodo, setTextBox, setTextDate, setTextTime, setTextMemo, boxId);
                        finish();
                    } else {//通常処理
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

        //---------------------------
        //日時が設定されたときの処理
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String strHourOfDay = String.format("%02d", hourOfDay);
                String strMinute = String.format("%02d", minute);
                editTime.setText(strHourOfDay + "時" + strMinute + "分");
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

        //Date time = calendar.getTime();
        //DateFormat formatterDate = new SimpleDateFormat("yyyy年M月d日H時m分");
        //String nowTime = formatterDate.format(time);

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

        // チェックボックスがクリックされた時に呼び出されるコールバックリスナーを登録します
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // チェックボックスがクリックされた時に呼び出されます
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        //---------------------------
    }

    public void onCheckboxClicked(View view) {
        final boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.reminder_button:
                if (checked) {
                    // チェックボックス1がチェックされる
                    //設定された時間
                    setTextDate = editDate.getText().toString();//"yyyy年MM月dd日"
                    setTextTime = editTime.getText().toString();//"HH時mm分"
                    String str1 = setTextDate + setTextTime;
                    String str2 = str1.replace("年", "/");
                    String str3 = str2.replace("月", "/");
                    String str4 = str3.replace("日", " ");
                    String str5 = str4.replace("時", ":");
                    String str6 = str5.replace("分", "");//"yyyy/MM/dd HH:mm"

                    Date dDate = null;
                    DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");// 変換
                    try {
                        dDate = df2.parse(str6);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //現在の時間
                    Date now = new Date(System.currentTimeMillis());
                    long triggerAtTime = getTriggerAtTime(now,dDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, (int) triggerAtTime);
                    scheduleNotification("10秒後に届く通知です", calendar);
                    Toast.makeText(TodoEdit.this,triggerAtTime + "後に設定",Toast.LENGTH_SHORT).show();
                } else {
                    // チェックボックス1のチェックが外される
                    Toast.makeText(TodoEdit.this,
                            "チェックがはずされました",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void scheduleNotification(String content, Calendar calendar){
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_CONTENT, content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public long getTriggerAtTime(Date nowTime, Date setTime ) {
        long triggerAtTime = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // 日付を作成します。
        try {
            nowTime = sdf.parse("2013/08/01 13:00:00");
            setTime = sdf.parse("2013/08/01 15:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 日付をlong値に変換します。
        long dateTimeTo = setTime.getTime();
        long dateTimeFrom = nowTime.getTime();

        // 差分の時間を算出します。
        long dayDiff = ( dateTimeTo - dateTimeFrom  ) / (1000 * 60 * 60 );

        return dayDiff;
    }

    public String getNowDate(){
        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatterDate = new SimpleDateFormat("yyyy年MM月dd日");
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
        editDate.setText(getNowDate());
        editTime.setText(getNowTime());
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
















