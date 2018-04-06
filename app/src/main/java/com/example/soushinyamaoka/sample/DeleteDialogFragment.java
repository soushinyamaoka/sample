package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeleteDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//CharSequenceとStringは互換あり
        String deleteMessage = "削除してよろしいですか？";
        final String deleteOK = "はい";
        final String deleteNG = "いいえ";
        final long deleteId = getArguments().getLong("deleteId");
        //ListView listView = findViewById(R.id.list_view);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(deleteMessage)
                .setPositiveButton(deleteOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理
                        MainActivity mainActivity = new MainActivity();
                        mainActivity.deleteList2(getActivity(),deleteId);
                        mainActivity.showlist(getActivity());

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
