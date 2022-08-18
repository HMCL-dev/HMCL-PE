package com.tungsten.hmclpe.launcher.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.SplashActivity;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.manifest.info.AppInfo;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.task.LanzouUrlGetTask;
import com.tungsten.hmclpe.utils.file.AssetsUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class InstallLauncherFile {

    public static void checkLauncherFiles(SplashActivity activity){
        /*
         *初始化进度监听回调
         */
        @SuppressLint("SetTextI18n") AssetsUtils.ProgressCallback progressCallback = progress -> activity.runOnUiThread(() -> {
            activity.loadingProgress.setProgress(progress);
            activity.loadingProgressText.setText(progress + " %");
        });
        /*
         *检查forge-install-bootstrapper.jar
         */
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_plugin));
        });
        if (!new File(AppManifest.PLUGIN_DIR + "/installer").exists() || !new File(AppManifest.PLUGIN_DIR + "/installer/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/installer/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/installer/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/installer");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/installer",AppManifest.PLUGIN_DIR + "/installer");
        }
        /*
         *检查TouchInjector.jar
         */
        if (!new File(AppManifest.PLUGIN_DIR + "/touch").exists() || !new File(AppManifest.PLUGIN_DIR + "/touch/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/touch/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/touch/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/touch");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/touch",AppManifest.PLUGIN_DIR + "/touch");
        }
        /*
         *检查authlib-injector.jar
         */
        if (!new File(AppManifest.PLUGIN_DIR + "/login/authlib-injector").exists() || !new File(AppManifest.PLUGIN_DIR + "/login/authlib-injector/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/login/authlib-injector/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/login/authlib-injector/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/login/authlib-injector");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/login/authlib-injector",AppManifest.PLUGIN_DIR + "/login/authlib-injector");
        }
        /*
         *检查nide8auth.jar
         */
        if (!new File(AppManifest.PLUGIN_DIR + "/login/nide8auth").exists() || !new File(AppManifest.PLUGIN_DIR + "/login/nide8auth/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/login/nide8auth/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/login/nide8auth/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/login/nide8auth");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/login/nide8auth",AppManifest.PLUGIN_DIR + "/login/nide8auth");
        }
        /*
         *检查布局方案，如果没有，就生产一个默认布局
         */
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_control));
        });
        if (SettingUtils.getControlPatternList().size() == 0) {
            AssetsUtils.getInstance(activity.getApplicationContext()).setProgressCallback(progressCallback).copyOnMainThread("control", AppManifest.CONTROLLER_DIR);
        }
        /*
         *检查除Java外的运行环境，这些文件一定会被内置进启动器，所以进行统一处理
         */
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_lib));
        });
        if (!new File(AppManifest.DEFAULT_RUNTIME_DIR + "/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.DEFAULT_RUNTIME_DIR + "/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "app_runtime/version")))) {
            FileUtils.deleteDirectory(AppManifest.BOAT_LIB_DIR);
            FileUtils.deleteDirectory(AppManifest.POJAV_LIB_DIR);
            FileUtils.deleteDirectory(AppManifest.CACIOCAVALLO_DIR);
            if (new File(AppManifest.DEFAULT_RUNTIME_DIR + "/version").exists()) {
                new File(AppManifest.DEFAULT_RUNTIME_DIR + "/version").delete();
            }
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/boat",AppManifest.BOAT_LIB_DIR);
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/pojav",AppManifest.POJAV_LIB_DIR);
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/caciocavallo",AppManifest.CACIOCAVALLO_DIR);
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/version",AppManifest.DEFAULT_RUNTIME_DIR + "/version");
        }
        /*
         *检查Java运行时，Java可能内置在启动器也可能需要下载，因此单独处理
         */
        checkJava8(activity, progressCallback);
        checkJava17(activity);
    }

    @SuppressLint("SetTextI18n")
    public static void checkJava8(SplashActivity activity, AssetsUtils.ProgressCallback callback){
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_8));
        });
        if (!new File(AppManifest.JAVA_DIR + "/default").exists() || !new File(AppManifest.JAVA_DIR + "/default/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.JAVA_DIR + "/default/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "app_runtime/java/default/version")))) {
            FileUtils.deleteDirectory(AppManifest.JAVA_DIR + "/default");
            AssetsUtils.getInstance(activity).setProgressCallback(callback).copyOnMainThread("app_runtime/java/default",AppManifest.JAVA_DIR + "/default");
        }
    }

    public static void checkJava17(SplashActivity activity){
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17));
        });
        if (!new File(AppManifest.JAVA_DIR + "/JRE17").exists() || !new File(AppManifest.JAVA_DIR + "/JRE17/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.JAVA_DIR + "/JRE17/version"))) < AppInfo.JAVA_17_VERSION_CODE) {
            /*
             *选择 Java 17 安装方式
             */
            activity.runOnUiThread(() -> {
                activity.loadingProgress.setProgress(0);
                activity.loadingProgressText.setText("0 %");
                activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_select));
                activity.selectText.setVisibility(View.VISIBLE);
                activity.download.setVisibility(View.VISIBLE);
                activity.local.setVisibility(View.VISIBLE);
            });
        }
        else {
            /*
             *检查完成，进入启动器
             */
            activity.runOnUiThread(() -> {
                enterLauncher(activity);
            });
        }
    }

    public static void getJRE17Url(SplashActivity activity) {
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_get));
        });
        FileUtils.deleteDirectory(AppManifest.JAVA_DIR + "/JRE17");
        FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/java");
        LanzouUrlGetTask task = new LanzouUrlGetTask(activity, new LanzouUrlGetTask.Callback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(Exception e) {
                FileStringUtils.writeFile(AppManifest.DEBUG_DIR + "/lanzou_exception.txt",e.toString());
                downloadJava17(activity,AppInfo.JAVA_17_DOWNLOAD_URL_FASTGIT);
            }

            @Override
            public void onFinish(String url) {
                if (url == null){
                    downloadJava17(activity,AppInfo.JAVA_17_DOWNLOAD_URL_FASTGIT);
                }
                else {
                    downloadJava17(activity,url);
                }
            }
        });
        activity.runOnUiThread(() -> {
            task.execute(AppInfo.JAVA_17_DOWNLOAD_URL);
        });
    }

    public static void downloadJava17(SplashActivity activity,String url) {
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_install));
        });
        DownloadUtil.downloadSingleFile(activity, new DownloadTaskListBean("", url, AppManifest.DEFAULT_CACHE_DIR + "/java/JRE17.zip",AppInfo.JAVA_17_SHA1), new DownloadTask.Feedback() {
            @Override
            public void addTask(DownloadTaskListBean bean) {
                System.out.println(bean.url);
                System.out.println(bean.path);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void updateProgress(DownloadTaskListBean bean) {
                activity.runOnUiThread(() -> {
                    activity.loadingProgress.setProgress(bean.progress);
                    activity.loadingProgressText.setText(bean.progress + " %");
                });
            }

            @Override
            public void updateSpeed(String speed) {

            }

            @Override
            public void removeTask(DownloadTaskListBean bean) {

            }

            @Override
            public void onFinished(ArrayList<DownloadTaskListBean> failedFile) {
                if (failedFile.size() > 0) {
                    if (url.equals(AppInfo.JAVA_17_DOWNLOAD_URL_GITHUB)) {
                        activity.runOnUiThread(() -> {
                            activity.loadingProgress.setProgress(0);
                            activity.loadingProgressText.setText("0 %");
                            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_select));
                            activity.selectText.setVisibility(View.VISIBLE);
                            activity.download.setVisibility(View.VISIBLE);
                            activity.local.setVisibility(View.VISIBLE);
                            Toast.makeText(activity, activity.getString(R.string.loading_hint_java_17_download_failed), Toast.LENGTH_SHORT).show();
                        });
                    }
                    else if (url.equals(AppInfo.JAVA_17_DOWNLOAD_URL_FASTGIT)) {
                        activity.runOnUiThread(() -> {
                            activity.loadingProgress.setProgress(0);
                            activity.loadingProgressText.setText("0 %");
                            downloadJava17(activity,AppInfo.JAVA_17_DOWNLOAD_URL_GITHUB);
                        });
                    }
                    else {
                        activity.runOnUiThread(() -> {
                            activity.loadingProgress.setProgress(0);
                            activity.loadingProgressText.setText("0 %");
                            downloadJava17(activity,AppInfo.JAVA_17_DOWNLOAD_URL_FASTGIT);
                        });
                    }
                }
                else {
                    activity.runOnUiThread(() -> {
                        activity.loadingProgress.setProgress(0);
                        activity.loadingProgressText.setText("0 %");
                        unZipJava(activity,AppManifest.DEFAULT_CACHE_DIR + "/java/JRE17.zip");
                    });
                }
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    public static void checkJava17File(SplashActivity activity,String path) {
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_check));
        });
        String sha1 = FileUtils.getFileSha1(path);
        if (Objects.equals(sha1, AppInfo.JAVA_17_SHA1)) {
            activity.runOnUiThread(() -> {
                unZipJava(activity,path);
            });
        }
        else {
            activity.runOnUiThread(() -> {
                activity.loadingProgress.setProgress(0);
                activity.loadingProgressText.setText("0 %");
                activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_select));
                activity.selectText.setVisibility(View.VISIBLE);
                activity.download.setVisibility(View.VISIBLE);
                activity.local.setVisibility(View.VISIBLE);
                Toast.makeText(activity, activity.getString(R.string.loading_hint_java_17_error), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public static void unZipJava(SplashActivity activity,String path){
        activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17_unzip));
        ZipManager.unzip(path, AppManifest.JAVA_DIR, new IZipCallback() {
            @Override
            public void onStart() {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgress(int percentDone) {
                activity.loadingProgress.setProgress(percentDone);
                activity.loadingProgressText.setText(percentDone + " %");
            }

            @Override
            public void onFinish(boolean success) {
                if (success) {
                    /*
                     *检查完成，进入启动器
                     */
                    enterLauncher(activity);
                }
                else {
                    activity.loadingText.setText(activity.getString(R.string.loading_hint_failed));
                    activity.loadingText.setTextColor(Color.RED);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public static void enterLauncher (SplashActivity activity) {
        activity.loadingText.setText(activity.getString(R.string.loading_hint_ready));
        activity.loadingProgress.setProgress(100);
        activity.loadingProgressText.setText("100 %");
        Intent intent = new Intent(activity,MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("fullscreen",activity.launcherSetting.fullscreen);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

}
