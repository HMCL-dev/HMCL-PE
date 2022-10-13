package com.tungsten.hmclpe.launcher.uis.universal.setting.right.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

public class FeedbackUI extends BaseUI implements View.OnClickListener {

    public LinearLayout feedbackUI;

    private ImageButton joinDiscord;
    private ImageButton jumpToGit;

    public FeedbackUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        feedbackUI = activity.findViewById(R.id.ui_feedback);

        joinDiscord = activity.findViewById(R.id.join_discord);
        jumpToGit = activity.findViewById(R.id.jump_to_git_issues);

        joinDiscord.setOnClickListener(this);
        jumpToGit.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(feedbackUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startFeedbackUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(feedbackUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startFeedbackUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == joinDiscord) {
            Uri uri = Uri.parse("https://discord.gg/zeMNy8Wdgd");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (v == jumpToGit){
            Uri uri = Uri.parse("https://github.com/Tungstend/HMCL-PE/issues");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
}
