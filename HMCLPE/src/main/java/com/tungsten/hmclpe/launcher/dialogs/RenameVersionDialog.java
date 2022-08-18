package com.tungsten.hmclpe.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;

public class RenameVersionDialog extends Dialog implements View.OnClickListener {

    private String currentVersion;
    private OnVersionRenameListener onVersionRenameListener;

    private EditText editText;
    private Button confirm;
    private Button cancel;

    public RenameVersionDialog(@NonNull Context context,String currentVersion,OnVersionRenameListener onVersionRenameListener) {
        super(context);
        this.currentVersion = currentVersion;
        this.onVersionRenameListener = onVersionRenameListener;
        setContentView(R.layout.dialog_rename_version);
        setCancelable(false);
        init();
    }

    private void init(){
        editText = findViewById(R.id.rename_version);
        confirm = findViewById(R.id.rename);
        cancel = findViewById(R.id.cancel);
        editText.setText(currentVersion);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == confirm){
            if (editText.getText().toString().equals("") || editText.getText().toString().contains("/")) {
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_rename_version_alert), Toast.LENGTH_SHORT).show();
            }
            else {
                onVersionRenameListener.onRename(editText.getText().toString());
                this.dismiss();
            }
        }
        if (v == cancel){
            this.dismiss();
        }
    }

    public interface OnVersionRenameListener{
        void onRename(String name);
    }
}
