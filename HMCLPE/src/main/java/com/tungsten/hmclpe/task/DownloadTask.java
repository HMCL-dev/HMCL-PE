package com.tungsten.hmclpe.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.utils.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadTask extends AsyncTask<ArrayList<DownloadTaskListBean>, Integer, ArrayList<DownloadTaskListBean>> {

    private final WeakReference<Context> ctx;
    private ArrayList<DownloadTaskListBean> failedFile;//下载失败的文件
    private final Feedback feedback;//回调
    private int maxTask = 8;//最大任务数

    public abstract static class Feedback {
        public abstract void addTask(DownloadTaskListBean bean);
        public abstract void updateProgress(DownloadTaskListBean bean);
        public abstract void updateSpeed(String speed);
        public abstract void removeTask(DownloadTaskListBean bean);
        public abstract void onFinished(ArrayList<DownloadTaskListBean> failedFile);
        public abstract void onCancelled();
    }

    public DownloadTask(Context ctx, Feedback feedback) {
        this.ctx = new WeakReference<>(ctx);
        this.feedback = feedback;
        failedFile = new ArrayList<>();
    }

    public void setMaxTask(int maxTask){
        this.maxTask = maxTask;
    }

    @Override
    public void onPreExecute() {

    }

    @SuppressLint("WrongThread")
    @Override
    public ArrayList<DownloadTaskListBean> doInBackground(ArrayList<DownloadTaskListBean>... args) {
        ArrayList<DownloadTaskListBean> list = args[0];
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(maxTask);
        ExecutorService threadPool = new ThreadPoolExecutor(maxTask, maxTask,
                0, TimeUnit.SECONDS,
                workQueue,
                new ThreadPoolExecutor.DiscardPolicy());
        for (int j = 0; j < list.size(); j++) {
            DownloadTaskListBean bean = list.get(j);
            String url = bean.url;
            String path = bean.path;
            String sha1 = bean.sha1;
            threadPool.execute(() -> {
                if (!new File(path).exists() || (new File(path).exists() && !Objects.equals(FileUtils.getFileSha1(path), sha1))) {
                    int tryTimes = 5;
                    for (int i = 0; i < tryTimes; i++) {
                        if (isCancelled()) {
                            threadPool.shutdownNow();
                            return;
                        }
                        feedback.addTask(bean);
                        DownloadFeedback fb = new DownloadFeedback() {
                            @Override
                            public void updateProgress(long curr, long max) {
                                long progress = 100 * curr / max;
                                bean.progress = (int) progress;
                                feedback.updateProgress(bean);
                            }

                            @Override
                            public void updateSpeed(String speed) {
                                feedback.updateSpeed(speed);
                            }
                        };
                        if (downloadFileMonitored(url, path, sha1, fb)) {
                            feedback.removeTask(bean);
                            break;
                        }
                        else {
                            if (i == tryTimes - 1) {
                                failedFile.add(bean);
                            }
                            feedback.removeTask(bean);
                        }
                    }
                }
            });
            while (!workQueue.isEmpty()) {
                ;
            }
            int progress = (j + 1) * 100 / list.size();
            onProgressUpdate(progress);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return failedFile;
    }

    @Override
    protected void onProgressUpdate(Integer... p1) {

    }

    @Override
    public void onPostExecute(ArrayList<DownloadTaskListBean> result) {
        for (DownloadTaskListBean bean : result) {
            Log.e("url", bean.url);
            Log.e("path", bean.path);
        }
        feedback.onFinished(result);
    }

    @Override
    protected void onCancelled(ArrayList<DownloadTaskListBean> result) {
        feedback.onCancelled();
    }

    public abstract static class DownloadFeedback {
        public abstract void updateProgress(long curr, long max);
        public abstract void updateSpeed(String speed);
    }

    public static boolean isRightFile(String path,String sha1) {
        if (new File(path).exists()) {
            if (sha1 != null && !sha1.equals("")) {
                if (Objects.equals(FileUtils.getFileSha1(path), sha1)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    public static boolean downloadFileMonitored(String url,String nameOutput,String sha1, DownloadFeedback monitor) {
        File nameOutputFile = new File(nameOutput);
        if (!nameOutputFile.exists()) {
            nameOutputFile.getParentFile().mkdirs();
        }
        else {
            if (!isRightFile(nameOutput,sha1)) {
                nameOutputFile.delete();
            }
        }
        try {
            URL downloadUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)downloadUrl.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            FileOutputStream fos = new FileOutputStream(nameOutputFile);
            int cur = 0;
            int oval = 0;
            long len = httpURLConnection.getContentLength();
            byte[] buf = new byte[65535];
            long lastTime = System.currentTimeMillis();
            long lastLen = 0;
            while ((cur = inputStream.read(buf)) != -1) {
                oval += cur;
                if (System.currentTimeMillis() - lastTime>=1000){
                    lastLen = oval;
                    lastTime = System.currentTimeMillis();
                    if (monitor != null)
                        monitor.updateSpeed(formetFileSize(oval - lastLen) + "/s");
                }
                fos.write(buf, 0, cur);
                if (monitor != null)
                    monitor.updateProgress(oval, len);
            }
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (!isRightFile(nameOutput,sha1)) {
            nameOutputFile.delete();
            return false;
        }
        return true;
    }

    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}