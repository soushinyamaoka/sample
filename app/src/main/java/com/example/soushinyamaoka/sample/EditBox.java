package com.example.soushinyamaoka.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditBox extends AppCompatActivity{

    BoxDBAdapter boxDBAdapter;
    EditText editEditBox;
    Button editEditButton;
    int boxId;
    String boxName;
    DBAdapter dbAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edit_box);
        editEditBox = findViewById(R.id.edit_edit_box);
        editEditButton = findViewById(R.id.edit_edit_button);
        dbAdapter = new DBAdapter(this);
        boxDBAdapter = new BoxDBAdapter(this);

        // 現在のintentを取得する
        Intent intent = getIntent();
        boxId = intent.getIntExtra("boxName", -1);
        boxName = boxDBAdapter.getBoxName(boxId);
        editEditBox.setText(boxName);

        editEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //todoの詳細を表示
                //-------------------------
                String setBoxName = editEditBox.getText().toString();
                try {
                    boxDBAdapter.updateBoxDB(boxId,setBoxName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
