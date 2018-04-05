package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class DeleteDialogFragment extends DialogFragment {

    //private static MyContext instance = null;
    private Context applicationContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//CharSequenceとStringは互換あり
        String deleteMessage = "削除してよろしいですか？";
        final String deleteOK = "はい";
        final String deleteNG = "いいえ";
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final long deleteId = getArguments().getLong("deleteId");

        builder.setMessage(deleteMessage)
                .setPositiveButton(deleteOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理
                        DBAdapter dbAdapter = new DBAdapter();
                        ArrayList<Integer> idAdapter = new ArrayList<>();
                        try {
                            dbAdapter.openDB();
                            idAdapter = dbAdapter.deletereadDB();//①DB上のidを取得しidAdapterに格納
                            int deleteDBID = idAdapter.get((int) deleteId);//②削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
                            dbAdapter.deleteDB(deleteDBID);//DB上の値をDB上のidで削除。
                            MainActivity mainActivity = new  MainActivity();
                            mainActivity.showlist();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
