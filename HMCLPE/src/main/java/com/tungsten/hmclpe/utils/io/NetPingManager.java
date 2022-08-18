package com.tungsten.hmclpe.utils.io;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetPingManager {
    private String mDomain; // 接口域名
    private InetAddress[] mAddress;
    private List<String> mAddressIpList;
    private IOnNetPingListener mIOnNetPingListener; // 将监控日志上报到前段页面
    private HandlerThread mHandlerThread;

    private static int DELAY_TIME = 1000;
    private ConnectivityManager manager;
    private final Handler mHandleMessage;

    /**
     * 延迟
     */
    public void setDuration(int delay) {
        DELAY_TIME = delay;
    }

    /**
     * 初始化网络诊断服务
     */
    public NetPingManager(Context context, String domain, IOnNetPingListener theListener) {
        this.mDomain = domain;
        this.mIOnNetPingListener = theListener;
        this.mAddressIpList = new ArrayList<>();
        if (null != context)
            this.manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mHandlerThread = new HandlerThread("ping");
        this.mHandlerThread.start();
        this.mHandleMessage = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //每次请求清空上传集合
                        if (null != mAddressIpList)
                            mAddressIpList.clear();
                        startNetDiagnosis();
                        if (null != mHandlerThread)
                            mHandleMessage.sendEmptyMessageDelayed(0, DELAY_TIME);
                        break;
                }
            }
        };
    }

    /**
     * 开始监听
     */
    public void startGetDelay() {
        if (null != this.mHandleMessage) {
            this.mHandleMessage.sendEmptyMessage(0);
        }
    }

    /**
     * 释放
     */
    public void release() {
        synchronized (NetPingManager.class) {
            if (null != this.manager)
                this.manager = null;
            if (null != this.mHandleMessage) {
                this.mHandleMessage.removeMessages(0);
            }
            if (null != mHandlerThread) {
                Looper looper = this.mHandlerThread.getLooper();
                if (looper != null) {
                    looper.quitSafely();
                }
            }
            this.mHandlerThread = null;
            this.mIOnNetPingListener = null;
            if (null != mAddressIpList)
                mAddressIpList.clear();
            this.mAddressIpList = null;
            this.manager = null;
        }
    }

    /**
     * 监控网络诊断的跟踪信息
     */
    public interface IOnNetPingListener {
        void onDelay(NetPingManager netPingManager,long log);

        void onError(NetPingManager netPingManager);
    }

    /**
     * 开始诊断网络
     */
    private void startNetDiagnosis() {
        if (!TextUtils.isEmpty(this.mDomain)) {
            // 网络状态
            if (isNetworkConnected()) {
                parseDomain(this.mDomain);// 域名解析
                // TCP三次握手时间测试
                execUseJava();
            } else {
                if (null != mIOnNetPingListener) {
                    mIOnNetPingListener.onError(this);
                }
//                Log.e("tag", "当前主机未联网,请检查网络！");
            }
        }
    }

    /**
     * 使用java执行connected
     */
    private boolean execUseJava() {
        if (mAddress != null && mAddressIpList != null) {
            int len = mAddress.length;
            if (len > 1) {
                execIP(mAddress[0], mAddressIpList.get(0));
            }
        }
        return false;
    }

    private static final int PORT = 80;
    private static final int CONN_TIMES = 4;
    // 设置每次连接的timeout时间
    private int TIME_OUT = 6000;
    private final long[] RttTimes = new long[CONN_TIMES];// 用于存储三次测试中每次的RTT值

    /**
     * 返回某个IP进行5次connect的最终结果
     */
    private boolean execIP(InetAddress inetAddress, String ip) {
        boolean isConnected = true;
        InetSocketAddress socketAddress;
        if (inetAddress != null && ip != null) {
            socketAddress = new InetSocketAddress(inetAddress, PORT);
            int flag = 0;
            for (int i = 0; i < CONN_TIMES; i++) {
                execSocket(socketAddress, i);
                if (RttTimes[i] == -1) {// 一旦发生timeOut,则尝试加长连接时间
                    TIME_OUT += 4000;
                    if (i > 0 && RttTimes[i - 1] == -1) {// 连续两次连接超时,停止后续测试
                        flag = -1;
                        break;
                    }
                } else if (RttTimes[i] == -2) {
                    if (i > 0 && RttTimes[i - 1] == -2) {// 连续两次出现IO异常,停止后续测试
                        flag = -2;
                        break;
                    }
                }
            }
            long time = 0;
            int count = 0;
            if (flag == -1) {
                isConnected = false;
            } else if (flag == -2) {
                isConnected = false;
            } else {
                for (int i = 0; i < CONN_TIMES; i++) {
                    if (RttTimes[i] > 0) {
                        time += RttTimes[i];
                        count++;
                    }
                }
                if (count > 0) {
                    if (mIOnNetPingListener != null)
                        mIOnNetPingListener.onDelay(this,time / count);
                }
            }
        } else {
            isConnected = false;
        }
        return isConnected;
    }

    /**
     * 针对某个IP第index次connect
     */
    private void execSocket(InetSocketAddress socketAddress, int index) {
        long start;
        long end;
        Socket mSocket = new Socket();
        try {
            start = System.currentTimeMillis();
            mSocket.connect(socketAddress, TIME_OUT);
            end = System.currentTimeMillis();
            RttTimes[index] = end - start;
        } catch (SocketTimeoutException e) {
            RttTimes[index] = -1;// 作为TIMEOUT标识
            e.printStackTrace();
        } catch (IOException e) {
            RttTimes[index] = -2;// 作为IO异常标识
            e.printStackTrace();
        } finally {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private Boolean isNetworkConnected() {
        if (manager == null) {
            return false;
        }
        try {
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            return !(networkinfo == null || !networkinfo.isAvailable());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 域名解析
     */
    private boolean parseDomain(String domain) {
        boolean flag = false;
        Map<String, Object> map = getDomainIp(domain);
        String useTime = (String) map.get("useTime");
        mAddress = (InetAddress[]) map.get("remoteInet");
        if (mAddress != null) {// 解析正确
            mAddressIpList.add(mAddress[0].getHostAddress());
            flag = true;
        } else {// 解析不到，判断第一次解析耗时，如果大于10s进行第二次解析
            if (Integer.parseInt(useTime) > 10000) {
                map = getDomainIp(domain);
                mAddress = (InetAddress[]) map.get("remoteInet");
                if (mAddress != null) {
                    mAddressIpList.add(mAddress[0].getHostAddress());
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 解析IP
     */
    private Map<String, Object> getDomainIp(String domain) {
        Map<String, Object> map = new HashMap<>();
        long start = 0;
        long end;
        String time = null;
        InetAddress[] remoteInet = null;
        try {
            start = System.currentTimeMillis();
            remoteInet = InetAddress.getAllByName(domain);
            if (remoteInet != null) {
                end = System.currentTimeMillis();
                time = (end - start) + "";
            }
        } catch (UnknownHostException e) {
            end = System.currentTimeMillis();
            time = (end - start) + "";
            remoteInet = null;
            e.printStackTrace();
        } finally {
            map.put("remoteInet", remoteInet);
            map.put("useTime", time);
        }
        return map;
    }
}