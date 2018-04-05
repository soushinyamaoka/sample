package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

public class DeleteDialogFragment extends DialogFragment {

    public void deleteDialog(long id){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.deleteList2((int)id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//CharSequenceとStringは互換あり
        String deleteMessage = "削除してよろしいですか？";
        final String deleteOK = "はい";
        final String deleteNG = "いいえ";
        final long deleteId = getArguments().getLong("deleteId");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(deleteMessage)
                .setPositiveButton(deleteOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理
                        DBAdapter dbAdapter = new DBAdapter(this);
                        ArrayList<Integer> idAdapter = new ArrayList<>();
                        MainActivity mainActivity = new MainActivity();
                        dbAdapter.openDB();
                        try {
                            idAdapter = dbAdapter.deletereadDB();//①DB上のidを取得しidAdapterに格納
                            int deleteDBID = idAdapter.get((int) deleteId);//②削除対象のリストと同じ位置にある、DB上のidを取得しdeleteIDに格納
                            dbAdapter.deleteDB(deleteDBID);//DB上の値をDB上のidで削除。
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
