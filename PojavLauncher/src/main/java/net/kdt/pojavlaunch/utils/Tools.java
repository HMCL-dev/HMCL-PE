package net.kdt.pojavlaunch.utils;

import android.app.Activity;
import android.content.Context;

import net.kdt.pojavlaunch.Logger;

import org.lwjgl.glfw.CallbackBridge;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public final class Tools {

    public static void launchMinecraft(final Activity activity,String javaPath,String home,String renderer, Vector<String> args,String gameDir,String glesVersion) throws Throwable {

        String[] launchArgs = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).equals(" ")) {
                launchArgs[i] = args.get(i);
                System.out.println("Minecraft Args:" + launchArgs[i]);
                Logger.getInstance(activity).appendToLog("Minecraft Args:" + launchArgs[i]);
            }
        }

        List<String> javaArgList = new ArrayList<String>();

        javaArgList.addAll(Arrays.asList(launchArgs));
        JREUtils.launchJavaVM(activity,javaPath,home,renderer, javaArgList,gameDir,glesVersion);
    }
    
    public static void getCacioJavaArgs(Context context,List<String> javaArgList, boolean isHeadless) {
        javaArgList.add("-Djava.awt.headless="+isHeadless);
        // Caciocavallo config AWT-enabled version
        javaArgList.add("-Dcacio.managed.screensize=" + CallbackBridge.physicalWidth + "x" + CallbackBridge.physicalHeight);
        // javaArgList.add("-Dcacio.font.fontmanager=net.java.openjdk.cacio.ctc.CTCFontManager");
        javaArgList.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
        javaArgList.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
        javaArgList.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");
        javaArgList.add("-Dawt.toolkit=net.java.openjdk.cacio.ctc.CTCToolkit");
        javaArgList.add("-Djava.awt.graphicsenv=net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");

        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/p");
        File cacioDir = new File(context.getDir("runtime",0).getAbsolutePath() + "/caciocavallo/");
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : cacioDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":" + file.getAbsolutePath());
                }
            }
        }
        javaArgList.add(cacioClasspath.toString());
    }

    public static String read(InputStream is) throws IOException {
        String out = "";
        int len;
        byte[] buf = new byte[512];
        while((len = is.read(buf))!=-1) {
            out += new String(buf,0,len);
        }
        return out;
    }

    public static String read(String path) throws IOException {
        return read(new FileInputStream(path));
    }

    public static void write(String path, byte[] content) throws IOException
    {
        File outPath = new File(path);
        outPath.getParentFile().mkdirs();
        outPath.createNewFile();

        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(path));
        fos.write(content, 0, content.length);
        fos.close();
    }

    public static void write(String path, String content) throws IOException {
        write(path, content.getBytes());
    }

}
