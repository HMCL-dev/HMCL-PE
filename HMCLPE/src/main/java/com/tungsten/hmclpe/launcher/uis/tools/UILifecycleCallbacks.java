package com.tungsten.hmclpe.launcher.uis.tools;

import android.content.Intent;

public interface UILifecycleCallbacks {
    void onCreate();

    void onStart();

    void onResume();

    void onRestart();

    void onPause();

    void onStop();

    void onDestroy();

    void onLoaded();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onNewIntent();
}
