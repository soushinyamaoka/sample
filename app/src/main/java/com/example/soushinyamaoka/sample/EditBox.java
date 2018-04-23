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
    ArrayAdapter<String> adapter;
    EditText editEditBox;
    Button editEditButton;
    int boxDataBaseId;
    int boxId;
    String boxName;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edit_box);
        editEditBox = findViewById(R.id.edit_edit_box);
        editEditButton = findViewById(R.id.edit_edit_button);
        boxDBAdapter = new BoxDBAdapter(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        //Mainから渡された「選択されたカテゴリのDB上のID」を取得
        //boxDataBaseId = intent.getIntExtra( "boxDataBaseId",-1);
        //Mainから渡された「選択されたカテゴリ名」を取得
        //String[] setBox = intent.getStringArrayExtra( "boxName");
        boxId = intent.getIntExtra("boxName", -1);
        boxDBAdapter.openBoxDB();
        //boxName = boxDBAdapter.changeToBoxName(boxId);
        boxName = boxDBAdapter.getBoxName(boxId);
        editEditBox.setText(boxName);

        editEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //todoの詳細を表示
                //-------------------------
                String setBoxName = editEditBox.getText().toString();
                boxDBAdapter.openBoxDB();
                try {
                    boxDBAdapter.updateBoxDB(boxId,setBoxName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //-------------------------
                Intent intent = new Intent();
                //intent.putExtra("changedBoxName", setBoxName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
