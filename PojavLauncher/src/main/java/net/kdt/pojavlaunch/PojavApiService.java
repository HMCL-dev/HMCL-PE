package net.kdt.pojavlaunch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.function.ApiInstallerCallback;
import net.kdt.pojavlaunch.utils.JREUtils;

import java.util.ArrayList;

public class PojavApiService extends Service {

    public ApiInstallerCallback callback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startApiInstaller(String javaPath, ArrayList<String> commands, String debugDir, ApiInstallerCallback callback) {
        this.callback = callback;
        new Thread(() -> {
            int exitCode = JREUtils.launchAPIInstaller(getApplicationContext(), javaPath, commands, debugDir);
            onExit(PojavApiService.this,exitCode);
        }).start();
    }

    public static void onExit(Context context, int exitCode) {
        ((PojavApiService) context).callback.onExit(exitCode);
        ((PojavApiService) context).stopSelf();
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
