package com.tungsten.hmclpe.launcher.uis.game.version.universal;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

public class ExportPackageTypeUI extends BaseUI implements View.OnClickListener {

    public LinearLayout exportPackageTypeUI;

    private LinearLayout mcbbs;
    private LinearLayout multimc;
    private LinearLayout server;

    public ExportPackageTypeUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exportPackageTypeUI = activity.findViewById(R.id.ui_export_package_type);

        mcbbs = activity.findViewById(R.id.export_package_mcbbs);
        multimc = activity.findViewById(R.id.export_package_multimc);
        server = activity.findViewById(R.id.export_package_server);
        mcbbs.setOnClickListener(this);
        multimc.setOnClickListener(this);
        server.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.export_package_type_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(exportPackageTypeUI,activity,context,true);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(exportPackageTypeUI,activity,context,true);
    }

    @Override
    public void onClick(View view) {
        if (view == mcbbs) {

        }
        if (view == multimc) {

        }
        if (view == server) {

        }
    }
}
