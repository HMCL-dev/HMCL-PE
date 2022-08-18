package com.tungsten.filepicker.utils;

/**
 * Created by Aditya on 4/15/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.R;

import java.util.ArrayList;

/**
 * Created by Aditya on 4/14/2017.
 */
public class Permissions extends AppCompatActivity {

    public static final int APP_PERMISSIONS_REQUEST = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED_OR_NEVER_ASKED = 2;
    public static final int GRANTED = 3;
    private Context mContext;

    public void requestPermissions(String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int ungrantedPermCount = 0;
            ArrayList<String> permissionsToBeAsked = new ArrayList<String>();
            for(int i=0; i < permissions.length; i++) {
                if (isPermissionIsGranted(permissions[i],this) != GRANTED) {
                    ungrantedPermCount++;
                    permissionsToBeAsked.add(permissions[i]);
                }
            }
            if (ungrantedPermCount == 0) {
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissionsToBeAsked.toArray(new String[permissionsToBeAsked.size()]),
                        APP_PERMISSIONS_REQUEST);
            }
        } else {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public static int isPermissionIsGranted(String permission, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                    return BLOCKED_OR_NEVER_ASKED;
                }
                return DENIED;
            }
            return GRANTED;
        } else {
            return GRANTED;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        Bundle b = this.getIntent().getExtras();
        String[] permissions = b.getStringArray(Constants.APP_PREMISSION_KEY);
        if (permissions != null) {
            requestPermissions(permissions);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.permission_request_error),Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    boolean isAllPermissionsGranted = true;
                    for (int i=0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            isAllPermissionsGranted = false;
                            break;
                        }
                    }
                    if (isAllPermissionsGranted) {
                        setResult(Activity.RESULT_OK);
                    } else {
                        setResult(Activity.RESULT_CANCELED);
                    }

                } else {
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
            }
        }
    }

}

