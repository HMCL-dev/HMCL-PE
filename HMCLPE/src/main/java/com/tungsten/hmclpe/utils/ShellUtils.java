package com.tungsten.hmclpe.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellUtils {

    public static void doShell(String cmd) throws Exception {
        Log.e("命令",cmd);
        Process proc = Runtime.getRuntime().exec((cmd).split(" "));
        // 标准输入流（必须写在 waitFor 之前）
        String inStr = consumeInputStream(proc.getInputStream());
        // 标准错误流（必须写在 waitFor 之前）
        String errStr = consumeInputStream(proc.getErrorStream());
        int retCode = proc.waitFor();
        if (retCode == 0) {
            Log.e("成功",inStr);
        } else {
            Log.e("失败",errStr);
        }
    }

    private static String consumeInputStream(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                Log.e("ShellUtils",s);
                sb.append(s + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
        }
        return "";
    }
}
