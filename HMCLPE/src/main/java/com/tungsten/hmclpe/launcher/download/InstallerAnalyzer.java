package com.tungsten.hmclpe.launcher.download;

import static com.tungsten.hmclpe.utils.Lang.getOrDefault;

import android.os.Handler;

import com.google.gson.Gson;
import com.tungsten.hmclpe.launcher.download.forge.ForgeInstallProfile;
import com.tungsten.hmclpe.launcher.download.forge.ForgeNewInstallProfile;
import com.tungsten.hmclpe.launcher.download.forge.ForgeVersion;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineVersion;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.ZipTools;
import com.tungsten.hmclpe.utils.platform.Bits;

import org.jenkinsci.constant_pool_scanner.ConstantPool;
import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner;
import org.jenkinsci.constant_pool_scanner.ConstantType;
import org.jenkinsci.constant_pool_scanner.Utf8Constant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InstallerAnalyzer {

    public static void checkType(String path,CheckInstallerTypeCallback callback) {
        Handler handler = new Handler();
        callback.onStart();
        new Thread(() -> {
            FileUtils.deleteDirectory(AppManifest.INSTALL_DIR);
            FileUtils.createDirectory(AppManifest.INSTALL_DIR + "/local");
            if (FileUtils.copyFile(path, AppManifest.INSTALL_DIR + "/local/installer.jar")) {
                handler.post(() -> {
                    try {
                        ZipTools.unzipFile(AppManifest.INSTALL_DIR + "/local/installer.jar", AppManifest.INSTALL_DIR + "/local/installer",false);
                        handler.post(() -> {
                            getType(handler,callback);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.post(() -> {
                            callback.onFinish(Type.UNKNOWN,null);
                        });
                    }
                });
            }
            else {
                handler.post(() -> {
                    callback.onFinish(Type.UNKNOWN,null);
                });
            }
        }).start();
    }

    public static void getType(Handler handler,CheckInstallerTypeCallback callback) {
        String root = AppManifest.INSTALL_DIR + "/local/installer/";
        if (new File(root + "install_profile.json").exists()) {
            new Thread(() -> {
                try {
                    String string = FileStringUtils.getStringFromFile(root + "install_profile.json");
                    Map<?, ?> installProfile = JsonUtils.fromNonNullJson(string, Map.class);
                    Gson gson = JsonUtils.defaultGsonBuilder()
                            .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                            .registerTypeAdapter(Bits.class, new Bits.Serializer())
                            .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                            .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                            .create();
                    if (installProfile.containsKey("spec")) {
                        ForgeNewInstallProfile profile = gson.fromJson(string,ForgeNewInstallProfile.class);
                        String[] versions = profile.getVersion().split("-");
                        String version;
                        if (versions.length == 3) {
                            if (versions[1].equals("forge")) {
                                version = versions[2];
                            }
                            else {
                                version = versions[1].replace("Forge","");
                            }
                        }
                        else {
                            version = profile.getVersion();
                        }
                        String mcVersion = profile.getMinecraft();
                        ForgeVersion forgeVersion = new ForgeVersion(null,0,mcVersion,null,version, Collections.emptyList());
                        FileUtils.createDirectory(AppManifest.INSTALL_DIR + "/forge");
                        if (FileUtils.copyFile(AppManifest.INSTALL_DIR + "/local/installer.jar",AppManifest.INSTALL_DIR + "/forge/forge-installer.jar")) {
                            handler.post(() -> {
                                callback.onFinish(Type.FORGE,forgeVersion);
                            });
                        }
                        else {
                            handler.post(() -> {
                                callback.onFinish(Type.UNKNOWN,null);
                            });
                        }
                    }
                    else if (installProfile.containsKey("install") && installProfile.containsKey("versionInfo")) {
                        ForgeInstallProfile profile = gson.fromJson(string,ForgeInstallProfile.class);
                        String[] versions = profile.getVersionInfo().getId().split("-");
                        String version;
                        if (versions.length == 3) {
                            if (versions[1].equals("forge")) {
                                version = versions[2];
                            }
                            else {
                                version = versions[1].replace("Forge","");
                            }
                        }
                        else {
                            version = profile.getVersionInfo().getId();
                        }
                        String mcVersion = profile.getInstall().getMinecraft();
                        ForgeVersion forgeVersion = new ForgeVersion(null,0,mcVersion,null,version, Collections.emptyList());
                        FileUtils.createDirectory(AppManifest.INSTALL_DIR + "/forge");
                        if (FileUtils.copyFile(AppManifest.INSTALL_DIR + "/local/installer.jar",AppManifest.INSTALL_DIR + "/forge/forge-installer.jar")) {
                            handler.post(() -> {
                                callback.onFinish(Type.FORGE,forgeVersion);
                            });
                        }
                        else {
                            handler.post(() -> {
                                callback.onFinish(Type.UNKNOWN,null);
                            });
                        }
                    }
                    else {
                        handler.post(() -> {
                            callback.onFinish(Type.UNKNOWN,null);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        callback.onFinish(Type.UNKNOWN,null);
                    });
                }
            }).start();
        }
        else if (new File(root + "Config.class").exists() || new File(root + "net/optifine/Config.class").exists() || new File(root + "notch/net/optifine/Config.class").exists()) {
            new Thread(() -> {
                Path configClass;
                if (new File(root + "Config.class").exists()) configClass = new File(root + "Config.class").toPath();
                else if (new File(root + "net/optifine/Config.class").exists()) configClass = new File(root + "net/optifine/Config.class").toPath();
                else configClass = new File(root + "notch/net/optifine/Config.class").toPath();
                ConstantPool pool = null;
                try {
                    pool = ConstantPoolScanner.parse(Files.readAllBytes(configClass), ConstantType.UTF8);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        callback.onFinish(Type.UNKNOWN,null);
                    });
                    return;
                }
                List<String> constants = new ArrayList<>();
                pool.list(Utf8Constant.class).forEach(utf8 -> constants.add(utf8.get()));
                String mcVersion = getOrDefault(constants, constants.indexOf("MC_VERSION") + 1, null);
                String ofEdition = getOrDefault(constants, constants.indexOf("OF_EDITION") + 1, null);
                String ofRelease = getOrDefault(constants, constants.indexOf("OF_RELEASE") + 1, null);
                String preName = (ofRelease.contains("pre") || ofRelease.contains("alpha")) ? "preview_" : "";
                OptifineVersion optifineVersion = new OptifineVersion("",mcVersion,ofRelease,ofEdition,0,preName + "OptiFine_" + mcVersion + "_" + ofEdition + "_" + ofRelease + ".jar");
                FileUtils.createDirectory(AppManifest.INSTALL_DIR + "/optifine");
                if (FileUtils.copyFile(AppManifest.INSTALL_DIR + "/local/installer.jar",AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName)) {
                    handler.post(() -> {
                        callback.onFinish(Type.OPTIFINE,optifineVersion);
                    });
                }
                else {
                    handler.post(() -> {
                        callback.onFinish(Type.UNKNOWN,null);
                    });
                }
            }).start();
        }
        else {
            callback.onFinish(Type.UNKNOWN,null);
        }
    }

    public interface CheckInstallerTypeCallback{
        void onStart();
        void onFinish(Type type,Object installer);
    }

    public enum Type{
        FORGE,
        OPTIFINE,
        UNKNOWN
    }
}
