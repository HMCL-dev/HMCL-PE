package com.tungsten.hmclpe.launcher;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.tungsten.hmclpe.utils.LocaleUtils;

import wang.switchy.hin2n.Hin2n;

public class HMCLPEApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceIdentifier.register(this);
        context = this.getApplicationContext();
        Hin2n.getInstance().setup(context);
    }

    public static Context getContext(){
        return context;
    }

    public static void releaseContext(){
        context = null;
    }

}