package com.tungsten.hmclpe.launcher.launch.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.account.ReLoginDialog;
import com.tungsten.hmclpe.launcher.launch.boat.BoatMinecraftActivity;
import com.tungsten.hmclpe.launcher.launch.boat.VirGLService;
import com.tungsten.hmclpe.launcher.launch.pojav.PojavMinecraftActivity;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.view.list.MaxHeightRecyclerView;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

public class LaunchCheckDialog extends Dialog implements View.OnClickListener, Handler.Callback {

    private MainActivity activity;
    private String launchVersion;
    private Bundle bundle;

    private ImageView javaState;
    private ImageView libState;
    private ImageView loginState;
    private ImageView launchState;

    private boolean java = false;
    private boolean lib = false;
    private boolean login = false;

    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancel;

    private MaxHeightRecyclerView recyclerView;

    private CheckJavaTask checkJavaTask;
    private CheckLibTask checkLibTask;
    private CheckAccountTask checkAccountTask;
    private LaunchTask launchTask;

    public LaunchCheckDialog(@NonNull Context context, MainActivity activity, String launchVersion, Bundle bundle) {
        super(context);
        setContentView(R.layout.dialog_launch_check);
        setCancelable(false);
        this.activity = activity;
        this.launchVersion = launchVersion;
        this.bundle = bundle;
        init();
    }

    private void init(){
        javaState = findViewById(R.id.check_java_state);
        libState = findViewById(R.id.check_lib_state);
        loginState = findViewById(R.id.check_account_state);
        launchState = findViewById(R.id.check_launch_state);

        speedText = findViewById(R.id.download_speed_text);
        cancel = findViewById(R.id.cancel_launch_game);
        cancel.setOnClickListener(this);

        recyclerView = findViewById(R.id.download_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Objects.requireNonNull(recyclerView.getItemAnimator()).setAddDuration(0L);
        recyclerView.getItemAnimator().setChangeDuration(0L);
        recyclerView.getItemAnimator().setMoveDuration(0L);
        recyclerView.getItemAnimator().setRemoveDuration(0L);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(getContext(), new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        startCheckTasks();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startCheckTasks() {
        checkJavaTask = new CheckJavaTask(activity, launchVersion, new CheckJavaTask.CheckJavaCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(Exception e) {
                if (e == null) {
                    java = true;
                    javaState.setBackground(getContext().getDrawable(R.drawable.ic_baseline_done_black));
                    checkState();
                }
                else {
                    throwException(e);
                }
            }
        });
        checkLibTask = new CheckLibTask(activity, launchVersion, new CheckLibTask.CheckLibCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(Exception e) {
                if (e == null) {
                    lib = true;
                    libState.setBackground(getContext().getDrawable(R.drawable.ic_baseline_done_black));
                    checkState();
                }
                else {
                    throwException(e);
                }
            }
        });
        checkAccountTask = new CheckAccountTask(activity, new CheckAccountTask.CheckAccountCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(Exception e, boolean finish) {
                if (finish) {
                    if (e == null) {
                        login = true;
                        loginState.setBackground(getContext().getDrawable(R.drawable.ic_baseline_done_black));
                        checkState();
                    }
                    else {
                        throwException(e);
                    }
                }
                else {
                    ReLoginDialog dialog = new ReLoginDialog(getContext(), activity.publicGameSetting.account.email, Objects.requireNonNull(getServerFromUrl(activity.publicGameSetting.account.loginServer)).getYggdrasilService(),activity.publicGameSetting.account, new ReLoginDialog.ReloginCallback() {
                        @Override
                        public void onRelogin(Account account) {
                            for (int i = 0;i < activity.uiManager.accountUI.accounts.size();i++) {
                                Account ac = activity.uiManager.accountUI.accounts.get(i);
                                if (activity.publicGameSetting.account.email.equals(ac.email) && activity.publicGameSetting.account.auth_player_name.equals(ac.auth_player_name) && activity.publicGameSetting.account.auth_uuid.equals(ac.auth_uuid) && activity.publicGameSetting.account.loginServer.equals(ac.loginServer)) {
                                    activity.uiManager.accountUI.accounts.get(i).refresh(account);
                                    GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                    break;
                                }
                            }
                            activity.publicGameSetting.account = account;
                            GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                            activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                            activity.uiManager.mainUI.refreshAccount();
                            login = true;
                            loginState.setBackground(getContext().getDrawable(R.drawable.ic_baseline_done_black));
                            checkState();
                        }

                        @Override
                        public void onCancel() {
                            exit();
                        }
                    });
                    dialog.show();
                }
            }
        });
        checkJavaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        checkLibTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,recyclerView);
        checkAccountTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,activity.publicGameSetting.account);
    }

    private AuthlibInjectorServer getServerFromUrl(String url){
        for (int i = 0;i < activity.uiManager.accountUI.serverList.size();i++){
            if (activity.uiManager.accountUI.serverList.get(i).getUrl().equals(url)){
                return activity.uiManager.accountUI.serverList.get(i);
            }
        }
        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkState(){
        if (java && lib && login) {
            launchTask = new LaunchTask(activity, new LaunchTask.LaunchCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish(Vector<String> args) {
                    launchState.setBackground(getContext().getDrawable(R.drawable.ic_baseline_done_black));
                    bundle.putSerializable("args",args);
                    PrivateGameSetting privateGameSetting;
                    String settingPath = launchVersion + "/hmclpe.cfg";
                    if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
                        privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
                    }
                    else {
                        privateGameSetting = activity.privateGameSetting;
                    }
                    Intent intent;
                    if (privateGameSetting.boatLauncherSetting.enable){
                        intent = new Intent(getContext(), BoatMinecraftActivity.class);
                        if (privateGameSetting.boatLauncherSetting.renderer.equals("VirGL")) {
                            Intent virGLService = new Intent(getContext(), VirGLService.class);
                            getContext().stopService(virGLService);
                            getContext().startService(virGLService);
                        }
                    }
                    else {
                        intent = new Intent(getContext(), PojavMinecraftActivity.class);
                    }
                    intent.putExtras(bundle);
                    dismiss();
                    activity.launch(intent);
                }
            });
            launchTask.execute(activity.publicGameSetting.account);
        }
    }

    private void throwException(Exception e) {
        exit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.launch_failed_dialog_title));
        builder.setMessage(e.toString());
        builder.setPositiveButton(getContext().getString(R.string.launch_failed_dialog_positive), (dialogInterface, i) -> {});
        builder.create().show();
    }

    private void exit(){
        if(checkLibTask != null && checkLibTask.getStatus() != null && checkLibTask.getStatus() == AsyncTask.Status.RUNNING){
            checkLibTask.cancel(true);
        }
        if(checkAccountTask != null && checkAccountTask.getStatus() != null && checkAccountTask.getStatus() == AsyncTask.Status.RUNNING){
            checkAccountTask.cancel(true);
        }
        if(checkJavaTask != null && checkJavaTask.getStatus() != null && checkJavaTask.getStatus() == AsyncTask.Status.RUNNING){
            checkJavaTask.cancel(true);
        }
        if(launchTask != null && launchTask.getStatus() != null && launchTask.getStatus() == AsyncTask.Status.RUNNING){
            launchTask.cancel(true);
        }
        dismiss();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == NetSpeedTimer.NET_SPEED_TIMER_DEFAULT) {
            String speed = (String) msg.obj;
            speedText.setText(speed);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == cancel) {
            exit();
        }
    }
}
