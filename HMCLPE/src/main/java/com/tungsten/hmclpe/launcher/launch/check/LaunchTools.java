package com.tungsten.hmclpe.launcher.launch.check;

/**
 * 启动流程
 * 判断当前版本是否启用特定设置，并获取当前版本设置
 *
 * 检测Java版本（当前选择的Java版本是否支持当前游戏版本）
 * 检查Forge动画禁用情况（1.12及以下版本启动forge需要禁用forge动画）
 *
 * 处理游戏依赖（检查运行库,检查游戏文件是否完整）
 *
 * 登录（检查是否创建用户，微软账户刷新，外置登录刷新或者重登录，以及检测authlib-injector，nide8auth的更新）
 *
 * 传入参数，使用相应后端启动游戏
 */

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.account.AddAccountDialog;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

public class LaunchTools {

    public static AddAccountDialog addAccountDialog;

    public static void launch(Context context, MainActivity activity,String launchVersion, Bundle bundle) {
        if (launchVersion.endsWith("/") || SettingUtils.getLocalVersionNames(activity.launcherSetting.gameFileDirectory).size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.launch_failed_dialog_title));
            builder.setMessage(context.getString(R.string.launch_check_dialog_exception_no_game));
            builder.setPositiveButton(context.getString(R.string.launch_failed_dialog_positive), (dialogInterface, i) -> {
                activity.uiManager.switchMainUI(activity.uiManager.downloadUI);
            });
            builder.create().show();
        }
        else if (activity.publicGameSetting.account.loginType == 0) {
            addAccountDialog = new AddAccountDialog(context, activity, new AddAccountDialog.AddAccountCallback() {
                @Override
                public void onAccountAdd(Account account) {
                    boolean exist = false;
                    for (Account a : InitializeSetting.initializeAccounts(context)) {
                        exist = account.email.equals(a.email) && account.auth_player_name.equals(a.auth_player_name) && account.auth_uuid.equals(a.auth_uuid) && account.loginServer.equals(a.loginServer);
                        if (exist) {
                            break;
                        }
                    }
                    activity.publicGameSetting.account = account;
                    GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                    if (!exist) {
                        activity.uiManager.accountUI.accounts.add(account);
                        activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                        GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                    }
                    activity.uiManager.mainUI.refreshAccount();
                    LaunchCheckDialog dialog = new LaunchCheckDialog(context,activity,launchVersion,bundle);
                    dialog.show();
                }

                @Override
                public void onCancel() {

                }
            });
            addAccountDialog.show();
        }
        else {
            LaunchCheckDialog dialog = new LaunchCheckDialog(context,activity,launchVersion,bundle);
            dialog.show();
        }
        MainActivity.verify();
    }

}
