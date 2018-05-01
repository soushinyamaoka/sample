package todo.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoComplete extends AppCompatActivity{

    int todoId = 0;
    int spinnerPosition = 0;
    String boxName;
    int boxId;
    private static final int TODO_DETAIL = 1001;
    private static final int TODO_DELETE = 1002;
    private Button deleteButton;
    public ListView listViewTodo;
    DBAdapter dbAdapter;
    DBHelper db;
    BoxDBAdapter boxDBAdapter;
    DialogFragment emptyTaskDialogFragment;
    ArrayAdapter<String> adapter;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.complete_todo);

        deleteButton = findViewById(R.id.delete_Button);
        listViewTodo = findViewById(R.id.list_view_complete);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        emptyTaskDialogFragment = new EmptyTaskDialogFragment();
        db = new DBHelper(this);

        Intent intent = getIntent();
        boxId = intent.getIntExtra("boxName",0);
        boxName = boxDBAdapter.getBoxName(boxId);

        setToolbar(boxName);

        //リストの生成

        showDividedTodoList(boxId);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 確認ダイアログの作成
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ToDoComplete.this);
                alertDialog.setTitle("確認");
                alertDialog.setMessage("完全に削除しますか？");
                alertDialog.setPositiveButton("削除する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteComplete();
                        showDividedTodoList(boxId);
                    }
                });
                alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDividedTodoList(boxId);
                    }
                });
                alertDialog.create().show();
            }
        });

        listViewTodo.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,View view,
                                                   int position, long listviewId) {
                        todoId = dbAdapter.getTodoId(null,boxId,listviewId);
                        dbAdapter.playBackDB(todoId, "未分類",2);//未分類id=2
                        showDividedTodoList(boxId);
                        Toast.makeText(ToDoComplete.this, "完了済みを解除しました", Toast.LENGTH_LONG).show();

                        return true;
                    }
                });

        listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(ToDoComplete.this, TodoDetail.class);
                int todoId = dbAdapter.getTodoId(getNowDate(),boxId,id);
                intent.putExtra( "todoId", todoId);
                intent.putExtra("spinnerPosition", spinnerPosition);
                intent.putExtra("boxName", boxId);//これは「完了済み」なのでid.1のはず

                startActivityForResult(intent, TODO_DETAIL);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TODO_DELETE) {
            // resultCodeがOKか確認する
            if (resultCode == RESULT_OK) {
                deleteComplete();
            } else {
                boxName = data.getStringExtra("boxName");
                showDividedTodoList(boxId);
            }
        }
    }

    public void showDividedTodoList(int boxId){
        ArrayList<String> lvAdapter = new ArrayList<>();
        try {
            lvAdapter = dbAdapter.readDividedBoxDB(boxId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(this, R.layout.text_complete_list, (List<String>) lvAdapter);
        listViewTodo.setAdapter(adapter);
    }

    public void deleteComplete(){
        try {
            dbAdapter.deleteDB(boxId);//DB上の値をDB上のidで削除。
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
            Intent intent = new Intent(ToDoComplete.this, MainActivity.class);
            // intentへ添え字付で値を保持させる
            intent.putExtra( "todoId", id );
            startActivity(intent);

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
