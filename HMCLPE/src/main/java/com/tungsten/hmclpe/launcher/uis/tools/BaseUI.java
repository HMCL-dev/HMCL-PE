package com.tungsten.hmclpe.launcher.uis.tools;

import android.content.Context;
import android.content.Intent;

import com.tungsten.hmclpe.launcher.MainActivity;

public class BaseUI implements UILifecycleCallbacks{

    public Context context;
    public MainActivity activity;

    //Method instruction
    public BaseUI(Context context,MainActivity activity){
        super();
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onLoaded() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent() {

    }
}
