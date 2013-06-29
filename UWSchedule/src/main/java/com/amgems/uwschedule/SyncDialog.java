package com.amgems.uwschedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;import com.amgems.uwschedule.R;

/**
 * Created by zac on 6/23/13.
 */
public class SyncDialog extends DialogFragment {
    public static final String TAG_NAME = "com.amgems.uwschedule.SyncDialog";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(R.layout.sync_dialog, null))
              .setPositiveButton(R.string.sync, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {

                  }
              })
              .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {

                  }
              });

        return dialogBuilder.create();
    }
}
