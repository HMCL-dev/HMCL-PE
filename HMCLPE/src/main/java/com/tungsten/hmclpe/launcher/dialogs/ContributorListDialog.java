package com.tungsten.hmclpe.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;

public class ContributorListDialog extends Dialog {

    public ContributorListDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_contributor_list);
    }

}
