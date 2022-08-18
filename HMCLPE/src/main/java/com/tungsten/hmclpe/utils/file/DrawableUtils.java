package com.tungsten.hmclpe.utils.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

public class DrawableUtils {

    public static Drawable getDrawableFromFile(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static Drawable getDrawableForView(Drawable rawDrawable, int color){
        Drawable drawable = DrawableCompat.wrap(rawDrawable);
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }
}
