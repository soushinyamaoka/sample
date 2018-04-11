package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TodoList extends Activity {

    public ListView listView;
    DBAdapter dbAdapter;
    DBHelper db;
    ArrayAdapter<String> adapter;
    ArrayList<String> lvAdapter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.todo_list_view);
        listView = findViewById(R.id.list_view);
        dbAdapter = new DBAdapter(this);
        db = new DBHelper(this);

        showList(this);
    }

    public void showList(Context context){
        lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        adapter = new ArrayAdapter<String>(context, R.layout.text_todo_list, (List<String>) lvAdapter);
        listView.setAdapter(adapter);
    }
}
