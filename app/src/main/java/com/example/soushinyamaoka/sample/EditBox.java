package com.example.soushinyamaoka.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class EditBox extends AppCompatActivity{

    BoxDBAdapter boxDBAdapter;
    BoxDBHelper boxdb;
    ArrayAdapter<String> adapter;
    EditText editEditBox;
    Button editEditButton;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edit_box);
        editEditBox = findViewById(R.id.edit_edit_box);
        editEditButton = findViewById(R.id.edit_edit_button);
        boxDBAdapter = new BoxDBAdapter(this);
        boxdb = new BoxDBHelper(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーの文字列を取得する
        final int boxDataBaseId = intent.getIntExtra( "boxDataBaseId",-1);
        //editEditBoxに現在格納されている値を表示
        String[] setBox;
        setBox = boxDBAdapter.getBoxName(boxDataBaseId);
        editEditBox.setText(setBox[0]);

        editEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //todoの詳細を表示
                //-------------------------
                String setBoxName = editEditBox.getText().toString();
                boxDBAdapter.openBoxDB();
                try {
                    boxDBAdapter.updateBoxDB(boxDataBaseId,setBoxName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //-------------------------
                finish();
            }
        });
    }
}
