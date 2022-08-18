package com.tungsten.hmclpe.utils.io;

import android.net.TrafficStats;

/**
 * <p>
 * <code>lastTotalRxBytes = getTotalRxBytes();<br>
 * lastTimeStamp = System.currentTimeMillis();<br>
 * new Timer().schedule(task, 1000, 2000); // 1s后启动任务，每2s执行一次<br>
 * TimerTask task = new TimerTask();
 */
public class NetSpeed {
    private static final String TAG = NetSpeed.class.getSimpleName();
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String getNetSpeed(int uid) {
        long nowTotalRxBytes = getTotalRxBytes(uid);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        if (speed < 1024){
            return String.valueOf(speed) + " B/s";
        }
        else if (speed >= 1024 && speed < 1048576){
            return String.valueOf(speed / 1024) + " KB/s";
        }
        else {
            return String.valueOf(speed / 1048576) + " MB/s";
        }
    }

    //getApplicationInfo().uid
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes());
    }
}
