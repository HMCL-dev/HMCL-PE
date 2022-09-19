package com.tungsten.hmclpe.launcher.launch.boat;

import static net.kdt.pojavlaunch.utils.Architecture.is64BitsDevice;
import static cosine.boat.utils.Architecture.ARCH_ARM;
import static cosine.boat.utils.Architecture.ARCH_ARM64;
import static cosine.boat.utils.Architecture.ARCH_X86;
import static cosine.boat.utils.Architecture.ARCH_X86_64;

import android.content.Context;

import com.tungsten.hmclpe.launcher.launch.AccountPatch;
import com.tungsten.hmclpe.launcher.setting.game.GameLaunchSetting;
import com.tungsten.hmclpe.launcher.launch.LaunchVersion;
import com.tungsten.hmclpe.launcher.launch.TouchInjector;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.string.StringUtils;

import net.kdt.pojavlaunch.utils.Tools;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import cosine.boat.LoadMe;
import cosine.boat.utils.Architecture;

public class BoatLauncher {

    public static Vector<String> getMcArgs(GameLaunchSetting gameLaunchSetting , Context context,int width,int height,String server){
        try {
            String arch = "";
            String march = "";
            if (Architecture.getDeviceArchitecture() == ARCH_ARM) {
                arch = "aarch32";
                march = "arm";
            }
            if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
                arch = "aarch64";
                march = "arm64";
            }
            if (Architecture.getDeviceArchitecture() == ARCH_X86) {
                arch = "i386";
                march = "x86";
            }
            if (Architecture.getDeviceArchitecture() == ARCH_X86_64) {
                arch = "amd64";
                march = "x86_64";
            }
            LaunchVersion version = LaunchVersion.fromDirectory(new File(gameLaunchSetting.currentVersion));
            String javaPath = gameLaunchSetting.javaPath;
            boolean highVersion = false;
            if (version.minimumLauncherVersion >= 21){
                highVersion = true;
            }
            String libraryPath;
            String classPath;
            String r = gameLaunchSetting.boatRenderer.equals("VirGL") ? "virgl" : "gl4es";
            String rarch = r.equals("gl4es") ? "/" + march : "";
            boolean isJava17 = javaPath.endsWith("JRE17");
            if (!highVersion){
                libraryPath = javaPath + "/lib/" + arch + "/jli:" + javaPath + "/lib/" + arch + ":" + AppManifest.BOAT_LIB_DIR + "/libs/" + march + ":" + AppManifest.BOAT_LIB_DIR + "/lwjgl-2:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-2/" + march + ":" + AppManifest.BOAT_LIB_DIR + "/renderer/" + r + rarch;
                classPath = AppManifest.BOAT_LIB_DIR + "/lwjgl-2/lwjgl.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-2/lwjgl_util.jar:" + version.getClassPath(gameLaunchSetting.gameFileDirectory,false,false);
            }
            else {
                if (isJava17) {
                    libraryPath = javaPath + "/lib:" + AppManifest.BOAT_LIB_DIR + "/libs/" + march + ":" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/" + march + ":" + AppManifest.BOAT_LIB_DIR + "/renderer/" + r + rarch;
                }
                else {
                    libraryPath = javaPath + "/lib/" + arch + "/jli:" + javaPath + "/lib/" + arch + ":" + AppManifest.BOAT_LIB_DIR + "/libs/" + march + ":" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/" + march + ":" + AppManifest.BOAT_LIB_DIR + "/renderer/" + r + rarch;
                }
                classPath = AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl-jemalloc.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl-tinyfd.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl-opengl.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl-openal.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl-glfw.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl-stb.jar:" + AppManifest.BOAT_LIB_DIR + "/lwjgl-3/lwjgl.jar:" + version.getClassPath(gameLaunchSetting.gameFileDirectory,true,isJava17);
            }
            String libName = is64BitsDevice() ? "lib64" : "lib";
            String ldLibPath = libraryPath + ":/system/" + libName + ":" + "/vendor/" + libName + ":" + "/vendor/" + libName + "/hw:" + context.getApplicationInfo().nativeLibraryDir;
            LoadMe.setLdLibraryPath(ldLibPath);
            Vector<String> args = new Vector<String>();
            args.add(javaPath + "/bin/java");
            Tools.getCacioJavaArgs(context, args, !isJava17, width, height);
            args.add("-cp");
            args.add(classPath);
            args.add("-Djava.library.path=" + libraryPath);
            args.add("-Dorg.lwjgl.util.Debug=true");
            args.add("-Dorg.lwjgl.util.DebugFunctions=true");
            args.add("-Dorg.lwjgl.util.DebugLoader=true");
            args.add("-Dfml.earlyprogresswindow=false");
            if (gameLaunchSetting.boatRenderer.equals("VirGL")) {
                args.add("-Dorg.lwjgl.opengl.libname=libGL.so.1");
            }
            else {
                args.add("-Dorg.lwjgl.opengl.libname=libGL.so");
            }
            args.add("-Dlwjgl.platform=Boat");
            args.add("-Dos.name=Linux");
            args.add("-Djava.io.tmpdir=" + AppManifest.DEFAULT_CACHE_DIR);
            String[] accountArgs;
            accountArgs = AccountPatch.getAccountArgs(context,gameLaunchSetting.account);
            Collections.addAll(args,accountArgs);
            String[] JVMArgs;
            JVMArgs = version.getJVMArguments(gameLaunchSetting);
            for (int i = 0;i < JVMArgs.length;i++) {
                if (JVMArgs[i].startsWith("-DignoreList") && !JVMArgs[i].endsWith("," + new File(gameLaunchSetting.currentVersion).getName() + ".jar")) {
                    JVMArgs[i] = JVMArgs[i] + "," + new File(gameLaunchSetting.currentVersion).getName() + ".jar";
                }
                if (!JVMArgs[i].startsWith("-DFabricMcEmu") && !JVMArgs[i].startsWith("net.minecraft.client.main.Main")) {
                    args.add(JVMArgs[i]);
                }
            }
            args.add("-Xms" + gameLaunchSetting.minRam + "M");
            args.add("-Xmx" + gameLaunchSetting.maxRam + "M");
            if (!gameLaunchSetting.extraJavaFlags.equals("")) {
                String[] extraJavaFlags = gameLaunchSetting.extraJavaFlags.split(" ");
                Collections.addAll(args, extraJavaFlags);
            }
            args.add(version.mainClass);
            String[] minecraftArgs;
            minecraftArgs = version.getMinecraftArguments(gameLaunchSetting, highVersion);
            Collections.addAll(args, minecraftArgs);
            args.add("--width");
            args.add(Integer.toString(width));
            args.add("--height");
            args.add(Integer.toString(height));
            if (StringUtils.isNotBlank(server)) {
                String[] ser = server.split(":");
                args.add("--server");
                args.add(ser[0]);
                args.add("--port");
                args.add(ser.length > 1 ? ser[1] : "25565");
            }
            String[] extraMinecraftArgs = gameLaunchSetting.extraMinecraftFlags.split(" ");
            Collections.addAll(args, extraMinecraftArgs);
            return TouchInjector.rebaseArguments(gameLaunchSetting, args);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
