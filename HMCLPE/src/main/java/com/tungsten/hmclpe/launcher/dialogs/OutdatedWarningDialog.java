package com.tungsten.hmclpe.launcher.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;

public class OutdatedWarningDialog extends Dialog implements View.OnClickListener {

    private CheckBox checkBox;
    private Button pojav;
    private Button fcl;
    private Button positive;

    public OutdatedWarningDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        setContentView(R.layout.dialog_outdated_warning);

        checkBox = findViewById(R.id.hide);
        pojav = findViewById(R.id.pojav);
        fcl = findViewById(R.id.fcl);
        positive = findViewById(R.id.positive);
        pojav.setOnClickListener(this);
        fcl.setOnClickListener(this);
        positive.setOnClickListener(this);
    }

    public static void init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("warning", Context.MODE_PRIVATE);
        boolean shouldShow = sharedPreferences.getBoolean("outdated_warning", true);
        if (shouldShow) {
            OutdatedWarningDialog dialog = new OutdatedWarningDialog(context);
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == pojav) {
            Uri uri = Uri.parse("https://github.com/PojavLauncherTeam/PojavLauncher");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == fcl) {
            Uri uri = Uri.parse("https://alist.8mi.tech/FCL");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == positive) {
            if (checkBox.isChecked()) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("warning", Context.MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("outdated_warning", false);
                editor.apply();
            }
            dismiss();
        }
    }
}
