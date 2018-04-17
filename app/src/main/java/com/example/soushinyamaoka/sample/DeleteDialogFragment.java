package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import static android.app.Activity.RESULT_OK;

public class DeleteDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//CharSequenceとStringは互換あり
        String deleteMessage = "完全に削除してよろしいですか？";
        final String deleteOK = "はい";
        final String deleteNG = "いいえ";
        final long listviewId = getArguments().getLong("deleteId");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(deleteMessage)
                .setPositiveButton(deleteOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理
                        Intent data = new Intent(DeleteDialogFragment.this,ToDoComplete.class);
                        //data.putExtra("boxName",boxName);
                        //setResult(RESULT_OK, data);
                        //finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(deleteNG, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
