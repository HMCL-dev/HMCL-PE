package com.tungsten.hmclpe.utils.file;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileStringUtils {
    public static String getStringFromFile(String path){
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String string = new String(bytes);
            return string;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public static void writeFile(String path,String string){
        try {
            String parent = new File(path).getParent();
            FileUtils.createDirectory(parent);
            FileUtils.createFile(path);
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(string);
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("writeFile",e.toString());
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
