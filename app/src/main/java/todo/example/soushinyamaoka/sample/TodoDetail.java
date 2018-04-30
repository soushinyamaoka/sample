package todo.example.soushinyamaoka.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.soushinyamaoka.sample.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private Button timeClearButton;
    private Button dateClearButton;
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
        dbAdapter = new DBAdapter(this);

        Intent intent = getIntent();
        todoId = intent.getIntExtra( "todoId", 0);
        spinnerPosition = intent.getIntExtra("spinnerPosition", -1);
        boxId = intent.getIntExtra("boxName",0);//Main画面で押されたカテゴリから取得
        if (boxId == 0) {//全てが選ばれていた場合
            allTodo = true;
        } else if (boxId == -1) {//今日が選ばれていた場合
            todayTodo = true;
        }

        if (boxId == 1){//完了済みのタスクの場合
            setContentView(R.layout.complete_todo_detail);
            textTodo = findViewById(R.id.new_edit_Todo);
            textDate = findViewById(R.id.new_edit_Date);
            textTime = findViewById(R.id.new_edit_Time);
            textMemo = findViewById(R.id.new_edit_Memo);
            textBox = findViewById(R.id.new_edit_box);
        } else {//完了済み以外の時
            boxName = boxDBAdapter.getBoxName(dbAdapter.getBoxIdData(todoId));//todo内のboxIdを取得し渡す。
            setContentView(R.layout.activity_detail_todo);
            editTodo = findViewById(R.id.new_edit_Todo);
            editDate = findViewById(R.id.new_edit_Date);
            editTime = findViewById(R.id.new_edit_Time);
            editMemo = findViewById(R.id.new_edit_Memo);
            spinner_box = (Spinner) findViewById(R.id.new_box_spinner);
            timeClearButton = findViewById(R.id.time_clear_button);
            dateClearButton = findViewById(R.id.date_clear_button);
        }

        db = new DBHelper(this);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        //todoの詳細を表示
        //-------------------------
        showDetail(todoId,boxId);
        //-------------------------

        TimePickerDialog.OnTimeSetListener onTimeSetListener
                = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editTime.setText(hourOfDay + "時" + minute + "分");
                setReminder();
            }
        };

        DatePickerDialog.OnDateSetListener onDateSetListener
                = new DatePickerDialog.OnDateSetListener() {
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
        if (boxId != 1){
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

            dateClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                // チェックボックスがクリックされた時に呼び出されます
                public void onClick(View v) {
                    editDate.setText("");
                }
            });

            timeClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                // チェックボックスがクリックされた時に呼び出されます
                public void onClick(View v) {
                    editTime.setText("");
                    cancelReminder();
                    Toast.makeText(TodoDetail.this,
                            "リマインダーを解除しました",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setReminder(){
        //設定された時間
        String strDate = editDate.getText().toString();//"yyyy年MM月dd日"
        String strTime = editTime.getText().toString();//"HH時mm分"
        String str1 = strDate + strTime;
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
        scheduleNotification(editTodo.getText().toString(), calendar);
        Toast.makeText(TodoDetail.this,
                "リマインダーを設定しました",
                Toast.LENGTH_SHORT).show();
    }

    private void cancelReminder(){
        // アラームの削除
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoId, intent, 0);

        //pendingIntent.cancel();
        //alarmManager.cancel(pendingIntent);
        // ActivityAlarmReceiverを呼び出すインテントを作成
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(
                this, todoId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // AlarmManager取得
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // PendingIntentをキャンセル
        am.cancel(sender);
    }

    private void scheduleNotification(String content, Calendar calendar){
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_CONTENT, content);
        notificationIntent.putExtra("requestCode",todoId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                todoId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public long getTriggerAtTime(Date nowTime, Date setTime ) {
        // 日付をlong値に変換します。
        long dateTimeTo = setTime.getTime();
        long dateTimeFrom = nowTime.getTime();

        // 差分の時間を算出します。
        long dayDiff = ( dateTimeTo - dateTimeFrom  ) / (1000);
        return dayDiff;
    }

    @Override
    public void onBackPressed(){
        if ((editDate.getText().toString().equals("")) && (!editTime.getText().toString().equals(""))){
            Toast.makeText(TodoDetail.this,
                    "日付を設定してください",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (boxId == 1){//完了済みのタスクの場合
                finish();
            } else {
                int updateBoxId = boxDBAdapter.getBoxId(spinner_box.getSelectedItemPosition());
                dbAdapter.updateDB(todoId,
                        editTodo.getText().toString(),
                        (String)spinner_box.getSelectedItem(),
                        editDate.getText().toString(),
                        editTime.getText().toString(),
                        editMemo.getText().toString(),
                        updateBoxId
                );
                Intent data = new Intent(TodoDetail.this,ToDoActivity.class);
                data.putExtra("boxName",boxId);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public void showDetail(int todoId, int boxId)  {
        String setTodo;
        String setDate;
        String setTime;
        String setMemo;

        setTodo = dbAdapter.getTodoData(todoId);
        setDate = dbAdapter.getDateData(todoId);
        setTime = dbAdapter.getTimeData(todoId);
        setMemo = dbAdapter.getMemoData(todoId);

        if (boxId == 1){//完了済みのタスクの場合
            textTodo.setText(setTodo);
            textDate.setText(setDate);
            textTime.setText(setTime);
            textMemo.setText(setMemo);
            textBox.setText(boxName);
        } else {
            editTodo.setText(setTodo);
            editDate.setText(setDate);
            editTime.setText(setTime);
            editMemo.setText(setMemo);
            setSpinner();
        }
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

    public void setSpinner(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        lvAdapter = boxDBAdapter.readBoxSpinnerDB();//「完了済み」のみ抽出しないList
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.text_box_list, lvAdapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_box.setAdapter(adapter);
        spinner_box.setSelection(spinnerPosition);//遷移元のアクティビティから渡された値。すでに未分類の分は加算済み
    }
}
















