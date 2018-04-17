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

public class ToDoComplete extends AppCompatActivity{

    String editTodo;
    String editBox;
    String editDate;
    String editMemo;

    int databaseId = 0;
    int spinnerPosition = 0;
    String today = "今日";
    String delete = "削除";
    private String allList = "全て";
    private String archiveList = "アーカイブ";
    String boxName;
    int boxDataBaseId;
    private static final int TODO_DETAIL = 1001;
    private static final int TODO_DELETE = 1002;

    private EditText editText;
    private Button editButton;
    private Button deleteButton;
    public ListView listViewTodo;
    DBAdapter dbAdapter;
    DialogFragment emptyTaskDialogFragment;
    DBHelper db;
    ArrayAdapter<String> adapter;
    DeleteDialogFragment deleteDialogFragment;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.complete_todo);

        editText = findViewById(R.id.edit_Text);
        editButton = findViewById(R.id.edit_Button);
        deleteButton = findViewById(R.id.delete_Button);
        listViewTodo = findViewById(R.id.list_view_todo);
        dbAdapter = new DBAdapter(this);
        emptyTaskDialogFragment = new EmptyTaskDialogFragment();
        db = new DBHelper(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        //Mainから渡された「選択されたカテゴリのDB上のID」を取得
        boxDataBaseId = intent.getIntExtra( "boxDataBaseId",-1);
        //Mainから渡された「選択されたカテゴリ名」を取得
        boxName = intent.getStringExtra("boxName");
        //Mainから渡されたboxのposition(spinnerPosition)を取得
        spinnerPosition = intent.getIntExtra("spinnerPosition",0);

        setToolbar(boxName);

        //リストの生成

        showDividedTodoList(this,boxName);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteComplete();
                Intent intent = new Intent(ToDoComplete.this, DeleteDialogFragment.class);
                // intentへ添え字付で値を保持させる
                startActivityForResult(intent, TODO_DELETE);
            }
        });

        listViewTodo.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,View view,
                                                   int position, long listviewId) {
                        databaseId = dbAdapter.changeDividedId(listviewId, boxName);
                        dbAdapter.backDB(databaseId,
                                        "今日");
                        //deleteArchive(getApplicationContext(),listviewId);
                        showDividedTodoList(getApplicationContext(),boxName);
                        Toast.makeText(ToDoComplete.this, "1件戻しました", Toast.LENGTH_LONG).show();

                        return false;
                    }
                });

        listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(ToDoComplete.this, TodoDetail.class);
                // intentへ添え字付で値を保持させる
                databaseId = dbAdapter.changeDividedId(id, boxName);
                intent.putExtra( "databaseId", databaseId );
                intent.putExtra("spinnerPosition", spinnerPosition);
                intent.putExtra("boxName", boxName);

                startActivityForResult(intent, TODO_DETAIL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(requestCode, resultCode, data);
        // startActivityForResult()の際に指定した識別コードとの比較
        // requestCodeがサブ画面か確認する
        if(requestCode == TODO_DELETE) {
            // resultCodeがOKか確認する
            if (resultCode == RESULT_OK) {
                deleteComplete();
            } else {
                boxName = data.getStringExtra("boxName");
                showDividedTodoList(this, boxName);
            }
        }
    }

    public void showDividedTodoList(Context context, String boxName){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDividedBoxDB(boxName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(context, R.layout.text_todo_list, (List<String>) lvAdapter);
        listViewTodo.setAdapter(adapter);
    }

    public void deleteComplete(){
        //dbAdapter = new DBAdapter(context);
        try {
            dbAdapter.openDB();
            dbAdapter.deleteDB("完了");//DB上の値をDB上のidで削除。
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDialog(){
        deleteDialogFragment = new DeleteDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("deleteId", listviewId);
        deleteDialogFragment.setArguments(bundle);
        deleteDialogFragment.show(getFragmentManager(), "delete");
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
