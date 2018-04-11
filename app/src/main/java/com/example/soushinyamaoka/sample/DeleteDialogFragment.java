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
        final long listviewId = getArguments().getLong("deleteId");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(deleteMessage)
                .setPositiveButton(deleteOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //リストクリック時の処理

                        //mainActivity.showlist(getActivity(),listView);

                        MainActivity mainActivity = new MainActivity();
                        mainActivity.deleteList(getActivity(),listviewId);

                        //listView = (ListView) getView().findViewById(R.id.todo_list_view);

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
