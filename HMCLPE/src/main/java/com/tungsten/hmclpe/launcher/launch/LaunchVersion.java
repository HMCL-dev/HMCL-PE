package com.tungsten.hmclpe.launcher.launch;

import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.tungsten.hmclpe.launcher.setting.game.GameLaunchSetting;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cosine.boat.utils.BoatUtils;

public class LaunchVersion {

    public class AssetsIndex {
        public String id;
        public String sha1;
        public int size;
        public int totalSize;
        public String url;
    }

    public class Download {
        public String path;
        public String sha1;
        public int size;
        public String url;
    }

    public AssetsIndex assetIndex;
    public String assets;

    public HashMap<String, Download> downloads;
    public String id;

    public class Library {
        public String name;
        public HashMap<String, Download> downloads;
    }

    public Library libraries[];

    public String mainClass;
    public String minecraftArguments;
    public int minimumLauncherVersion;
    public String releaseTime;
    public String time;
    public String type;

    public class Arguments {
        private Object[] game;
        private Object[] jvm;
    }

    public Arguments arguments;

    // forge
    public String inheritsFrom;

    public String minecraftPath;

    public static LaunchVersion fromDirectory(File file) {
        try {

            String json = new String(BoatUtils.readFile(new File(file, file.getName() + ".json")), "UTF-8");
            LaunchVersion result = new Gson().fromJson(json, LaunchVersion.class);
            if (new File(file, file.getName() + ".jar").exists()) {
                result.minecraftPath = new File(file, file.getName() + ".jar").getAbsolutePath();
            } else {
                result.minecraftPath = "";
            }

            if (result.inheritsFrom != null && !result.inheritsFrom.equals("")) {

                LaunchVersion self = result;
                result = LaunchVersion.fromDirectory(new File(file.getParentFile(), self.inheritsFrom));

                if (self.assetIndex != null) {
                    result.assetIndex = self.assetIndex;
                }
                if (self.assets != null && !self.assets.equals("")) {
                    result.assets = self.assets;
                }
                if (self.downloads != null && !self.downloads.isEmpty()) {

                    if (result.downloads == null) {
                        result.downloads = new HashMap<String, Download>();
                    }

                    for (Map.Entry<String, Download> e : self.downloads.entrySet()) {
                        result.downloads.put(e.getKey(), e.getValue());
                    }
                }
                if (self.libraries != null && self.libraries.length > 0) {
                    Library newLibs[] = new Library[result.libraries.length + self.libraries.length];
                    int i = 0;
                    for (Library lib : self.libraries) {
                        newLibs[i] = lib;
                        i++;
                    }
                    for (Library lib : result.libraries) {
                        newLibs[i] = lib;
                        i++;
                    }
                    result.libraries = newLibs;
                }
                if (self.mainClass != null && !self.mainClass.equals("")) {
                    result.mainClass = self.mainClass;
                }
                if (self.minecraftArguments != null && !self.minecraftArguments.equals("")) {
                    result.minecraftArguments = self.minecraftArguments;
                }
                if (self.minimumLauncherVersion > result.minimumLauncherVersion) {
                    result.minimumLauncherVersion = self.minimumLauncherVersion;
                }
                if (self.releaseTime != null && !self.releaseTime.equals("")) {
                    result.releaseTime = self.releaseTime;
                }
                if (self.time != null && !self.time.equals("")) {
                    result.time = self.time;
                }
                if (self.type != null && !self.type.equals("")) {
                    result.type = self.type;
                }
                if (self.minecraftPath != null && !self.minecraftPath.equals("")) {
                    result.minecraftPath = self.minecraftPath;
                }
                if (result.minimumLauncherVersion >= 21) {
                    if (self.arguments.game != null && self.arguments.game.length > 0) {
                        Object newObj[] = new Object[result.arguments.game.length + self.arguments.game.length];
                        int i = 0;
                        for (Object obj : self.arguments.game) {
                            newObj[i] = obj;
                            i++;
                        }
                        for (Object obj : result.arguments.game) {
                            newObj[i] = obj;
                            i++;
                        }
                        result.arguments.game = newObj;
                    }
                }
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String getClassPath(String gameFileDir,boolean high,boolean isJava17) {
        String cp = "";
        int count = 0;
        String libraries_path = gameFileDir + "/libraries/";
        for (Library lib : this.libraries) {
            if (lib.name == null || lib.name.equals("") || lib.name.contains("org.lwjgl") || lib.name.contains("natives") || (isJava17 && lib.name.contains("java-objc-bridge"))) {
                continue;
            }
            Log.e("boat",lib.name);
            String names[] = lib.name.split(":");
            String packageName = names[0];
            String mainName = names[1];
            String versionName = names[2];

            String path = "";
            path = path + libraries_path;
            path = path + packageName.replaceAll("\\.", "/");
            path = path + "/";
            path = path + mainName;
            path = path + "/";
            path = path + versionName;
            path = path + "/" + mainName + "-" + versionName + ".jar";
            Log.e("路径",path);
            if (!new File(path).exists()){
                continue;
            }
            if (count > 0) {
                cp = cp + ":";
            }
            cp = cp + path;
            count++;
        }
        String split = count > 0 ? ":" : "";
        if (high) {
            cp = cp + split + minecraftPath;
        }
        else {
            cp = minecraftPath + split + cp;
        }
        return cp;
    }

    public String[] getJVMArguments(GameLaunchSetting gameLaunchSetting) {
        StringBuilder test = new StringBuilder();
        if (arguments != null && arguments.jvm != null) {
            Object[] jvmObjs = this.arguments.jvm;
            for (Object obj : jvmObjs) {
                if (obj instanceof String && !((String) obj).startsWith("-Djava.library.path") && !((String) obj).startsWith("-cp") && !((String) obj).startsWith("${classpath}")) {
                    test.append(obj.toString()).append(" ");
                }
            }
        }
        else {
            return new String[0];
        }
        String result = "";

        int state = 0;
        int start = 0;
        int stop = 0;
        for (int i = 0; i < test.length(); i++) {
            if (state == 0) {
                if (test.charAt(i) != '$') {
                    result = result + test.charAt(i);

                } else {
                    if (i + 1 < test.length() && test.charAt(i + 1) == '{') {
                        state = 1;
                        start = i;
                    } else {
                        result = result + test.charAt(i);
                    }
                }
                continue;
            } else {
                if (test.charAt(i) == '}') {
                    stop = i;

                    String key = test.substring(start + 2, stop);

                    String value = "";

                    if (key.equals("version_name")) {
                        value = id;
                    }
                    else if (key.equals("launcher_name")) {
                        value = "HMCL-PE";
                    }
                    else if (key.equals("launcher_version")) {
                        value = "1.0.0";
                    }
                    else if (key.equals("version_type")) {
                        value = type;
                    }
                    else if (key.equals("assets_index_name")) {
                        if (assetIndex != null) {
                            value = assetIndex.id;
                        }
                        else {
                            value = assets;
                        }
                    }
                    else if (key.equals("game_directory")) {
                        value = gameLaunchSetting.game_directory;
                    }
                    else if (key.equals("assets_root")) {
                        value = gameLaunchSetting.gameFileDirectory + "/assets";
                    }
                    else if (key.equals("user_properties")) {
                        value = "{}";
                    }
                    else if (key.equals("auth_player_name")) {
                        value = gameLaunchSetting.account.auth_player_name;
                    }
                    else if (key.equals("auth_session")) {
                        value = gameLaunchSetting.account.auth_session;
                    }
                    else if (key.equals("auth_uuid")) {
                        value = gameLaunchSetting.account.auth_uuid;
                    }
                    else if (key.equals("auth_access_token")) {
                        value = gameLaunchSetting.account.auth_access_token;
                    }
                    else if (key.equals("user_type")) {
                        value = gameLaunchSetting.account.user_type;
                    }
                    else if (key.equals("primary_jar_name")) {
                        value = new File(gameLaunchSetting.currentVersion).getName() + ".jar";
                    }
                    else if (key.equals("library_directory")) {
                        value = gameLaunchSetting.gameFileDirectory + "/libraries";
                    }
                    else if (key.equals("classpath_separator")) {
                        value = ":";
                    }
                    else {
                        value = "";
                    }
                    result = result + value;
                    i = stop;
                    state = 0;
                }
            }
        }
        return result.split(" ");
    }

    public String[] getMinecraftArguments(GameLaunchSetting gameLaunchSetting, boolean isHighVer) {
        StringBuilder test = new StringBuilder();
        if (isHighVer) {
            Object[] objs = this.arguments.game;
            for (Object obj : objs) {
                if (obj instanceof String) {
                    test.append(obj.toString()).append(" ");
                }
            }
        }
        else {
            test = new StringBuilder(this.minecraftArguments);
        }
        String result = "";

        int state = 0;
        int start = 0;
        int stop = 0;
        for (int i = 0; i < test.length(); i++) {
            if (state == 0) {
                if (test.charAt(i) != '$') {
                    result = result + test.charAt(i);

                } else {
                    if (i + 1 < test.length() && test.charAt(i + 1) == '{') {
                        state = 1;
                        start = i;
                    } else {
                        result = result + test.charAt(i);
                    }
                }
                continue;
            } else {
                if (test.charAt(i) == '}') {
                    stop = i;

                    String key = test.substring(start + 2, stop);

                    String value = "";

                    if (key.equals("version_name")) {
                        value = id;
                    }
                    else if (key.equals("launcher_name")) {
                        value = "HMCL-PE";
                    }
                    else if (key.equals("launcher_version")) {
                        value = "1.0.0";
                    }
                    else if (key.equals("version_type")) {
                        value = type;
                    }
                    else if (key.equals("assets_index_name")) {
                        if (assetIndex != null) {
                            value = assetIndex.id;
                        }
                        else {
                            value = assets;
                        }
                    }
                    else if (key.equals("game_directory")) {
                        value = gameLaunchSetting.game_directory;
                    }
                    else if (key.equals("assets_root")) {
                        value = gameLaunchSetting.gameFileDirectory + "/assets";
                    }
                    else if (key.equals("user_properties")) {
                        value = "{}";
                    }
                    else if (key.equals("auth_player_name")) {
                        value = gameLaunchSetting.account.auth_player_name;
                    }
                    else if (key.equals("auth_session")) {
                        value = gameLaunchSetting.account.auth_session;
                    }
                    else if (key.equals("auth_uuid")) {
                        value = gameLaunchSetting.account.auth_uuid;
                    }
                    else if (key.equals("auth_access_token")) {
                        value = gameLaunchSetting.account.auth_access_token;
                    }
                    else if (key.equals("user_type")) {
                        value = gameLaunchSetting.account.user_type;
                    }
                    else if (key.equals("primary_jar_name")) {
                        value = new File(gameLaunchSetting.currentVersion).getName() + ".jar";
                    }
                    else if (key.equals("library_directory")) {
                        value = gameLaunchSetting.gameFileDirectory + "/libraries";
                    }
                    else if (key.equals("classpath_separator")) {
                        value = ":";
                    }
                    else {
                        value = "";
                    }
                    result = result + value;
                    i = stop;
                    state = 0;
                }
            }
        }
        if (!isHighVer && arguments != null && arguments.game != null) {
            Object[] objs = this.arguments.game;
            for (Object obj : objs) {
                if (obj instanceof String) {
                    result = result + " " + obj.toString();
                }
            }
        }
        return result.split(" ");
    }
    public List<String> getLibraries() {
        List<String> libs=new ArrayList<>();
        for (Library lib : this.libraries) {
            if (lib.name == null || lib.name.equals("") || lib.name.contains("net.java.jinput") || lib.name.contains("org.lwjgl")||lib.name.contains("platform")) {
                continue;
            }
            libs.add(parseLibNameToPath(lib.name));
        }
        return libs;
    }
    private Map<String,String> SHAs;
    public String getSHA1(String libName){
        if (SHAs==null){
            SHAs=new ArrayMap<>();
            for (Library lib : this.libraries) {
                if (lib.name == null || lib.name.equals("") || lib.name.contains("net.java.jinput") || lib.name.contains("org.lwjgl")||lib.name.contains("platform")) {
                    continue;
                }
                String sha1;
                try {
                    sha1=lib.downloads.get("artifact").sha1;
                }catch (Exception e){
                    continue;
                }
                SHAs.put(parseLibNameToPath(lib.name),sha1);
            }
        }
        return SHAs.get(libName);
    }

    public String parseLibNameToPath(String libName){
        String[] tmp=libName.split(":");
        return tmp[0].replace(".","/")+"/"+tmp[1]+"/"+tmp[2]+"/"+tmp[1]+"-"+tmp[2]+".jar";
    }
}
