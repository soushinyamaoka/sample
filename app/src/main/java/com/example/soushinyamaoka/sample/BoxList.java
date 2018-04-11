package com.example.soushinyamaoka.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BoxList extends AppCompatActivity {

    private ListView listViewBox;
    DBAdapter dbAdapter;
    DBHelper db;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_box);

        listViewBox = findViewById(R.id.list_view_box);
        dbAdapter = new DBAdapter(this);
        db = new DBHelper(this);

        showBox(this);

        listViewBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 遷移先のactivityを指定してintentを作成
                Intent intent = new Intent(BoxList.this, TodoDetail.class);
                // intentへ添え字付で値を保持させる
                intent.putExtra( "todoId", id );
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
