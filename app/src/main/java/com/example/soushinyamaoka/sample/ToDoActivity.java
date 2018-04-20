package com.example.soushinyamaoka.sample;

import android.app.DialogFragment;
import android.content.Context;
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

import java.util.ArrayList;
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

    int databaseId = 0;
    int spinnerPosition = 0;

    int boxDataBaseId;
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
        boxDataBaseId = intent.getIntExtra( "boxDataBaseId",-1);
        spinnerPosition = intent.getIntExtra("spinnerPosition",0);
        boxId = intent.getIntExtra("boxName",0);
        boxDBAdapter.openBoxDB();
        if (boxId == 0){
            boxName = "全て";
        } else {
            boxName =  boxDBAdapter.changeToBoxName(boxId);
        }

        setToolbar(boxName);

        //リストの生成
        if (boxId == 0){//全ての場合
            showList(this);
        } else if (boxId == 1){//今日の場合
            showDividedTodoList(this,boxId);
        } else {//その他すべて
            showDividedTodoList(this,boxId);
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
                        //editBox = "今日";
                        dbAdapter.openDB();
                        dbAdapter.writeDB(editTodo, boxName, editDate, editTime, editMemo, boxId);
                        //dbAdapter.readDB();
                        showDividedTodoList(getApplicationContext(),boxId);

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
                                           int position, long listviewId) {
                deleteList(getApplicationContext(), listviewId);
                //deleteArchive(getApplicationContext(),listviewId);
                showDividedTodoList(getApplicationContext(),boxId);
                Toast.makeText(ToDoActivity.this, "完了済みにしました", Toast.LENGTH_LONG).show();

                return false;
            }
        });

        listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(ToDoActivity.this, TodoDetail.class);
                if (boxId == 0){//全てを選んだ場合
                    int todoId = dbAdapter.getTodoId((int) id);//todoのidを取得
                    boxId = dbAdapter.getBoxId((int) id);//todoのboxidを取得
                    databaseId = dbAdapter.changeDividedId(id, boxId);//todoのidを取得
                    spinnerPosition = boxDBAdapter.getSpinnerPosition(boxId);
                    intent.putExtra( "databaseId", databaseId );
                    intent.putExtra("spinnerPosition", spinnerPosition-2);
                    intent.putExtra("boxName", boxId);//todoのboxidを取得
                } else {
                    // intentへ添え字付で値を保持させる
                    databaseId = dbAdapter.changeDividedId(id, boxId);//todoのidを取得
                    intent.putExtra( "databaseId", databaseId );
                    intent.putExtra("spinnerPosition", spinnerPosition);
                    intent.putExtra("boxName", boxId);//todoのboxidを取得
                }
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
                boxName = data.getStringExtra("boxName");
                if (boxName.equals("全て")){
                    showList(this);
                } else {
                    showDividedTodoList(this, boxId);
                }
            }
        }
    }

    public void showList(Context context){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(context, R.layout.text_todo_list, (List<String>) lvAdapter);
        listViewTodo.setAdapter(adapter);
    }

    public void showDividedTodoList(Context context, int boxId){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDividedBoxDB(boxId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(context, R.layout.text_todo_list, (List<String>) lvAdapter);
        listViewTodo.setAdapter(adapter);
    }

    public String getBoxName(int boxId){
        String setName;
        setName = boxDBAdapter.changeToBoxName(boxId);

        return setName;
    }

    public void deleteList(Context context, long listviewId){
        dbAdapter = new DBAdapter(context);
        try {
            dbAdapter.openDB();
            databaseId = dbAdapter.changeDividedId(listviewId,boxId);//List上のIDをDB上のIDに変換

            String[] getTodo = dbAdapter.getTodo(databaseId);
            //String[] getBox = dbAdapter.getBox(databaseId);
            String[] getDate = dbAdapter.getDate(databaseId);
            String[] getTime = dbAdapter.getTime(databaseId);
            String[] getMemo = dbAdapter.getMemo(databaseId);

            String setTodo = getTodo[0];
            String setBox = "完了";
            String setDate = getDate[0];
            String setTime = getTime[0];
            String setMemo = getMemo[0];
            int setBoxId = 0;

            dbAdapter.openDB();
            dbAdapter.updateDB(databaseId, setTodo, setBox, setDate, setTime, setMemo,setBoxId);
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