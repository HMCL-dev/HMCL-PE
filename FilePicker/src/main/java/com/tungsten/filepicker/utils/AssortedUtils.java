package com.tungsten.filepicker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

public class AssortedUtils {

	public static void SavePrefs(String key, String value, Context context)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key,value);
		editor.apply();
	}

	public static String GetPrefs(String key, Context context)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(key, "");
	}

	public static long GetMinimumDirSize(File file)
	{
		long result = 0;
		for (File f : file.listFiles()) {
			if(f.isFile()) {
				result += f.length();
			} else if(f.isDirectory()) {
				result += f.getTotalSpace();
			}
		}
		return result;
	}

}