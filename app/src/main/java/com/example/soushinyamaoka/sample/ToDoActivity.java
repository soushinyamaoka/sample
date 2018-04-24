package com.example.soushinyamaoka.sample;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by syama on 2018/02/03.
 */

public class ToDoActivity extends AppCompatActivity {

    String editTodo;
    String editBox;
    String editDate;
    String editTime;
    String editMemo;
    int boxId;

    int todoId = 0;
    int spinnerPosition = 0;

    String delete = "削除";
    String boxName;
    private static final int TODO_DETAIL = 1001;

    private EditText editText;
    private Button editButton;
    public ListView listViewTodo;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;
    DialogFragment emptyTaskDialogFragment;
    DBHelper db;
    ArrayAdapter<String> adapter;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_todo);

        editText = findViewById(R.id.edit_Text);
        editButton = findViewById(R.id.edit_Button);
        listViewTodo = findViewById(R.id.list_view_todo);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        emptyTaskDialogFragment = new EmptyTaskDialogFragment();
        db = new DBHelper(this);

        Intent intent = getIntent();
        spinnerPosition = intent.getIntExtra("spinnerPosition",0);//detail表示の際に使用
        boxId = intent.getIntExtra("boxName",0);
        boxDBAdapter.openBoxDB();
        if (boxId == 0){
            boxName = "全て";
        } else if (boxId == -1){
            boxName = "今日";
        } else {
            boxName =  boxDBAdapter.changeToBoxName(boxId);
        }

        setToolbar(boxName);

        //リストの生成
        if (boxId == 0){//全ての場合
            showList(boxId);
        } else if (boxId == -1){//今日の場合
            showList(boxId);
        } else {//その他すべて
            showDividedTodoList(boxId);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editTodo = editText.getText().toString();//書き込まれた内容(getText)をstrに格納
                    if(editTodo.equals("")){
                        emptyTaskDialogFragment.show(getFragmentManager(), "empty");
                    }
                    else {
                        dbAdapter.openDB();
                        dbAdapter.writeDB(editTodo, boxName, getNowDate(), getNowTime(), editMemo, boxId);
                        //dbAdapter.readDB();
                        showDividedTodoList(boxId);

                        editText.getEditableText().clear();
                    }
                } catch (Exception e) {//エラーの場合
                    Log.e(TAG, "SQLExcepption:" + e.toString());
                }
            }
        });

        listViewTodo.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent,View view,
                                           int position, long id) {
                todoId = dbAdapter.getTodoId(getNowDate(),boxId,id);
                deleteList(todoId);
                //deleteArchive(getApplicationContext(),listviewId);
                if (boxId == 0){//全てが選択されているとき
                    showList(boxId);
                } else {
                    showDividedTodoList(boxId);
                }
                Toast.makeText(ToDoActivity.this, "完了済みにしました", Toast.LENGTH_LONG).show();

                return false;
            }
        });

        listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(ToDoActivity.this, TodoDetail.class);
                int todoId = dbAdapter.getTodoId(getNowDate(),boxId,id);//todoのidを取得
                boxId = dbAdapter.getBoxIdData(todoId);//todoのboxidを取得
                spinnerPosition = boxDBAdapter.getSpinnerPosition(boxId);

                intent.putExtra( "todoId", todoId);
                intent.putExtra("spinnerPosition", spinnerPosition);
                intent.putExtra("boxName", boxId);//todoのboxidを取得
                startActivityForResult(intent, TODO_DETAIL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(requestCode, resultCode, data);
        // startActivityForResult()の際に指定した識別コードとの比較
        // requestCodeがサブ画面か確認する
        if(requestCode == TODO_DETAIL) {
            // resultCodeがOKか確認する
            if (resultCode == RESULT_OK) {
                // 結果を取得して, 表示する.
                boxId = data.getIntExtra("boxName",0);
                if (boxId == 0){
                    showList(boxId);
                } else {
                    showDividedTodoList(boxId);
                }
            }
        }
    }

    public String getNowDate(){
        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatterDate = new SimpleDateFormat("yyyy年MM月dd日");

        // フォーマット
        String nowDate = formatterDate.format(now);
        return nowDate;
    }

    public String getNowTime(){
        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatterTime = new SimpleDateFormat("HH時mm分");

        // フォーマット
        String nowTime = formatterTime.format(now);
        return nowTime;
    }

    public void showList(int boxId) {
        ArrayList<String> lvAdapter = new ArrayList<>();
        if (boxId == -1){
            //当日日付と比較し、当日以前のdateのtodoを取得
            dbAdapter.openDB();
            lvAdapter = dbAdapter.readTodayDB(getNowDate());
        } else {
            dbAdapter.openDB();
            lvAdapter = dbAdapter.readDB();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(this, R.layout.text_todo_list, (List<String>) lvAdapter);
        listViewTodo.setAdapter(adapter);
    }

    public void showDividedTodoList(int boxId){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        lvAdapter = dbAdapter.readDividedBoxDB(boxId);
        // Adapterの作成
        adapter = new ArrayAdapter<String>(this, R.layout.text_todo_list, (List<String>) lvAdapter);
        listViewTodo.setAdapter(adapter);
    }

    public void deleteList(int todoId){
        dbAdapter.openDB();
        try {
            String[] getTodo = dbAdapter.getTodoData(todoId);
            String[] getDate = dbAdapter.getDateData(todoId);
            String[] getTime = dbAdapter.getTimeData(todoId);
            String[] getMemo = dbAdapter.getMemoData(todoId);
            String boxName = "完了済み";
            int boxId = 1;

            dbAdapter.openDB();
            dbAdapter.updateDB(todoId, getTodo[0], boxName, getDate[0], getTime[0], getMemo[0],boxId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == android.R.id.home){
            Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
            // intentへ添え字付で値を保持させる
            intent.putExtra( "todoId", id );
            startActivity(intent);

            //int requestCode = 1234;
            //startActivityForResult(intent, requestCode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(String boxName){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_todo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(boxName);
    }
}