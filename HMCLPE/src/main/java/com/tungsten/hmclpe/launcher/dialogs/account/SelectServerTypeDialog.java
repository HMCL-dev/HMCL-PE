package com.tungsten.hmclpe.launcher.dialogs.account;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;

public class SelectServerTypeDialog extends Dialog implements View.OnClickListener {

    private OnServerAddListener onServerAddListener;

    private LinearLayout authlib;
    private LinearLayout nide8auth;

    public SelectServerTypeDialog(@NonNull Context context,OnServerAddListener onServerAddListener) {
        super(context);
        this.onServerAddListener = onServerAddListener;
        setContentView(R.layout.dialog_select_server_type);
        setCancelable(false);
        init();
    }

    private void init(){
        authlib = findViewById(R.id.server_type_authlib);
        nide8auth = findViewById(R.id.server_type_nide);

        authlib.setOnClickListener(this);
        nide8auth.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == authlib) {
            AddAuthLibServerDialog addAuthLibServerDialog = new AddAuthLibServerDialog(getContext(), authlibInjectorServer -> {
                onServerAddListener.onServerAdd(authlibInjectorServer);
            });
            addAuthLibServerDialog.show();
        }
        if (view == nide8auth) {
            AddNide8AuthServerDialog addNide8AuthServerDialog = new AddNide8AuthServerDialog(getContext(), authlibInjectorServer -> {
                onServerAddListener.onServerAdd(authlibInjectorServer);
            });
            addNide8AuthServerDialog.show();
        }
        dismiss();
    }

    public interface OnServerAddListener{
        void onServerAdd(AuthlibInjectorServer server);
    }
}
