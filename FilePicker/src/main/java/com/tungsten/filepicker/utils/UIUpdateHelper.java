package com.tungsten.filepicker.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.tungsten.filepicker.NavigationHelper;

/**
 * Created by Aditya on 4/16/2017.
 */
public class UIUpdateHelper {

    Context mContext;
    NavigationHelper mNavigationeHelper;

    public UIUpdateHelper(NavigationHelper mNavigationeHelper, Context mContext) {
        this.mContext = mContext;
        this.mNavigationeHelper = mNavigationeHelper;
    }

    public Runnable updateRunner() {
        return new Runnable() {
            @Override
            public void run() {
                mNavigationeHelper.triggerFileChanged();
            }
        };
    }

    public Runnable errorRunner(final String msg) {
        return new Runnable() {
            @Override
            public void run() {
                    UIUtils.ShowToast(msg, mContext);
                    mNavigationeHelper.triggerFileChanged();
            }
        };
    }

    public Runnable progressUpdater(final ProgressDialog progressDialog, final int progress, final String msg) {
        return new Runnable() {
            @Override
            public void run() {
                if(progressDialog!=null) {
                    progressDialog.setProgress(progress);
                    progressDialog.setMessage(msg);
                }
            }
        };
    }

    public Runnable toggleProgressBarVisibility(final ProgressDialog progressDialog) {
        return new Runnable() {
            @Override
            public void run() {
                if(progressDialog!=null) {
                    progressDialog.dismiss();
                }
            }
        };
    }
}
