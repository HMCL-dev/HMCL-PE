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
    private ImageButton joinQQChannel;
    private ImageButton joinQQ;
    private ImageButton jumpToGit;

    public FeedbackUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        feedbackUI = activity.findViewById(R.id.ui_feedback);

        joinDiscord = activity.findViewById(R.id.join_discord);
        joinQQChannel = activity.findViewById(R.id.join_qq_channel);
        joinQQ = activity.findViewById(R.id.join_qq_group);
        jumpToGit = activity.findViewById(R.id.jump_to_git_issues);

        joinDiscord.setOnClickListener(this);
        joinQQChannel.setOnClickListener(this);
        joinQQ.setOnClickListener(this);
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

    /****************
     *
     * 发起添加群流程。群号：HMCL-PE(715191324) 的 key 为： 7rX0cr37hu_jNPaGIlqAEf4Ndv1BG-WU
     * 调用 joinQQGroup(7rX0cr37hu_jNPaGIlqAEf4Ndv1BG-WU) 即可发起手Q客户端申请加群 HMCL-PE(715191324)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == joinDiscord) {
            Uri uri = Uri.parse("https://discord.gg/zeMNy8Wdgd");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (v == joinQQChannel) {
            Uri uri = Uri.parse("https://qun.qq.com/qqweb/qunpro/share?_wv=3&_wwv=128&appChannel=share&inviteCode=1izjNP&businessType=9&from=246610&biz=ka");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (v == joinQQ){
            joinQQGroup("7rX0cr37hu_jNPaGIlqAEf4Ndv1BG-WU");
        }
        if (v == jumpToGit){
            Uri uri = Uri.parse("https://github.com/Tungstend/HMCL-PE/issues");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
}
