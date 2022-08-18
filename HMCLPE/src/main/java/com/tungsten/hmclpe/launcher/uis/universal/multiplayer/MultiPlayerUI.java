package com.tungsten.hmclpe.launcher.uis.universal.multiplayer;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.hin2n.CreateCommunityDialog;
import com.tungsten.hmclpe.launcher.dialogs.hin2n.JoinCommunityDialog;
import com.tungsten.hmclpe.launcher.launch.check.LaunchTools;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.multiplayer.Hin2nService;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;

import wang.switchy.hin2n.model.EdgeStatus;
import wang.switchy.hin2n.model.N2NSettingInfo;

public class MultiPlayerUI extends BaseUI implements View.OnClickListener {

    public LinearLayout multiPlayerUI;

    private LinearLayout launch;
    private LinearLayout create;
    private LinearLayout join;
    private LinearLayout help;
    private LinearLayout feedback;

    public MultiPlayerUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        multiPlayerUI = activity.findViewById(R.id.ui_multi_player);

        launch = activity.findViewById(R.id.launch_game_from_multiplayer);
        create = activity.findViewById(R.id.create_multiplayer_community);
        join = activity.findViewById(R.id.join_multiplayer_community);
        help = activity.findViewById(R.id.multiplayer_help);
        feedback = activity.findViewById(R.id.multiplayer_feedback);

        launch.setOnClickListener(this);
        create.setOnClickListener(this);
        join.setOnClickListener(this);
        help.setOnClickListener(this);
        feedback.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.multi_player_ui_title),activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(multiPlayerUI,activity,context,true);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(multiPlayerUI,activity,context,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Hin2nService.VPN_REQUEST_CODE_CREATE && resultCode == RESULT_OK) {
            Intent intent = new Intent(context, Hin2nService.class);
            Bundle bundle = new Bundle();
            N2NSettingInfo n2NSettingInfo = new N2NSettingInfo(Hin2nService.getCreatorModel());
            bundle.putParcelable("n2nSettingInfo", n2NSettingInfo);
            intent.putExtra("Setting", bundle);
            activity.startService(intent);
        }
        if (requestCode == Hin2nService.VPN_REQUEST_CODE_JOIN && resultCode == RESULT_OK) {
            Intent intent = new Intent(context, Hin2nService.class);
            Bundle bundle = new Bundle();
            new Thread(() -> {
                N2NSettingInfo n2NSettingInfo = new N2NSettingInfo(Hin2nService.getPlayerModel());
                activity.runOnUiThread(() -> {
                    bundle.putParcelable("n2nSettingInfo", n2NSettingInfo);
                    intent.putExtra("Setting", bundle);
                    activity.startService(intent);
                    JoinCommunityDialog.getInstance().progressBar.setVisibility(View.GONE);
                    JoinCommunityDialog.getInstance().positive.setVisibility(View.VISIBLE);
                    JoinCommunityDialog.getInstance().negative.setEnabled(true);
                    JoinCommunityDialog.getInstance().dismiss();
                });
            }).start();
        }
    }

    @Override
    public void onClick(View view) {
        EdgeStatus.RunningStatus status = Hin2nService.INSTANCE == null ? EdgeStatus.RunningStatus.DISCONNECT : Hin2nService.INSTANCE.getCurrentStatus();
        boolean isCreated = Hin2nService.INSTANCE != null && status != EdgeStatus.RunningStatus.DISCONNECT && status != EdgeStatus.RunningStatus.FAILED;
        if (view == launch) {
            String settingPath = activity.publicGameSetting.currentVersion + "/hmclpe.cfg";
            String finalPath;
            if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
                finalPath = settingPath;
            }
            else {
                finalPath = AppManifest.SETTING_DIR + "/private_game_setting.json";
            }
            Bundle bundle = new Bundle();
            bundle.putString("setting_path",finalPath);
            bundle.putBoolean("test",false);
            LaunchTools.launch(context, activity, activity.publicGameSetting.currentVersion, bundle);
        }
        if (view == create) {
            if (!isCreated) {
                CreateCommunityDialog dialog = new CreateCommunityDialog(context, null, this);
                dialog.show();
            }
            else {
                Toast.makeText(context, context.getString(R.string.dialog_hin2n_menu_in), Toast.LENGTH_SHORT).show();
            }
        }
        if (view == join) {
            if (!isCreated) {
                JoinCommunityDialog dialog = new JoinCommunityDialog(context, null, this);
                dialog.show();
            }
            else {
                Toast.makeText(context, context.getString(R.string.dialog_hin2n_menu_in), Toast.LENGTH_SHORT).show();
            }
        }
        if (view == help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_hin2n_help_title));
            builder.setMessage(context.getString(R.string.dialog_hin2n_help_text));
            builder.setPositiveButton(context.getString(R.string.dialog_hin2n_help_positive), null);
            builder.create().show();
        }
        if (view == feedback) {
            Uri uri = Uri.parse("https://github.com/Tungstend/HMCL-PE/issues");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
}
