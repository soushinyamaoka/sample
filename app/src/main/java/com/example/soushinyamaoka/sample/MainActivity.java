package com.example.soushinyamaoka.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView listViewBox;
    private TextView allTodoView;
    private TextView completeView;
    private EditText editBoxView;
    private Button editBoxButton;
    DBAdapter dbAdapter;
    BoxDBAdapter boxDBAdapter;
    DBHelper db;
    BoxDBHelper boxdb;
    ArrayAdapter<String> adapter;
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
        editBoxView = findViewById(R.id.edit_box_view);
        editBoxButton = findViewById(R.id.edit_box_button);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);
        db = new DBHelper(this);
        boxdb = new BoxDBHelper(this);

        showBox(this);

        allTodoView.setClickable(true);
        allTodoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                String boxName = "全て";
                intent.putExtra("boxname", boxName);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
                startActivity(intent);
            }
        });

        completeView.setClickable(true);
        completeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                String boxName = "完了";
                intent.putExtra("boxname", boxName);
                int requestCode = 123;
                startActivityForResult(intent, requestCode);
                startActivity(intent);
            }
        });
        listViewBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long boxListViewId) {

                if (view.getId() == R.id.edit){
                    startEditBoxClass(position);
                } else if (view.getId() == R.id.delete){
                    deleteBoxList(boxListViewId);
                    Toast.makeText(MainActivity.this, "削除しました。", Toast.LENGTH_SHORT).show();
                } else {
                    startTodoActivityClass((ListView)adapterView,position);
                }
            }
        });

        editBoxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String setBoxName = editBoxView.getText().toString();
                boxDBAdapter.openBoxDB();
                boxDBAdapter.writeBoxDB(setBoxName);
                showBox(getApplicationContext());
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

    public void showBox(Context context) {
        //ArrayList<String> lvAdapter = new ArrayList<>();
        String[] lvAdapter = new String[0];
        boxDBAdapter.openBoxDB();
        try {
            lvAdapter = boxDBAdapter.readBoxDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        //adapter = new ArrayAdapter<String>(context, R.layout.text_box_list, (List<String>) lvAdapter);
        //listViewBox.setAdapter(adapter);
        customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_item, boxDBAdapter.readBoxDB());
        listViewBox.setAdapter(customAdapter);
    }

    public void deleteBoxList(long boxListViewId){
        try {
            boxDBAdapter.openBoxDB();
            boxDataBaseId = boxDBAdapter.changeBoxId(boxListViewId);//List上のIDをDB上のIDに変換

            String setBox = "完了";

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
        // Adapterの作成
        //adapter = new ArrayAdapter<String>(context, R.layout.text_box_list, (List<String>) lvAdapter);
        //listViewBox.setAdapter(adapter);
        customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_item, boxDBAdapter.readBoxDB());
        int BoxListViewId = (int) customAdapter.getItemId(position);

        Intent editIntent = new Intent(MainActivity.this, EditBox.class);
        boxDataBaseId = boxDBAdapter.changeBoxId(BoxListViewId);
        editIntent.putExtra("boxDataBaseId", boxDataBaseId);
        int requestCode = 123;
        startActivityForResult(editIntent, requestCode);
    }

    public void startTodoActivityClass(ListView adapterView,int position){
        // 遷移先のactivityを指定してintentを作成
        Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
        // intentへ添え字付で値を保持させる
        ListView list = (ListView) adapterView;
        String boxName = (String) list.getItemAtPosition(position);
        intent.putExtra("boxname", boxName);
        int requestCode = 123;
        startActivityForResult(intent, requestCode);
    }
}
