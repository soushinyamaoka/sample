package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

public class DeleteDialogFragment extends DialogFragment {

    DBAdapter dbAdapter = new DBAdapter(this);

    public int deleteDialog(long id){
        ArrayList<Integer> idAdapter = new ArrayList<>();
        try {
            idAdapter = dbAdapter.deletereadDB();//①DB上のidを取得しidAdapterに格納
        } catch (Exception e) {
            e.printStackTrace();
        }
        int deleteDBID = idAdapter.get((int) id);//②削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
        //dbAdapter.deleteDB(deleteDBID);//DB上の値をDB上のidで削除。

        return deleteDBID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//CharSequenceとStringは互換あり
        String deleteMessage = "削除してよろしいですか？";
        final String deleteOK = "はい";
        final String deleteNG = "いいえ";

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(deleteMessage)
                .setPositiveButton(deleteOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理

                        // MainActivityのインスタンスを取得
                        //MainActivity mainActivity = (MainActivity) getActivity();
                        //mainActivity.deleteList2();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
