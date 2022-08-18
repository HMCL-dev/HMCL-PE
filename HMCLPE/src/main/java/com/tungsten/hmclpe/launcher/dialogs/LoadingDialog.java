package com.tungsten.hmclpe.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;

public class LoadingDialog extends Dialog {

    private final TextView loadingText;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        loadingText = findViewById(R.id.loading_text);
    }

    public void setLoadingText(String string) {
        loadingText.setText(string);
    }

}
