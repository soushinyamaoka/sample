package com.example.soushinyamaoka.sample;

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
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;
    DBHelper db;
    CustomAdapter customAdapter;
    int boxDataBaseId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_box);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_box);
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
                startActivity(intent);
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
                startActivity(intent);
            }
        });

        todayView.setClickable(true);
        todayView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dateの値に合わせて設定したい
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                int boxId = 2;
                intent.putExtra("boxName", boxId);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
                startActivity(intent);
            }
        });

        listViewBox.setClickable(true);
        listViewBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (view.getId() == R.id.edit){
                    startEditBoxClass(position);
                } else if (view.getId() == R.id.delete){
                    ListView list = (ListView) adapterView;
                    int boxlistViewId = (int) list.getItemAtPosition(position);
                    deleteBoxList(boxlistViewId);
                    showBox();
                    Toast.makeText(MainActivity.this, "削除しました。", Toast.LENGTH_SHORT).show();
                } else if (view.getId() == R.id.text){

                    startTodoActivityClass((ListView)adapterView,position);
                }
            }
        });

        editBoxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String setBoxName = editBoxView.getText().toString();

                boxDBAdapter.openBoxDB();
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
                startActivity(intent);
            }
        });
    }

    public void showBox() {
        boxDBAdapter.openBoxDB();
        customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_item, boxDBAdapter.readBoxDB());
        listViewBox.setAdapter(customAdapter);
    }

    public void deleteBoxList(long boxListViewId){
        try {
            boxDBAdapter.openBoxDB();
            boxDataBaseId = boxDBAdapter.changeBoxId(boxListViewId);//List上のIDをDB上のIDに変換

            String setBox = "完了";
            int setBoxId = 0;

            //カテゴリ内にあるタスクのカテゴリを完了に変更
            boxDBAdapter.openBoxDB();
            boxDBAdapter.updateBoxDB(boxDataBaseId,setBox);
            //カテゴリ名を削除
            boxDBAdapter.openBoxDB();
            boxDBAdapter.deleteBoxDB(boxDataBaseId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEditBoxClass(int position){
        boxDBAdapter.openBoxDB();
        customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_item, boxDBAdapter.readBoxDB());
        //editIntentにEditBox.classを入れる
        Intent editIntent = new Intent(MainActivity.this, EditBox.class);
        //リスト上のIDをDB上のIDに変換し、Intentに格納
        int BoxListViewId = (int) customAdapter.getItemId(position);
        boxDataBaseId = boxDBAdapter.changeBoxId(BoxListViewId);
        editIntent.putExtra("boxDataBaseId", boxDataBaseId);
        //選択されたカテゴリ名を取得し、Intentに格納
        String[] setBoxName = boxDBAdapter.getBoxName(boxDataBaseId);
        editIntent.putExtra("boxName", setBoxName);
        int requestCode = 123;
        startActivityForResult(editIntent, requestCode);
    }

    public void startTodoActivityClass(ListView adapterView,int position){
        // 遷移先のactivityを指定してintentを作成
        Intent todoActiveIntent = new Intent(MainActivity.this, ToDoActivity.class);
        ListView list = (ListView) adapterView;

        int boxListViewId = (int)list.getItemAtPosition(position);

        boxDBAdapter.openBoxDB();
        boxDataBaseId = boxDBAdapter.changeBoxId(boxListViewId);

        String[] setBoxName = boxDBAdapter.getBoxName(boxDataBaseId);

        todoActiveIntent.putExtra("boxDataBaseId", boxDataBaseId);
        //todoActiveIntent.putExtra("boxName", setBoxName[0]);
        todoActiveIntent.putExtra("boxName", boxDataBaseId);
        todoActiveIntent.putExtra("spinnerPosition", boxListViewId);

        int requestCode = 123;
        startActivityForResult(todoActiveIntent, requestCode);
    }

    // SubActivity からの返しの結果を受け取る
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK && requestCode == 123 &&
                null != intent) {
            String changedBoxName = intent.getStringExtra("changedBoxName");

        }
    }
}
