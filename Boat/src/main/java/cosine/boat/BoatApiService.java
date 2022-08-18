package cosine.boat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import cosine.boat.function.ApiInstallerCallback;

public class BoatApiService extends Service {

    public ApiInstallerCallback callback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startApiInstaller(String javaPath, ArrayList<String> commands, String debugDir, ApiInstallerCallback callback) {
        this.callback = callback;
        new Thread(() -> {
            int exitCode = LoadMe.launchJVM(javaPath,commands,debugDir);
            onExit(BoatApiService.this,exitCode);
        }).start();
    }

    public static void onExit(Context context, int exitCode) {
        ((BoatApiService) context).callback.onExit(exitCode);
        ((BoatApiService) context).stopSelf();
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
