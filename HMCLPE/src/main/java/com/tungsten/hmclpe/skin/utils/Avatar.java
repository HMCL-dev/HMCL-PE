package com.tungsten.hmclpe.skin.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class Avatar {

    public static void setAvatar(String texture, ImageView face,ImageView hat){
        face.post(() -> {
            Bitmap skin = stringToBitmap(texture);
            Bitmap faceBitmap;
            Bitmap faceBitmapSec;
            faceBitmap = Bitmap.createBitmap(skin, 8, 8, 8, 8, (Matrix)null, false);
            faceBitmapSec = Bitmap.createBitmap(skin, 40, 8, 8, 8, (Matrix)null, false);
            Matrix matrix = new Matrix();
            float scale = (face.getWidth() / 8);
            Matrix matrixSec = new Matrix();
            float scaleSec = (hat.getWidth() / 8);
            matrix.postScale(scale,scale);
            Bitmap newBitmap = Bitmap.createBitmap(faceBitmap,0,0,8,8,matrix,false);
            matrixSec.postScale(scaleSec,scaleSec);
            Bitmap newBitmapSec = Bitmap.createBitmap(faceBitmapSec,0,0,8,8,matrixSec,false);
            handler.post(() -> {
                face.setImageBitmap(newBitmap);
                hat.setImageBitmap(newBitmapSec);
            });
        });
    }


    public static Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String bitmapToString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string=Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

    public static Bitmap getBitmapFromRes(Context context,int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        options.inTargetDensity = value.density;
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(),id,options);
    }

    @SuppressLint("HandlerLeak")
     static final android.os.Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

}