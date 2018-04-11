package com.example.soushinyamaoka.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewBox;
    private Button allTodoButton;
    DBAdapter dbAdapter;
    DBHelper db;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_box);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        setTitle( "受信箱" );

        listViewBox = findViewById(R.id.list_view_box);
        allTodoButton = findViewById(R.id.all_todo_button);
        dbAdapter = new DBAdapter(this);
        db = new DBHelper(this);

        showBox(this);

        allTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                // intentへ添え字付で値を保持させる
                startActivity(intent);

                //int requestCode = 123;
                //startActivityForResult(intent, requestCode);
            }
        });

        listViewBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                // intentへ添え字付で値を保持させる
                ListView list = (ListView)adapterView;
                String boxName = (String)list.getItemAtPosition(position);
                intent.putExtra( "boxname", boxName );
                //startActivity(intent);

                int requestCode = 123;
                startActivityForResult(intent, requestCode);
            }
        });
    }

    public void showBox(Context context){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDBBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(context, R.layout.text_box_list, (List<String>) lvAdapter);
        listViewBox.setAdapter(adapter);
    }

}
