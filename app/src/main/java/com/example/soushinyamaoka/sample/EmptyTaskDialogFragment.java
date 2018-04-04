package com.example.soushinyamaoka.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by SoushinYamaoka on 2018/03/25.
 */

public class EmptyTaskDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//CharSequenceとStringは互換あり
        String empMessage = "タスクを入力してください";

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(empMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //リストクリック時の処理
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
