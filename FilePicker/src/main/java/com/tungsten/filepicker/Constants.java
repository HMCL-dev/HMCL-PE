package com.tungsten.filepicker;

import android.Manifest;
import android.os.Environment;

import com.tungsten.filepicker.fileoperations.GetRemovableDevice;

import java.io.File;
import java.util.List;

/**
 * Created by adik on 10/18/2015.
 */
public class Constants {

    public enum FILTER_OPTIONS {
        FILES,
        FOLDER,
        ALL
    }

    public enum SORT_OPTIONS {
        NAME,
        SIZE,
        LAST_MODIFIED
    }

    public enum APP_MODE {
        FILE_BROWSER,
        FILE_CHOOSER,
        FOLDER_CHOOSER,
    }

    public enum CHOICE_MODE {
        SINGLE_CHOICE,
        MULTI_CHOICE
    }

    public enum SELECTION_MODES {
        SINGLE_SELECTION,
        MULTIPLE_SELECTION
    }

    public static final String APP_PREMISSION_KEY = "APP_PERMISSIONS";
    public static final String FILE_SELECTED_BROADCAST = "com.tungsten.filepicker.FILE_SELECTED_BROADCAST";
    public static final String INITIAL_DIRECTORY = "INITIAL_DIRECTORY";
    public static final String ALLOWED_FILE_EXTENSIONS = "ALLOWED_FILE_EXTENSIONS";
    public static final String BROADCAST_SELECTED_FILE = "BROADCAST_SELECTED_FILE";
    public static final String SELECTION_MODE = "SELECTION_MODE";
    public static final String SELECTED_ITEMS = "SELECTED_ITEMS";
    public static final String [] APP_PREMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String INTERNALSTORAGE = "Internal Storage";
    public static final String EXTERNALSTORAGE = "External Storage";
    public static File internalStorageRoot = Environment.getExternalStorageDirectory();
    public static File externalStorageRoot;
    public static final String SHOW_FOLDER_SIZE = "false";
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	
    static {

        try {
            List<GetRemovableDevice.StorageInfo> infos = GetRemovableDevice.getStorageList();
            boolean isExternalDirectoryInitialized = false;
            for(int i=0; i<infos.size(); i++) {
                if (infos.get(i).getDisplayName().contains(Constants.EXTERNALSTORAGE)) {
                    File detectedDirectory =  new File(infos.get(i).path).getCanonicalFile();
                    if (detectedDirectory!=null && detectedDirectory.exists() && detectedDirectory.isDirectory() && detectedDirectory.getTotalSpace()>0)
                        externalStorageRoot = detectedDirectory;
                    else
                        externalStorageRoot = new File("/");
                    isExternalDirectoryInitialized = true;
                    break;
                }
            }
            if (!isExternalDirectoryInitialized)
                externalStorageRoot = new File("/");
        } catch (Exception e) {
            e.printStackTrace();
            externalStorageRoot = new File("/");
        }
    }
}
