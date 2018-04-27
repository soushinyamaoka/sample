package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView listViewBox;
    private TextView allTodoView;
    private TextView completeView;
    private TextView todayView;
    private EditText editBoxView;
    private Button editBoxButton;
    private Toolbar toolbar;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;
    DBHelper db;
    CustomAdapter customAdapter;
    int boxDataBaseId;
    int boxId;
    int spinnerPosition;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_box);
        toolbar = (Toolbar) findViewById(R.id.toolbar_box);
        setSupportActionBar(toolbar);
        setTitle("受信箱");

        listViewBox = findViewById(R.id.list_view_box);
        allTodoView = findViewById(R.id.all_todo_view);
        completeView = findViewById(R.id.complete_view);
        todayView = findViewById(R.id.today_todo_view);
        editBoxView = findViewById(R.id.edit_box_view);
        editBoxButton = findViewById(R.id.edit_box_button);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        db = new DBHelper(this);

        showBox();

        allTodoView.setClickable(true);
        allTodoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                int boxId = 0;
                intent.putExtra("boxName",boxId);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
                //startActivity(intent);
            }
        });

        completeView.setClickable(true);
        completeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDoComplete.class);
                int boxId = 1;
                intent.putExtra("boxName", boxId);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
                //startActivity(intent);
            }
        });

        todayView.setClickable(true);
        todayView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dateの値に合わせて設定したい
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                int boxId = -1;
                intent.putExtra("boxName", boxId);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
                //startActivity(intent);
            }
        });

        listViewBox.setClickable(true);
        listViewBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                spinnerPosition = position + 1;//スピナーには未分類が表示されるため、プラス１をする
                boxId = boxDBAdapter.getBoxId(spinnerPosition);//Mainの一覧には未分類が非表示のため、あらかじめプラス１した値を渡す
                if (view.getId() == R.id.edit){
                    startEditBoxClass(boxId);
                } else if (view.getId() == R.id.delete){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("確認");
                    alertDialog.setMessage("カテゴリを削除しますか？"+ "\n" +"(タスクは「完了済み」になります)");
                    alertDialog.setPositiveButton("削除する", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteBoxList(boxId);
                            showBox();
                            Toast.makeText(MainActivity.this, "削除しました。", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showBox();
                        }
                    });
                    alertDialog.create().show();

                } else if (view.getId() == R.id.text){
                    startTodoActivityClass(spinnerPosition,boxId);
                }
            }
        });

        editBoxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String setBoxName = editBoxView.getText().toString();

                boxDBAdapter.writeBoxDB(setBoxName);

                showBox();
                editBoxView.getEditableText().clear();
            }
        });

        FloatingActionButton fabEditTodo = (FloatingActionButton) findViewById(R.id.fab_edit_todo);
        fabEditTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodoEdit.class);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
            }
        });
    }

    public void showBox() {
        customAdapter = new CustomAdapter(this, R.layout.row_item, boxDBAdapter.readBoxDB());
        listViewBox.setAdapter(customAdapter);
    }

    public void deleteBoxList(int boxId){
        try {
            String setBox = "完了済み";
            //boxId = 1;//完了
            //カテゴリ内にあるタスクのカテゴリを完了に変更
            //boxDBAdapter.updateBoxDB(boxId,setBox);
            //カテゴリ名を削除
            boxDBAdapter.deleteBoxDB(boxId);

            //カテゴリ内にあるタスクのカテゴリを完了に変更
            //-------------------------
            Integer[] integerArray = dbAdapter.getTodoIdAsArray(null,boxId);
            int[] todoIdArray = new int[integerArray.length];
            String[] todoArray = new String[integerArray.length];
            String[] boxArray = new String[integerArray.length];
            String[] dateArray = new String[integerArray.length];
            String[] timeArray = new String[integerArray.length];
            String[] memoArray = new String[integerArray.length];
            for (int i=0;i<integerArray.length;i++){
                todoIdArray[i] = integerArray[i];
                todoArray[i] = dbAdapter.getTodoData(todoIdArray[i]);
                dateArray[i] = dbAdapter.getDateData(todoIdArray[i]);
                timeArray[i] = dbAdapter.getTimeData(todoIdArray[i]);
                memoArray[i] = dbAdapter.getMemoData(todoIdArray[i]);
                dbAdapter.updateDB(todoIdArray[i],
                        todoArray[i],
                        "完了済み",
                        dateArray[i],
                        timeArray[i],
                        memoArray[i],
                        1);
            }
            //-------------------------
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEditBoxClass(int boxId){
        customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_item, boxDBAdapter.readBoxDB());
        //editIntentにEditBox.classを入れる
        Intent editIntent = new Intent(MainActivity.this, EditBox.class);
        editIntent.putExtra("boxName", boxId);
        int requestCode = 123;
        startActivityForResult(editIntent, requestCode);
    }

    public void startTodoActivityClass(int spinnerPosition, int boxId){
        // 遷移先のactivityを指定してintentを作成
        Intent todoActiveIntent = new Intent(MainActivity.this, ToDoActivity.class);

        todoActiveIntent.putExtra("boxName", boxId);
        todoActiveIntent.putExtra("spinnerPosition", spinnerPosition);//Detail表示の際に使用

        int requestCode = 123;
        startActivityForResult(todoActiveIntent, requestCode);
    }

    // SubActivity からの返しの結果を受け取る
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK && requestCode == 123 &&
                null != intent) {
            //String changedBoxName = intent.getStringExtra("changedBoxName");
            showBox();
        }
    }
}
