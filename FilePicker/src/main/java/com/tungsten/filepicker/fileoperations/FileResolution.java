package com.tungsten.filepicker.fileoperations;

import com.tungsten.filepicker.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Aditya on 4/14/2017.
 */
public class FileResolution {

    public static HashMap<String, Integer> extIconMap;

    static {
        extIconMap = new HashMap<String, Integer>();
        extIconMap.put("jpg", R.drawable.ic_photo_black_24dp);
        extIconMap.put("bmp", R.drawable.ic_photo_black_24dp);
        extIconMap.put("png", R.drawable.ic_photo_black_24dp);
        extIconMap.put("gif", R.drawable.ic_photo_black_24dp);
        extIconMap.put("psd", R.drawable.ic_photo_black_24dp);

        extIconMap.put("mp3", R.drawable.ic_audiotrack_black_24dp);
        extIconMap.put("wav", R.drawable.ic_audiotrack_black_24dp);
        extIconMap.put("wma", R.drawable.ic_audiotrack_black_24dp);
        extIconMap.put("aac", R.drawable.ic_audiotrack_black_24dp);
        extIconMap.put("mid", R.drawable.ic_audiotrack_black_24dp);

        extIconMap.put("3gp", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("3g2", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("avi", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("flv", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("mov", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("mp4", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("mpg", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("rm", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("vob", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("wmv", R.drawable.ic_ondemand_video_black_24dp);
        extIconMap.put("mkv", R.drawable.ic_ondemand_video_black_24dp);

        extIconMap.put("rar", R.drawable.ic_layers_black_24dp);
        extIconMap.put("zip", R.drawable.ic_layers_black_24dp);

        extIconMap.put("apk", R.drawable.ic_android_black_24dp);

    }

    public static int getFileIcon(File f) {
        if (f.isDirectory()) {
            return R.drawable.ic_folder_open_black_24dp;
        } else {
            String ext = FilenameUtils.getExtension(f.getName());
            if (ext!=null && extIconMap.containsKey(ext))
                return extIconMap.get(ext);

            return R.drawable.ic_insert_drive_file_black_24dp;
        }
    }
}
