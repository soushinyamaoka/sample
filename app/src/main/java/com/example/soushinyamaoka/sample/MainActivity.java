package com.example.soushinyamaoka.sample;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import static android.content.ContentValues.TAG;

/**
 * Created by syama on 2018/02/03.
 */

public class MainActivity extends Activity {

    private EditText editText;
    private Button editButton;
    public ListView listView;
    DBAdapter dbAdapter = new DBAdapter(this);
    DialogFragment newFragment = new SampleDialogFragment();

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        editButton = findViewById(R.id.editButton);
        listView = findViewById(R.id.listView);

        showlist();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String str = editText.getText().toString();//書き込まれた内容(getText)をstrに格納
                    if(str.equals("")){
                        newFragment.show(getFragmentManager(), "test1");
                    }
                    else {
                        dbAdapter.openDB();
                        dbAdapter.writeDB(str);//writeDBメソッドを呼び出し、strを引数として渡す
                        dbAdapter.readDB();
                        showlist();
                    }
                } catch (Exception e) {//エラーの場合
                    Log.e(TAG, "SQLExcepption:" + e.toString());
                }
            }
        });

        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent,View view,
                                           int position, long id) {
               dbAdapter.deleteDB(id);
                try {
                    dbAdapter.readDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
    public void showlist(){
        ArrayList<String> lvAdapter = new ArrayList<>();
        dbAdapter.openDB();
        try {
            lvAdapter = dbAdapter.readDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Adapterの作成
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list, (List<String>) lvAdapter);
        // ListViewにAdapterを関連付ける
        listView.setAdapter(adapter);
    }


}
