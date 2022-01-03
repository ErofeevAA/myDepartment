package com.example.mydepartment.dialog;

import android.app.AlertDialog;
import android.content.Context;

import com.example.mydepartment.R;

public class FailDialogBuilder extends AlertDialog.Builder {

    public FailDialogBuilder(Context context, String alert) {
        super(context);
        setTitle("Failed");
        setIcon(R.drawable.ic_baseline_warning_24);
        setMessage(alert);
        setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
    }
}
